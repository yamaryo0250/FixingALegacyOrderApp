package ryo.myappcompany.fixingalegacyorderapp.repository

import ryo.myappcompany.fixingalegacyorderapp.domain.ProductInfo
import ryo.myappcompany.fixingalegacyorderapp.domain.PurchaseResult

/**
 * フラッシュセールリポジトリ interface
 */
interface FlashSaleRepository {

    /**
     * 商品詳細情報取得
     *
     * @return 商品詳細情報(ドメインクラス)
     */
    suspend fun loadProductDetails(): ProductInfo

    /**
     * 商品購入処理
     *
     * @param itemId 商品ID
     * @return 購入処理結果
     */
    suspend fun purchaseItem(itemId: String): PurchaseResult
}
