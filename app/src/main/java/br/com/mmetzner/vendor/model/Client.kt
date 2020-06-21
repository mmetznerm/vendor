package br.com.mmetzner.vendor.model

class Client(
    var id: String? = null,
    val name: String? = null,
    val address: String? = null,
    val cpfCnpj: String? = null,
    val phone: String? = null,
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val cep: String? = null,
    val charges: List<Charge>? = null
)