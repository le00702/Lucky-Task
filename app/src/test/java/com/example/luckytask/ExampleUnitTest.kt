package com.example.luckytask

import com.example.luckytask.firestore.GroupTaskViewModel
import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    fun generateRandomAlphanumeric(length: Int): String {
        val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
        return (1..length)
            .map { allowedChars.random() }
            .joinToString("")
    }

    @Test
    fun generateRandomAlphanumericTest(){
        val length = 8
        for(i in 1..100) {
            val randomString = generateRandomAlphanumeric(length)
            println(randomString)
        }


    }
}