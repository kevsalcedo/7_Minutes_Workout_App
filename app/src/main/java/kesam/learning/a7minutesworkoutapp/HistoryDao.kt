package kesam.learning.a7minutesworkoutapp

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface HistoryDao {

    @Insert
    fun insert(historyEntity: HistoryEntity)

    @Query("Select * from `history-table`")
    fun fetchAllDates(): Flow<List<HistoryEntity>>

    @Delete
    fun delete(historyEntity: HistoryEntity)

    @Query("Select * from `history-table` where date=:date")
    fun fetchHistoryById(date:String): Flow<HistoryEntity>

}