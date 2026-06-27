package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.database.AppDatabase
import com.example.data.model.*
import com.example.data.repository.FinanceRepository
import com.example.data.preferences.UserPreferences
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class FinanceViewModel(application: Application) : AndroidViewModel(application) {
    private val db = AppDatabase.getDatabase(application)
    private val repository = FinanceRepository(db.purchaseDao(), db.debtDao(), db.customerDao(), db.productDao(), db.saleDao())
    val userPreferences = UserPreferences(application)

    val purchases: StateFlow<List<Purchase>> = repository.allPurchases.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val debts: StateFlow<List<Debt>> = repository.allDebts.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )
    
    val customers: StateFlow<List<Customer>> = repository.allCustomers.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )
    
    val products: StateFlow<List<Product>> = repository.allProducts.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )
    
    val sales: StateFlow<List<Sale>> = repository.allSales.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val totalPurchasesAmount: StateFlow<Double> = repository.totalPurchasesAmount.map { it ?: 0.0 }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0.0
    )

    fun addPurchase(productName: String, category: String, quantity: Int, price: Double) {
        viewModelScope.launch {
            repository.insertPurchase(
                Purchase(
                    productName = productName,
                    category = category,
                    quantity = quantity,
                    price = price
                )
            )
        }
    }

    fun updatePurchase(purchase: Purchase) {
        viewModelScope.launch {
            repository.updatePurchase(purchase)
        }
    }

    fun deletePurchase(purchase: Purchase) {
        viewModelScope.launch {
            repository.deletePurchase(purchase)
        }
    }

    fun addDebt(personName: String, amount: Double, type: DebtType, notes: String, dueDate: Long? = null) {
        viewModelScope.launch {
            repository.insertDebt(
                Debt(
                    personName = personName,
                    amount = amount,
                    type = type,
                    notes = notes,
                    dueDate = dueDate
                )
            )
        }
    }

    fun updateDebt(debt: Debt) {
        viewModelScope.launch {
            repository.updateDebt(debt)
        }
    }

    fun deleteDebt(debt: Debt) {
        viewModelScope.launch {
            repository.deleteDebt(debt)
        }
    }

    fun addCustomer(name: String, phone: String, address: String) {
        viewModelScope.launch {
            repository.insertCustomer(Customer(name = name, phone = phone, address = address))
        }
    }

    fun updateCustomer(customer: Customer) {
        viewModelScope.launch { repository.updateCustomer(customer) }
    }

    fun deleteCustomer(customer: Customer) {
        viewModelScope.launch { repository.deleteCustomer(customer) }
    }

    fun addProduct(name: String, category: String, stockQuantity: Int, costPrice: Double, sellPrice: Double, barcode: String) {
        viewModelScope.launch {
            repository.insertProduct(Product(name = name, category = category, stockQuantity = stockQuantity, costPrice = costPrice, sellPrice = sellPrice, barcode = barcode))
        }
    }

    fun updateProduct(product: Product) {
        viewModelScope.launch { repository.updateProduct(product) }
    }

    fun deleteProduct(product: Product) {
        viewModelScope.launch { repository.deleteProduct(product) }
    }

    fun addSale(productId: Int, customerId: Int?, quantity: Int, totalAmount: Double, profit: Double) {
        viewModelScope.launch {
            repository.insertSale(Sale(productId = productId, customerId = customerId, quantity = quantity, totalAmount = totalAmount, profit = profit))
        }
    }
}
