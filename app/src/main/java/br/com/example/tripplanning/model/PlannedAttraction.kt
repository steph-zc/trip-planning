package br.com.example.tripplanning.model

import java.io.Serializable

// Uma atividade depois de o usuário ajustar os parâmetros dela.
// É isso que aparece no resumo da viagem, na quarta tela.
data class PlannedAttraction(

    val attraction: Attraction,
    val durationHours: Int,
    val difficulty: Difficulty,
    // Horário de início escolhido no TimePicker, no formato "HH:mm"
    val startTime: String,
    // Marcado quando o usuário quer acompanhamento de um guia
    val withGuide: Boolean,
    val notes: String

) : Serializable
