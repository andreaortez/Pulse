import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat.*
import androidx.recyclerview.widget.RecyclerView
import com.example.medilink.R
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

data class DayUi(val date: LocalDate)

class DayCalendarAdapter(
    private val days: List<DayUi>,
    private val onDaySelected: (LocalDate) -> Unit,
    initialSelectedIndex: Int,

    ) : RecyclerView.Adapter<DayCalendarAdapter.DayViewHolder>() {

    private var selectedPosition = 2

    class DayViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvDayNumber: TextView = itemView.findViewById(R.id.tvDayNumber)
        val tvDayName: TextView = itemView.findViewById(R.id.tvDayName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_calendar_day, parent, false)
        return DayViewHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: DayViewHolder, position: Int) {
        val item = days[position]
        val date = item.date

        val localeEs = Locale("es", "ES")

        val dayNumber = date.format(DateTimeFormatter.ofPattern("dd", localeEs))
        val dayName = date.format(
            DateTimeFormatter.ofPattern("EEE", localeEs)
        ).replaceFirstChar { it.titlecase(localeEs) }



        holder.tvDayNumber.text = dayNumber
        holder.tvDayName.text = dayName

        val isSelected = position == selectedPosition
        holder.itemView.isSelected = isSelected

        val white = getColor(holder.itemView.context, android.R.color.white)
        getColor(holder.itemView.context, android.R.color.white)


        holder.tvDayNumber.setTextColor(white)
        holder.tvDayName.setTextColor(white)

        holder.itemView.setOnClickListener {
            val oldPos = selectedPosition
            selectedPosition = holder.bindingAdapterPosition
            notifyItemChanged(oldPos)
            notifyItemChanged(selectedPosition)
            onDaySelected(date)
        }
    }

    override fun getItemCount(): Int = days.size
}
