package com.reshmenamma.pride.logic

import androidx.compose.ui.graphics.Color
import java.util.concurrent.TimeUnit

enum class InstarStage(val id: Int, val displayName: String) {
    STAGE_1(1, "1st Instar"),
    STAGE_2(2, "2nd Instar"),
    STAGE_3(3, "3rd Instar"),
    STAGE_4(4, "4th Instar"),
    STAGE_5(5, "5th Instar"),
    COCOON(6, "Cocooning");

    companion object {
        fun fromId(id: Int) = entries.find { it.id == id } ?: STAGE_1
    }
}

data class InstarRequirements(
    val minTemp: Float,
    val maxTemp: Float,
    val minHum: Float,
    val maxHum: Float,
    val description: String,
    val dos: List<String>,
    val donts: List<String>
)

object InstarEngine {
    val Varieties = listOf(
        "PM (Pure Mysore)",
        "CSR2 x CSR4 (Bivoltine Hybrid)",
        "FC1 x FC2",
        "Kolar Gold",
        "M5",
        "V1"
    )

    val Requirements = mapOf(
        InstarStage.STAGE_1 to InstarRequirements(
            26f, 28f, 85f, 90f,
            "Very small, sensitive. Require high humidity and tender leaves.",
            listOf("Use paraffin paper to maintain humidity", "Feed finely chopped tender leaves", "Keep the rearing room warm"),
            listOf("Don't expose to direct sunlight", "Don't allow leaves to dry up", "Avoid strong wind/drafts")
        ),
        InstarStage.STAGE_2 to InstarRequirements(
            25f, 27f, 80f, 85f,
            "Steady growth. Still sensitive to temperature fluctuations.",
            listOf("Clean the bed regularly", "Ensure uniform spacing", "Keep room temperature stable"),
            listOf("Don't overcrowding", "Avoid feeding wet leaves", "Don't use strong chemicals nearby")
        ),
        InstarStage.STAGE_3 to InstarRequirements(
            24f, 26f, 75f, 80f,
            "Rapid growth. Start of 'Late Age' rearing.",
            listOf("Increase spacing between worms", "Provide good ventilation", "Feed medium-aged leaves"),
            listOf("Don't keep bed too thick", "Avoid high humidity during moulting", "Don't disturb during moult")
        ),
        InstarStage.STAGE_4 to InstarRequirements(
            23f, 25f, 70f, 75f,
            "Heavy feeding stage. Lots of leaf required.",
            listOf("Maintain clean environment", "Plenty of fresh air", "Feed mature leaves"),
            listOf("Avoid temperature above 30°C", "Don't feed yellow/wilted leaves", "Don't allow waste to accumulate")
        ),
        InstarStage.STAGE_5 to InstarRequirements(
            22f, 24f, 65f, 70f,
            "Maximum growth. Voracious eaters.",
            listOf("Spread the worms widely", "Ensure maximum ventilation", "Keep temperature cool"),
            listOf("Don't allow bed to become damp", "Avoid overcrowding at all costs", "Don't feed dust-covered leaves")
        ),
        InstarStage.COCOON to InstarRequirements(
            22f, 24f, 60f, 65f,
            "Spinning silk. Needs stable environment.",
            listOf("Transfer to mountages on time", "Keep room well ventilated", "Maintain low humidity"),
            listOf("Don't disturb the worms while spinning", "Avoid high temperature", "Don't harvest too early")
        )
    )

    fun calculateCurrentStage(startStageId: Int, startDateMs: Long): InstarStage {
        val diffMs = System.currentTimeMillis() - startDateMs
        val daysPassed = TimeUnit.MILLISECONDS.toDays(diffMs).toInt()
        
        // Simple progression: Each stage lasts roughly 3-5 days
        // This is a simplification; in reality it depends on temperature
        val additionalStages = daysPassed / 4
        val currentStageId = (startStageId + additionalStages).coerceAtMost(6)
        return InstarStage.fromId(currentStageId)
    }

    fun getStatusColor(temp: Float, hum: Float, stage: InstarStage): Color {
        val req = Requirements[stage] ?: return Color.Gray
        val tempDiff = if (temp > req.maxTemp) temp - req.maxTemp else if (temp < req.minTemp) req.minTemp - temp else 0f
        val humDiff = if (hum > req.maxHum) hum - req.maxHum else if (hum < req.minHum) req.minHum - hum else 0f
        
        return when {
            tempDiff == 0f && humDiff == 0f -> Color(0xFF22C55E) // Green (Optimal)
            tempDiff < 2f && humDiff < 5f -> Color(0xFFF59E0B) // Orange (Warning)
            else -> Color(0xFFEF4444) // Red (Danger)
        }
    }

    fun getDetailedAdvice(temp: Float, hum: Float, stage: InstarStage): List<AdviceItem> {
        val req = Requirements[stage] ?: return emptyList()
        val advice = mutableListOf<AdviceItem>()

        if (temp > req.maxTemp) {
            advice.add(AdviceItem("Temperature too high!", "Open windows, use fans, or spread wet gunny bags on the floor.", true))
        } else if (temp < req.minTemp) {
            advice.add(AdviceItem("Temperature too low!", "Use heaters or charcoal stoves safely to warm the room.", true))
        }

        if (hum > req.maxHum) {
            advice.add(AdviceItem("Humidity too high!", "Increase ventilation and sprinkle lime powder on the rearing bed.", false))
        } else if (hum < req.minHum) {
            advice.add(AdviceItem("Humidity too low!", "Sprinkle water on the floor and hang wet curtains near windows.", false))
        }

        return advice
    }

    data class AdviceItem(val issue: String, val action: String, val isTemp: Boolean)
}
