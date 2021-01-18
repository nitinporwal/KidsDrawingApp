package com.example.kiddrawingapp

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.media.MediaScannerConnection
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.get
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
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

        var undoIb = findViewById<ImageButton>(R.id.ib_undo)
        undoIb.setOnClickListener {
            drawingView.onClickUndo()
        }

        var saveIb = findViewById<ImageButton>(R.id.ib_save)
        var drawingViewContainerFl = findViewById<FrameLayout>(R.id.fl_drawing_view_container)
        saveIb.setOnClickListener {
            if(isReadStorageAllowed()) {
                BitmapAsyncTask(getBitmapFromView(drawingViewContainerFl)).execute()
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

    private fun getBitmapFromView(view: View) : Bitmap {
        val returnedBitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(returnedBitmap)
        val bgDrawable = view.background
        if(bgDrawable != null) {
            bgDrawable.draw(canvas)
        }
        else {
            canvas.drawColor(Color.WHITE)
        }
        view.draw(canvas)
        return returnedBitmap
    }

    private inner class BitmapAsyncTask(val mBitmap: Bitmap): AsyncTask<Any, Void, String>(){

        private lateinit var mProgressDialog: Dialog
        override fun onPreExecute() {
            super.onPreExecute()
            showProgressDialog()
        }
        override fun doInBackground(vararg params: Any?): String {
            var result = ""
            if(mBitmap != null) {
                try {
                    val bytes = ByteArrayOutputStream()
                    mBitmap.compress(Bitmap.CompressFormat.PNG, 90, bytes)
                    val f = File(externalCacheDir!!.absoluteFile.toString() + File.separator + "KidsDrawingApp_" + System.currentTimeMillis()/1000 + ".png")
                    val fos = FileOutputStream(f)
                    fos.write(bytes.toByteArray())
                    fos.close()
                    result = f.absolutePath
                } catch (e: Exception) {
                    result = ""
                    e.printStackTrace()
                }
            }
            return result
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            if(!result!!.isEmpty()) {
                Toast.makeText(this@MainActivity, "File saved successfully : $result", Toast.LENGTH_SHORT).show()
            }
            else {
                Toast.makeText(this@MainActivity, "Something went wrong while saving the file.", Toast.LENGTH_SHORT).show()
            }
            cancelProgressDialog()
            MediaScannerConnection.scanFile(this@MainActivity, arrayOf(result), null) {
                path, uri -> val shareInternt = Intent()
                shareInternt.action = Intent.ACTION_SEND
                shareInternt.putExtra(Intent.EXTRA_STREAM, uri)
                shareInternt.type = "image/png"
                startActivity(
                        Intent.createChooser(shareInternt, "Share")
                )
            }
        }

        private fun showProgressDialog() {
            mProgressDialog = Dialog(this@MainActivity)
            mProgressDialog.setContentView(R.layout.dialog_custom_progress)
            mProgressDialog.show()
        }

        private fun cancelProgressDialog() {
            mProgressDialog.dismiss()
        }

    }

    companion object {
        private const val STORAGE_PERMISSION_CODE = 1
        private const val GALLERY = 2
    }
}