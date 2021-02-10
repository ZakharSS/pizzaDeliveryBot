package com.example.pizzabotcc.model

/**
 * A point that waits for pizza
 * X and Y are coordinates of the point on cartesian plane
 */

data class DeliveryPoint(val x: Int, val y: Int): Comparable<DeliveryPoint>{

    /**
     * Square of the distance from (0,0) to the deliver point
     */
    private val squareDistanceToDeliverPoint: Int
        get() = x.times(2) + y.times(2)

    override fun compareTo(other: DeliveryPoint): Int {
        val distance = squareDistanceToDeliverPoint
        val otherDistance = other.squareDistanceToDeliverPoint
        return when {
            distance == otherDistance -> 0
            distance > otherDistance -> 1
            else -> -1
        }
    }
}