package com.patika.getir_lite.feature.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.patika.getir_lite.databinding.ListHeaderBinding

/**
 * A RecyclerView adapter for displaying a single header item in a list.
 * This adapter is configurable with a header text, height, and background color.
 *
 * @property header The text to be displayed in the header. If empty, the header will be hidden.
 * @property height The height of the header view. If set to 0, the default layout height is used.
 * @property color The background color of the header view. If set to 0, the default color is used.
 */
class HeaderAdapter(
    private val header: String = "",
    private val height: Int = 0,
    private val color: Int = 0,
) :
    RecyclerView.Adapter<HeaderAdapter.HeaderViewHolder>() {
    override fun getItemCount(): Int = 1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HeaderViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ListHeaderBinding.inflate(layoutInflater, parent, false)
        return HeaderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HeaderViewHolder, position: Int) {
        holder.bind()
    }

    inner class HeaderViewHolder(private val binding: ListHeaderBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind() = with(binding) {
            when {
                header.isNotEmpty() -> tvHeader.text = header
                else -> tvHeader.visibility = View.GONE
            }

            if (height > 0) {
                root.layoutParams.height = height
            }
            if (color != 0) {
                root.setBackgroundColor(color)
            }
        }
    }
}
