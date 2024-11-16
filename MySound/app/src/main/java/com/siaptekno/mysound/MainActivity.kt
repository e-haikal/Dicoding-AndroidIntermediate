package com.siaptekno.mysound

import android.media.SoundPool
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {

    private lateinit var sp: SoundPool
    private var soundId: Int = 0
    private var spLoaded = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnSound = findViewById<Button>(R.id.btn_sound_pool)

        sp = SoundPool.Builder()
            .setMaxStreams(10)
            .build()

        /*
      Tambahkan listener ke soundpool jika proses load sudah selesai
       */
        sp.setOnLoadCompleteListener { _, _, status ->
            if (status == 0) {
                spLoaded = true
            } else {
                Toast.makeText(this@MainActivity, "Gagal load", Toast.LENGTH_SHORT).show()
            }
        }

        /*
   Load raw weather_forecast ke soundpool, jika selesai maka id nya dimasukkan ke variable soundId
    */
        soundId = sp.load(this, R.raw.weather_forecast, 1)


        btnSound.setOnClickListener {
            if (spLoaded) {
                sp.play(soundId, 1f, 1f, 0, 0, 1f)
            }
        }
    }
}