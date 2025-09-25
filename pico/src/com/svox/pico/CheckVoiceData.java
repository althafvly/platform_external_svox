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

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.speech.tts.TextToSpeech;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Checks if the voice data for the SVOX Pico Engine is present on the
 * external storage or system directory.
 */
public class CheckVoiceData extends AppCompatActivity {

    // Deprecated, but still used for Pico legacy
    private final static String PICO_LINGWARE_PATH =
            Environment.getExternalStorageDirectory() + "/svox/";
    private final static String PICO_SYSTEM_LINGWARE_PATH = "/system_ext/tts/lang_pico/";

    private final static String[] dataFiles = {
            "de-DE_gl0_sg.bin", "de-DE_ta.bin", "en-GB_kh0_sg.bin", "en-GB_ta.bin",
            "en-US_lh0_sg.bin", "en-US_ta.bin", "es-ES_ta.bin", "es-ES_zl0_sg.bin",
            "fr-FR_nk0_sg.bin", "fr-FR_ta.bin", "it-IT_cm0_sg.bin", "it-IT_ta.bin"
    };

    private final static String[] dataFilesInfo = {
            "deu-DEU", "deu-DEU", "eng-GBR", "eng-GBR", "eng-USA", "eng-USA",
            "spa-ESP", "spa-ESP", "fra-FRA", "fra-FRA", "ita-ITA", "ita-ITA"
    };

    private final static String[] supportedLanguages = {
            "deu-DEU", "eng-GBR", "eng-USA", "spa-ESP", "fra-FRA", "ita-ITA"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int result = TextToSpeech.Engine.CHECK_VOICE_DATA_PASS;
        boolean foundMatch = false;

        ArrayList<String> available = new ArrayList<>();
        ArrayList<String> unavailable = new ArrayList<>();
        HashMap<String, Boolean> languageCountry = getStringBooleanHashMap();

        // Check for files
        for (int i = 0; i < supportedLanguages.length; i++) {
            if ((languageCountry.isEmpty()) || languageCountry.containsKey(supportedLanguages[i])) {
                if (fileNotExists(dataFiles[2 * i]) || fileNotExists(dataFiles[(2 * i) + 1])) {
                    result = TextToSpeech.Engine.CHECK_VOICE_DATA_MISSING_DATA;
                    unavailable.add(supportedLanguages[i]);
                } else {
                    available.add(supportedLanguages[i]);
                    foundMatch = true;
                }
            }
        }

        if (!languageCountry.isEmpty() && !foundMatch) {
            result = TextToSpeech.Engine.CHECK_VOICE_DATA_FAIL;
        }

        // Return data
        Intent returnData = new Intent();
        returnData.putExtra(TextToSpeech.Engine.EXTRA_VOICE_DATA_ROOT_DIRECTORY, PICO_LINGWARE_PATH);
        returnData.putExtra(TextToSpeech.Engine.EXTRA_VOICE_DATA_FILES, dataFiles);
        returnData.putExtra(TextToSpeech.Engine.EXTRA_VOICE_DATA_FILES_INFO, dataFilesInfo);
        returnData.putStringArrayListExtra(TextToSpeech.Engine.EXTRA_AVAILABLE_VOICES, available);
        returnData.putStringArrayListExtra(TextToSpeech.Engine.EXTRA_UNAVAILABLE_VOICES, unavailable);
        setResult(result, returnData);
        finish();
    }

    @NonNull
    private HashMap<String, Boolean> getStringBooleanHashMap() {
        HashMap<String, Boolean> languageCountry = new HashMap<>();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            ArrayList<String> langCountryVars = bundle.getStringArrayList(
                    TextToSpeech.Engine.EXTRA_CHECK_VOICE_DATA_FOR);
            if (langCountryVars != null) {
                for (String s : langCountryVars) {
                    if (!s.isEmpty()) {
                        languageCountry.put(s, true);
                    }
                }
            }
        }
        return languageCountry;
    }

    private boolean fileNotExists(String filename) {
        File tempFile = new File(PICO_LINGWARE_PATH + filename);
        File tempFileSys = new File(PICO_SYSTEM_LINGWARE_PATH + filename);
        return !tempFile.exists() && !tempFileSys.exists();
    }
}
