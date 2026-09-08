package br.com.example.tripplanning.model

import java.io.Serializable

// Guarda tudo o que o usuário informou sobre a viagem.
// Este objeto é passado de uma tela para a outra dentro do Intent,
// por isso a classe (e tudo o que ela contém) é Serializable.
data class Trip(

    val destination: String,
    // Datas escolhidas no DatePicker, no formato "dd/MM/yyyy"
    val departureDate: String,
    val returnDate: String,
    val travelers: Int,
    val preferences: List<Preference>,
    // Atividades já ajustadas e confirmadas pelo usuário
    val plannedAttractions: MutableList<PlannedAttraction> = mutableListOf()

) : Serializable {

    // Texto pronto com as preferências separadas por vírgula, para mostrar na tela e no log.
    fun preferencesLabel(): String =
        preferences.joinToString(", ") { it.label }

    // Soma das horas de todas as atividades escolhidas.
    fun totalHours(): Int =
        plannedAttractions.sumOf { it.durationHours }
}
