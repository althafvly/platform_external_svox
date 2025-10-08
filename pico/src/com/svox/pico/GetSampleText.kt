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
import android.speech.tts.TextToSpeech
import androidx.appcompat.app.AppCompatActivity

/**
 * Returns the sample text string for the requested language.
 */
class GetSampleText : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val returnData = Intent()
        val extras = intent.extras

        if (extras == null) {
            returnData.putExtra("sampleText", "")
            setResult(TextToSpeech.LANG_NOT_SUPPORTED, returnData)
            finish()
            return
        }

        val language = extras.getString("language")
        val country = extras.getString("country")

        val sampleText = when (language) {
            "eng" -> {
                if (country == "GBR") {
                    getString(R.string.eng_gbr_sample)
                } else {
                    getString(R.string.eng_usa_sample)
                }
            }
            "fra" -> getString(R.string.fra_fra_sample)
            "ita" -> getString(R.string.ita_ita_sample)
            "deu" -> getString(R.string.deu_deu_sample)
            "spa" -> getString(R.string.spa_esp_sample)
            else -> ""
        }

        val result = if (sampleText.isEmpty()) {
            TextToSpeech.LANG_NOT_SUPPORTED
        } else {
            TextToSpeech.LANG_AVAILABLE
        }

        returnData.putExtra("sampleText", sampleText)
        setResult(result, returnData)
        finish()
    }
}
