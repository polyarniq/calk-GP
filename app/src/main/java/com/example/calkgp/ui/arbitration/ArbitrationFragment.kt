package com.example.calkgp.ui.arbitration

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.calkgp.R
import com.example.calkgp.calculator.DutyCalculator
import com.example.calkgp.databinding.FragmentArbitrationBinding
import com.example.calkgp.model.ArbitrationType

class ArbitrationFragment : Fragment() {

    private var _binding: FragmentArbitrationBinding? = null
    private val binding get() = _binding!!

    private val nonPropertyTypes = listOf(
        "Неимущественный иск (ИП)" to ArbitrationType.NON_PROPERTY_IP,
        "Неимущественный иск (организация)" to ArbitrationType.NON_PROPERTY_ORG
    )

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentArbitrationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toggleGroup.check(R.id.btnProperty)

        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, nonPropertyTypes.map { it.first })
        binding.dropdownType.setAdapter(adapter)

        binding.toggleGroup.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                binding.propertyCard.visibility = if (checkedId == R.id.btnProperty) View.VISIBLE else View.GONE
                binding.nonPropertyCard.visibility = if (checkedId == R.id.btnNonProperty) View.VISIBLE else View.GONE
            }
        }

        binding.btnCalculate.setOnClickListener {
            val amountStr = binding.editAmount.text.toString().replace(",", ".")
            val amount = amountStr.toDoubleOrNull()
            if (amount == null || amount <= 0) {
                Toast.makeText(requireContext(), "Введите сумму иска", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val result = DutyCalculator.calculateArbitration(ArbitrationType.PROPERTY_CLAIM, amount)
            navigateToResult(result.amount, result.legalReference, result.description)
        }

        binding.dropdownType.setOnItemClickListener { _, _, position, _ ->
            val type = nonPropertyTypes[position].second
            val result = DutyCalculator.calculateArbitration(type)
            navigateToResult(result.amount, result.legalReference, result.description)
        }
    }

    private fun navigateToResult(amount: Double, legalRef: String, description: String) {
        findNavController().navigate(
            R.id.action_arbitration_to_result,
            bundleOf("amount" to amount.toFloat(), "legalRef" to legalRef, "description" to description)
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
