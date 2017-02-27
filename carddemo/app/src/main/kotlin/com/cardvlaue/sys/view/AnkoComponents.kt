package com.cardvlaue.sys.view

import android.content.Context
import android.graphics.Color
import android.support.v4.content.ContextCompat
import android.view.Gravity
import android.widget.ImageView
import com.cardvlaue.sys.R
import org.jetbrains.anko.*
import org.jetbrains.anko.appcompat.v7.toolbar

class AnkoComponents {

    /**
     * 空白帧布局
     */
    class MatchFrameLayout() : AnkoComponent<Context> {
        override fun createView(ui: AnkoContext<Context>) = with(ui) {
            frameLayout { lparams { width = matchParent; height = matchParent; backgroundColor = ContextCompat.getColor(ctx, R.color.app_background_color) } }
        }
    }

    /**
     * 无返回标题栏（蓝）
     */
    class ToolbarBlueNoBackComponent(val str: String) : AnkoComponent<Context> {
        override fun createView(ui: AnkoContext<Context>) = with(ui) {
            toolbar {
                backgroundColor = ContextCompat.getColor(ctx, R.color.app_main_color)

                textView(str) {
                    textColor = Color.WHITE
                    textSize = 20f
                    gravity = Gravity.CENTER
                }.lparams {
                    width = matchParent
                    height = dip(50)
                }
            }
        }
    }

    /**
     * 无返回标题栏（白）
     */
    class ToolbarWhiteNoBackComponent(val str: String) : AnkoComponent<Context> {
        override fun createView(ui: AnkoContext<Context>) = with(ui) {
            toolbar {
                backgroundColor = Color.WHITE

                textView(str) {
                    textColor = ContextCompat.getColor(ctx, R.color.text_big_color)
                    textSize = 20f
                    gravity = Gravity.CENTER
                }.lparams {
                    width = matchParent
                    height = dip(50)
                }
            }
        }
    }

    /**
     * 有返回标题栏（蓝）
     */
    class ToolbarBlueHasBackComponent(val str: String, val back: () -> Unit) : AnkoComponent<Context> {
        override fun createView(ui: AnkoContext<Context>) = with(ui) {
            toolbar {
                backgroundColor = ContextCompat.getColor(ctx, R.color.app_main_color)

                frameLayout {

                    textView(str) {
                        textColor = Color.WHITE
                        textSize = 20f
                    }.lparams { gravity = Gravity.CENTER }

                    imageButton {
                        imageResource = R.mipmap.icon_back_white
                        backgroundColor = Color.TRANSPARENT
                        scaleType = ImageView.ScaleType.FIT_CENTER
                        onClick { back() }
                    }.lparams {
                        width = dip(50)
                        height = matchParent
                    }
                }.lparams {
                    width = matchParent
                    height = dip(50)
                }
            }
        }
    }

    /**
     * 有返回标题栏（白）
     */
    class ToolbarWhiteHasBackComponent(val str: String, val back: () -> Unit) : AnkoComponent<Context> {
        override fun createView(ui: AnkoContext<Context>) = with(ui) {
            toolbar {
                backgroundColor = Color.WHITE

                frameLayout {

                    textView(str) {
                        textColor = ContextCompat.getColor(ctx, R.color.text_big_color)
                        textSize = 20f
                    }.lparams { gravity = Gravity.CENTER }

                    frameLayout {
                        onClick { back() }

                        imageView {
                            imageResource = R.mipmap.icon_back_black
                            scaleType = ImageView.ScaleType.FIT_XY
                        }.lparams {
                            width = dip(16)
                            height = dip(24)
                            gravity = Gravity.CENTER
                        }
                    }.lparams {
                        width = dip(50)
                        height = matchParent
                    }
                }.lparams {
                    width = matchParent
                    height = dip(50)
                }
            }
        }
    }

    /**
     * 更多 Item 公共布局组件
     */
    class MoreItemUI(val img: Int, val txt: String) : AnkoComponent<Context> {
        override fun createView(ui: AnkoContext<Context>) = with(ui) {
            frameLayout {
                linearLayout {
                    backgroundColor = Color.WHITE
                    leftPadding = dip(16)
                    rightPadding = dip(16)
                    gravity = Gravity.CENTER_VERTICAL

                    imageView {
                        imageResource = img
                        scaleType = ImageView.ScaleType.FIT_XY
                    }.lparams {
                        width = dip(20)
                        height = dip(20)
                    }

                    textView(txt) {
                        textColor = ContextCompat.getColor(ctx, R.color.text_big_color)
                        textSize = 14f
                    }.lparams {
                        width = 0
                        weight = 1f
                        leftMargin = dip(16)
                        rightMargin = dip(16)
                    }

                    imageView {
                        imageResource = R.mipmap.icon_more_gray
                    }.lparams {
                        width = dip(12)
                        height = dip(12)
                    }

                }.lparams {
                    width = matchParent
                    height = dip(48)
                }
            }
        }
    }

    /**
     * 融资意向 Item 公共布局组件
     */
    class FinanceIntentItemUI(val lStr: String, val rStr: String, val rId: Int) : AnkoComponent<Context> {
        override fun createView(ui: AnkoContext<Context>) = with(ui) {
            linearLayout {
                gravity = Gravity.CENTER_VERTICAL

                textView(lStr) {
                    textColor = ContextCompat.getColor(ctx, R.color.text_big_color)
                    textSize = 14f
                }

                view { }.lparams {
                    width = 0
                    height = 1
                    weight = 1f
                }

                textView(rStr) {
                    id = rId
                    textColor = ContextCompat.getColor(ctx, R.color.app_main_color)
                    textSize = 14f
                }

                imageView {
                    imageResource = R.mipmap.icon_more_gray
                }.lparams {
                    width = dip(12)
                    height = dip(12)
                    leftMargin = dip(4)
                }
            }
        }
    }

}