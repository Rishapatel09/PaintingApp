package com.example.drawingapp.ui.sketch

import android.app.AlertDialog
import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.drawingapp.R
import com.example.drawingapp.custom.DrawingView
import java.io.OutputStream

class SketchFragment : Fragment() {

    private lateinit var drawingView: DrawingView


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?


    ): View {
        val view = inflater.inflate(R.layout.fragment_sketch, container, false)

        drawingView = view.findViewById(R.id.drawing_view)

        view.findViewById<View>(R.id.color_black).setOnClickListener {
            drawingView.setBrushColor(Color.BLACK)
        }
        view.findViewById<View>(R.id.color_red).setOnClickListener {
            drawingView.setBrushColor(Color.RED)
        }
        view.findViewById<View>(R.id.color_green).setOnClickListener {
            drawingView.setBrushColor(Color.GREEN)
        }
        view.findViewById<View>(R.id.color_blue).setOnClickListener {
            drawingView.setBrushColor(Color.BLUE)
        }
        view.findViewById<View>(R.id.color_yellow).setOnClickListener {
            drawingView.setBrushColor(Color.YELLOW)
        }
        view.findViewById<View>(R.id.color_purple).setOnClickListener {
            drawingView.setBrushColor(Color.MAGENTA)
        }

        // Undo Button functionality
        val undoButton = view.findViewById<ImageButton>(R.id.ib_undo)
        undoButton.setOnClickListener {
            drawingView.undo()
        }

        // Save button functionality
        val saveButton = view.findViewById<ImageButton>(R.id.ib_save)
        saveButton.setOnClickListener {
            val bitmap = drawingView.getBitmap()

            val filename = "Drawing_${System.currentTimeMillis()}.png"
            val fos: OutputStream?
            val resolver = requireContext().contentResolver
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
                put(
                    MediaStore.MediaColumns.RELATIVE_PATH,
                    Environment.DIRECTORY_PICTURES + "/KidsDrawings"
                )
            }

            val imageUri =
                resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
            fos = imageUri?.let { resolver.openOutputStream(it) }

            if (fos != null) {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
                fos.flush()
                fos.close()
                Toast.makeText(requireContext(), "Saved to Gallery!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Save failed!", Toast.LENGTH_SHORT).show()
            }
        }

        // Share button functionality
        val shareButton = view.findViewById<ImageButton>(R.id.ib_share)
        shareButton.setOnClickListener {
            val bitmap = drawingView.getBitmap()

            val filename = "Drawing_${System.currentTimeMillis()}.png"
            val resolver = requireContext().contentResolver
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
                put(
                    MediaStore.MediaColumns.RELATIVE_PATH,
                    Environment.DIRECTORY_PICTURES + "/KidsDrawings"
                )
            }
            val imageUri =
                resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

            if (imageUri != null) {
                val outputStream = resolver.openOutputStream(imageUri)
                outputStream?.let { fos ->
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
                    fos.flush()
                    fos.close()

                    val shareIntent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_STREAM, imageUri)
                        type = "image/png"
                        flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                    }
                    startActivity(Intent.createChooser(shareIntent, "Share Drawing via"))
                } ?: run {
                    Toast.makeText(requireContext(), "Sharing failed!", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(requireContext(), "Sharing failed!", Toast.LENGTH_SHORT).show()
            }

        }

        val brushButton = view.findViewById<ImageButton>(R.id.ib_brush)
        brushButton.setOnClickListener {
            val brushSizes = arrayOf("Small", "Medium", "Large")

            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("Select Brush Size")
            builder.setItems(brushSizes) { dialog, which ->
                when (which) {
                    0 -> drawingView.setBrushSize(10f) // Small
                    1 -> drawingView.setBrushSize(20f) // Medium
                    2 -> drawingView.setBrushSize(30f) // Large
                }
            }
            builder.show()
        }

        drawingView = view.findViewById(R.id.drawing_view)

        val imageUriString = arguments?.getString("imageUri")
        imageUriString?.let {
            val uri = android.net.Uri.parse(it)
            drawingView.post {
                drawingView.setBackgroundImage(uri)
            }
        }

        return view
    }
}


