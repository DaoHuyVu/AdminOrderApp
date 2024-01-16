package com.example.adminorderapp.util

import android.content.ContentResolver
import android.net.Uri
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody


class UriResolver {
    companion object{
        fun getPartFromUri(uri : Uri,contentResolver: ContentResolver,fileName : String) : MultipartBody.Part{
            val inputStream = contentResolver.openInputStream(uri)
            val byteArray = inputStream?.readBytes()
            val mimeType = MediaType.parse(contentResolver.getType(uri)!!)!!
            val fileExtension = getFileExtensionFromMimeType(mimeType)
            val body = RequestBody.create(mimeType,byteArray!!)
            val part = MultipartBody.Part.createFormData("image","$fileName.$fileExtension",body)
            inputStream.close()
            return part
        }
        private fun getFileExtensionFromMimeType(mediaType: MediaType) : String{
            return mediaType.toString().substringAfter("image/")
        }
    }

}