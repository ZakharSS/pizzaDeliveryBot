package com.example.pizzabotcc

import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo.IME_ACTION_DONE
import androidx.appcompat.app.AppCompatActivity
import com.example.pizzabotcc.bot.DeliveryBot
import com.example.pizzabotcc.databinding.ActivityMainBinding
import com.example.pizzabotcc.extensions.hideSoftKeyboard

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val deliveryBot = DeliveryBot.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupToolbar()
        binding.btnApplyCommand.setOnClickListener { applyCommand() }
        binding.btnBuildRoute.setOnClickListener { buildRoute() }
        binding.etCommandInput.setOnEditorActionListener { view, actionId, event ->
            when (actionId) {
                IME_ACTION_DONE -> {
                    applyCommand()
                    true
                }
                else -> false
            }
        }
    }

    private fun applyCommand() {
        binding.tvRouteInstruction.text = null
        binding.tvPizzaDeliverCount.text = null
        binding.tvRouteInstructionTitle.visibility = View.GONE

        binding.etCommandInput.text?.toString()?.let { command ->
            try {
                val deliveryPoints = deliveryBot.parseCommand(command)
                binding.btnBuildRoute.isEnabled = deliveryPoints.isNotEmpty()
                binding.tvPizzaDeliverCount.text = resources.getQuantityString(
                    R.plurals.pizzaCount,
                    deliveryPoints.size,
                    deliveryPoints.size
                )
                binding.inputLayoutCommand.error = null
                hideSoftKeyboard()
            } catch (e: IllegalArgumentException) {
                binding.inputLayoutCommand.error = e.message
                binding.btnBuildRoute.isEnabled = false
            }
        } ?: run {
            binding.inputLayoutCommand.error = "Please, type a command"
            binding.btnBuildRoute.isEnabled = false
        }
    }

    private fun buildRoute() =
        try {
            val deliveryRoute = deliveryBot.buildRoute()
            binding.tvRouteInstructionTitle.visibility = if (deliveryRoute.isEmpty()) View.GONE else View.VISIBLE
            binding.tvRouteInstruction.text = deliveryRoute
            binding.inputLayoutCommand.error = null
        } catch (e: java.lang.IllegalArgumentException) {
            binding.tvRouteInstruction.text = null
            binding.inputLayoutCommand.error = e.message
        }

    private fun setupToolbar() {
        supportActionBar?.run {
            title = getString(R.string.toolbar_title)
            setDisplayShowHomeEnabled(true)
            setIcon(R.drawable.ic_pizza)
        }
    }
}