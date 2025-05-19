package com.example.lovepoly.ui.opengl

import android.content.Context
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class CubeRenderer(private val context: Context) : GLSurfaceView.Renderer {

    private lateinit var triangleGrid: TriangleGrid
    private val projectionMatrix = FloatArray(16)
    private val viewMatrix = FloatArray(16)
    private val mvpMatrix = FloatArray(16)
    private val modelMatrix = FloatArray(16)
    
    // Quaternion cho xoay
    private var currentRotation = Quaternion()
    private var targetRotation = Quaternion()
    private val slerpSpeed = 0.2f     // Tốc độ SLERP (càng nhỏ càng mượt)
    private var isRotating = false

    // Biến cho arcball rotation
    private var lastTouchX = 0f
    private var lastTouchY = 0f
    private val sphereRadius = 2.0f  // Bán kính của quả cầu xoay

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        GLES20.glClearColor(0f, 0f, 0f, 1f)
        GLES20.glEnable(GLES20.GL_DEPTH_TEST)
        GLES20.glEnable(GLES20.GL_TEXTURE_2D)
        triangleGrid = TriangleGrid(context)
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)
        val ratio: Float = width.toFloat() / height
        Matrix.frustumM(projectionMatrix, 0, -ratio, ratio, -1f, 1f, 3f, 7f)
    }

    override fun onDrawFrame(gl: GL10?) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)

        // Cập nhật xoay mượt mà
        if (isRotating) {
            currentRotation = Quaternion.slerp(currentRotation, targetRotation, slerpSpeed)
            
            // Dừng xoay khi đã đạt gần đến góc đích
            if (currentRotation.distanceTo(targetRotation) < 0.01f) {
                isRotating = false
            }
        }

        // Thiết lập camera cố định
        Matrix.setLookAtM(viewMatrix, 0, 
            0f, 0f, 5f,  // Vị trí camera
            0f, 0f, 0f,  // Điểm nhìn
            0f, 1f, 0f   // Vector up
        )

        // Áp dụng quaternion vào ma trận model
        Matrix.setIdentityM(modelMatrix, 0)
        currentRotation.toMatrix(modelMatrix)

        // Tính toán ma trận MVP
        Matrix.multiplyMM(mvpMatrix, 0, viewMatrix, 0, modelMatrix, 0)
        Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix, 0, mvpMatrix, 0)

        // Draw triangle grid với ma trận MVP
        triangleGrid.draw(mvpMatrix, 0f, 0f)
    }

    fun rotate(dx: Float, dy: Float) {
        // Tính toán độ lớn của vector vuốt
        val moveLength = sqrt(dx * dx + dy * dy)
        
        if (moveLength > 0) {
            // Chuẩn hóa vector vuốt
            val normalizedX = dx / moveLength
            val normalizedY = dy / moveLength
            
            // Tạo trục xoay vuông góc với vector vuốt
            val rotationAxisX = -normalizedY
            val rotationAxisY = -normalizedX
            val rotationAxisZ = 0f
            
            // Góc xoay tỷ lệ trực tiếp với tốc độ vuốt
            val rotationAngle = moveLength * 2.0f  // Tăng hệ số để phản ứng nhanh hơn
            
            // Tạo quaternion cho phép xoay mới
            val newRotation = Quaternion.fromAxisAngle(
                rotationAxisX,
                rotationAxisY,
                rotationAxisZ,
                rotationAngle * (Math.PI / 180f).toFloat()
            )
            
            // Kết hợp với xoay hiện tại
            targetRotation = currentRotation.multiply(newRotation)
            isRotating = true
        }
    }

    fun stopRotation() {
        isRotating = false
    }

    fun reset() {
        currentRotation = Quaternion()
        targetRotation = Quaternion()
        isRotating = false
    }
}

// Class Quaternion để xử lý xoay
class Quaternion(
    var x: Float = 0f,
    var y: Float = 0f,
    var z: Float = 0f,
    var w: Float = 1f
) {
    companion object {
        fun fromAxisAngle(axisX: Float, axisY: Float, axisZ: Float, angle: Float): Quaternion {
            val halfAngle = angle * 0.5f
            val s = sin(halfAngle)
            return Quaternion(
                axisX * s,
                axisY * s,
                axisZ * s,
                cos(halfAngle)
            )
        }

        fun slerp(q1: Quaternion, q2: Quaternion, t: Float): Quaternion {
            var dot = q1.x * q2.x + q1.y * q2.y + q1.z * q2.z + q1.w * q2.w

            // Nếu dot < 0, q1 và q2 ở hai phía của đường tròn
            if (dot < 0) {
                q2.x = -q2.x
                q2.y = -q2.y
                q2.z = -q2.z
                q2.w = -q2.w
                dot = -dot
            }

            if (dot > 0.9995f) {
                // Nếu quá gần nhau, sử dụng linear interpolation
                return Quaternion(
                    q1.x + (q2.x - q1.x) * t,
                    q1.y + (q2.y - q1.y) * t,
                    q1.z + (q2.z - q1.z) * t,
                    q1.w + (q2.w - q1.w) * t
                ).normalize()
            }

            // Thực hiện spherical linear interpolation
            val theta = kotlin.math.acos(dot)
            val sinTheta = kotlin.math.sin(theta)
            val w1 = kotlin.math.sin((1 - t) * theta) / sinTheta
            val w2 = kotlin.math.sin(t * theta) / sinTheta

            return Quaternion(
                w1 * q1.x + w2 * q2.x,
                w1 * q1.y + w2 * q2.y,
                w1 * q1.z + w2 * q2.z,
                w1 * q1.w + w2 * q2.w
            )
        }
    }

    fun multiply(q: Quaternion): Quaternion {
        return Quaternion(
            w * q.x + x * q.w + y * q.z - z * q.y,
            w * q.y - x * q.z + y * q.w + z * q.x,
            w * q.z + x * q.y - y * q.x + z * q.w,
            w * q.w - x * q.x - y * q.y - z * q.z
        )
    }

    fun normalize(): Quaternion {
        val length = kotlin.math.sqrt(x * x + y * y + z * z + w * w)
        if (length > 0) {
            x /= length
            y /= length
            z /= length
            w /= length
        }
        return this
    }

    fun distanceTo(q: Quaternion): Float {
        val dx = x - q.x
        val dy = y - q.y
        val dz = z - q.z
        val dw = w - q.w
        return kotlin.math.sqrt(dx * dx + dy * dy + dz * dz + dw * dw)
    }

    fun toMatrix(matrix: FloatArray) {
        val xx = x * x
        val xy = x * y
        val xz = x * z
        val xw = x * w
        val yy = y * y
        val yz = y * z
        val yw = y * w
        val zz = z * z
        val zw = z * w

        matrix[0] = 1 - 2 * (yy + zz)
        matrix[1] = 2 * (xy - zw)
        matrix[2] = 2 * (xz + yw)
        matrix[3] = 0f

        matrix[4] = 2 * (xy + zw)
        matrix[5] = 1 - 2 * (xx + zz)
        matrix[6] = 2 * (yz - xw)
        matrix[7] = 0f

        matrix[8] = 2 * (xz - yw)
        matrix[9] = 2 * (yz + xw)
        matrix[10] = 1 - 2 * (xx + yy)
        matrix[11] = 0f

        matrix[12] = 0f
        matrix[13] = 0f
        matrix[14] = 0f
        matrix[15] = 1f
    }
}
