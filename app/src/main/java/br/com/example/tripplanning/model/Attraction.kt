package br.com.example.tripplanning.model

import java.io.Serializable

// Uma atividade que o aplicativo sugere para a viagem.
// Os valores "default" são apenas a sugestão inicial: na terceira tela
// o usuário pode mudar a duração e a dificuldade.
data class Attraction(

    val id: Int,
    val name: String,
    val description: String,
    val preference: Preference,
    // Id do desenho em res/drawable usado como imagem ilustrativa
    val imageResId: Int,
    val defaultDurationHours: Int,
    val defaultDifficulty: Difficulty,
    // Página aberta pelo WebView na tela de detalhes
    val websiteUrl: String

) : Serializable
