package com.eltrio.emotionalmusicplayer.my_classes

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.R.attr.data
import android.R.attr.onClick
import android.view.ViewGroup

abstract class MyAdapter<VH : RecyclerView.ViewHolder, I : Any>(
    protected val context: Context,
    private val data: ArrayList<I>,
    private val onClickListener: OnClickListener<I>? = null
) :
    RecyclerView.Adapter<VH>() {

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.itemView.setOnClickListener {
            onClickListener?.onClick(holder, position, data[position])
        }

        holder.itemView.setOnLongClickListener {
            onClickListener?.onLongClick(holder, position, data[position])
            true
        }

        for (i in 0 until (holder.itemView as ViewGroup).childCount) {
            (holder.itemView as ViewGroup).getChildAt(i).setOnClickListener {
                onClickListener?.onSpecialClick(holder, position, data[position])
            }
        }

        onBindViewHolderItem(holder, data[holder.adapterPosition])
    }

    protected open fun onBindViewHolderItem(holder: VH, item: I) {

    }

    override fun getItemCount(): Int {
        return data.size
    }

    fun updateData(data: ArrayList<I>) {
        this.data.clear()
        this.data.addAll(data)
        this.notifyDataSetChanged()
    }

    fun addItemFirst(item: I) {
        this.data.add(0, item)
        notifyItemInserted(0)
    }

    fun addItemFirstLast(item: I) {
        data.add(item)
        notifyItemInserted(data.indexOf(item))
    }

    fun add(position: Int, item: I) {
        data.add(position, item)
        notifyItemInserted(position)
    }

    fun remove(position: Int) {
        data.removeAt(position)
        notifyItemRemoved(position)
    }

    fun remove(item: I) {
        val position = data.indexOf(item)
        data.remove(item)
        notifyItemRemoved(position)
    }

    fun refresh(position: Int) {
        notifyItemChanged(position)
    }

    fun clear() {
        data.clear()
        notifyDataSetChanged()
    }

    interface OnClickListener<I> {
        fun onClick(holder: RecyclerView.ViewHolder, position: Int, item: I) {}

        fun onLongClick(holder: RecyclerView.ViewHolder, position: Int, item: I) {}

        fun onSpecialClick(holder: RecyclerView.ViewHolder, position: Int, item: I) {}
    }


}