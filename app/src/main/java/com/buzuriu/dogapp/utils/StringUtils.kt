package com.buzuriu.dogapp.utils

import java.util.*

class StringUtils {
    companion object {
        fun isEmailValid(email: String): Boolean {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
        }

        fun getRandomUID(): String {
            return UUID.randomUUID().toString()
        }

        fun isLetters(string: String): Boolean {
            return string.all { it.isLetter() }
        }

        fun removeFirstCharacterIfWhitespace(word: String): String {
            var newWord = String()
            newWord = word.removePrefix(" ")
            return newWord
        }

    }
}