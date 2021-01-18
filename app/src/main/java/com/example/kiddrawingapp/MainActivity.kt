package com.example.kiddrawingapp

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.get
import java.util.jar.Manifest

class MainActivity : AppCompatActivity() {

    private var mImageButtonCurrentPaint: ImageButton? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var drawingView = findViewById<DrawingView>(R.id.drawing_view)
        drawingView.setBrushSize(10.toFloat())

        var paintColorsLl = findViewById<LinearLayout>(R.id.ll_paint_colors)
        mImageButtonCurrentPaint = paintColorsLl[1] as ImageButton
        mImageButtonCurrentPaint!!.setImageDrawable(
                ContextCompat.getDrawable(this, R.drawable.pallet_pressed)
        )

        var brushIb = findViewById<ImageButton>(R.id.ib_brush)
        brushIb.setOnClickListener{
            showBrushSizeChooserDialog()
        }

        var galleryIb = findViewById<ImageButton>(R.id.ib_gallery)
        galleryIb.setOnClickListener {
            if(isReadStorageAllowed()) {
                var pickPhotoIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)

                startActivityForResult(pickPhotoIntent, GALLERY)
            }
            else {
                requestStoragePermission()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK) {
            if(requestCode == GALLERY) {
                try {
                    if(data!!.data != null) {
                        var backgroundIv = findViewById<ImageView>(R.id.iv_background)
                        backgroundIv.visibility = View.VISIBLE
                        backgroundIv.setImageURI(data!!.data)
                    }
                    else {
                        Toast.makeText(this, "Error in parsing the image or its curroupted.", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
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

    fun paintClicked(view: View) {
        if(view !== mImageButtonCurrentPaint) {
            val imageButton = view as ImageButton
            var drawingView = findViewById<DrawingView>(R.id.drawing_view)
            val colorTag = imageButton.tag.toString()
            drawingView.setColor(colorTag)
            imageButton.setImageDrawable(
                    ContextCompat.getDrawable(this, R.drawable.pallet_pressed)
            )
            mImageButtonCurrentPaint!!.setImageDrawable(
                    ContextCompat.getDrawable(this, R.drawable.pallet_normal)
            )
            mImageButtonCurrentPaint = view
        }
    }

    private fun requestStoragePermission() {
        if(ActivityCompat.shouldShowRequestPermissionRationale(this, arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE).toString())) {
            Toast.makeText(this, "Need permission to add a Background", Toast.LENGTH_SHORT).show()
        }
        ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE), STORAGE_PERMISSION_CODE)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == STORAGE_PERMISSION_CODE) {
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission granted now you can read the storage files.", Toast.LENGTH_LONG).show()
            }
            else {
                Toast.makeText(this, "Oops you just denied the permission.", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun isReadStorageAllowed(): Boolean {
        val result = ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
        return result == PackageManager.PERMISSION_GRANTED
    }

    companion object {
        private const val STORAGE_PERMISSION_CODE = 1
        private const val GALLERY = 2
    }
}