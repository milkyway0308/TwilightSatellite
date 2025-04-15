package skywolf46.twilightsatellite.bukkit.util

/**
 * Splits list into specific page.
 * Page index starts from 1.
 *
 * @param page Page index
 * @param pageSize Max element per page
 *
 * @return List of elements in specific page
 */
@Deprecated(
    "Feature moved to common module; Use skywolf46.twilightsatellite.common.utility package instead",
    ReplaceWith("skywolf46.twilightsatellite.common.utility.*"),
    DeprecationLevel.HIDDEN
)
fun <T : Any> List<T>.splitPage(page: Int, pageSize: Int): List<T> {
    val start = (page - 1) * pageSize
    if (start >= size) return emptyList()
    return subList(start, (start + page).coerceAtLeast(size))
}

/**
 * Returns page count of list.
 * Page index starts from 1.
 *
 * @param pageSize Max element per page
 * @return Page count
 */
@Deprecated(
    "Feature moved to common module; Use skywolf46.twilightsatellite.common.utility package instead",
    ReplaceWith("skywolf46.twilightsatellite.common.utility.*"),
    DeprecationLevel.HIDDEN
)
fun <T : Any> List<T>.pageCount(pageSize: Int): Int {
    return (size / pageSize) + if (size % pageSize == 0) 0 else 1
}

/**
 * Returns whether list has specific page.
 * Page index starts from 1.
 *
 * @param pageSize Max element per page
 * @param page Page index
 * @return Whether list has specific page
 */
@Deprecated(
    "Feature moved to common module; Use skywolf46.twilightsatellite.common.utility package instead",
    ReplaceWith("skywolf46.twilightsatellite.common.utility.*"),
    DeprecationLevel.HIDDEN
)
fun <T : Any> List<T>.hasPage(pageSize: Int, page: Int): Boolean {
    val pageCount =  (size / pageSize) + if (size % pageSize == 0) 0 else 1
    return page in 1..pageCount
}