package ryo.myappcompany.fixingalegacyorderapp.repository

import ryo.myappcompany.fixingalegacyorderapp.domain.ApiConnectException
import ryo.myappcompany.fixingalegacyorderapp.domain.ProductInfo
import ryo.myappcompany.fixingalegacyorderapp.domain.PurchaseResult
import ryo.myappcompany.fixingalegacyorderapp.manager.OrderManager
import javax.inject.Inject

/**
 * フラッシュセールリポジトリ 実装
 */
class FlashSaleRepositoryImpl @Inject constructor(
    private val orderManager: OrderManager
) : FlashSaleRepository {

    /**
     * 商品詳細情報取得
     *
     * @return 商品詳細情報(ドメインクラス)
     */
    override suspend fun loadProductDetails(): ProductInfo {
        return try {
            orderManager.loadProductDetails()
        } catch (e: Exception) {
            if (e is kotlinx.coroutines.CancellationException) {
                throw e
            }

            throw ApiConnectException(e)
        }
    }

    /**
     * 商品購入処理
     *
     * @param itemId 商品ID
     * @return 購入処理結果
     */
    override suspend fun purchaseItem(itemId: String): PurchaseResult {
        return try {
            orderManager.purchaseItem(itemId)
        } catch (e: Exception) {
            if (e is kotlinx.coroutines.CancellationException) {
                throw e
            }

            throw ApiConnectException(e)
        }
    }
}
