package br.com.mmetzner.vendor.model

class Client(
    var id: String? = null,
    val name: String? = null,
    val address: String? = null,
    val cpfCnpj: String? = null,
    val phone: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val city: String? = null,
    val charges: List<Charge>? = null
)