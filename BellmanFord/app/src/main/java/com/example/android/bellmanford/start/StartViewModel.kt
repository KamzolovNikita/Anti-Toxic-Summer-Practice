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