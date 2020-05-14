package hu.bme.aut.android.tourguide

class MyFilter(val cityNameList: MutableList<String> = mutableListOf(), val minDist: Double = 0.0, val maxDist: Double = 0.0, val minTime: Double = 0.0, val maxTime: Double = 0.0)