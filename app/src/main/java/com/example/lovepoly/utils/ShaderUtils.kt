package com.example.lovepoly.utils

import android.graphics.Bitmap
import android.opengl.GLES20
import android.opengl.GLUtils
import android.util.Log

object ShaderUtils {
    fun compileVertexShader(shaderCode: String): Int {
        return compileShader(GLES20.GL_VERTEX_SHADER, shaderCode)
    }

    fun compileFragmentShader(shaderCode: String): Int {
        return compileShader(GLES20.GL_FRAGMENT_SHADER, shaderCode)
    }

    private fun compileShader(type: Int, shaderCode: String): Int {
        val shaderId = GLES20.glCreateShader(type)
        if (shaderId == 0) {
            Log.e("ShaderUtils", "Could not create shader.")
            return 0
        }

        GLES20.glShaderSource(shaderId, shaderCode)
        GLES20.glCompileShader(shaderId)

        val compileStatus = IntArray(1)
        GLES20.glGetShaderiv(shaderId, GLES20.GL_COMPILE_STATUS, compileStatus, 0)

        if (compileStatus[0] == 0) {
            Log.e("ShaderUtils", "Compilation failed: ${GLES20.glGetShaderInfoLog(shaderId)}")
            GLES20.glDeleteShader(shaderId)
            return 0
        }

        return shaderId
    }

    fun linkProgram(vertexShaderId: Int, fragmentShaderId: Int): Int {
        val programId = GLES20.glCreateProgram()
        if (programId == 0) {
            Log.e("ShaderUtils", "Could not create program.")
            return 0
        }

        GLES20.glAttachShader(programId, vertexShaderId)
        GLES20.glAttachShader(programId, fragmentShaderId)
        GLES20.glLinkProgram(programId)

        val linkStatus = IntArray(1)
        GLES20.glGetProgramiv(programId, GLES20.GL_LINK_STATUS, linkStatus, 0)

        if (linkStatus[0] == 0) {
            Log.e("ShaderUtils", "Linking failed: ${GLES20.glGetProgramInfoLog(programId)}")
            GLES20.glDeleteProgram(programId)
            return 0
        }

        return programId
    }

    fun createProgram(vertexCode: String, fragmentCode: String): Int {
        val vertexShader = compileVertexShader(vertexCode)
        val fragmentShader = compileFragmentShader(fragmentCode)
        return linkProgram(vertexShader, fragmentShader)
    }

    // ✅ Hàm thêm mới để load texture từ Bitmap
    fun loadTexture(bitmap: Bitmap): Int {
        val textureIds = IntArray(1)
        GLES20.glGenTextures(1, textureIds, 0)

        if (textureIds[0] == 0) {
            Log.e("ShaderUtils", "Failed to generate texture.")
            return 0
        }

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureIds[0])

        // Set texture parameters
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE)

        // Load the bitmap into OpenGL
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0)

        // Unbind texture
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0)

        return textureIds[0]
    }
}