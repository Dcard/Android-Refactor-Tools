package com.hiking.art.extensions

import kotlin.math.ceil
import kotlin.math.floor

private const val DIVIDER_LENGTH = 40
private const val DIVIDER_CHAR = '='

fun String.makeDivider(): String {
    val dividerSegmentLength = (DIVIDER_LENGTH - this.length - 2) / 2f
    return if (dividerSegmentLength <= 0) this else {
        val start = String(CharArray(floor(dividerSegmentLength).toInt()) { DIVIDER_CHAR })
        val end = String(CharArray(ceil(dividerSegmentLength).toInt()) { DIVIDER_CHAR })
        "$start $this $end"
    }
}

fun String.normalizeStringResourceName() = replace(".", "_")