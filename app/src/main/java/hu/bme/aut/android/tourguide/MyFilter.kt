package hu.bme.aut.android.tourguide

import java.io.Serializable

class MyFilter(val cityNameList: MutableList<String> = mutableListOf(), val minDist: Double = 0.0, val maxDist: Double = 1000.0, val minTime: Double = 0.0, val maxTime: Double = 1000.0): Serializable