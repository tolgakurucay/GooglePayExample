package com.tolgakurucay.googlepayexample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log

import com.android.billingclient.api.*
import com.tolgakurucay.googlepayexample.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    private lateinit var binding:ActivityMainBinding
    private lateinit var billingClient: BillingClient



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //initialized billing client
        billingClient=BillingClient.newBuilder(this)
            .enablePendingPurchases()
            .setListener(object:PurchasesUpdatedListener{
                override fun onPurchasesUpdated(p0: BillingResult, p1: MutableList<Purchase>?) {
                    if(p0.responseCode==BillingClient.BillingResponseCode.OK && p1!=null){
                       Log.d("deneme","completed")
                    }
                    else
                    {
                        Log.d("deneme","failed")
                    }

                }

            })
            .build()
        connectToGooglePayBilling()



    }

    private fun connectToGooglePayBilling(){
        billingClient.startConnection(
            object:BillingClientStateListener{
                override fun onBillingServiceDisconnected() {
                    connectToGooglePayBilling()
                }

                override fun onBillingSetupFinished(p0: BillingResult) {
                    if(p0.responseCode==BillingClient.BillingResponseCode.OK){
                        //
                        getProductDetails()
                    }
                }

            }
        )
    }

    private fun getProductDetails(){
        val productIds= arrayListOf<String>()
        productIds.add("300dollars")

        val getProductDetailsQuery=SkuDetailsParams
            .newBuilder()
            .setSkusList(productIds)
            .setType(BillingClient.SkuType.SUBS)//subscription or in app
            .build()

       billingClient.querySkuDetailsAsync(getProductDetailsQuery
       , object : SkuDetailsResponseListener {
               override fun onSkuDetailsResponse(p0: BillingResult, p1: MutableList<SkuDetails>?) {
                   if(p0.responseCode==BillingClient.BillingResponseCode.OK && p1!=null){
                       val itemInfo=p1.get(0)
                       binding.ItemName.setText(itemInfo.title)
                       binding.ItemPrice.setText(itemInfo.price)
                       binding.ItemPrice.setOnClickListener {
                           billingClient.launchBillingFlow(this@MainActivity,BillingFlowParams.newBuilder().setSkuDetails(itemInfo).build())
                       }


                   }
               }

           })

    }




}