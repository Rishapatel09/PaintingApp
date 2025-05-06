package com.example.drawingapp.custom

import android.content.Context
import android.graphics.*
import android.net.Uri
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

// Custom View class for freehand drawing
class DrawingView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    // Current path being drawn
    private var drawPath = Path()

    // Paint object describing how to draw (color, stroke size, etc.)
    private var drawPaint = Paint().apply {
        color = Color.BLACK
        isAntiAlias = true
        strokeWidth = 10f
        style = Paint.Style.STROKE
        strokeJoin = Paint.Join.ROUND
        strokeCap = Paint.Cap.ROUND
    }

    // List of all completed paths and their paint styles
    private val paths = ArrayList<Pair<Path, Paint>>()

    // Optional background image (for tracing)
    private var backgroundBitmap: Bitmap? = null

    // Called whenever the view needs to be redrawn
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Draw background image first, if any
        backgroundBitmap?.let {
            canvas.drawBitmap(it, 0f, 0f, null)
        }

        // Draw all saved paths
        for ((path, paint) in paths) {
            canvas.drawPath(path, paint)
        }

        // Draw the current in-progress path
        canvas.drawPath(drawPath, drawPaint)
    }

    // Handle touch events (drawing with finger)
    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> drawPath.moveTo(event.x, event.y)  // Start new path
            MotionEvent.ACTION_MOVE -> drawPath.lineTo(event.x, event.y)  // Extend path
            MotionEvent.ACTION_UP -> {
                // Save the completed path and reset for next path
                paths.add(Pair(Path(drawPath), Paint(drawPaint)))
                drawPath.reset()
            }
        }
        invalidate()  // Redraw the view
        return true
    }

    // Create a bitmap image of the current drawing
    fun getBitmap(): Bitmap {
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        draw(canvas)
        return bitmap
    }

    // Change brush color
    fun setBrushColor(color: Int) {
        drawPaint.color = color
    }

    // Change brush size
    fun setBrushSize(size: Float) {
        drawPaint.strokeWidth = size
    }

    // Set an imported image as the background for tracing
    fun setBackgroundImage(uri: Uri) {
        try {
            val inputStream = context.contentResolver.openInputStream(uri)
            inputStream?.use {
                val originalBitmap = BitmapFactory.decodeStream(it)
                if (originalBitmap != null) {
                    backgroundBitmap = Bitmap.createScaledBitmap(
                        originalBitmap,
                        width.takeIf { it > 0 } ?: 1,
                        height.takeIf { it > 0 } ?: 1,
                        true
                    )
                    invalidate()  // Redraw with new background
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // Undo the last drawn path
    fun undo() {
        if (paths.isNotEmpty()) {
            paths.removeAt(paths.size - 1)
            invalidate()
        }
    }
}
