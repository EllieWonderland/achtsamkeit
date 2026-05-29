package com.elliewonderland.achtsamkeit.model

object MoodKey {
    // Morning
    const val EXCITEMENT  = "excitement"
    const val PEACE       = "peace"
    const val TIREDNESS   = "tiredness"
    const val ANXIETY     = "anxiety"
    const val MELANCHOLY  = "melancholy"
    // Evening
    const val SATISFACTION = "satisfaction"
    const val RELIEF       = "relief"
    const val EXHAUSTION   = "exhaustion"
    const val OVERWHELMED  = "overwhelmed"
    const val LONELINESS   = "loneliness"
    // Legacy (Rückwärtskompatibilität)
    const val JOY     = "joy"
    const val STRESS  = "stress"
    const val BALANCE = "balance"
    const val SADNESS = "sadness"
}

object EnergyKey {
    // Morning
    const val FULL   = "full"
    const val MEDIUM = "medium"
    const val LOW    = "low"
    const val EMPTY  = "empty"
    // Evening
    const val SATISFIED_TIRED = "satisfied_tired"
    const val WIRED           = "wired"
}

object GratitudeKey {
    // Morning
    const val RELATIONS       = "relations"
    const val COMFORT         = "comfort"
    const val HEALTH          = "health"
    const val NATURE          = "nature"
    const val OPPORTUNITY     = "opportunity"
    const val SELF_COMPASSION = "self_compassion"
    const val STRUGGLED       = "struggled"
    // Evening
    const val ENCOUNTER        = "encounter"
    const val MICRO_JOYS       = "micro_joys"
    const val ACHIEVEMENT      = "achievement"
    const val LEARNING         = "learning"
    const val COMFORT_RECEIVED = "comfort_received"
    const val CONNECTION       = "connection"
    const val NONE             = "none"
    // Legacy (Rückwärtskompatibilität)
    const val PEOPLE   = "people"
    const val BODY     = "body"
    const val PLEASURE = "pleasure"
}

object SelfCareKey {
    // Morning
    const val PHYSICAL      = "physical"
    const val BOUNDARIES    = "boundaries"
    const val DIGITAL_DETOX = "digital_detox"
    const val SOUL          = "soul"
    const val STILLNESS     = "stillness"
    const val COMPASSION    = "compassion"
    const val NO_ENERGY     = "no_energy"
    // Evening
    const val NEEDS_MET       = "needs_met"
    const val BOUNDARIES_KEPT = "boundaries_kept"
    const val UNPLUGGED       = "unplugged"
    const val JOYFUL_MOMENT   = "joyful_moment"
    const val RELEASE         = "release"
    const val FORGIVENESS     = "forgiveness"
    const val NEGLECTED       = "neglected"
    // Legacy (Rückwärtskompatibilität)
    const val BREATHING = "breathing"
    const val OUTSIDE   = "outside"
}
