package br.com.mmetzner.vendor.model

class ProductItemRequest(
    val productId: String? = null,
    val productQuantity: Int = 0
) {
    var productDescription: String? = null
}