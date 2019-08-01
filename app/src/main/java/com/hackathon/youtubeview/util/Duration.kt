package com.hackathon.youtubeview.util

import java.text.ParseException

class Duration {
    var hours = 0
    var minutes = 0
    var seconds = 0

    override fun toString(): String {
        return if (hours > 0) {
            "${hours}h${minutes}m${seconds}s"
        } else {
            "$minutes:$seconds"
        }
    }

    companion object {
        fun parse(iso: String): Duration {
            if (iso[0] != 'P') {
                throw ParseException("Durations must start with P!", 0)
            }

            val duration = Duration()
            var timeMode = false

            var acc = 0
            for (i in 1 until iso.length) {
                if (iso[i].isDigit()) {
                    acc = acc*10 + Character.getNumericValue(iso[i])
                    continue
                } else if (timeMode) {
                    when (iso[i]) {
                        'H' -> duration.hours += acc
                        'M' -> duration.minutes += acc
                        'S' -> duration.seconds += acc
                    }
                } else {
                    when (iso[i]) {
                        'Y' -> duration.hours += acc * 365 * 24
                        'M' -> duration.hours += acc * 24 * 30
                        'W' -> duration.hours += acc * 24 * 7
                        'D' -> duration.hours += acc * 24
                        'T' -> timeMode = true
                    }
                }

                acc = 0
            }

            return duration
        }
    }
}