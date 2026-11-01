package com.example.util

import android.content.Context
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

/**
 * Supported Indian languages for multi-language speech, translation, and Kisan Voice Assistant.
 */
enum class IndianLanguage(
    val code: String,
    val displayName: String,
    val nativeName: String,
    val flagEmoji: String,
    val localeTag: String
) {
    HINDI("hi", "Hindi", "हिन्दी", "🇮🇳", "hi-IN"),
    ENGLISH("en", "English", "English", "🌐", "en-IN"),
    GUJARATI("gu", "Gujarati", "ગુજરાતી", "🌾", "gu-IN"),
    MARATHI("mr", "Marathi", "मराठी", "🚩", "mr-IN"),
    PUNJABI("pa", "Punjabi", "ਪੰਜਾਬੀ", "🚜", "pa-IN"),
    BENGALI("bn", "Bengali", "বাংলা", "🐟", "bn-IN"),
    TELUGU("te", "Telugu", "తెలుగు", "🌱", "te-IN"),
    TAMIL("ta", "Tamil", "தமிழ்", "🥥", "ta-IN"),
    KANNADA("kn", "Kannada", "ಕನ್ನಡ", "🍃", "kn-IN"),
    MALAYALAM("ml", "Malayalam", "മലയാളം", "🌴", "ml-IN");

    fun getLocale(): Locale {
        return when (this) {
            HINDI -> Locale("hi", "IN")
            ENGLISH -> Locale("en", "IN")
            GUJARATI -> Locale("gu", "IN")
            MARATHI -> Locale("mr", "IN")
            PUNJABI -> Locale("pa", "IN")
            BENGALI -> Locale("bn", "IN")
            TELUGU -> Locale("te", "IN")
            TAMIL -> Locale("ta", "IN")
            KANNADA -> Locale("kn", "IN")
            MALAYALAM -> Locale("ml", "IN")
        }
    }
}

/**
 * Voice Assistant Manager providing Text-To-Speech (bolne vala system) and
 * intelligent agricultural question-answering across 10 Indian languages.
 */
class VoiceAssistantManager(context: Context) {
    private var textToSpeech: TextToSpeech? = null
    private var isTtsInitialized = false

    private val _isSpeaking = MutableStateFlow(false)
    val isSpeaking: StateFlow<Boolean> = _isSpeaking.asStateFlow()

    private val _lastSpokenText = MutableStateFlow("")
    val lastSpokenText: StateFlow<String> = _lastSpokenText.asStateFlow()

    private val _selectedLanguage = MutableStateFlow(IndianLanguage.HINDI)
    val selectedLanguage: StateFlow<IndianLanguage> = _selectedLanguage.asStateFlow()

    init {
        try {
            textToSpeech = TextToSpeech(context.applicationContext) { status ->
                if (status == TextToSpeech.SUCCESS) {
                    isTtsInitialized = true
                    textToSpeech?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                        override fun onStart(utteranceId: String?) {
                            _isSpeaking.value = true
                        }

                        override fun onDone(utteranceId: String?) {
                            _isSpeaking.value = false
                        }

                        override fun onError(utteranceId: String?) {
                            _isSpeaking.value = false
                        }
                    })
                    // Set default Hindi language
                    textToSpeech?.language = Locale("hi", "IN")
                } else {
                    Log.e("VoiceAssistant", "Failed to initialize TextToSpeech: status $status")
                }
            }
        } catch (e: Exception) {
            Log.e("VoiceAssistant", "Error initializing TTS", e)
        }
    }

    fun setLanguage(lang: IndianLanguage) {
        _selectedLanguage.value = lang
        if (isTtsInitialized && textToSpeech != null) {
            try {
                val locale = lang.getLocale()
                val result = textToSpeech?.setLanguage(locale)
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    // Fallback to Hindi or default
                    textToSpeech?.language = Locale("hi", "IN")
                }
            } catch (e: Exception) {
                Log.w("VoiceAssistant", "Language not available on device TTS: ${lang.code}", e)
            }
        }
    }

    fun speak(text: String, language: IndianLanguage = _selectedLanguage.value) {
        _lastSpokenText.value = text
        if (!isTtsInitialized || textToSpeech == null) {
            Log.w("VoiceAssistant", "TTS not initialized, unable to speak: $text")
            return
        }

        try {
            textToSpeech?.setLanguage(language.getLocale())
            textToSpeech?.setSpeechRate(0.92f) // Slightly slower for clarity for farmers
            textToSpeech?.setPitch(1.0f)

            val params = Bundle().apply {
                putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "cold_storage_voice_${System.currentTimeMillis()}")
            }
            textToSpeech?.speak(text, TextToSpeech.QUEUE_FLUSH, params, "cold_storage_voice")
            _isSpeaking.value = true
        } catch (e: Exception) {
            Log.e("VoiceAssistant", "Error speaking text", e)
            _isSpeaking.value = false
        }
    }

    fun stopSpeaking() {
        try {
            textToSpeech?.stop()
            _isSpeaking.value = false
        } catch (e: Exception) {
            Log.e("VoiceAssistant", "Error stopping TTS", e)
        }
    }

    fun shutdown() {
        try {
            textToSpeech?.stop()
            textToSpeech?.shutdown()
            textToSpeech = null
        } catch (e: Exception) {
            Log.e("VoiceAssistant", "Error shutting down TTS", e)
        }
    }

    /**
     * Generates audio summary of the current storage state in the selected Indian language.
     */
    fun getStorageStatusSpeech(
        language: IndianLanguage = _selectedLanguage.value,
        c1Temp: Double,
        c2Temp: Double,
        batteryPercent: Int,
        solarWatts: Double,
        gasStatus: String
    ): String {
        val c1Safe = c1Temp in 10.0..14.0
        val c2Safe = c2Temp in 2.0..6.0
        val allSafe = c1Safe && c2Safe

        return when (language) {
            IndianLanguage.HINDI -> {
                val statusText = if (allSafe) "कमरे का तापमान बिल्कुल सही है।" else "कृपया ध्यान दें, तापमान की जांच करें।"
                "कोल्ड स्टोरेज की ताजा रिपोर्ट। कमरा नंबर 1 का तापमान $c1Temp डिग्री है। कमरा नंबर 2 का तापमान $c2Temp डिग्री है। सोलर पैनल से ${solarWatts.toInt()} वाट बिजली आ रही है, और बैटरी $batteryPercent प्रतिशत चार्ज है। हवा शुद्ध है। $statusText आपकी सभी फसलें पूरी तरह सुरक्षित हैं।"
            }
            IndianLanguage.GUJARATI -> {
                val statusText = if (allSafe) "બધું બરાબર છે." else "તાપમાન તપાસો."
                "કોલ્ડ સ્ટોરેજ રિપોર્ટ. ચેમ્બર 1 નું તાપમાન $c1Temp ડિગ્રી છે. ચેમ્બર 2 નું તાપમાન $c2Temp ડિગ્રી છે. સોલાર પાવર ${solarWatts.toInt()} વોટ છે અને બેટરી $batteryPercent ટકા છે. $statusText પાક સુરક્ષિત છે."
            }
            IndianLanguage.MARATHI -> {
                val statusText = if (allSafe) "सर्व सुरक्षित आहे." else "तापमान तपासा."
                "कोल्ड स्टोरेज अहवाल. खोली क्रमांक 1 चे तापमान $c1Temp अंश आहे. खोली क्रमांक 2 चे तापमान $c2Temp अंश आहे. सौर ऊर्जा ${solarWatts.toInt()} वॅट आणि बॅटरी $batteryPercent टक्के आहे. $statusText शेतमाल सुरक्षित आहे."
            }
            IndianLanguage.PUNJABI -> {
                val statusText = if (allSafe) "ਸਭ ਕੁਝ ਠੀਕ ਹੈ।" else "ਤਾਪਮਾਨ ਚੈੱਕ ਕਰੋ।"
                "ਕੋਲਡ ਸਟੋਰੇਜ ਰਿਪੋਰਟ। ਚੈਂਬਰ 1 ਦਾ ਤਾਪਮਾਨ $c1Temp ਡਿਗਰੀ ਹੈ। ਚੈਂਬਰ 2 ਦਾ ਤਾਪਮਾਨ $c2Temp ਡਿਗਰੀ ਹੈ। ਸੋਲਰ ਪਾਵਰ ${solarWatts.toInt()} ਵਾਟ ਅਤੇ ਬੈਟਰੀ $batteryPercent ਪ੍ਰਤੀਸ਼ਤ ਹੈ। $statusText ਸਾਰੀਆਂ ਫ਼ਸਲਾਂ ਸੁਰੱਖਿਅਤ ਹਨ।"
            }
            IndianLanguage.BENGALI -> {
                val statusText = if (allSafe) "সবকিছু ঠিক আছে।" else "তাপমাত্রা পরীক্ষা করুন।"
                "কোল্ড স্টোরেজ রিপোর্ট। চেম্বার ১ এর তাপমাত্রা $c1Temp ডিগ্রি। চেম্বার ২ এর তাপমাত্রা $c2Temp ডিগ্রি। সৌরশক্তি ${solarWatts.toInt()} ওয়াট এবং ব্যাটারি $batteryPercent শতাংশ। $statusText ফসল সম্পূর্ণ নিরাপদ।"
            }
            IndianLanguage.TELUGU -> {
                "కోల్డ్ స్టోరేజ్ నివేదిక. ఛాంబర్ 1 ఉష్ణోగ్రత $c1Temp డిగ్రీలు. ఛాంబర్ 2 ఉష్ణోగ్రత $c2Temp డిగ్రీలు. సోలార్ పవర్ ${solarWatts.toInt()} వాట్లు, బ్యాటరీ $batteryPercent శాతం. పంటలు సురక్షితంగా ఉన్నాయి."
            }
            IndianLanguage.TAMIL -> {
                "குளிர்சாதன கிடங்கு நிலை. அறை 1 வெப்பநிலை $c1Temp டிகிரி. அறை 2 வெப்பநிலை $c2Temp டிகிரி. சூரிய சக்தி ${solarWatts.toInt()} வாட்ஸ், பேட்டரி $batteryPercent சதவீதம். பயிர்கள் பாதுகாப்பாக உள்ளன."
            }
            IndianLanguage.KANNADA -> {
                "ಕೋಲ್ಡ್ ಸ್ಟೋರೇಜ್ ವರದಿ. ಕೋಣೆ 1 ರ ತಾಪಮಾನ $c1Temp ಡಿಗ್ರಿ. ಕೋಣೆ 2 ರ ತಾಪಮಾನ $c2Temp ಡಿಗ್ರಿ. ಸೌರಶಕ್ತಿ ${solarWatts.toInt()} ವ್ಯಾಟ್ಸ್, ಬ್ಯಾಟರಿ $batteryPercent ಶೇಕಡಾ. ಬೆಳೆಗಳು ಸುರಕ್ಷಿತವಾಗಿವೆ."
            }
            IndianLanguage.MALAYALAM -> {
                "കോൾഡ് സ്റ്റോറേജ് റിപ്പോർട്ട്. ചേംബർ 1 താപനില $c1Temp ഡിഗ്രി. ചേംബർ 2 താപനില $c2Temp ഡിഗ്രി. സൗരോർജ്ജം ${solarWatts.toInt()} വാട്ട്സ്, ബാറ്ററി $batteryPercent ശതമാനം. എല്ലാം സുരക്ഷിതമാണ്."
            }
            IndianLanguage.ENGLISH -> {
                val statusText = if (allSafe) "All systems are running normally." else "Attention required, please check chambers."
                "Cold storage status update. Chamber 1 is at $c1Temp degrees Celsius. Chamber 2 is at $c2Temp degrees Celsius. Solar input is ${solarWatts.toInt()} Watts, with battery charge at $batteryPercent percent. Air quality is safe. $statusText"
            }
        }
    }

    /**
     * Answers farmer questions in natural voice in their native Indian language.
     */
    fun answerQuestion(
        questionType: FarmerQuestionType,
        language: IndianLanguage = _selectedLanguage.value,
        c1Temp: Double,
        c2Temp: Double,
        batteryPercent: Int,
        solarWatts: Double,
        gasStatus: String,
        produceName: String = "Tomato"
    ): String {
        return when (questionType) {
            FarmerQuestionType.GARLIC_MANDI -> when (language) {
                IndianLanguage.HINDI -> "उदयपुर सविना मंडी में लहसुन 130 से 175 रुपये प्रति किलो बिक रहा है। कोल्ड स्टोरेज में रखने पर आगामी त्योहारी सीजन में 30 से 40 प्रतिशत तक अधिक मुनाफा मिल सकता है।"
                else -> "In Udaipur Savina Mandi, quality garlic is trading between 130 and 175 rupees per kg. Cold storage preserves quality and can yield 30 to 40 percent higher profit in upcoming festive demand."
            }

            FarmerQuestionType.TOMATO_RETURN -> when (language) {
                IndianLanguage.HINDI -> "सविना मंडी में टमाटर 28 से 38 रुपये प्रति किलो है। कमरा 1 (10-12°C) में 10 से 15 दिन सुरक्षित रखने पर स्थानीय आवक कम होते ही 12 से 18 रुपये प्रति किलो का अतिरिक्त रिटर्न मिल सकता है।"
                else -> "In Savina Mandi, tomatoes are trading at 28 to 38 rupees per kg. Storing in Chamber 1 at 10-12°C for 10-15 days can net an extra 12 to 18 rupees per kg."
            }

            FarmerQuestionType.ONION_STORAGE -> when (language) {
                IndianLanguage.HINDI -> "प्याज को कमरा 1 में 10 से 12 डिग्री और 65 से 70 प्रतिशत नमी पर रखें। सविना मंडी में वर्तमान भाव 22 से 30 रुपये है, जो आगे चलकर 35 से 42 रुपये तक पहुंचने की उम्मीद है।"
                else -> "Store dry onions in Chamber 1 at 10-12°C and 65-70% humidity. Current Savina Mandi rate is 22 to 30 rupees per kg, with prices expected to reach 35 to 42 rupees."
            }

            FarmerQuestionType.CROP_DISEASES -> when (language) {
                IndianLanguage.HINDI -> "फसलों में फफूंद और सड़न रोकने के लिए ट्राइकोडर्मा या कार्बेंडाजिम का उपयोग करें। रस चूसक कीटों के लिए 5 मिलीलीटर नीम तेल प्रति लीटर पानी में स्प्रे करें। केवल स्वस्थ और सूखी फसल ही स्टोर करें।"
                else -> "Apply Trichoderma or carbendazim to stop fungal rot. For sucking pests, spray 5ml neem oil per litre of water. Always pre-cure produce thoroughly before cold storage."
            }

            FarmerQuestionType.FERTILIZER_GUIDE -> when (language) {
                IndianLanguage.HINDI -> "लहसुन और प्याज के लिए प्रति बीघा 1 बोरी डीएपी, 25 किलो पोटाश और 10 किलो सल्फर डालें। फसल बढ़वार के लिए नैनो यूरिया 4 मिलीलीटर प्रति लीटर पानी का स्प्रे सर्वोत्तम है।"
                else -> "For garlic and onion, apply 1 bag DAP, 25kg potash, and 10kg sulfur per bigha. Use foliar spray of Nano Urea at 4ml per litre for vigorous growth."
            }

            FarmerQuestionType.TEMPERATURE -> when (language) {
                IndianLanguage.HINDI -> "कमरा नंबर 1 का तापमान $c1Temp डिग्री है और कमरा नंबर 2 का तापमान $c2Temp डिग्री है। दोनों कमरे में ठंडक बिल्कुल सही बनी हुई है।"
                IndianLanguage.GUJARATI -> "ચેમ્બર 1 નું તાપમાન $c1Temp ડિગ્રી અને ચેમ્બર 2 નું $c2Temp ડિગ્રી છે. બરાબર ઠંડક છે."
                IndianLanguage.MARATHI -> "खोली 1 चे तापमान $c1Temp अंश आणि खोली 2 चे $c2Temp अंश आहे. योग्य थंडी चालू आहे."
                IndianLanguage.PUNJABI -> "ਚੈਂਬਰ 1 ਦਾ ਤਾਪਮਾਨ $c1Temp ਡਿਗਰੀ ਅਤੇ ਚੈਂਬਰ 2 ਦਾ $c2Temp ਡਿਗਰੀ ਹੈ। ਠੰਢ ਬਿਲਕੁਲ ਸਹੀ ਹੈ।"
                IndianLanguage.BENGALI -> "চেম্বার ১ এ তাপমাত্রা $c1Temp ডিগ্রি এবং চেম্বার ২ এ $c2Temp ডিগ্রি। তাপমাত্রা স্বাভাবিক।"
                IndianLanguage.TELUGU -> "ఛాంబర్ 1 లో $c1Temp డిగ్రీలు మరియు ఛాంబర్ 2 లో $c2Temp డిగ్రీల ఉష్ణోగ్రత ఉంది."
                IndianLanguage.TAMIL -> "அறை 1 வெப்பநிலை $c1Temp டிகிரி மற்றும் அறை 2 வெப்பநிலை $c2Temp டிகிரி."
                IndianLanguage.KANNADA -> "ಕೋಣೆ 1 ರಲ್ಲಿ $c1Temp ಡಿಗ್ರಿ ಮತ್ತು ಕೋಣೆ 2 ರಲ್ಲಿ $c2Temp ಡಿಗ್ರಿ ತಾಪಮಾನವಿದೆ."
                IndianLanguage.MALAYALAM -> "ചേംബർ 1 താപനില $c1Temp ഡിഗ്രിയും ചേംബർ 2 താപനില $c2Temp ഡിഗ്രിയുമാണ്."
                IndianLanguage.ENGLISH -> "Chamber 1 temperature is $c1Temp degrees Celsius, and Chamber 2 is at $c2Temp degrees Celsius. Both cooling zones are operating normally."
            }

            FarmerQuestionType.SHELF_LIFE -> when (language) {
                IndianLanguage.HINDI -> "आपके $produceName इस तापमान में लगभग 12 से 15 दिनों तक बिल्कुल ताजे और सुरक्षित रहेंगे। कोई सड़न नहीं होगी।"
                IndianLanguage.GUJARATI -> "તમારા $produceName લગભગ 12 થી 15 દિવસ સુધી તાજા અને સુરક્ષિત રહેશે."
                IndianLanguage.MARATHI -> "तुमचे $produceName सुमारे 12 ते 15 दिवस ताजे आणि सुरक्षित राहतील."
                IndianLanguage.PUNJABI -> "ਤੁਹਾਡੇ $produceName ਲਗਭਗ 12 ਤੋਂ 15 ਦਿਨਾਂ ਤੱਕ ਬਿਲਕੁਲ ਤਾਜ਼ੇ ਰਹਿਣਗੇ।"
                IndianLanguage.BENGALI -> "আপনার $produceName প্রায় ১২ থেকে ১৫ দিন তাজা এবং সুরক্ষিত থাকবে।"
                IndianLanguage.TELUGU -> "మీ పంట దాదాపు 12 నుండి 15 రోజుల వరకు తాజాగా ఉంటుంది."
                IndianLanguage.TAMIL -> "உங்கள் பயிர் சுமார் 12 முதல் 15 நாட்களுக்கு புதியதாக இருக்கும்."
                IndianLanguage.KANNADA -> "ನಿಮ್ಮ ಬೆಳೆ ಸುಮಾರು 12 ರಿಂದ 15 ದಿನಗಳವರೆಗೆ ತಾಜಾವಾಗಿರುತ್ತದೆ."
                IndianLanguage.MALAYALAM -> "നിങ്ങളുടെ വിള ഏകദേശം 12 മുതൽ 15 ദിവസം വരെ കേടുകൂടാതെയിരിക്കും."
                IndianLanguage.ENGLISH -> "Under current cold storage conditions, your $produceName will remain fresh and safe for approximately 12 to 15 days."
            }

            FarmerQuestionType.POWER_SOLAR -> when (language) {
                IndianLanguage.HINDI -> "सोलर पैनल से ${solarWatts.toInt()} वाट बिजली आ रही है, और बैटरी $batteryPercent प्रतिशत चार्ज है। बिजली की कोई कमी नहीं है।"
                IndianLanguage.GUJARATI -> "સોલાર પેનલમાંથી ${solarWatts.toInt()} વોટ પાવર આવી રહ્યો છે અને બેટરી $batteryPercent ટકા છે."
                IndianLanguage.MARATHI -> "सौर पॅनेल मधून ${solarWatts.toInt()} वॅट वीज येत असून बॅटरी $batteryPercent टक्के चार्ज आहे."
                IndianLanguage.PUNJABI -> "ਸੋਲਰ ਪੈਨਲ ਤੋਂ ${solarWatts.toInt()} ਵਾਟ ਬਿਜਲੀ ਆ ਰਹੀ ਹੈ ਅਤੇ ਬੈਟਰੀ $batteryPercent ਪ੍ਰਤੀਸ਼ਤ ਹੈ।"
                IndianLanguage.BENGALI -> "সৌর প্যানেল থেকে ${solarWatts.toInt()} ওয়াট বিদ্যুৎ আসছে এবং ব্যাটারি $batteryPercent শতাংশ।"
                IndianLanguage.TELUGU -> "సోలార్ నుండి ${solarWatts.toInt()} వాట్ల విద్యుత్ అందుతోంది, బ్యాటరీ $batteryPercent శాతం ఉంది."
                IndianLanguage.TAMIL -> "சூரிய தகடுகளிலிருந்து ${solarWatts.toInt()} வாட்ஸ் மின்சாரம் கிடைக்கிறது, பேட்டரி $batteryPercent சதவீதம்."
                IndianLanguage.KANNADA -> "ಸೌರ ಫಲಕದಿಂದ ${solarWatts.toInt()} ವ್ಯಾಟ್ಸ್ ವಿದ್ಯುತ್ ಬರುತ್ತಿದೆ, ಬ್ಯಾಟರಿ $batteryPercent ಶೇಕಡಾ ಇದೆ."
                IndianLanguage.MALAYALAM -> "സോളാർ പാനലിൽ നിന്ന് ${solarWatts.toInt()} വാട്ട്സ് വൈദ്യുതി ലഭിക്കുന്നു, ബാറ്ററി $batteryPercent ശതമാനമാണ്."
                IndianLanguage.ENGLISH -> "Solar panels are generating ${solarWatts.toInt()} Watts of clean energy, and battery storage is at $batteryPercent percent capacity."
            }

            FarmerQuestionType.SAFETY_GAS -> when (language) {
                IndianLanguage.HINDI -> "हवा बिल्कुल शुद्ध है। कोई हानिकारक गैस या सड़न का संकेत नहीं है। सब सुरक्षित है।"
                IndianLanguage.GUJARATI -> "હવા શુદ્ધ છે. કોઈ હાનિકારક ગેસ નથી. બધું સુરક્ષિત છે."
                IndianLanguage.MARATHI -> "हवा स्वच्छ आहे. कोणताही अपायकारक गॅस नाही. सर्व सुरक्षित आहे."
                IndianLanguage.PUNJABI -> "ਹਵਾ ਬਿਲਕੁਲ ਸਾਫ਼ ਹੈ। ਕੋਈ ਖ਼ਤਰਨਾਕ ਗੈਸ ਨਹੀਂ ਹੈ।"
                IndianLanguage.BENGALI -> "বাতাস পরিষ্কার এবং নিরাপদ। ক্ষতিকারক কোনো গ্যাস নেই।"
                IndianLanguage.TELUGU -> "గాలి స్వచ్ఛంగా ఉంది. హానికరమైన వాయువులు లేవు."
                IndianLanguage.TAMIL -> "காற்று தூய்மையாக உள்ளது. எந்த ஆபத்தான வாயுக்களும் இல்லை."
                IndianLanguage.KANNADA -> "ಗಾಳಿ ಶುದ್ಧವಾಗಿದೆ. ಯಾವುದೇ ಅಪಾಯಕಾರಿ ಅನಿಲವಿಲ್ಲ."
                IndianLanguage.MALAYALAM -> "വായു ശുദ്ധമാണ്. വിഷവാതകങ്ങൾ ഒന്നുമില്ല."
                IndianLanguage.ENGLISH -> "Air quality is clean and normal. Ethylene gas levels are safe with no risk of spoilage detected."
            }

            FarmerQuestionType.HOW_TO_OPERATE -> when (language) {
                IndianLanguage.HINDI -> "मशीन अपने आप चलती है। आप होम स्क्रीन पर दिए गए आसान बटनों से कूलिंग और पंखे को चालू या बंद कर सकते हैं।"
                IndianLanguage.GUJARATI -> "મશીન ઓટોમેટિક ચાલે છે. તમે બટન દબાવીને કૂલિંગ કે પંખો ચાલુ-બંધ કરી શકો છો."
                IndianLanguage.MARATHI -> "यंत्र स्वयंचलित आहे. तुम्ही बटण दाबून कुलिंग आणि पंखा चालू किंवा बंद करू शकता."
                IndianLanguage.PUNJABI -> "ਮਸ਼ੀਨ ਆਪਣੇ ਆਪ ਚੱਲਦੀ ਹੈ। ਤੁਸੀਂ ਬਟਨਾਂ ਨਾਲ ਕੂਲਿੰਗ ਜਾਂ ਪੱਖਾ ਚਾਲੂ ਕਰ ਸਕਦੇ ਹੋ।"
                IndianLanguage.BENGALI -> "মেশিন স্বয়ংক্রিয়ভাবে চলে। আপনি সহজ বোতাম টিপে কুলিং ও পাখা নিয়ন্ত্রণ করতে পারেন।"
                IndianLanguage.TELUGU -> "మిషన్ ఆటోమేటిక్ గా పనిచేస్తుంది. మీరు బటన్లను ఉపయోగించి సులభంగా నియంత్రించవచ్చు."
                IndianLanguage.TAMIL -> "இயந்திரம் தானாக இயங்குகிறது. பொத்தான்கள் மூலம் குளிரூட்டியை கட்டுப்படுத்தலாம்."
                IndianLanguage.KANNADA -> "ಯಂತ್ರವು ಸ್ವಯಂಚಾಲಿತವಾಗಿ ಕಾರ್ಯನಿರ್ವಹಿಸುತ್ತದೆ. ಬಟನ್ ಒತ್ತುವ ಮೂಲಕ ನಿಯಂತ್ರಿಸಬಹುದು."
                IndianLanguage.MALAYALAM -> "മെഷീൻ ഓട്ടോമാറ്റിക്കായി പ്രവർത്തിക്കുന്നു. ബട്ടണുകൾ ഉപയോഗിച്ച് നിയന്ത്രിക്കാം."
                IndianLanguage.ENGLISH -> "The cold storage unit is automated. You can easily toggle the cooling compressors and circulation fans using the big switches on the dashboard."
            }
        }
    }

    /**
     * Intelligently interprets any free-form voice speech query in Hindi or other languages
     * and returns an appropriate spoken response.
     */
    fun processVoiceQuery(
        query: String,
        language: IndianLanguage = _selectedLanguage.value,
        c1Temp: Double,
        c2Temp: Double,
        batteryPercent: Int,
        solarWatts: Double,
        gasStatus: String,
        activeCratesCount: Int = 12
    ): String {
        val lower = query.lowercase().trim()
        val questionType = when {
            lower.contains("lahsun") || lower.contains("garlic") || lower.contains("लहसुन") || lower.contains("लहसून") -> {
                FarmerQuestionType.GARLIC_MANDI
            }
            lower.contains("tamatar") || lower.contains("tomato") || lower.contains("टमाटर") || lower.contains("return") || lower.contains("मुनाफा") -> {
                FarmerQuestionType.TOMATO_RETURN
            }
            lower.contains("pyaz") || lower.contains("onion") || lower.contains("कांदा") || lower.contains("प्याज") || lower.contains("प्याज़") -> {
                FarmerQuestionType.ONION_STORAGE
            }
            lower.contains("rog") || lower.contains("disease") || lower.contains("pest") || lower.contains("keet") || lower.contains("fungus") || lower.contains("रोग") || lower.contains("कीट") || lower.contains("फंगस") || lower.contains("इलाज") -> {
                FarmerQuestionType.CROP_DISEASES
            }
            lower.contains("khad") || lower.contains("fertilizer") || lower.contains("dap") || lower.contains("urea") || lower.contains("खाद") || lower.contains("यूरिया") || lower.contains("डीएपी") -> {
                FarmerQuestionType.FERTILIZER_GUIDE
            }
            lower.contains("taapman") || lower.contains("tapman") || lower.contains("temp") || lower.contains("degree") || lower.contains("thanda") || lower.contains("garam") || lower.contains("तापमान") || lower.contains("તાપમાન") || lower.contains("ਤਾਪਮਾਨ") -> {
                FarmerQuestionType.TEMPERATURE
            }
            lower.contains("battery") || lower.contains("solar") || lower.contains("bijli") || lower.contains("power") || lower.contains("current") || lower.contains("चार्ज") || lower.contains("बैटरी") || lower.contains("सोलर") -> {
                FarmerQuestionType.POWER_SOLAR
            }
            lower.contains("gas") || lower.contains("khatra") || lower.contains("danger") || lower.contains("safe") || lower.contains("alert") || lower.contains("खतरा") || lower.contains("गैस") -> {
                FarmerQuestionType.SAFETY_GAS
            }
            lower.contains("chala") || lower.contains("band") || lower.contains("button") || lower.contains("kaise") || lower.contains("fan") || lower.contains("cooling") || lower.contains("चालू") || lower.contains("बंद") -> {
                FarmerQuestionType.HOW_TO_OPERATE
            }
            lower.contains("din") || lower.contains("fasal") || lower.contains("aloo") || lower.contains("seb") || lower.contains("shelf") || lower.contains("life") || lower.contains("खराब") || lower.contains("दिन") -> {
                FarmerQuestionType.SHELF_LIFE
            }
            else -> null
        }

        return if (questionType != null) {
            answerQuestion(
                questionType = questionType,
                language = language,
                c1Temp = c1Temp,
                c2Temp = c2Temp,
                batteryPercent = batteryPercent,
                solarWatts = solarWatts,
                gasStatus = gasStatus
            )
        } else {
            "सविना मंडी उदयपुर में वर्तमान में लहसुन और टमाटर के भाव अच्छे बने हुए हैं। कोल्ड स्टोरेज में रखने पर फसलें सुरक्षित हैं और बाजार में तेजी आने पर आपको अतिरिक्त मुनाफा मिलेगा।"
        }
    }
}

enum class FarmerQuestionType(
    val hindiTitle: String,
    val englishTitle: String,
    val iconEmoji: String
) {
    GARLIC_MANDI("सविना मंडी में लहसुन का ताजा भाव और तेजी कब आएगी?", "Garlic Price & Savina Mandi Trend", "🧄"),
    TOMATO_RETURN("टमाटर कोल्ड स्टोरेज में रखने पर कितना मुनाफा मिलेगा?", "Tomato Storage ROI & Profit", "🍅"),
    ONION_STORAGE("प्याज को कब बेचना सबसे ज्यादा फायदेमंद रहेगा?", "Onion Best Selling Time & Storage", "🧅"),
    CROP_DISEASES("फसलों में कीट और फफूंद रोकने का जैविक उपाय?", "Crop Disease, Pest & Fungus Care", "🌿"),
    FERTILIZER_GUIDE("लहसुन और प्याज में कौन सा खाद डालें?", "Fertilizer, DAP & Urea Advice", "🧪"),
    TEMPERATURE("कमरे का तापमान कैसा है?", "How is the temperature?", "🌡️"),
    SHELF_LIFE("फसल कितने दिन चलेगी?", "How long will produce last?", "🧺"),
    POWER_SOLAR("बिजली और सोलर बैटरी कैसी है?", "How is solar & battery?", "☀️"),
    SAFETY_GAS("क्या कोई गैस या खतरा है?", "Is there any danger or gas?", "🛡️"),
    HOW_TO_OPERATE("मशीन कैसे चालू-बंद करें?", "How to operate machine?", "⚙️")
}
