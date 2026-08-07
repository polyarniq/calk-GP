package com.polyarniq.calkgp.model

enum class CourtType {
    PROPERTY_CLAIM,
    NON_PROPERTY_CLAIM,
    NON_PROPERTY_GENERAL,
    CONTRACT_CLAIM,
    DIVORCE,
    CHALLENGE_NPA,
    NON_REGULATORY_ACT,
    SPECIAL_PROCEEDING,
    SUPERVISORY,
    CASSATION,
    APPEAL,
    COURT_ORDER
}

enum class ArbitrationType {
    PROPERTY_CLAIM,
    NON_PROPERTY_IP,
    NON_PROPERTY_ORG,
    APPEAL,
    CASSATION,
    SUPERVISORY,
    BANKRUPTCY_IP,
    BANKRUPTCY_ORG,
    COURT_ORDER
}

enum class NotaryType {
    POWER_OF_ATTORNEY,
    WILL,
    INHERITANCE_DIRECT,
    INHERITANCE_OTHER,
    INHERITANCE_PROTECTION,
    DEAL_CERTIFICATION
}

enum class ZagsType {
    MARRIAGE,
    DIVORCE_MUTUAL,
    DIVORCE_COURT,
    DIVORCE_ABSENT,
    NAME_CHANGE,
    PATERNITY,
    REPEAT_CERTIFICATE,
    ARCHIVE_CERTIFICATE
}

enum class RosreestrType {
    RIGHT_INDIVIDUAL,
    RIGHT_LEGAL,
    MORTGAGE,
    DDU,
    LAND_PLOT,
    CADASTRAL_ONLY
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
