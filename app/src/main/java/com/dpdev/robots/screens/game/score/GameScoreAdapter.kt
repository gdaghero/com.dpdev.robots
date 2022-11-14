package com.dpdev.robots.screens.game.score

import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.dpdev.robots.R
import com.dpdev.robots.databinding.ItemGameScoreBinding
import com.dpdev.robots.model.UiScore
import com.dpdev.robots.screens.game.score.GameScoreAdapter.ViewHolder

class GameScoreAdapter : ListAdapter<UiScore, ViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemGameScoreBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(private val binding: ItemGameScoreBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: UiScore) {
            val player = item.point.player
            with(binding.tvPlayer) {
                text = itemView.context.getString(
                    R.string.item_game_score_format,
                    player.name,
                    item.point.points.toString()
                )
                setTypeface(null, if (!item.isPlaying) Typeface.NORMAL else Typeface.BOLD)
            }

            binding.tvEmoji.text = player.emoji
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<UiScore>() {

            override fun areItemsTheSame(oldItem: UiScore, newItem: UiScore): Boolean =
                oldItem.point.player.name == newItem.point.player.name &&
                    oldItem.isPlaying == newItem.isPlaying

            override fun areContentsTheSame(oldItem: UiScore, newItem: UiScore): Boolean =
                oldItem == newItem
        }
    }
}
