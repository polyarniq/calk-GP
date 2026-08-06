package com.polyarniq.calkgp.ui.registration

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
import com.polyarniq.calkgp.databinding.FragmentRegistrationBinding
import com.polyarniq.calkgp.model.*

class RegistrationFragment : Fragment() {

    private var _binding: FragmentRegistrationBinding? = null
    private val binding get() = _binding!!

    private val categories = listOf("ЗАГС", "Росреестр", "ФНС")

    private val zagsActions = listOf(
        "Регистрация брака" to ZagsType.MARRIAGE,
        "Расторжение (взаимное согласие)" to ZagsType.DIVORCE_MUTUAL,
        "Расторжение (судебный порядок)" to ZagsType.DIVORCE_COURT,
        "Расторжение (один супруг)" to ZagsType.DIVORCE_ABSENT,
        "Перемена имени" to ZagsType.NAME_CHANGE,
        "Установление отцовства" to ZagsType.PATERNITY,
        "Повторное свидетельство" to ZagsType.REPEAT_CERTIFICATE,
        "Справка из архива" to ZagsType.ARCHIVE_CERTIFICATE
    )

    private val rosreestrActions = listOf(
        "Право собственности (физ. лицо)" to RosreestrType.RIGHT_INDIVIDUAL,
        "Право собственности (юр. лицо)" to RosreestrType.RIGHT_LEGAL,
        "Ипотека" to RosreestrType.MORTGAGE,
        "Договор долевого участия" to RosreestrType.DDU,
        "Земельный участок (ЛПХ, ИЖС)" to RosreestrType.LAND_PLOT,
        "Кадастровый учёт" to RosreestrType.CADASTRAL_ONLY
    )

    private val fnsActions = listOf(
        "Регистрация ООО" to FnsType.LLC,
        "Регистрация ИП" to FnsType.IP,
        "Изменения в устав" to FnsType.CHARTER_CHANGES,
        "Ликвидация" to FnsType.LIQUIDATION
    )

    private var currentCategory = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentRegistrationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val catAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, categories)
        binding.dropdownCategory.setAdapter(catAdapter)

        binding.dropdownCategory.setOnItemClickListener { _, _, position, _ ->
            currentCategory = position
            binding.actionCard.visibility = View.VISIBLE
            val actions = getActionsForCategory(position)
            val actionAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, actions.map { it.first })
            binding.dropdownAction.setAdapter(actionAdapter)
            binding.dropdownAction.text.clear()
        }

        binding.dropdownAction.setOnItemClickListener { _, _, position, _ ->
            val actions = getActionsForCategory(currentCategory)
            val (_, type) = actions[position]
            val result = calculateForCategory(currentCategory, type)
            findNavController().navigate(
                R.id.action_registration_to_result,
                bundleOf("amount" to result.amount.toFloat(), "legalRef" to result.legalReference, "description" to result.description)
            )
        }
    }

    private fun getActionsForCategory(category: Int): List<Pair<String, Any>> {
        return when (category) {
            0 -> zagsActions
            1 -> rosreestrActions
            2 -> fnsActions
            else -> emptyList()
        }
    }

    private fun calculateForCategory(category: Int, type: Any): DutyResult {
        return when (category) {
            0 -> DutyCalculator.calculateZags(type as ZagsType)
            1 -> DutyCalculator.calculateRosreestr(type as RosreestrType)
            2 -> DutyCalculator.calculateFns(type as FnsType)
            else -> DutyResult(0.0, "", "")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
