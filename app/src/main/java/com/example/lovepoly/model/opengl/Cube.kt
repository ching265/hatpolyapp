package com.example.lovepoly.model.opengl

import android.opengl.GLES20
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.ShortBuffer
import javax.microedition.khronos.opengles.GL10

class Cube {
    // Đỉnh khối lập phương (8 đỉnh)
    private val vertices = floatArrayOf(
        -1f,  1f,  1f, // 0
        -1f, -1f,  1f, // 1
        1f, -1f,  1f, // 2
        1f,  1f,  1f, // 3
        -1f,  1f, -1f, // 4
        -1f, -1f, -1f, // 5
        1f, -1f, -1f, // 6
        1f,  1f, -1f  // 7
    )

    // Các mặt (6 mặt, mỗi mặt gồm 2 tam giác → 6 điểm)
    private val indices = shortArrayOf(
        0, 1, 2, 0, 2, 3, // Front
        4, 5, 6, 4, 6, 7, // Back
        0, 1, 5, 0, 5, 4, // Left
        3, 2, 6, 3, 6, 7, // Right
        0, 3, 7, 0, 7, 4, // Top
        1, 2, 6, 1, 6, 5  // Bottom
    )

    // TODO: thêm màu hoặc texture

    val vertexBuffer: FloatBuffer
    val indexBuffer: ShortBuffer

    init {
        vertexBuffer = ByteBuffer.allocateDirect(vertices.size * 4)
            .order(ByteOrder.nativeOrder()).asFloatBuffer().apply {
                put(vertices)
                position(0)
            }

        indexBuffer = ByteBuffer.allocateDirect(indices.size * 2)
            .order(ByteOrder.nativeOrder()).asShortBuffer().apply {
                put(indices)
                position(0)
            }
    }

    fun draw(gl: GL10) {
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY)
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer)
        gl.glDrawElements(GL10.GL_TRIANGLES, indices.size, GL10.GL_UNSIGNED_SHORT, indexBuffer)
        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY)
    }
}