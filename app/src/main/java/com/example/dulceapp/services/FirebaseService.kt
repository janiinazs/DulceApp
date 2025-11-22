package com.example.dulceapp.services

import android.net.Uri
import com.example.dulceapp.adapters.CartItem
import com.example.dulceapp.entities.Order
import com.example.dulceapp.entities.Product
import com.example.dulceapp.entities.Promotion
import com.example.dulceapp.entities.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID

object FirebaseService {

    val auth: FirebaseAuth by lazy { Firebase.auth }
    val firestore: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }
    private val storage: FirebaseStorage by lazy { FirebaseStorage.getInstance() }

    fun savePromotion(promotion: Promotion, callback: (Boolean, String?) -> Unit) {
        firestore.collection("promotions").add(promotion)
            .addOnSuccessListener { callback(true, null) }
            .addOnFailureListener { e -> callback(false, e.message) }
    }

    fun updatePromotion(promotionId: String, promotionData: Map<String, Any>, callback: (Boolean, String?) -> Unit) {
        firestore.collection("promotions").document(promotionId).update(promotionData)
            .addOnSuccessListener { callback(true, null) }
            .addOnFailureListener { e -> callback(false, e.message) }
    }

    fun deletePromotion(promotionId: String, callback: (Boolean, String?) -> Unit) {
        firestore.collection("promotions").document(promotionId).delete()
            .addOnSuccessListener { callback(true, null) }
            .addOnFailureListener { e -> callback(false, e.message) }
    }

    fun fetchAllPromotions(callback: (List<Promotion>?, String?) -> Unit) {
        firestore.collection("promotions").get()
            .addOnSuccessListener { querySnapshot ->
                val promotions = querySnapshot.toObjects(Promotion::class.java).mapIndexed { index, promo ->
                    promo.id = querySnapshot.documents[index].id
                    promo
                }
                callback(promotions, null)
            }.addOnFailureListener { e -> callback(null, e.message) }
    }

    fun uploadPromotionImage(imageUri: Uri, callback: (Boolean, String?) -> Unit) {
        val fileName = UUID.randomUUID().toString()
        storage.reference.child("promotion_images/$fileName").putFile(imageUri)
            .continueWithTask { task ->
                if (!task.isSuccessful) { task.exception?.let { throw it } }
                storage.reference.child("promotion_images/$fileName").downloadUrl
            }
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    callback(true, task.result.toString())
                } else {
                    callback(false, task.exception?.message)
                }
            }
    }

    fun logout() {
        auth.signOut()
    }

    fun findUserByEmail(email: String, callback: (User?) -> Unit) {
        firestore.collection("users").whereEqualTo("email", email).limit(1).get()
            .addOnSuccessListener { querySnapshot ->
                callback(querySnapshot.documents.firstOrNull()?.toObject(User::class.java))
            }
            .addOnFailureListener { callback(null) }
    }

    fun sendPasswordResetEmail(email: String, callback: (Boolean, Exception?) -> Unit) {
        auth.sendPasswordResetEmail(email)
            .addOnSuccessListener { callback(true, null) }
            .addOnFailureListener { e -> callback(false, e) }
    }

    fun fetchAllUsers(callback: (List<User>?, String?) -> Unit) {
        firestore.collection("users").get()
            .addOnSuccessListener { querySnapshot ->
                val list = querySnapshot.documents.mapNotNull { doc ->
                    doc.toObject(User::class.java)
                }
                callback(list, null)
            }.addOnFailureListener { e -> callback(null, e.message) }
    }

    fun fetchAllOrders(callback: (List<Order>?, String?) -> Unit) {
        firestore.collection("orders")
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val list = querySnapshot.toObjects(Order::class.java)
                callback(list, null)
            }.addOnFailureListener { e -> callback(null, e.message) }
    }

    fun deleteUser(uid: String, callback: (Boolean, String?) -> Unit) {
        firestore.collection("users").document(uid).delete()
            .addOnSuccessListener { callback(true, null) }
            .addOnFailureListener { e -> callback(false, e.message) }
    }

    fun removeProductFromCart(productName: String, callback: (Boolean, String?) -> Unit) {
        val firebaseUser = auth.currentUser ?: return callback(false, "No hay un usuario autenticado.")
        firestore.collection("users").document(firebaseUser.uid).collection("cart").document(productName).delete()
            .addOnSuccessListener { callback(true, null) }
            .addOnFailureListener { e -> callback(false, e.message) }
    }

    fun addProductToCart(promotion: Promotion, quantity: Int, callback: (Boolean, String?) -> Unit) {
        val firebaseUser = auth.currentUser ?: return callback(false, "No hay un usuario autenticado.")
        val cartItem = hashMapOf(
            "name" to promotion.name,
            "price" to promotion.price,
            "quantity" to quantity,
            "imageUrl" to promotion.imageUrl
        )
        firestore.collection("users").document(firebaseUser.uid).collection("cart").document(promotion.name)
            .set(cartItem, SetOptions.merge())
            .addOnSuccessListener { callback(true, null) }
            .addOnFailureListener { e -> callback(false, e.message) }
    }

    fun addProductToCart(product: Product, quantity: Int, callback: (Boolean, String?) -> Unit) {
        val firebaseUser = auth.currentUser ?: return callback(false, "No hay un usuario autenticado.")
        val cartItem = hashMapOf("name" to product.name, "price" to product.price, "quantity" to quantity, "imageUrl" to product.imageUrl)
        firestore.collection("users").document(firebaseUser.uid).collection("cart").document(product.name).set(cartItem, SetOptions.merge())
            .addOnSuccessListener { callback(true, null) }
            .addOnFailureListener { e -> callback(false, e.message) }
    }

    fun getCartItems(callback: (List<CartItem>?, String?) -> Unit) {
        val firebaseUser = auth.currentUser ?: return callback(null, "No hay un usuario autenticado.")
        firestore.collection("users").document(firebaseUser.uid).collection("cart").get()
            .addOnSuccessListener { querySnapshot ->
                val cartList = querySnapshot.documents.mapNotNull { doc ->
                    doc.toObject(CartItem::class.java)
                }
                callback(cartList, null)
            }.addOnFailureListener { e -> callback(null, e.message) }
    }

    fun uploadProfileImage(userId: String, imageUri: Uri, callback: (Boolean, String?) -> Unit) {
        val fileName = UUID.randomUUID().toString()
        val storageRef = storage.reference.child("profile_images/$userId/$fileName")

        storageRef.putFile(imageUri)
            .continueWithTask { task ->
                if (!task.isSuccessful) { task.exception?.let { throw it } }
                storageRef.downloadUrl
            }
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val downloadUri = task.result
                    callback(true, downloadUri.toString())
                } else {
                    callback(false, task.exception?.message)
                }
            }
    }

    fun updateUserProfile(userId: String, updatedData: Map<String, Any>, callback: (Boolean, String?) -> Unit) {
        firestore.collection("users").document(userId).update(updatedData)
            .addOnSuccessListener { callback(true, null) }
            .addOnFailureListener { e -> callback(false, e.message) }
    }

    fun fetchProductById(productId: String, callback: (Product?, String?) -> Unit) {
        firestore.collection("products").document(productId).get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val product = document.toObject(Product::class.java)
                    product?.id = document.id
                    callback(product, null)
                } else {
                    callback(null, "Producto no encontrado")
                }
            }
            .addOnFailureListener { e ->
                callback(null, e.message)
            }
    }

    fun fetchAllProducts(callback: (List<Product>?, String?) -> Unit) {
        firestore.collection("products").get()
            .addOnSuccessListener { querySnapshot ->
                val products = querySnapshot.toObjects(Product::class.java).mapIndexed { index, product ->
                    product.id = querySnapshot.documents[index].id
                    product
                }
                callback(products, null)
            }.addOnFailureListener { e -> callback(null, e.message) }
    }

    fun fetchProductsByCategory(category: String, callback: (List<Product>?, String?) -> Unit) {
        firestore.collection("products").whereEqualTo("category", category).get()
            .addOnSuccessListener { querySnapshot ->
                callback(querySnapshot.toObjects(Product::class.java), null)
            }.addOnFailureListener { e -> callback(null, e.message) }
    }

    fun uploadProductImage(imageUri: Uri, callback: (Boolean, String?) -> Unit) {
        val fileName = UUID.randomUUID().toString()
        storage.reference.child("product_images/$fileName").putFile(imageUri)
            .continueWithTask { task ->
                if (!task.isSuccessful) { task.exception?.let { throw it } }
                storage.reference.child("product_images/$fileName").downloadUrl
            }
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    callback(true, task.result.toString())
                } else {
                    callback(false, task.exception?.message)
                }
            }
    }

    fun saveProduct(product: Product, callback: (Boolean, String?) -> Unit) {
        firestore.collection("products").add(product)
            .addOnSuccessListener { callback(true, null) }
            .addOnFailureListener { e -> callback(false, e.message) }
    }

    fun updateProduct(productId: String, productData: Map<String, Any>, callback: (Boolean, String?) -> Unit) {
        firestore.collection("products").document(productId).update(productData)
            .addOnSuccessListener { callback(true, null) }
            .addOnFailureListener { e -> callback(false, e.message) }
    }

    fun deleteProduct(productId: String, callback: (Boolean, String?) -> Unit) {
        firestore.collection("products").document(productId).delete()
            .addOnSuccessListener { callback(true, null) }
            .addOnFailureListener { e -> callback(false, e.message) }
    }

    fun deleteOrder(orderId: String, callback: (Boolean, String?) -> Unit) {
        firestore.collection("orders").document(orderId).delete()
            .addOnSuccessListener { callback(true, null) }
            .addOnFailureListener { e -> callback(false, e.message) }
    }

    fun updateOrderStatus(orderId: String, newStatus: String, callback: (Boolean, String?) -> Unit) {
        firestore.collection("orders").document(orderId)
            .update("status", newStatus)
            .addOnSuccessListener { callback(true, null) }
            .addOnFailureListener { e -> callback(false, e.message) }
    }

    fun saveUser(user: User, callback: (Boolean, Exception?) -> Unit) {
        auth.createUserWithEmailAndPassword(user.email, user.password)
            .addOnSuccessListener { authResult ->
                val firebaseUser = authResult.user!!
                val firestoreUser = hashMapOf(
                    "uid" to firebaseUser.uid,
                    "username" to user.username,
                    "email" to user.email,
                    "role" to user.role,
                    "latitude" to user.latitude,
                    "longitude" to user.longitude,
                    "keyword" to user.keyword,
                    "profileImageUrl" to ""
                )
                firestore.collection("users").document(firebaseUser.uid).set(firestoreUser)
                    .addOnSuccessListener { callback(true, null) }
                    .addOnFailureListener { e -> callback(false, e) }
            }.addOnFailureListener { e -> callback(false, e) }
    }

    fun getCurrentUser(callback: (User?) -> Unit) {
        val firebaseUser = auth.currentUser ?: return callback(null)
        firestore.collection("users").document(firebaseUser.uid).get()
            .addOnSuccessListener { document ->
                callback(document.toObject(User::class.java))
            }.addOnFailureListener { callback(null) }
    }

    fun login(email: String, password: String, callback: (User?, String?) -> Unit) {
         auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener { authResult ->
                val firebaseUser = authResult.user!!
                firestore.collection("users").document(firebaseUser.uid).get()
                    .addOnSuccessListener { document ->
                        callback(document.toObject(User::class.java), null)
                    }.addOnFailureListener { e -> callback(null, "Error al buscar datos: ${e.message}") }
            }.addOnFailureListener { e -> callback(null, "Email o contraseña incorrectos.") }
    }

    fun saveOrder(orderData: Map<String, Any>, callback: (Boolean, String?) -> Unit) {
        firestore.collection("orders").add(orderData)
            .addOnSuccessListener { callback(true, null) }
            .addOnFailureListener { e -> callback(false, e.message) }
    }

    fun clearCart(callback: (Boolean, String?) -> Unit) {
        val firebaseUser = auth.currentUser ?: return callback(false, "No hay un usuario autenticado.")
        val cartRef = firestore.collection("users").document(firebaseUser.uid).collection("cart")
        cartRef.get().addOnSuccessListener { snapshot ->
            val batch = firestore.batch()
            snapshot.documents.forEach { batch.delete(it.reference) }
            batch.commit()
                .addOnSuccessListener { callback(true, null) }
                .addOnFailureListener { e -> callback(false, e.message) }
        }.addOnFailureListener { e -> callback(false, e.message) }
    }
}
