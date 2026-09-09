@file:Suppress("NonAsciiCharacters")

package ryo.myappcompany.fixingalegacyorderapp.usecase

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import ryo.myappcompany.fixingalegacyorderapp.domain.ProductInfo
import ryo.myappcompany.fixingalegacyorderapp.domain.PurchaseResult
import ryo.myappcompany.fixingalegacyorderapp.repository.FlashSaleRepository

class PurchaseItemUseCaseTest {
    // テスト対象
    private lateinit var purchaseItemUseCase: PurchaseItemUseCase
    // モック化
    private lateinit var flashSaleRepository: FlashSaleRepository

    @Before
    fun setUp() {
        flashSaleRepository = mockk()
        purchaseItemUseCase = PurchaseItemUseCase(flashSaleRepository)
    }

    @Test
    fun UseCaseを呼んだ時_内部で正しくRepositoryのメソッドが呼ばれ_戻り値がそのまま返ること() =
        runTest {
            // 事前準備
            val productInfo = ProductInfo(
                productName = "限定ワイヤレスイヤホン",
                stock = 3
            )
            val purchaseResult = PurchaseResult.Success(productInfo)

            coEvery { flashSaleRepository.purchaseItem("item_123") } returns purchaseResult

            // 実行/検証
            val result = purchaseItemUseCase("item_123")

            coVerify { flashSaleRepository.purchaseItem("item_123") }

            Assert.assertEquals(
                PurchaseResult.Success(productInfo),
                result
            )
        }
}
