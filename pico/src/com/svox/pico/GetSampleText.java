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
import android.speech.tts.TextToSpeech;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Returns the sample text string for the requested language.
 */
public class GetSampleText extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int result = TextToSpeech.LANG_AVAILABLE;
        Intent returnData = new Intent();

        Intent i = getIntent();
        Bundle extras = i.getExtras();
        if (extras == null) {
            result = TextToSpeech.LANG_NOT_SUPPORTED;
            returnData.putExtra("sampleText", "");
            setResult(result, returnData);
            finish();
            return;
        }

        String language = extras.getString("language", "");
        String country = extras.getString("country", "");
        // String variant = extras.getString("variant", "");

        switch (language) {
            case "eng":
                if ("GBR".equals(country)) {
                    returnData.putExtra("sampleText", getString(R.string.eng_gbr_sample));
                } else {
                    returnData.putExtra("sampleText", getString(R.string.eng_usa_sample));
                }
                break;
            case "fra":
                returnData.putExtra("sampleText", getString(R.string.fra_fra_sample));
                break;
            case "ita":
                returnData.putExtra("sampleText", getString(R.string.ita_ita_sample));
                break;
            case "deu":
                returnData.putExtra("sampleText", getString(R.string.deu_deu_sample));
                break;
            case "spa":
                returnData.putExtra("sampleText", getString(R.string.spa_esp_sample));
                break;
            default:
                result = TextToSpeech.LANG_NOT_SUPPORTED;
                returnData.putExtra("sampleText", "");
                break;
        }

        setResult(result, returnData);
        finish();
    }
}
