package ryo.myappcompany.fixingalegacyorderapp.domain

/**
 * 購入処理結果
 */
sealed class PurchaseResult

/**
 * 購入処理成功
 */
class Success(
    val productInfo: ProductInfo
) : PurchaseResult()

/**
 * 購入処理失敗
 *
 * @param cause 処理の失敗内容
 */
class Failure(
    val cause: Int
) : PurchaseResult()
