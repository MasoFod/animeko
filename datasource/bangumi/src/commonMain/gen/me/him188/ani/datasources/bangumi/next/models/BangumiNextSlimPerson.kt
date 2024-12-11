/**
 *
 * Please note:
 * This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * Do not edit this file manually.
 *
 */

@file:Suppress(
    "ArrayInDataClass",
    "EnumEntryName",
    "RemoveRedundantQualifierName",
    "UnusedImport",
)

package me.him188.ani.datasources.bangumi.next.models

import kotlinx.serialization.Required
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 *
 *
 * @param comment
 * @param id
 * @param lock
 * @param name
 * @param nameCN
 * @param nsfw
 * @param type
 * @param images
 */
@Serializable

data class BangumiNextSlimPerson(

    @SerialName(value = "comment") @Required val comment: kotlin.Int,

    @SerialName(value = "id") @Required val id: kotlin.Int,

    @SerialName(value = "lock") @Required val lock: kotlin.Boolean,

    @SerialName(value = "name") @Required val name: kotlin.String,

    @SerialName(value = "nameCN") @Required val nameCN: kotlin.String,

    @SerialName(value = "nsfw") @Required val nsfw: kotlin.Boolean,

    @SerialName(value = "type") @Required val type: kotlin.Int,

    @SerialName(value = "images") val images: BangumiNextPersonImages? = null

)
