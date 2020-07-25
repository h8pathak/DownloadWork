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

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.BindingAdapter
import androidx.lifecycle.observe
import androidx.work.WorkInfo
import com.commonsware.jetpack.work.download.databinding.ActivityMainBinding

@BindingAdapter("android:enabled")
fun View.setEnabled(info: WorkInfo?) {
    isEnabled = info?.state?.isFinished ?: true
}

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val vm: DownloadViewModel by viewModels()
        val binding = ActivityMainBinding.inflate(layoutInflater)

        binding.viewModel = vm
        binding.lifecycleOwner = this

        setContentView(binding.root)

        vm.liveWorkStatus.observe(this) { workStatus ->
            if (workStatus != null && workStatus.state.isFinished) {
                Toast.makeText(this, R.string.msg_done, Toast.LENGTH_LONG).show()
            }
        }
    }
}
