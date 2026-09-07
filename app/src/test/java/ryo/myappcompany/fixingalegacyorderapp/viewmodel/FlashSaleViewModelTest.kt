@file:Suppress("NonAsciiCharacters")

package ryo.myappcompany.fixingalegacyorderapp.viewmodel

import android.util.Log
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import app.cash.turbine.test
import io.mockk.coVerify
import kotlinx.coroutines.test.runCurrent
import ryo.myappcompany.fixingalegacyorderapp.R
import ryo.myappcompany.fixingalegacyorderapp.domain.ApiConnectException
import ryo.myappcompany.fixingalegacyorderapp.domain.ProductInfo
import ryo.myappcompany.fixingalegacyorderapp.domain.PurchaseResult
import ryo.myappcompany.fixingalegacyorderapp.ui.FlashSaleUiEvent
import ryo.myappcompany.fixingalegacyorderapp.ui.FlashSaleUiState
import ryo.myappcompany.fixingalegacyorderapp.usecase.LoadProductDetailsUseCase
import ryo.myappcompany.fixingalegacyorderapp.usecase.PurchaseItemUseCase
import kotlin.time.Duration.Companion.milliseconds

@OptIn(ExperimentalCoroutinesApi::class)
class FlashSaleViewModelTest {

    // テスト対象のViewModel
    private lateinit var viewModel: FlashSaleViewModel

    // モック化するUseCase
    private lateinit var loadProductDetailsUseCase: LoadProductDetailsUseCase
    private lateinit var purchaseItemUseCase: PurchaseItemUseCase

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        // 依存するUseCaseのモック化
        loadProductDetailsUseCase = mockk()
        purchaseItemUseCase = mockk()

        // android.util.Logの静的モック化
        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 0
    }

    @After
    fun tearDown() {
        // Mainディスパッチャを元に戻す
        Dispatchers.resetMain()

        // 静的モック化の解除
        unmockkStatic(Log::class)
    }

    @Test
    fun 初期化時_商品情報の取得に成功した場合_Stateが正しく更新されること() =
        runTest(testDispatcher) {
            // 1. Given (事前準備): loadProductDetailsUseCase() が呼ばれた時、成功時の ProductInfo を返すようにモックの振る舞いを定義する
            val productInfo = ProductInfo(
                productName = "限定ワイヤレスイヤホン",
                stock = 3
            )
            coEvery { loadProductDetailsUseCase() } returns productInfo

            // 2. When (実行): テスト対象の ViewModel をインスタンス化する（initブロックが走り、loadProductDetails が実行される）
            viewModel = FlashSaleViewModel(loadProductDetailsUseCase, purchaseItemUseCase)
            advanceUntilIdle()

            // 3. Then (検証): viewModel.uiState.value の productName と stock が、モックで定義した値と一致しているか検証する
            Assert.assertEquals(
                FlashSaleUiState(
                    isLoading = false,
                    productName = productInfo.productName,
                    stock = productInfo.stock
                ),
                viewModel.uiState.value
            )
        }

    @Test
    fun 商品購入処理で通信エラーで発生した場合_正しくエラーイベントがChannelに流れること() =
        runTest(testDispatcher) {

            // 事前準備
            // 商品詳細情報取得のモック化
            val productInfo = ProductInfo(
                productName = "限定ワイヤレスイヤホン",
                stock = 3
            )
            coEvery { loadProductDetailsUseCase() } returns productInfo

            // 商品購入処理のモック化
            val purchaseResult = PurchaseResult.Failure.NetWorkError(ApiConnectException())
            coEvery { purchaseItemUseCase("item_123") } returns purchaseResult

            // 実行/検証
            viewModel = FlashSaleViewModel(loadProductDetailsUseCase, purchaseItemUseCase)

            viewModel.flashSaleUiEvent.test {
                viewModel.onBuyClicked()

                advanceUntilIdle()

                Assert.assertEquals(
                    FlashSaleUiEvent.Failure(R.string.msg_connection_error),
                    awaitItem()
                )

                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun 初期化時_商品情報の取得に失敗した場合_Stateがエラー状態に更新されエラーイベントが流れること() =
        runTest(testDispatcher) {

            // 事前準備
            coEvery { loadProductDetailsUseCase() } throws ApiConnectException()

            // 実行
            viewModel = FlashSaleViewModel(loadProductDetailsUseCase, purchaseItemUseCase)

            // 検証
            viewModel.flashSaleUiEvent.test {
                advanceUntilIdle() // testブロックの中で初期化処理を進める

                Assert.assertEquals(
                    FlashSaleUiEvent.Failure(R.string.msg_connection_error),
                    awaitItem()
                )

                cancelAndIgnoreRemainingEvents()
            }

            Assert.assertEquals(
                FlashSaleUiState(
                    isLoading = false,
                    productName = "-",
                    stock = 0
                ),
                viewModel.uiState.value
            )
        }

    @Test
    fun 商品購入処理時_すでにisLoadingがtrueの場合_処理が中断されUseCaseが呼ばれないこと() =
        runTest(testDispatcher) {

            // 事前準備
            // 商品詳細情報取得のモック化
            val productInfo = ProductInfo(
                productName = "限定ワイヤレスイヤホン",
                stock = 3
            )
            coEvery { loadProductDetailsUseCase() } returns productInfo

            // 商品購入処理のモック化
            val purchaseProductInfo = ProductInfo(
                productName = "限定ワイヤレスイヤホン",
                stock = 2
            )

            val purchaseResult = PurchaseResult.Success(purchaseProductInfo)
            coEvery { purchaseItemUseCase("item_123") } coAnswers {
                kotlinx.coroutines.delay(1000.milliseconds) // 遅延を仕込み、その間に2回目の呼び出しを実行想定
                purchaseResult
            }

            // 実行/検証
            viewModel = FlashSaleViewModel(loadProductDetailsUseCase, purchaseItemUseCase)
            advanceUntilIdle()

            viewModel.onBuyClicked() // 1回目の呼び出し
            runCurrent() // 最初の中断ポイント(PurchaseItemUseCaseの呼び出し)まで進める

            viewModel.onBuyClicked() // 重複呼び出し
            runCurrent() // 多重実行防止で弾かれるはず

            Assert.assertEquals(
                FlashSaleUiState(
                    isLoading = true, // 読み込み中の検証
                    productName = "限定ワイヤレスイヤホン",
                    stock = 3
                ),
                viewModel.uiState.value
            )

            advanceUntilIdle()

            // 重複呼び出しがブロックされていること(1度だけ要求されていること)
            coVerify(exactly = 1) { purchaseItemUseCase(any()) }
        }


    @Test
    fun 商品購入処理で_購入に成功した場合_Stateの在庫が更新され成功イベントが流れること() =
        runTest(testDispatcher) {
            // 事前準備
            // 商品詳細情報取得のモック化
            val productInfo = ProductInfo(
                productName = "限定ワイヤレスイヤホン",
                stock = 3
            )
            coEvery { loadProductDetailsUseCase() } returns productInfo

            // 商品購入処理のモック化
            val purchaseProductInfo = ProductInfo(
                productName = "限定ワイヤレスイヤホン",
                stock = 2
            )

            val purchaseResult = PurchaseResult.Success(purchaseProductInfo)
            coEvery { purchaseItemUseCase("item_123") } coAnswers {
                kotlinx.coroutines.delay(1000.milliseconds) // 遅延を仕込み、isLoadingの検証
                purchaseResult
            }

            // 実行/検証
            viewModel = FlashSaleViewModel(loadProductDetailsUseCase, purchaseItemUseCase)
            advanceUntilIdle()

            viewModel.onBuyClicked()
            runCurrent()

            Assert.assertEquals(
                FlashSaleUiState(
                    isLoading = true, // 読み込み中の検証
                    productName = "限定ワイヤレスイヤホン",
                    stock = 3
                ),
                viewModel.uiState.value
            )

            viewModel.flashSaleUiEvent.test {
                advanceUntilIdle()

                Assert.assertEquals(
                    FlashSaleUiEvent.Success(R.string.msg_purchase_complete),
                    awaitItem()
                )

                cancelAndIgnoreRemainingEvents()
            }

            Assert.assertEquals(
                FlashSaleUiState(
                    isLoading = false,
                    productName = "限定ワイヤレスイヤホン",
                    stock = 2
                ),
                viewModel.uiState.value
            )
        }

    @Test
    fun 商品購入処理で_在庫切れエラーが発生した場合_正しく在庫切れイベントがChannelに流れること() =
        runTest(testDispatcher) {
            // 事前準備
            // 商品詳細情報取得のモック化
            val productInfo = ProductInfo(
                productName = "限定ワイヤレスイヤホン",
                stock = 3
            )
            coEvery { loadProductDetailsUseCase() } returns productInfo

            // 商品購入処理のモック化
            val purchaseResult = PurchaseResult.Failure.OutOfStock
            coEvery { purchaseItemUseCase("item_123") } coAnswers {
                kotlinx.coroutines.delay(1000.milliseconds) // 遅延を仕込み、isLoadingの検証
                purchaseResult
            }

            // 実行/検証
            viewModel = FlashSaleViewModel(loadProductDetailsUseCase, purchaseItemUseCase)
            advanceUntilIdle()

            viewModel.onBuyClicked()
            runCurrent()

            Assert.assertEquals(
                FlashSaleUiState(
                    isLoading = true, // 読み込み中の検証
                    productName = "限定ワイヤレスイヤホン",
                    stock = 3
                ),
                viewModel.uiState.value
            )

            viewModel.flashSaleUiEvent.test {
                advanceUntilIdle()

                Assert.assertEquals(
                    FlashSaleUiEvent.Failure(R.string.msg_out_of_stock),
                    awaitItem()
                )

                cancelAndIgnoreRemainingEvents()
            }

            Assert.assertEquals(
                FlashSaleUiState(
                    isLoading = false,
                    productName = "限定ワイヤレスイヤホン",
                    stock = 3
                ),
                viewModel.uiState.value
            )

        }
}
