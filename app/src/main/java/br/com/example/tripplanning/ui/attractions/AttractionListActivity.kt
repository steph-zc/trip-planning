package br.com.example.tripplanning.ui.attractions

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.com.example.tripplanning.R
import br.com.example.tripplanning.data.AttractionRepository
import br.com.example.tripplanning.model.Attraction
import br.com.example.tripplanning.model.Trip
import br.com.example.tripplanning.ui.adapter.AttractionAdapter
import br.com.example.tripplanning.util.Extras
import br.com.example.tripplanning.util.TripLogger

// Tela 2: mostra as atividades sugeridas para a viagem, filtradas
// pelas preferências que o usuário marcou na tela anterior.
class AttractionListActivity : AppCompatActivity() {

    private lateinit var txtDestination: TextView
    private lateinit var txtTripInfo: TextView
    private lateinit var txtAttractionsCount: TextView
    private lateinit var recyclerAttractions: RecyclerView

    // A viagem montada na tela 1, recebida pelo Intent.
    private lateinit var trip: Trip

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_attraction_list)
        Log.d(TAG, "onCreate: tela criada")

        // Se a viagem não veio junto, não há o que mostrar: a tela fecha.
        // Isso protege contra a Activity ser aberta fora do fluxo normal.
        val receivedTrip = intent.getSerializableExtra(Extras.TRIP, Trip::class.java)
        if (receivedTrip == null) {
            Log.d(TAG, "Nenhuma viagem recebida no Intent. Fechando a tela.")
            finish()
            return
        }
        trip = receivedTrip
        TripLogger.logArrival("AttractionListActivity", trip)

        // Mostra a seta de voltar na barra de título.
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        linkComponents()
        fillHeader()
        fillList()
    }

    private fun linkComponents() {
        txtDestination = findViewById(R.id.txtDestination)
        txtTripInfo = findViewById(R.id.txtTripInfo)
        txtAttractionsCount = findViewById(R.id.txtAttractionsCount)
        recyclerAttractions = findViewById(R.id.recyclerAttractions)
    }

    // Repete no topo o que o usuário informou, para ele não perder o contexto.
    private fun fillHeader() {
        txtDestination.text = trip.destination
        txtTripInfo.text = getString(
            R.string.list_trip_info,
            trip.departureDate,
            trip.returnDate,
            trip.preferencesLabel()
        )
    }

    // Busca as atividades no repositório e entrega ao RecyclerView.
    private fun fillList() {
        val attractions = AttractionRepository.findByPreferences(trip.preferences)
        Log.d(TAG, "Atividades encontradas para as preferências: ${attractions.size}")

        txtAttractionsCount.text = resources.getQuantityString(
            R.plurals.list_attractions_count,
            attractions.size,
            attractions.size
        )

        // O LayoutManager define como os itens se organizam.
        // LinearLayoutManager empilha um embaixo do outro.
        recyclerAttractions.layoutManager = LinearLayoutManager(this)
        recyclerAttractions.adapter = AttractionAdapter(attractions) { attraction ->
            openDetails(attraction)
        }
    }

    // Chamado quando o usuário toca em um cartão da lista.
    private fun openDetails(attraction: Attraction) {
        TripLogger.logSelectedAttraction(attraction)
        TripLogger.logTransition("AttractionListActivity", "AttractionDetailsActivity", trip)

        // TEMPORÁRIO: a terceira tela ainda não existe.
        // Na próxima etapa esta linha vira o Intent que abre os detalhes.
        Toast.makeText(this, attraction.name, Toast.LENGTH_SHORT).show()
    }

    // Faz a seta de voltar da barra de título encerrar esta tela,
    // devolvendo o usuário ao formulário da viagem.
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    companion object {
        private const val TAG = "AttractionListActivity"
    }
}
