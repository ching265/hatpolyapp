package com.example.lovepoly.ui.level

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.lovepoly.databinding.FragmentLevel1Binding
import com.example.lovepoly.model.Piece
import com.example.lovepoly.ui.opengl.CubeGLSurfaceView

class Level1Fragment : Fragment() {

    private var _binding: FragmentLevel1Binding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLevel1Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Load 16 pieces từ assets
        val pieces = (0 until 16).map { index ->
            Piece(
                id = index,
                imagePath = "images/watermelon_icecream/piece_$index.png"
            )
        }

        val glSurfaceView = CubeGLSurfaceView(requireContext())
        binding.glContainer.addView(glSurfaceView)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}