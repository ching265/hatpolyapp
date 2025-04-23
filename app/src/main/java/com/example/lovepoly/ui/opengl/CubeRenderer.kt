package com.example.lovepoly.ui.opengl

import android.content.Context
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class CubeRenderer(private val context: Context) : GLSurfaceView.Renderer {

    private val pieces = mutableListOf<PiecePlane>()
    private val projectionMatrix = FloatArray(16)
    private val viewMatrix = FloatArray(16)
    private val mvpMatrix = FloatArray(16)
    private val rotationMatrix = FloatArray(16)
    var angleX = 0f
    var angleY = 0f

    private var angle = 0f

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        GLES20.glClearColor(0f, 0f, 0f, 1f)

        for (i in 0 until 16) {
            val path = "images/watermelon_icecream/piece_$i.png"
            pieces.add(PiecePlane(context, path))
        }
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)
        val ratio: Float = width.toFloat() / height
        Matrix.frustumM(projectionMatrix, 0, -ratio, ratio, -1f, 1f, 3f, 7f)
    }


    override fun onDrawFrame(gl: GL10?) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)

        Matrix.setLookAtM(viewMatrix, 0, 0f, 0f, 5f, 0f, 0f, 0f, 0f, 1f, 0f)
        Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix, 0, viewMatrix, 0)

        // Apply rotation
        Matrix.setRotateM(rotationMatrix, 0, angleY, 0f, 1f, 0f)
        val temp = FloatArray(16)
        Matrix.setRotateM(temp, 0, angleX, 1f, 0f, 0f)
        Matrix.multiplyMM(rotationMatrix, 0, rotationMatrix, 0, temp, 0);        val finalMVP = FloatArray(16)
        Matrix.multiplyMM(finalMVP, 0, mvpMatrix, 0, rotationMatrix, 0)

        // Vẽ 16 mảnh theo lưới 4x4
        val spacing = 0.32f

        for (i in 0 until 16) {
            val row = i / 4
            val col = i % 4
            val x = (col - 1.5f) * spacing
            val y = (1.5f - row) * spacing
            pieces[i].draw(finalMVP, x, y)
        }

        // Update góc xoay
        angle += 1f
    }
}
