/*
 * Copyright (C) 2011 The Android Open Source Project
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
import android.util.Log
import java.util.Locale

/**
 * The SpeechSynthesis class provides a high-level api to create and play
 * synthesized speech. This class is used internally to talk to a native
 * TTS library that implements the interface defined in
 * frameworks/base/include/tts/TtsEngine.h
 *
 */
class SynthProxy(nativeSoLib: String?, engineConfig: String?) {
    private var mJniData: Long = 0

    /**
     * Constructor; pass the location of the native TTS .so to use.
     */
    init {
        val applyFilter = shouldApplyAudioFilter(nativeSoLib)
        Log.v(TAG, "About to load $nativeSoLib, applyFilter=$applyFilter")
        mJniData = native_setup(nativeSoLib, engineConfig)
        if (mJniData == 0L) {
            throw RuntimeException("Failed to load $nativeSoLib")
        }
        native_setLowShelf(
            applyFilter, PICO_FILTER_GAIN, PICO_FILTER_LOWSHELF_ATTENUATION,
            PICO_FILTER_TRANSITION_FREQ, PICO_FILTER_SHELF_SLOPE
        )
    }

    // HACK: Apply audio filter if the engine is pico
    private fun shouldApplyAudioFilter(nativeSoLib: String?): Boolean {
        return nativeSoLib?.lowercase(Locale.getDefault())?.contains("pico") ?: false
    }

    /** Stops and clears the AudioTrack. */
    fun stop() {
        native_stop(mJniData)
    }

    /**
     * Synchronous stop of the synthesizer. This method returns when the synth
     * has completed the stop procedure and doesn't use any of the resources it
     * was using while synthesizing.
     */
    fun stopSync() {
        native_stopSync(mJniData)
    }

    fun speak(request: SynthesisRequest, callback: SynthesisCallback?): Int {
        // Use charSequenceText instead of text
        val text = request.charSequenceText.toString()
        return native_speak(mJniData, text, callback)
    }

    /**
     * Queries for language support.
     * Return codes are defined in android.speech.tts.TextToSpeech
     */
    fun isLanguageAvailable(language: String?, country: String?, variant: String?): Int {
        return native_isLanguageAvailable(mJniData, language, country, variant)
    }

    /** Updates the engine configuration. */
    fun setConfig(engineConfig: String?): Int {
        return native_setProperty(mJniData, "engineConfig", engineConfig)
    }

    /** Sets the language. */
    fun setLanguage(language: String?, country: String?, variant: String?): Int {
        return native_setLanguage(mJniData, language, country, variant)
    }

    /** Loads the language: it's not set, but prepared for use later. */
    fun loadLanguage(language: String?, country: String?, variant: String?): Int {
        return native_loadLanguage(mJniData, language, country, variant)
    }

    /** Sets the speech rate. */
    fun setSpeechRate(speechRate: Int): Int {
        return native_setProperty(mJniData, "rate", speechRate.toString())
    }

    /** Sets the pitch of the synthesized voice. */
    fun setPitch(pitch: Int): Int {
        return native_setProperty(mJniData, "pitch", pitch.toString())
    }

    /** Returns the currently set language, country and variant information. */
    val language: Array<String?>?
        get() = native_getLanguage(mJniData)

    /** Shuts down the native synthesizer. */
    fun shutdown() {
        native_shutdown(mJniData)
        mJniData = 0
    }

    protected fun finalize() {
        if (mJniData != 0L) {
            Log.w(TAG, "SynthProxy finalized without being shutdown")
            native_finalize(mJniData)
            mJniData = 0
        }
    }

    private external fun native_setup(nativeSoLib: String?, engineConfig: String?): Long
    private external fun native_setLowShelf(
        applyFilter: Boolean,
        filterGain: Float,
        attenuationInDb: Float,
        freqInHz: Float,
        slope: Float
    ): Int

    private external fun native_finalize(jniData: Long)
    private external fun native_stop(jniData: Long): Int
    private external fun native_stopSync(jniData: Long): Int
    private external fun native_speak(
        jniData: Long,
        text: String?,
        request: SynthesisCallback?
    ): Int

    private external fun native_isLanguageAvailable(
        jniData: Long,
        language: String?,
        country: String?,
        variant: String?
    ): Int

    private external fun native_setLanguage(
        jniData: Long,
        language: String?,
        country: String?,
        variant: String?
    ): Int

    private external fun native_loadLanguage(
        jniData: Long,
        language: String?,
        country: String?,
        variant: String?
    ): Int

    private external fun native_setProperty(jniData: Long, name: String?, value: String?): Int
    private external fun native_getLanguage(jniData: Long): Array<String?>?
    private external fun native_shutdown(jniData: Long)

    companion object {
        init {
            System.loadLibrary("jni_ttscompat")
        }

        private const val TAG = "SynthProxy"

        // Default parameters of a filter to be applied when using the Pico engine.
        private const val PICO_FILTER_GAIN = 5.0f // linear gain
        private const val PICO_FILTER_LOWSHELF_ATTENUATION = -18.0f // in dB
        private const val PICO_FILTER_TRANSITION_FREQ = 1100.0f // in Hz
        private const val PICO_FILTER_SHELF_SLOPE = 1.0f // Q
    }
}
