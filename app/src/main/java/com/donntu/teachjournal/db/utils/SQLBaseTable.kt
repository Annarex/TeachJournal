package com.donntu.teachjournal.db.utils

import android.annotation.SuppressLint
import android.content.ContentValues
import android.database.Cursor
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase


open class SQLBaseTable<T> {
    var KEY_ID : String? = "_id"
    var sqlDatabase:SQLiteDatabase? = null
    fun onOpenDb(db:SQLiteDatabase){
        this.sqlDatabase = db
    }
    open fun getTableName(): String? {return ""}
    open fun loadDbItem(cr : Cursor?): T? { return null }
    open fun convertToCv(item: T?): ContentValues? { return null}
    fun get(selection:String?, args: Array<String?>?) : T? {
        return get(selection, args, null)
    }
    fun get(selection:String?, args: Array<String?>?, orderBy: String?) : T? {
        var cur:Cursor? = sqlDatabase?.query(
            getTableName(),
            null,
            selection,
            args,
            null,
            null,
            orderBy
        )
        if(cur?.count == 0){ return null}
        var item: T? = loadDbItem(cur!!)
        cur.close()
        return item
    }
    fun get(id: Int?): T?{
        return  get(KEY_ID+" = ?", arrayOf(id.toString()), null)
    }
    fun getCount(selection: String, args: Array<String>): Int?{
        var res : Int = 0
        var sql : String? = "select count(*) from "+getTableName()+" where "+ selection + ";"
        var cur: Cursor? = sqlDatabase?.rawQuery(sql, args);
        if (DataCheck.checkCursor(cur!!) == true) {
            res = cur.getInt(0);
            cur.close();
        }
        return res
    }

    fun getAll(orderBy:String?): List<T>?{
        var cur: Cursor? = sqlDatabase?.query(
            getTableName(),
            null,
            null,
            null,
            null,
            null,
            orderBy
        )
        return getList(cur)
    }

    fun getList(selection: String?, selectionArgs: Array<String?>?, groupBy: String?, orderBy: String?
    ): List<T>? {
        val cur: Cursor? = sqlDatabase!!.query(
            getTableName(),  // The table name
            null,
            selection,  // SQL WHERE clause
            selectionArgs,  // selectionArgs to SQL WHERE clause
            groupBy,  // GROUP BY clause
            null,
            orderBy
        ) // SQL ORDER BY clause
        return getList(cur)
    }

    fun getList(cur: Cursor?): List<T>? {
        val itemList: ArrayList<T> = ArrayList()
        if (DataCheck.checkCursor(cur!!)!!) {
            do {
                val item = loadDbItem(cur)
                itemList.add(item!!)
            } while (cur.moveToNext())
            cur.close()
        }
        return itemList
    }


    fun getStringList(column: String?, selection: String?, selectionArgs: Array<String?>? ): List<String?>? {
        return getStringList(column, selection, selectionArgs, null)
    }

    @SuppressLint("Range")
    fun getStringList(column: String?, selection: String?, selectionArgs: Array<String?>?, groupBy: String?): List<String?>? {
        val itemList: ArrayList<String?> = ArrayList()
        assert(column != null)
        val cur = sqlDatabase!!.query(
            getTableName(), arrayOf(column),  // A list of which columns to return
            selection,
            selectionArgs,
            groupBy,
            null,
            column
        )
        if (DataCheck.checkCursor(cur)!!) {
            do {
                itemList.add(cur.getString(cur.getColumnIndex(column)))
            } while (cur.moveToNext())
            cur.close()
        }
        return itemList
    }

    open fun getAllInColumn(column: String?): kotlin.collections.List<String?>? {
        return getStringList(column, null, null)
    }

    protected open fun delete(selection: String?, selectionArgs: Array<String>?): Int {
        return sqlDatabase!!.delete(getTableName(), selection, selectionArgs)
    }

    open fun delete(id: Long): Int {
        return delete("$KEY_ID = ?", arrayOf(id.toString()))
    }

    open fun deleteAll(): Int {
        return sqlDatabase!!.delete(
            getTableName(),
            null,
            null
        )
    }

    open fun insert(item: T): Long {
        val cv: ContentValues? = convertToCv(item)
        return sqlDatabase!!.insert(getTableName(), null, cv)
    }

    open fun update(oldId: Long, item: T): Int {
        val cv: ContentValues? = convertToCv(item)
        return try {
            sqlDatabase!!.update(getTableName(), cv, "$KEY_ID = ?", arrayOf(oldId.toString()))
        } catch (e: SQLException) {
            e.printStackTrace()
            //            Log.e(DebugUtils.TAG_DEBUG,
            //                  "Error updating:" + " Table " + getTableName() + "; id = " + oldId, e);
            -1
        }
    }

    protected open fun update(selection: String?, selectionArgs: Array<String?>?, item: T): Int {
        val cv: ContentValues? = convertToCv(item)
        return try {
            sqlDatabase!!.update(getTableName(), cv, selection, selectionArgs)
        } catch (e: SQLException) {
            e.printStackTrace()
            //            Log.e(DebugUtils.TAG_DEBUG, "Error updating:" + " Table " + getTableName()
            //                    + "; class = " + item.getClass(), e);
            -1
        }
    }

    open fun exists(id: Long): Boolean {
        return getCount("$KEY_ID = ?", arrayOf(id.toString()))!! > 0
    }

    open fun exists(item: T): Boolean {
        val itemList: List<T>? = getAll(null)
        return itemList!!.contains(item)
    }
}