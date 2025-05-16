package com.example.lovepoly.ui.opengl

import android.content.Context
import android.opengl.GLES20
import android.opengl.Matrix
import com.example.lovepoly.R
import kotlin.math.cos
import kotlin.math.sin

class TriangleGrid(
    private val context: Context,
    private val gridSize: Int = 4  // Kích thước lưới (4x4)
) {
    private val triangles = mutableListOf<Triangle>()
    private val mProgram: Int
    private val textureId: Int
    private var currentAngle = 0f
    private var targetAngle = 0f
    private var rotationSpeed = 0f

    init {
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

        // Tạo lưới tam giác
        createGrid()
        shuffleAllTriangles()
    }

    private fun createGrid() {
        val radius = 1.0f
        val n = gridSize
        // Tạo lưới điểm trên mặt cầu
        val points = Array(n + 1) { i ->
            Array(n + 1) { j ->
                val theta = Math.PI * (i.toDouble() / n)
                val phi = 2 * Math.PI * (j.toDouble() / n)
                val x = (radius * Math.sin(theta) * Math.cos(phi)).toFloat()
                val y = (radius * Math.sin(theta) * Math.sin(phi)).toFloat()
                val z = (radius * Math.cos(theta)).toFloat()
                floatArrayOf(x, y, z)
            }
        }
        for (row in 0 until n) {
            for (col in 0 until n) {
                // Tam giác 1
                val v0 = points[row][col]
                val v1 = points[row + 1][col]
                val v2 = points[row][col + 1]
                val vertices1 = floatArrayOf(
                    v0[0], v0[1], v0[2],
                    v1[0], v1[1], v1[2],
                    v2[0], v2[1], v2[2]
                )
                val textureCoords1 = floatArrayOf(
                    col.toFloat() / n, row.toFloat() / n,
                    col.toFloat() / n, (row + 1).toFloat() / n,
                    (col + 1).toFloat() / n, row.toFloat() / n
                )
                triangles.add(Triangle(vertices1, textureCoords1, Point(col, row)))

                // Tam giác 2
                val v3 = points[row + 1][col + 1]
                val vertices2 = floatArrayOf(
                    v1[0], v1[1], v1[2],
                    v3[0], v3[1], v3[2],
                    v2[0], v2[1], v2[2]
                )
                val textureCoords2 = floatArrayOf(
                    col.toFloat() / n, (row + 1).toFloat() / n,
                    (col + 1).toFloat() / n, (row + 1).toFloat() / n,
                    (col + 1).toFloat() / n, row.toFloat() / n
                )
                triangles.add(Triangle(vertices2, textureCoords2, Point(col, row)))
            }
        }
    }

    fun shuffleAllTriangles() {
        triangles.forEach { it.shuffleAngle() }
    }

    fun draw(mvpMatrix: FloatArray, angleX: Float, angleZ: Float) {
        // Vẽ từng tam giác với hai góc xoay
        triangles.forEach { triangle ->
            val rotationMatrixX = FloatArray(16)
            val rotationMatrixZ = FloatArray(16)
            val combinedMatrix = FloatArray(16)
            android.opengl.Matrix.setRotateM(rotationMatrixX, 0, angleX, 1f, 0f, 0f)
            android.opengl.Matrix.setRotateM(rotationMatrixZ, 0, angleZ, 0f, 0f, 1f)
            android.opengl.Matrix.multiplyMM(combinedMatrix, 0, rotationMatrixZ, 0, rotationMatrixX, 0)
            val finalMVP = FloatArray(16)
            android.opengl.Matrix.multiplyMM(finalMVP, 0, mvpMatrix, 0, combinedMatrix, 0)
            triangle.draw(finalMVP, textureId, mProgram, 0f)
        }
    }

    fun rotate(angle: Float) {
        targetAngle = angle
    }

    fun reset() {
        triangles.forEach { it.resetToOriginal() }
        currentAngle = 0f
        targetAngle = 0f
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