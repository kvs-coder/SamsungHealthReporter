package com.kvs.samsunghealthreporter.model.type

import kotlinx.serialization.Serializable

/**
 * **Gender** the gender of the user.
 *
 * Mirrors the Samsung Health Data SDK `DataType.UserProfileDataType.Gender` entries by name.
 */
@Serializable
public enum class Gender {
    GENDER_UNKNOWN,
    GENDER_MALE,
    GENDER_FEMALE,
}
