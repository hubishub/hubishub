package com.hubishub.animalvoice.ui

import android.app.Application
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.hubishub.animalvoice.audio.AudioProcessor
import com.hubishub.animalvoice.model.Animal
import com.hubishub.animalvoice.model.AnimalData
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    val animals: List<Animal> = AnimalData.animals

    private val _selectedAnimal = MutableLiveData<Animal>(animals[0])
    val selectedAnimal: LiveData<Animal> = _selectedAnimal

    private val _recordingState = MutableLiveData(RecordingState.IDLE)
    val recordingState: LiveData<RecordingState> = _recordingState

    private val _amplitude = MutableLiveData(0)
    val amplitude: LiveData<Int> = _amplitude

    private val _hasRecording = MutableLiveData(false)
    val hasRecording: LiveData<Boolean> = _hasRecording

    private val _toastMessage = MutableLiveData<String?>()
    val toastMessage: LiveData<String?> = _toastMessage

    private val audioProcessor = AudioProcessor(application)
    private val amplitudeHandler = Handler(Looper.getMainLooper())

    private val amplitudeRunnable = object : Runnable {
        override fun run() {
            if (_recordingState.value == RecordingState.RECORDING) {
                _amplitude.value = audioProcessor.getAmplitude()
                amplitudeHandler.postDelayed(this, 80)
            }
        }
    }

    fun selectAnimal(animal: Animal) {
        _selectedAnimal.value = animal
    }

    fun startRecording() {
        if (_recordingState.value == RecordingState.PLAYING) {
            audioProcessor.stopPlayback()
        }
        audioProcessor.startRecording()
        _recordingState.value = RecordingState.RECORDING
        amplitudeHandler.post(amplitudeRunnable)
    }

    fun stopRecordingAndPlay() {
        amplitudeHandler.removeCallbacks(amplitudeRunnable)
        _amplitude.value = 0
        audioProcessor.stopRecording()

        if (!audioProcessor.hasRecording()) {
            _recordingState.value = RecordingState.IDLE
            _toastMessage.value = "No audio recorded. Please try again."
            return
        }

        _hasRecording.value = true
        playTransformed()
    }

    fun playTransformed() {
        val animal = _selectedAnimal.value ?: return
        if (!audioProcessor.hasRecording()) {
            _toastMessage.value = "Nothing recorded yet! Hold the mic button to record."
            return
        }

        if (_recordingState.value == RecordingState.RECORDING) return

        _recordingState.value = RecordingState.PLAYING
        viewModelScope.launch {
            audioProcessor.playTransformed(animal.pitch, animal.speed) {
                _recordingState.postValue(RecordingState.IDLE)
            }
        }
    }

    fun stopPlayback() {
        audioProcessor.stopPlayback()
        _recordingState.value = RecordingState.IDLE
    }

    fun cancelRecording() {
        amplitudeHandler.removeCallbacks(amplitudeRunnable)
        _amplitude.value = 0
        audioProcessor.stopRecording()
        _recordingState.value = RecordingState.IDLE
    }

    fun clearToast() {
        _toastMessage.value = null
    }

    override fun onCleared() {
        super.onCleared()
        amplitudeHandler.removeCallbacks(amplitudeRunnable)
        audioProcessor.release()
    }

    enum class RecordingState {
        IDLE, RECORDING, PLAYING
    }
}
