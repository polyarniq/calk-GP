package com.polyarniq.calkgp.ui.notary

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
import com.polyarniq.calkgp.databinding.FragmentNotaryBinding
import com.polyarniq.calkgp.model.NotaryType

class NotaryFragment : Fragment() {

    private var _binding: FragmentNotaryBinding? = null
    private val binding get() = _binding!!

    private data class NotaryOption(val name: String, val type: NotaryType, val needsAmount: Boolean)

    private val options = listOf(
        NotaryOption("Доверенность", NotaryType.POWER_OF_ATTORNEY, false),
        NotaryOption("Завещание", NotaryType.WILL, false),
        NotaryOption("Наследство — дети, супруг, родители (0.3%)", NotaryType.INHERITANCE_DIRECT, true),
        NotaryOption("Наследство — другие наследники (0.6%)", NotaryType.INHERITANCE_OTHER, true),
        NotaryOption("Охрана наследства", NotaryType.INHERITANCE_PROTECTION, false),
        NotaryOption("Удостоверение сделки", NotaryType.DEAL_CERTIFICATION, false)
    )

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentNotaryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, options.map { it.name })
        binding.dropdownAction.setAdapter(adapter)

        binding.dropdownAction.setOnItemClickListener { _, _, position, _ ->
            val option = options[position]
            binding.amountCard.visibility = if (option.needsAmount) View.VISIBLE else View.GONE
        }

        binding.btnCalculate.setOnClickListener {
            val selectedName = binding.dropdownAction.text.toString()
            val option = options.find { it.name == selectedName }
            if (option == null) {
                Toast.makeText(requireContext(), "Выберите действие", Toast.LENGTH_SHORT).show()
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

            val result = DutyCalculator.calculateNotary(option.type, amount)
            findNavController().navigate(
                R.id.action_notary_to_result,
                bundleOf("amount" to result.amount.toFloat(), "legalRef" to result.legalReference, "description" to result.description)
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
