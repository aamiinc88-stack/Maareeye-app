package com.example.data.repository

import com.example.data.dao.*
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow

class FinanceRepository(
    private val purchaseDao: PurchaseDao,
    private val debtDao: DebtDao,
    private val customerDao: CustomerDao,
    private val productDao: ProductDao,
    private val saleDao: SaleDao
) {
    val allPurchases: Flow<List<Purchase>> = purchaseDao.getAllPurchases()
    val allDebts: Flow<List<Debt>> = debtDao.getAllDebts()
    val totalPurchasesAmount: Flow<Double?> = purchaseDao.getTotalPurchasesAmount()
    val allCustomers: Flow<List<Customer>> = customerDao.getAllCustomers()
    val allProducts: Flow<List<Product>> = productDao.getAllProducts()
    val allSales: Flow<List<Sale>> = saleDao.getAllSales()

    suspend fun insertPurchase(purchase: Purchase) = purchaseDao.insertPurchase(purchase)
    suspend fun updatePurchase(purchase: Purchase) = purchaseDao.updatePurchase(purchase)
    suspend fun deletePurchase(purchase: Purchase) = purchaseDao.deletePurchase(purchase)

    suspend fun insertDebt(debt: Debt) = debtDao.insertDebt(debt)
    suspend fun updateDebt(debt: Debt) = debtDao.updateDebt(debt)
    suspend fun deleteDebt(debt: Debt) = debtDao.deleteDebt(debt)

    suspend fun insertCustomer(customer: Customer) = customerDao.insertCustomer(customer)
    suspend fun updateCustomer(customer: Customer) = customerDao.updateCustomer(customer)
    suspend fun deleteCustomer(customer: Customer) = customerDao.deleteCustomer(customer)

    suspend fun insertProduct(product: Product) = productDao.insertProduct(product)
    suspend fun updateProduct(product: Product) = productDao.updateProduct(product)
    suspend fun deleteProduct(product: Product) = productDao.deleteProduct(product)

    suspend fun insertSale(sale: Sale) = saleDao.insertSale(sale)
    suspend fun updateSale(sale: Sale) = saleDao.updateSale(sale)
    suspend fun deleteSale(sale: Sale) = saleDao.deleteSale(sale)
}
