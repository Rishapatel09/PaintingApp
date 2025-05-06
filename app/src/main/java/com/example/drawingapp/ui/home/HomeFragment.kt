package com.example.drawingapp.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.drawingapp.R
import com.example.drawingapp.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    // ViewBinding instance to access views
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    // Request code to identify image pick result
    private val REQUEST_CODE_PICK_IMAGE = 1001

    // Inflate the layout for this fragment
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Sketch button click -> directly navigate to Sketch screen without any background
        binding.sketchCard.setOnClickListener {
            findNavController().navigate(R.id.navigation_sketch)
        }

        // Trace button click -> open gallery to pick an image
        binding.traceCard.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, REQUEST_CODE_PICK_IMAGE)
        }

        return root
    }

    // Clean up ViewBinding when view is destroyed
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // Handle the result when user picks an image from gallery
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE_PICK_IMAGE && resultCode == android.app.Activity.RESULT_OK) {
            val imageUri = data?.data
            if (imageUri != null) {
                // Pass the selected image URI to Sketch screen
                val bundle = Bundle()
                bundle.putString("imageUri", imageUri.toString())
                findNavController().navigate(R.id.navigation_sketch, bundle)
            }
        }
    }
}
