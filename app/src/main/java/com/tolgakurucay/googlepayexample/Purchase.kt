package com.tolgakurucay.googlepayexample

data class Purchase(
    val productId:String,
    val purchaseToken:String,
    val customerName:String,
    val customerEmail:String,
    val purchaseTime:String,
    val quantity:String,
    val developerPayload:String




)
