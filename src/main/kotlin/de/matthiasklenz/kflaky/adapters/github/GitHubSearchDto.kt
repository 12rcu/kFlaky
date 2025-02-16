package de.matthiasklenz.kflaky.adapters.github

import kotlinx.serialization.Serializable

@Serializable
data class GithubRequest(
    val token: String,
    val query: String,
)
