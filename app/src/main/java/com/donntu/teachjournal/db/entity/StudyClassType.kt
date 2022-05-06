package com.donntu.teachjournal.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class StudyClassType {
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    var title: String? = null
    var abbr: String? = null

    constructor(id:Int, title:String,abbr:String){
        this.title = title
        this.abbr = abbr
    }

    override fun toString(): String {
        return abbr + " - "+ title
    }
}