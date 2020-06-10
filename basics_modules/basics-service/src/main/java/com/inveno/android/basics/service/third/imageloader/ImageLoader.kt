package com.inveno.android.basics.service.third.imageloader

import android.content.Context
import android.graphics.drawable.Drawable
import android.net.Uri
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.inveno.android.basics.service.callback.BaseStatefulCallBack
import com.inveno.android.basics.service.callback.StatefulCallBack
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class ImageLoader {
    companion object{
        private val logger:Logger = LoggerFactory.getLogger(ImageLoader::class.java)

        val supportGifListener = object : RequestListener<Drawable>{
            override fun onLoadFailed(
                e: GlideException?,
                model: Any?,
                target: Target<Drawable>?,
                isFirstResource: Boolean
            ): Boolean {
                return false
            }

            override fun onResourceReady(
                resource: Drawable?,
                model: Any?,
                target: Target<Drawable>?,
                dataSource: DataSource?,
                isFirstResource: Boolean
            ): Boolean {
                resource?.let {
                    if(it is GifDrawable){
                        it.setLoopCount(1)
                    }
                }
                return false
            }

        }
        fun loadImage(imageView: ImageView,imageUrl:String){
            logger.info("loadImage imageUrl:$imageUrl")
            if(imageUrl.startsWith("android")){
                Glide.with(imageView).load(Uri.parse(imageUrl)).listener(supportGifListener).into(imageView)
            }else{
                Glide.with(imageView).load(imageUrl).listener(supportGifListener).into(imageView)
            }

        }

        fun loadDrawable(context: Context, imageUrl:String):StatefulCallBack<Drawable>{
            logger.info("loadDrawable imageUrl:$imageUrl")
            return object  : BaseStatefulCallBack<Drawable>() {
                override fun execute() {
                    Glide.with(context).load(imageUrl)
                        .listener(object:RequestListener<Drawable>{
                            override fun onLoadFailed(
                                e: GlideException?,
                                model: Any?,
                                target: Target<Drawable>?,
                                isFirstResource: Boolean
                            ): Boolean {
                                invokeFail(900,"net error")
                                return true
                            }

                            override fun onResourceReady(
                                resource: Drawable?,
                                model: Any?,
                                target: Target<Drawable>?,
                                dataSource: DataSource?,
                                isFirstResource: Boolean
                            ): Boolean {
                                invokeSuccess(resource!!)
                                return true
                            }
                        }).submit()
                }
            }
        }
    }
}