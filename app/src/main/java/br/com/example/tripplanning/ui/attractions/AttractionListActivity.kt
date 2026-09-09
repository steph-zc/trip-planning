package br.com.example.tripplanning.ui.attractions

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.com.example.tripplanning.R
import br.com.example.tripplanning.data.AttractionRepository
import br.com.example.tripplanning.model.Attraction
import br.com.example.tripplanning.model.Trip
import br.com.example.tripplanning.ui.adapter.AttractionAdapter
import br.com.example.tripplanning.ui.details.AttractionDetailsActivity
import br.com.example.tripplanning.ui.summary.TripSummaryActivity
import br.com.example.tripplanning.util.Extras
import br.com.example.tripplanning.util.TripLogger

// Tela 2: mostra as atividades sugeridas para a viagem, filtradas
// pelas preferências que o usuário marcou na tela anterior.
class AttractionListActivity : AppCompatActivity() {

    private lateinit var txtDestination: TextView
    private lateinit var txtTripInfo: TextView
    private lateinit var txtAttractionsCount: TextView
    private lateinit var txtChosenCount: TextView
    private lateinit var recyclerAttractions: RecyclerView
    private lateinit var btnSeeSummary: Button

    // A viagem montada na tela 1, recebida pelo Intent.
    private lateinit var trip: Trip

    // Abre a tela de detalhes e fica esperando a resposta dela.
    // Como o Trip viaja no Intent, cada tela trabalha com a sua própria cópia:
    // é por aqui que a viagem com a atividade nova volta para esta tela.
    private val detailsLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode != RESULT_OK) {
            Log.d(TAG, "Voltou dos detalhes sem adicionar nada")
            return@registerForActivityResult
        }

        val updatedTrip = result.data?.getSerializableExtra(Extras.TRIP, Trip::class.java)
        if (updatedTrip != null) {
            trip = updatedTrip
            TripLogger.logArrival("AttractionListActivity", trip)
            updateChosenCount()

            val lastAdded = trip.plannedAttractions.lastOrNull()
            if (lastAdded != null) {
                Toast.makeText(
                    this,
                    getString(R.string.details_added, lastAdded.attraction.name),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

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
        updateChosenCount()
    }

    private fun linkComponents() {
        txtDestination = findViewById(R.id.txtDestination)
        txtTripInfo = findViewById(R.id.txtTripInfo)
        txtAttractionsCount = findViewById(R.id.txtAttractionsCount)
        txtChosenCount = findViewById(R.id.txtChosenCount)
        recyclerAttractions = findViewById(R.id.recyclerAttractions)
        btnSeeSummary = findViewById(R.id.btnSeeSummary)
        btnSeeSummary.setOnClickListener { openSummary() }
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

    // Mostra quantas atividades já foram adicionadas à viagem.
    private fun updateChosenCount() {
        val chosen = trip.plannedAttractions.size
        txtChosenCount.text = if (chosen == 0) {
            getString(R.string.list_chosen_none)
        } else {
            resources.getQuantityString(R.plurals.list_chosen_count, chosen, chosen)
        }
    }

    // Chamado quando o usuário toca em um cartão da lista.
    private fun openDetails(attraction: Attraction) {
        TripLogger.logSelectedAttraction(attraction)
        TripLogger.logTransition("AttractionListActivity", "AttractionDetailsActivity", trip)

        val intent = Intent(this, AttractionDetailsActivity::class.java)
        intent.putExtra(Extras.TRIP, trip)
        intent.putExtra(Extras.ATTRACTION, attraction)
        detailsLauncher.launch(intent)
    }

    // Abre o resumo final da viagem.
    private fun openSummary() {
        // Um resumo vazio não diz nada ao usuário, então a tela só abre
        // depois de pelo menos uma atividade escolhida.
        if (trip.plannedAttractions.isEmpty()) {
            Log.d(TAG, "Resumo pedido sem nenhuma atividade escolhida")
            AlertDialog.Builder(this)
                .setTitle(R.string.dialog_incomplete_title)
                .setMessage(R.string.error_no_attraction)
                .setPositiveButton(R.string.dialog_ok, null)
                .show()
            return
        }

        TripLogger.logTransition("AttractionListActivity", "TripSummaryActivity", trip)

        val intent = Intent(this, TripSummaryActivity::class.java)
        intent.putExtra(Extras.TRIP, trip)
        startActivity(intent)
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
