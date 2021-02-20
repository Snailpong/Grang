package com.pong.grang.model

import android.speech.tts.TextToSpeech

data class TTSModel(
    var textToSpeech : TextToSpeech,
    var ttsState : Int
) {
}