package com.tolgakurucay.googlepayexample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast

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
                        Toast.makeText(this@MainActivity,"Billing process has completed",Toast.LENGTH_SHORT).show()

                    }
                    else if(p0.responseCode==BillingClient.BillingResponseCode.USER_CANCELED){

                        Toast.makeText(this@MainActivity,"Billing process has canceled",Toast.LENGTH_SHORT).show()

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
                    try{
                        if(p0.responseCode==BillingClient.BillingResponseCode.OK){
                            getProductDetails()
                        }
                        else if(p0.responseCode==BillingClient.BillingResponseCode.USER_CANCELED){
                            Toast.makeText(this@MainActivity,"Exited the connecting",Toast.LENGTH_SHORT).show()
                        }
                    }
                    catch (ex:Exception){
                        Toast.makeText(this@MainActivity,"Error!\n"+ex.localizedMessage,Toast.LENGTH_SHORT).show()
                    }


                }

            }
        )
    }

    private fun getProductDetails(){
        val productIds= arrayListOf<String>()
        productIds.add("300dollars")
        productIds.add("100dollar")



        val getProductDetailsQuery=SkuDetailsParams
            .newBuilder()
            .setSkusList(productIds)
            .setType(BillingClient.SkuType.SUBS)//subscription or in app
            .build()

       billingClient.querySkuDetailsAsync(getProductDetailsQuery
       , object : SkuDetailsResponseListener {
               override fun onSkuDetailsResponse(p0: BillingResult, p1: MutableList<SkuDetails>?) {
                   if(p0.responseCode==BillingClient.BillingResponseCode.OK && p1!=null){
                       //for gold subs(300 dollars)
                       val goldItemInfo=p1.get(0)

                       binding.goldSubscriptionTextView.setText(goldItemInfo.title)
                       binding.goldSubscriptionButton.setText(goldItemInfo.price)
                       binding.goldSubscriptionButton.setOnClickListener {
                           billingClient.launchBillingFlow(this@MainActivity,BillingFlowParams.newBuilder().setSkuDetails(goldItemInfo).build())
                       }

                       //for premium subs(100 dollars)
                       val premiumItemInfo=p1.get(1)

                       binding.premiumSubscriptionTextView.setText(premiumItemInfo.title)
                       binding.premiumSubscriptionButton.setText(premiumItemInfo.price)
                       binding.premiumSubscriptionButton.setOnClickListener {
                           billingClient.launchBillingFlow(this@MainActivity,BillingFlowParams.newBuilder().setSkuDetails(premiumItemInfo).build())
                       }




                   }
               }

           })

    }




}