package com.sscl.basesample.viewbinding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class SampleViewModel : ViewModel() {

    object SampleViewModelFactory : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return SampleViewModel() as T
        }
    }
}