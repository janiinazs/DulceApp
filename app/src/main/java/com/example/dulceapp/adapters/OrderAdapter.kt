package com.example.dulceapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.dulceapp.databinding.ItemOrderBinding
import com.example.dulceapp.entities.Order
import java.text.SimpleDateFormat
import java.util.Locale

class OrderAdapter(
    private var orders: List<Order>,
    private val onMarkAsDeliveredClick: (Order) -> Unit,
    private val onDeleteClick: (Order) -> Unit // Listener para el borrado
) : RecyclerView.Adapter<OrderAdapter.OrderViewHolder>() {

    fun updateOrders(newOrders: List<Order>) {
        this.orders = newOrders
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val binding = ItemOrderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OrderViewHolder(binding, onMarkAsDeliveredClick, onDeleteClick)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        holder.bind(orders[position])
    }

    override fun getItemCount(): Int = orders.size

    class OrderViewHolder(
        private val binding: ItemOrderBinding,
        private val onMarkAsDeliveredClick: (Order) -> Unit,
        private val onDeleteClick: (Order) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(order: Order) {
            binding.textViewCustomerName.text = order.customerName
            binding.textViewCustomerPhone.text = "Teléfono: ${order.phone}"
            binding.textViewOrderTotal.text = "Total: ${order.total}"

            val simpleDateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())

            order.createdAt?.toDate()?.let {
                binding.textViewOrderDate.text = "Fecha Pedido: ${simpleDateFormat.format(it)}"
            }
            order.deliveryDate?.toDate()?.let {
                binding.textViewDeliveryDate.text = "Fecha Entrega: ${simpleDateFormat.format(it)}"
            }

            binding.textViewOrderAddress.text = "Dirección: ${order.address}"

            val itemsText = order.items.joinToString(separator = "\n") { "- ${it.quantity} x ${it.name}" }
            binding.textViewOrderItems.text = "Productos:\n$itemsText"

            // --- LÓGICA DE VISIBILIDAD DE BOTONES ---
            if (order.status.equals("pendiente", ignoreCase = true)) {
                binding.buttonMarkDelivered.visibility = View.VISIBLE
                binding.buttonDeleteOrder.visibility = View.GONE
                binding.buttonMarkDelivered.setOnClickListener {
                    onMarkAsDeliveredClick(order)
                }
            } else if (order.status.equals("entregado", ignoreCase = true)) {
                binding.buttonMarkDelivered.visibility = View.GONE
                binding.buttonDeleteOrder.visibility = View.VISIBLE
                binding.buttonDeleteOrder.setOnClickListener {
                    onDeleteClick(order)
                }
            } else {
                // Para cualquier otro estado, ocultar ambos
                binding.buttonMarkDelivered.visibility = View.GONE
                binding.buttonDeleteOrder.visibility = View.GONE
            }
        }
    }
}
