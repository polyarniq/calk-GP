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

    private data class CourtOption(val name: String, val type: CourtType, val needsAmount: Boolean, val needsPersonType: Boolean)

    private val options = listOf(
        CourtOption("Иск имущественного характера", CourtType.PROPERTY_CLAIM, true, false),
        CourtOption("Судебный приказ", CourtType.COURT_ORDER, true, false),
        CourtOption("Иск имущественного характера, не подлежащего оценке", CourtType.NON_PROPERTY_CLAIM, false, true),
        CourtOption("Иск неимущественного характера", CourtType.NON_PROPERTY_GENERAL, false, true),
        CourtOption("Иск, вытекающий из договора", CourtType.CONTRACT_CLAIM, false, true),
        CourtOption("Расторжение брака", CourtType.DIVORCE, false, false),
        CourtOption("Оспаривание НПА", CourtType.CHALLENGE_NPA, false, true),
        CourtOption("Признание ненормативного правового акта недействительным", CourtType.NON_REGULATORY_ACT, false, true),
        CourtOption("Особое производство", CourtType.SPECIAL_PROCEEDING, false, false),
        CourtOption("Заявление о правопреемстве", CourtType.SUCCESSION, false, true),
        CourtOption("Принудительное исполнение решений третейского суда", CourtType.ARBITRATION_ENFORCEMENT, true, false),
        CourtOption("Признание и исполнение решений иностранных судов", CourtType.FOREIGN_COURT_RECOGNITION, true, false),
        CourtOption("Отмена решений третейского суда", CourtType.ARBITRATION_CANCELLATION, true, false),
        CourtOption("Выдача дубликата исполнительного листа", CourtType.DUPLICATE_EXECUTION, false, false),
        CourtOption("Пересмотр заочного решения судом", CourtType.REVIEW_ABSENTIA, false, false),
        CourtOption("Восстановление срока / отсрочка / рассрочка / поворот исполнения / разъяснение", CourtType.RESTORATION_OF_DEADLINE, false, false),
        CourtOption("Пересмотр по новым или вновь открывшимся обстоятельствам", CourtType.REVIEW_NEW_CIRCUMSTANCES, false, false),
        CourtOption("Обеспечение иска / замена обеспечительной меры / отмена обеспечения", CourtType.PROVISIONAL_MEASURES, false, false),
        CourtOption("Взыскание алиментов", CourtType.ALIMONY, false, false),
        CourtOption("Компенсация за нарушение права на судопроизводство в разумный срок", CourtType.COMPENSATION_TIMELINESS, false, true),
        CourtOption("Компенсация за нарушение условий содержания под стражей", CourtType.COMPENSATION_DETENTION, false, false),
        CourtOption("Апелляционная жалоба", CourtType.APPEAL, false, true),
        CourtOption("Кассационная жалоба", CourtType.CASSATION, false, true),
        CourtOption("Надзорная жалоба в ВС РФ", CourtType.SUPERVISORY, false, true)
    )

    private val personTypeOptions = listOf("Физическое лицо", "Организация")

    private var selectedOption: CourtOption? = null
    private var selectedIsPhysicalPerson: Boolean = true

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentCourtBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, options.map { it.name })
        binding.dropdownType.setAdapter(adapter)

        val personTypeAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, personTypeOptions)
        binding.dropdownPersonType.setAdapter(personTypeAdapter)
        binding.dropdownPersonType.setText(personTypeOptions[0], false)

        binding.dropdownType.setOnItemClickListener { _, _, position, _ ->
            val option = options[position]
            selectedOption = option
            binding.amountCard.visibility = if (option.needsAmount) View.VISIBLE else View.GONE
            binding.personTypeCard.visibility = if (option.needsPersonType) View.VISIBLE else View.GONE
            binding.alimonyCard.visibility = if (option.type == CourtType.ALIMONY) View.VISIBLE else View.GONE
            binding.editAmount.text?.clear()
            binding.switchAlimony.isChecked = false
        }

        binding.dropdownPersonType.setOnItemClickListener { _, _, position, _ ->
            selectedIsPhysicalPerson = position == 0
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
            } else if (option.type == CourtType.ALIMONY) {
                if (binding.switchAlimony.isChecked) 1.0 else 0.0
            } else 0.0

            val result = DutyCalculator.calculateCourt(option.type, amount, selectedIsPhysicalPerson)
            findNavController().navigate(
                R.id.action_court_to_result,
                bundleOf("amount" to result.amount.toFloat(), "legalRef" to result.legalReference, "description" to result.description)
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
