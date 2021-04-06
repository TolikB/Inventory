package com.codinginflow.inventarization.data

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DataRepository @Inject constructor(
    private val inventarDb: InventarDatabase
) {
    private val inventarDao = inventarDb.newsArticleDao()

    suspend fun updateScanningInventar(col: Int, id: String) =
        inventarDao.updateInventar(col, id)

    suspend fun updateScanningInventar(inv: Inventar) =
        inventarDao.updateInventar(1, inv.myid!!, inv.userName!!, inv.notes!!)

    fun getAllInventarItems(): Flow<List<Inventar>> =
        inventarDao.getAll()

    fun getScanningInventar(): Flow<List<Inventar>> =
        inventarDao.getScanningInventar()

    suspend fun startNewInventarization() =
        inventarDao.startNew(0)

}