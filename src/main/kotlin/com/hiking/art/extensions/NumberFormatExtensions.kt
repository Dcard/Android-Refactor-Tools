package com.hiking.art.extensions

import java.text.DecimalFormat

fun Int.formatForDisplay(): String = DecimalFormat("#,###").format(this)
fun Float.formatForDisplay(): String = DecimalFormat("#,###.###").format(this)