package br.com.example.tripplanning.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import br.com.example.tripplanning.R
import br.com.example.tripplanning.model.Attraction

// Adapter da lista personalizada de atividades.
// O RecyclerView não sabe nada sobre atividades: é este adapter que
// transforma cada objeto Attraction em um cartão na tela.
//
// onAttractionClick é a função que a Activity passa para ser chamada
// quando o usuário toca em um cartão. Assim o adapter não precisa
// conhecer a tela de detalhes.
class AttractionAdapter(
    private val attractions: List<Attraction>,
    private val onAttractionClick: (Attraction) -> Unit
) : RecyclerView.Adapter<AttractionAdapter.AttractionViewHolder>() {

    // Guarda as views de um cartão já encontradas pelo findViewById.
    // É o que faz o RecyclerView ser rápido: ao rolar a lista ele
    // reaproveita os cartões que saíram da tela em vez de criar outros.
    class AttractionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val imgAttraction: ImageView = itemView.findViewById(R.id.imgAttraction)
        val txtName: TextView = itemView.findViewById(R.id.txtAttractionName)
        val txtDescription: TextView = itemView.findViewById(R.id.txtAttractionDescription)
        val txtInfo: TextView = itemView.findViewById(R.id.txtAttractionInfo)
    }

    // Cria um cartão vazio a partir do layout item_attraction.
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AttractionViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_attraction, parent, false)
        return AttractionViewHolder(itemView)
    }

    override fun getItemCount(): Int = attractions.size

    // Preenche um cartão com os dados da atividade daquela posição.
    override fun onBindViewHolder(holder: AttractionViewHolder, position: Int) {
        val attraction = attractions[position]

        holder.imgAttraction.setImageResource(attraction.imageResId)
        holder.txtName.text = attraction.name
        holder.txtDescription.text = attraction.description
        holder.txtInfo.text = holder.itemView.context.getString(
            R.string.attraction_info,
            attraction.preference.label,
            attraction.defaultDurationHours,
            attraction.defaultDifficulty.label
        )

        holder.itemView.setOnClickListener { onAttractionClick(attraction) }
    }
}
