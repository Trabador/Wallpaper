package com.alexis.wallpaperchanger

import android.app.WallpaperManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import kotlin.random.Random

class WallpaperWorker(appContext: Context, workerParams: WorkerParameters): Worker(appContext, workerParams) {
    private val context = appContext
    override fun doWork(): Result {
        setWallpaperWork()
        return Result.success()
    }

    private fun setWallpaperWork() {
        val wallpaperManager = WallpaperManager.getInstance(applicationContext)

        var bitmap: Bitmap?
        val index = Random.nextInt(1,4)
        when(index){
            1 -> bitmap = BitmapFactory.decodeResource(context.resources,R.drawable.anime)
            2 -> bitmap = BitmapFactory.decodeResource(context.resources,R.drawable.sombra)
            3 -> bitmap = BitmapFactory.decodeResource(context.resources,R.drawable.it)
            else -> bitmap = BitmapFactory.decodeResource(context.resources,R.drawable.volcano)
        }
        Log.v("pruebas", index.toString()+" Worker ")

        wallpaperManager.setBitmap(bitmap)
    }
}