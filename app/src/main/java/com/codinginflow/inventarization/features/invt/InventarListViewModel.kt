package com.codinginflow.inventarization.features.invt

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codinginflow.inventarization.data.Inventar
import com.codinginflow.inventarization.data.DataRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class InventarListViewModel @Inject constructor(
    private val repository: DataRepository
) : ViewModel() {

    private val _path = MutableLiveData<String>()
    val path: LiveData<String> = _path

    fun sendResultToMail() {
        viewModelScope.launch {
            repository.getScanningInventar().stateIn(viewModelScope, SharingStarted.Lazily, null)
                .collect {
                    val filePath = createExcel(it)
                    filePath?.let { path ->
                        _path.postValue(path)
                    }
                }
        }
    }

    private fun createExcel(employeeDtoList: List<Inventar>?): String? {

        try {
            /* Create excel workbook. */
            val excelWookBook: Workbook = XSSFWorkbook()
            /* */
            val createHelper = excelWookBook.creationHelper
            /* Create employee info sheet. */
            val employeeSheet = excelWookBook.createSheet("Файл инвентаризации")
            /* Create employee info row. Row number start with 0.
                 * The input parameter for method createRow(int rowNumber) is the row number that will be created.
                 * */
            /* First create header row. */
            val headerRow = employeeSheet.createRow(0)
            headerRow.createCell(0).setCellValue("Инвентарный номер")
            headerRow.createCell(1).setCellValue("Название основного средства")
            headerRow.createCell(2).setCellValue("ФИО")
            headerRow.createCell(3).setCellValue("Відділ")
            headerRow.createCell(4).setCellValue("Департамент")
            headerRow.createCell(5).setCellValue("В наличии")
            headerRow.createCell(6).setCellValue("Примечание")


            /* Loop for the employee dto list, add each employee data info into one row. */if (employeeDtoList != null) {
                for (i in employeeDtoList.indices) {
                    val eDto: Inventar = employeeDtoList[i]
                    /* Create row to save employee info. */
                    val row = employeeSheet.createRow(i + 1)
                    row.createCell(0).setCellValue(eDto.myid)
                    row.createCell(1).setCellValue(eDto.itemName)
                    row.createCell(2).setCellValue(eDto.userName)
                    row.createCell(3).setCellValue(eDto.otdel)
                    row.createCell(4).setCellValue(eDto.deportament)
                    row.createCell(5).setCellValue(eDto.color.toString())
                    row.createCell(6).setCellValue(eDto.notes)
                }
            }
            val sdf = SimpleDateFormat("yyyy_MM_dd_HH_mm")
            val date = sdf.format(Date())
            val path = "/storage/emulated/0/Android/data/com.codinginflow.inventarization/files/exel$date.xlsx"
            /* Write to excel file */
            val fOut = FileOutputStream(path)
            excelWookBook.write(fOut)
            fOut.close()
            return path
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return null
    }

    var pendingScrollToTopAfterRefresh = false

    val invtList =
        repository.getScanningInventar().stateIn(viewModelScope, SharingStarted.Lazily, null)


}