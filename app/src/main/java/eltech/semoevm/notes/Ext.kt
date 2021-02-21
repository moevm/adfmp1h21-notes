package eltech.semoevm.notes

import android.content.Context
import android.content.res.Configuration
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.content.res.Resources
import android.util.TypedValue

fun Context.getScreenWidthDp(): Int {
    val px = resources.displayMetrics.widthPixels
    return (px / resources.displayMetrics.density).toInt()
}