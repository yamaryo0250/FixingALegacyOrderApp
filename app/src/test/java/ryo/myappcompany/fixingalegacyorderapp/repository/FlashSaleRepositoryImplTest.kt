@file:Suppress("NonAsciiCharacters")

package ryo.myappcompany.fixingalegacyorderapp.repository

import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import ryo.myappcompany.fixingalegacyorderapp.domain.ApiConnectException
import ryo.myappcompany.fixingalegacyorderapp.domain.ProductInfo
import ryo.myappcompany.fixingalegacyorderapp.domain.PurchaseResult
import ryo.myappcompany.fixingalegacyorderapp.manager.OrderManager
import kotlin.test.assertFailsWith

class FlashSaleRepositoryImplTest {
    // テスト対象
    private lateinit var repository: FlashSaleRepository
    // モック化
    private lateinit var orderManager: OrderManager

    @Before
    fun setUp() {
        orderManager = mockk()
        repository = FlashSaleRepositoryImpl(orderManager)
    }

    @Test
    fun 商品詳細情報取得_OrderManagerがProductInfoを返した場合_そのまま返すこと() = runTest {
        // 事前準備
        val productInfo = ProductInfo(
            productName = "限定ワイヤレスイヤホン",
            stock = 3
        )
        coEvery { orderManager.loadProductDetails() } returns productInfo

        // 実行/検証
        Assert.assertEquals(productInfo, repository.loadProductDetails())
    }

    @Test
    fun 商品詳細情報取得_OrderManagerがCancellationExceptionを投げた場合_そのまま再スローこと() = runTest {
        // 事前準備
        coEvery { orderManager.loadProductDetails() } throws CancellationException()

        // 実行/検証
        assertFailsWith<CancellationException> {
            repository.loadProductDetails()
        }
    }

    @Test
    fun 商品詳細情報取得_OrderManagerがその他のExceptionを投げた場合_ApiConnectExceptionにラップしてスローすること() =
        runTest {
            // 事前準備
            coEvery { orderManager.loadProductDetails() } throws Exception("Test")

            // 実行/検証
            assertFailsWith<ApiConnectException> {
                repository.loadProductDetails()
            }
        }

    @Test
    fun 商品購入処理_OrderManagerがPurchaseResultを返した場合_そのまま返すこと() = runTest {
        // 事前準備
        val productInfo = ProductInfo(
            productName = "限定ワイヤレスイヤホン",
            stock = 3
        )

        val purchaseResult = PurchaseResult.Success(productInfo)

        coEvery { orderManager.purchaseItem("item_123") } returns purchaseResult

        // 実行/検証
        Assert.assertEquals(
            PurchaseResult.Success(productInfo),
            repository.purchaseItem("item_123")
        )
    }

    @Test
    fun 商品購入処理_OrderManagerがCancellationExceptionを投げた場合_そのまま再スローすること() = runTest {
        // 事前準備
        coEvery { orderManager.purchaseItem("item_123") } throws CancellationException()

        // 実行/検証
        assertFailsWith<CancellationException> {
            repository.purchaseItem("item_123")
        }
    }

    @Test
    fun 商品購入処理_OrderManagerがその他のExceptionを投げた場合_NetWorkErrorにラップして返すこと() =
        runTest {
            // 事前準備
            coEvery { orderManager.purchaseItem("item_123") } throws Exception("Test")

            // 実行/検証
            val result = repository.purchaseItem("item_123")

            // 型の検証
            Assert.assertTrue(result is PurchaseResult.Failure.NetWorkError)

            val netWorkErrorResult = result as PurchaseResult.Failure.NetWorkError

            // Exceptionの中身検証
            Assert.assertEquals(
                "Test",
                netWorkErrorResult.exception.cause?.message
            )
        }
}
