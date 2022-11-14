package com.dpdev.robots.screens.game

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.dpdev.robots.R
import com.dpdev.robots.databinding.FragmentGameBinding
import com.dpdev.robots.model.UiGame
import com.dpdev.robots.screens.game.GameUiContract.Action
import com.dpdev.robots.screens.game.GameUiContract.State
import com.dpdev.robots.screens.game.score.GameScoreAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class GameFragment : Fragment(R.layout.fragment_game) {

    private val viewModel by viewModels<GameViewModel>()

    private lateinit var binding: FragmentGameBinding

    private val scoreAdapter = GameScoreAdapter()

    private val iconTint by lazy {
        ContextCompat.getColor(requireContext(), R.color.white)
    }

    private val playDrawable by lazy {
        ContextCompat.getDrawable(
            requireContext(), R.drawable.ic_baseline_play_arrow_24
        )!!.apply { setTint(iconTint) }
    }

    private val stopDrawable by lazy {
        ContextCompat.getDrawable(
            requireContext(), R.drawable.ic_baseline_stop_24
        )!!.apply { setTint(iconTint) }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentGameBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect(::render)
            }
        }

        setUpUI()
    }

    private fun setUpUI() {
        with(binding.tToolbar) {
            setupWithNavController(findNavController())
            setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.menu_settings -> {
                        navigateToSettings()
                        true
                    }
                    else -> false
                }
            }
        }
        binding.mbPlay.setOnClickListener {
            viewModel.sendAction(Action.ToggleGame)
        }
        with(binding.rvScore) {
            itemAnimator = null
            adapter = scoreAdapter
        }
    }

    private fun render(state: State) {
        scoreAdapter.submitList(state.score)

        with(binding) {
            val isPlaying = state.game?.status is UiGame.UiStatus.Started
            mbPlay.text = getString(
                if (!isPlaying) R.string.game_round_start_text else R.string.game_round_stop_text
            )
            mbPlay.setCompoundDrawablesWithIntrinsicBounds(
                if (!isPlaying) playDrawable else stopDrawable, null, null, null
            )
            bvBoard.round = state.game?.currentRound
            tvTime.text = getString(
                R.string.item_game_stats_time_format,
                state.elapsedTimeSeconds.toString()
            )
            tvRoundNumber.text = getString(
                R.string.item_game_stats_round_number,
                state.game?.currentRound?.number?.toString()
            )
            tvStalemateRounds.text = getString(
                R.string.item_game_stats_stalemate_format,
                state.game?.stalemateRounds.toString()
            )
        }
    }

    private fun navigateToSettings() {
        findNavController().navigate(
            directions = GameFragmentDirections.actionGameFragmentToSettingsFragment()
        )
    }
}
