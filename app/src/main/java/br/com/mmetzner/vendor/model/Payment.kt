package br.com.mmetzner.vendor.model

class Payment(
    var id: String? = null,
    val description: String? = null,
    val charge:  Boolean = false,
    val days:  Int = 0
)