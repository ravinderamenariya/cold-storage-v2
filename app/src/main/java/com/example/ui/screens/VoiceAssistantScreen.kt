package com.example.ui.screens

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.ui.components.VisionXHeader
import com.example.ui.theme.VxBorder
import com.example.ui.theme.VxBorderGreen
import com.example.ui.theme.VxEmerald
import com.example.ui.theme.VxError
import com.example.ui.theme.VxNavy
import com.example.ui.theme.VxPrimaryAccent
import com.example.ui.theme.VxPrimaryBlue
import com.example.ui.theme.VxSuccess
import com.example.ui.theme.VxSurfaceBlue
import com.example.ui.theme.VxSurfaceCard
import com.example.ui.theme.VxSurfaceGreen
import com.example.ui.theme.VxSurfaceSubtle
import com.example.ui.theme.VxTextPrimary
import com.example.ui.theme.VxTextSecondary
import com.example.util.AppTranslations
import com.example.util.FarmerQuestionType
import com.example.util.IndianLanguage

@Composable
fun VoiceAssistantScreen(
    selectedLanguage: IndianLanguage,
    isSpeaking: Boolean,
    lastSpokenText: String,
    onSelectLanguage: (IndianLanguage) -> Unit,
    onSpeakCurrentStatus: () -> Unit,
    onAskFarmerQuestion: (FarmerQuestionType) -> Unit,
    onAskVoiceQuery: (String) -> Unit,
    onStopSpeaking: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var queryText by remember { mutableStateOf("") }
    var isListening by remember { mutableStateOf(false) }
    var speechError by remember { mutableStateOf<String?>(null) }

    // Android SpeechRecognizer
    val speechRecognizer = remember {
        if (SpeechRecognizer.isRecognitionAvailable(context)) {
            SpeechRecognizer.createSpeechRecognizer(context)
        } else {
            null
        }
    }

    DisposableEffect(speechRecognizer) {
        onDispose {
            speechRecognizer?.destroy()
        }
    }

    fun startListening() {
        if (speechRecognizer == null) {
            speechError = "Speech recognition is not supported on this device."
            return
        }

        speechError = null
        isListening = true
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, selectedLanguage.localeTag)
            putExtra(RecognizerIntent.EXTRA_PROMPT, "Boliye... Speak now in ${selectedLanguage.displayName}")
        }

        speechRecognizer.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {}
            override fun onBeginningOfSpeech() {}
            override fun onRmsChanged(rmsdB: Float) {}
            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onEndOfSpeech() {
                isListening = false
            }

            override fun onError(error: Int) {
                isListening = false
            }

            override fun onResults(results: Bundle?) {
                isListening = false
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (!matches.isNullOrEmpty()) {
                    val recognized = matches[0]
                    queryText = recognized
                    onAskVoiceQuery(recognized)
                }
            }

            override fun onPartialResults(partialResults: Bundle?) {}
            override fun onEvent(eventType: Int, params: Bundle?) {}
        })

        speechRecognizer.startListening(intent)
    }

    // Permission launcher for RECORD_AUDIO
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            startListening()
        } else {
            speechError = "Microphone permission is required to speak."
        }
    }

    fun handleMicClick() {
        if (isListening) {
            speechRecognizer?.stopListening()
            isListening = false
        } else {
            val permissionCheck = ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO)
            if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                startListening()
            } else {
                permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
            }
        }
    }

    // Infinite breathing animation for pulsing mic button
    val infiniteTransition = rememberInfiniteTransition(label = "mic_pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = if (isListening || isSpeaking) 1.15f else 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    Scaffold(
        topBar = {
            VisionXHeader(
                title = AppTranslations.get("nav_voice", selectedLanguage),
                subtitle = "Multilingual Kisan Voice Assistant",
                showBack = true,
                onBackClick = onBack,
                language = selectedLanguage,
                onSpeakStatusClick = onSpeakCurrentStatus
            )
        },
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(top = 12.dp, bottom = 40.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 1. Language Switcher Row (All Indian Languages)
                item {
                    Text(
                        text = "🌐 ${AppTranslations.get("language", selectedLanguage)} / Select Language",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = VxTextPrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(IndianLanguage.values()) { lang ->
                            val isSelected = lang == selectedLanguage
                            Surface(
                                onClick = { onSelectLanguage(lang) },
                                shape = RoundedCornerShape(20.dp),
                                color = if (isSelected) VxPrimaryBlue else VxSurfaceCard,
                                border = BorderStroke(1.dp, if (isSelected) VxPrimaryBlue else VxBorder),
                                modifier = Modifier.testTag("lang_chip_${lang.code}")
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(text = lang.flagEmoji, fontSize = 14.sp)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = lang.nativeName,
                                        fontSize = 13.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) Color.White else VxTextPrimary
                                    )
                                    if (lang != IndianLanguage.ENGLISH && lang != IndianLanguage.HINDI) {
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "(${lang.displayName})",
                                            fontSize = 10.sp,
                                            color = if (isSelected) Color.White.copy(alpha = 0.8f) else VxTextSecondary
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // 2. Giant Mic & Voice Status Hub
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSpeaking) VxSurfaceGreen else if (isListening) VxSurfaceBlue else VxSurfaceCard
                        ),
                        border = BorderStroke(
                            width = 2.dp,
                            color = if (isSpeaking) VxSuccess else if (isListening) VxPrimaryBlue else VxBorder
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // Pulsing Big Mic Circle
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(96.dp)
                                    .scale(if (isListening || isSpeaking) pulseScale else 1f)
                                    .clip(CircleShape)
                                    .background(
                                        if (isSpeaking) VxSuccess else if (isListening) VxError else VxPrimaryBlue
                                    )
                                    .border(
                                        4.dp,
                                        if (isSpeaking) VxBorderGreen else VxSurfaceBlue,
                                        CircleShape
                                    )
                                    .clickable { handleMicClick() }
                                    .testTag("voice_assistant_mic_button")
                            ) {
                                Icon(
                                    imageVector = if (isSpeaking) Icons.Default.VolumeUp else Icons.Default.Mic,
                                    contentDescription = "Microphone",
                                    tint = Color.White,
                                    modifier = Modifier.size(46.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = when {
                                    isListening -> "सुन रहे हैं... बोलिए (Listening...)"
                                    isSpeaking -> AppTranslations.get("speaking", selectedLanguage)
                                    else -> AppTranslations.get("tap_to_speak", selectedLanguage)
                                },
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSpeaking) VxSuccess else VxTextPrimary,
                                textAlign = TextAlign.Center
                            )

                            Text(
                                text = "इस डिवाइस, कोल्ड स्टोरेज तापमान या फसल के बारे में कुछ भी पूछें",
                                fontSize = 12.sp,
                                color = VxTextSecondary,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(top = 4.dp)
                            )

                            // Stop Speaking / Read Status buttons
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 16.dp),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Button(
                                    onClick = onSpeakCurrentStatus,
                                    colors = ButtonDefaults.buttonColors(containerColor = VxPrimaryBlue),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(imageVector = Icons.Default.VolumeUp, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(text = AppTranslations.get("speak_all", selectedLanguage), fontSize = 13.sp)
                                }

                                if (isSpeaking) {
                                    Button(
                                        onClick = onStopSpeaking,
                                        colors = ButtonDefaults.buttonColors(containerColor = VxError),
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        Icon(imageVector = Icons.Default.Stop, contentDescription = null, modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(text = AppTranslations.get("stop_speech", selectedLanguage), fontSize = 12.sp)
                                    }
                                }
                            }

                            if (speechError != null) {
                                Text(
                                    text = speechError ?: "",
                                    color = VxError,
                                    fontSize = 12.sp,
                                    modifier = Modifier.padding(top = 8.dp)
                                )
                            }
                        }
                    }
                }

                // 3. Spoken Text Transcription Card (Shows exactly what device said)
                if (lastSpokenText.isNotBlank()) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = VxSurfaceGreen),
                            border = BorderStroke(1.dp, VxBorderGreen)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.VolumeUp,
                                        contentDescription = null,
                                        tint = VxSuccess,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "डिवाइस का जवाब (Voice Response):",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = VxSuccess
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = lastSpokenText,
                                    fontSize = 14.sp,
                                    color = VxTextPrimary,
                                    lineHeight = 20.sp
                                )
                            }
                        }
                    }
                }

                // 4. One-Tap Quick Questions for Farmers / Illiterate Users
                item {
                    Text(
                        text = "⚡ ${AppTranslations.get("quick_questions", selectedLanguage)}",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = VxTextPrimary
                    )
                }

                items(FarmerQuestionType.values()) { qType ->
                    Surface(
                        onClick = { onAskFarmerQuestion(qType) },
                        shape = RoundedCornerShape(12.dp),
                        color = VxSurfaceCard,
                        border = BorderStroke(1.dp, VxBorder),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("quick_question_${qType.name.lowercase()}")
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(text = qType.iconEmoji, fontSize = 24.sp)
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = qType.hindiTitle,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = VxTextPrimary
                                    )
                                    Text(
                                        text = qType.englishTitle,
                                        fontSize = 11.sp,
                                        color = VxTextSecondary
                                    )
                                }
                            }
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(VxSurfaceBlue),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.VolumeUp,
                                    contentDescription = "Speak answer",
                                    tint = VxPrimaryBlue,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }

                // 5. Type query input (for writing query)
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "✍️ लिखकर पूछें (Type question)",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = VxTextPrimary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = queryText,
                            onValueChange = { queryText = it },
                            placeholder = { Text("जैसे: क्या कमरा 1 ठंडा है?", fontSize = 13.sp) },
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = VxPrimaryBlue,
                                unfocusedBorderColor = VxBorder
                            ),
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                            keyboardActions = KeyboardActions(
                                onSend = {
                                    if (queryText.isNotBlank()) {
                                        onAskVoiceQuery(queryText)
                                    }
                                }
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("voice_assistant_query_input")
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        IconButton(
                            onClick = {
                                if (queryText.isNotBlank()) {
                                    onAskVoiceQuery(queryText)
                                }
                            },
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(VxPrimaryBlue)
                                .testTag("voice_assistant_send_button")
                        ) {
                            Icon(imageVector = Icons.Default.Send, contentDescription = "Send", tint = Color.White)
                        }
                    }
                }
            }
        }
    }
}
