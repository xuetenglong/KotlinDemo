package com.cardvlaue.sys.data

/*
 * 事件类
 */

/**
 * 行业选择事件
 */
data class SearchSelectEvent(val event: String, val data: SearchQueryDO)

/**
 * 底部导航栏
 */
data class MainTabEvent(val event: String, val data: List<TabItemDO>)

/**
 * 导航栏图片和文字颜色
 */
data class TabItemDO(val url: String, val color: String?)

/**
 * 事件常量
 */
object EventConst {

    /**
     * 应用退出
     */
    val APP_EXIT = "EventConst_EventConst"
    /**
     * 行业选择
     */
    val INDUSTRY_SELECT = "EventConst_INDUSTRY_SELECT"
    /**
     * 地址选择
     */
    val ADDRESS_SELECT = "EventConst_ADDRESS_SELECT"
}