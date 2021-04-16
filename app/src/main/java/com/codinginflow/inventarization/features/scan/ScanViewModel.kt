package com.codinginflow.inventarization.features.scan

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.*
import com.codinginflow.inventarization.data.DataRepository
import com.codinginflow.inventarization.data.Inventar
import com.codinginflow.inventarization.data.LoadNewFileWorker
import com.google.zxing.integration.android.IntentResult
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.io.File
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


    fun onActivityResult(data: IntentResult): Boolean {
        if (inventList.value.isNullOrEmpty())
            fillList()
        inventList.value?.forEach {
            if (it.myid == data.contents) {
                _currentInventar.value = it
                viewModelScope.launch {
                    repository.updateScanningInventar(1, it.myid!!)
                }
                return@forEach
            }
            if (it.myid!!.length != data.contents.length) {
                _error.postValue("Не можу прочитати QR спробуй ще раз")
                return@forEach
            }
        }
        if (data.contents.length == 16) {
            _currentInventar.value = Inventar(
                (1000..1000000).random(), data.contents,
                "", "", "", "",
                "", "", 1
            )
        }
        return false
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

    fun loadNewFile(pathFile: String, appContext: Context) {
        viewModelScope.launch {
            val newFileWorker = OneTimeWorkRequest.Builder(LoadNewFileWorker::class.java)
            val data = Data.Builder()
            data.putString("file_path", pathFile)
            newFileWorker.setInputData(data.build())
            WorkManager.getInstance(appContext).enqueue(newFileWorker.build())
        }
    }
}