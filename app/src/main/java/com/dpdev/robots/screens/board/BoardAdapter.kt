package com.dpdev.robots.screens.board

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.dpdev.robots.R
import com.dpdev.robots.model.UiPosition
import com.dpdev.robots.screens.board.BoardAdapter.ViewHolder
import com.dpdev.robots.databinding.ViewItemBoardBinding as Binding

class BoardAdapter : ListAdapter<UiPosition, ViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = Binding.inflate(layoutInflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(private val binding: Binding) : RecyclerView.ViewHolder(binding.root) {

        private val positionDrawable by lazy {
            ContextCompat.getDrawable(itemView.context, R.drawable.bg_board_position)!!
        }

        fun bind(position: UiPosition) {
            when (position) {
                is UiPosition.Empty -> {
                    binding.tvEmoji.text = null
                    val tint = ContextCompat.getColor(itemView.context, position.colorRes)
                    positionDrawable.alpha = 255
                    positionDrawable.setTint(tint)
                }

                is UiPosition.Price -> {
                    val tint = ContextCompat.getColor(itemView.context, position.colorRes)
                    positionDrawable.alpha = 255
                    positionDrawable.setTint(tint)
                    binding.tvEmoji.text = itemView.context.getString(R.string.emoji_trophy)
                }

                is UiPosition.Taken -> {
                    val player = position.player
                    positionDrawable.setTint(Color.parseColor(player.colorHex))
                    positionDrawable.alpha = if (position.highlight) 200 else 55
                    binding.tvEmoji.text = player.emoji
                }
            }

            binding.ivPosition.setImageDrawable(positionDrawable)
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<UiPosition>() {

            override fun areItemsTheSame(oldItem: UiPosition, newItem: UiPosition) =
                oldItem.coordinate == newItem.coordinate

            override fun areContentsTheSame(oldItem: UiPosition, newItem: UiPosition) =
                oldItem == newItem
        }
    }
}
