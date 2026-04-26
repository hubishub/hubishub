package com.hubishub.animalvoice.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.hubishub.animalvoice.R
import com.hubishub.animalvoice.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()
    private lateinit var animalAdapter: AnimalAdapter

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            startRecording()
        } else {
            Toast.makeText(this, "Microphone permission is required to record your voice!", Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupAnimalGrid()
        setupMicButton()
        setupPlayButton()
        observeViewModel()
    }

    private fun setupAnimalGrid() {
        animalAdapter = AnimalAdapter(
            animals = viewModel.animals,
            selectedId = viewModel.selectedAnimal.value?.id ?: 1,
            onAnimalClick = { animal ->
                viewModel.selectAnimal(animal)
            }
        )

        binding.rvAnimals.apply {
            layoutManager = GridLayoutManager(this@MainActivity, 3)
            adapter = animalAdapter
            setHasFixedSize(true)
        }
    }

    private fun setupMicButton() {
        binding.btnRecord.setOnTouchListener { _, event ->
            when (event.action) {
                android.view.MotionEvent.ACTION_DOWN -> {
                    checkPermissionAndRecord()
                    true
                }
                android.view.MotionEvent.ACTION_UP,
                android.view.MotionEvent.ACTION_CANCEL -> {
                    if (viewModel.recordingState.value == MainViewModel.RecordingState.RECORDING) {
                        viewModel.stopRecordingAndPlay()
                    }
                    true
                }
                else -> false
            }
        }
    }

    private fun setupPlayButton() {
        binding.btnPlay.setOnClickListener {
            when (viewModel.recordingState.value) {
                MainViewModel.RecordingState.PLAYING -> viewModel.stopPlayback()
                else -> viewModel.playTransformed()
            }
        }
    }

    private fun checkPermissionAndRecord() {
        when {
            ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                    == PackageManager.PERMISSION_GRANTED -> startRecording()
            else -> requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        }
    }

    private fun startRecording() {
        viewModel.startRecording()
    }

    private fun observeViewModel() {
        viewModel.selectedAnimal.observe(this) { animal ->
            binding.tvSelectedAnimal.text = animal.emoji
            binding.tvAnimalName.text = animal.name
            binding.tvAnimalDescription.text = animal.description
            animalAdapter.updateSelection(animal.id)
        }

        viewModel.recordingState.observe(this) { state ->
            when (state) {
                MainViewModel.RecordingState.IDLE -> {
                    binding.btnRecord.setImageResource(R.drawable.ic_mic)
                    binding.tvRecordHint.text = getString(R.string.hold_to_record)
                    binding.waveformView.visibility = View.INVISIBLE
                    binding.btnPlay.visibility = if (viewModel.hasRecording.value == true) View.VISIBLE else View.GONE
                    binding.btnPlay.setImageResource(R.drawable.ic_play)
                    stopPulseAnimation()
                }
                MainViewModel.RecordingState.RECORDING -> {
                    binding.btnRecord.setImageResource(R.drawable.ic_mic_active)
                    binding.tvRecordHint.text = getString(R.string.release_to_play)
                    binding.waveformView.visibility = View.VISIBLE
                    binding.btnPlay.visibility = View.GONE
                    startPulseAnimation()
                }
                MainViewModel.RecordingState.PLAYING -> {
                    binding.btnRecord.setImageResource(R.drawable.ic_mic)
                    binding.tvRecordHint.text = getString(R.string.playing_as_animal)
                    binding.waveformView.visibility = View.INVISIBLE
                    binding.btnPlay.visibility = View.VISIBLE
                    binding.btnPlay.setImageResource(R.drawable.ic_stop)
                    stopPulseAnimation()
                }
                null -> {}
            }
        }

        viewModel.amplitude.observe(this) { amplitude ->
            binding.waveformView.updateAmplitude(amplitude)
        }

        viewModel.hasRecording.observe(this) { has ->
            if (viewModel.recordingState.value == MainViewModel.RecordingState.IDLE) {
                binding.btnPlay.visibility = if (has) View.VISIBLE else View.GONE
            }
        }

        viewModel.toastMessage.observe(this) { msg ->
            if (msg != null) {
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
                viewModel.clearToast()
            }
        }
    }

    private fun startPulseAnimation() {
        val pulse = AnimationUtils.loadAnimation(this, R.anim.pulse)
        binding.btnRecord.startAnimation(pulse)
        binding.tvSelectedAnimal.startAnimation(
            AnimationUtils.loadAnimation(this, R.anim.bounce)
        )
    }

    private fun stopPulseAnimation() {
        binding.btnRecord.clearAnimation()
        binding.tvSelectedAnimal.clearAnimation()
    }

    override fun onPause() {
        super.onPause()
        if (viewModel.recordingState.value == MainViewModel.RecordingState.RECORDING) {
            viewModel.cancelRecording()
        }
    }
}
