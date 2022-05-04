package com.donntu.teachjournal.db.utils

class BaseJournalEntity {
    protected var id: Long? = null

    @JvmName("getId1")
    fun getId(): Long?{
        return id
    }
}