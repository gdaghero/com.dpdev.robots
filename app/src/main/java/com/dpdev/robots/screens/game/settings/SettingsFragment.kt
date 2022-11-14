package com.dpdev.robots.screens.game.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.dpdev.robots.R
import com.dpdev.robots.databinding.FragmentSettingsBinding
import com.dpdev.robots.screens.game.settings.SettingsContract.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SettingsFragment : Fragment(R.layout.fragment_settings) {

    private lateinit var binding: FragmentSettingsBinding

    private val viewModel by viewModels<SettingsViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentSettingsBinding.inflate(inflater)
        this.binding = binding
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect(::render)
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiEffects.collect(::sideEffect)
            }
        }

        with(binding) {
            tToolbar.setupWithNavController(findNavController())
            mbAdd.setOnClickListener {
                viewModel.sendAction(Action.IncrementPlayerCount)
            }
            mbRemove.setOnClickListener {
                viewModel.sendAction(Action.DecrementPlayerCount)
            }
            mbSave.setOnClickListener {
                viewModel.sendAction(Action.SaveSettings)
            }
        }
    }

    private fun render(state: State) {
        binding.tvPlayers.text = getString(
            R.string.settings_players_format,
            state.players.toString(),
            state.maxPlayersCount.toString()
        )
    }

    private fun sideEffect(effect: Effect) {
        when (effect) {
            is Effect.SettingsSaved -> {
                Toast
                    .makeText(context, R.string.settings_saved_message, Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }
}
