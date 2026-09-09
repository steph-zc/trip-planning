package br.com.example.tripplanning.model

import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.TimeUnit

// Guarda tudo o que o usuário informou sobre a viagem.
// Este objeto é passado de uma tela para a outra dentro do Intent,
// por isso a classe (e tudo o que ela contém) é Serializable.
data class Trip(

    val destination: String,
    // Datas escolhidas no DatePicker, no formato "dd/MM/yyyy"
    val departureDate: String,
    val returnDate: String,
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

    // Quantos dias a viagem dura, contando o dia da partida e o do retorno.
    // Se as datas vierem em formato inesperado, devolve 1 para nunca dividir por zero.
    fun totalDays(): Int {
        val format = SimpleDateFormat(DATE_PATTERN, Locale.forLanguageTag("pt-BR"))
        return try {
            val start = format.parse(departureDate) ?: return 1
            val end = format.parse(returnDate) ?: return 1
            val days = TimeUnit.MILLISECONDS.toDays(end.time - start.time).toInt()
            if (days < 0) 1 else days + 1
        } catch (e: Exception) {
            1
        }
    }

    // Horas de passeio que cabem na viagem, considerando um dia útil de
    // turismo por dia. Serve de referência para a barra de progresso do resumo.
    fun availableHours(): Int =
        totalDays() * HOURS_PER_DAY

    companion object {
        private const val DATE_PATTERN = "dd/MM/yyyy"
        private const val HOURS_PER_DAY = 8
    }
}
