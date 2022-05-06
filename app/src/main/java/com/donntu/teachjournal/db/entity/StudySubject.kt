package com.donntu.teachjournal.db.entity


class StudySubject  {
    var title: String? = null
    var abbr: String? = null

    constructor(id:Int, title:String,abbr:String){
        //this.id = id
        this.title = title
        this.abbr = abbr
    }

    override fun toString(): String {
        return abbr + " - "+ title
    }
}