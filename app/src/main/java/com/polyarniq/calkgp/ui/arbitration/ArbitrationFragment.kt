package com.polyarniq.calkgp.ui.arbitration

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.polyarniq.calkgp.R
import com.polyarniq.calkgp.calculator.DutyCalculator
import com.polyarniq.calkgp.databinding.FragmentArbitrationBinding
import com.polyarniq.calkgp.model.ArbitrationType

class ArbitrationFragment : Fragment() {

    private var _binding: FragmentArbitrationBinding? = null
    private val binding get() = _binding!!

    private data class ArbOption(val name: String, val type: ArbitrationType, val needsAmount: Boolean)

    private val options = listOf(
        ArbOption("Иск имущественного характера", ArbitrationType.PROPERTY_CLAIM, true),
        ArbOption("Судебный приказ", ArbitrationType.COURT_ORDER, true),
        ArbOption("Иск неимущественного (ИП)", ArbitrationType.NON_PROPERTY_IP, false),
        ArbOption("Иск неимущественного (орг.)", ArbitrationType.NON_PROPERTY_ORG, false),
        ArbOption("Апелляционная жалоба (ИП)", ArbitrationType.APPEAL, false),
        ArbOption("Кассационная жалоба (ИП)", ArbitrationType.CASSATION, false),
        ArbOption("Надзорная жалоба в ВС РФ (ИП)", ArbitrationType.SUPERVISORY, false),
        ArbOption("Банкротство (ИП)", ArbitrationType.BANKRUPTCY_IP, false),
        ArbOption("Банкротство (организация)", ArbitrationType.BANKRUPTCY_ORG, false)
    )

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentArbitrationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toggleGroup.check(R.id.btnNonProperty)
        binding.propertyCard.visibility = View.GONE
        binding.nonPropertyCard.visibility = View.VISIBLE

        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, options.map { it.name })
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
                Toast.makeText(requireContext(), getString(R.string.error_amount), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val result = DutyCalculator.calculateArbitration(ArbitrationType.PROPERTY_CLAIM, amount)
            navigateToResult(result.amount, result.legalReference, result.description)
        }

        binding.dropdownType.setOnItemClickListener { _, _, position, _ ->
            val option = options[position]
            if (option.needsAmount) {
                binding.toggleGroup.check(R.id.btnProperty)
                binding.propertyCard.visibility = View.VISIBLE
                binding.nonPropertyCard.visibility = View.GONE
            } else {
                val result = DutyCalculator.calculateArbitration(option.type)
                navigateToResult(result.amount, result.legalReference, result.description)
            }
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
