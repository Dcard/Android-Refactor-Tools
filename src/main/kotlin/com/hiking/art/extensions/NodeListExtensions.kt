package com.hiking.art.extensions

import org.w3c.dom.NodeList

fun NodeList.asCollection() = (0 until length).map { item(it) }