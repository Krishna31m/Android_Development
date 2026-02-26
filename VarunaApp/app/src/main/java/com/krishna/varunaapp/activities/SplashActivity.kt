package com.krishna.varunaapp.activities

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.krishna.varunaapp.R
import com.krishna.varunaapp.utils.FirebaseUtils

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val drop = findViewById<ImageView>(R.id.drop)
        val water = findViewById<ImageView>(R.id.water)
        val container = findViewById<LinearLayout>(R.id.letterContainer)

        val text = getString(R.string.welcome_subtitle)

        // ================= WATER DROP ANIMATION =================
        drop.post {

            val distance = water.top - drop.top - drop.height / 2

            val dropAnim = ObjectAnimator.ofFloat(drop, "translationY", 0f, distance.toFloat())
            dropAnim.duration = 1500
            dropAnim.interpolator = AccelerateInterpolator()

            val waterAnim = ObjectAnimator.ofFloat(water, "scaleY", 1f, 1.1f)
            waterAnim.duration = 300
            waterAnim.repeatCount = 1
            waterAnim.repeatMode = ObjectAnimator.REVERSE

            val set = AnimatorSet()
            set.playSequentially(dropAnim, waterAnim)
            set.start()
        }

        // ================= LETTER ANIMATION =================
        val maxTotalTime = 2500L
        val letterDuration = 500L
        val calculatedDelay = if (text.isNotEmpty())
            (maxTotalTime - letterDuration) / text.length
        else
            0L

        for ((index, char) in text.withIndex()) {

            val letterView = TextView(this).apply {
                this.text = char.toString()
                textSize = 22f
                setTextColor(Color.BLACK)
                alpha = 0f
            }

            container.addView(letterView)

            when (index % 4) {
                0 -> letterView.translationY = -120f
                1 -> letterView.translationX = -120f
                2 -> letterView.translationY = 120f
                3 -> letterView.translationX = 120f
            }

            letterView.animate()
                .alpha(1f)
                .translationX(0f)
                .translationY(0f)
                .setStartDelay(index * calculatedDelay)
                .setDuration(letterDuration)
                .setInterpolator(DecelerateInterpolator())
                .start()
        }


        // ================= DELAY NAVIGATION =================
        val splashDuration = 3000L

        drop.postDelayed({

            startActivity(
                Intent(
                    this@SplashActivity,
                    if (FirebaseUtils.currentUser() != null)
                        MainActivity::class.java
                    else
                        LoginActivity::class.java
                )
            )
            finish()

        }, splashDuration)

    }
}


//package com.krishna.varunaapp.activities
//
//import android.animation.AnimatorSet
//import android.animation.ObjectAnimator
//import android.content.Intent
//import android.graphics.Color
//import android.os.Bundle
//import android.view.View
//import android.view.animation.AccelerateInterpolator
//import android.widget.ImageView
//import android.widget.LinearLayout
//import android.widget.TextView
//import androidx.appcompat.app.AppCompatActivity
//import com.krishna.varunaapp.R
//import com.krishna.varunaapp.utils.FirebaseUtils
//
//class SplashActivity : AppCompatActivity() {
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_splash)
//
//        val drop = findViewById<ImageView>(R.id.drop)
//        val water = findViewById<ImageView>(R.id.water)
//
//        drop.post {
//
//            val distance = water.top - drop.top - drop.height / 2
//
//            val dropAnim = ObjectAnimator.ofFloat(drop, "translationY", 0f, distance.toFloat())
//            dropAnim.duration = 1500
//            dropAnim.interpolator = AccelerateInterpolator()
//
//            val waterAnim = ObjectAnimator.ofFloat(water, "scaleY", 1f, 1.1f)
//            waterAnim.duration = 300
//            waterAnim.repeatCount = 1
//            waterAnim.repeatMode = ObjectAnimator.REVERSE
//
//            val set = AnimatorSet()
//            set.playSequentially(dropAnim, waterAnim)
//
//            set.start()
//
//            set.addListener(object : android.animation.Animator.AnimatorListener {
//                override fun onAnimationEnd(animation: android.animation.Animator) {
//                    startActivity(
//                        Intent(
//                            this@SplashActivity,
//                            if (FirebaseUtils.currentUser() != null)
//                                MainActivity::class.java
//                            else
//                                LoginActivity::class.java
//                        )
//                    )
//                    finish()
//                }
//                override fun onAnimationStart(animation: android.animation.Animator) {}
//                override fun onAnimationCancel(animation: android.animation.Animator) {}
//                override fun onAnimationRepeat(animation: android.animation.Animator) {}
//            })
//
//
//        }
//
//        val container = findViewById<LinearLayout>(R.id.letterContainer)
//
//        var text = getString(R.string.splash_welcome)
//
//        for ((index, char) in text.withIndex()) {
//
//            val letterView = TextView(this).apply {
//                text = char.toString()
//                textSize = 26f
//                setTextColor(Color.BLACK)
//                alpha = 0f
//            }
//
//            container.addView(letterView)
//
//            // Different direction based on position
//            when (index % 4) {
//                0 -> letterView.translationY = -100f
//                1 -> letterView.translationX = -100f
//                2 -> letterView.translationY = 100f
//                3 -> letterView.translationX = 100f
//            }
//
//            letterView.animate()
//                .alpha(1f)
//                .translationX(0f)
//                .translationY(0f)
//                .setStartDelay(index * 120L)
//                .setDuration(600)
//                .setInterpolator(android.view.animation.DecelerateInterpolator())
//                .start()
//        }
//
//
//    }
//}




//package com.krishna.varunaapp.activities
//
//import android.content.Intent
//import android.os.Bundle
//import androidx.appcompat.app.AppCompatActivity
//import com.krishna.varunaapp.utils.FirebaseUtils
//
//
//class SplashActivity : AppCompatActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//        startActivity(
//            Intent(this, if (FirebaseUtils.currentUser() != null) MainActivity::class.java else LoginActivity::class.java)
//        )
//        finish()
//    }
//}