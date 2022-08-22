package kesam.learning.a7minutesworkoutapp

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "history-table")
data class HistoryEntity(
    @PrimaryKey//(autoGenerate = true)
    val date: String
)
