package ryo.myappcompany.fixingalegacyorderapp.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ryo.myappcompany.fixingalegacyorderapp.R
import ryo.myappcompany.fixingalegacyorderapp.domain.PurchaseResult
import ryo.myappcompany.fixingalegacyorderapp.ui.FlashSaleUiState
import ryo.myappcompany.fixingalegacyorderapp.ui.FlashSaleUiEvent
import ryo.myappcompany.fixingalegacyorderapp.usecase.LoadProductDetailsUseCase
import ryo.myappcompany.fixingalegacyorderapp.usecase.PurchaseItemUseCase
import javax.inject.Inject

/**
 * ViewModel
 *
 * @param loadProductDetailsUseCase 商品詳細情報取得UseCase
 * @param purchaseItemUseCase 商品購入処理UseCase
 */
@HiltViewModel
class FlashSaleViewModel @Inject constructor(
    private val loadProductDetailsUseCase: LoadProductDetailsUseCase,
    private val purchaseItemUseCase: PurchaseItemUseCase
) : ViewModel() {

    companion object {
        private const val TAG: String = "FlashSaleViewModel"
    }

    private val _uiState = MutableStateFlow(FlashSaleUiState())
    val uiState: StateFlow<FlashSaleUiState> = _uiState

    private val _flashSaleUiEvent = Channel<FlashSaleUiEvent>(Channel.BUFFERED)
    val flashSaleUiEvent = _flashSaleUiEvent.receiveAsFlow()

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

            try {
                val productInfo = loadProductDetailsUseCase()

                Log.d(TAG, "Complete load product details.")

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        productName = productInfo.productName,
                        stock = productInfo.stock
                    )
                }
            } catch (e: Exception) {
                if (e is kotlinx.coroutines.CancellationException) {
                    throw e
                }

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        productName = "-"
                    )
                }

                _flashSaleUiEvent.send(
                    FlashSaleUiEvent.Failure(R.string.msg_connection_error)
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
                is PurchaseResult.Success -> {
                    _uiState.update { it.copy(stock = purchaseResult.productInfo.stock) }

                    _flashSaleUiEvent.send(
                        FlashSaleUiEvent.Success(R.string.msg_purchase_complete)
                    )
                }

                is PurchaseResult.Failure.OutOfStock -> {
                    _flashSaleUiEvent.send(
                        FlashSaleUiEvent.Failure(R.string.msg_out_of_stock)
                    )
                }

                is PurchaseResult.Failure.NetWorkError -> {
                    _flashSaleUiEvent.send(
                        FlashSaleUiEvent.Failure(R.string.msg_connection_error)
                    )
                }
            }
        }
    }
}
