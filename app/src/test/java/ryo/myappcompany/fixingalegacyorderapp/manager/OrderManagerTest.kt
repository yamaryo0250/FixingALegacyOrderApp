@file:Suppress("NonAsciiCharacters")

package ryo.myappcompany.fixingalegacyorderapp.manager

import android.util.Log
import io.mockk.every
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import ryo.myappcompany.fixingalegacyorderapp.domain.PurchaseResult

@OptIn(ExperimentalCoroutinesApi::class)
class OrderManagerTest {

    // テスト対象のクラス
    private lateinit var orderManager: OrderManager

    @Before
    fun setUp() {
        // テスト対象の初期化
        orderManager = OrderManager()

        // android.util.Logの静的モック化
        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 0
    }

    @After
    fun tearDown() {
        // 静的モック化の解除
        unmockkStatic(Log::class)
    }

    @Test
    fun purchaseItemを同時に100回呼び出しても_在庫の3個分だけ成功し_残りは在庫切れになること() =
        runBlocking {

            val requestCount = 100

            // TODO: 以下に、「100個のコルーチンを Dispatchers.Default 上で同時に立ち上げ、purchaseItem("item_123")を呼ぶ」処理を記述してください
            // ヒント1: List(requestCount) { ... } と async(Dispatchers.Default) を組み合わせる
            // ヒント2: awaitAll() で全ての結果(PurchaseResult)のリストを受け取る

            // 実行 (When)
             val results: List<PurchaseResult> = List(requestCount) {
                 async(Dispatchers.Default) {
                     orderManager.purchaseItem("item_123")
                 }
             }.awaitAll()

            // TODO: 結果を検証してください (Then)
            // 1. results の中で、PurchaseResult.Success の数がピッタリ「3回」であること
            // 2. results の中で、PurchaseResult.Failure.OutOfStock の数がピッタリ「97回」であること
            // (※NetworkErrorはシミュレート上発生しない前提として無視して構いません)
            val successCount = results.count {
                it is PurchaseResult.Success
            }

            val outOfStockCount= results.count {
                it is PurchaseResult.Failure.OutOfStock
            }

            Assert.assertEquals(3, successCount)
            Assert.assertEquals(97, outOfStockCount)
        }
}
