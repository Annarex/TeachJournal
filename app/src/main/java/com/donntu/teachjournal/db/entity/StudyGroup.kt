package com.donntu.teachjournal.db.entity


class StudyGroup {
    var code : String? = ""
    var semester : Int? = 1
    //private String specialtyCode;
    //private String specialty;


    constructor(id: Int?, code: String?) { //}, String specialtyCode, String specialty) {
        this.code = code
        //this.specialtyCode = specialtyCode;
        //this.specialty = specialty;
    }

    override fun toString(): String {
        return code!!
    }
}