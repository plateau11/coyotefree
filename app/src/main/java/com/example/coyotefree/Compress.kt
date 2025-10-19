package com.example.coyotefree;

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.default
import id.zelory.compressor.constraint.destination
import id.zelory.compressor.constraint.format
import id.zelory.compressor.constraint.quality
import id.zelory.compressor.constraint.resolution
import id.zelory.compressor.constraint.size
import id.zelory.compressor.loadBitmap
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException
import java.text.DecimalFormat
import java.util.Random
import java.util.UUID
import kotlin.math.log10
import kotlin.math.pow

class Compress : AppCompatActivity() {

    // 🔹 Declare global variables (class-level)
    private lateinit var chooseImageButton: Button
    private lateinit var compressImageButton: Button
    private lateinit var customCompressImageButton: Button
    private lateinit var compressedImageView: ImageView
    private lateinit var compressedSizeTextView: TextView
    private lateinit var actualImageView: ImageView
    private lateinit var actualSizeTextView: TextView

    // Global variables to store width and height
    private var imageWidth: Int = 1280
    private var imageHeight: Int = 720
    private var imageSizeInBytes: Long = 2097152

    companion object {
        private const val PICK_IMAGE_REQUEST = 1
    }

    private var actualImage: File? = null
    private var compressedImage: File? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_compress)

        clearAppCache()

        // 🔹 Initialize views
        chooseImageButton = findViewById(R.id.chooseImageButton)
        compressImageButton = findViewById(R.id.compressImageButton)
        customCompressImageButton = findViewById(R.id.customCompressImageButton)
        compressedImageView = findViewById(R.id.compressedImageView)
        compressedSizeTextView = findViewById(R.id.compressedSizeTextView)
        actualImageView = findViewById(R.id.actualImageView)
        actualSizeTextView = findViewById(R.id.actualSizeTextView)

        actualImageView.setBackgroundColor(getRandomColor())
        clearImage()
        setupClickListener()
    }

    private fun setupClickListener() {
        chooseImageButton.setOnClickListener {
            //clearAppCache()
            clearCoyoteCompressedFolder()
            chooseImage() }
        compressImageButton.setOnClickListener { compressImage() }
        customCompressImageButton.setOnClickListener {
            showDimensionsDialog()
        }
    }

    private fun chooseImage() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    private fun compressImage() {
        actualImage?.let { imageFile ->
            lifecycleScope.launch {
                // Default compression
                compressedImage = Compressor.compress(this@Compress, imageFile){
                    val publicPicturesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                    val randomName = "img_" + UUID.randomUUID().toString().take(6) + ".jpg"
                    val outputDir = File(publicPicturesDir, "Coyote_CompressedImages")
                    outputDir.mkdirs()
                    val outputFile = File(outputDir, randomName)
                    default()
                    destination(outputFile)
                }
                setCompressedImage()
            }
        } ?: showError(getString(R.string.Pleasechoose))
    }

    private fun customCompressImage() {

        actualImage?.let { imageFile ->
            lifecycleScope.launch {
                // Default compression with custom destination file
                /*compressedImage = Compressor.compress(this@MainActivity, imageFile) {
                    default()
                    getExternalFilesDir(Environment.DIRECTORY_PICTURES)?.also {
                        val file = File("${it.absolutePath}${File.separator}my_image.${imageFile.extension}")
                        destination(file)
                    }
                }*/

                // Full custom
                compressedImage = Compressor.compress(this@Compress, imageFile) {
                    resolution(imageWidth, imageHeight)
                    quality(80)
                    format(Bitmap.CompressFormat.WEBP)
                    size(imageSizeInBytes) // 2 MB
                    val publicPicturesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                    val randomName = "img_" + UUID.randomUUID().toString().take(6) + ".jpg"
                    val outputDir = File(publicPicturesDir, "Coyote_CompressedImages")
                    outputDir.mkdirs()
                    val outputFile = File(outputDir, randomName)
                    //default()
                    destination(outputFile)

                }
                setCompressedImage()

            }
        } ?: showError(getString(R.string.Pleasechoose))
    }

    private fun setCompressedImage() {
        compressedImage?.let {
            compressedImageView.setImageBitmap(BitmapFactory.decodeFile(it.absolutePath))
            compressedSizeTextView.text = String.format("Size : %s", getReadableFileSize(it.length()))
            Toast.makeText(this, getString(R.string.compressedimagesavedin) + it.path, Toast.LENGTH_LONG).show()
            //Toast.makeText(this, "Height and width: " + imageHeight+" "+imageWidth, Toast.LENGTH_LONG).show()
            Log.d("Compressor", getString(R.string.compressedimagesavedin) + it.path)
            //clearAppCache()
        }
    }


    private fun showDimensionsDialog() {

        //imageWidth = 612
        //imageHeight = 816
        //imageSizeInBytes = 2097152

        // Inflate the dialog layout
        val dialogView = layoutInflater.inflate(R.layout.dialog_dimensions, null)

        // Get references to the EditText fields in the dialog
        val editWidth = dialogView.findViewById<EditText>(R.id.editWidth)
        val editHeight = dialogView.findViewById<EditText>(R.id.editHeight)
        val editSize = dialogView.findViewById<EditText>(R.id.editSize) // For size input
        val sizeUnitSpinner = dialogView.findViewById<Spinner>(R.id.sizeUnitSpinner) // Spinner for KB/MB selection

        // Spinner setup (MB/KB)
        val sizeUnits = arrayOf("KB", "MB")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, sizeUnits)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        sizeUnitSpinner.adapter = adapter

        // Build and show the dialog
        val dialog = AlertDialog.Builder(this)
            .setTitle(getString(R.string.EnterImageDimension))
            .setView(dialogView)
            .setPositiveButton("OK") { _, _ ->
                // Parse the input values and save to global variables
                val widthInput = editWidth.text.toString()
                val heightInput = editHeight.text.toString()
                val sizeInput = editSize.text.toString()
                val selectedSizeUnit = sizeUnitSpinner.selectedItem.toString()

                // Case 1: If the user enters all fields (width, height, and size)
                if (widthInput.isNotEmpty() && heightInput.isNotEmpty() && sizeInput.isNotEmpty()) {
                    val width = widthInput.toIntOrNull()
                    val height = heightInput.toIntOrNull()
                    val size = sizeInput.toLongOrNull()

                    if (width != null && height != null && size != null && width > 0 && height > 0 && size > 0) {
                        imageWidth = width
                        imageHeight = height
                        imageSizeInBytes = if (selectedSizeUnit == "MB") {
                            size * 1024 * 1024 // Convert MB to bytes
                        } else {
                            size * 1024 // Convert KB to bytes
                        }
                        // Proceed with compression or any other logic
                        Toast.makeText(this, "Width: $imageWidth, Height: $imageHeight, Size: $imageSizeInBytes bytes", Toast.LENGTH_SHORT).show()
                        customCompressImage()
                    } else {
                        Toast.makeText(this, getString(R.string.pleaseentervalid), Toast.LENGTH_SHORT).show()
                    }
                }
                // Case 2: If the user only enters width and height
                else if (widthInput.isNotEmpty() && heightInput.isNotEmpty() && sizeInput.isEmpty()) {
                    val width = widthInput.toIntOrNull()
                    val height = heightInput.toIntOrNull()

                    if (width != null && height != null && width > 0 && height > 0) {
                        imageWidth = width
                        imageHeight = height
                        // Proceed with compression or any other logic
                        Toast.makeText(this, "Width: $imageWidth, Height: $imageHeight", Toast.LENGTH_SHORT).show()
                        customCompressImage()
                    } else {
                        Toast.makeText(this, getString(R.string.pleaseentervalidsecond), Toast.LENGTH_SHORT).show()
                    }
                }
                // Case 3: If the user only enters size
                else if (widthInput.isEmpty() && heightInput.isEmpty() && sizeInput.isNotEmpty()) {
                    val size = sizeInput.toLongOrNull()

                    if (size != null && size > 0) {
                        imageSizeInBytes = if (selectedSizeUnit == "MB") {
                            size * 1024 * 1024 // Convert MB to bytes
                        } else {
                            size * 1024 // Convert KB to bytes
                        }
                        // Proceed with compression or any other logic
                        Toast.makeText(this, "Size: $imageSizeInBytes bytes", Toast.LENGTH_SHORT).show()
                        customCompressImage()
                    } else {
                        Toast.makeText(this, getString(R.string.pleaseentervalid3), Toast.LENGTH_SHORT).show()
                    }
                }
                // Case 4: Invalid combination of fields (neither empty nor valid)
                else {
                    Toast.makeText(this, getString(R.string.pleaseentervalid), Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton(getString(R.string.cancel), null)
            .create()

        dialog.show()
    }




    private fun clearImage() {
        actualImageView.setBackgroundColor(getRandomColor())
        compressedImageView.setImageDrawable(null)
        compressedImageView.setBackgroundColor(getRandomColor())
        compressedSizeTextView.text = getString(R.string.size)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK) {
            if (data == null) {
                showError(getString(R.string.failedtoopen))
                return
            }
            try {
                actualImage = FileUtil.from(this, data.data)?.also {
                    actualImageView.setImageBitmap(loadBitmap(it))
                    actualSizeTextView.text = String.format("Size : %s", getReadableFileSize(it.length()))
                    clearImage()
                }
            } catch (e: IOException) {
                showError(getString(R.string.failedtoread))
                e.printStackTrace()
            }
        }
    }

    private fun clearAppCache() {
        try {
            val cacheDir = cacheDir
            cacheDir?.let {
                deleteDir(it)
                //Toast.makeText(this, "Cache cleared", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, getString(R.string.failetoclear), Toast.LENGTH_SHORT).show()
        }
    }

    // Recursive function to delete files/folders
    private fun deleteDir(dir: File?): Boolean {
        if (dir != null && dir.isDirectory) {
            val children = dir.list()
            if (children != null) {
                for (child in children) {
                    val success = deleteDir(File(dir, child))
                    if (!success) return false
                }
            }
        }
        return dir?.delete() ?: false
    }


    private fun showError(errorMessage: String) {
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
    }

    private fun getRandomColor() = Random().run {
        Color.argb(100, nextInt(256), nextInt(256), nextInt(256))
    }

    private fun getReadableFileSize(size: Long): String {
        if (size <= 0) {
            return "0"
        }
        val units = arrayOf("B", "KB", "MB", "GB", "TB")
        val digitGroups = (log10(size.toDouble()) / log10(1024.0)).toInt()
        return DecimalFormat("#,##0.#").format(size / 1024.0.pow(digitGroups.toDouble())) + " " + units[digitGroups]
    }

    private fun clearCoyoteCompressedFolder() {
        val coyoteFolder = File(filesDir, "CoyoteFolderCompressed")
        if (coyoteFolder.exists() && coyoteFolder.isDirectory()) {
            val files = coyoteFolder.listFiles()

            // Check if files exist in the folder
            if (files != null) {
                for (file in files) {
                    if (file.exists() && file.isFile()) {
                        // Delete the file
                        val deleted = file.delete()
                        if (deleted) {
                            Log.d("ClearFolder", "Deleted: " + file.getName())
                        } else {
                            Log.d("ClearFolder", "Failed to delete: " + file.getName())
                        }
                    }
                }
            }

            // Optionally, you can delete the folder itself if it's empty
            if (coyoteFolder.listFiles().size == 0) {
                val folderDeleted = coyoteFolder.delete()
                if (folderDeleted) {
                    Log.d("ClearFolder", "Folder deleted")
                }
            }
        } else {
            Log.d("ClearFolder", "CoyoteFolder does not exist")
        }
    }

    override fun attachBaseContext(newBase: Context) {
        /*val prefs = newBase.getSharedPreferences("langpreference", MODE_PRIVATE)
        val langPref = prefs.getString("lpref", "en")
        //String langCode = langPref.equalsIgnoreCase("es") ? "es" : "en";
        //String langCode = getLocaleCodeFromPreference(langPref);
        val context = LocaleHelper.wrap(newBase, langPref)
        super.attachBaseContext(context)*/

        super.attachBaseContext(LocaleHelper.wrap(newBase));
    }

}