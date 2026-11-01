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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.PlayArrow
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
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.window.Dialog
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
    onSpeakText: (String) -> Unit = {},
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var queryText by remember { mutableStateOf("") }
    var isListening by remember { mutableStateOf(false) }
    var speechError by remember { mutableStateOf<String?>(null) }
    var showDirectVoiceDialog by remember { mutableStateOf(false) }

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
            // If device/browser has no SpeechRecognizer, open interactive voice query dialog
            showDirectVoiceDialog = true
            return
        }

        speechError = null
        isListening = true
        showDirectVoiceDialog = true
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
                    SpeechRecognizer.ERROR_NO_MATCH -> "No speech recognized. Please select or speak your question below."
                    SpeechRecognizer.ERROR_NETWORK, SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "Network issue. Please select question below."
                    SpeechRecognizer.ERROR_AUDIO -> "Audio recording error. Please select question below."
                    else -> "Voice recognition note (code $error). Select question below."
                }
            }

            override fun onResults(results: Bundle?) {
                isListening = false
                showDirectVoiceDialog = false
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
            speechError = "Speech recognizer active. You can speak or select directly below."
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
            // Permission denied, still open dialog for easy prompt tap & voice reply
            showDirectVoiceDialog = true
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
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(750, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    // Direct Voice Recording & Prompt Dialog
    if (showDirectVoiceDialog) {
        Dialog(onDismissRequest = {
            stopListening()
            showDirectVoiceDialog = false
        }) {
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 6.dp,
                border = BorderStroke(2.dp, VxTheme.primaryBlue.copy(alpha = 0.5f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .testTag("direct_voice_recording_dialog")
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.GraphicEq,
                                contentDescription = null,
                                tint = VxTheme.primaryBlue,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "किसान आवाज़ रिकॉर्डर",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        IconButton(
                            onClick = {
                                stopListening()
                                showDirectVoiceDialog = false
                            },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Animated Mic circle
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(72.dp)
                            .scale(if (isListening) pulseScale else 1f)
                            .clip(CircleShape)
                            .background(if (isListening) VxTheme.errorRed else VxTheme.primaryBlue)
                            .clickable {
                                if (isListening) stopListening() else startListening()
                            }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Mic,
                            contentDescription = "Mic",
                            tint = Color.White,
                            modifier = Modifier.size(36.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = if (isListening) "बोलिए... सुन रहे हैं (Listening...)" else "सीधे आवाज़ से सवाल पूछें",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isListening) VxTheme.errorRed else VxTheme.primaryBlue
                    )

                    Text(
                        text = "नीचे दिए गए सवाल पर टैप करके तुरंत बोलें और Gemini से आवाज़ में उत्तर सुनें:",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
                    )

                    // Spoken Quick Prompts in Dialog
                    val directVoicePrompts = listOf(
                        "🧄 सविना मंडी में लहसुन का भाव और तेजी कब आएगी?",
                        "🍅 टमाटर को कोल्ड स्टोरेज में रखने पर कितना मुनाफा मिलेगा?",
                        "🧅 प्याज को कब बेचना सबसे ज्यादा फायदेमंद रहेगा?",
                        "🌿 फसलों में कीट और फफूंद रोकने का जैविक उपाय?",
                        "🧪 लहसुन और प्याज में कौन सा खाद डालें?",
                        "☀️ सोलर पावर कम होने पर बैकअप कितना चलेगा?",
                        "🌡️ कमरा 1 और कमरा 2 का तापमान कैसा है?"
                    )

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        items(directVoicePrompts) { prompt ->
                            Surface(
                                onClick = {
                                    stopListening()
                                    showDirectVoiceDialog = false
                                    onAskVoiceQuery(prompt)
                                },
                                shape = RoundedCornerShape(12.dp),
                                color = VxTheme.surfaceBlue,
                                border = BorderStroke(1.dp, VxTheme.primaryBlue.copy(alpha = 0.35f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.VolumeUp,
                                        contentDescription = null,
                                        tint = VxTheme.primaryBlue,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = prompt,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = VxTheme.primaryBlue
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Text fallback input inside dialog
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = queryText,
                            onValueChange = { queryText = it },
                            placeholder = { Text("या यहाँ अपना सवाल लिखें...", fontSize = 12.sp) },
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        IconButton(
                            onClick = {
                                if (queryText.isNotBlank()) {
                                    val text = queryText
                                    queryText = ""
                                    stopListening()
                                    showDirectVoiceDialog = false
                                    onAskVoiceQuery(text)
                                }
                            },
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(VxTheme.primaryBlue)
                        ) {
                            Icon(imageVector = Icons.Default.Send, contentDescription = "Send", tint = Color.White)
                        }
                    }
                }
            }
        }
    }

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

                // Gemini AI Status Pill & Active Speech Banner
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = VxTheme.purple.copy(alpha = 0.12f),
                            border = BorderStroke(1.dp, VxTheme.purple.copy(alpha = 0.35f))
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
                                    text = "Gemini 3.8 Flash कृषि विशेषज्ञ Active",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = VxTheme.purple
                                )
                            }
                        }

                        if (isSpeaking) {
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = VxTheme.success.copy(alpha = 0.15f),
                                border = BorderStroke(1.dp, VxTheme.success)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.VolumeUp,
                                        contentDescription = null,
                                        tint = VxTheme.success,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "बोल रहा है (Speaking)",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = VxTheme.success
                                    )
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
                                    isSpeaking -> "Gemini बोल रहा है... (Speaking Aloud)"
                                    else -> "माइक दबाकर सीधे बोलें (Tap Mic to Speak)"
                                },
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSpeaking) VxTheme.success else MaterialTheme.colorScheme.onSurface,
                                textAlign = TextAlign.Center
                            )

                            Text(
                                text = "सविना मंडी भाव, लहसुन, टमाटर रिटर्न, फसल रोग व सोलर बैकअप पर सीधे बात करें",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(top = 4.dp)
                            )

                            // Direct Voice Dialog trigger button
                            Button(
                                onClick = { showDirectVoiceDialog = true },
                                colors = ButtonDefaults.buttonColors(containerColor = VxTheme.surfaceBlue),
                                border = BorderStroke(1.dp, VxTheme.primaryBlue),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 12.dp)
                                    .testTag("open_direct_voice_dialog_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.GraphicEq,
                                    contentDescription = null,
                                    tint = VxTheme.primaryBlue,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "🎙️ आवाज़ से पूछें (Open Voice Assistant)",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = VxTheme.primaryBlue
                                )
                            }

                            // Stop Speaking / Read Status buttons
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 10.dp),
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
                                    Text(text = "स्टोरेज व मंडी स्टेटस सुनें", fontSize = 13.sp)
                                }

                                if (isSpeaking) {
                                    Button(
                                        onClick = onStopSpeaking,
                                        colors = ButtonDefaults.buttonColors(containerColor = VxTheme.errorRed),
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        Icon(imageVector = Icons.Default.Stop, contentDescription = null, modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(text = "आवाज़ रोकें", fontSize = 12.sp)
                                    }
                                }
                            }

                            if (speechError != null) {
                                Text(
                                    text = speechError ?: "",
                                    color = VxTheme.primaryBlue,
                                    fontSize = 12.sp,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(top = 8.dp)
                                )
                            }
                        }
                    }
                }

                // 3. What-If & Mandi Return Quick Prompt Chips
                item {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "💡 क्या-अगर और मंडी सवाल (Mandi & Return Scenarios):",
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
                                "🧄 सविना मंडी में लहसुन का ताजा भाव और तेजी कब आएगी?",
                                "🍅 टमाटर को कोल्ड स्टोरेज में रखने पर कितना मुनाफा मिलेगा?",
                                "🧅 प्याज को कब बेचना सबसे ज्यादा फायदेमंद रहेगा?",
                                "🌿 फसलों में कीट और फफूंद रोकने का जैविक उपाय?",
                                "🧪 लहसुन और प्याज में कौन सा खाद डालें?",
                                "☀️ सोलर पावर कम होने पर बैकअप कितना चलेगा?"
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
                                modifier = Modifier.fillMaxWidth(0.95f)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
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

                                        // Listen again button on every response
                                        IconButton(
                                            onClick = { onSpeakText(msg.text) },
                                            modifier = Modifier.size(28.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.VolumeUp,
                                                contentDescription = "Listen Aloud",
                                                tint = VxTheme.primaryBlue,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
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
                            modifier = Modifier.fillMaxWidth(0.92f)
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
                                    text = "Gemini 3.8 Flash कृषि व मंडी विश्लेषण कर रहा है...",
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
                        text = "⚡ किसान त्वरित प्रश्न (One-Tap Agricultural Voice Questions):",
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
                            placeholder = { Text("जैसे: सविना मंडी में लहसुन का भाव क्या है?", fontSize = 12.sp) },
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
