package com.example.android.bellmanford.start

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class StartViewModel : ViewModel() {

    private val _eventInfoNavigate = MutableLiveData<Boolean>()
    val eventInfoNavigate: LiveData<Boolean>
        get() = _eventInfoNavigate

    private val _eventDevelopersNavigate = MutableLiveData<Boolean>()
    val eventDevelopersNavigate: LiveData<Boolean>
        get() = _eventDevelopersNavigate

    private val _eventAlgorithmNavigate = MutableLiveData<Boolean>()
    val eventAlgorithmNavigate: LiveData<Boolean>
        get() = _eventAlgorithmNavigate


    fun onAlgorithmNavigate() {
        _eventAlgorithmNavigate.value = true
    }

    fun onAlgorithmNavigateFinish() {
        _eventAlgorithmNavigate.value = false
    }

    fun onInfoNavigate() {
        _eventInfoNavigate.value = true
    }

    fun onInfoNavigateFinish() {
        _eventInfoNavigate.value = false
    }

    fun onDevelopersNavigate() {
        _eventDevelopersNavigate.value = true
    }

    fun onDevelopersNavigateFinish() {
        _eventDevelopersNavigate.value = false
    }

}