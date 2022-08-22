package kesam.learning.a7minutesworkoutapp

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import kesam.learning.a7minutesworkoutapp.databinding.ItemHistoryRowBinding

class ItemAdapter(
    private val items: ArrayList<String>,
     private val deleteListener: (date:String) -> Unit
) :
    RecyclerView.Adapter<ItemAdapter.ViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemHistoryRowBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val date: String = items[position]
        val context = holder.itemView.context // This is only to add color from our color pallete


        holder.tvItemNumber.text = (position + 1).toString()
        holder.tvDate.text = date

        // Updating the background color according to the odd/even positions in list.
        if (position % 2 == 0) {
            holder.llHistoryItemMain.setBackgroundColor(
                ContextCompat.getColor(
                    context,
                    R.color.colorLightGray
                )
            )
        } else {
            holder.llHistoryItemMain.setBackgroundColor(Color.parseColor("#FFFFFF"))
        }


        holder.ivDelete.setOnClickListener {
            deleteListener.invoke(date)
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    class ViewHolder(binding: ItemHistoryRowBinding) : RecyclerView.ViewHolder(binding.root) {
        val llHistoryItemMain = binding.llHistoryItemMain
        val tvItemNumber = binding.tvItemNumber
        val tvDate = binding.tvDate
        val ivDelete = binding.ivDelete
    }



}