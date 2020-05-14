package hu.bme.aut.android.tourguide

import java.io.Serializable

class User(var uid: String = "", var phoneNumber: String = "", var name: String = "", var email: String = "", var password: String = "", val cities: MutableList<City> = mutableListOf()): Serializable