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

package com.svox.pico;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import java.util.ArrayList;
import java.util.Locale;

import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;

public class EngineSettings extends AppCompatActivity {
    private static final int VOICE_DATA_CHECK_CODE = 42;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the fragment
        getSupportFragmentManager()
                .beginTransaction()
                .replace(android.R.id.content, new EngineSettingsFragment())
                .commit();

        // Start the voice data check
        Intent i = new Intent(this, CheckVoiceData.class);
        startActivityForResult(i, VOICE_DATA_CHECK_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == VOICE_DATA_CHECK_CODE && data != null) {
            ArrayList<String> available = data.getStringArrayListExtra(TextToSpeech.Engine.EXTRA_AVAILABLE_VOICES);
            ArrayList<String> unavailable = data.getStringArrayListExtra(TextToSpeech.Engine.EXTRA_UNAVAILABLE_VOICES);

            EngineSettingsFragment fragment = (EngineSettingsFragment)
                    getSupportFragmentManager().findFragmentById(android.R.id.content);

            if (fragment != null) {
                fragment.updateVoiceList(available, R.string.installed);
                fragment.updateVoiceList(unavailable, R.string.not_installed);
            }
        }
    }

    public static class EngineSettingsFragment extends PreferenceFragmentCompat {

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.voices_list, rootKey);
        }

        @Override
        public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);

            int statusBarHeight = 0;
            int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
            if (resourceId > 0) {
                statusBarHeight = getResources().getDimensionPixelSize(resourceId);
            }

            if (getListView() != null) {
                getListView().setPadding(
                        getListView().getPaddingLeft(),
                        statusBarHeight + 16,
                        getListView().getPaddingRight(),
                        getListView().getPaddingBottom()
                );
            }
        }

        private void updateVoiceList(ArrayList<String> voices, int summaryRes) {
            if (voices == null) return;

            for (String voice : voices) {
                Log.e("debug", voice);
                String[] parts = voice.split("-");
                Locale loc = new Locale(parts[0], parts[1]);
                Preference pref = findPreference(voice);
                if (pref != null) {
                    pref.setTitle(loc.getDisplayLanguage() + " (" + loc.getDisplayCountry() + ")");
                    pref.setSummary(summaryRes);
                    pref.setIconSpaceReserved(false);
                    pref.setEnabled(false);
                }
            }
        }
    }
}
