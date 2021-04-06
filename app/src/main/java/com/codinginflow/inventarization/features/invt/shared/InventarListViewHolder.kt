package com.codinginflow.inventarization.features.invt.shared

import androidx.recyclerview.widget.RecyclerView
import com.codinginflow.inventarization.data.Inventar
import com.codinginflow.inventarization.databinding.CardInventarBinding

class InventarListViewHolder(
    private val binding: CardInventarBinding,
    private val onItemClick: (Int) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(i: Inventar) {
        binding.apply {
            otdelId.text = i.otdel
            userNameId.text = i.userName
            nameId.text = i.itemName
            inventarId.text = i.myid
            departmantId.text = i.deportament
            notes.text = i.notes
        }
    }

    init {
        binding.apply {
            root.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClick(position)
                }
            }
        }
    }
}