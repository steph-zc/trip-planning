package br.com.example.tripplanning.data

import br.com.example.tripplanning.R
import br.com.example.tripplanning.model.Attraction
import br.com.example.tripplanning.model.Difficulty
import br.com.example.tripplanning.model.Preference

// Catálogo fixo de atividades do aplicativo.
// É um "object": existe uma única cópia dele em todo o app, e as telas
// só leem daqui. Se um dia os dados vierem de um banco ou da internet,
// basta trocar o conteúdo deste arquivo, sem mexer nas telas.
object AttractionRepository {

    private val attractions = listOf(

        // ----- Aventura -----
        Attraction(
            id = 1,
            name = "Trilha na montanha",
            description = "Caminhada guiada até o mirante, com vista para todo o vale.",
            preference = Preference.ADVENTURE,
            imageResId = R.drawable.ic_adventure,
            defaultDurationHours = 4,
            defaultDifficulty = Difficulty.HARD,
            websiteUrl = "https://pt.wikipedia.org/wiki/Trekking"
        ),
        Attraction(
            id = 2,
            name = "Rafting no rio",
            description = "Descida de bote em corredeiras, com equipamento incluso.",
            preference = Preference.ADVENTURE,
            imageResId = R.drawable.ic_adventure,
            defaultDurationHours = 3,
            defaultDifficulty = Difficulty.MEDIUM,
            websiteUrl = "https://pt.wikipedia.org/wiki/Rafting"
        ),
        Attraction(
            id = 3,
            name = "Voo de parapente",
            description = "Salto duplo com instrutor e vista aérea da região.",
            preference = Preference.ADVENTURE,
            imageResId = R.drawable.ic_adventure,
            defaultDurationHours = 2,
            defaultDifficulty = Difficulty.MEDIUM,
            websiteUrl = "https://pt.wikipedia.org/wiki/Parapente"
        ),

        // ----- Cultura -----
        Attraction(
            id = 4,
            name = "Museu histórico",
            description = "Acervo permanente sobre a formação e a história da cidade.",
            preference = Preference.CULTURE,
            imageResId = R.drawable.ic_culture,
            defaultDurationHours = 2,
            defaultDifficulty = Difficulty.EASY,
            websiteUrl = "https://pt.wikipedia.org/wiki/Museu"
        ),
        Attraction(
            id = 5,
            name = "Centro histórico",
            description = "Caminhada pelas ruas antigas, igrejas e casarões restaurados.",
            preference = Preference.CULTURE,
            imageResId = R.drawable.ic_culture,
            defaultDurationHours = 3,
            defaultDifficulty = Difficulty.EASY,
            websiteUrl = "https://pt.wikipedia.org/wiki/Centro_hist%C3%B3rico"
        ),
        Attraction(
            id = 6,
            name = "Teatro municipal",
            description = "Apresentação noturna no principal teatro da cidade.",
            preference = Preference.CULTURE,
            imageResId = R.drawable.ic_culture,
            defaultDurationHours = 2,
            defaultDifficulty = Difficulty.EASY,
            websiteUrl = "https://pt.wikipedia.org/wiki/Teatro"
        ),

        // ----- Praia -----
        Attraction(
            id = 7,
            name = "Dia de praia",
            description = "Tarde livre na orla, com cadeira e guarda-sol reservados.",
            preference = Preference.BEACH,
            imageResId = R.drawable.ic_beach,
            defaultDurationHours = 5,
            defaultDifficulty = Difficulty.EASY,
            websiteUrl = "https://pt.wikipedia.org/wiki/Praia"
        ),
        Attraction(
            id = 8,
            name = "Mergulho livre",
            description = "Passeio de barco até os recifes, com máscara e snorkel.",
            preference = Preference.BEACH,
            imageResId = R.drawable.ic_beach,
            defaultDurationHours = 3,
            defaultDifficulty = Difficulty.MEDIUM,
            websiteUrl = "https://pt.wikipedia.org/wiki/Mergulho_livre"
        ),
        Attraction(
            id = 9,
            name = "Aula de surfe",
            description = "Primeira aula com prancha e instrutor na praia principal.",
            preference = Preference.BEACH,
            imageResId = R.drawable.ic_beach,
            defaultDurationHours = 2,
            defaultDifficulty = Difficulty.HARD,
            websiteUrl = "https://pt.wikipedia.org/wiki/Surfe"
        ),

        // ----- Gastronomia -----
        Attraction(
            id = 10,
            name = "Tour gastronômico",
            description = "Roteiro a pé por restaurantes típicos, com degustação em cada parada.",
            preference = Preference.GASTRONOMY,
            imageResId = R.drawable.ic_gastronomy,
            defaultDurationHours = 4,
            defaultDifficulty = Difficulty.EASY,
            websiteUrl = "https://pt.wikipedia.org/wiki/Gastronomia"
        ),
        Attraction(
            id = 11,
            name = "Aula de culinária",
            description = "Oficina prática de pratos locais, com chef e ingredientes inclusos.",
            preference = Preference.GASTRONOMY,
            imageResId = R.drawable.ic_gastronomy,
            defaultDurationHours = 3,
            defaultDifficulty = Difficulty.MEDIUM,
            websiteUrl = "https://pt.wikipedia.org/wiki/Culin%C3%A1ria"
        ),
        Attraction(
            id = 12,
            name = "Feira local",
            description = "Visita à feira de produtores, com frutas e doces da região.",
            preference = Preference.GASTRONOMY,
            imageResId = R.drawable.ic_gastronomy,
            defaultDurationHours = 2,
            defaultDifficulty = Difficulty.EASY,
            websiteUrl = "https://pt.wikipedia.org/wiki/Feira_livre"
        )
    )

    // Devolve as atividades das preferências marcadas pelo usuário.
    // Se nenhuma preferência foi marcada, devolve o catálogo inteiro.
    fun findByPreferences(preferences: List<Preference>): List<Attraction> {
        if (preferences.isEmpty()) {
            return attractions
        }
        return attractions.filter { preferences.contains(it.preference) }
    }

    // Procura uma atividade pelo id. Devolve null se não encontrar.
    fun findById(id: Int): Attraction? =
        attractions.find { it.id == id }

    fun findAll(): List<Attraction> = attractions
}
