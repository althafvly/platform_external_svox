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

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import java.util.Locale

class EngineSettings : AppCompatActivity() {

    private val voiceDataLauncher = registerForActivityResult(
        androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val data = result.data ?: return@registerForActivityResult

        val available = data.getStringArrayListExtra(TextToSpeech.Engine.EXTRA_AVAILABLE_VOICES)
        val unavailable = data.getStringArrayListExtra(TextToSpeech.Engine.EXTRA_UNAVAILABLE_VOICES)

        (supportFragmentManager.findFragmentById(android.R.id.content) as? EngineSettingsFragment)?.apply {
            updateVoiceList(available, R.string.installed)
            updateVoiceList(unavailable, R.string.not_installed)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Load the fragment
        supportFragmentManager
            .beginTransaction()
            .replace(android.R.id.content, EngineSettingsFragment())
            .commit()

        // Start the voice data check using modern API
        voiceDataLauncher.launch(Intent(this, CheckVoiceData::class.java))
    }

    class EngineSettingsFragment : PreferenceFragmentCompat() {

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.voices_list, rootKey)
        }

        @SuppressLint("DiscouragedApi", "InternalInsetResource")
        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)
            val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
            val statusBarHeight = if (resourceId > 0) resources.getDimensionPixelSize(resourceId) else 0
            listView?.setPadding(listView.paddingLeft, statusBarHeight + 16, listView.paddingRight, listView.paddingBottom)
        }

        fun updateVoiceList(voices: ArrayList<String>?, summaryRes: Int) {
            voices?.forEach { voice ->
                Log.d("EngineSettings", voice)
                val parts = voice.split("-")
                val loc = if (parts.size >= 2) Locale(parts[0], parts[1]) else Locale.getDefault()
                findPreference<Preference>(voice)?.apply {
                    title = "${loc.displayLanguage} (${loc.displayCountry})"
                    setSummary(summaryRes)
                    isIconSpaceReserved = false
                    isEnabled = false
                }
            }
        }
    }
}

