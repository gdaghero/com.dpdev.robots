package com.dpdev.robots.screens.board

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.dpdev.core.model.GameConfiguration
import com.dpdev.core.model.Position
import com.dpdev.core.model.Round
import com.dpdev.robots.R
import com.dpdev.robots.model.UiCoordinate
import com.dpdev.robots.model.UiPosition
import com.dpdev.robots.model.UiRound
import com.dpdev.robots.shared.GridSpacingItemDecoration
import com.dpdev.robots.databinding.ViewBoardBinding as Binding

class BoardView(context: Context, attrs: AttributeSet) :
    FrameLayout(context, attrs) {

    private val binding: Binding = Binding.inflate(LayoutInflater.from(context), this, true)
    private val boardAdapter: BoardAdapter = BoardAdapter()

    private var isInitialized = false

    var round: UiRound? = null
        set(value) {
            field = value
            tryInitialize()
            updateRound()
        }

    private fun tryInitialize() {
        if (isInitialized) return

        val spanCount = round?.board?.rows ?: return
        with(binding.rvBoard) {
            itemAnimator = null
            adapter = boardAdapter
            layoutManager = GridLayoutManager(context, spanCount)
            addItemDecoration(
                GridSpacingItemDecoration(
                    spanCount = spanCount,
                    spacing = 16,
                    includeEdge = false
                )
            )
        }

        isInitialized = true
    }

    private fun updateRound() {
        boardAdapter.submitList(
            round?.board?.positions ?: emptyList()
        )
    }
}
