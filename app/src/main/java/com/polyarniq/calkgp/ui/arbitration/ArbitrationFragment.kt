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

    private var selectedOption: ArbOption? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentArbitrationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, options.map { it.name })
        binding.dropdownType.setAdapter(adapter)

        binding.dropdownType.setOnItemClickListener { _, _, position, _ ->
            val option = options[position]
            selectedOption = option
            binding.amountCard.visibility = if (option.needsAmount) View.VISIBLE else View.GONE
            binding.editAmount.text?.clear()
        }

        binding.btnCalculate.setOnClickListener {
            val option = selectedOption
            if (option == null) {
                Toast.makeText(requireContext(), "Выберите тип заявления", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val amount = if (option.needsAmount) {
                val amountStr = binding.editAmount.text.toString().replace(",", ".")
                val parsed = amountStr.toDoubleOrNull()
                if (parsed == null || parsed <= 0) {
                    Toast.makeText(requireContext(), getString(R.string.error_amount), Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                parsed
            } else 0.0

            val result = DutyCalculator.calculateArbitration(option.type, amount)
            findNavController().navigate(
                R.id.action_arbitration_to_result,
                bundleOf("amount" to result.amount, "legalRef" to result.legalReference, "description" to result.description)
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
