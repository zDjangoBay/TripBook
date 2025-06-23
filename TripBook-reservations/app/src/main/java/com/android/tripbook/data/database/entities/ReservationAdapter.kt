
//Affichage d'une liste dynamique et performante
class ReservationAdapter(private val reservations: List<Reservation>) :
        RecyclerView.Adapter<ReservationAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtDestination: TextView = itemView.findViewById(R.id.txtDestination)
        val txtDates: TextView = itemView.findViewById(R.id.txtDates)
        val txtTransport: TextView = itemView.findViewById(R.id.txtTransport)
        val txtPersonnes: TextView = itemView.findViewById(R.id.txtPersonnes)
        val txtPrix: TextView = itemView.findViewById(R.id.txtPrix)
        val txtStatut: TextView = itemView.findViewById(R.id.txtStatut)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_reservation, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val res = reservations[position]
        holder.txtDestination.text = res.destination
        holder.txtDates.text = "${res.dateDepart} ➡ ${res.dateRetour}"
        holder.txtTransport.text = "Transport : ${res.moyenTransport}"
        holder.txtPersonnes.text = "Personnes : ${res.nombrePersonnes}"
        holder.txtPrix.text = "Total : ${res.prixTotal} €"
        holder.txtStatut.text = "Statut : ${res.statut}"
    }

    override fun getItemCount(): Int = reservations.size
}
