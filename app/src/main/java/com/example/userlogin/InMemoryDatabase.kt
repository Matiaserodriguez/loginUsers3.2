package com.example.userlogin


data class InMemoryDatabase(val users: MutableList<User> = mutableListOf())

class User(owner: InMemoryDatabase, usr: String, pass: String) {
    val username = usr
    val password = pass
    private var courses : MutableMap<Int, String> = mutableMapOf()

    init {
        owner.users.add(this)
    }

    fun addCourse(courseCode: Int, courseName: String){
        courses[courseCode] = courseName
    }

    fun removeCourse (courseCode: Int) {
        courses.remove(courseCode)
    }
}

var db = InMemoryDatabase()