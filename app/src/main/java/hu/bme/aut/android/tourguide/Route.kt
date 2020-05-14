package hu.bme.aut.android.tourguide

import java.io.Serializable

class Route( val name: String= "",val description: String = "", val distance: Double = 0.0, val time: Int = 0, val city: String = "", val points: MutableList<Point> = mutableListOf()): Serializable
