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

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.work.*
import androidx.work.testing.WorkManagerTestInitHelper
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

@RunWith(AndroidJUnit4::class)
class DownloadWorkerTest {
  private lateinit var expected: File
  private val context =
    InstrumentationRegistry.getInstrumentation().targetContext

  @Before
  fun setUp() {
    WorkManagerTestInitHelper.initializeTestWorkManager(context)

    expected = File(context.cacheDir, "oldbook.pdf")

    if (expected.exists()) {
      expected.delete()
    }
  }

  @Test
  fun download() {
    assertFalse(expected.exists())

    WorkManager.getInstance(context).enqueue(buildWorkRequest(null))

    assertTrue(expected.exists())
  }

  @Test
  fun downloadWithConstraints() {
    val constraints = Constraints.Builder()
      .setRequiredNetworkType(NetworkType.CONNECTED)
      .setRequiresBatteryNotLow(true)
      .build()
    val work = buildWorkRequest(constraints)

    assertFalse(expected.exists())

    WorkManager.getInstance(context).enqueue(work)
    WorkManagerTestInitHelper.getTestDriver(context)!!.setAllConstraintsMet(work.id)

    assertTrue(expected.exists())
  }

  private fun buildWorkRequest(constraints: Constraints?): WorkRequest {
    val builder = OneTimeWorkRequest.Builder(DownloadWorker::class.java)
      .setInputData(
        Data.Builder()
          .putString(
            DownloadWorker.KEY_URL,
            "https://commonsware.com/Android/Android-1_0-CC.pdf"
          )
          .putString(DownloadWorker.KEY_FILENAME, "oldbook.pdf")
          .build()
      )
      .addTag("download")

    if (constraints != null) {
      builder.setConstraints(constraints)
    }

    return builder.build()
  }
}
