package skywolf46.twilightsatellite.common.data

data class ConditionMatcher(val baseComparator: Comparator, val left: String, val right: Double) {
    companion object {
        fun fromString(str: String): ConditionMatcher {
            if (!str.endsWith(")")) {
                return ConditionMatcher(Comparator.POSITIVE, str, 0.0)
            }
            val firstStringBuilder = StringBuilder()
            var pointer = 0
            while (pointer < str.length && str[pointer] != '(') {
                firstStringBuilder.append(str[pointer])
                pointer++
            }
            val rightSide = str.substring(pointer + 1, str.length - 1).trim()
            val comparator = Comparator.values().firstOrNull { rightSide.startsWith(it.character) }
                ?: throw IllegalArgumentException("Invalid comparator: $str")
            return ConditionMatcher(comparator, firstStringBuilder.toString(), rightSide.substring(comparator.character.length).toDouble())
        }
    }

    fun compare(leftValue: Double): Boolean {
        return baseComparator.comparator(leftValue, right)
    }

    enum class Comparator(val character: String, val comparator: (Double, Double) -> Boolean) {
        EQUAL("==", { a, b -> a == b }),
        NOT_EQUAL("!=", { a, b -> a != b }),
        GREATER_THAN_OR_EQUAL(">=", { a, b -> a >= b }),
        LESS_THAN_OR_EQUAL("<=", { a, b -> a <= b }),
        GREATER_THAN(">", { a, b -> a > b }),
        LESS_THAN("<", { a, b -> a < b }),
        POSITIVE("TO_PREVENT_POSITIVE_COMPARATOR", { a, b -> a >= 0 })
    }
}