package ryo.myappcompany.fixingalegacyorderapp.ui

/**
 * UIの状態クラス
 *
 * @param isLoading 読み込み中判定
 * @param productName 商品名
 * @param stock 在庫数
 */
data class FlashSaleUiState(
    val isLoading: Boolean,
    val productName: String,
    val stock: Int
)
