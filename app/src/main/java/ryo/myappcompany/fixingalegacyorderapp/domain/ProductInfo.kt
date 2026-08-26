package ryo.myappcompany.fixingalegacyorderapp.domain

/**
 * 商品詳細情報(ドメインクラス)
 *
 * @param productName 商品名
 * @param stock 在庫数
 */
data class ProductInfo(
    val productName: String,
    val stock: Int
)
