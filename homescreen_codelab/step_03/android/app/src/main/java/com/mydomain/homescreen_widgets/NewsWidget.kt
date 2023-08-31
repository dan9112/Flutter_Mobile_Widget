package com.mydomain.homescreen_widgets

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.util.TypedValue
import android.widget.RemoteViews

import es.antonborri.home_widget.HomeWidgetPlugin
import java.io.File

/**
 * Implementation of App Widget functionality.
 */
class NewsWidget : AppWidgetProvider() {
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {
            val widgetData = HomeWidgetPlugin.getData(context)
            val views = RemoteViews(context.packageName, R.layout.news_widget).apply {
                val title = widgetData.getString("headline_title", null)
                setTextViewText(R.id.headline_title, title ?: "No title set")

                val description = widgetData.getString("headline_description", null)
                setTextViewText(R.id.headline_description, description ?: "No description set")

                widgetData.getString("filename", null).also { imageName ->
                    File(imageName).also { imageFile ->
                        if (imageFile.exists()) {
                            val inBitmap = BitmapFactory.decodeFile(imageFile.absolutePath)

                            val colorValue = TypedValue()
                            context.theme.resolveAttribute(android.R.attr.colorBackground, colorValue, true)

                            setImageViewBitmap(R.id.widget_image, inBitmap.changeBackgroundColor(colorValue.data))
                        } else {
                            println("image not found!, looked @: $imageName")
                        }
                    }
                }
            }

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }

    private fun Bitmap.changeBackgroundColor(color: Int) = run {
        Bitmap.createBitmap(width, height, config).also {
            Canvas(it).run {
                drawColor(color)
                drawBitmap(this@changeBackgroundColor, 0f, 0f, null)
            }
            recycle()
        }
    }
}
