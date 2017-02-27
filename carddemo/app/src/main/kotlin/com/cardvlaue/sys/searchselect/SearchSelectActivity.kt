package com.cardvlaue.sys.searchselect

import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.ImageButton
import android.widget.ListView
import android.widget.SearchView
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONObject
import com.cardvlaue.sys.BaseActivity
import com.cardvlaue.sys.CVApplication
import com.cardvlaue.sys.R
import com.cardvlaue.sys.data.EventConst
import com.cardvlaue.sys.data.SearchQueryDO
import com.cardvlaue.sys.data.SearchSelectEvent
import com.cardvlaue.sys.data.source.TasksDataSource
import com.cardvlaue.sys.dialog.ContentLoadingDialog
import com.cardvlaue.sys.util.RxBus2
import com.cardvlaue.sys.util.ToastUtil
import com.cardvlaue.sys.util.bindView
import com.trello.rxlifecycle2.kotlin.bindToLifecycle
import io.reactivex.android.schedulers.AndroidSchedulers
import org.jetbrains.anko.onQueryTextListener
import timber.log.Timber
import java.util.*

/**
 * 搜索和选择
 * Created by cardvalue on 2017/2/9.
 */
class SearchSelectActivity : BaseActivity() {

    val mBackView: ImageButton by bindView(R.id.ibtn_title_search_back)
    val mSearchView: SearchView by bindView(R.id.sv_title_search_button)
    val mListView: ListView by bindView(R.id.lv_search_select_select)
    val mAdapter = SearchSelectAdapter()
    /**
     * 步骤一数据
     */
    val oneStepData = ArrayList<SearchQueryDO>()
    /**
     * 步骤二数据
     */
    val twoStepData = ArrayList<SearchQueryDO>()
    /**
     * 步骤三数据
     */
    val threeStepData = ArrayList<SearchQueryDO>()
    /**
     * 步骤一缓存数据
     */
    val tempData = ArrayList<SearchQueryDO>()
    /**
     * 当前步骤
     * 0 -> 默认
     * 1 -> 步骤一
     * 2 -> 步骤二
     * 3 -> 步骤三
     */
    var currentStep = 0
    var tempStep = 0
    lateinit var mTasksRepository: TasksDataSource
    lateinit var loginId: String
    lateinit var loginToken: String
    /**
     * 步骤一 ID
     */
    lateinit var oneId: String
    /**
     * 步骤一 TITLE
     */
    lateinit var oneTitle: String
    /**
     * 步骤二 ID
     */
    lateinit var twoId: String
    /**
     * 步骤二 TITLE
     */
    lateinit var twoTitle: String
    val mLoadingDialog: ContentLoadingDialog = ContentLoadingDialog.newInstance("加载中...")
    /**
     * 加载标志
     */
    var loadFlag = false
    /**
     * 界面类型
     */
    private var typeFlag = Type.UNKNOWN

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_select)
        mBackView.setOnClickListener {
            Timber.i("后退:$currentStep")
            when (currentStep) {
                2 -> {
                    currentStep--
                    mAdapter.updateData(oneStepData)
                    mSearchView.visibility = View.VISIBLE
                }
                3 -> {
                    currentStep--
                    mAdapter.updateData(twoStepData)
                }
                else -> {
                    currentStep = 0
                    finish()
                }
            }
        }
        mListView.adapter = mAdapter
        mListView.setOnItemClickListener { adapterView, view, i, l ->
            mSearchView.visibility = View.GONE
            when (currentStep) {
                1 -> {
                    oneId = oneStepData[i].id
                    oneTitle = oneStepData[i].title
                    when (typeFlag) {
                        Type.INDUSTRY -> {
                            val queryCondition = JSONObject()
                            queryCondition.put("industryGId", oneId)
                            queryIndustryData(queryCondition.toJSONString())
                        }
                        Type.ADDRESS -> queryCityData(oneId)
                        else -> {
                        }
                    }

                }
                2 -> {
                    twoId = twoStepData[i].id
                    twoTitle = twoStepData[i].title
                    when (typeFlag) {
                        Type.INDUSTRY -> {
                            val queryCondition = JSONObject()
                            queryCondition.put("merchantId", loginId)
                            queryCondition.put("industryPId", twoId)
                            queryIndustryData(queryCondition.toJSONString())
                        }
                        Type.ADDRESS -> queryDistrictData(twoId)
                        else -> {
                        }
                    }
                }
                3 -> {
                    when (typeFlag) {
                        Type.INDUSTRY ->
                            RxBus2.get().send(SearchSelectEvent(EventConst.INDUSTRY_SELECT,
                                    SearchQueryDO("$oneId,$twoId,${threeStepData[i].id}",
                                            "$oneTitle,$twoTitle,${threeStepData[i].title}")))
                        Type.ADDRESS ->
                            RxBus2.get().send(SearchSelectEvent(EventConst.ADDRESS_SELECT,
                                    SearchQueryDO("$oneId,$twoId,${threeStepData[i].id}",
                                            "$oneTitle,$twoTitle,${threeStepData[i].title}")))
                        else -> {
                        }
                    }
                    finish()
                }
                else -> {
                    finish()
                }
            }
        }
        // 点击搜索备份数据
        mSearchView.setOnSearchClickListener {
            tempStep = currentStep
            currentStep = 0
            tempData.clear()
            tempData.addAll(oneStepData)
            oneStepData.clear()
            mAdapter.updateData(oneStepData)
        }
        // 关闭搜索还原数据
        mSearchView.setOnCloseListener {
            currentStep = tempStep
            oneStepData.clear()
            oneStepData.addAll(tempData)
            mAdapter.updateData(oneStepData)
            false
        }
        mSearchView.onQueryTextListener {
            onQueryTextSubmit { query ->
                if (!TextUtils.isEmpty(query)) {
                    when (typeFlag) {
                        Type.INDUSTRY -> {
                            val where = JSONObject()
                            where.put("industryName", query)
                            queryIndustryData(where.toJSONString())
                        }
                        Type.ADDRESS -> searchAddressData(query)
                        else -> {
                        }
                    }
                }
                false
            }
        }
        mLoadingDialog.isCancelable = false
        mTasksRepository = (application as CVApplication).tasksRepositoryComponent.tasksRepository
        val loginData = mTasksRepository.login
        loginId = loginData.objectId
        loginToken = loginData.accessToken
        val typeExtra = intent.getIntExtra(TYPE_FLAG, 0)
        when (typeExtra) {
            1 -> {
                typeFlag = Type.INDUSTRY
                queryIndustryData("")
            }
            2 -> {
                typeFlag = Type.ADDRESS
                queryProvinceData()
            }
            else -> {
                typeFlag = Type.UNKNOWN
            }
        }
    }

    /**
     * 查询行业清单
     */
    fun queryIndustryData(where: String) {
        if (loadFlag) {
            return
        }
        loadStart()

        Timber.i("where:$where")
        mTasksRepository.queryIndustry(loginId, loginToken, where)
                .bindToLifecycle(this)
                .observeOn(AndroidSchedulers.mainThread())
                .filter { responses ->
                    Timber.e("queryIndustryData:%s", JSON.toJSONString(responses))
                    responses != null && !responses.isEmpty()
                }
                .subscribe({ responses ->
                    when (currentStep) {
                        0 -> {
                            currentStep = 1
                            oneStepData.clear()
                            oneStepData.addAll(responses)
                        }
                        1 -> {
                            currentStep = 2
                            twoStepData.clear()
                            twoStepData.addAll(responses)
                        }
                        2 -> {
                            currentStep = 3
                            threeStepData.clear()
                            threeStepData.addAll(responses)
                        }
                        else -> {
                            currentStep = 0
                            oneStepData.clear()
                            twoStepData.clear()
                            threeStepData.clear()
                        }
                    }
                    mAdapter.updateData(responses)
                }, { throwable ->
                    loadEnd()
                    val errorMsg = throwable.message
                    Timber.e("queryIndustryError:%s", errorMsg)
                    if (!TextUtils.isEmpty(errorMsg) && errorMsg!!.contains("No address associated with hostname")) {
                        ToastUtil.showFailure(this, "手机网络不可用")
                    } else {
                        ToastUtil.showFailure(this, "行业调用异常")
                    }
                }) {
                    loadEnd()
                }
    }

    /**
     * 搜索地址清单
     */
    fun searchAddressData(where: String?) {
        if (loadFlag) {
            return
        }
        loadStart()

        val queryCondition = JSONObject()
        queryCondition.put("keyWord", where)
        Timber.i("where:$where")
        mTasksRepository.queryProvince(loginId, loginToken, queryCondition.toJSONString())
                .bindToLifecycle(this)
                .observeOn(AndroidSchedulers.mainThread())
                .filter { responses ->
                    Timber.e("queryCityData:%s", JSON.toJSONString(responses))
                    responses != null
                }
                .filter { responses ->
                    if (responses.isEmpty()) {
                        ToastUtil.showFailure(this, SEARCH_EMPTY)
                        return@filter false
                    }
                    true
                }
                .subscribe({ responses ->
                    currentStep = 1
                    oneStepData.clear()
                    oneStepData.addAll(responses)
                    mAdapter.updateData(responses)
                }, { throwable ->
                    loadEnd()
                    val errorMsg = throwable.message
                    Timber.e("queryIndustryError:%s", errorMsg)
                    if (!TextUtils.isEmpty(errorMsg) && errorMsg!!.contains("No address associated with hostname")) {
                        ToastUtil.showFailure(this, "手机网络不可用")
                    } else {
                        ToastUtil.showFailure(this, "省市区县搜索异常")
                    }
                }) {
                    loadEnd()
                }
    }

    /**
     * 查询省份清单
     */
    fun queryProvinceData() {
        if (loadFlag) {
            return
        }
        loadStart()
        mTasksRepository.queryAddress(loginId, loginToken)
                .bindToLifecycle(this)
                .observeOn(AndroidSchedulers.mainThread())
                .filter { responses ->
                    Timber.e("queryProvinceData:%s", JSON.toJSONString(responses))
                    responses != null && !responses.isEmpty()
                }
                .subscribe({ responses ->
                    currentStep = 1
                    oneStepData.clear()
                    oneStepData.addAll(responses)
                    mAdapter.updateData(responses)
                }, { throwable ->
                    loadEnd()
                    val errorMsg = throwable.message
                    Timber.e("queryProvinceDataError:%s", errorMsg)
                    if (!TextUtils.isEmpty(errorMsg) && errorMsg!!.contains("No address associated with hostname")) {
                        ToastUtil.showFailure(this, "手机网络不可用")
                    } else {
                        ToastUtil.showFailure(this, "省份查询异常")
                    }
                }) {
                    loadEnd()
                }
    }

    /**
     * 查询城市清单
     */
    fun queryCityData(id: String) {
        if (loadFlag) {
            return
        }
        loadStart()
        mTasksRepository.queryAddressProidLists(loginId, loginToken, id)
                .bindToLifecycle(this)
                .observeOn(AndroidSchedulers.mainThread())
                .filter { responses ->
                    Timber.e("queryCityData:%s", JSON.toJSONString(responses))
                    responses != null && !responses.isEmpty()
                }
                .subscribe({ responses ->
                    currentStep = 2
                    twoStepData.clear()
                    twoStepData.addAll(responses)
                    mAdapter.updateData(responses)
                }, { throwable ->
                    loadEnd()
                    val errorMsg = throwable.message
                    Timber.e("queryCityDataError:%s", errorMsg)
                    if (!TextUtils.isEmpty(errorMsg) && errorMsg!!.contains("No address associated with hostname")) {
                        ToastUtil.showFailure(this, "手机网络不可用")
                    } else {
                        ToastUtil.showFailure(this, "城市查询异常")
                    }
                }) {
                    loadEnd()
                }
    }

    /**
     * 查询区县清单
     */
    fun queryDistrictData(id: String) {
        if (loadFlag) {
            return
        }
        loadStart()
        mTasksRepository.queryAddressCountyLists(loginId, loginToken, id)
                .bindToLifecycle(this)
                .observeOn(AndroidSchedulers.mainThread())
                .filter { responses ->
                    Timber.e("queryDistrictData:%s", JSON.toJSONString(responses))
                    responses != null && !responses.isEmpty()
                }
                .subscribe({ responses ->
                    currentStep = 3
                    threeStepData.clear()
                    threeStepData.addAll(responses)
                    mAdapter.updateData(responses)
                }, { throwable ->
                    loadEnd()
                    val errorMsg = throwable.message
                    Timber.e("queryDistrictDataError:%s", errorMsg)
                    if (!TextUtils.isEmpty(errorMsg) && errorMsg!!.contains("No address associated with hostname")) {
                        ToastUtil.showFailure(this, "手机网络不可用")
                    } else {
                        ToastUtil.showFailure(this, "区县查询异常")
                    }
                }) {
                    loadEnd()
                }
    }

    /**
     * 加载开始
     */
    fun loadStart() {
        Timber.i("加载开始")
        loadFlag = true
        if (!mLoadingDialog.isVisible) {
            mLoadingDialog.show(supportFragmentManager, "SearchSelectActivity")
        }
    }

    /**
     * 加载结束
     */
    fun loadEnd() {
        Timber.i("加载结束")
        loadFlag = false
        if (!mLoadingDialog.isHidden) {
            mLoadingDialog.dismiss()
        }
    }

    companion object {
        /**
         * 类型常量
         * 1 -> 行业查询
         * 2 -> 省市区县查询
         */
        val TYPE_FLAG = "type"
        val SEARCH_EMPTY = "未找到结果"
    }

    /**
     * 界面类型
     */
    private enum class Type {
        /**
         * 未知
         */
        UNKNOWN,
        /**
         * 行业
         */
        INDUSTRY,
        /**
         * 地址
         */
        ADDRESS
    }
}