package br.com.example.tripplanning.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import br.com.example.tripplanning.R
import br.com.example.tripplanning.model.PlannedAttraction

// Adapter da ListView do resumo da viagem.
//
// A ListView é mais antiga que o RecyclerView e não obriga a usar um
// ViewHolder: se a gente não fizer nada, ela chama findViewById para cada
// item toda vez que ele aparece na tela, o que deixa a rolagem travada.
// O reaproveitamento aqui é feito à mão, com o convertView e a tag da view.
// É exatamente esse trabalho que o RecyclerView já faz sozinho.
class PlannedAttractionAdapter(
    private val context: Context,
    private var items: List<PlannedAttraction>,
    private var detailed: Boolean
) : BaseAdapter() {

    // Guarda as views de um item para não procurá-las de novo.
    private class ViewHolder(itemView: View) {
        val imgPlanned: ImageView = itemView.findViewById(R.id.imgPlanned)
        val txtName: TextView = itemView.findViewById(R.id.txtPlannedName)
        val txtInfo: TextView = itemView.findViewById(R.id.txtPlannedInfo)
        val txtDescription: TextView = itemView.findViewById(R.id.txtPlannedDescription)
        val txtNotes: TextView = itemView.findViewById(R.id.txtPlannedNotes)
    }

    // Troca a lista e o modo de exibição, e manda a ListView se redesenhar.
    fun update(newItems: List<PlannedAttraction>, newDetailed: Boolean) {
        items = newItems
        detailed = newDetailed
        notifyDataSetChanged()
    }

    override fun getCount(): Int = items.size

    override fun getItem(position: Int): PlannedAttraction = items[position]

    override fun getItemId(position: Int): Long = items[position].attraction.id.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        // convertView é um item que saiu da tela e pode ser reaproveitado.
        // Só quando ele vem nulo é que vale a pena inflar um layout novo.
        val itemView = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.item_planned_attraction, parent, false)

        val holder = itemView.tag as? ViewHolder ?: ViewHolder(itemView).also { itemView.tag = it }
        val planned = items[position]

        holder.imgPlanned.setImageResource(planned.attraction.imageResId)
        holder.txtName.text = planned.attraction.name
        holder.txtInfo.text = context.getString(
            R.string.summary_item_info,
            planned.startTime,
            planned.durationHours,
            planned.difficulty.label
        )

        holder.txtDescription.text = planned.attraction.description
        holder.txtNotes.text = context.getString(R.string.summary_notes, planned.notes)

        // No modo compacto sobram só o nome e a linha de informações.
        // GONE (em vez de INVISIBLE) faz o cartão encolher de verdade.
        holder.txtDescription.visibility = if (detailed) View.VISIBLE else View.GONE
        holder.txtNotes.visibility =
            if (detailed && planned.notes.isNotBlank()) View.VISIBLE else View.GONE

        return itemView
    }
}
