package com.cardvlaue.sys.util

import android.text.TextUtils

/**
 * Created by cardvalue on 2017/2/14.
 */
object StringUtils {

    fun notEmptyPhone(string: String): String {
        if (TextUtils.isEmpty(string)) {
            return ""
        }
        val stringBuilder = StringBuilder()
        string.forEach { s ->
            if (TextUtils.isDigitsOnly(s.toString())) {
                stringBuilder.append(s)
            }
        }
        return stringBuilder.toString()
    }
}