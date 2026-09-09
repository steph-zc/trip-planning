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
            defaultDifficulty = Difficulty.HARD
        ),
        Attraction(
            id = 2,
            name = "Rafting no rio",
            description = "Descida de bote em corredeiras, com equipamento incluso.",
            preference = Preference.ADVENTURE,
            imageResId = R.drawable.ic_adventure,
            defaultDurationHours = 3,
            defaultDifficulty = Difficulty.MEDIUM
        ),
        Attraction(
            id = 3,
            name = "Voo de parapente",
            description = "Salto duplo com instrutor e vista aérea da região.",
            preference = Preference.ADVENTURE,
            imageResId = R.drawable.ic_adventure,
            defaultDurationHours = 2,
            defaultDifficulty = Difficulty.MEDIUM
        ),

        // ----- Cultura -----
        Attraction(
            id = 4,
            name = "Museu histórico",
            description = "Acervo permanente sobre a formação e a história da cidade.",
            preference = Preference.CULTURE,
            imageResId = R.drawable.ic_culture,
            defaultDurationHours = 2,
            defaultDifficulty = Difficulty.EASY
        ),
        Attraction(
            id = 5,
            name = "Centro histórico",
            description = "Caminhada pelas ruas antigas, igrejas e casarões restaurados.",
            preference = Preference.CULTURE,
            imageResId = R.drawable.ic_culture,
            defaultDurationHours = 3,
            defaultDifficulty = Difficulty.EASY
        ),
        Attraction(
            id = 6,
            name = "Teatro municipal",
            description = "Apresentação noturna no principal teatro da cidade.",
            preference = Preference.CULTURE,
            imageResId = R.drawable.ic_culture,
            defaultDurationHours = 2,
            defaultDifficulty = Difficulty.EASY
        ),

        // ----- Praia -----
        Attraction(
            id = 7,
            name = "Dia de praia",
            description = "Tarde livre na orla, com cadeira e guarda-sol reservados.",
            preference = Preference.BEACH,
            imageResId = R.drawable.ic_beach,
            defaultDurationHours = 5,
            defaultDifficulty = Difficulty.EASY
        ),
        Attraction(
            id = 8,
            name = "Mergulho livre",
            description = "Passeio de barco até os recifes, com máscara e snorkel.",
            preference = Preference.BEACH,
            imageResId = R.drawable.ic_beach,
            defaultDurationHours = 3,
            defaultDifficulty = Difficulty.MEDIUM
        ),
        Attraction(
            id = 9,
            name = "Aula de surfe",
            description = "Primeira aula com prancha e instrutor na praia principal.",
            preference = Preference.BEACH,
            imageResId = R.drawable.ic_beach,
            defaultDurationHours = 2,
            defaultDifficulty = Difficulty.HARD
        ),

        // ----- Gastronomia -----
        Attraction(
            id = 10,
            name = "Tour gastronômico",
            description = "Roteiro a pé por restaurantes típicos, com degustação em cada parada.",
            preference = Preference.GASTRONOMY,
            imageResId = R.drawable.ic_gastronomy,
            defaultDurationHours = 4,
            defaultDifficulty = Difficulty.EASY
        ),
        Attraction(
            id = 11,
            name = "Aula de culinária",
            description = "Oficina prática de pratos locais, com chef e ingredientes inclusos.",
            preference = Preference.GASTRONOMY,
            imageResId = R.drawable.ic_gastronomy,
            defaultDurationHours = 3,
            defaultDifficulty = Difficulty.MEDIUM
        ),
        Attraction(
            id = 12,
            name = "Feira local",
            description = "Visita à feira de produtores, com frutas e doces da região.",
            preference = Preference.GASTRONOMY,
            imageResId = R.drawable.ic_gastronomy,
            defaultDurationHours = 2,
            defaultDifficulty = Difficulty.EASY
        ),

        // ----- Vida noturna -----
        Attraction(
            id = 13,
            name = "Bar com música ao vivo",
            description = "Noite em um bar tradicional, com banda tocando o repertório da região.",
            preference = Preference.NIGHTLIFE,
            imageResId = R.drawable.ic_nightlife,
            defaultDurationHours = 3,
            defaultDifficulty = Difficulty.EASY
        ),
        Attraction(
            id = 14,
            name = "Mirante à noite",
            description = "Subida ao mirante depois do pôr do sol, com a cidade toda iluminada.",
            preference = Preference.NIGHTLIFE,
            imageResId = R.drawable.ic_nightlife,
            defaultDurationHours = 2,
            defaultDifficulty = Difficulty.MEDIUM
        ),
        Attraction(
            id = 15,
            name = "Festival de rua",
            description = "Programação noturna com food trucks, música e artesanato local.",
            preference = Preference.NIGHTLIFE,
            imageResId = R.drawable.ic_nightlife,
            defaultDurationHours = 4,
            defaultDifficulty = Difficulty.EASY
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
