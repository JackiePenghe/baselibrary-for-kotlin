package com.sscl.basesample.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class USBListenerActivityViewModel : ViewModel() {

    object USBListenerActivityViewModelFactory : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return USBListenerActivityViewModel() as T
        }
    }

    val uDiskPath = MutableLiveData<String?>()
}