package com.cardvlaue.sys.data

import java.util.*

/**
 * Created by cardvalue on 2017/2/13.
 */
class HomeImageDO {
    var resultCode = ""
    var resultMsg = ""
    var resultData = ArrayList<HomeImageItemDO>()

    fun success() = "1" == resultCode
}