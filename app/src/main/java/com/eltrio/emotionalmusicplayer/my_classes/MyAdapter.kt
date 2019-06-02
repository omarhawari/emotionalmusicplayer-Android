package com.eltrio.emotionalmusicplayer.my_classes

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup

open class MyAdapter<VH : RecyclerView.ViewHolder, I: Any>(
    protected val context: Context,
    private val data: ArrayList<I>,
    private val layoutRes: Int
) :
    RecyclerView.Adapter<VH>() {

    override fun onBindViewHolder(holder: VH, position: Int) {
        onBindViewHolderItem(holder, data[holder.adapterPosition])
    }

    protected open fun onBindViewHolderItem(holder: VH, item: I) {

    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, p1: Int): VH {
        return MyViewHolder(LayoutInflater.from(context).inflate(layoutRes, viewGroup, false)) as VH
    }

    override fun getItemCount(): Int {
        return data.size
    }

}