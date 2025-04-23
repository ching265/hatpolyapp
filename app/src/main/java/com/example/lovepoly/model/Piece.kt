package com.example.lovepoly.model

data class Piece(
    val id: Int,
    // id ảnh
    val imagePath: String,
    // góc xoay đúng theo X
    val targetRotationX: Float = 0f,
    // góc xoay đúng theo Y
    val targetRotationY: Float = 0f,
    // xoay đúng => false (khóa xoay)
    var isLocked: Boolean = false
)