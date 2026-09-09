package ryo.myappcompany.fixingalegacyorderapp.usecase

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import ryo.myappcompany.fixingalegacyorderapp.domain.ProductInfo
import ryo.myappcompany.fixingalegacyorderapp.repository.FlashSaleRepository

class LoadProductDetailsUseCaseTest {
    // テスト対象
    private lateinit var loadProductDetailsUseCase: LoadProductDetailsUseCase
    // モック化
    private lateinit var flashSaleRepository: FlashSaleRepository

    @Before
    fun setUp() {
        flashSaleRepository = mockk()
        loadProductDetailsUseCase = LoadProductDetailsUseCase(flashSaleRepository)
    }

    @Test
    fun UseCaseを呼んだ時_内部で正しくRepositoryのメソッドが呼ばれ_戻り値がそのまま返ること() =
        runTest {
            // 事前準備
            val productInfo = ProductInfo(
                productName = "限定ワイヤレスイヤホン",
                stock = 3
            )
            coEvery { flashSaleRepository.loadProductDetails() } returns productInfo

            // 実行/検証
            val result = loadProductDetailsUseCase()

            coVerify { flashSaleRepository.loadProductDetails() }

            Assert.assertEquals(
                productInfo,
                result
            )
        }
}
