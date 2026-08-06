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

    private val nonPropertyTypes = listOf(
        "Иск неимущественного характера" to CourtType.NON_PROPERTY_GENERAL,
        "Расторжение брака" to CourtType.DIVORCE,
        "Оспаривание НПА" to CourtType.CHALLENGE_NPA,
        "Особое производство" to CourtType.SPECIAL_PROCEEDING,
        "Надзорная жалоба" to CourtType.SUPERVISORY,
        "Кассационная жалоба" to CourtType.CASSATION,
        "Апелляционная жалоба" to CourtType.APPEAL
    )

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentCourtBinding.inflate(inflater, container, false)
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
                Toast.makeText(requireContext(), getString(R.string.error_amount), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val result = DutyCalculator.calculateCourt(CourtType.PROPERTY_CLAIM, amount)
            navigateToResult(result.amount, result.legalReference, result.description)
        }

        binding.dropdownType.setOnItemClickListener { _, _, position, _ ->
            val type = nonPropertyTypes[position].second
            val result = DutyCalculator.calculateCourt(type)
            navigateToResult(result.amount, result.legalReference, result.description)
        }
    }

    private fun navigateToResult(amount: Double, legalRef: String, description: String) {
        findNavController().navigate(
            R.id.action_court_to_result,
            bundleOf("amount" to amount.toFloat(), "legalRef" to legalRef, "description" to description)
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
