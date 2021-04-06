package com.codinginflow.inventarization.features.invt.shared

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.codinginflow.inventarization.data.Inventar
import com.codinginflow.inventarization.databinding.CardInventarBinding

class InventarListAdapter(
    private val onItemClick: (Inventar) -> Unit
) :
    ListAdapter<Inventar, InventarListViewHolder>(InventarComparator()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InventarListViewHolder {
        val binding =
            CardInventarBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return InventarListViewHolder(binding,
            onItemClick = { position ->
                val article = getItem(position)
                if (article != null) {
                    onItemClick(article)
                }
            }
        )
    }

    override fun onBindViewHolder(holder: InventarListViewHolder, position: Int) {
        val currentItem = getItem(position)
        if (currentItem != null) {
            holder.bind(currentItem)
        }
    }
}