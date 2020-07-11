package br.com.mmetzner.vendor.model

class Order(
    var id: String? = null,
    val clientId: String? = null,
    val products: List<ProductItemRequest>? = null,
    val finished: Boolean = false
) {
    var client: Client? = null
    var productList: List<Product?>? = null
}