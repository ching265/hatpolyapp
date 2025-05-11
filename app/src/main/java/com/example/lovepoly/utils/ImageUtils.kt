package com.example.lovepoly.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.example.lovepoly.model.opengl.PieceTriangle

object ImageUtils {

    fun generateTrianglesFromDrawable(
        context: Context,
        drawableRes: Int,
        gridSize: Int = 5
    ): List<PieceTriangle> {
        val bitmap = BitmapFactory.decodeResource(context.resources, drawableRes)
        val textureId = ShaderUtils.loadTexture(bitmap)
        return splitBitmapToTriangles(textureId, bitmap, gridSize)
    }

    private fun splitBitmapToTriangles(textureId: Int, bitmap: Bitmap, gridSize: Int): List<PieceTriangle> {
        val pieces = mutableListOf<PieceTriangle>()

        val imgWidth = bitmap.width.toFloat()
        val imgHeight = bitmap.height.toFloat()

        val cellWidth = imgWidth / gridSize
        val cellHeight = imgHeight / gridSize

        for (row in 0 until gridSize) {
            for (col in 0 until gridSize) {
                val x = col * cellWidth
                val y = row * cellHeight

                // Tọa độ texture
                val u0 = x / imgWidth
                val v0 = y / imgHeight
                val u1 = (x + cellWidth) / imgWidth
                val v1 = (y + cellHeight) / imgHeight

                // Tọa độ OpenGL
                val glX = -1f + 2f * col / gridSize
                val glY = 1f - 2f * row / gridSize
                val nextX = -1f + 2f * (col + 1) / gridSize
                val nextY = 1f - 2f * (row + 1) / gridSize

                // Tam giác 1
                pieces.add(
                    PieceTriangle(
                        floatArrayOf(
                            glX, glY, 0f,
                            nextX, glY, 0f,
                            glX, nextY, 0f
                        ),
                        floatArrayOf(
                            u0, v0,
                            u1, v0,
                            u0, v1
                        ),
                        textureId
                    )
                )

                // Tam giác 2
                pieces.add(
                    PieceTriangle(
                        floatArrayOf(
                            glX, nextY, 0f,
                            nextX, glY, 0f,
                            nextX, nextY, 0f
                        ),
                        floatArrayOf(
                            u0, v1,
                            u1, v0,
                            u1, v1
                        ),
                        textureId
                    )
                )
            }
        }

        return pieces
    }
}