package ryo.myappcompany.fixingalegacyorderapp.ui

/**
 * イベントクラス
 */
sealed interface FlashSaleUiEvent {

    val message: Int

    /**
     * 完了メッセージ表示用
     */
    data class Success(
        override val message: Int
    ) : FlashSaleUiEvent

    /**
     * エラーメッセージ表示用
     */
    data class Failure(
        override val message: Int
    ) : FlashSaleUiEvent
}
