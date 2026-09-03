package ryo.myappcompany.fixingalegacyorderapp.manager

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import ryo.myappcompany.fixingalegacyorderapp.domain.ProductInfo
import ryo.myappcompany.fixingalegacyorderapp.domain.PurchaseResult
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

/**
 * 注文関連処理のロジッククラス
 */
class OrderManager @Inject constructor() {

    companion object {
        private const val TAG: String = "OrderManager"
    }

    // 在庫数
    private var currentStock = 3

    // 在庫数管理への同時アクセス防止(排他制御)
    private val mutex = Mutex()

    /**
     * 商品詳細情報取得
     *
     * @return 商品詳細情報(ドメインクラス)
     */
    suspend fun loadProductDetails(): ProductInfo = withContext(Dispatchers.IO) {
        Log.d(TAG, "ProductDetails loading...")

        // APIデータ取得のシミュレート
        delay(1000.milliseconds)

        // 在庫管理数参照の排他制御
        return@withContext mutex.withLock {
            ProductInfo(
                productName = "限定ワイヤレスイヤホン",
                stock = currentStock
            )
        }
    }

    /**
     * 商品購入処理
     *
     * @param itemId 商品ID
     * @return 購入処理結果
     */
    suspend fun purchaseItem(itemId: String): PurchaseResult = withContext(Dispatchers.IO) {
        Log.d(TAG, "Execute purchaseItem process.")

        // 通信遅延のシミュレート
        delay(1500.milliseconds)

        // 在庫管理数操作の排他制御
        mutex.withLock {
            if (currentStock > 0) {
                currentStock--
                Log.d(TAG, "currentStock updated →$currentStock")

                return@withContext PurchaseResult.Success(
                    ProductInfo(
                        productName = "限定ワイヤレスイヤホン",
                        stock = currentStock
                    )
                )
            } else {
                Log.d(TAG, "Out of Stock..")

                return@withContext PurchaseResult.Failure.OutOfStock
            }
        }
    }
}
