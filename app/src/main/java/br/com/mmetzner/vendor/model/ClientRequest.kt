package br.com.mmetzner.vendor.model

class ClientRequest(
    val name: String? = null,
    val address: String? = null,
    val cpfCnpj: String? = null,
    val phone: String? = null,
    val city: String? = null,
    val latitude: Double? = 0.0,
    val longitude: Double? = 0.0
)