package com.dicoding.picodiploma.loginwithanimation

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.dicoding.picodiploma.loginwithanimation.databinding.LayoutLoadingBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

private const val timestampFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"

fun getTimeLineUploaded(context: Context, timeStamp: String): String {
    val currentTime = getCurrentDate()
    val uploadTime = parseUTCDate(timeStamp)
    val diff: Long = currentTime.time - uploadTime.time
    val second = diff / 1000
    val minutes = second / 60
    val hours = minutes / 60
    val days = hours / 24
    val label = when (minutes.toInt()) {
        0 -> "$second ${context.getString(R.string.second_ago)}"
        in 1..59 -> "$minutes ${context.getString(R.string.minutes_ago)}"
        in 60..1440 -> "$hours ${context.getString(R.string.hours_ago)}"
        else -> "$days ${context.getString(R.string.days_ago)}"
    }
    return label
}

fun parseUTCDate(timeStamp: String): Date =
    try {
        val formatter = SimpleDateFormat(timestampFormat, Locale.getDefault())
        formatter.timeZone = TimeZone.getTimeZone("UTC")
        formatter.parse(timeStamp) as Date
    } catch (e: ParseException) {
        getCurrentDate()
    }

fun getCurrentDate(): Date = Date()

fun ImageView.loadImage(url: String) {
    Glide.with(this.context)
        .load(url)
        .into(this)
}

fun View.enable() {
    isEnabled = true
}

fun View.disable() {
    isEnabled = false
}

fun Context.showToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
}

fun showAlertLoading(context: Context): AlertDialog {
    val binding = LayoutLoadingBinding.inflate(LayoutInflater.from(context), null, false)

    return MaterialAlertDialogBuilder(context, R.style.CustomDialogLoading)
        .setView(binding.root)
        .setCancelable(false)
        .create()
}

fun hideSoftKeyboard(context: Context, view: View) {
    (context.getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE) as InputMethodManager)
        .hideSoftInputFromWindow(view.windowToken, 0)
}

private const val FILENAME_FORMAT = "dd-MMM-yyyy"
val timeStamp: String = SimpleDateFormat(
    FILENAME_FORMAT,
    Locale.US
).format(System.currentTimeMillis())

fun createCustomTempFile(context: Context): File {
    val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    return File.createTempFile(timeStamp, ".jpg", storageDir)
}

fun uriToFile(selectedImg: Uri, context: Context): File {
    val contentResolver: ContentResolver = context.contentResolver
    val myFile = createCustomTempFile(context)
    val inputStream = contentResolver.openInputStream(selectedImg) as InputStream
    val outputStream: OutputStream = FileOutputStream(myFile)
    val buf = ByteArray(1024)
    var len: Int
    while (inputStream.read(buf).also { len = it } > 0) outputStream.write(buf, 0, len)
    outputStream.close()
    inputStream.close()
    return myFile
}

fun reduceFileImage(file: File): File {
    val bitmap = BitmapFactory.decodeFile(file.path)
    var compressQuality = 200
    var streamLength: Int
    do {
        val bmpStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, bmpStream)
        val bmpPicByteArray = bmpStream.toByteArray()
        streamLength = bmpPicByteArray.size
        compressQuality -= 5
    } while (streamLength > 1000000)
    bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, FileOutputStream(file))
    return file
}