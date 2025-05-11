package com.example.lovepoly.ui.opengl

import android.content.Context
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import com.example.lovepoly.model.opengl.PieceTriangle
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class CubeRenderer(
    private val context: Context,
    private val pieces: List<PieceTriangle>
) : GLSurfaceView.Renderer {

    private val mvpMatrix = FloatArray(16)
    private val projectionMatrix = FloatArray(16)
    private val viewMatrix = FloatArray(16)
    private val rotationMatrix = FloatArray(16)

    // Góc xoay cập nhật từ gesture
    var angleX = 0f
    var angleY = 0f

    override fun onSurfaceCreated(unused: GL10?, config: EGLConfig?) {
        GLES20.glClearColor(0f, 0f, 0f, 1f)
        GLES20.glEnable(GLES20.GL_DEPTH_TEST)
        GLES20.glEnable(GLES20.GL_CULL_FACE)

        for (piece in pieces) {
            piece.setup()
        }
    }

    override fun onSurfaceChanged(unused: GL10?, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)

        val ratio = width.toFloat() / height
        Matrix.frustumM(projectionMatrix, 0, -ratio, ratio, -1f, 1f, 3f, 7f)
    }

    override fun onDrawFrame(unused: GL10?) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)

        // Camera setup
        Matrix.setLookAtM(
            viewMatrix, 0,
            0f, 0f, 5f,  // eye
            0f, 0f, 0f,  // center
            0f, 1f, 0f   // up
        )

        // Tạo ma trận xoay
        val tempMatrix = FloatArray(16)
        Matrix.setIdentityM(rotationMatrix, 0)
        Matrix.rotateM(rotationMatrix, 0, angleX, 1f, 0f, 0f)
        Matrix.rotateM(rotationMatrix, 0, angleY, 0f, 1f, 0f)

        Matrix.multiplyMM(tempMatrix, 0, viewMatrix, 0, rotationMatrix, 0)
        Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix, 0, tempMatrix, 0)

        // Vẽ từng mảnh tam giác
        for (piece in pieces) {
            piece.draw(mvpMatrix)
        }
    }
}
