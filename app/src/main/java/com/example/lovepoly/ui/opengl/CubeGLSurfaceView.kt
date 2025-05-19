package com.example.lovepoly.ui.opengl

import android.content.Context
import android.opengl.GLSurfaceView
import android.util.AttributeSet
import android.view.MotionEvent
import com.example.lovepoly.model.Piece
import kotlin.math.abs

class CubeGLSurfaceView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : GLSurfaceView(context, attrs) {

    private val renderer: CubeRenderer
    private var previousX: Float = 0f
    private var previousY: Float = 0f
    private var isRotating: Boolean = false
    private val rotationSensitivity: Float = 0.3f
    private val minMovement: Float = 3f

    init {
        setEGLContextClientVersion(2)
        renderer = CubeRenderer(context)
        setRenderer(renderer)
        renderMode = RENDERMODE_WHEN_DIRTY
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x
        val y = event.y

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                // Kiểm tra xem touch có nằm trong vùng xoay không
                if (isInRotationArea(x, y)) {
                    previousX = x
                    previousY = y
                    isRotating = true
                    return true
                }
                return false
            }
            MotionEvent.ACTION_MOVE -> {
                if (isRotating) {
                    val dx = x - previousX
                    val dy = y - previousY
                    
                    // Chỉ xoay khi di chuyển đủ lớn
                    if (Math.abs(dx) > minMovement || Math.abs(dy) > minMovement) {
                        renderer.rotate(dx * rotationSensitivity, dy * rotationSensitivity)
                        requestRender()
                    }
                    
                    previousX = x
                    previousY = y
                    return true
                }
                return false
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                if (isRotating) {
                    isRotating = false
                    renderer.stopRotation()
                    return true
                }
                return false
            }
        }
        return false
    }

    private fun isInRotationArea(x: Float, y: Float): Boolean {
        // Kiểm tra xem touch có nằm trong vùng xoay không
        // Ở đây chúng ta có thể thêm logic để xác định vùng xoay
        // Ví dụ: chỉ cho phép xoay ở giữa màn hình
        val centerX = width / 2f
        val centerY = height / 2f
        val radius = Math.min(width, height) / 3f
        
        val dx = x - centerX
        val dy = y - centerY
        return (dx * dx + dy * dy) <= radius * radius
    }

    fun resetRotation() {
        renderer.reset()
        requestRender()
        // Đảm bảo view được vẽ lại ngay lập tức
        renderMode = RENDERMODE_WHEN_DIRTY
    }
}