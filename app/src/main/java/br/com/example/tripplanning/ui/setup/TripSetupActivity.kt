package br.com.example.tripplanning.ui.setup

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import br.com.example.tripplanning.R
import br.com.example.tripplanning.model.Preference
import br.com.example.tripplanning.model.Trip
import br.com.example.tripplanning.ui.attractions.AttractionListActivity
import br.com.example.tripplanning.util.Extras
import br.com.example.tripplanning.util.TripLogger
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

// Tela 1: o usuário informa destino, datas e marca as preferências
// que vão filtrar as atividades da tela seguinte.
class TripSetupActivity : AppCompatActivity() {

    private lateinit var editDestination: EditText
    private lateinit var btnDepartureDate: Button
    private lateinit var btnReturnDate: Button
    private lateinit var checkAdventure: CheckBox
    private lateinit var checkCulture: CheckBox
    private lateinit var checkBeach: CheckBox
    private lateinit var checkGastronomy: CheckBox
    private lateinit var checkNightlife: CheckBox
    private lateinit var btnContinue: Button

    // Datas escolhidas no DatePickerDialog.
    // Ficam nulas enquanto o usuário não escolher, e é assim que a
    // validação sabe que o campo ainda está em branco.
    private var departureDate: Calendar? = null
    private var returnDate: Calendar? = null

    // Formato usado para mostrar a data na tela e para guardar no objeto Trip.
    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.forLanguageTag("pt-BR"))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trip_setup)
        Log.d(TAG, "onCreate: tela criada")
        linkComponents()
        configComponents()
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart: tela ficou visível")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume: tela pronta para o usuário")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause: tela perdeu o foco")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop: tela não está mais visível")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy: tela destruída")
    }

    // Busca no layout cada componente que o código precisa manipular.
    private fun linkComponents() {
        editDestination = findViewById(R.id.editDestination)
        btnDepartureDate = findViewById(R.id.btnDepartureDate)
        btnReturnDate = findViewById(R.id.btnReturnDate)
        checkAdventure = findViewById(R.id.checkAdventure)
        checkCulture = findViewById(R.id.checkCulture)
        checkBeach = findViewById(R.id.checkBeach)
        checkGastronomy = findViewById(R.id.checkGastronomy)
        checkNightlife = findViewById(R.id.checkNightlife)
        btnContinue = findViewById(R.id.btnContinue)
    }

    // Define o texto inicial e o que cada componente faz quando tocado.
    private fun configComponents() {
        // O texto das caixas vem do enum Preference, para o nome da
        // preferência existir em um lugar só em todo o aplicativo.
        checkAdventure.text = Preference.ADVENTURE.label
        checkCulture.text = Preference.CULTURE.label
        checkBeach.text = Preference.BEACH.label
        checkGastronomy.text = Preference.GASTRONOMY.label
        checkNightlife.text = Preference.NIGHTLIFE.label

        btnDepartureDate.setOnClickListener { pickDepartureDate() }
        btnReturnDate.setOnClickListener { pickReturnDate() }
        btnContinue.setOnClickListener { confirmTrip() }
    }

    private fun pickDepartureDate() {
        showDatePicker(current = departureDate, minDate = null) { chosen ->
            departureDate = chosen
            btnDepartureDate.text = dateFormat.format(chosen.time)
            Log.d(TAG, "Data de partida escolhida: ${dateFormat.format(chosen.time)}")

            // Se o retorno já escolhido ficou antes da nova partida, ele é
            // apagado: assim as duas datas nunca ficam em ordem inválida.
            if (returnDate != null && returnDate!!.before(chosen)) {
                returnDate = null
                btnReturnDate.setText(R.string.setup_select_date)
                Log.d(TAG, "Data de retorno apagada por ficar anterior à partida")
                showAlert(
                    R.string.dialog_attention_title,
                    getString(R.string.warning_return_cleared)
                )
            }
        }
    }

    private fun pickReturnDate() {
        // Sem a data de partida não há como travar o calendário do retorno,
        // então o usuário é orientado a escolher a partida primeiro.
        if (departureDate == null) {
            showAlert(
                R.string.dialog_incomplete_title,
                getString(R.string.error_departure_first)
            )
            return
        }

        showDatePicker(current = returnDate, minDate = departureDate) { chosen ->
            returnDate = chosen
            btnReturnDate.text = dateFormat.format(chosen.time)
            Log.d(TAG, "Data de retorno escolhida: ${dateFormat.format(chosen.time)}")
        }
    }

    // Abre o calendário do Android já posicionado na data atual do campo
    // (ou na data de hoje, se ainda não houver escolha).
    // Quando minDate é informada, o calendário desabilita os dias anteriores a ela.
    private fun showDatePicker(
        current: Calendar?,
        minDate: Calendar?,
        onDatePicked: (Calendar) -> Unit
    ) {
        val startFrom = current ?: minDate ?: Calendar.getInstance()

        val dialog = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                onDatePicked(startOfDay(year, month, dayOfMonth))
            },
            startFrom.get(Calendar.YEAR),
            startFrom.get(Calendar.MONTH),
            startFrom.get(Calendar.DAY_OF_MONTH)
        )

        if (minDate != null) {
            dialog.datePicker.minDate = minDate.timeInMillis
        }
        dialog.show()
    }

    // Cria a data com a hora zerada.
    // Sem isso, duas datas do mesmo dia teriam horas diferentes e as
    // comparações de "antes" e "depois" dariam resultado errado.
    private fun startOfDay(year: Int, month: Int, dayOfMonth: Int): Calendar {
        val date = Calendar.getInstance()
        date.set(year, month, dayOfMonth, 0, 0, 0)
        date.set(Calendar.MILLISECOND, 0)
        return date
    }

    // Monta a lista com as preferências que o usuário marcou.
    private fun selectedPreferences(): List<Preference> {
        val preferences = mutableListOf<Preference>()
        if (checkAdventure.isChecked) preferences.add(Preference.ADVENTURE)
        if (checkCulture.isChecked) preferences.add(Preference.CULTURE)
        if (checkBeach.isChecked) preferences.add(Preference.BEACH)
        if (checkGastronomy.isChecked) preferences.add(Preference.GASTRONOMY)
        if (checkNightlife.isChecked) preferences.add(Preference.NIGHTLIFE)
        return preferences
    }

    // Devolve a mensagem do primeiro problema encontrado, ou null se estiver tudo certo.
    private fun findValidationError(): String? {
        if (editDestination.text.toString().isBlank()) {
            return getString(R.string.error_destination)
        }
        if (departureDate == null) {
            return getString(R.string.error_departure_date)
        }
        if (returnDate == null) {
            return getString(R.string.error_return_date)
        }
        // O calendário já impede escolher um retorno anterior à partida.
        // Esta checagem é a segunda barreira, caso alguma mudança futura
        // deixe passar uma combinação inválida.
        if (returnDate!!.before(departureDate)) {
            return getString(R.string.error_date_order)
        }
        if (selectedPreferences().isEmpty()) {
            return getString(R.string.error_preferences)
        }
        return null
    }

    // Valida os campos e, estando tudo certo, monta o objeto Trip.
    private fun confirmTrip() {
        val error = findValidationError()
        if (error != null) {
            Log.d(TAG, "Validação falhou: $error")
            showAlert(R.string.dialog_incomplete_title, error)
            return
        }

        val trip = Trip(
            destination = editDestination.text.toString().trim(),
            departureDate = dateFormat.format(departureDate!!.time),
            returnDate = dateFormat.format(returnDate!!.time),
            preferences = selectedPreferences()
        )

        TripLogger.logTransition("TripSetupActivity", "AttractionListActivity", trip)

        // A viagem inteira viaja dentro do Intent. Como Trip é Serializable,
        // basta uma chamada para levar todos os campos de uma vez.
        val intent = Intent(this, AttractionListActivity::class.java)
        intent.putExtra(Extras.TRIP, trip)
        startActivity(intent)
    }

    // Aviso mostrado quando falta preencher algum campo ou quando uma
    // escolha do usuário precisa ser explicada.
    private fun showAlert(titleResId: Int, message: String) {
        AlertDialog.Builder(this)
            .setTitle(titleResId)
            .setMessage(message)
            .setPositiveButton(R.string.dialog_ok, null)
            .show()
    }

    companion object {
        private const val TAG = "TripSetupActivity"
    }
}
