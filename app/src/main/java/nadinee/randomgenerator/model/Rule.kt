package nadinee.randomgenerator.model

import kotlinx.serialization.Serializable

@Serializable
data class Rule(
    val number: Int,
    val minPos: Int,
    val maxPos: Int
)