/*
 * Copyright (C) 2010 The Android Open Source Project
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
package com.svox.pico.providers

import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.database.MatrixCursor
import android.net.Uri
import android.os.Environment

/*
 * This content provider enables the TtsService to get a String of configuration
 * data from the plugin engine and pass it back to that engine's .so file in the
 * native layer.
 *
 * In this particular case, the only configuration information being passed is
 * the location of the data files for the Pico engine which live under
 *     /<external storage>/svox/
 *
 */
class SettingsProvider : ContentProvider() {

    private class SettingsCursor(columns: Array<String>) : MatrixCursor(columns) {
        var settings: String? = null

        fun putSettings(value: String) {
            settings = value
        }

        override fun getCount() = 1

        override fun getString(column: Int) = settings
    }

    override fun onCreate() = true

    override fun query(
        uri: Uri,
        projection: Array<String>?,
        selection: String?,
        selectionArgs: Array<String>?,
        sortOrder: String?
    ): Cursor {
        val cursor = SettingsCursor(arrayOf("key", "value"))
        cursor.putSettings("${Environment.getExternalStorageDirectory()}/svox/")
        return cursor
    }

    // The following operations are not supported
    override fun insert(uri: Uri, values: ContentValues?) = null
    override fun update(uri: Uri, values: ContentValues?, selection: String?, selectionArgs: Array<String>?) = 0
    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?) = 0
    override fun getType(uri: Uri) = null
}
