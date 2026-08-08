package com.polyarniq.calkgp.calculator

import com.polyarniq.calkgp.model.*
import kotlin.math.min

data class CourtParams(
    val type: CourtType,
    val claimAmount: Double = 0.0,
    val isPhysicalPerson: Boolean = true,
    val isCombinedClaim: Boolean = false,
    val isPropertyAlreadyRecognized: Boolean = false,
    val isDivorceWithProperty: Boolean = false,
    val divorcePropertyAmount: Double = 0.0,
    val exemption35: Int = 0,
    val fullExemption36: Int = 0,
    val conditionalExemption36: Int = 0
)

object DutyCalculator {

    fun calculateCourt(params: CourtParams): DutyResult {
        if (params.exemption35 > 0) {
            return DutyResult(0.0, "ст. 333.35 НК РФ", "Льгота: полное освобождение от уплаты госпошлины (ст. 333.35 НК РФ)")
        }
        if (params.fullExemption36 > 0) {
            return DutyResult(0.0, "ст. 333.36 п.1 НК РФ", "Льгота: полное освобождение от уплаты госпошлины (ст. 333.36 п.1 НК РФ)")
        }

        val baseResult = calculateCourtBase(params)
        val baseAmount = baseResult.amount

        if (params.conditionalExemption36 > 0) {
            if (params.claimAmount <= 1_000_000) {
                return DutyResult(0.0, "ст. 333.36 п.2 НК РФ", "Льгота: освобождение от уплаты при цене иска до 1 000 000 ₽ (ст. 333.36 п.2 НК РФ)")
            } else {
                val threshold = calcCourtProperty(1_000_000.0)
                val reduced = baseAmount - threshold
                return DutyResult(maxOf(reduced, 0.0), "ст. 333.36 п.2,3 НК РФ", "Льгота: уменьшение пошлины на 25 000 ₽ при цене иска свыше 1 000 000 ₽ (ст. 333.36 п.3 НК РФ)")
            }
        }

        return baseResult
    }

    private fun calculateCourtBase(params: CourtParams): DutyResult {
        val type = params.type
        val claimAmount = params.claimAmount
        val isPhysicalPerson = params.isPhysicalPerson

        val mainResult = when (type) {
            CourtType.PROPERTY_CLAIM -> {
                if (params.isPropertyAlreadyRecognized) {
                    if (isPhysicalPerson)
                        DutyResult(3000.0, "ст. 333.19 п.1 пп.3 НК РФ", "Раздел имущества: ранее суд признал право собственности (неимущественный иск). Физическое лицо.")
                    else
                        DutyResult(20000.0, "ст. 333.19 п.1 пп.3 НК РФ", "Раздел имущества: ранее суд признал право собственности (неимущественный иск). Организация.")
                } else {
                    val amount = calcCourtProperty(claimAmount)
                    DutyResult(amount, "ст. 333.19 п.1 пп.1 НК РФ", "Госпошлина при подаче искового заявления имущественного характера в суд общей юрисдикции")
                }
            }
            CourtType.NON_PROPERTY_CLAIM ->
                if (isPhysicalPerson)
                    DutyResult(3000.0, "ст. 333.19 п.1 пп.3 НК РФ", "Исковое заявление имущественного характера, не подлежащего оценке. Физическое лицо.")
                else
                    DutyResult(20000.0, "ст. 333.19 п.1 пп.3 НК РФ", "Исковое заявление имущественного характера, не подлежащего оценке. Организация.")
            CourtType.NON_PROPERTY_GENERAL ->
                if (isPhysicalPerson)
                    DutyResult(3000.0, "ст. 333.19 п.1 пп.3 НК РФ", "Исковое заявление неимущественного характера. Физическое лицо.")
                else
                    DutyResult(20000.0, "ст. 333.19 п.1 пп.3 НК РФ", "Исковое заявление неимущественного характера. Организация.")
            CourtType.CONTRACT_CLAIM ->
                if (isPhysicalPerson)
                    DutyResult(3000.0, "ст. 333.19 п.1 пп.4 НК РФ", "Иск по спорам при заключении/изменении/расторжении договоров. Физическое лицо.")
                else
                    DutyResult(20000.0, "ст. 333.19 п.1 пп.4 НК РФ", "Иск по спорам при заключении/изменении/расторжении договоров. Организация.")
            CourtType.DIVORCE -> {
                var amount = 5000.0
                var ref = "ст. 333.19 п.1 пп.5 НК РФ"
                var desc = "Исковое заявление о расторжении брака"
                if (params.isDivorceWithProperty && params.divorcePropertyAmount > 0) {
                    amount += calcCourtProperty(params.divorcePropertyAmount)
                    ref = "ст. 333.19 п.1 пп.5 + пп.1; ст. 333.20 п.1 пп.12 НК РФ"
                    desc = "Расторжение брака + раздел имущества"
                }
                DutyResult(amount, ref, desc)
            }
            CourtType.CHALLENGE_NPA ->
                if (isPhysicalPerson)
                    DutyResult(4000.0, "ст. 333.19 п.1 пп.6 НК РФ", "Административный иск об оспаривании НПА. Физическое лицо.")
                else
                    DutyResult(20000.0, "ст. 333.19 п.1 пп.6 НК РФ", "Административный иск об оспаривании НПА. Организация.")
            CourtType.NON_REGULATORY_ACT ->
                if (isPhysicalPerson)
                    DutyResult(3000.0, "ст. 333.19 п.1 пп.7 НК РФ", "Административный иск о признании ненормативного ПА недействительным. Физическое лицо.")
                else
                    DutyResult(15000.0, "ст. 333.19 п.1 пп.7 НК РФ", "Административный иск о признании ненормативного ПА недействительным. Организация.")
            CourtType.SPECIAL_PROCEEDING ->
                DutyResult(3000.0, "ст. 333.19 п.1 пп.8 НК РФ", "Заявление по делам особого производства")
            CourtType.SUPERVISORY ->
                if (isPhysicalPerson)
                    DutyResult(7000.0, "ст. 333.19 п.1 пп.21 НК РФ", "Кассационная или надзорная жалоба в ВС РФ. Физическое лицо.")
                else
                    DutyResult(25000.0, "ст. 333.19 п.1 пп.21 НК РФ", "Кассационная или надзорная жалоба в ВС РФ. Организация.")
            CourtType.CASSATION ->
                if (isPhysicalPerson)
                    DutyResult(5000.0, "ст. 333.19 п.1 пп.20 НК РФ", "Кассационная жалоба. Физическое лицо.")
                else
                    DutyResult(20000.0, "ст. 333.19 п.1 пп.20 НК РФ", "Кассационная жалоба. Организация.")
            CourtType.APPEAL ->
                if (isPhysicalPerson)
                    DutyResult(3000.0, "ст. 333.19 п.1 пп.19 НК РФ", "Апелляционная жалоба. Физическое лицо.")
                else
                    DutyResult(15000.0, "ст. 333.19 п.1 пп.19 НК РФ", "Апелляционная жалоба. Организация.")
            CourtType.COURT_ORDER -> {
                val amount = calcCourtProperty(claimAmount) * 0.5
                DutyResult(amount, "ст. 333.19 п.1 пп.2 НК РФ", "Заявление о вынесении судебного приказа (50% от пошлины за иск)")
            }
            CourtType.SUCCESSION ->
                if (isPhysicalPerson)
                    DutyResult(2000.0, "ст. 333.19 п.1 пп.9 НК РФ", "Заявление о правопреемстве. Физическое лицо.")
                else
                    DutyResult(15000.0, "ст. 333.19 п.1 пп.9 НК РФ", "Заявление о правопреемстве. Организация.")
            CourtType.ARBITRATION_ENFORCEMENT -> {
                val amount = calcCourtProperty(claimAmount) * 0.3
                DutyResult(amount, "ст. 333.19 п.1 пп.10 НК РФ", "Заявление о выдаче исполнительных листов на принудительное исполнение решений третейского суда")
            }
            CourtType.FOREIGN_COURT_RECOGNITION -> {
                val amount = calcCourtProperty(claimAmount) * 0.3
                DutyResult(amount, "ст. 333.19 п.1 пп.10 НК РФ", "Заявление о признании и исполнении решения иностранного суда")
            }
            CourtType.ARBITRATION_CANCELLATION -> {
                val amount = calcCourtProperty(claimAmount)
                DutyResult(amount, "ст. 333.19 п.1 пп.11 НК РФ", "Заявление об отмене решения третейского суда")
            }
            CourtType.DUPLICATE_EXECUTION ->
                DutyResult(1500.0, "ст. 333.19 п.1 пп.12 НК РФ", "Заявление о выдаче дубликата исполнительного листа")
            CourtType.REVIEW_ABSENTIA ->
                DutyResult(1500.0, "ст. 333.19 п.1 пп.12 НК РФ", "Заявление о пересмотре заочного решения судом")
            CourtType.RESTORATION_OF_DEADLINE ->
                DutyResult(3000.0, "ст. 333.19 п.1 пп.13 НК РФ", "Заявление о восстановлении срока / отсрочке / рассрочке / повороте исполнения / разъяснении")
            CourtType.REVIEW_NEW_CIRCUMSTANCES ->
                DutyResult(10000.0, "ст. 333.19 п.1 пп.14 НК РФ", "Заявление о пересмотре по новым или вновь открывшимся обстоятельствам")
            CourtType.PROVISIONAL_MEASURES ->
                DutyResult(10000.0, "ст. 333.19 п.1 пп.15 НК РФ", "Заявление об обеспечении иска / замене обеспечительной меры / отмене обеспечения")
            CourtType.ALIMONY ->
                DutyResult(if (claimAmount > 0) 300.0 else 150.0, "ст. 333.19 п.1 пп.16 НК РФ", if (claimAmount > 0) "Взыскание алиментов на детей и истца (×2)" else "Взыскание алиментов")
            CourtType.COMPENSATION_TIMELINESS ->
                if (isPhysicalPerson)
                    DutyResult(300.0, "ст. 333.19 п.1 пп.17 НК РФ", "Компенсация за нарушение права на судопроизводство в разумный срок. Физическое лицо.")
                else
                    DutyResult(6000.0, "ст. 333.19 п.1 пп.17 НК РФ", "Компенсация за нарушение права на судопроизводство в разумный срок. Организация.")
            CourtType.COMPENSATION_DETENTION ->
                DutyResult(300.0, "ст. 333.19 п.1 пп.18 НК РФ", "Компенсация за нарушение условий содержания под стражей")
        }

        if (params.isCombinedClaim) {
            val nonPropertyDuty = if (isPhysicalPerson) 3000.0 else 20000.0
            return DutyResult(
                mainResult.amount + nonPropertyDuty,
                mainResult.legalReference + " + ст. 333.19 п.1 пп.3; ст. 333.20 п.1 пп.1 НК РФ",
                mainResult.description + " + неимущественное требование"
            )
        }

        return mainResult
    }

    private fun calcCourtProperty(amount: Double): Double {
        return when {
            amount <= 100_000 -> 4_000.0
            amount <= 300_000 -> 4_000 + (amount - 100_000) * 0.03
            amount <= 500_000 -> 10_000 + (amount - 300_000) * 0.025
            amount <= 1_000_000 -> 15_000 + (amount - 500_000) * 0.02
            amount <= 3_000_000 -> 25_000 + (amount - 1_000_000) * 0.01
            amount <= 8_000_000 -> 45_000 + (amount - 3_000_000) * 0.007
            amount <= 24_000_000 -> 80_000 + (amount - 8_000_000) * 0.0035
            amount <= 50_000_000 -> 136_000 + (amount - 24_000_000) * 0.003
            amount <= 100_000_000 -> 214_000 + (amount - 50_000_000) * 0.002
            else -> min(314_000 + (amount - 100_000_000) * 0.0015, 900_000.0)
        }
    }

    fun calculateArbitration(type: ArbitrationType, claimAmount: Double = 0.0): DutyResult {
        return when (type) {
            ArbitrationType.PROPERTY_CLAIM -> {
                val amount = calcArbitrationProperty(claimAmount)
                DutyResult(amount, "ст. 333.21 п.1 пп.1 НК РФ", "Иск имущественного характера в арбитражный суд")
            }
            ArbitrationType.NON_PROPERTY_IP ->
                DutyResult(15_000.0, "ст. 333.21 п.1 пп.4 НК РФ", "Иск неимущественного характера (для физических лиц)")
            ArbitrationType.NON_PROPERTY_ORG ->
                DutyResult(50_000.0, "ст. 333.21 п.1 пп.4 НК РФ", "Иск неимущественного характера (для организаций)")
            ArbitrationType.APPEAL ->
                DutyResult(10_000.0, "ст. 333.21 п.1 пп.19 НК РФ", "Апелляционная жалоба (для физических лиц)")
            ArbitrationType.CASSATION ->
                DutyResult(20_000.0, "ст. 333.21 п.1 пп.20 НК РФ", "Кассационная жалоба (для физических лиц)")
            ArbitrationType.SUPERVISORY ->
                DutyResult(30_000.0, "ст. 333.21 п.1 пп.21 НК РФ", "Кассационная/надзорная жалоба в ВС РФ (для физ. лиц)")
            ArbitrationType.BANKRUPTCY_IP ->
                DutyResult(10_000.0, "ст. 333.21 п.1 пп.8 НК РФ", "Заявление о признании банкротом (для физ. лиц)")
            ArbitrationType.BANKRUPTCY_ORG ->
                DutyResult(100_000.0, "ст. 333.21 п.1 пп.8 НК РФ", "Заявление о признании банкротом (для организаций)")
            ArbitrationType.COURT_ORDER -> {
                val amount = calcArbitrationProperty(claimAmount) * 0.5
                DutyResult(maxOf(amount, 8000.0), "ст. 333.21 п.1 пп.3 НК РФ", "Заявление о вынесении судебного приказа (50%, мин. 8000)")
            }
        }
    }

    private fun calcArbitrationProperty(amount: Double): Double {
        return when {
            amount <= 100_000 -> 10_000.0
            amount <= 1_000_000 -> 10_000 + (amount - 100_000) * 0.05
            amount <= 10_000_000 -> 55_000 + (amount - 1_000_000) * 0.03
            amount <= 50_000_000 -> 325_000 + (amount - 10_000_000) * 0.01
            else -> min(725_000 + (amount - 50_000_000) * 0.005, 10_000_000.0)
        }
    }

    fun calculateNotary(type: NotaryType, amount: Double = 0.0): DutyResult {
        return when (type) {
            NotaryType.POWER_OF_ATTORNEY ->
                DutyResult(200.0, "ст. 333.24 п.1 пп.1-3 НК РФ", "Удостоверение доверенности")
            NotaryType.WILL ->
                DutyResult(100.0, "ст. 333.24 п.1 пп.13 НК РФ", "Удостоверение завещания")
            NotaryType.INHERITANCE_DIRECT -> {
                val duty = min(amount * 0.003, 100_000.0)
                DutyResult(duty, "ст. 333.24 п.1 пп.22 НК РФ", "Свидетельство о праве на наследство (дети, супруг, родители, братья/сёстры)")
            }
            NotaryType.INHERITANCE_OTHER -> {
                val duty = min(amount * 0.006, 1_000_000.0)
                DutyResult(duty, "ст. 333.24 п.1 пп.22 НК РФ", "Свидетельство о праве на наследство (другие наследники)")
            }
            NotaryType.INHERITANCE_PROTECTION ->
                DutyResult(600.0, "ст. 333.24 п.1 пп.23 НК РФ", "Принятие мер по охране наследства")
            NotaryType.DEAL_CERTIFICATION ->
                DutyResult(500.0, "ст. 333.24 п.1 пп.6 НК РФ", "Удостоверение сделки, предмет которой не подлежит оценке")
        }
    }

    fun calculateZags(type: ZagsType): DutyResult {
        return when (type) {
            ZagsType.MARRIAGE ->
                DutyResult(350.0, "ст. 333.26 п.1 пп.1 НК РФ", "Регистрация заключения брака")
            ZagsType.DIVORCE_MUTUAL ->
                DutyResult(5000.0, "ст. 333.26 п.1 пп.2 НК РФ", "Расторжение брака при взаимном согласии (с каждого)")
            ZagsType.DIVORCE_COURT ->
                DutyResult(5000.0, "ст. 333.26 п.1 пп.2 НК РФ", "Расторжение брака в судебном порядке (с каждого)")
            ZagsType.DIVORCE_ABSENT ->
                DutyResult(350.0, "ст. 333.26 п.1 пп.2 НК РФ", "Расторжение по заявлению одного супруга (супруг безвестно отсутствует/недееспособен/осуждён)")
            ZagsType.NAME_CHANGE ->
                DutyResult(5000.0, "ст. 333.26 п.1 пп.4 НК РФ", "Регистрация перемены имени")
            ZagsType.PATERNITY ->
                DutyResult(350.0, "ст. 333.26 п.1 пп.3 НК РФ", "Регистрация установления отцовства")
            ZagsType.REPEAT_CERTIFICATE ->
                DutyResult(500.0, "ст. 333.26 п.1 пп.6 НК РФ", "Повторное свидетельство о регистрации акта")
            ZagsType.ARCHIVE_CERTIFICATE ->
                DutyResult(350.0, "ст. 333.26 п.1 пп.7 НК РФ", "Справка из архива ЗАГС")
        }
    }

    fun calculateRosreestr(type: RosreestrType): DutyResult {
        return when (type) {
            RosreestrType.RIGHT_INDIVIDUAL ->
                DutyResult(4_000.0, "ст. 333.33 п.1 пп.22 НК РФ", "Регистрация права (физ. лицо, кадастр. ст-ть до 20 млн ₽)")
            RosreestrType.RIGHT_LEGAL ->
                DutyResult(44_000.0, "ст. 333.33 п.1 пп.22 НК РФ", "Регистрация права (юр. лицо, кадастр. ст-ть до 22 млн ₽)")
            RosreestrType.MORTGAGE ->
                DutyResult(1_000.0, "ст. 333.33 п.1 пп.28 НК РФ", "Регистрация ипотеки")
            RosreestrType.DDU ->
                DutyResult(350.0, "ст. 333.33 п.1 пп.30 НК РФ", "Регистрация договора долевого участия")
            RosreestrType.LAND_PLOT ->
                DutyResult(700.0, "ст. 333.33 п.1 пп.24 НК РФ", "Регистрация земельного участка (ЛПХ, садоводство, ИЖС)")
            RosreestrType.CADASTRAL_ONLY ->
                DutyResult(2_000.0, "ст. 333.33 п.1 пп.22.2 НК РФ", "Государственный кадастровый учёт без регистрации прав")
        }
    }

    fun calculateFns(type: FnsType): DutyResult {
        return when (type) {
            FnsType.LLC ->
                DutyResult(4_000.0, "ст. 333.33 п.1 пп.1 НК РФ", "Государственная регистрация юридического лица")
            FnsType.IP ->
                DutyResult(800.0, "ст. 333.33 п.1 пп.6 НК РФ", "Государственная регистрация физического лица в качестве ИП")
            FnsType.CHARTER_CHANGES ->
                DutyResult(800.0, "ст. 333.33 п.1 пп.3 НК РФ", "Регистрация изменений в учредительные документы (20% от 4000)")
            FnsType.LIQUIDATION ->
                DutyResult(800.0, "ст. 333.33 п.1 пп.3 НК РФ", "Государственная регистрация ликвидации юридического лица (20% от 4000)")
        }
    }
}
