/*
  Copyright (c) 2017-2019 CommonsWare, LLC

  Licensed under the Apache License, Version 2.0 (the "License"); you may not
  use this file except in compliance with the License. You may obtain	a copy
  of the License at http://www.apache.org/licenses/LICENSE-2.0. Unless required
  by applicable law or agreed to in writing, software distributed under the
  License is distributed on an "AS IS" BASIS,	WITHOUT	WARRANTIES OR CONDITIONS
  OF ANY KIND, either express or implied. See the License for the specific
  language governing permissions and limitations under the License.

  Covered in detail in the book _Elements of Android Jetpack_

  https://commonsware.com/Jetpack
*/

package com.commonsware.jetpack.work.download

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import okhttp3.OkHttpClient
import okhttp3.Request
import okio.buffer
import okio.sink
import java.io.File
import java.io.IOException

class DownloadWorker(context: Context, workerParams: WorkerParameters) :
        Worker(context, workerParams) {

    override fun doWork(): Result {
        val client = OkHttpClient()
        val request = Request.Builder()
                .url(inputData.getString(KEY_URL)!!)
                .build()

        try {
            client.newCall(request).execute().use { response ->
                val dir = applicationContext.cacheDir
                val downloadedFile = File(dir, inputData.getString(KEY_FILENAME)!!)
                val sink = downloadedFile.sink().buffer()

                response.body?.let { sink.writeAll(it.source()) }
                sink.close()
            }
        } catch (e: IOException) {
            Log.e(javaClass.simpleName, "Exception downloading file", e)

            return ListenableWorker.Result.failure()
        }

        return ListenableWorker.Result.success()
    }

    companion object {
        const val KEY_URL = "url"
        const val KEY_FILENAME = "filename"
    }
}
