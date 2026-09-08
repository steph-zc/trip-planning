package br.com.example.tripplanning.util

import android.util.Log
import br.com.example.tripplanning.model.Attraction
import br.com.example.tripplanning.model.Trip

// Imprime no console (Logcat) o que o usuário informou a cada troca de tela,
// como pede o enunciado da atividade.
// Deixar isso em um lugar só evita repetir Log.d espalhado pelas telas.
object TripLogger {

    private const val TAG = "TripPlanning"

    // Chamado sempre que uma tela envia o usuário para a próxima.
    fun logTransition(from: String, to: String, trip: Trip) {
        Log.d(TAG, "===== $from -> $to =====")
        logTrip(trip)
    }

    // Imprime o estado atual da viagem.
    fun logTrip(trip: Trip) {
        Log.d(TAG, "Destino: ${trip.destination}")
        Log.d(TAG, "Partida: ${trip.departureDate} | Retorno: ${trip.returnDate}")
        Log.d(TAG, "Viajantes: ${trip.travelers}")
        Log.d(TAG, "Preferências: ${trip.preferencesLabel()}")

        if (trip.plannedAttractions.isEmpty()) {
            Log.d(TAG, "Atividades escolhidas: nenhuma até agora")
            return
        }

        Log.d(TAG, "Atividades escolhidas (${trip.plannedAttractions.size}):")
        trip.plannedAttractions.forEach { planned ->
            Log.d(
                TAG,
                "  - ${planned.attraction.name} | ${planned.durationHours}h | " +
                    "${planned.difficulty.label} | início ${planned.startTime} | " +
                    "guia: ${if (planned.withGuide) "sim" else "não"}"
            )
        }
        Log.d(TAG, "Total de horas: ${trip.totalHours()}")
    }

    // Usado quando o usuário toca em uma atividade da lista.
    fun logSelectedAttraction(attraction: Attraction) {
        Log.d(TAG, "Atividade selecionada: ${attraction.name} (${attraction.preference.label})")
    }
}
