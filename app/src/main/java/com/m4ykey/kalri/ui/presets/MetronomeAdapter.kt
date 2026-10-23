package com.m4ykey.kalri.ui.presets

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.m4ykey.kalri.R
import com.m4ykey.kalri.data.local.MetronomeEntity

class MetronomeAdapter(
    private val onItemClick : (MetronomeEntity) -> Unit
) : ListAdapter<MetronomeEntity, MetronomeViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MetronomeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.metronome_list_item, parent, false)
        return MetronomeViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: MetronomeViewHolder,
        position: Int
    ) {
        val item = getItem(position)

        holder.txtBpm.text = "${item.bpm} BPM"
        holder.txtName.text = item.name

        holder.itemView.setOnClickListener {
            onItemClick(item)
        }
    }

    private companion object {
        val DiffCallback = object : DiffUtil.ItemCallback<MetronomeEntity>() {
            override fun areItemsTheSame(
                oldItem: MetronomeEntity,
                newItem: MetronomeEntity
            ): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: MetronomeEntity,
                newItem: MetronomeEntity
            ): Boolean {
                return oldItem == newItem
            }

        }
    }
}