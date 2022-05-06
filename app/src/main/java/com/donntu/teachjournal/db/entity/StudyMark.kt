package com.donntu.teachjournal.db.entity


class StudyMark {
    enum class Mark(val code: Int) {
        TYPE_NO_MARK(0), TYPE_MARK(1), TYPE_SYMBOL (2), TYPE_ABSENT(3)
    }
    var studentId: Int? = null
    var classId: Int? = null
    var type: Int? = null
    var mark: Int? = null
    var symbol: String? = null
    var note: String? = null

    constructor(id: Int, studentId: Int, classId: Int, type: Int, mark: Int, symbol: String, note: String){
        //this.id = id
        this.classId = classId
        this.studentId = studentId
        this.type = type
        this.mark = mark
        this.symbol = symbol
        this.note = note
    }
    constructor(studentId: Int, classId: Int, type: Int){
        this.classId = classId
        this.studentId = studentId
        this.type = type
    }

    override fun toString(): String {
        when (type) {
            Mark.TYPE_MARK.code -> return mark.toString()
            Mark.TYPE_SYMBOL.code -> return symbol.toString()
            Mark.TYPE_ABSENT.code -> return note.toString()
            Mark.TYPE_NO_MARK.code -> return ""
        }
        return ""
    }
}