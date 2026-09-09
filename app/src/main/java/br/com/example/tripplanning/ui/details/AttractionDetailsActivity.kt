package br.com.example.tripplanning.ui.details

import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import br.com.example.tripplanning.R
import br.com.example.tripplanning.model.Attraction
import br.com.example.tripplanning.model.Difficulty
import br.com.example.tripplanning.model.PlannedAttraction
import br.com.example.tripplanning.model.Trip
import br.com.example.tripplanning.util.Extras
import br.com.example.tripplanning.util.TripLogger
import java.util.Locale

// Tela 3: mostra os detalhes da atividade escolhida e deixa o usuário
// ajustar os parâmetros antes de adicioná-la à viagem.
class AttractionDetailsActivity : AppCompatActivity() {

    private lateinit var imgAttraction: ImageView
    private lateinit var txtAttractionName: TextView
    private lateinit var txtAttractionDescription: TextView
    private lateinit var txtDuration: TextView
    private lateinit var seekDuration: SeekBar
    private lateinit var groupDifficulty: RadioGroup
    private lateinit var radioEasy: RadioButton
    private lateinit var radioMedium: RadioButton
    private lateinit var radioHard: RadioButton
    private lateinit var btnStartTime: Button
    private lateinit var editNotes: EditText
    private lateinit var btnAddToTrip: Button

    private lateinit var trip: Trip
    private lateinit var attraction: Attraction

    // Horário de início. Começa com um valor padrão e muda no TimePickerDialog.
    private var startHour = DEFAULT_HOUR
    private var startMinute = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_attraction_details)
        Log.d(TAG, "onCreate: tela criada")

        // A tela só funciona com a viagem e a atividade que vieram da lista.
        val receivedTrip = intent.getSerializableExtra(Extras.TRIP, Trip::class.java)
        val receivedAttraction = intent.getSerializableExtra(Extras.ATTRACTION, Attraction::class.java)
        if (receivedTrip == null || receivedAttraction == null) {
            Log.d(TAG, "Dados incompletos no Intent. Fechando a tela.")
            finish()
            return
        }
        trip = receivedTrip
        attraction = receivedAttraction

        TripLogger.logArrival("AttractionDetailsActivity", trip)
        TripLogger.logSelectedAttraction(attraction)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        linkComponents()
        fillAttraction()
        configComponents()
    }

    private fun linkComponents() {
        imgAttraction = findViewById(R.id.imgAttraction)
        txtAttractionName = findViewById(R.id.txtAttractionName)
        txtAttractionDescription = findViewById(R.id.txtAttractionDescription)
        txtDuration = findViewById(R.id.txtDuration)
        seekDuration = findViewById(R.id.seekDuration)
        groupDifficulty = findViewById(R.id.groupDifficulty)
        radioEasy = findViewById(R.id.radioEasy)
        radioMedium = findViewById(R.id.radioMedium)
        radioHard = findViewById(R.id.radioHard)
        btnStartTime = findViewById(R.id.btnStartTime)
        editNotes = findViewById(R.id.editNotes)
        btnAddToTrip = findViewById(R.id.btnAddToTrip)
    }

    // Mostra a atividade e usa os valores sugeridos do catálogo como ponto de partida.
    private fun fillAttraction() {
        imgAttraction.setImageResource(attraction.imageResId)
        txtAttractionName.text = attraction.name
        txtAttractionDescription.text = attraction.description

        radioEasy.text = Difficulty.EASY.label
        radioMedium.text = Difficulty.MEDIUM.label
        radioHard.text = Difficulty.HARD.label

        seekDuration.progress = attraction.defaultDurationHours
        updateDurationLabel(attraction.defaultDurationHours)
        selectDifficulty(attraction.defaultDifficulty)
        updateStartTimeLabel()
    }

    private fun configComponents() {
        seekDuration.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {

            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                updateDurationLabel(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) = Unit

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                Log.d(TAG, "Duração ajustada para ${seekBar?.progress} h")
            }
        })

        btnStartTime.setOnClickListener { showTimePicker() }
        btnAddToTrip.setOnClickListener { addToTrip() }
    }

    private fun updateDurationLabel(hours: Int) {
        txtDuration.text = getString(R.string.details_duration, hours)
    }

    // Marca no RadioGroup a dificuldade sugerida pelo catálogo.
    private fun selectDifficulty(difficulty: Difficulty) {
        when (difficulty) {
            Difficulty.EASY -> radioEasy.isChecked = true
            Difficulty.MEDIUM -> radioMedium.isChecked = true
            Difficulty.HARD -> radioHard.isChecked = true
        }
    }

    // Lê do RadioGroup qual dificuldade está marcada.
    private fun selectedDifficulty(): Difficulty {
        return when (groupDifficulty.checkedRadioButtonId) {
            R.id.radioEasy -> Difficulty.EASY
            R.id.radioHard -> Difficulty.HARD
            else -> Difficulty.MEDIUM
        }
    }

    private fun showTimePicker() {
        TimePickerDialog(
            this,
            { _, hour, minute ->
                startHour = hour
                startMinute = minute
                updateStartTimeLabel()
                Log.d(TAG, "Horário de início escolhido: ${formattedStartTime()}")
            },
            startHour,
            startMinute,
            // true = relógio de 24 horas, o formato usado no Brasil
            true
        ).show()
    }

    private fun updateStartTimeLabel() {
        btnStartTime.text = formattedStartTime()
    }

    // Monta o texto "HH:mm" sempre com dois dígitos.
    private fun formattedStartTime(): String =
        String.format(Locale.forLanguageTag("pt-BR"), "%02d:%02d", startHour, startMinute)

    // Junta tudo o que o usuário ajustou e devolve a viagem atualizada para a lista.
    private fun addToTrip() {
        val planned = PlannedAttraction(
            attraction = attraction,
            durationHours = seekDuration.progress,
            difficulty = selectedDifficulty(),
            startTime = formattedStartTime(),
            notes = editNotes.text.toString().trim()
        )

        // Se a atividade já estava na viagem, a versão antiga sai e entra a nova.
        // Assim o usuário pode voltar e reajustar sem duplicar o item.
        trip.plannedAttractions.removeAll { it.attraction.id == attraction.id }
        trip.plannedAttractions.add(planned)

        TripLogger.logTransition("AttractionDetailsActivity", "AttractionListActivity", trip)

        // setResult devolve a viagem atualizada para a tela que abriu esta.
        // Sem isso, a lista continuaria com a cópia antiga, sem a atividade nova.
        val result = Intent()
        result.putExtra(Extras.TRIP, trip)
        setResult(RESULT_OK, result)
        finish()
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    companion object {
        private const val TAG = "AttractionDetails"
        private const val DEFAULT_HOUR = 9
    }
}
