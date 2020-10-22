package com.kvs.samsunghealthreporter.decorator

val Double.roundedDecimal: Double
    get() {
        val string = String.format("%.3f", this)
        return if (string.contains(",")) {
            string.replace(",", ".").toDouble()
        } else {
            string.toDouble()
        }
    }

