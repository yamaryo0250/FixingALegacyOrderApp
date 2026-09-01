package ryo.myappcompany.fixingalegacyorderapp.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ryo.myappcompany.fixingalegacyorderapp.R
import ryo.myappcompany.fixingalegacyorderapp.domain.Failure
import ryo.myappcompany.fixingalegacyorderapp.domain.Success
import ryo.myappcompany.fixingalegacyorderapp.ui.FlashSaleUiState
import ryo.myappcompany.fixingalegacyorderapp.ui.PurchaseEvent
import ryo.myappcompany.fixingalegacyorderapp.usecase.LoadingProductDetailsUseCase
import ryo.myappcompany.fixingalegacyorderapp.usecase.PurchaseItemUseCase
import javax.inject.Inject

/**
 * ViewModel
 *
 * @param loadingProductDetailsUseCase 商品詳細情報取得UseCase
 * @param purchaseItemUseCase 商品購入処理UseCase
 */
@HiltViewModel
class FlashSaleViewModel @Inject constructor(
    private val loadingProductDetailsUseCase: LoadingProductDetailsUseCase,
    private val purchaseItemUseCase: PurchaseItemUseCase
) : ViewModel() {

    companion object {
        val TAG: String = FlashSaleViewModel::class.java.simpleName
    }

    private val _uiState = MutableStateFlow(FlashSaleUiState())
    val uiState: StateFlow<FlashSaleUiState> = _uiState

    private val _purchaseEvent = MutableSharedFlow<PurchaseEvent>()
    val purchaseEvent = _purchaseEvent.asSharedFlow()

    init {
        loadProductDetails()
    }

    /**
     * 商品詳細情報取得
     */
    private fun loadProductDetails() {

        viewModelScope.launch {
            Log.d(TAG, "Start load product details.")

            _uiState.update { it.copy(isLoading = true) }

            val productInfo = loadingProductDetailsUseCase()

            Log.d(TAG, "Complete load product details.")

            _uiState.update {
                it.copy(
                    isLoading = false,
                    productName = productInfo.productName,
                    stock = productInfo.stock
                )
            }
        }
    }

    /**
     * ボタン押下検知時処理
     */
    fun onBuyClicked() {

        viewModelScope.launch {
            Log.d(TAG, "Start purchase item process by clicked.")

            // 多重実行防止
            if (uiState.value.isLoading) {
                Log.d(TAG, "onBuyClicked cancel. Because already executed.")
                return@launch
            }

            _uiState.update { it.copy(isLoading = true) }

            val purchaseResult = purchaseItemUseCase("item_123")

            _uiState.update { it.copy(isLoading = false) }

            when (purchaseResult) {
                is Success -> {
                    _uiState.update { it.copy(stock = purchaseResult.productInfo.stock) }

                    _purchaseEvent.emit(
                        PurchaseEvent.Success(R.string.msg_purchase_complete)
                    )
                }

                is Failure -> {
                    _purchaseEvent.emit(
                        PurchaseEvent.Failure(purchaseResult.cause)
                    )
                }
            }
        }
    }
}
