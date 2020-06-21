package br.com.mmetzner.vendor.model

class ChargeRequest(
    val clientId: String? = null,
    val orderId: String? = null,
    val value: Double? = null,
    val date: String? = null,
    val finalized: Boolean = false
)