package de.matthiasklenz.kflaky.adapters.github

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GithubRepoResponse(
    @SerialName("total_count")
    val totalCount: Double? = null,
    @SerialName("incomplete_results")
    val incompleteResults: Boolean? = null,
    val items: List<GitHubRepoItem>
)

@Serializable
data class GitHubRepoItem(
    val id: Double,
    @SerialName("node_id")
    val nodeId: String? = null,
    val name: String,
    @SerialName("full_name")
    val fullName: String? = null,
    val owner: GithubRepoOwner,
    val private: Boolean? = null,
    @SerialName("html_url")
    val htmlUrl: String? = null,
    @SerialName("description")
    val description: String? = null,
    val fork: Boolean? = null,
    val url: String? = null,
    @SerialName("created_at")
    val createdAt: String? = null,
    @SerialName("updated_at")
    val updatedAt: String? = null,
    @SerialName("pushed_at")
    val pushedAt: String? = null,
    val homepage: String? = null,
    val size: Double? = null,
    @SerialName("stargazers_count")
    val stargazersCount: Double? = null,
    @SerialName("watchers_count")
    val watchersCount: Double? = null,
    val language: String,
    @SerialName("forks_count")
    val forkCount: Double? = null,
    @SerialName("open_issues_count")
    val openIssuesCount: Double? = null,
    @SerialName("master_branch")
    val masterBranch: String? = null,
    @SerialName("default_branch")
    val defaultBranch: String? = null,
    val score: Double? = null,
    @SerialName("archive_url")
    val archiveUrl: String? = null,
    @SerialName("assignees_url")
    val assigneesUrl: String? = null,
    @SerialName("blobs_url")
    val blobsUrl: String? = null,
    @SerialName("branches_url")
    val branchesUrl: String? = null,
    @SerialName("collaborators_url")
    val collaboratorsUrl: String? = null,
    @SerialName("comments_url")
    val commentsUrl: String? = null,
    @SerialName("commits_url")
    val commitsUrl: String? = null,
    @SerialName("compare_url")
    val compareUrl: String? = null,
    @SerialName("contributors_url")
    val contributorsUrl: String? = null,
    @SerialName("deployments_url")
    val deploymentsUrl: String? = null,
    @SerialName("downloads_url")
    val downloadsUrl: String? = null,
    @SerialName("events_url")
    val eventsUrl: String? = null,
    @SerialName("forks_url")
    val forksUrl: String? = null,
    @SerialName("git_commits_url")
    val gitCommitsUrl: String? = null,
    @SerialName("git_refs_url")
    val gitRefsUrl: String? = null,
    @SerialName("git_tags_url")
    val gitTagsUrl: String? = null,
    @SerialName("git_url")
    val gitUrl: String? = null,
    @SerialName("issue_comment_url")
    val issueCommentUrl: String? = null,
    @SerialName("issue_events_url")
    val issueEventsUrl: String? = null,
    @SerialName("issues_url")
    val issuesUrl: String? = null,
    @SerialName("keys_url")
    val keysUrl: String? = null,
    @SerialName("labels_url")
    val labelsUrl: String? = null,
    @SerialName("languages_url")
    val languagesUrl: String? = null,
    @SerialName("merges_url")
    val mergesUrl: String? = null,
    @SerialName("milestones_url")
    val milestonesUrl: String? = null,
    @SerialName("notifications_url")
    val notificationsUrl: String? = null,
    @SerialName("pulls_url")
    val pullsUrl: String? = null,
    @SerialName("releases_url")
    val releasesUrl: String? = null,
    @SerialName("ssh_url")
    val sshUrl: String? = null,
    @SerialName("stargazers_url")
    val stargazersUrl: String? = null,
    @SerialName("statuses_url")
    val statusesUrl: String? = null,
    @SerialName("subscribers_url")
    val subscribersUrl: String? = null,
    @SerialName("subscription_url")
    val subscriptionUrl: String? = null,
    @SerialName("tags_url")
    val tagsUrl: String? = null,
    @SerialName("teams_url")
    val teamsUrl: String? = null,
    @SerialName("trees_url")
    val treesUrl: String? = null,
    @SerialName("clone_url")
    val cloneUrl: String,
    @SerialName("mirror_url")
    val mirrorUrl: String? = null,
    @SerialName("hooks_url")
    val hooksUrl: String? = null,
    @SerialName("svn_url")
    val svnUrl: String? = null,
    val forks: Double? = null,
    @SerialName("open_issues")
    val openIssues: Double? = null,
    val watchers: Double? = null,
    @SerialName("has_issues")
    val hasIssues: Boolean? = null,
    @SerialName("has_projects")
    val hasProjects: Boolean? = null,
    @SerialName("has_pages")
    val hasPages: Boolean? = null,
    @SerialName("has_wiki")
    val hasWiki: Boolean? = null,
    @SerialName("has_downloads")
    val hasDownloads: Boolean? = null,
    val archived: Boolean? = null,
    val disabled: Boolean? = null,
    val visibility: String? = null,
    val license: GithubRepoLicence? = null
)

@Serializable
data class GithubRepoLicence(
    val key: String? = null,
    val name: String? = null,
    val url: String? = null,
    @SerialName("spdx_id")
    val spdxId: String? = null,
    @SerialName("node_id")
    val nodeId: String? = null,
    @SerialName("html_url")
    val htmlUrl: String? = null,
)

@Serializable
data class GithubRepoOwner(
    val login: String? = null,
    val id: Double? = null,
    @SerialName("node_id")
    val nodeId: String? = null,
    @SerialName("avatar_url")
    val avatarUrl: String? = null,
    @SerialName("gravatar_id")
    val gravatarId: String? = null,
    val url: String? = null,
    @SerialName("received_events_url")
    val receivedEventsUrl: String? = null,
    val type: String? = null,
    @SerialName("html_url")
    val htmlUrl: String? = null,
    @SerialName("followers_url")
    val followersUrl: String? = null,
    @SerialName("following_url")
    val followingUrl: String? = null,
    @SerialName("gists_url")
    val gistsUrl: String? = null,
    @SerialName("starred_url")
    val starredUrl: String? = null,
    @SerialName("subscriptions_url")
    val subscriptionsUrl: String? = null,
    @SerialName("organizations_url")
    val organizationsUrl: String? = null,
    @SerialName("repos_url")
    val reposUrl: String? = null,
    @SerialName("events_url")
    val eventsUrl: String? = null,
    @SerialName("site_admin")
    val siteAdmin: Boolean? = null,
)