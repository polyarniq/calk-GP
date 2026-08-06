package com.polyarniq.calkgp.ui.court

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
import com.polyarniq.calkgp.databinding.FragmentCourtBinding
import com.polyarniq.calkgp.model.CourtType

class CourtFragment : Fragment() {

    private var _binding: FragmentCourtBinding? = null
    private val binding get() = _binding!!

    private data class CourtOption(val name: String, val type: CourtType, val needsAmount: Boolean)

    private val options = listOf(
        CourtOption("Иск имущественного характера", CourtType.PROPERTY_CLAIM, true),
        CourtOption("Судебный приказ", CourtType.COURT_ORDER, true),
        CourtOption("Иск неимущественного характера", CourtType.NON_PROPERTY_GENERAL, false),
        CourtOption("Расторжение брака", CourtType.DIVORCE, false),
        CourtOption("Оспаривание НПА", CourtType.CHALLENGE_NPA, false),
        CourtOption("Особое производство", CourtType.SPECIAL_PROCEEDING, false),
        CourtOption("Апелляционная жалоба", CourtType.APPEAL, false),
        CourtOption("Кассационная жалоба", CourtType.CASSATION, false),
        CourtOption("Надзорная жалоба в ВС РФ", CourtType.SUPERVISORY, false)
    )

    private var selectedOption: CourtOption? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentCourtBinding.inflate(inflater, container, false)
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

            val result = DutyCalculator.calculateCourt(option.type, amount)
            findNavController().navigate(
                R.id.action_court_to_result,
                bundleOf("amount" to result.amount, "legalRef" to result.legalReference, "description" to result.description)
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
