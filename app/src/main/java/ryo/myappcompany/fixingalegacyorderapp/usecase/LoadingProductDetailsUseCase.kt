package ryo.myappcompany.fixingalegacyorderapp.usecase

import ryo.myappcompany.fixingalegacyorderapp.domain.ProductInfo
import ryo.myappcompany.fixingalegacyorderapp.repository.FlashSaleRepository

/**
 * 商品詳細情報取得UseCase
 *
 * @param flashSaleRepository
 */
class LoadingProductDetailsUseCase(
    private val flashSaleRepository: FlashSaleRepository
) {
    suspend operator fun invoke(): ProductInfo {
        return flashSaleRepository.loadProductDetails()
    }
}
