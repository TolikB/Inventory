package com.codinginflow.inventarization.data

import android.content.Context
import android.text.TextUtils
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.codinginflow.inventarization.di.AppModule
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.util.ArrayList

class AssetsFileWorker(
    context: Context,
    workerParams: WorkerParameters
) :
    CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val list = readFile(getFileFromAssets(applicationContext, "invent.xlsx"))
        Log.e("doWork", "doWorkdoWorkdoWork  ${list.size}")
        val database = AppModule.provideDatabase(applicationContext)
        database.newsArticleDao().insert(list)
        return Result.success()
    }

    @Throws(IOException::class)
    fun getFileFromAssets(context: Context, fileName: String): File =
        File(context.cacheDir, fileName)
            .also {
                if (!it.exists()) {
                    it.outputStream().use { cache ->
                        context.assets.open(fileName).use { inputStream ->
                            inputStream.copyTo(cache)
                        }
                    }
                }
            }

    @Throws(IOException::class)
    fun readFile(path: File): ArrayList<Inventar> {
//        val excelFile = File(path)
        val fis = FileInputStream(path)

        // we create an XSSF Workbook object for our XLSX Excel File
        val workbook = XSSFWorkbook(fis)
        // we get first sheet
        val sheet = workbook.getSheetAt(0)
        // we iterate on rows
        val rowIt: Iterator<Row> = sheet.iterator()
        val list = ArrayList<Inventar>()
        while (rowIt.hasNext()) {
            val row = rowIt.next()
            // iterate on cells for the current row
            val cellIterator = row.cellIterator()
            Log.e("@@@@@@@@@@@@@@@@@@@", "  !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!")
            var x = 0
            val inventar = Inventar(0, null, null, null, null, null, null, null, null)
            while (cellIterator.hasNext()) {
                val cell = cellIterator.next()
                if (x == 5) {
                    x = 0
                    inventar.deportament = cell.toString()
                    Log.e("@@@@5555555555@@@@@", "  $cell")
                    if (!inventar.myid.isNullOrEmpty())
                        list.add(inventar)
                }
                if (x == 4) {
                    x++
                    inventar.otdel = cell.toString()
                    Log.e("@@@4444444444444@@@@@@", "  $cell")
                }
                if (x == 3) {
                    x++
                    inventar.discription = cell.toString()
                    Log.e("@@@@3333333333333@@", "  $cell")
                }
                if (x == 2) {
                    x++
                    inventar.userName = cell.toString()
                    Log.e("@@@@@22222222222@@@@@@", "  $cell")
                }
                if (x == 1) {
                    x++
                    inventar.color = 0
                    inventar.itemName = cell.toString()
                    Log.e("@@@@@111111111@@@@@@@", "  $cell")
                }
                if (x == 0) {
                    Log.e("@@@@@0000000000@@@@@@@", "  $cell")
                    if (cell.toString().isDigitsOnly())
                        inventar.myid = cell.toString()
                    x++
                }
            }
            Log.e("$$$$$$$$$$$$$$$$$", "  $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$")
        }
        Log.e("####################", "  ################################")
        fis.close()
        return list
    }

}

inline fun CharSequence.isDigitsOnly() = TextUtils.isDigitsOnly(this)
