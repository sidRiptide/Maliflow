package com.example.maliflow.view_models

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class AuthViewModel : ViewModel(){
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()

    fun signUp(email: String, password: String, name: String, phone: String, paybill: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userId = auth.currentUser?.uid
                    val userData = hashMapOf(
                        "name" to name,
                        "email" to email,
                        "phone" to phone,
                        "paybill" to paybill,
                        "createdAt" to FieldValue.serverTimestamp()
                    )

                    userId?.let {
                        db.collection("users").document(it).set(userData)
                            .addOnSuccessListener {
                                Log.d("Signup", "User profile saved")
                            }
                            .addOnFailureListener { e ->
                                Log.e("Signup", "Error saving profile", e)
                            }
                    }
                } else {
                    Log.e("Signup", "Signup failed", task.exception)
                }
            }
    }
    fun login(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("Login", "Login successful: ${auth.currentUser?.uid}")
                } else {
                    Log.e("Login", "Login failed", task.exception)
                }
            }
    }


}