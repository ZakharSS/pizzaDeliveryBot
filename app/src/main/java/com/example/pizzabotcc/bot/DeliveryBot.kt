package com.example.pizzabotcc.bot

import android.graphics.Point
import com.example.pizzabotcc.model.DeliveryPoint
import kotlin.math.absoluteValue

/**
 * This class is responsible for Pizza Bot functionality: reading users commands and building a route.
 */
class DeliveryBot private constructor() {

    private val deliveryPoints = mutableListOf<DeliveryPoint>()
    private var yAxisSize = 0
    private var xAxisSize = 0

    /**
     * Parses a command containing a size of the grid and coordinates of delivery points
     *
     * @param command a string command should match the pattern (Ex.: 7x7 (4, 3) (2, 5))
     * @return a list of [DeliveryPoint]s with their coordinates
     *
     * @throws IllegalArgumentException in case command doesn't match the pattern
     */
    fun parseCommand(command: String): List<DeliveryPoint> {
        deliveryPoints.clear()

        val rootPattern = "(\\dx\\d)\\s*((\\(\\d,\\s*\\d\\)\\s*)+)".toRegex()
        val matchFound = rootPattern.matches(command)
        if (!matchFound) {
            throw IllegalArgumentException("Command format is incorrect")
        }
        val commandParts = rootPattern.findAll(command).firstOrNull()?.groups

        commandParts?.get(AXES_SIZE)?.value?.split("x")?.let {
            yAxisSize = it.first().trim().toIntOrNull() ?: 0
            xAxisSize = it.last().trim().toIntOrNull() ?: 0
        }

        // parse delivery points (Ex.: (4, 3) (2, 5))
        val deliveryPointsPattern = "(\\((\\d,\\s*\\d)\\)\\s*)+?".toRegex()

        commandParts?.get(DELIVERY_POINTS)?.value?.let {
            var x = -1
            var y = -1
            deliveryPointsPattern.findAll(it).forEach { matchResult ->
                matchResult.groups[DELIVERY_POINTS]?.value?.split(",")?.let { points ->
                    x = points.first().trim().toIntOrNull() ?: -1
                    y = points.last().trim().toIntOrNull() ?: -1
                }
                if (x !in 0..xAxisSize || y !in 0..yAxisSize) throw IllegalArgumentException("Delivery coordinates are out of specified zone")

                DeliveryPoint(x, y).also { point ->
                    deliveryPoints.add(point)
                }
            }
        }?: throw IllegalArgumentException("Command format is incorrect")

        return deliveryPoints

    }

    /**
     * @return String with movement instructions for the delivery bot
     *
     * @throws IllegalArgumentException if there are no delivery points for building a route
     */
    fun buildRoute(): String {
        if (deliveryPoints.isEmpty()) {
            throw IllegalArgumentException("There are no delivery points")
        }

        return composeRoute()
    }

    /**
     * @return String with movement instructions for the delivery bot
     *
     */
    private fun composeRoute(): String {

        val currentBotPoint = Point(0, 0)
        val route = StringBuilder()

        // sort delivery points by the nearest distance to the bot's start point (0,0)
        deliveryPoints.sorted().forEach { deliveryPoint ->
            // --- method moveBot
            // Bot's movements for both axes
            val movementCountByX = deliveryPoint.x - currentBotPoint.x
            val movementCountByY = deliveryPoint.y - currentBotPoint.y

            // get movement command for both axes
            val xCommand = getCommandForX(movementCountByX)
            val yCommand = getCommandForY(movementCountByY)

            // add movement commands to the result command string
            repeat(movementCountByX.absoluteValue) {
                route.append(xCommand)
            }
            repeat(movementCountByY.absoluteValue) {
                route.append(yCommand)
            }

            //--- end method move Bot

            // Bot has reached the delivery point

            route.append(DROP_PIZZA)

            currentBotPoint.x = deliveryPoint.x
            currentBotPoint.y = deliveryPoint.y
        }

        return route.toString()
    }

    /**
     * @param movementCountByX is a quantity of movements to the delivery point by X axes
     *
     * @return a corresponding movement instruction which means direction by X axes (watch [DIRECTION_EAST], [DIRECTION_WEST]) or empty string in case of zero value
     */
    private fun getCommandForX(movementCountByX: Int): String = when {
        movementCountByX > 0 -> DIRECTION_EAST
        movementCountByX < 0 -> DIRECTION_WEST
        else -> ""
    }

    /**
     * @param movementCountByY is a quantity of movements to the delivery point by Y axes
     *
     * @return a corresponding movement instruction which means direction by Y axes (watch [DIRECTION_NORTH], [DIRECTION_SOUTH]) or empty string in case of zero value
     */
    private fun getCommandForY(movementCountByY: Int): String = when {
        movementCountByY > 0 -> DIRECTION_NORTH
        movementCountByY < 0 -> DIRECTION_SOUTH
        else -> ""
    }

    companion object {

        private const val AXES_SIZE: Int = 1
        private const val DELIVERY_POINTS: Int = 2

        private const val DIRECTION_EAST = "E"
        private const val DIRECTION_WEST = "W"
        private const val DIRECTION_NORTH = "N"
        private const val DIRECTION_SOUTH = "S"
        private const val DROP_PIZZA = "D"

        @Volatile
        private var INSTANCE: DeliveryBot? = null

        fun getInstance() = INSTANCE ?: synchronized(this) {
            DeliveryBot().also { INSTANCE = it }
        }
    }
}