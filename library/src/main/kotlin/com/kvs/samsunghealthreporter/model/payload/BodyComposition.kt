package com.kvs.samsunghealthreporter.model.payload

import com.kvs.samsunghealthreporter.decorator.encoded
import com.kvs.samsunghealthreporter.decorator.endTimestamp
import com.kvs.samsunghealthreporter.decorator.originalBuilder
import com.kvs.samsunghealthreporter.decorator.require
import com.kvs.samsunghealthreporter.decorator.source
import com.kvs.samsunghealthreporter.decorator.startTimestamp
import com.kvs.samsunghealthreporter.decorator.validateInterval
import com.kvs.samsunghealthreporter.decorator.zoneOffsetSeconds
import com.kvs.samsunghealthreporter.model.DataSource
import com.kvs.samsunghealthreporter.model.Payload
import com.kvs.samsunghealthreporter.model.type.HealthType
import com.samsung.android.sdk.health.data.data.HealthDataPoint
import com.samsung.android.sdk.health.data.request.DataType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * **BodyComposition** weight and body composition.
 *
 * @param startTimestamp **Long** epoch milliseconds
 * @param endTimestamp **Long?** epoch milliseconds; null for an instantaneous measurement
 * @param harmonized **Harmonized** the measured values
 * @param uid **String?** Samsung Health id; null until inserted
 * @param clientDataId **String?** your own id, 1–36 characters
 * @param zoneOffsetSeconds **Int?** zone offset in seconds
 * @param dataSource **DataSource?** writing app and device; set by Samsung Health
 * @throws com.kvs.samsunghealthreporter.SamsungHealthException.InvalidValue when [endTimestamp] is before [startTimestamp]
 */
@Serializable
@SerialName("body_composition")
public data class BodyComposition(
    override val startTimestamp: Long,
    override val endTimestamp: Long? = null,
    val harmonized: Harmonized,
    override val uid: String? = null,
    override val clientDataId: String? = null,
    override val zoneOffsetSeconds: Int? = null,
    override val dataSource: DataSource? = null,
) : WritableSample {
    init {
        validateInterval()
    }

    override val healthType: HealthType get() = HealthType.BODY_COMPOSITION

    override val json: String get() = encoded

    /**
     * **Harmonized** the values of BodyComposition.
     *
     * @param weight **Float** weight in kilograms (2–500)
     * @param height **Float?** height in centimeters (20–300)
     * @param bodyFat **Float?** body fat in percent (0–100)
     * @param bodyFatMass **Float?** body fat mass in kilograms (2–500)
     * @param bodyMassIndex **Float?** body mass index (0–90), calculated by Samsung Health
     * @param fatFree **Float?** fat-free mass in percent (0–100)
     * @param fatFreeMass **Float?** fat-free mass in kilograms (2–500)
     * @param muscleMass **Float?** muscle mass in percent (10–99)
     * @param skeletalMuscle **Float?** skeletal muscle in percent (0–100)
     * @param skeletalMuscleMass **Float?** skeletal muscle mass in kilograms (2–500)
     * @param totalBodyWater **Float?** total body water in liters (2–500)
     * @param basalMetabolicRate **Int?** basal metabolic rate in kilocalories per day (100–12000)
     */
    @Serializable
    public data class Harmonized(
        val weight: Float,
        val height: Float? = null,
        val bodyFat: Float? = null,
        val bodyFatMass: Float? = null,
        val bodyMassIndex: Float? = null,
        val fatFree: Float? = null,
        val fatFreeMass: Float? = null,
        val muscleMass: Float? = null,
        val skeletalMuscle: Float? = null,
        val skeletalMuscleMass: Float? = null,
        val totalBodyWater: Float? = null,
        val basalMetabolicRate: Int? = null,
    )

    // region Original
    internal fun asOriginal(): HealthDataPoint =
        originalBuilder()
            .addFieldData(DataType.BodyCompositionType.WEIGHT, harmonized.weight)
            .addFieldData(DataType.BodyCompositionType.HEIGHT, harmonized.height)
            .addFieldData(DataType.BodyCompositionType.BODY_FAT, harmonized.bodyFat)
            .addFieldData(DataType.BodyCompositionType.BODY_FAT_MASS, harmonized.bodyFatMass)
            .addFieldData(DataType.BodyCompositionType.BODY_MASS_INDEX, harmonized.bodyMassIndex)
            .addFieldData(DataType.BodyCompositionType.FAT_FREE, harmonized.fatFree)
            .addFieldData(DataType.BodyCompositionType.FAT_FREE_MASS, harmonized.fatFreeMass)
            .addFieldData(DataType.BodyCompositionType.MUSCLE_MASS, harmonized.muscleMass)
            .addFieldData(DataType.BodyCompositionType.SKELETAL_MUSCLE, harmonized.skeletalMuscle)
            .addFieldData(DataType.BodyCompositionType.SKELETAL_MUSCLE_MASS, harmonized.skeletalMuscleMass)
            .addFieldData(DataType.BodyCompositionType.TOTAL_BODY_WATER, harmonized.totalBodyWater)
            .addFieldData(DataType.BodyCompositionType.BASAL_METABOLIC_RATE, harmonized.basalMetabolicRate)
            .build()
    // endregion

    /** Factory of **BodyComposition** */
    public companion object : Payload.Factory<BodyComposition>({ BodyComposition.serializer() }) {
        internal fun from(point: HealthDataPoint): BodyComposition =
            BodyComposition(
                startTimestamp = point.startTimestamp,
                endTimestamp = point.endTimestamp,
                harmonized =
                    Harmonized(
                        weight = point.require(DataType.BodyCompositionType.WEIGHT),
                        height = point.getValue(DataType.BodyCompositionType.HEIGHT),
                        bodyFat = point.getValue(DataType.BodyCompositionType.BODY_FAT),
                        bodyFatMass = point.getValue(DataType.BodyCompositionType.BODY_FAT_MASS),
                        bodyMassIndex = point.getValue(DataType.BodyCompositionType.BODY_MASS_INDEX),
                        fatFree = point.getValue(DataType.BodyCompositionType.FAT_FREE),
                        fatFreeMass = point.getValue(DataType.BodyCompositionType.FAT_FREE_MASS),
                        muscleMass = point.getValue(DataType.BodyCompositionType.MUSCLE_MASS),
                        skeletalMuscle = point.getValue(DataType.BodyCompositionType.SKELETAL_MUSCLE),
                        skeletalMuscleMass = point.getValue(DataType.BodyCompositionType.SKELETAL_MUSCLE_MASS),
                        totalBodyWater = point.getValue(DataType.BodyCompositionType.TOTAL_BODY_WATER),
                        basalMetabolicRate = point.getValue(DataType.BodyCompositionType.BASAL_METABOLIC_RATE),
                    ),
                uid = point.uid,
                clientDataId = point.clientDataId,
                zoneOffsetSeconds = point.zoneOffsetSeconds,
                dataSource = point.source,
            )
    }
}
