package com.example.maliflow.view_models

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import com.example.maliflow.data.Transaction
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.toObject
import java.text.SimpleDateFormat
import java.util.*

class TransactionViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    val transactions: SnapshotStateList<Transaction> = mutableStateListOf()
    private var listenerRegistration: ListenerRegistration? = null

    fun addDummyTransaction(amount: Double, customer: String, type: String) {
        val userId = auth.currentUser?.uid ?: return

        val transaction = hashMapOf(
            "amount" to amount,
            "customer" to customer,
            "type" to type,
            "date" to SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date())
        )

        db.collection("users")
            .document(userId)
            .collection("transactions")
            .add(transaction)
    }
    fun startListening() {
        val userId = auth.currentUser?.uid ?: return

        listenerRegistration = db.collection("users")
            .document(userId)
            .collection("transactions")
            .addSnapshotListener { snapshot, _ ->
                transactions.clear()
                snapshot?.documents?.forEach { doc ->
                    val t = doc.toObject<Transaction>()
                    if (t != null) transactions.add(t)
                }
            }
    }

    override fun onCleared() {
        super.onCleared()
        listenerRegistration?.remove()
    }
}
