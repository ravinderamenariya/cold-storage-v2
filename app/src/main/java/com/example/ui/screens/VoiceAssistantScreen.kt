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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import com.example.ui.AiChatMessage
import com.example.ui.components.VisionXHeader
import com.example.ui.theme.VxTheme
import com.example.util.AppTranslations
import com.example.util.FarmerQuestionType
import com.example.util.IndianLanguage

@Composable
fun VoiceAssistantScreen(
    selectedLanguage: IndianLanguage,
    isSpeaking: Boolean,
    lastSpokenText: String,
    chatMessages: List<AiChatMessage> = emptyList(),
    isAiThinking: Boolean = false,
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
                speechError = when (error) {
                    SpeechRecognizer.ERROR_NO_MATCH -> "No speech recognized. Please try again."
                    SpeechRecognizer.ERROR_NETWORK, SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "Network issue in voice recognition."
                    SpeechRecognizer.ERROR_AUDIO -> "Audio recording error."
                    else -> "Voice recognition error (code $error)."
                }
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

        try {
            speechRecognizer.startListening(intent)
        } catch (e: Exception) {
            isListening = false
            speechError = "Could not start voice recognition: ${e.message}"
        }
    }

    fun stopListening() {
        try {
            speechRecognizer?.stopListening()
        } catch (_: Exception) {}
        isListening = false
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            startListening()
        } else {
            speechError = "Audio permission is required to use the voice microphone."
        }
    }

    fun handleMicClick() {
        if (isListening) {
            stopListening()
        } else if (isSpeaking) {
            onStopSpeaking()
        } else {
            val hasPermission = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED

            if (hasPermission) {
                startListening()
            } else {
                permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
            }
        }
    }

    // Pulsing Animation when Listening or Speaking
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.14f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    Scaffold(
        topBar = {
            VisionXHeader(
                title = "GREENCOLD Kisan AI",
                subtitle = "Gemini 3.8 Flash • Voice & Return Advisory",
                showBack = true,
                onBackClick = onBack
            )
        },
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { innerPadding ->
        BoxWithConstraints(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                contentPadding = PaddingValues(top = 12.dp, bottom = 28.dp)
            ) {
                // 1. Language Selector Row
                item {
                    Column {
                        Text(
                            text = "🌐 भाषा चुनें (Select Voice Language):",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            contentPadding = PaddingValues(horizontal = 2.dp)
                        ) {
                            items(IndianLanguage.values()) { lang ->
                                val isSelected = lang == selectedLanguage
                                Surface(
                                    onClick = { onSelectLanguage(lang) },
                                    shape = RoundedCornerShape(20.dp),
                                    color = if (isSelected) VxTheme.primaryBlue else MaterialTheme.colorScheme.surface,
                                    border = BorderStroke(
                                        width = if (isSelected) 2.dp else 1.dp,
                                        color = if (isSelected) VxTheme.primaryBlue else MaterialTheme.colorScheme.outline
                                    ),
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
                                            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                                        )
                                        if (lang != IndianLanguage.ENGLISH && lang != IndianLanguage.HINDI) {
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = "(${lang.displayName})",
                                                fontSize = 10.sp,
                                                color = if (isSelected) Color.White.copy(alpha = 0.8f) else MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Gemini AI Status Pill
                item {
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = VxTheme.purple.copy(alpha = 0.12f),
                        border = BorderStroke(1.dp, VxTheme.purple.copy(alpha = 0.35f)),
                        modifier = Modifier.padding(vertical = 2.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = "Gemini",
                                tint = VxTheme.purple,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Connected to Gemini 3.8 Flash AI Model",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = VxTheme.purple
                            )
                        }
                    }
                }

                // 2. Giant Mic & Voice Status Hub
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSpeaking) VxTheme.surfaceGreen else if (isListening) VxTheme.surfaceBlue else MaterialTheme.colorScheme.surface
                        ),
                        border = BorderStroke(
                            width = 2.dp,
                            color = if (isSpeaking) VxTheme.success else if (isListening) VxTheme.primaryBlue else MaterialTheme.colorScheme.outline
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // Pulsing Big Mic Circle
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(92.dp)
                                    .scale(if (isListening || isSpeaking) pulseScale else 1f)
                                    .clip(CircleShape)
                                    .background(
                                        if (isSpeaking) VxTheme.success else if (isListening) VxTheme.errorRed else VxTheme.primaryBlue
                                    )
                                    .border(
                                        4.dp,
                                        if (isSpeaking) VxTheme.borderGreen else VxTheme.surfaceBlue,
                                        CircleShape
                                    )
                                    .clickable { handleMicClick() }
                                    .testTag("voice_assistant_mic_button")
                            ) {
                                Icon(
                                    imageVector = if (isSpeaking) Icons.Default.VolumeUp else Icons.Default.Mic,
                                    contentDescription = "Microphone",
                                    tint = Color.White,
                                    modifier = Modifier.size(44.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            Text(
                                text = when {
                                    isListening -> "सुन रहे हैं... बोलिए (Listening...)"
                                    isSpeaking -> AppTranslations.get("speaking", selectedLanguage)
                                    else -> AppTranslations.get("tap_to_speak", selectedLanguage)
                                },
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSpeaking) VxTheme.success else MaterialTheme.colorScheme.onSurface,
                                textAlign = TextAlign.Center
                            )

                            Text(
                                text = "कोल्ड स्टोरेज, क्या-अगर सवाल या फसल रिटर्न के बारे में सीधे पूछें",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(top = 4.dp)
                            )

                            // Stop Speaking / Read Status buttons
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 14.dp),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Button(
                                    onClick = onSpeakCurrentStatus,
                                    colors = ButtonDefaults.buttonColors(containerColor = VxTheme.primaryBlue),
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
                                        colors = ButtonDefaults.buttonColors(containerColor = VxTheme.errorRed),
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
                                    color = VxTheme.errorRed,
                                    fontSize = 12.sp,
                                    modifier = Modifier.padding(top = 8.dp)
                                )
                            }
                        }
                    }
                }

                // 3. What-If & Return Quick Prompt Chips
                item {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "💡 क्या-अगर सवाल (Ask What-If & Return Scenarios):",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            contentPadding = PaddingValues(horizontal = 2.dp)
                        ) {
                            val whatIfPrompts = listOf(
                                "अगर 5 दिन बाद बेचें तो क्या return मिलेगा?",
                                "कमरा 1 का तापमान 8°C करने पर क्या होगा?",
                                "आलू को कब बेचना सबसे फायदेमंद रहेगा?",
                                "सोलर पावर कम होने पर कूलिंग कितनी देर चलेगी?"
                            )
                            items(whatIfPrompts) { prompt ->
                                Surface(
                                    onClick = { onAskVoiceQuery(prompt) },
                                    shape = RoundedCornerShape(16.dp),
                                    color = VxTheme.surfaceBlue,
                                    border = BorderStroke(1.dp, VxTheme.primaryBlue.copy(alpha = 0.35f))
                                ) {
                                    Text(
                                        text = prompt,
                                        fontSize = 12.sp,
                                        color = VxTheme.primaryBlue,
                                        fontWeight = FontWeight.Medium,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // 4. Live Conversational Chat Feed with Gemini 3.8 Flash
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "💬 किसान संवाद (Gemini AI Chat):",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = VxTheme.purple.copy(alpha = 0.12f)
                        ) {
                            Text(
                                text = "Gemini Active",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = VxTheme.purple,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }
                }

                items(chatMessages, key = { it.id }) { msg ->
                    if (msg.isUser) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            Surface(
                                shape = RoundedCornerShape(topStart = 16.dp, topEnd = 4.dp, bottomStart = 16.dp, bottomEnd = 16.dp),
                                color = VxTheme.primaryBlue,
                                modifier = Modifier.fillMaxWidth(0.85f)
                            ) {
                                Text(
                                    text = msg.text,
                                    color = Color.White,
                                    fontSize = 13.sp,
                                    lineHeight = 18.sp,
                                    modifier = Modifier.padding(12.dp)
                                )
                            }
                        }
                    } else {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Start
                        ) {
                            Card(
                                shape = RoundedCornerShape(topStart = 4.dp, topEnd = 16.dp, bottomStart = 16.dp, bottomEnd = 16.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                border = BorderStroke(1.dp, VxTheme.purple.copy(alpha = 0.35f)),
                                modifier = Modifier.fillMaxWidth(0.92f)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.AutoAwesome,
                                            contentDescription = null,
                                            tint = VxTheme.purple,
                                            modifier = Modifier.size(15.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "GREENCOLD Kisan AI (Gemini 3.8 Flash)",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = VxTheme.purple
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = msg.text,
                                        fontSize = 13.sp,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        lineHeight = 19.sp
                                    )
                                }
                            }
                        }
                    }
                }

                // Gemini Thinking Indicator
                if (isAiThinking) {
                    item {
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = VxTheme.surfaceBlue),
                            border = BorderStroke(1.dp, VxTheme.primaryBlue.copy(alpha = 0.4f)),
                            modifier = Modifier.fillMaxWidth(0.88f)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(18.dp),
                                    strokeWidth = 2.dp,
                                    color = VxTheme.primaryBlue
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = "Gemini 3.8 Flash विश्लेषण कर रहा है... (Calculating return & advice)",
                                    fontSize = 12.sp,
                                    color = VxTheme.primaryBlue,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }

                // 5. One-Tap Quick Questions for Farmers / Illiterate Users
                item {
                    Text(
                        text = "⚡ ${AppTranslations.get("quick_questions", selectedLanguage)}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                items(FarmerQuestionType.values()) { qType ->
                    Surface(
                        onClick = { onAskFarmerQuestion(qType) },
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surface,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("quick_question_${qType.name.lowercase()}")
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(text = qType.iconEmoji, fontSize = 22.sp)
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = qType.hindiTitle,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = qType.englishTitle,
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(VxTheme.surfaceBlue),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.VolumeUp,
                                    contentDescription = "Speak answer",
                                    tint = VxTheme.primaryBlue,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }

                // 6. Type Query Input Box
                item {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "✍️ लिखकर पूछें (Type question directly to Gemini)",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = queryText,
                            onValueChange = { queryText = it },
                            placeholder = { Text("जैसे: अगर 5 दिन बाद बेचें तो क्या return मिलेगा?", fontSize = 12.sp) },
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = VxTheme.primaryBlue,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline
                            ),
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                            keyboardActions = KeyboardActions(
                                onSend = {
                                    if (queryText.isNotBlank()) {
                                        onAskVoiceQuery(queryText)
                                        queryText = ""
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
                                    queryText = ""
                                }
                            },
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(VxTheme.primaryBlue)
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
