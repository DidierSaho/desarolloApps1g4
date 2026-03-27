package com.example.clase3

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class ProductViewModel(application: Application) : AndroidViewModel(application) {
    private val productDao = AppDatabase.getDatabase(application).productDao()
    val allProducts: Flow<List<Product>> = productDao.getAllProducts()

    fun insert(product: Product) = viewModelScope.launch {
        productDao.insertProduct(product)
    }

    fun update(product: Product) = viewModelScope.launch {
        productDao.updateProduct(product)
    }

    fun delete(product: Product) = viewModelScope.launch {
        productDao.deleteProduct(product)
    }

    suspend fun getProductById(id: Int): Product? {
        return productDao.getProductById(id)
    }
}
