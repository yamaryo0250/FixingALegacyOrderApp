package ryo.myappcompany.fixingalegacyorderapp.manager

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import ryo.myappcompany.fixingalegacyorderapp.domain.Failure
import ryo.myappcompany.fixingalegacyorderapp.domain.ProductInfo
import ryo.myappcompany.fixingalegacyorderapp.domain.PurchaseResult
import ryo.myappcompany.fixingalegacyorderapp.domain.Success
import kotlin.time.Duration.Companion.milliseconds

/**
 * 注文関連処理のロジッククラス
 */
object OrderManager {

    val TAG: String = OrderManager::class.java.simpleName

    // 在庫数
    var currentStock = 3

    /**
     * 商品詳細情報取得
     *
     * @return 商品詳細情報(ドメインクラス)
     */
    suspend fun loadProductDetails(): ProductInfo = withContext(Dispatchers.IO) {
        Log.d(TAG, "ProductDetails loading...")

        // APIデータ取得のシミュレート
        delay(1000.milliseconds)
        return@withContext ProductInfo(
            productName = "限定ワイヤレスイヤホン",
            stock = currentStock
        )
    }

    /**
     * 商品購入処理
     *
     * @param itemId 商品ID
     * @return 購入処理結果
     */
    suspend fun purchaseItem(itemId: String): PurchaseResult = withContext(Dispatchers.IO) {
        Log.d(TAG, "Execute purchaseItem process.")

        try {
            // 通信遅延のシミュレート
            delay(1500.milliseconds)
        } catch (e: InterruptedException) {
            e.stackTrace
        }

        if (currentStock > 0) {
            currentStock--

            return@withContext Success
        } else {
            return@withContext Failure("在庫切れです。")
        }
    }
}
