package com.example.fin.cloud

import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID

fun uploadImageToStorage(imageUri: Uri, onComplete: (String?, String?) -> Unit) {
    val storageReference = FirebaseStorage.getInstance().reference
    val imageRef = storageReference.child("images/${UUID.randomUUID()}.jpg")

    imageRef.putFile(imageUri)
        .addOnSuccessListener { taskSnapshot ->
            imageRef.downloadUrl.addOnSuccessListener { uri ->
                onComplete(uri.toString(), null)
            }.addOnFailureListener { exception ->
                onComplete(null, exception.message)
            }
        }
        .addOnFailureListener { exception ->
            onComplete(null, exception.message)
        }
}