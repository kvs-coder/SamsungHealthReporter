package com.kvs.samsunghealthreporter.model.payload

import com.kvs.samsunghealthreporter.SamsungHealthException
import com.kvs.samsunghealthreporter.decorator.mapped
import com.kvs.samsunghealthreporter.model.Payload
import com.kvs.samsunghealthreporter.model.type.Gender
import com.samsung.android.sdk.health.data.data.UserDataPoint
import com.samsung.android.sdk.health.data.request.DataType
import kotlinx.serialization.Serializable

/**
 * **UserProfile** the user's Samsung Health profile. Read-only.
 *
 * @param nickname **String?** the nickname
 * @param gender **Gender?** the gender
 * @param dateOfBirth **String?** the date of birth, as Samsung Health stores it
 * @param height **Float?** height in centimeters
 * @param weight **Float?** weight in kilograms
 */
@Serializable
public data class UserProfile(
    val nickname: String? = null,
    val gender: Gender? = null,
    val dateOfBirth: String? = null,
    val height: Float? = null,
    val weight: Float? = null,
) : Payload {
    override val json: String get() = Companion.encode(this)

    /** Factory of **UserProfile** */
    public companion object : Payload.Factory<UserProfile>({ UserProfile.serializer() }) {
        @Throws(SamsungHealthException.InvalidValue::class)
        internal fun from(point: UserDataPoint): UserProfile =
            UserProfile(
                nickname = point.getValue(DataType.UserProfileDataType.NICKNAME),
                gender = point.getValue(DataType.UserProfileDataType.GENDER)?.mapped<Gender>(),
                dateOfBirth = point.getValue(DataType.UserProfileDataType.DATE_OF_BIRTH),
                height = point.getValue(DataType.UserProfileDataType.HEIGHT),
                weight = point.getValue(DataType.UserProfileDataType.WEIGHT),
            )
    }
}
