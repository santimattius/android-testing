package com.santimattius.template.ui.compose

import android.content.Intent

private const val SCHEME = "androidtesting"
private const val HOST = "movies"

/**
 * Returns the movie id of an `androidtesting://movies/{id}` view intent, or `null` otherwise.
 *
 * Reopening a finished task from Recents redelivers its original intent. That link was already
 * handled, so it is ignored.
 *
 * Any app can send this intent, including explicitly and so bypassing the manifest filter, which
 * is why every part is validated here. Only the id is read; nothing else from the intent is used.
 */
internal fun Intent.linkedMovieId(): Long? {
    val fromRecents = flags and Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY != 0
    val link = data?.takeIf {
        !fromRecents && action == Intent.ACTION_VIEW && it.scheme == SCHEME && it.host == HOST
    }
    return link?.pathSegments?.singleOrNull()?.toLongOrNull()
}
