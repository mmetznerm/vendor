package br.com.mmetzner.vendor.model

class OrderRequest(
    val clientId: String,
    val products: List<ProductItemRequest>,
    val date: String,
    val truckId: String
)