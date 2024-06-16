package com.bestapp.zipbab.ui.profile

enum class Location {
    START, MIDDLE, END;

    companion object {
        fun get(position: Int): Location? {
            return entries.getOrNull(position % entries.size)
        }
    }
}