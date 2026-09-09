package br.com.example.tripplanning.ui.summary

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ListView
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.TextView
import android.widget.ToggleButton
import androidx.appcompat.app.AppCompatActivity
import br.com.example.tripplanning.R
import br.com.example.tripplanning.model.PlannedAttraction
import br.com.example.tripplanning.model.Trip
import br.com.example.tripplanning.ui.adapter.PlannedAttractionAdapter
import br.com.example.tripplanning.util.Extras
import br.com.example.tripplanning.util.TripLogger

// Tela 4: resumo final, com todas as atividades escolhidas e seus detalhes.
class TripSummaryActivity : AppCompatActivity() {

    private lateinit var txtDestination: TextView
    private lateinit var txtTripInfo: TextView
    private lateinit var txtHours: TextView
    private lateinit var progressHours: ProgressBar
    private lateinit var spinnerSort: Spinner
    private lateinit var toggleDetails: ToggleButton
    private lateinit var listPlanned: ListView
    private lateinit var txtEmpty: TextView

    private lateinit var trip: Trip
    private lateinit var adapter: PlannedAttractionAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trip_summary)
        Log.d(TAG, "onCreate: tela criada")

        val receivedTrip = intent.getSerializableExtra(Extras.TRIP, Trip::class.java)
        if (receivedTrip == null) {
            Log.d(TAG, "Nenhuma viagem recebida no Intent. Fechando a tela.")
            finish()
            return
        }
        trip = receivedTrip
        TripLogger.logArrival("TripSummaryActivity", trip)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        linkComponents()
        fillHeader()
        configList()
    }

    private fun linkComponents() {
        txtDestination = findViewById(R.id.txtDestination)
        txtTripInfo = findViewById(R.id.txtTripInfo)
        txtHours = findViewById(R.id.txtHours)
        progressHours = findViewById(R.id.progressHours)
        spinnerSort = findViewById(R.id.spinnerSort)
        toggleDetails = findViewById(R.id.toggleDetails)
        listPlanned = findViewById(R.id.listPlanned)
        txtEmpty = findViewById(R.id.txtEmpty)
    }

    private fun fillHeader() {
        txtDestination.text = trip.destination

        // O texto dos dias sai de um "plurals" para nunca aparecer "1 dias".
        val days = resources.getQuantityString(
            R.plurals.summary_days,
            trip.totalDays(),
            trip.totalDays()
        )
        txtTripInfo.text = getString(
            R.string.summary_trip_info,
            trip.departureDate,
            trip.returnDate,
            days
        )

        val planned = trip.totalHours()
        val available = trip.availableHours()
        txtHours.text = getString(R.string.summary_hours, planned, available)

        // A barra compara as horas já planejadas com as que cabem na viagem.
        progressHours.max = available
        // coerceAtMost evita a barra estourar se o usuário planejar mais
        // horas do que a viagem comporta.
        progressHours.progress = planned.coerceAtMost(available)
    }

    private fun configList() {
        // Sem nenhuma atividade escolhida, a lista dá lugar ao aviso.
        if (trip.plannedAttractions.isEmpty()) {
            listPlanned.visibility = View.GONE
            txtEmpty.visibility = View.VISIBLE
            return
        }

        listPlanned.visibility = View.VISIBLE
        txtEmpty.visibility = View.GONE

        adapter = PlannedAttractionAdapter(this, sortedAttractions(), toggleDetails.isChecked)
        listPlanned.adapter = adapter

        spinnerSort.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                Log.d(TAG, "Ordenação escolhida: ${spinnerSort.selectedItem}")
                refreshList()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) = Unit
        }

        toggleDetails.setOnCheckedChangeListener { _, isChecked ->
            Log.d(TAG, "Modo de exibição: ${if (isChecked) "detalhado" else "compacto"}")
            refreshList()
        }
    }

    private fun refreshList() {
        adapter.update(sortedAttractions(), toggleDetails.isChecked)
    }

    // Ordena conforme a opção escolhida no Spinner.
    // A posição vem do string-array summary_sort_options.
    private fun sortedAttractions(): List<PlannedAttraction> {
        return when (spinnerSort.selectedItemPosition) {
            SORT_BY_DURATION -> trip.plannedAttractions.sortedByDescending { it.durationHours }
            SORT_BY_NAME -> trip.plannedAttractions.sortedBy { it.attraction.name }
            else -> trip.plannedAttractions.sortedBy { it.startTime }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    companion object {
        private const val TAG = "TripSummaryActivity"
        private const val SORT_BY_DURATION = 1
        private const val SORT_BY_NAME = 2
    }
}
