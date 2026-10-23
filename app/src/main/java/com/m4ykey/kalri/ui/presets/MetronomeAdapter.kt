package com.m4ykey.kalri.ui.presets

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.m4ykey.kalri.R

class MetronomeAdapter(private val items : List<MetronomeItem>) : RecyclerView.Adapter<MetronomeViewHolder>() {

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
        val item = items[position]

        holder.txtBpm.text = item.bpm.toString()
        holder.txtName.text = item.name
    }

    override fun getItemCount(): Int {
        return items.size
    }
}