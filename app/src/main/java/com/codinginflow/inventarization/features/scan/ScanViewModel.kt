package com.codinginflow.inventarization.features.scan

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codinginflow.inventarization.data.Inventar
import com.codinginflow.inventarization.data.DataRepository
import com.google.zxing.integration.android.IntentResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ScanViewModel @Inject constructor(
    private val repository: DataRepository
) : ViewModel() {

    private val _inventList = MutableLiveData<List<Inventar>>().apply { emptyList<Inventar>() }
    val inventList: LiveData<List<Inventar>> = _inventList

    private val _currentInventar = MutableLiveData<Inventar>()
    val currentInventar: LiveData<Inventar> = _currentInventar

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error


    fun onActivityResult(data: IntentResult) {
        if (inventList.value.isNullOrEmpty())
            fillList()
        inventList.value?.forEach {
            if (it.myid == data.contents) {
                _currentInventar.value = it
                viewModelScope.launch {
                    repository.updateScanningInventar(1, it.myid!!)
                }
            }
            if (it.myid!!.length != data.contents.length) {
                _error.postValue("Не можу прочитати QR спробуй ще раз")
            }
        }
    }

    fun update(inventar: Inventar) {
        viewModelScope.launch {
            repository.updateScanningInventar(inventar)
        }
    }

    fun fillList() {
        viewModelScope.launch {
            repository.getAllInventarItems().collect {
                _inventList.postValue(it)
            }
        }
    }

    fun startNewInventarization() {
        viewModelScope.launch {
            repository.startNewInventarization()
        }
    }
}