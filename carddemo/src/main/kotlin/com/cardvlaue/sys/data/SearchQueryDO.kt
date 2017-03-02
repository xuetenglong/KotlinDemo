package com.cardvlaue.sys.data

/**
 * Created by cardvalue on 2017/2/10.
 */
class SearchQueryDO {
    var id = ""
    var title = ""

    constructor()
    constructor(id: String, title: String) {
        this.id = id
        this.title = title
    }
}