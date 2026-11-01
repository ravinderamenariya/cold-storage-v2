# Implementation Plan - Gemini Voice Assistant & Full Agricultural Advisor

Upgrade the Gemini AI Assistant in VisionX Smart Cold Storage to support direct voice speaking with resilient audio/text fallback, and transform it from a basic chamber telemetry echo into a full agricultural, crop health, mandi price, and ROI advisory engine.

## User Review Required

> [!IMPORTANT]
> **Gemini Scope & Voice Capability**:
> 1. Gemini will now answer **any** agricultural question (crop diseases, pest control, fertilizer, weather impacts, harvesting, Savina/Mewar mandi rates, selling window ROI, and cold storage conditions) rather than restricting answers to chamber temperature readouts.
> 2. Voice input will feature dual-mode handling: native `SpeechRecognizer` with automatic fallback to quick voice recording dialog / instant voice query chips if browser streaming speech recognition lacks audio permissions or Play Services.
> 3. Dashboard speaker buttons will give comprehensive conversational agronomic advice powered by Gemini rather than rigid hardcoded temperature phrases.

---

## Proposed Changes

### 1. Gemini AI Service Upgrade (`GeminiAiService.kt`)
- **System Prompt Expansion**: Configure Gemini 3.8 Flash as a **Full Agricultural & Market Advisor** for Indian farmers (specifically Mewar & Rajasthan). Give it deep domain expertise on:
  - Produce shelf life and cold storage management.
  - Crop agronomy (Tomato, Garlic, Onion, Chilli, Potato, Flowers, etc.), soil health, pest control, and post-harvest care.
  - Savina & Rajasthan APMC mandi price trends, arbitrage, and optimal selling windows.
  - Return on Investment (ROI) and What-If financial analysis.
- **Intelligent Knowledge-Based Fallback**: Replace the static temperature echo in `getFallbackAnswer` with an intelligent offline agronomic knowledge base covering crop diseases, price forecasts, shelf life, and selling recommendations so farmers receive insightful answers even if offline.
- **Direct Voice Response Optimization**: Format all answers cleanly for Text-To-Speech (TTS) with natural Hindi and Indian English agricultural vocabulary, removing markdown asterisks and symbols.

### 2. Voice Assistant & Input Engine (`VoiceAssistantScreen.kt`, `VoiceAssistantManager.kt`)
- **Direct Voice & Text Fallback Pipeline**:
  - Implement a dedicated Voice Recording / Speech Input modal that works seamlessly even when Google Speech Services are unavailable in the browser emulator.
  - Add instant spoken topic chips (e.g. "लहसुन के भाव कब बढ़ेंगे?", "टमाटर में सड़न से कैसे बचाएं?", "5 दिन स्टोरेज में रखने पर क्या मुनाफा होगा?", "सविना मंडी आज का ताजा भाव").
  - Auto-trigger Text-To-Speech immediately when Gemini returns a response, speaking the full answer aloud with clear audio progress and stop controls.
- **Connect Dashboard Audio to Gemini**:
  - Update `onSpeakStatus` in `DashboardScreen.kt` and `FarmerDashboardCard.kt` to route through Gemini AI for an intelligent spoken summary rather than a static temperature string.

### 3. ViewModel Integration (`AppViewModel.kt`)
- Wire full query dispatching to Gemini AI with immediate TTS playback.
- Maintain chat history with persistent streaming states and thinking indicators.
- Provide a `speakGeminiAdvice()` function that triggers an open AI spoken summary for the dashboard.

---

## Verification Plan

### Automated Tests
- Run `gradle :app:testDebugUnitTest` to verify unit tests pass.
- Run `compile_applet` to ensure error-free compilation.

### Manual / User Verification
1. **Voice Assistant Screen**:
   - Open "बोलें साथी" (Voice AI tab).
   - Tap the microphone button or type any custom agricultural question (e.g., "लहसुन कब बेचना चाहिए?").
   - Confirm Gemini answers with actionable market advice and speaks it aloud via Text-To-Speech.
2. **What-If Scenarios**:
   - Tap the quick scenario chip: "सविना मंडी में 5 दिन बाद बेचें तो क्या return मिलेगा?".
   - Verify Gemini analyzes projected price differences, respiration rates, and financial ROI.
3. **Dashboard Speaker Button**:
   - Tap the speaker icon on the Dashboard header and ensure it delivers an intelligent status overview.
