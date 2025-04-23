package com.example.lovepoly.ui.opengl

import android.content.Context
import android.opengl.GLSurfaceView
import android.view.MotionEvent
import com.example.lovepoly.model.Piece

class CubeGLSurfaceView(context: Context) : GLSurfaceView(context) {

    private val renderer: CubeRenderer
    private var previousX = 0f
    private var previousY = 0f

    init {
        setEGLContextClientVersion(2)

        renderer = CubeRenderer(context)
        setRenderer(renderer)

        renderMode = RENDERMODE_CONTINUOUSLY
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x
        val y = event.y

        when (event.action) {
            MotionEvent.ACTION_MOVE -> {
                val dx = x - previousX
                val dy = y - previousY

                renderer.angleY += dx * 0.5f
                renderer.angleX += dy * 0.5f
            }
        }

        previousX = x
        previousY = y

        return true
    }
}