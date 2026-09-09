package br.com.example.tripplanning.model

import java.io.Serializable

// Tipos de preferência que o usuário marca na primeira tela.
// "label" é o texto que aparece na interface.
enum class Preference(val label: String) : Serializable {

    ADVENTURE("Aventura"),
    CULTURE("Cultura"),
    BEACH("Praia"),
    GASTRONOMY("Gastronomia"),
    NIGHTLIFE("Vida noturna")
}
