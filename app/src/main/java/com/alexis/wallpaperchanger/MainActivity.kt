package com.alexis.wallpaperchanger

import android.app.Activity
import android.app.WallpaperManager
import android.content.Intent
import android.content.IntentFilter
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.work.*
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import kotlinx.coroutines.delay
import java.lang.Error
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    private val reqCode = 5
    private lateinit var wallpaper: ImageView
    private  var wallpaperURI: Uri? = null
    private lateinit var chooseButton: Button
    private lateinit var setButton: Button
    private lateinit var filter: IntentFilter
    private  var mReceiver: ScreenReceiver? = null

    lateinit var mAdView : AdView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        MobileAds.initialize(this, "ca-app-pub-3940256099942544~3347511713")


        this.wallpaper = findViewById(R.id.wallpaper)
        this.chooseButton = findViewById(R.id.chooseBtn)
        this.setButton = findViewById(R.id.setBtn)

        filter = IntentFilter(Intent.ACTION_SCREEN_ON)
        filter.addAction(Intent.ACTION_SCREEN_OFF)
        filter.addAction(Intent.ACTION_USER_PRESENT)

        mReceiver = ScreenReceiver()
        registerReceiver(mReceiver, filter)

        this.chooseButton.setOnClickListener { onChooseImageClick() }
        this.setButton.setOnClickListener { setWallpaper() }
        val workManager = WorkManager.getInstance(applicationContext)
        workManager.cancelAllWork()

        mAdView = findViewById(R.id.adView)
        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)
    }

    private fun setWallpaper() {
        val wallpaperManager = WallpaperManager.getInstance(applicationContext)
        val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, this.wallpaperURI)
        try {
            wallpaperManager.setBitmap(bitmap)
            Toast.makeText(applicationContext, "Wallpaper set", Toast.LENGTH_SHORT).show()

            val constraints = Constraints.Builder().setRequiredNetworkType(NetworkType.NOT_REQUIRED).setRequiresBatteryNotLow(false).build()
            val periodicWorker = PeriodicWorkRequest.Builder(WallpaperWorker::class.java, 15,TimeUnit.MINUTES).setConstraints(constraints).build()



            //val workManager = WorkManager.getInstance(applicationContext)
            //workManager.enqueue(periodicWorker)
            //WorkManager.getInstance(applicationContext).enqueue(periodicWorker)
            val workManager = WorkManager.getInstance(applicationContext)
            workManager.cancelAllWork()

            workManager.enqueueUniquePeriodicWork("WALLP",ExistingPeriodicWorkPolicy.KEEP,periodicWorker)

            //WorkManager.getInstance(applicationContext).enqueueUniquePeriodicWork("wallpaperChange",ExistingPeriodicWorkPolicy.KEEP,periodicWorker)



        }catch (e: Error){
            Toast.makeText(applicationContext, "An error occurred", Toast.LENGTH_SHORT).show()
        }
    }

    private fun onChooseImageClick() {
        openGallery()
    }

    private fun openGallery() {
        val intent =  Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent,reqCode)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK && requestCode == reqCode){
            if(data != null) {
                val res = data.data
                wallpaperURI = res
                wallpaper.setImageURI(res)
                wallpaper.visibility = View.VISIBLE
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if(mReceiver != null){
            unregisterReceiver(mReceiver)
            mReceiver = null
        }
    }
}
