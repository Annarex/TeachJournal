package com.donntu.teachjournal.db.utils

import android.database.Cursor

class DataCheck {
    companion object {
    fun checkCursor(cur: Cursor): Boolean?{
        if(cur == null) return false;
        if(!cur.moveToFirst()){
            cur.close()
            return false
        }
        return true
    }
    }
}