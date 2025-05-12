package com.example.lovepoly.ui.opengl

import android.content.Context
import android.opengl.GLES20
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

class Rectangle3D(private val context: Context) {
    private var vertexBuffer: FloatBuffer
    private var colorBuffer: FloatBuffer
    private var mProgram: Int

    // Tọa độ các đỉnh của hình chữ nhật
    private val vertices = floatArrayOf(
        // Mặt trước
        -0.5f, -0.5f, 0.5f,  // bottom left
        0.5f, -0.5f, 0.5f,   // bottom right
        0.5f, 0.5f, 0.5f,    // top right
        -0.5f, 0.5f, 0.5f,   // top left

        // Mặt sau
        -0.5f, -0.5f, -0.5f, // bottom left
        0.5f, -0.5f, -0.5f,  // bottom right
        0.5f, 0.5f, -0.5f,   // top right
        -0.5f, 0.5f, -0.5f   // top left
    )

    // Màu sắc cho các đỉnh
    private val colors = floatArrayOf(
        1.0f, 0.0f, 0.0f, 1.0f,  // red
        0.0f, 1.0f, 0.0f, 1.0f,  // green
        0.0f, 0.0f, 1.0f, 1.0f,  // blue
        1.0f, 1.0f, 0.0f, 1.0f,  // yellow
        1.0f, 0.0f, 1.0f, 1.0f,  // magenta
        0.0f, 1.0f, 1.0f, 1.0f,  // cyan
        1.0f, 1.0f, 1.0f, 1.0f,  // white
        0.5f, 0.5f, 0.5f, 1.0f   // gray
    )

    // Thứ tự vẽ các mặt
    private val indices = shortArrayOf(
        // Mặt trước
        0, 1, 2,
        0, 2, 3,
        // Mặt phải
        1, 5, 6,
        1, 6, 2,
        // Mặt sau
        5, 4, 7,
        5, 7, 6,
        // Mặt trái
        4, 0, 3,
        4, 3, 7,
        // Mặt trên
        3, 2, 6,
        3, 6, 7,
        // Mặt dưới
        4, 5, 1,
        4, 1, 0
    )

    private var indexBuffer: ByteBuffer

    init {
        // Khởi tạo vertex buffer
        val bb = ByteBuffer.allocateDirect(vertices.size * 4)
        bb.order(ByteOrder.nativeOrder())
        vertexBuffer = bb.asFloatBuffer()
        vertexBuffer.put(vertices)
        vertexBuffer.position(0)

        // Khởi tạo color buffer
        val cb = ByteBuffer.allocateDirect(colors.size * 4)
        cb.order(ByteOrder.nativeOrder())
        colorBuffer = cb.asFloatBuffer()
        colorBuffer.put(colors)
        colorBuffer.position(0)

        // Khởi tạo index buffer
        indexBuffer = ByteBuffer.allocateDirect(indices.size * 2)
        indexBuffer.order(ByteOrder.nativeOrder())
        indexBuffer.asShortBuffer().put(indices)
        indexBuffer.position(0)

        // Tạo và compile shader program
        val vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode)
        val fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode)
        mProgram = GLES20.glCreateProgram().also { program ->
            GLES20.glAttachShader(program, vertexShader)
            GLES20.glAttachShader(program, fragmentShader)
            GLES20.glLinkProgram(program)
        }
    }

    fun draw(mvpMatrix: FloatArray) {
        GLES20.glUseProgram(mProgram)

        // Lấy handle cho các biến trong shader
        val positionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition")
        val colorHandle = GLES20.glGetAttribLocation(mProgram, "vColor")
        val mvpMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix")

        // Enable vertex array
        GLES20.glEnableVertexAttribArray(positionHandle)
        GLES20.glEnableVertexAttribArray(colorHandle)

        // Chuẩn bị vertex data
        GLES20.glVertexAttribPointer(positionHandle, 3, GLES20.GL_FLOAT, false, 0, vertexBuffer)
        GLES20.glVertexAttribPointer(colorHandle, 4, GLES20.GL_FLOAT, false, 0, colorBuffer)

        // Áp dụng transformation matrix
        GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, mvpMatrix, 0)

        // Vẽ hình chữ nhật
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, indices.size, GLES20.GL_UNSIGNED_SHORT, indexBuffer)

        // Disable vertex array
        GLES20.glDisableVertexAttribArray(positionHandle)
        GLES20.glDisableVertexAttribArray(colorHandle)
    }

    private fun loadShader(type: Int, shaderCode: String): Int {
        return GLES20.glCreateShader(type).also { shader ->
            GLES20.glShaderSource(shader, shaderCode)
            GLES20.glCompileShader(shader)
        }
    }

    companion object {
        private const val vertexShaderCode =
            "uniform mat4 uMVPMatrix;" +
            "attribute vec4 vPosition;" +
            "attribute vec4 vColor;" +
            "varying vec4 fragmentColor;" +
            "void main() {" +
            "  gl_Position = uMVPMatrix * vPosition;" +
            "  fragmentColor = vColor;" +
            "}"

        private const val fragmentShaderCode =
            "precision mediump float;" +
            "varying vec4 fragmentColor;" +
            "void main() {" +
            "  gl_FragColor = fragmentColor;" +
            "}"
    }
} 