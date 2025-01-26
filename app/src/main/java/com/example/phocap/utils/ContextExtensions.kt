package com.example.phocap.utils

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File

fun Context.createImageFile(): File = File.createTempFile("JPEG", ".jpg", cacheDir)

fun Context.getUriForFile(photoFile: File): Uri =
    FileProvider.getUriForFile(this, "${this.packageName}.fileprovider", photoFile)

