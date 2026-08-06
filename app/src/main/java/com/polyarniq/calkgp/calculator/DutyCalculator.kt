package com.polyarniq.calkgp.calculator

import com.polyarniq.calkgp.model.*
import kotlin.math.min

object DutyCalculator {

    fun calculateCourt(type: CourtType, claimAmount: Double = 0.0): DutyResult {
        return when (type) {
            CourtType.PROPERTY_CLAIM -> {
                val amount = calcPropertyClaim(claimAmount)
                DutyResult(amount, "ст. 333.19 п.1 НК РФ", "Госпошлина при обращении в суд общей юрисдикции (имущественный иск)")
            }
            CourtType.NON_PROPERTY_GENERAL ->
                DutyResult(300.0, "ст. 333.19 п.3 НК РФ", "Исковое заявление неимущественного характера")
            CourtType.DIVORCE ->
                DutyResult(600.0, "ст. 333.19 п.5 НК РФ", "Расторжение брака")
            CourtType.CHALLENGE_NPA ->
                DutyResult(300.0, "ст. 333.19 п.6 НК РФ", "Оспаривание нормативного правового акта")
            CourtType.SPECIAL_PROCEEDING ->
                DutyResult(300.0, "ст. 333.19 п.8 НК РФ", "Особое производство")
            CourtType.SUPERVISORY ->
                DutyResult(300.0, "ст. 333.19 п.9 НК РФ", "Надзорная жалоба")
            CourtType.CASSATION ->
                DutyResult(150.0, "ст. 333.19 п.9 НК РФ", "Кассационная жалоба")
            CourtType.APPEAL ->
                DutyResult(150.0, "ст. 333.19 п.9 НК РФ", "Апелляционная жалоба")
        }
    }

    private fun calcPropertyClaim(amount: Double): Double {
        return when {
            amount <= 20_000 -> maxOf(amount * 0.04, 400.0)
            amount <= 100_000 -> 800 + (amount - 20_000) * 0.03
            amount <= 200_000 -> 3_200 + (amount - 100_000) * 0.02
            amount <= 1_000_000 -> 5_200 + (amount - 200_000) * 0.01
            else -> min(13_200 + (amount - 1_000_000) * 0.005, 60_000.0)
        }
    }

    fun calculateArbitration(type: ArbitrationType, claimAmount: Double = 0.0): DutyResult {
        return when (type) {
            ArbitrationType.PROPERTY_CLAIM -> {
                val amount = calcArbitrationProperty(claimAmount)
                DutyResult(amount, "ст. 333.21 п.1 НК РФ", "Госпошлина при обращении в арбитражный суд (имущественный иск)")
            }
            ArbitrationType.NON_PROPERTY_IP ->
                DutyResult(3_000.0, "ст. 333.21 п.1 НК РФ", "Иск неимущественного характера (ИП)")
            ArbitrationType.NON_PROPERTY_ORG ->
                DutyResult(6_000.0, "ст. 333.21 п.1 НК РФ", "Иск неимущественного характера (организация)")
            ArbitrationType.APPEAL -> {
                val base = calcArbitrationProperty(claimAmount)
                DutyResult(base * 0.5, "ст. 333.21 п.1 НК РФ", "Апелляционная жалоба (50% от пошлины за иск)")
            }
            ArbitrationType.CASSATION -> {
                val base = calcArbitrationProperty(claimAmount)
                DutyResult(base * 0.5, "ст. 333.21 п.1 НК РФ", "Кассационная жалоба (50% от пошлины за иск)")
            }
        }
    }

    private fun calcArbitrationProperty(amount: Double): Double {
        return when {
            amount <= 100_000 -> maxOf(amount * 0.04, 2_000.0)
            amount <= 200_000 -> 4_000 + (amount - 100_000) * 0.03
            amount <= 1_000_000 -> 7_000 + (amount - 200_000) * 0.02
            amount <= 2_000_000 -> 23_000 + (amount - 1_000_000) * 0.01
            else -> min(33_000 + (amount - 2_000_000) * 0.005, 200_000.0)
        }
    }

    fun calculateNotary(type: NotaryType, amount: Double = 0.0, relation: InheritanceRelation = InheritanceRelation.DIRECT): DutyResult {
        return when (type) {
            NotaryType.POWER_OF_ATTORNEY ->
                DutyResult(200.0, "ст. 333.24 п.1 НК РФ", "Удостоверение доверенности")
            NotaryType.WILL ->
                DutyResult(100.0, "ст. 333.24 п.1 НК РФ", "Удостоверение завещания")
            NotaryType.INHERITANCE_DIRECT -> {
                val duty = min(amount * 0.003, 100_000.0)
                DutyResult(duty, "ст. 333.24 п.1 НК РФ", "Свидетельство о праве на наследство (дети, супруг, родители)")
            }
            NotaryType.INHERITANCE_OTHER -> {
                val duty = min(amount * 0.006, 1_000_000.0)
                DutyResult(duty, "ст. 333.24 п.1 НК РФ", "Свидетельство о праве на наследство (другие наследники)")
            }
            NotaryType.INHERITANCE_PROTECTION ->
                DutyResult(600.0, "ст. 333.24 п.1 НК РФ", "Принятие мер по охране наследства")
            NotaryType.DEAL_CERTIFICATION ->
                DutyResult(500.0, "ст. 333.24 п.1 НК РФ", "Удостоверение сделки")
        }
    }

    fun calculateZags(type: ZagsType): DutyResult {
        return when (type) {
            ZagsType.MARRIAGE ->
                DutyResult(350.0, "ст. 333.26 п.1 НК РФ", "Государственная регистрация заключения брака")
            ZagsType.DIVORCE_MUTUAL ->
                DutyResult(650.0, "ст. 333.26 п.2 НК РФ", "Расторжение брака по обоюдному согласию (с каждого)")
            ZagsType.DIVORCE_COURT ->
                DutyResult(350.0, "ст. 333.26 п.2 НК РФ", "Расторжение брака на основании решения суда")
            ZagsType.REPEAT_CERTIFICATE ->
                DutyResult(350.0, "ст. 333.26 п.6 НК РФ", "Повторное свидетельство / справка")
        }
    }

    fun calculateRosreestr(type: RosreestrType): DutyResult {
        return when (type) {
            RosreestrType.RIGHT_INDIVIDUAL ->
                DutyResult(2_000.0, "ст. 333.33 п.1 НК РФ", "Регистрация права (физическое лицо)")
            RosreestrType.RIGHT_LEGAL ->
                DutyResult(22_000.0, "ст. 333.33 п.1 НК РФ", "Регистрация права (юридическое лицо)")
            RosreestrType.MORTGAGE ->
                DutyResult(1_000.0, "ст. 333.33 п.1 НК РФ", "Регистрация ипотеки")
            RosreestrType.DDU ->
                DutyResult(350.0, "ст. 333.33 п.1 НК РФ", "Регистрация договора долевого участия")
        }
    }

    fun calculateFns(type: FnsType): DutyResult {
        return when (type) {
            FnsType.LLC ->
                DutyResult(4_000.0, "ст. 333.33 п.1 НК РФ", "Регистрация юридического лица (ООО)")
            FnsType.IP ->
                DutyResult(800.0, "ст. 333.33 п.1 НК РФ", "Регистрация ИП")
            FnsType.CHARTER_CHANGES ->
                DutyResult(800.0, "ст. 333.33 п.1 НК РФ", "Внесение изменений в учредительные документы")
            FnsType.LIQUIDATION ->
                DutyResult(800.0, "ст. 333.33 п.1 НК РФ", "Ликвидация юридического лица")
        }
    }
}
