package com.example.kiddrawingapp

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.core.view.get

class MainActivity : AppCompatActivity() {

    private var mImageButtonCurrentPaint: ImageButton? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var drawingView = findViewById<DrawingView>(R.id.drawing_view)
        drawingView.setBrushSize(10.toFloat())

        var paintColorsLl = findViewById<LinearLayout>(R.id.ll_paint_colors)
        mImageButtonCurrentPaint = paintColorsLl[0] as ImageButton
        mImageButtonCurrentPaint!!.setImageDrawable(
                ContextCompat.getDrawable(this, R.drawable.pallet_pressed)
        )

        var brushIb = findViewById<ImageButton>(R.id.ib_brush)
        brushIb.setOnClickListener{
            showBrushSizeChooserDialog()
        }
    }

    private fun showBrushSizeChooserDialog() {
        val brushDialog = Dialog(this)
        brushDialog.setContentView(R.layout.dialog_brush_size)
        brushDialog.setTitle("Brush Size: ")
        val xsmallBtn = brushDialog.findViewById<ImageButton>(R.id.ib_xs_brush)
        val xxsmallBtn = brushDialog.findViewById<ImageButton>(R.id.ib_xxs_brush)
        val smallBtn = brushDialog.findViewById<ImageButton>(R.id.ib_small_brush)
        val mediumBtn = brushDialog.findViewById<ImageButton>(R.id.ib_medium_brush)
        val largeBtn = brushDialog.findViewById<ImageButton>(R.id.ib_large_brush)
        val xlargeBtn = brushDialog.findViewById<ImageButton>(R.id.ib_xlarge_brush)
        var drawingView = findViewById<DrawingView>(R.id.drawing_view)
        xxsmallBtn.setOnClickListener{
            drawingView.setBrushSize((2.toFloat()))
            brushDialog.dismiss()
        }

        xsmallBtn.setOnClickListener{
            drawingView.setBrushSize((5.toFloat()))
            brushDialog.dismiss()
        }

        smallBtn.setOnClickListener{
            drawingView.setBrushSize((10.toFloat()))
            brushDialog.dismiss()
        }

        mediumBtn.setOnClickListener{
            drawingView.setBrushSize((20.toFloat()))
            brushDialog.dismiss()
        }

        largeBtn.setOnClickListener{
            drawingView.setBrushSize((30.toFloat()))
            brushDialog.dismiss()
        }

        xlargeBtn.setOnClickListener{
            drawingView.setBrushSize((40.toFloat()))
            brushDialog.dismiss()
        }

        brushDialog.show()
    }
}