package com.example.lovepoly.ui.opengl

import android.content.Context
import android.opengl.GLES20
import com.example.lovepoly.R
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

class Rectangle3D(private val context: Context) {
    private var vertexBuffer: FloatBuffer
    private var textureBuffer: FloatBuffer
    private var mProgram: Int
    private var textureId: Int

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

    // Tọa độ texture cho các đỉnh
    private val textureCoords = floatArrayOf(
        // Mặt trước
        0.0f, 1.0f,  // bottom left
        1.0f, 1.0f,  // bottom right
        1.0f, 0.0f,  // top right
        0.0f, 0.0f,  // top left

        // Mặt sau
        0.0f, 1.0f,  // bottom left
        1.0f, 1.0f,  // bottom right
        1.0f, 0.0f,  // top right
        0.0f, 0.0f   // top left
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

        // Khởi tạo texture buffer
        val tb = ByteBuffer.allocateDirect(textureCoords.size * 4)
        tb.order(ByteOrder.nativeOrder())
        textureBuffer = tb.asFloatBuffer()
        textureBuffer.put(textureCoords)
        textureBuffer.position(0)

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

        // Load texture
        textureId = TextureHelper.loadTexture(context, R.drawable.watermelon_icecream)
    }

    fun draw(mvpMatrix: FloatArray) {
        GLES20.glUseProgram(mProgram)

        // Lấy handle cho các biến trong shader
        val positionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition")
        val textureCoordHandle = GLES20.glGetAttribLocation(mProgram, "aTexCoord")
        val mvpMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix")
        val textureHandle = GLES20.glGetUniformLocation(mProgram, "uTexture")

        // Enable vertex array
        GLES20.glEnableVertexAttribArray(positionHandle)
        GLES20.glEnableVertexAttribArray(textureCoordHandle)

        // Chuẩn bị vertex data
        GLES20.glVertexAttribPointer(positionHandle, 3, GLES20.GL_FLOAT, false, 0, vertexBuffer)
        GLES20.glVertexAttribPointer(textureCoordHandle, 2, GLES20.GL_FLOAT, false, 0, textureBuffer)

        // Áp dụng transformation matrix
        GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, mvpMatrix, 0)

        // Set texture
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId)
        GLES20.glUniform1i(textureHandle, 0)

        // Vẽ hình chữ nhật
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, indices.size, GLES20.GL_UNSIGNED_SHORT, indexBuffer)

        // Disable vertex array
        GLES20.glDisableVertexAttribArray(positionHandle)
        GLES20.glDisableVertexAttribArray(textureCoordHandle)
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
            "attribute vec2 aTexCoord;" +
            "varying vec2 vTexCoord;" +
            "void main() {" +
            "  gl_Position = uMVPMatrix * vPosition;" +
            "  vTexCoord = aTexCoord;" +
            "}"

        private const val fragmentShaderCode =
            "precision mediump float;" +
            "varying vec2 vTexCoord;" +
            "uniform sampler2D uTexture;" +
            "void main() {" +
            "  gl_FragColor = texture2D(uTexture, vTexCoord);" +
            "}"
    }
} 