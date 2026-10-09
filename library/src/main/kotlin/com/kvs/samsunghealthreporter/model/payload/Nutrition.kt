package com.kvs.samsunghealthreporter.model.payload

import com.kvs.samsunghealthreporter.decorator.encoded
import com.kvs.samsunghealthreporter.decorator.endTimestamp
import com.kvs.samsunghealthreporter.decorator.mapped
import com.kvs.samsunghealthreporter.decorator.originalBuilder
import com.kvs.samsunghealthreporter.decorator.require
import com.kvs.samsunghealthreporter.decorator.source
import com.kvs.samsunghealthreporter.decorator.startTimestamp
import com.kvs.samsunghealthreporter.decorator.validateInterval
import com.kvs.samsunghealthreporter.decorator.zoneOffsetSeconds
import com.kvs.samsunghealthreporter.model.DataSource
import com.kvs.samsunghealthreporter.model.Payload
import com.kvs.samsunghealthreporter.model.type.HealthType
import com.kvs.samsunghealthreporter.model.type.MealType
import com.samsung.android.sdk.health.data.data.HealthDataPoint
import com.samsung.android.sdk.health.data.request.DataType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * **Nutrition** one food intake.
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
@SerialName("nutrition")
public data class Nutrition(
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

    override val healthType: HealthType get() = HealthType.NUTRITION

    override val json: String get() = encoded

    /**
     * **Harmonized** the values of Nutrition.
     *
     * @param title **String** the food's name
     * @param mealType **MealType** the meal the food belongs to
     * @param calories **Float** calories in kilocalories (0–99999)
     * @param carbohydrate **Float?** carbohydrate in grams
     * @param protein **Float?** protein in grams
     * @param totalFat **Float?** total fat in grams
     * @param saturatedFat **Float?** saturated fat in grams
     * @param monounsaturatedFat **Float?** monounsaturated fat in grams
     * @param polyunsaturatedFat **Float?** polyunsaturated fat in grams
     * @param transFat **Float?** trans fat in grams
     * @param sugar **Float?** sugar in grams
     * @param dietaryFiber **Float?** dietary fiber in grams
     * @param cholesterol **Float?** cholesterol in milligrams
     * @param sodium **Float?** sodium in milligrams
     * @param potassium **Float?** potassium in milligrams
     * @param calcium **Float?** calcium in milligrams
     * @param iron **Float?** iron in milligrams
     * @param vitaminA **Float?** vitamin A in micrograms
     * @param vitaminC **Float?** vitamin C in milligrams
     */
    @Serializable
    public data class Harmonized(
        val title: String,
        val mealType: MealType,
        val calories: Float,
        val carbohydrate: Float? = null,
        val protein: Float? = null,
        val totalFat: Float? = null,
        val saturatedFat: Float? = null,
        val monounsaturatedFat: Float? = null,
        val polyunsaturatedFat: Float? = null,
        val transFat: Float? = null,
        val sugar: Float? = null,
        val dietaryFiber: Float? = null,
        val cholesterol: Float? = null,
        val sodium: Float? = null,
        val potassium: Float? = null,
        val calcium: Float? = null,
        val iron: Float? = null,
        val vitaminA: Float? = null,
        val vitaminC: Float? = null,
    )

    // region Original
    internal fun asOriginal(): HealthDataPoint =
        originalBuilder()
            .addFieldData(DataType.NutritionType.TITLE, harmonized.title)
            .addFieldData(
                DataType.NutritionType.MEAL_TYPE,
                harmonized.mealType.mapped<DataType.NutritionType.MealType>(),
            ).addFieldData(DataType.NutritionType.CALORIES, harmonized.calories)
            .addFieldData(DataType.NutritionType.CARBOHYDRATE, harmonized.carbohydrate)
            .addFieldData(DataType.NutritionType.PROTEIN, harmonized.protein)
            .addFieldData(DataType.NutritionType.TOTAL_FAT, harmonized.totalFat)
            .addFieldData(DataType.NutritionType.SATURATED_FAT, harmonized.saturatedFat)
            .addFieldData(DataType.NutritionType.MONOSATURATED_FAT, harmonized.monounsaturatedFat)
            .addFieldData(DataType.NutritionType.POLYSATURATED_FAT, harmonized.polyunsaturatedFat)
            .addFieldData(DataType.NutritionType.TRANS_FAT, harmonized.transFat)
            .addFieldData(DataType.NutritionType.SUGAR, harmonized.sugar)
            .addFieldData(DataType.NutritionType.DIETARY_FIBER, harmonized.dietaryFiber)
            .addFieldData(DataType.NutritionType.CHOLESTEROL, harmonized.cholesterol)
            .addFieldData(DataType.NutritionType.SODIUM, harmonized.sodium)
            .addFieldData(DataType.NutritionType.POTASSIUM, harmonized.potassium)
            .addFieldData(DataType.NutritionType.CALCIUM, harmonized.calcium)
            .addFieldData(DataType.NutritionType.IRON, harmonized.iron)
            .addFieldData(DataType.NutritionType.VITAMIN_A, harmonized.vitaminA)
            .addFieldData(DataType.NutritionType.VITAMIN_C, harmonized.vitaminC)
            .build()
    // endregion

    /** Factory of **Nutrition** */
    public companion object : Payload.Factory<Nutrition>({ Nutrition.serializer() }) {
        internal fun from(point: HealthDataPoint): Nutrition =
            Nutrition(
                startTimestamp = point.startTimestamp,
                endTimestamp = point.endTimestamp,
                harmonized =
                    Harmonized(
                        title = point.require(DataType.NutritionType.TITLE),
                        mealType = point.require(DataType.NutritionType.MEAL_TYPE).mapped<MealType>(),
                        calories = point.require(DataType.NutritionType.CALORIES),
                        carbohydrate = point.getValue(DataType.NutritionType.CARBOHYDRATE),
                        protein = point.getValue(DataType.NutritionType.PROTEIN),
                        totalFat = point.getValue(DataType.NutritionType.TOTAL_FAT),
                        saturatedFat = point.getValue(DataType.NutritionType.SATURATED_FAT),
                        monounsaturatedFat = point.getValue(DataType.NutritionType.MONOSATURATED_FAT),
                        polyunsaturatedFat = point.getValue(DataType.NutritionType.POLYSATURATED_FAT),
                        transFat = point.getValue(DataType.NutritionType.TRANS_FAT),
                        sugar = point.getValue(DataType.NutritionType.SUGAR),
                        dietaryFiber = point.getValue(DataType.NutritionType.DIETARY_FIBER),
                        cholesterol = point.getValue(DataType.NutritionType.CHOLESTEROL),
                        sodium = point.getValue(DataType.NutritionType.SODIUM),
                        potassium = point.getValue(DataType.NutritionType.POTASSIUM),
                        calcium = point.getValue(DataType.NutritionType.CALCIUM),
                        iron = point.getValue(DataType.NutritionType.IRON),
                        vitaminA = point.getValue(DataType.NutritionType.VITAMIN_A),
                        vitaminC = point.getValue(DataType.NutritionType.VITAMIN_C),
                    ),
                uid = point.uid,
                clientDataId = point.clientDataId,
                zoneOffsetSeconds = point.zoneOffsetSeconds,
                dataSource = point.source,
            )
    }
}
