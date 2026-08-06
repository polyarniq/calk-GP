package com.polyarniq.calkgp.model

enum class CourtType {
    PROPERTY_CLAIM,
    NON_PROPERTY_GENERAL,
    DIVORCE,
    CHALLENGE_NPA,
    SPECIAL_PROCEEDING,
    SUPERVISORY,
    CASSATION,
    APPEAL
}

enum class ArbitrationType {
    PROPERTY_CLAIM,
    NON_PROPERTY_IP,
    NON_PROPERTY_ORG,
    APPEAL,
    CASSATION
}

enum class NotaryType {
    POWER_OF_ATTORNEY,
    WILL,
    INHERITANCE_DIRECT,
    INHERITANCE_OTHER,
    INHERITANCE_PROTECTION,
    DEAL_CERTIFICATION
}

enum class InheritanceRelation {
    DIRECT,
    OTHER
}

enum class RegistrationCategory {
    ZAGS,
    ROSREESTR,
    FNS
}

enum class ZagsType {
    MARRIAGE,
    DIVORCE_MUTUAL,
    DIVORCE_COURT,
    REPEAT_CERTIFICATE
}

enum class RosreestrType {
    RIGHT_INDIVIDUAL,
    RIGHT_LEGAL,
    MORTGAGE,
    DDU
}

enum class FnsType {
    LLC,
    IP,
    CHARTER_CHANGES,
    LIQUIDATION
}

data class DutyResult(
    val amount: Double,
    val legalReference: String,
    val description: String
)
