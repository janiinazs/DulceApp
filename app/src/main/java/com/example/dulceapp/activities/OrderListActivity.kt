package com.example.dulceapp.activities

import android.app.AlertDialog
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dulceapp.R
import com.example.dulceapp.adapters.OrderAdapter
import com.example.dulceapp.databinding.ActivityOrderListBinding
import com.example.dulceapp.entities.Order
import com.example.dulceapp.services.FirebaseService

class OrderListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOrderListBinding
    private lateinit var orderAdapter: OrderAdapter
    private var allOrders: List<Order> = emptyList()
    private var currentFilter = "pendiente"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbarOrderList)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setupRecyclerView()
        setupFilterButtons()
        loadOrders()
    }

    private fun setupRecyclerView() {
        orderAdapter = OrderAdapter(
            orders = emptyList(),
            onMarkAsDeliveredClick = { order ->
                updateOrderStatus(order, "entregado")
            },
            onDeleteClick = { order ->
                showDeleteConfirmationDialog(order)
            }
        )
        binding.recyclerViewOrders.apply {
            layoutManager = LinearLayoutManager(this@OrderListActivity)
            adapter = orderAdapter
        }
    }

    private fun updateOrderStatus(order: Order, newStatus: String) {
        FirebaseService.updateOrderStatus(order.id, newStatus) { success, error ->
            if (success) {
                Toast.makeText(this, "Pedido marcado como 'entregado'", Toast.LENGTH_SHORT).show()
                loadOrders()
            } else {
                Toast.makeText(this, "Error al actualizar el estado: ${error ?: ""}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupFilterButtons() {
        binding.toggleButtonGroup.check(R.id.button_pending)
        binding.toggleButtonGroup.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                currentFilter = when (checkedId) {
                    R.id.button_pending -> "pendiente"
                    R.id.button_delivered -> "entregado"
                    else -> "pendiente"
                }
                filterAndDisplayOrders()
            }
        }
    }

    private fun loadOrders() {
        FirebaseService.fetchAllOrders { orders, errorMessage ->
            if (orders != null) {
                allOrders = orders.sortedWith(compareBy<Order> { !it.status.equals("pendiente", true) }.thenBy { it.deliveryDate })
                filterAndDisplayOrders()
            } else {
                Toast.makeText(this, "Error al cargar los pedidos: ${errorMessage ?: ""}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun filterAndDisplayOrders() {
        val filteredList = allOrders.filter { it.status.equals(currentFilter, ignoreCase = true) }
        orderAdapter.updateOrders(filteredList)
    }

    private fun showDeleteConfirmationDialog(order: Order) {
        AlertDialog.Builder(this)
            .setTitle("Eliminar Historial")
            .setMessage("¿Estás seguro de que quieres eliminar este pedido del historial? Esta acción no se puede deshacer.")
            .setPositiveButton("Sí, eliminar") { _, _ ->
                FirebaseService.deleteOrder(order.id) { success, error ->
                    if (success) {
                        Toast.makeText(this, "Pedido eliminado del historial", Toast.LENGTH_SHORT).show()
                        loadOrders()
                    } else {
                        Toast.makeText(this, "Error al eliminar: ${error ?: ""}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .setNegativeButton("No", null)
            .show()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}
