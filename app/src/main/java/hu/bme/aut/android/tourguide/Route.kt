package hu.bme.aut.android.tourguide

import java.io.Serializable

class Route( val name: String= "Lajos",val description: String = "Lajos", val distance: Double = -1.1, val time: Int = -1, val city: String = "Lajos", val points: MutableList<Point> = mutableListOf()): Serializable
