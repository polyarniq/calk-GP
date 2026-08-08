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
import com.polyarniq.calkgp.calculator.CourtParams
import com.polyarniq.calkgp.calculator.DutyCalculator
import com.polyarniq.calkgp.databinding.FragmentCourtBinding
import com.polyarniq.calkgp.model.CourtType

class CourtFragment : Fragment() {

    private var _binding: FragmentCourtBinding? = null
    private val binding get() = _binding!!

    private data class CourtOption(
        val name: String,
        val type: CourtType,
        val needsAmount: Boolean,
        val needsPersonType: Boolean,
        val showCombinedClaim: Boolean = false,
        val showPropertyDivided: Boolean = false,
        val showDivorceProperty: Boolean = false,
        val showExemption35: Boolean = false,
        val showExemption36: Boolean = false,
        val showConditionalExemption: Boolean = false
    )

    private val options = listOf(
        CourtOption("Иск имущественного характера", CourtType.PROPERTY_CLAIM, true, false,
            showCombinedClaim = true, showPropertyDivided = true, showExemption35 = true, showExemption36 = true, showConditionalExemption = true),
        CourtOption("Судебный приказ", CourtType.COURT_ORDER, true, false,
            showCombinedClaim = true, showExemption35 = true, showExemption36 = true, showConditionalExemption = true),
        CourtOption("Иск имущественного характера, не подлежащего оценке", CourtType.NON_PROPERTY_CLAIM, false, true,
            showExemption35 = true, showExemption36 = true),
        CourtOption("Иск неимущественного характера", CourtType.NON_PROPERTY_GENERAL, false, true,
            showExemption35 = true, showExemption36 = true),
        CourtOption("Иск, вытекающий из договора", CourtType.CONTRACT_CLAIM, false, true,
            showExemption35 = true, showExemption36 = true),
        CourtOption("Расторжение брака", CourtType.DIVORCE, false, false,
            showDivorceProperty = true, showExemption35 = true),
        CourtOption("Оспаривание НПА", CourtType.CHALLENGE_NPA, false, true,
            showExemption35 = true, showExemption36 = true),
        CourtOption("Признание ненормативного правового акта недействительным", CourtType.NON_REGULATORY_ACT, false, true,
            showExemption35 = true, showExemption36 = true),
        CourtOption("Особое производство", CourtType.SPECIAL_PROCEEDING, false, false,
            showExemption35 = true, showExemption36 = true),
        CourtOption("Заявление о правопреемстве", CourtType.SUCCESSION, false, true,
            showExemption35 = true, showExemption36 = true),
        CourtOption("Принудительное исполнение решений третейского суда", CourtType.ARBITRATION_ENFORCEMENT, true, false,
            showExemption35 = true, showConditionalExemption = true),
        CourtOption("Признание и исполнение решений иностранных судов", CourtType.FOREIGN_COURT_RECOGNITION, true, false,
            showExemption35 = true, showConditionalExemption = true),
        CourtOption("Отмена решений третейского суда", CourtType.ARBITRATION_CANCELLATION, true, false,
            showExemption35 = true, showConditionalExemption = true),
        CourtOption("Выдача дубликата исполнительного листа", CourtType.DUPLICATE_EXECUTION, false, false,
            showExemption35 = true, showExemption36 = true),
        CourtOption("Пересмотр заочного решения судом", CourtType.REVIEW_ABSENTIA, false, false,
            showExemption35 = true, showExemption36 = true),
        CourtOption("Восстановление срока / отсрочка / рассрочка / поворот исполнения / разъяснение", CourtType.RESTORATION_OF_DEADLINE, false, false,
            showExemption35 = true, showExemption36 = true),
        CourtOption("Пересмотр по новым или вновь открывшимся обстоятельствам", CourtType.REVIEW_NEW_CIRCUMSTANCES, false, false,
            showExemption35 = true, showExemption36 = true),
        CourtOption("Обеспечение иска / замена обеспечительной меры / отмена обеспечения", CourtType.PROVISIONAL_MEASURES, false, false,
            showExemption35 = true, showExemption36 = true),
        CourtOption("Взыскание алиментов", CourtType.ALIMONY, false, false,
            showExemption35 = true, showExemption36 = true),
        CourtOption("Компенсация за нарушение права на судопроизводство в разумный срок", CourtType.COMPENSATION_TIMELINESS, false, true,
            showExemption35 = true, showExemption36 = true),
        CourtOption("Компенсация за нарушение условий содержания под стражей", CourtType.COMPENSATION_DETENTION, false, false,
            showExemption35 = true, showExemption36 = true),
        CourtOption("Апелляционная жалоба", CourtType.APPEAL, false, true,
            showExemption35 = true, showExemption36 = true),
        CourtOption("Кассационная жалоба", CourtType.CASSATION, false, true,
            showExemption35 = true, showExemption36 = true),
        CourtOption("Надзорная жалоба в ВС РФ", CourtType.SUPERVISORY, false, true,
            showExemption35 = true, showExemption36 = true)
    )

    private val personTypeOptions = listOf("Физическое лицо", "Организация")

    private val exemption35Options = listOf(
        "Не применяется",
        "Федеральные/региональные/местные госорганы (ст. 333.35 п.1 пп.4)",
        "Центральный банк РФ (ст. 333.35 п.1 пп.5)",
        "Герои Советского Союза / Герои РФ / полные кавалеры ордена Славы (ст. 333.35 п.1 пп.11)",
        "Ветераны ВОВ / инвалиды ВОВ / бывшие узники концлагерей / военнопленные ВОВ (ст. 333.35 п.1 пп.12)"
    )

    private val exemption36FullOptions = listOf(
        "Не применяется",
        "Иск о взыскании зарплаты / пособий (ст. 333.36 п.1 пп.1)",
        "Иск о взыскании алиментов (ст. 333.36 п.1 пп.2)",
        "Иск о возмещении вреда здоровью / смерти кормильца (ст. 333.36 п.1 пп.3)",
        "Иск о возмещении вреда преступлением (ст. 333.36 п.1 пп.4)",
        "Потерпевший по ст. 6.1.1 КоАП (ст. 333.36 п.1 пп.4.1)",
        "Кассация по уголовному делу (взыскание вреда) (ст. 333.36 п.1 пп.8)",
        "Прокурор в защиту прав граждан/государства (ст. 333.36 п.1 пп.9)",
        "Возмещение вреда от уголовного преследования (ст. 333.36 п.1 пп.10)",
        "Реабилитированные / жертвы политрепрессий (ст. 333.36 п.1 пп.11)",
        "Вынужденные переселенцы / беженцы (ст. 333.36 п.1 пп.12)",
        "Защита прав потребителей (уполномоченные органы) (ст. 333.36 п.1 пп.13)",
        "Усыновление/удочерение ребёнка (ст. 333.36 п.1 пп.14)",
        "Защита прав и интересов ребёнка (ст. 333.36 п.1 пп.15)",
        "Инвалиды I/II группы — неимущественный иск (ст. 333.36 п.1 пп.17)",
        "Психиатрическое освидетельствование (недобровольное) (ст. 333.36 п.1 пп.18)",
        "Госорганы как истец/ответчик (ст. 333.36 п.1 пп.19)",
        "Интеллектуальная собственность (принудительная лицензия) (ст. 333.36 п.1 пп.21)",
        "Дети-сироты / оставшиеся без попечения (ст. 333.36 п.1 пп.22)",
        "Единственное жильё (70% льгота) (ст. 333.36 п.1 пп.23)",
        "Участники СМО / ветераны боевых действий (ст. 333.36 п.1 пп.24)"
    )

    private val conditionalExemptionOptions = listOf(
        "Не применяется",
        "Общественная организация инвалидов (ст. 333.36 п.2 пп.1)",
        "Инвалид I или II группы / ребёнок-инвалид (ст. 333.36 п.2 пп.2)",
        "Ветеран боевых действий / военной службы (ст. 333.36 п.2 пп.3)",
        "Иск о нарушении прав потребителей (ст. 333.36 п.2 пп.4)",
        "Пенсионер — иск к ПФР / негосударственному ПФ (ст. 333.36 п.2 пп.5)"
    )

    private var selectedOption: CourtOption? = null
    private var selectedIsPhysicalPerson: Boolean = true
    private var selectedExemption35: Int = 0
    private var selectedExemption36Full: Int = 0
    private var selectedConditionalExemption: Int = 0

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

        val exemption35Adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, exemption35Options)
        binding.dropdownExemption35.setAdapter(exemption35Adapter)
        binding.dropdownExemption35.setText(exemption35Options[0], false)

        val exemption36Adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, exemption36FullOptions)
        binding.dropdownExemption36.setAdapter(exemption36Adapter)
        binding.dropdownExemption36.setText(exemption36FullOptions[0], false)

        val conditionalAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, conditionalExemptionOptions)
        binding.dropdownConditionalExemption.setAdapter(conditionalAdapter)
        binding.dropdownConditionalExemption.setText(conditionalExemptionOptions[0], false)

        binding.dropdownType.setOnItemClickListener { _, _, position, _ ->
            val option = options[position]
            selectedOption = option
            binding.amountCard.visibility = if (option.needsAmount) View.VISIBLE else View.GONE
            binding.personTypeCard.visibility = if (option.needsPersonType) View.VISIBLE else View.GONE
            binding.alimonyCard.visibility = if (option.type == CourtType.ALIMONY) View.VISIBLE else View.GONE

            val hasPeculiarities = option.showCombinedClaim || option.showPropertyDivided || option.showDivorceProperty
            binding.peculiaritiesCard.visibility = if (hasPeculiarities) View.VISIBLE else View.GONE
            binding.switchCombinedClaim.visibility = if (option.showCombinedClaim) View.VISIBLE else View.GONE
            binding.switchPropertyDivided.visibility = if (option.showPropertyDivided) View.VISIBLE else View.GONE
            binding.switchDivorceProperty.visibility = if (option.showDivorceProperty) View.VISIBLE else View.GONE
            binding.divorcePropertyAmountLayout.visibility = View.GONE

            val hasExemptions = option.showExemption35 || option.showExemption36 || option.showConditionalExemption
            binding.exemptionsCard.visibility = if (hasExemptions) View.VISIBLE else View.GONE
            binding.exemption35Layout.visibility = if (option.showExemption35) View.VISIBLE else View.GONE
            binding.exemption36Layout.visibility = if (option.showExemption36) View.VISIBLE else View.GONE
            binding.conditionalExemptionLayout.visibility = if (option.showConditionalExemption) View.VISIBLE else View.GONE

            binding.editAmount.text?.clear()
            binding.switchAlimony.isChecked = false
            binding.switchCombinedClaim.isChecked = false
            binding.switchPropertyDivided.isChecked = false
            binding.switchDivorceProperty.isChecked = false
            binding.editDivorcePropertyAmount.text?.clear()
            binding.dropdownExemption35.setText(exemption35Options[0], false)
            binding.dropdownExemption36.setText(exemption36FullOptions[0], false)
            binding.dropdownConditionalExemption.setText(conditionalExemptionOptions[0], false)
            selectedExemption35 = 0
            selectedExemption36Full = 0
            selectedConditionalExemption = 0
            selectedIsPhysicalPerson = true
            binding.dropdownPersonType.setText(personTypeOptions[0], false)
        }

        binding.dropdownPersonType.setOnItemClickListener { _, _, position, _ ->
            selectedIsPhysicalPerson = position == 0
        }

        binding.dropdownExemption35.setOnItemClickListener { _, _, position, _ ->
            selectedExemption35 = position
        }

        binding.dropdownExemption36.setOnItemClickListener { _, _, position, _ ->
            selectedExemption36Full = position
        }

        binding.dropdownConditionalExemption.setOnItemClickListener { _, _, position, _ ->
            selectedConditionalExemption = position
        }

        binding.switchDivorceProperty.setOnCheckedChangeListener { _, isChecked ->
            binding.divorcePropertyAmountLayout.visibility = if (isChecked) View.VISIBLE else View.GONE
            if (!isChecked) binding.editDivorcePropertyAmount.text?.clear()
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

            val divorcePropertyAmount = if (option.showDivorceProperty && binding.switchDivorceProperty.isChecked) {
                val str = binding.editDivorcePropertyAmount.text.toString().replace(",", ".")
                str.toDoubleOrNull() ?: 0.0
            } else 0.0

            val params = CourtParams(
                type = option.type,
                claimAmount = amount,
                isPhysicalPerson = selectedIsPhysicalPerson,
                isCombinedClaim = option.showCombinedClaim && binding.switchCombinedClaim.isChecked,
                isPropertyAlreadyRecognized = option.showPropertyDivided && binding.switchPropertyDivided.isChecked,
                isDivorceWithProperty = option.showDivorceProperty && binding.switchDivorceProperty.isChecked,
                divorcePropertyAmount = divorcePropertyAmount,
                exemption35 = if (option.showExemption35) selectedExemption35 else 0,
                fullExemption36 = if (option.showExemption36) selectedExemption36Full else 0,
                conditionalExemption36 = if (option.showConditionalExemption) selectedConditionalExemption else 0
            )

            val result = DutyCalculator.calculateCourt(params)
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
