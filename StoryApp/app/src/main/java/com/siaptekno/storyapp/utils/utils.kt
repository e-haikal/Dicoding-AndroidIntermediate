package com.siaptekno.storyapp.utils

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.core.content.FileProvider
import androidx.exifinterface.media.ExifInterface
import com.siaptekno.storyapp.BuildConfig
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Constants for file handling
private const val MAXIMAL_SIZE = 1_000_000 // Maximum file size in bytes for compressed images
private const val FILENAME_FORMAT = "yyyyMMdd_HHmmss" // File name format with timestamp
private val timeStamp: String = SimpleDateFormat(FILENAME_FORMAT, Locale.US).format(Date()) // Current timestamp

/**
 * Creates a URI for saving an image. Uses MediaStore for API 29+ or FileProvider for older APIs.
 */
fun getImageUri(context: Context): Uri {
    var uri: Uri? = null
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, "$timeStamp.jpg") // Image file name
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg") // File type
            put(MediaStore.MediaColumns.RELATIVE_PATH, "Pictures/MyCamera/") // Save path
        }
        uri = context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
    }
    return uri ?: getImageUriForPreQ(context) // Fallback for pre-Q devices
}

/**
 * Creates a URI for saving an image using FileProvider for pre-API 29 devices.
 */
fun getImageUriForPreQ(context: Context): Uri {
    val filesDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    val imageFile = File(filesDir, "/MyCamera/$timeStamp.jpg").apply {
        if (parentFile?.exists() == false) parentFile?.mkdir() // Ensure the directory exists
    }
    return FileProvider.getUriForFile(
        context,
        "${BuildConfig.APPLICATION_ID}.fileprovider",
        imageFile
    )
}

/**
 * Converts a URI to a File object by copying its content to a temporary file.
 */
fun uriToFile(imageUri: Uri, context: Context): File {
    val myFile = createCustomTempFile(context) // Create temporary file
    val inputStream = context.contentResolver.openInputStream(imageUri) as InputStream
    val outputStream = FileOutputStream(myFile)
    val buffer = ByteArray(1024)
    var length: Int
    while (inputStream.read(buffer).also { length = it } > 0) {
        outputStream.write(buffer, 0, length)
    }
    outputStream.close()
    inputStream.close()
    return myFile
}

/**
 * Creates a temporary file in the external cache directory for storing intermediate results.
 */
fun createCustomTempFile(context: Context): File {
    val filesDir = context.externalCacheDir
    return File.createTempFile(timeStamp, ".jpg", filesDir)
}

/**
 * Compresses an image file to reduce its size below the specified MAXIMAL_SIZE.
 */
fun File.reduceFileImage(): File {
    val file = this
    val bitmap = BitmapFactory.decodeFile(file.path).getRotatedBitmap(file) // Adjust rotation
    var compressQuality = 100 // Start with maximum quality
    var streamLength: Int
    do {
        val bmpStream = ByteArrayOutputStream()
        bitmap?.compress(Bitmap.CompressFormat.JPEG, compressQuality, bmpStream)
        val bmpPicByteArray = bmpStream.toByteArray()
        streamLength = bmpPicByteArray.size
        compressQuality -= 5 // Gradually reduce quality
    } while (streamLength > MAXIMAL_SIZE)
    bitmap?.compress(Bitmap.CompressFormat.JPEG, compressQuality, FileOutputStream(file))
    return file
}

/**
 * Checks and adjusts the orientation of a bitmap based on EXIF metadata.
 */
fun Bitmap.getRotatedBitmap(file: File): Bitmap? {
    val orientation = ExifInterface(file).getAttributeInt(
        ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED
    )
    return when (orientation) {
        ExifInterface.ORIENTATION_ROTATE_90 -> rotateImage(this, 90F)
        ExifInterface.ORIENTATION_ROTATE_180 -> rotateImage(this, 180F)
        ExifInterface.ORIENTATION_ROTATE_270 -> rotateImage(this, 270F)
        ExifInterface.ORIENTATION_NORMAL -> this
        else -> this
    }
}

/**
 * Rotates a bitmap by the specified angle.
 */
fun rotateImage(source: Bitmap, angle: Float): Bitmap {
    val matrix = Matrix().apply {
        postRotate(angle) // Apply rotation
    }
    return Bitmap.createBitmap(source, 0, 0, source.width, source.height, matrix, true)
}
