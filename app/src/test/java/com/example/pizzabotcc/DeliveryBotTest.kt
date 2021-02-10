package com.example.pizzabotcc

import com.example.pizzabotcc.bot.DeliveryBot
import org.junit.Assert.assertEquals
import org.junit.Test

import org.junit.Before

class DeliveryBotTest {

    private lateinit var deliveryBot: DeliveryBot

    @Before
    fun setup() {
        deliveryBot = DeliveryBot.getInstance()
    }

    @Test
    fun `test delivery bot create instructions`() {
        val command = "5x5 (1, 3) (4, 4)"
        deliveryBot.parseCommand(command)
        val deliveryRoute = deliveryBot.buildRoute()
        assertEquals(deliveryRoute, "ENNNDEEEND")
    }

    @Test(expected = IllegalArgumentException::class)
    fun `test points out of the delivery zone`(){
        val command = "5x5 (1, 3) (6, 1)"
        deliveryBot.parseCommand(command)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `test command format check`(){
        val incorrectCommand = "5x5 (1.3) 6, 1"
        deliveryBot.parseCommand(incorrectCommand)
    }

}