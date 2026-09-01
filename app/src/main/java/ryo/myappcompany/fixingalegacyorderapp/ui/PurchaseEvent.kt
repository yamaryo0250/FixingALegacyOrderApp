package ryo.myappcompany.fixingalegacyorderapp.ui

/**
 * 購入処理のイベントクラス
 */
sealed interface PurchaseEvent {

    val message: Int

    /**
     * 購入完了メッセージ表示用
     */
    data class Success(
        override val message: Int
    ) : PurchaseEvent

    /**
     * 購入エラーメッセージ表示用
     */
    data class Failure(
        override val message: Int
    ) : PurchaseEvent
}
