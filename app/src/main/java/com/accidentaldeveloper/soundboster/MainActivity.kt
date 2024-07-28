package com.accidentaldeveloper.soundboster

import android.media.AudioAttributes
import android.media.AudioManager
import android.media.SoundPool
import android.media.audiofx.BassBoost
import android.media.audiofx.LoudnessEnhancer
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private lateinit var soundPool: SoundPool
    private var bassBoost: BassBoost? = null
    private var loudnessEnhancer: LoudnessEnhancer? = null
    private var soundId: Int = 0
    private var audioSessionId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize SoundPool
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_MEDIA)
            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
            .build()

        soundPool = SoundPool.Builder()
            .setMaxStreams(10)
            .setAudioAttributes(audioAttributes)
            .build()

        // Load your sound(s)
        soundId = soundPool.load(this, R.raw.indiacator, 1)

        // Obtain AudioSessionId from AudioManager
        val audioManager = getSystemService(AUDIO_SERVICE) as AudioManager
        audioSessionId = audioManager.generateAudioSessionId()

        // Initialize audio effects after loading the sound
        soundPool.setOnLoadCompleteListener { _, _, status ->
            if (status == 0) {
                initializeAudioEffects()
                playSound()
            }
        }
    }

    private fun initializeAudioEffects() {
        bassBoost = BassBoost(0, audioSessionId).apply {
            enabled = true
            setStrength(1000.toShort()) // Set the strength from 0 to 1000
        }

        loudnessEnhancer = LoudnessEnhancer(audioSessionId).apply {
            enabled = true
            setTargetGain(150000) // Set the gain in millibels (1000 mB = 1 dB)
        }
    }

    private fun playSound() {
        soundPool.play(soundId, 1f, 1f, 1, 0, 1f)
    }

    fun setBassBoostStrength(strength: Short) {
        bassBoost?.setStrength(strength)
    }

    fun setLoudnessEnhancerGain(gain: Int) {
        loudnessEnhancer?.setTargetGain(gain)
    }

    override fun onDestroy() {
        super.onDestroy()
        bassBoost?.release()
        loudnessEnhancer?.release()
        soundPool.release()
    }
}
