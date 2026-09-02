package ryo.myappcompany.fixingalegacyorderapp.domain


/**
 * 購入処理結果
 */
sealed class PurchaseResult {

    /**
     * 購入処理成功
     */
    data class Success(
        val productInfo: ProductInfo
    ) : PurchaseResult()

    /**
     * 購入処理失敗
     */
    sealed class Failure : PurchaseResult() {
        /**
         * 在庫切れ
         */
        data object OutOfStock : Failure()  // 在庫切れ

        /**
         * 通信エラー
         *
         * @param exception
         */
        data class NetWorkError(val exception: ApiConnectException) : Failure() // 通信エラー
    }
}
