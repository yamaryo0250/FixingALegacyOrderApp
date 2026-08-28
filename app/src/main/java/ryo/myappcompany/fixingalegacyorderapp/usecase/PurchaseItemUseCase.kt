package ryo.myappcompany.fixingalegacyorderapp.usecase

import ryo.myappcompany.fixingalegacyorderapp.domain.PurchaseResult
import ryo.myappcompany.fixingalegacyorderapp.repository.FlashSaleRepository

/**
 * 商品購入処理UseCase
 *
 * @param flashSaleRepository
 */
class PurchaseItemUseCase(
    private val flashSaleRepository: FlashSaleRepository
) {
    suspend operator fun invoke(itemId: String): PurchaseResult {
        return flashSaleRepository.purchaseItem(itemId)
    }
}
