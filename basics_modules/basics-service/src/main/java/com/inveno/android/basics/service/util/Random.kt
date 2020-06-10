package com.inveno.android.basics.service.util

import java.util.*

val random = Random()

fun randomInt(max:Int):Int{
    return random.nextInt(max)
}