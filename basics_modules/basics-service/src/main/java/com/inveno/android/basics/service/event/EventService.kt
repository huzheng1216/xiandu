package com.inveno.android.basics.service.event

import org.slf4j.Logger
import org.slf4j.LoggerFactory

class EventService {
    companion object{
        private val eventListenerMap = mutableMapOf<String,MutableList<EventListener>>()

        private val logger:Logger = LoggerFactory.getLogger(EventService::class.java)

        fun register(name:String,listener:EventListener):EventCanceler{
            ensure(name).add(listener)
            return object : EventCanceler{
                override fun cancel() {
                    ensure(name).remove(listener)
                }
            }
        }

        fun post(name:String){
            postWith(name,"{}")
        }

        fun postWith(name: String,arg:String){
            logger.info("postWith name:[$name], arg:[$arg]")
            ensure(name).forEach {
                it.onEvent(name,arg)
            }
        }

        private fun ensure(name:String):MutableList<EventListener>{
            var result = eventListenerMap[name]
            if(result == null){
                result = mutableListOf()
                eventListenerMap[name] = result
            }
            return result
        }
    }
}