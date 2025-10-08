/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.svox.pico

import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.speech.tts.TextToSpeech
import androidx.appcompat.app.AppCompatActivity
import java.io.File

/**
 * Checks if the voice data for the SVOX Pico Engine is present on the
 * external storage or system directory.
 */
class CheckVoiceData : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val available = ArrayList<String>()
        val unavailable = ArrayList<String>()
        val requestedLanguages = intent.getStringArrayListExtra(TextToSpeech.Engine.EXTRA_CHECK_VOICE_DATA_FOR) ?: arrayListOf()
        var foundMatch = false
        var result = TextToSpeech.Engine.CHECK_VOICE_DATA_PASS

        for (i in supportedLanguages.indices) {
            val lang = supportedLanguages[i]
            if (requestedLanguages.isEmpty() || requestedLanguages.contains(lang)) {
                if (fileNotExists(dataFiles[2 * i]) || fileNotExists(dataFiles[2 * i + 1])) {
                    unavailable.add(lang)
                    result = TextToSpeech.Engine.CHECK_VOICE_DATA_MISSING_DATA
                } else {
                    available.add(lang)
                    foundMatch = true
                }
            }
        }

        if (requestedLanguages.isNotEmpty() && !foundMatch) {
            result = TextToSpeech.Engine.CHECK_VOICE_DATA_FAIL
        }

        // Return data
        val returnData = Intent().apply {
            putExtra(TextToSpeech.Engine.EXTRA_VOICE_DATA_ROOT_DIRECTORY, PICO_LINGWARE_PATH)
            putExtra(TextToSpeech.Engine.EXTRA_VOICE_DATA_FILES, dataFiles)
            putExtra(TextToSpeech.Engine.EXTRA_VOICE_DATA_FILES_INFO, dataFilesInfo)
            putStringArrayListExtra(TextToSpeech.Engine.EXTRA_AVAILABLE_VOICES, available)
            putStringArrayListExtra(TextToSpeech.Engine.EXTRA_UNAVAILABLE_VOICES, unavailable)
        }

        setResult(result, returnData)
        finish()
    }

    private fun fileNotExists(filename: String?) = filename?.let {
        !File(PICO_LINGWARE_PATH, it).exists() && !File(PICO_SYSTEM_LINGWARE_PATH, it).exists()
    } ?: true

    companion object {
        private val PICO_LINGWARE_PATH = Environment.getExternalStorageDirectory().toString() + "/svox/"
        private const val PICO_SYSTEM_LINGWARE_PATH = "/system_ext/tts/lang_pico/"

        private val dataFiles = arrayOf(
            "de-DE_gl0_sg.bin", "de-DE_ta.bin",
            "en-GB_kh0_sg.bin", "en-GB_ta.bin",
            "en-US_lh0_sg.bin", "en-US_ta.bin",
            "es-ES_ta.bin", "es-ES_zl0_sg.bin",
            "fr-FR_nk0_sg.bin", "fr-FR_ta.bin",
            "it-IT_cm0_sg.bin", "it-IT_ta.bin"
        )

        private val dataFilesInfo = arrayOf(
            "deu-DEU", "deu-DEU",
            "eng-GBR", "eng-GBR",
            "eng-USA", "eng-USA",
            "spa-ESP", "spa-ESP",
            "fra-FRA", "fra-FRA",
            "ita-ITA", "ita-ITA"
        )

        private val supportedLanguages = arrayOf(
            "deu-DEU", "eng-GBR", "eng-USA", "spa-ESP", "fra-FRA", "ita-ITA"
        )
    }
}
