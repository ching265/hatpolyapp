package com.example.lovepoly.ui.opengl

import android.content.Context
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class CubeRenderer(private val context: Context) : GLSurfaceView.Renderer {

    private lateinit var rectangle3D: Rectangle3D
    private val projectionMatrix = FloatArray(16)
    private val viewMatrix = FloatArray(16)
    private val mvpMatrix = FloatArray(16)
    private val rotationMatrix = FloatArray(16)
    var angleX = 0f
    var angleY = 0f

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        GLES20.glClearColor(0f, 0f, 0f, 1f)
        GLES20.glEnable(GLES20.GL_DEPTH_TEST)
        rectangle3D = Rectangle3D(context)
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
        Matrix.multiplyMM(rotationMatrix, 0, rotationMatrix, 0, temp, 0)
        val finalMVP = FloatArray(16)
        Matrix.multiplyMM(finalMVP, 0, mvpMatrix, 0, rotationMatrix, 0)

        // Draw rectangle
        rectangle3D.draw(finalMVP)
    }
}
