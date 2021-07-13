package com.example.android.bellmanford.anim

import android.animation.ObjectAnimator
import android.view.View

object AppAnimation {
    fun fadingButtonAnimation(view: View) {
        ObjectAnimator.ofFloat(view, View.ALPHA, 0.3F, 1F).setDuration(300).start()
    }
}