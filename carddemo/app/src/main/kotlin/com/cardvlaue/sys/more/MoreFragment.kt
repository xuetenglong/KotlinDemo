package com.cardvlaue.sys.more

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.cardvlaue.sys.R
import com.cardvlaue.sys.about.AboutActivity
import com.cardvlaue.sys.customerservice.CustomerServiceActivity
import com.cardvlaue.sys.feedback.FeedBackOneActivity
import com.cardvlaue.sys.view.AnkoComponents
import com.cardvlaue.sys.util.IntentUtils
import com.cardvlaue.sys.util.ScreenUtil
import com.cardvlaue.sys.data.source.remote.UrlConstants
import com.cardvlaue.sys.webshow.WebShowActivity
import org.jetbrains.anko.*
import org.jetbrains.anko.support.v4.UI

class MoreFragment : Fragment() {

    private lateinit var mToolbarView: Toolbar

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        mToolbarView = AnkoComponents.ToolbarBlueNoBackComponent("更多").createView(AnkoContext.Companion.create(context))

        val strategyView = AnkoComponents.MoreItemUI(R.mipmap.gonglue, STR_STRATEGY).createView(AnkoContext.Companion.create(context))
        val introductionView = AnkoComponents.MoreItemUI(R.mipmap.pruoduce, STR_INTRODUCTION).createView(AnkoContext.Companion.create(context))
        val honourView = AnkoComponents.MoreItemUI(R.mipmap.honor, STR_HONOUR).createView(AnkoContext.Companion.create(context))
        val contactView = AnkoComponents.MoreItemUI(R.mipmap.contact, STR_CONTRACT).createView(AnkoContext.Companion.create(context))
        val aboutView = AnkoComponents.MoreItemUI(R.mipmap.about, "关于我们").createView(AnkoContext.Companion.create(context))
        val feedbackView = AnkoComponents.MoreItemUI(R.mipmap.opinion, "意见反馈").createView(AnkoContext.Companion.create(context))
        val serviceView = AnkoComponents.MoreItemUI(R.mipmap.kefu, "在线客服").createView(AnkoContext.Companion.create(context))

        strategyView.onClick {
            startActivity(Intent(context, WebShowActivity::class.java)
                    .putExtra(WebShowActivity.EXTRA_COLOR, "1001")
                    .putExtra(WebShowActivity.EXTRA_TITLE, getString(R.string.more_faq))
                    .putExtra(WebShowActivity.EXTRA_URL, UrlConstants.STRATEGY))
        }
        introductionView.onClick {
            startActivity(Intent(context, WebShowActivity::class.java)
                    .putExtra(WebShowActivity.EXTRA_COLOR, "1001")
                    .putExtra(WebShowActivity.EXTRA_TITLE, getString(R.string.more_introduction))
                    .putExtra(WebShowActivity.EXTRA_URL, UrlConstants.INTRODUCTION))
        }
        honourView.onClick {
            startActivity(Intent(context, WebShowActivity::class.java)
                    .putExtra(WebShowActivity.EXTRA_COLOR, "1001")
                    .putExtra(WebShowActivity.EXTRA_TITLE, getString(R.string.more_honour))
                    .putExtra(WebShowActivity.EXTRA_URL, UrlConstants.HONOUR))
        }
        contactView.onClick {
            startActivity(Intent(context, WebShowActivity::class.java)
                    .putExtra(WebShowActivity.EXTRA_COLOR, "1001")
                    .putExtra(WebShowActivity.EXTRA_TITLE, getString(R.string.more_contact))
                    .putExtra(WebShowActivity.EXTRA_URL, UrlConstants.CONTACT))
        }
        aboutView.onClick { startActivity(Intent(context, AboutActivity::class.java)) }
        feedbackView.onClick { startActivity(Intent(context, FeedBackOneActivity::class.java)) }
        serviceView.onClick { startActivity(Intent(context, CustomerServiceActivity::class.java)) }

        return UI {
            linearLayout {
                orientation = LinearLayout.VERTICAL
                backgroundColor = ContextCompat.getColor(ctx, R.color.app_background_color)

                view {
                    id = ID_STATUS_BAR
                    backgroundColor = ContextCompat.getColor(ctx, R.color.app_main_color)
                }.lparams {
                    width = matchParent
                    height = dip(24)
                }

                addView(mToolbarView)

                scrollView {

                    linearLayout {
                        orientation = LinearLayout.VERTICAL

                        linearLayout {
                            backgroundColor = Color.WHITE

                            imageView {
                                imageResource = R.mipmap.icon_more_phone
                            }.lparams {
                                gravity = Gravity.CENTER_VERTICAL
                                leftMargin = dip(40)
                                rightMargin = dip(16)
                            }

                            linearLayout {
                                orientation = LinearLayout.VERTICAL

                                textView("4008-803-803") {
                                    textSize = 17f
                                    textColor = Color.BLACK
                                }

                                textView("服务时间周一至周五：9:00-18:00") {
                                    textSize = 12f
                                    textColor = Color.parseColor("#606060")
                                }
                            }.lparams {
                                gravity = Gravity.CENTER_VERTICAL
                            }

                            onClick {
                                AlertDialog.Builder(ctx).setMessage("拨打客服电话").setPositiveButton("呼叫") { dialogInterface, i ->
                                    val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:4008-803-803"))
                                    if (IntentUtils.isCallPhoneIntentSafe(ctx, intent))
                                        startActivity(intent)
                                }.setNegativeButton("取消", null).show()
                            }
                        }.lparams {
                            width = matchParent
                            height = dip(88)
                        }

                        view { }.lparams {
                            height = dip(8)
                        }

                        /**
                         * 融资攻略
                         */
                        addView(strategyView)

                        view { }.lparams {
                            height = dip(8)
                        }

                        /**
                         * 公司简介
                         */
                        addView(introductionView)

                        view {
                            backgroundColor = Color.parseColor("#E8E8E8")
                        }.lparams {
                            height = 1
                        }

                        /**
                         * 资质荣誉
                         */
                        addView(honourView)

                        view {
                            backgroundColor = Color.parseColor("#E8E8E8")
                        }.lparams {
                            height = 1
                        }

                        /**
                         * 联系方式
                         */
                        addView(contactView)

                        view {
                            backgroundColor = Color.parseColor("#E8E8E8")
                        }.lparams {
                            height = 1
                        }

                        /**
                         * 关于我们
                         */
                        addView(aboutView)

                        view { }.lparams {
                            height = dip(8)
                        }

                        /**
                         * 意见反馈
                         */
                        addView(feedbackView)

                        view {
                            backgroundColor = Color.parseColor("#E8E8E8")
                        }.lparams {
                            height = 1
                        }

                        /**
                         * 在线客服
                         */
                        addView(serviceView)
                    }

                }.lparams {
                    width = matchParent
                    height = matchParent
                }
            }
        }.view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).setSupportActionBar(mToolbarView)
        mToolbarView.setContentInsetsRelative(0, 0)
        ScreenUtil.statusBarHeight(resources, view.findViewById(ID_STATUS_BAR))
    }

    override fun onResume() {
        super.onResume()
        ScreenUtil.FlymeSetStatusBarLightMode(activity.window, false)
        ScreenUtil.setStatusBarDarkMode(true, activity)
    }

    companion object {
        val ID_STATUS_BAR = 1001

        val STR_STRATEGY = "融资攻略"
        val STR_INTRODUCTION = "公司简介"
        val STR_HONOUR = "资质荣誉"
        val STR_CONTRACT = "联系方式"
    }
}