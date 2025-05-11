package com.example.lovepoly.ui.opengl

import android.content.Context
import android.opengl.GLSurfaceView
import android.view.MotionEvent
import com.example.lovepoly.ui.level.Level1ViewModel

class CubeGLSurfaceView(
    context: Context,
    pieces: List<PiecePlane>
) : GLSurfaceView(context) {

    private val renderer: CubeRenderer

    init {
        setEGLContextClientVersion(2)

        // Truyền danh sách mảnh vào renderer
        renderer = CubeRenderer(pieces)
        setRenderer(renderer)

        renderMode = RENDERMODE_CONTINUOUSLY
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x
        val y = event.y

        when (event.action) {
            MotionEvent.ACTION_MOVE -> {
                // TODO: bạn cần viết hàm rotateScene trong renderer
                renderer.rotateScene(x, y)
            }
        }

        return true
    }
}
