package com.hubishub.animalvoice.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.AlphaAnimation
import android.view.animation.AnimationSet
import android.view.animation.ScaleAnimation
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.hubishub.animalvoice.R

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val logo = findViewById<TextView>(R.id.splash_logo)
        val title = findViewById<TextView>(R.id.splash_title)
        val subtitle = findViewById<TextView>(R.id.splash_subtitle)

        val fadeIn = AlphaAnimation(0f, 1f).apply { duration = 600 }
        val scaleIn = ScaleAnimation(
            0.6f, 1f, 0.6f, 1f,
            ScaleAnimation.RELATIVE_TO_SELF, 0.5f,
            ScaleAnimation.RELATIVE_TO_SELF, 0.5f
        ).apply { duration = 600 }

        val animSet = AnimationSet(true).apply {
            addAnimation(fadeIn)
            addAnimation(scaleIn)
        }

        logo.startAnimation(animSet)

        val textFade = AlphaAnimation(0f, 1f).apply {
            duration = 700
            startOffset = 400
            fillAfter = true
        }
        title.startAnimation(textFade)
        subtitle.startAnimation(AlphaAnimation(0f, 1f).apply {
            duration = 700
            startOffset = 600
            fillAfter = true
        })

        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }, 2000)
    }
}
