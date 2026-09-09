package br.com.example.tripplanning.model

import java.io.Serializable

// Nível de dificuldade de uma atividade.
// O usuário pode ajustar esse valor na terceira tela.
enum class Difficulty(val label: String) : Serializable {

    EASY("Fácil"),
    MEDIUM("Média"),
    HARD("Difícil")
}
