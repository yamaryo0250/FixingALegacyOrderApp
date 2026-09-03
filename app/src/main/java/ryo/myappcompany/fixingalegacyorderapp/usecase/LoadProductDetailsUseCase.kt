package ryo.myappcompany.fixingalegacyorderapp.usecase

import ryo.myappcompany.fixingalegacyorderapp.domain.ProductInfo
import ryo.myappcompany.fixingalegacyorderapp.repository.FlashSaleRepository
import javax.inject.Inject

/**
 * 商品詳細情報取得UseCase
 *
 * @param flashSaleRepository
 */
class LoadProductDetailsUseCase @Inject constructor(
    private val flashSaleRepository: FlashSaleRepository
) {
    suspend operator fun invoke(): ProductInfo {
        return flashSaleRepository.loadProductDetails()
    }
}
