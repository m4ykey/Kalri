package com.m4ykey.kalri.ui.presets

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.m4ykey.kalri.R

class MetronomeViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
    val txtBpm : TextView = itemView.findViewById(R.id.txtBpm)
    val txtName : TextView = itemView.findViewById(R.id.txtName)
}