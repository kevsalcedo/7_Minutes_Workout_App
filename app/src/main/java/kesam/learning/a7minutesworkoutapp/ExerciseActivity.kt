package kesam.learning.a7minutesworkoutapp

import android.app.Dialog
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isInvisible
import androidx.recyclerview.widget.LinearLayoutManager
import kesam.learning.a7minutesworkoutapp.databinding.ActivityExerciseBinding
import kesam.learning.a7minutesworkoutapp.databinding.ActivityMainBinding
import kesam.learning.a7minutesworkoutapp.databinding.DialogCustomBackConfirmationBinding
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList

class ExerciseActivity : AppCompatActivity(), TextToSpeech.OnInitListener {

    private var binding: ActivityExerciseBinding? = null

    private var restTimer: CountDownTimer? = null // Variable for Timer which will be initialized later.
    private var restProgress = 0 // PauseOffset = timerDuration - time left
    private var restTimerDuration: Long = 1

    private var workTimer: CountDownTimer? = null
    private var workProgress = 0
    private var workTimerDuration: Long = 1

    private var exerciseList: ArrayList<ExerciseModel>? = null
    private var currentExercisePosition = -1

    private var tts: TextToSpeech? = null
    private var player: MediaPlayer? = null

    private var exerciseAdapter : ExerciseStatusAdapter? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExerciseBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        setSupportActionBar(binding?.toolbarExercise)
        if (supportActionBar != null){
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }

        binding?.toolbarExercise?.setNavigationOnClickListener {
            customDialogForBackButton()
        }

        tts = TextToSpeech(this, this)

        exerciseList = Constants.defaultExerciseList()

        setupRestView()

        setupExerciseStatusRecyclerView()

    }

    private fun setupRestView(){
        try{
            val soundURI = Uri.parse(
                "android.resource://kesam.learning.a7minutesworkoutapp/" +
                        R.raw.press_start)
            player = MediaPlayer.create(applicationContext,soundURI)
            player?.isLooping = false
            player?.start()
        }catch (e: Exception){
            e.printStackTrace()
        }

        binding?.flRestProgressBar?.visibility = View.VISIBLE
        binding?.tvRestTimer?.visibility = View.VISIBLE
        binding?.tvExerciseName?.visibility = View.INVISIBLE
        binding?.flWorkProgressBar?.visibility = View.INVISIBLE
        binding?.ivExercise?.visibility = View.INVISIBLE
        binding?.tvTitle?.visibility = View.VISIBLE
        binding?.tvUpcomingLabel?.visibility = View.VISIBLE
        binding?.tvUpcomingExerciseName?.visibility = View.VISIBLE

        if (restTimer != null){
            restTimer?.cancel()
            restProgress = 0
        }

        binding?.tvUpcomingExerciseName?.text = exerciseList!![currentExercisePosition + 1].getName()
        setRestProgressBar()
    }
    private fun setRestProgressBar(){
        binding?.restProgressBar?.progress = restProgress

        restTimer = object : CountDownTimer(restTimerDuration * 1000,1000 ){
            override fun onTick(millisUntilFinished: Long) {
                restProgress++
                binding?.restProgressBar?.progress = 10 - restProgress
                binding?.tvRestTimer?.text = (10 - restProgress).toString()
            }
            override fun onFinish() {
                currentExercisePosition++

                exerciseList!![currentExercisePosition].setIsSelected(true)
                exerciseAdapter!!.notifyDataSetChanged()

                setupWorkView()
            }
        }.start()
    }

    private fun setupWorkView(){
        binding?.flRestProgressBar?.visibility = View.INVISIBLE
        binding?.tvRestTimer?.visibility = View.INVISIBLE
        binding?.tvExerciseName?.visibility = View.VISIBLE
        binding?.flWorkProgressBar?.visibility = View.VISIBLE
        binding?.ivExercise?.visibility = View.VISIBLE
        binding?.tvTitle?.visibility = View.INVISIBLE
        binding?.tvUpcomingLabel?.visibility = View.INVISIBLE
        binding?.tvUpcomingExerciseName?.visibility = View.INVISIBLE

        if (workTimer != null){
            workTimer?.cancel()
            workProgress = 0
        }

        speakOut(exerciseList!![currentExercisePosition].getName())

        binding?.ivExercise?.setImageResource(exerciseList!![currentExercisePosition].getImage())
        binding?.tvExerciseName?.text = exerciseList!![currentExercisePosition].getName()

        setWorkProgressBar()
    }

    private fun setWorkProgressBar(){
        binding?.workProgressBar?.progress = workProgress

        workTimer = object : CountDownTimer(workTimerDuration * 1000,1000 ){
            override fun onTick(millisUntilFinished: Long) {
                workProgress++
                binding?.workProgressBar?.progress = 30 - workProgress
                binding?.tvWorkTimer?.text = (30 - workProgress).toString()
            }
            override fun onFinish() {

                if (currentExercisePosition < exerciseList?.size!! - 1){
                    exerciseList!![currentExercisePosition].setIsSelected(false)
                    exerciseList!![currentExercisePosition].setIsCompleted(true)
                    exerciseAdapter!!.notifyDataSetChanged()

                    setupRestView()
                }else{
                    finish()
                    val intent = Intent(this@ExerciseActivity, FinalActivity::class.java)
                    startActivity(intent)
                }
        }}.start()
    }


    override fun onBackPressed() { //This is only for the back button in the bottom
        customDialogForBackButton()
        //super.onBackPressed() is not needed
    }

    private fun customDialogForBackButton(){
        val customDialog = Dialog(this)
        val dialogBinding = DialogCustomBackConfirmationBinding.inflate(layoutInflater)
        customDialog.setContentView(dialogBinding.root) // the custom dialog should look like the XML file DialogCustomBackConfirmation"
        customDialog.setCanceledOnTouchOutside(false) // Don't cancel the dialog when touch outside of the window
        dialogBinding.btnYes.setOnClickListener{
            this@ExerciseActivity.finish() // Close the exercise activity and go back to Main Activity
        }
        dialogBinding.btnNo.setOnClickListener{
            customDialog.dismiss() // Dismiss the custom dialog
        }
        customDialog.show()
    }

    private fun setupExerciseStatusRecyclerView (){
        // layoutManager property allow us to make changes as distribute items like a grid view,
        // or position items next to each other
        binding?.rvExerciseStatus?.layoutManager =
            // GridLayoutManager() -> third option
            LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL, false)

        exerciseAdapter = ExerciseStatusAdapter(exerciseList!!)
        binding?.rvExerciseStatus?.adapter = exerciseAdapter

    }

    private fun speakOut(text: String) {
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "")
    }

    override fun onDestroy() {
        super.onDestroy()

        if (restTimer != null){
            restTimer?.cancel()
            restProgress = 0
        }

        if (workTimer != null){
            workTimer?.cancel()
            workProgress = 0
        }

        if (tts != null) {
            tts?.stop()
            tts?.shutdown()
        }

        if (player != null) {
            player?.stop()
        }

        binding = null
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts!!.setLanguage(Locale.US)

            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "The Language specified is not supported!")
            }

        } else {
            Log.e("TTS", "Initialization Failed!")
        }
    }


}