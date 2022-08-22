package kesam.learning.a7minutesworkoutapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import kesam.learning.a7minutesworkoutapp.databinding.ActivityExerciseBinding
import kesam.learning.a7minutesworkoutapp.databinding.ActivityFinalBinding
import kesam.learning.a7minutesworkoutapp.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class FinalActivity : AppCompatActivity() {

    private var binding: ActivityFinalBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFinalBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        binding?.toolbarFinish?.setNavigationOnClickListener {
            onBackPressed()
        }

        binding?.btnFinish?.setOnClickListener {
            //val intent = Intent(this@FinalActivity, MainActivity::class.java)
            //startActivity(intent) --> Don't use it because we need to close the activities
            finish()
        }

        val dao = (application as WorkOutApp).db.historyDao()
        addDateToDatabase(dao)

        /*
        setSupportActionBar(binding?.toolbarFinish)
        if (supportActionBar != null){
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }

         */
    }

    private fun addDateToDatabase(historyDao: HistoryDao){

        val myCalendar = Calendar.getInstance()
        val dateTime = myCalendar.time
        Log.e("Date: ", "" + dateTime)

        val sdf = SimpleDateFormat("dd MMM yyyy HH:mm:ss", Locale.getDefault())
        val date = sdf.format(dateTime)
        Log.e("Formatted Date : ", "" + date)

        CoroutineScope(Dispatchers.IO).launch {
            historyDao.insert(HistoryEntity(date))
            Log.e("Date : ", "Added...")
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}

