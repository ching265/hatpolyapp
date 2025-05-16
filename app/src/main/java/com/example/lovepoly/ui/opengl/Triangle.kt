package com.example.lovepoly.ui.opengl

import android.opengl.GLES20
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

class Triangle(
    private val vertices: FloatArray,  // 3 đỉnh, mỗi đỉnh có 3 tọa độ (x,y,z)
    private val textureCoords: FloatArray,  // 3 đỉnh, mỗi đỉnh có 2 tọa độ texture (u,v)
    val gridPosition: Point  // Vị trí trong lưới
) {
    // Lưu trữ tọa độ ban đầu
    val originalVertices = vertices.copyOf()
    // Góc xoay riêng của tam giác
    var individualAngle: Float = 0f
    var originalAngle: Float = 0f
    
    // Buffer cho vertices
    private val vertexBuffer: FloatBuffer
    // Buffer cho texture coordinates
    private val textureBuffer: FloatBuffer
    
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
    }

    fun draw(mvpMatrix: FloatArray, textureId: Int, program: Int, globalAngle: Float = 0f) {
        GLES20.glUseProgram(program)

        // Lấy handle cho các biến trong shader
        val positionHandle = GLES20.glGetAttribLocation(program, "vPosition")
        val textureCoordHandle = GLES20.glGetAttribLocation(program, "aTexCoord")
        val mvpMatrixHandle = GLES20.glGetUniformLocation(program, "uMVPMatrix")
        val textureHandle = GLES20.glGetUniformLocation(program, "uTexture")

        // Enable vertex array
        GLES20.glEnableVertexAttribArray(positionHandle)
        GLES20.glEnableVertexAttribArray(textureCoordHandle)

        // Chuẩn bị vertex data
        GLES20.glVertexAttribPointer(positionHandle, 3, GLES20.GL_FLOAT, false, 0, vertexBuffer)
        GLES20.glVertexAttribPointer(textureCoordHandle, 2, GLES20.GL_FLOAT, false, 0, textureBuffer)

        // Áp dụng thêm xoay quanh tâm tam giác
        val center = floatArrayOf(
            (vertices[0] + vertices[3] + vertices[6]) / 3f,
            (vertices[1] + vertices[4] + vertices[7]) / 3f,
            (vertices[2] + vertices[5] + vertices[8]) / 3f
        )
        val rotateMatrix = FloatArray(16)
        android.opengl.Matrix.setIdentityM(rotateMatrix, 0)
        android.opengl.Matrix.translateM(rotateMatrix, 0, center[0], center[1], center[2])
        android.opengl.Matrix.rotateM(rotateMatrix, 0, individualAngle + globalAngle, 0f, 0f, 1f)
        android.opengl.Matrix.translateM(rotateMatrix, 0, -center[0], -center[1], -center[2])
        val finalMVP = FloatArray(16)
        android.opengl.Matrix.multiplyMM(finalMVP, 0, mvpMatrix, 0, rotateMatrix, 0)

        // Áp dụng transformation matrix
        GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, finalMVP, 0)

        // Set texture
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId)
        GLES20.glUniform1i(textureHandle, 0)

        // Vẽ tam giác
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 3)

        // Disable vertex array
        GLES20.glDisableVertexAttribArray(positionHandle)
        GLES20.glDisableVertexAttribArray(textureCoordHandle)
    }

    fun updateVertices(newVertices: FloatArray) {
        vertexBuffer.clear()
        vertexBuffer.put(newVertices)
        vertexBuffer.position(0)
    }

    fun resetToOriginal() {
        updateVertices(originalVertices)
    }

    fun shuffleAngle() {
        // Xoay ngẫu nhiên 0, 120, 240 độ (tam giác đều)
        val angles = listOf(0f, 120f, 240f)
        individualAngle = angles.random()
        originalAngle = individualAngle
    }
}

data class Point(val x: Int, val y: Int) 