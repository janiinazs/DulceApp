package com.example.dulceapp.activities

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.dulceapp.R
import com.example.dulceapp.databinding.ActivityCheckoutBinding
import com.example.dulceapp.services.FirebaseService
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import java.util.Calendar

class CheckoutActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCheckoutBinding
    private var selectedDate: Calendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCheckoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbarCheckout)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        receiveAndShowData()
        setupPaymentOptions()
        setupConfirmButton()
        setupDateTimePickers()
    }

    private fun receiveAndShowData() {
        val userName = intent.getStringExtra("USER_NAME")
        val totalPrice = intent.getStringExtra("TOTAL_PRICE")

        binding.editTextName.setText(userName)
        binding.textViewCheckoutTotal.text = totalPrice
    }
    
    private fun setupDateTimePickers() {
        binding.editTextDeliveryDate.setOnClickListener {
            showDatePickerDialog()
        }

        binding.editTextDeliveryTime.setOnClickListener {
            showTimePickerDialog()
        }
    }
    
    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                selectedDate.set(Calendar.YEAR, year)
                selectedDate.set(Calendar.MONTH, month)
                selectedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                binding.editTextDeliveryDate.setText("$dayOfMonth/${month + 1}/$year")
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.datePicker.minDate = System.currentTimeMillis() - 1000
        datePickerDialog.show()
    }

    private fun showTimePickerDialog() {
        val calendar = Calendar.getInstance()
        val timePickerDialog = TimePickerDialog(
            this,
            { _, hourOfDay, minute ->
                selectedDate.set(Calendar.HOUR_OF_DAY, hourOfDay)
                selectedDate.set(Calendar.MINUTE, minute)
                binding.editTextDeliveryTime.setText(String.format("%02d:%02d", hourOfDay, minute))
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true
        )
        timePickerDialog.show()
    }

    private fun setupPaymentOptions() {
        binding.buttonConfirmFinal.isEnabled = false

        binding.radioGroupPayment.setOnCheckedChangeListener { _, checkedId ->
            if (checkedId == R.id.radioButton_transfer) {
                binding.cardBankInfo.visibility = View.VISIBLE
                binding.cardCreditCardInfo.visibility = View.GONE
            } else {
                binding.cardBankInfo.visibility = View.GONE
                binding.cardCreditCardInfo.visibility = View.VISIBLE
            }
        }

        binding.checkboxTerms.setOnCheckedChangeListener { _, isChecked ->
            binding.buttonConfirmFinal.isEnabled = isChecked
        }
    }

    // --- LÓGICA ACTUALIZADA PARA GUARDAR LA FECHA DE ENTREGA ---
    private fun setupConfirmButton() {
        binding.buttonConfirmFinal.setOnClickListener {
            val name = binding.editTextName.text.toString().trim()
            val address = binding.editTextAddress.text.toString().trim()
            val phone = binding.editTextPhone.text.toString().trim()
            val deliveryDate = binding.editTextDeliveryDate.text.toString()
            val deliveryTime = binding.editTextDeliveryTime.text.toString()
            val review = binding.editTextReview.text.toString().trim()
            val rating = binding.ratingBar.rating
            val total = binding.textViewCheckoutTotal.text.toString()
            val paymentMethod = if (binding.radioButtonCard.isChecked) "Tarjeta" else "Transferencia"

            if (name.isEmpty() || address.isEmpty() || phone.isEmpty() || deliveryDate.isEmpty() || deliveryTime.isEmpty()) {
                Toast.makeText(this, "Por favor, completa todos los campos, incluyendo fecha y hora de entrega.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            FirebaseService.getCartItems { cartItems, error ->
                if (cartItems != null && cartItems.isNotEmpty()) {
                    val orderData = hashMapOf(
                        "customerName" to name,
                        "address" to address,
                        "phone" to phone,
                        "total" to total,
                        "paymentMethod" to paymentMethod,
                        "review" to review,
                        "rating" to rating,
                        "createdAt" to FieldValue.serverTimestamp(),
                        "deliveryDate" to Timestamp(selectedDate.time), // <-- FECHA DE ENTREGA GUARDADA
                        "items" to cartItems,
                        "status" to "pendiente"
                    )

                    FirebaseService.saveOrder(orderData) { success, saveError ->
                        if (success) {
                            FirebaseService.clearCart { cleared, _ ->
                                if (cleared) {
                                    Toast.makeText(this, "¡Pedido realizado! Gracias por tu compra.", Toast.LENGTH_LONG).show()
                                    val intent = Intent(this, HomeActivity::class.java)
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                                    startActivity(intent)
                                    finish()
                                } else {
                                    Toast.makeText(this, "Pedido guardado, pero no se pudo vaciar el carrito.", Toast.LENGTH_LONG).show()
                                }
                            }
                        } else {
                            Toast.makeText(this, "Error al realizar el pedido: ${saveError ?: "Inténtalo de nuevo"}", Toast.LENGTH_LONG).show()
                        }
                    }
                } else {
                    Toast.makeText(this, "No puedes realizar un pedido con el carrito vacío.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}
