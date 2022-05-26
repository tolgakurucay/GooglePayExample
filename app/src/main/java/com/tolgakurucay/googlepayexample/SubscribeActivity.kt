package com.tolgakurucay.googlepayexample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.android.billingclient.api.*
import com.android.billingclient.api.Purchase
import com.tolgakurucay.googlepayexample.databinding.ActivitySubscribeBinding


class SubscribeActivity : AppCompatActivity() {

    private lateinit var binding:ActivitySubscribeBinding
    private lateinit var billingClient: BillingClient
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivitySubscribeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //initializing billing Client

        billingClient=BillingClient.newBuilder(this)
            .enablePendingPurchases()
            .setListener(object:PurchasesUpdatedListener{
                /* override fun onPurchasesUpdated(p0: BillingResult, p1: MutableList<Purchase>?) {
                    if(p0.responseCode==BillingClient.BillingResponseCode.OK && p1!=null){
                        //completed
                    }
                    else
                    {
                        //failed
                    }
                }*/
                override fun onPurchasesUpdated(p0: BillingResult, p1: MutableList<Purchase>?) {
                    TODO("Not yet implemented")
                }

            })
            .build()

        establishConnection()

    }


    fun establishConnection() {
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    // The BillingClient is ready. You can query purchases here.
                    showProducts()
                }
            }

            override fun onBillingServiceDisconnected() {
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.
                establishConnection()
            }
        })
    }

    fun showProducts() {
        val skuList: MutableList<String> = ArrayList()
        skuList.add("300dollars")
        val params = SkuDetailsParams.newBuilder()
        params.setSkusList(skuList).setType(BillingClient.SkuType.SUBS)
        billingClient.querySkuDetailsAsync(
            params.build()
        ) { billingResult, skuDetailsList ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && skuDetailsList != null) {
                // Process the result.
                for (skuDetails in skuDetailsList) {
                    if (skuDetails.sku == "300dollars") {
                        //Now update the UI

                        binding.txtPrice.setText(skuDetails.price + " Per Month")
                        binding.txtPrice.setOnClickListener { view -> launchPurchaseFlow(skuDetails) }
                    }
                }
            }
        }
    }

    fun launchPurchaseFlow(skuDetails: SkuDetails?) {
        val billingFlowParams = BillingFlowParams.newBuilder()
            .setSkuDetails(skuDetails!!)
            .build()
        billingClient.launchBillingFlow(this@SubscribeActivity, billingFlowParams)

    }

   /* fun verifySubPurchase(purchases: Purchase) {
        val acknowledgePurchaseParams = AcknowledgePurchaseParams
            .newBuilder()
            .setPurchaseToken(purchases.purchaseToken)
            .build()
        billingClient.acknowledgePurchase(
            acknowledgePurchaseParams
        ) { billingResult ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                //Toast.makeText(SubscriptionActivity.this, "Item Consumed", Toast.LENGTH_SHORT).show();
                // Handle the success of the consume operation.
                //user prefs to set premium
                Toast.makeText(this@StoreActivity, "You are a premium user now", Toast.LENGTH_SHORT)
                    .show()
                //updateUser();

                //Setting premium to 1
                // 1 - premium
                //0 - no premium
                prefs.setPremium(1)
            }
        }
        Log.d(TAG, "Purchase Token: " + purchases.purchaseToken)
        Log.d(TAG, "Purchase Time: " + purchases.purchaseTime)
        Log.d(TAG, "Purchase OrderID: " + purchases.orderId)
    }*/

   /* override fun onResume() {
        super.onResume()
        billingClient.queryPurchasesAsync(
            BillingClient.SkuType.SUBS
        ) { billingResult, list ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                for (purchase in list) {
                    if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED && !purchase.isAcknowledged) {
                        verifySubPurchase(purchase)
                    }
                }
            }
        }
    }*/
    /*fun checkSubscription() {
        billingClient = BillingClient.newBuilder(this).enablePendingPurchases()
            .setListener { billingResult: BillingResult?, list: List<Purchase?>? -> }
            .build()
        val finalBillingClient = billingClient
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingServiceDisconnected() {}
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    finalBillingClient.queryPurchasesAsync(
                        BillingClient.SkuType.SUBS
                    ) { billingResult1: BillingResult, list: List<Purchase?> ->
                        //this "list" will contain all the sub purchases.
                        if (billingResult1.responseCode == BillingClient.BillingResponseCode.OK && list.size > 0) {
                            //list is more than 0 meaning there is an active subscription available
                            prefs.setPremium(1)
                        } else if (list.size == 0) {
                            //When the list returns zero, it means there are no active subscription
                            prefs.setPremium(0)
                        }
                    }
                }
            }
        })
    }*/


}