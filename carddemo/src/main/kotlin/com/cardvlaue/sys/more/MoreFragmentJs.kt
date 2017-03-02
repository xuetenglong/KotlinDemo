package com.cardvlaue.sys.more

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.cardvlaue.sys.main.MainActivity
import com.cardvlaue.sys.view.AnkoComponents
import com.taobao.weex.utils.WXFileUtils
import org.jetbrains.anko.AnkoContext

class MoreFragmentJs : Fragment() {

    private lateinit var mView: FrameLayout
    private lateinit var mActivity: MainActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mView = AnkoComponents.MatchFrameLayout().createView(AnkoContext.Companion.create(context))
        mActivity = activity as MainActivity
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mActivity.setContainer(mView)
        return mView
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mActivity.renderPage(WXFileUtils.loadAsset("more-lists.js", activity))
    }
}