package com.alexis.wallpaperchanger

import android.app.WallpaperManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.util.Log

class ScreenReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if(intent != null){
            if(intent.action.equals(Intent.ACTION_SCREEN_ON)){
                Log.v("Pantalla", "Se encendio")
                val bitmap = BitmapFactory.decodeResource(context?.resources,R.drawable.volcano)
                val wallpaperManager = WallpaperManager.getInstance(context)
                wallpaperManager.setBitmap(bitmap)
            }
        }
    }
}