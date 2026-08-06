package ryo.myappcompany.fixingalegacyorderapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ryo.myappcompany.fixingalegacyorderapp.legacy.LegacyOrderManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class FlashSaleState(
    val isLoading: Boolean = false,
    val productName: String = "",
    val stock: Int = 0,
    val message: String = ""
)

class FlashSaleViewModel : ViewModel() {
    private val legacyManager = LegacyOrderManager.getInstance()

    private val _uiState = MutableStateFlow(FlashSaleState())
    val uiState: StateFlow<FlashSaleState> = _uiState

    init {
        loadProductDetails()

        // レガシーシステムからの在庫更新通知を受け取る
        legacyManager.addStockListener { newStock ->
            _uiState.value = _uiState.value.copy(stock = newStock)
        }
    }

    private fun loadProductDetails() {
        _uiState.value = _uiState.value.copy(isLoading = true)

        viewModelScope.launch {
            // APIデータ取得のシミュレート
            delay(1000)
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                productName = "限定ワイヤレスイヤホン"
            )
        }
    }

    fun onBuyClicked() {
        _uiState.value = _uiState.value.copy(isLoading = true)

        legacyManager.purchaseItem("item_123", object : LegacyOrderManager.PurchaseCallback {
            override fun onSuccess() {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    message = "購入が完了しました！"
                )
            }

            override fun onFailure(error: String) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    message = error
                )
            }
        })
    }
}
