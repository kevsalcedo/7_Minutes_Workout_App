package kesam.learning.a7minutesworkoutapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import kesam.learning.a7minutesworkoutapp.databinding.ActivityHistoryBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HistoryActivity : AppCompatActivity() {

    private  var binding: ActivityHistoryBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        setSupportActionBar(binding?.toolbarHistory)
        if (supportActionBar != null){
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.title = "HISTORY"
        }

        binding?.toolbarHistory?.setNavigationOnClickListener {
            onBackPressed()
        }

        val dao = (application as WorkOutApp).db.historyDao()
        getAllCompletedDates(dao)
    }

    private fun getAllCompletedDates(historyDao: HistoryDao){
        lifecycleScope.launch {
            historyDao.fetchAllDates().collect{ allCompletedDatesList ->
                if (allCompletedDatesList.isNotEmpty()){

                    binding?.tvNoRecordsAvailable?.visibility = View.GONE
                    binding?.rvItemsList?.visibility = View.VISIBLE
                    binding?.tvHistoryBelow?.visibility = View.VISIBLE

                    binding?.rvItemsList?.layoutManager = LinearLayoutManager(this@HistoryActivity)

                    val dates = ArrayList<String>()
                    for (date in allCompletedDatesList){
                        dates.add(date.date)
                    }
                    val historyAdapter = ItemAdapter(ArrayList(dates))
                    { deleteId ->
                        lifecycleScope.launch {
                            historyDao.fetchHistoryById(deleteId).collect {
                                if (it != null) {
                                    deleteRecordAlertDialog(historyDao, it)

                                }
                            }
                        }
                    }



                    binding?. rvItemsList?.adapter = historyAdapter

                }else{
                    binding?.tvHistoryBelow?.visibility = View.GONE
                    binding?.rvItemsList?.visibility = View.GONE
                    binding?.tvNoRecordsAvailable?.visibility = View.VISIBLE
                }

            }
        }
    }


    private fun deleteRecordAlertDialog(historyDao: HistoryDao,history: HistoryEntity) {
        val builder = AlertDialog.Builder(this)
        //set title for alert dialog
        builder.setTitle("Delete Record")
        //set message for alert dialog
        builder.setMessage("Are you sure you wants to delete ${history.date}.")
        builder.setIcon(android.R.drawable.ic_dialog_alert)

        //performing positive action
        builder.setPositiveButton("Yes") { dialogInterface, _ ->
            CoroutineScope(Dispatchers.IO).launch {
                historyDao.delete(history)
                dialogInterface.dismiss() // Dialog will be dismissed
            }
        }
        builder.setNegativeButton("No") { dialogInterface, _ ->
            dialogInterface.dismiss() // Dialog will be dismissed
        }
        // Create the AlertDialog
        val alertDialog: AlertDialog = builder.create()
        // Set other dialog properties
        alertDialog.setCancelable(false) // Will not allow user to cancel after clicking on remaining screen area.
        alertDialog.show()  // show the dialog to UI
    }



    override fun onDestroy() {
        super.onDestroy()

        binding = null
    }

}