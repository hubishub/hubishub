package com.hubishub.animalvoice.audio

import android.content.Context
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.media.PlaybackParams
import android.os.Build
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class AudioProcessor(private val context: Context) {

    companion object {
        private const val SAMPLE_RATE = 44100
    }

    private var mediaRecorder: MediaRecorder? = null
    private var mediaPlayer: MediaPlayer? = null
    private var isRecording = false

    val recordedFile: File get() = File(context.cacheDir, "recorded_voice.m4a")

    fun startRecording() {
        stopRecording()
        recordedFile.delete()

        mediaRecorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            MediaRecorder(context)
        } else {
            @Suppress("DEPRECATION")
            MediaRecorder()
        }.apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setAudioSamplingRate(SAMPLE_RATE)
            setAudioEncodingBitRate(128000)
            setOutputFile(recordedFile.absolutePath)
            prepare()
            start()
        }
        isRecording = true
    }

    fun stopRecording() {
        if (isRecording) {
            try {
                mediaRecorder?.apply {
                    stop()
                    release()
                }
            } catch (e: Exception) {
                // Recording may not have started yet
            }
            mediaRecorder = null
            isRecording = false
        }
    }

    fun isRecording() = isRecording

    fun getAmplitude(): Int {
        return try {
            mediaRecorder?.maxAmplitude ?: 0
        } catch (e: Exception) {
            0
        }
    }

    // Play the recorded voice transformed with animal pitch and speed
    suspend fun playTransformed(pitch: Float, speed: Float, onComplete: () -> Unit) {
        withContext(Dispatchers.IO) {
            stopPlayback()
            if (!recordedFile.exists() || recordedFile.length() == 0L) return@withContext

            withContext(Dispatchers.Main) {
                try {
                    mediaPlayer = MediaPlayer().apply {
                        setDataSource(recordedFile.absolutePath)
                        prepare()
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            playbackParams = PlaybackParams().apply {
                                this.pitch = pitch.coerceIn(0.1f, 4.0f)
                                this.speed = speed.coerceIn(0.1f, 4.0f)
                            }
                        }
                        setOnCompletionListener { onComplete() }
                        start()
                    }
                } catch (e: Exception) {
                    onComplete()
                }
            }
        }
    }

    fun stopPlayback() {
        mediaPlayer?.apply {
            if (isPlaying) stop()
            reset()
            release()
        }
        mediaPlayer = null
    }

    fun isPlaying() = mediaPlayer?.isPlaying == true

    fun hasRecording() = recordedFile.exists() && recordedFile.length() > 0

    fun release() {
        stopRecording()
        stopPlayback()
    }
}
