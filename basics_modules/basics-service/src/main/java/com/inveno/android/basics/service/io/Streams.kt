package com.inveno.android.basics.service.io

import java.io.InputStream
import java.io.OutputStream

class Streams {
    companion object {

        fun copy(input:InputStream,output:OutputStream){
            val tempArray = ByteArray(1024)
            var offset = 0
            val expectLength = tempArray.size
            var realLength = 0
            do {
                realLength = input.read(tempArray,0,expectLength)
                if(realLength>0){
                    output.write(tempArray,0,realLength)
                    offset+=realLength
                }
            }while (realLength>0)
        }
    }
}