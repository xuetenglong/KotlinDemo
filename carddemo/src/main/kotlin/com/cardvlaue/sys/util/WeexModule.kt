package com.cardvlaue.sys.util

import android.content.Intent
import android.net.Uri
import android.support.v7.app.AlertDialog
import com.cardvlaue.sys.about.AboutActivity
import com.cardvlaue.sys.customerservice.CustomerServiceActivity
import com.cardvlaue.sys.feedback.FeedBackOneActivity
import com.cardvlaue.sys.financeintention.FinanceUseActivity
import com.cardvlaue.sys.shopadd.BusIndustrySelect
import com.cardvlaue.sys.webshow.WebShowActivity
import com.taobao.weex.annotation.JSMethod
import com.taobao.weex.common.WXModule
import timber.log.Timber

/**
 * Created by cardvalue on 2017/1/11.
 */
class WeexModule : WXModule() {

    @JSMethod
    fun openNativeWeb(title: String, url: String) {
        val context = mWXSDKInstance.context
        context.startActivity(Intent(context, WebShowActivity::class.java)
                .putExtra(WebShowActivity.EXTRA_COLOR, "1001")
                .putExtra(WebShowActivity.EXTRA_TITLE, title)
                .putExtra(WebShowActivity.EXTRA_URL, url))
    }

    /**
     * 拨打客服电话
     */
    @JSMethod
    fun callService(title: String, phone: String, yes: String, no: String) {
        val context = mWXSDKInstance.context
        AlertDialog.Builder(context).setMessage(title).setPositiveButton(yes) { dialogInterface, i ->
            val intent = Intent(Intent.ACTION_DIAL, Uri.parse(phone))
            if (IntentUtils.isCallPhoneIntentSafe(context, intent))
                context.startActivity(intent)
        }.setNegativeButton(no, null).show()
    }

    /**
     * 打开关于我们
     */
    @JSMethod
    fun startAbout() {
        val context = mWXSDKInstance.context
        context.startActivity(Intent(context, AboutActivity::class.java))
    }

    /**
     * 打开意见反馈
     */
    @JSMethod
    fun startFeedback() {
        val context = mWXSDKInstance.context
        context.startActivity(Intent(context, FeedBackOneActivity::class.java))
    }

    /**
     * 打开在线客服
     */
    @JSMethod
    fun startServiceOnline() {
        val context = mWXSDKInstance.context
        context.startActivity(Intent(context, CustomerServiceActivity::class.java))
    }

    /**
     * 选择融资用途
     */
    @JSMethod
    fun selectUse(msg: String) {
        Timber.i("selectUse:%s", msg)
        val selectEvent = BusIndustrySelect(FinanceUseActivity.BUS_FINANCE_USE)
        selectEvent.title = msg
        RxBus2.Companion.get().send(selectEvent)
    }
}