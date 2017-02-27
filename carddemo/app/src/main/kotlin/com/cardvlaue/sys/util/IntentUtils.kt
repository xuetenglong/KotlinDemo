package com.cardvlaue.sys.util

import android.content.Context
import android.content.Intent

/**
 * Intent
 */
object IntentUtils {

    fun isCallPhoneIntentSafe(context: Context, intent: Intent): Boolean = context.packageManager.queryIntentActivities(intent, 0).size > 0
}