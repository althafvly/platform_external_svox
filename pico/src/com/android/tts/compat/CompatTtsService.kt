/*
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.android.tts.compat

import android.speech.tts.SynthesisCallback
import android.speech.tts.SynthesisRequest
import android.speech.tts.TextToSpeech
import android.speech.tts.TextToSpeechService
import android.util.Log
import androidx.core.net.toUri

abstract class CompatTtsService : TextToSpeechService() {
    private var mNativeSynth: SynthProxy? = null

    protected abstract fun getSoFilename(): String?

    override fun onCreate() {
        if (DBG) Log.d(TAG, "onCreate()")

        val soFilename = getSoFilename()

        // Shutdown existing synth if present
        mNativeSynth?.apply {
            stopSync()
            shutdown()
        }

        // Load engineConfig from the SettingsProvider content provider
        var engineConfig: String? = ""
        contentResolver.query(
            "content://$packageName.providers.SettingsProvider".toUri(),
            null, null, null, null
        )?.use { cursor ->
            if (cursor.moveToFirst()) {
                engineConfig = cursor.getString(0)
            }
        }

        mNativeSynth = SynthProxy(soFilename, engineConfig)

        // mNativeSynth is used by TextToSpeechService#onCreate, so must be set first
        super.onCreate()
    }

    override fun onDestroy() {
        if (DBG) Log.d(TAG, "onDestroy()")
        super.onDestroy()
        mNativeSynth?.shutdown()
        mNativeSynth = null
    }

    override fun onGetLanguage(): Array<String?>? {
        return mNativeSynth?.language
    }

    override fun onIsLanguageAvailable(lang: String?, country: String?, variant: String?): Int {
        if (DBG) Log.d(TAG, "onIsLanguageAvailable($lang,$country,$variant)")
        return mNativeSynth?.isLanguageAvailable(lang, country, variant)
            ?: TextToSpeech.ERROR
    }

    override fun onLoadLanguage(lang: String?, country: String?, variant: String?): Int {
        if (DBG) Log.d(TAG, "onLoadLanguage($lang,$country,$variant)")
        val result = onIsLanguageAvailable(lang, country, variant)
        if (result >= TextToSpeech.LANG_AVAILABLE) {
            mNativeSynth?.setLanguage(lang, country, variant)
        }
        return result
    }

    override fun onSynthesizeText(request: SynthesisRequest, callback: SynthesisCallback) {
        val synth = mNativeSynth
        if (synth == null) {
            callback.error()
            return
        }

        // Set language
        if (synth.setLanguage(
                request.language,
                request.country,
                request.variant
            ) != TextToSpeech.SUCCESS
        ) {
            Log.e(
                TAG,
                "setLanguage(${request.language},${request.country},${request.variant}) failed"
            )
            callback.error()
            return
        }

        // Set speech rate
        if (synth.setSpeechRate(request.speechRate) != TextToSpeech.SUCCESS) {
            Log.e(TAG, "setSpeechRate(${request.speechRate}) failed")
            callback.error()
            return
        }

        // Set pitch
        if (synth.setPitch(request.pitch) != TextToSpeech.SUCCESS) {
            Log.e(TAG, "setPitch(${request.pitch}) failed")
            callback.error()
            return
        }

        // Synthesize
        if (synth.speak(request, callback) != TextToSpeech.SUCCESS) {
            callback.error()
        }
    }

    override fun onStop() {
        if (DBG) Log.d(TAG, "onStop()")
        mNativeSynth?.stop()
    }

    companion object {
        private const val DBG = false
        private const val TAG = "CompatTtsService"

        init {
            System.loadLibrary("jni_ttspico")
        }
    }
}
