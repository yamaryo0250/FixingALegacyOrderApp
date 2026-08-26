package ryo.myappcompany.fixingalegacyorderapp.ui

/**
 * 購入処理のイベントクラス
 */
sealed interface PurchaseEvent {

    /**
     * 購入完了メッセージ表示用
     */
    data class Success(val message: String) : PurchaseEvent

    /**
     * 購入エラーメッセージ表示用
     */
    data class Failure(val message: String) : PurchaseEvent
}