package com.kvs.samsunghealthreporter.decorator

val Number.roundedDecimal: Number
    get() {
        val string = String.format("%.3f", this)
        return if (string.contains(",")) {
            string.replace(",", ".").toDouble()
        } else {
            string.toDouble()
        }
    }

