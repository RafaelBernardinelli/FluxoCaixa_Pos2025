package br.edu.utfpr.fluxocaixa_pos2025.entity;

data class Transaction(
        var _id: Int = 0,
        var type: String,
        var detail: String,
        var value: Double,
        var date: Long
)
