package com.cardvlaue.sys.searchselect

import android.content.Context
import android.graphics.Color
import android.text.TextUtils
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.LinearLayout
import com.cardvlaue.sys.R
import com.cardvlaue.sys.data.SearchQueryDO
import org.jetbrains.anko.*
import java.util.*

/**
 * Created by cardvalue on 2017/2/9.
 */
class SearchSelectAdapter : BaseAdapter() {

    var listData = ArrayList<SearchQueryDO>()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return ItemView(listData[position].title).createView(AnkoContext.Companion.create(parent.context))
    }

    override fun getItem(position: Int): Any = listData[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getCount(): Int = listData.size

    fun updateData(list: List<SearchQueryDO>) {
        listData.clear()
        listData.addAll(list)
        notifyDataSetChanged()
    }

    class ItemView(val string: String?) : AnkoComponent<Context> {
        override fun createView(ui: AnkoContext<Context>): View = with(ui) {
            frameLayout {
                backgroundColor = Color.WHITE

                linearLayout {
                    orientation = LinearLayout.HORIZONTAL
                    leftPadding = dip(12)
                    rightPadding = dip(12)
                    gravity = Gravity.CENTER_VERTICAL

                    textView {
                        text = string
                        textSize = 14f
                        textColor = Color.BLACK
                        maxLines = 1
                        ellipsize = TextUtils.TruncateAt.END
                    }.lparams {
                        width = wrapContent
                        height = wrapContent
                        weight = 1f
                    }
                    imageView {
                        imageResource = R.mipmap.icon_more
                        scaleType = ImageView.ScaleType.CENTER
                    }.lparams {
                        width = dip(20)
                        height = dip(20)
                    }
                }.lparams {
                    width = matchParent
                    height = dip(50)
                }
            }
        }
    }
}