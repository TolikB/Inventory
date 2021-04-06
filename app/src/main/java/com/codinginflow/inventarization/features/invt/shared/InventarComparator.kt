package com.codinginflow.inventarization.features.invt.shared

import androidx.recyclerview.widget.DiffUtil
import com.codinginflow.inventarization.data.Inventar

class InventarComparator : DiffUtil.ItemCallback<Inventar>() {

    override fun areItemsTheSame(oldItem: Inventar, newItem: Inventar) =
        oldItem.myid == newItem.myid

    override fun areContentsTheSame(oldItem: Inventar, newItem: Inventar) =
        oldItem == newItem
}