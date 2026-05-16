package com.elliewonderland.achtsamkeit.data.repository

import android.app.Activity
import com.revenuecat.purchases.Purchases
import com.revenuecat.purchases.PurchaseParams
import com.revenuecat.purchases.awaitCustomerInfo
import com.revenuecat.purchases.awaitOfferings
import com.revenuecat.purchases.awaitPurchase

object PremiumRepository {

    private const val ENTITLEMENT = "premium"

    suspend fun isPremium(): Boolean = runCatching {
        Purchases.sharedInstance.awaitCustomerInfo()
            .entitlements[ENTITLEMENT]?.isActive == true
    }.getOrDefault(false)

    suspend fun purchase(activity: Activity): Boolean = runCatching {
        val offerings = Purchases.sharedInstance.awaitOfferings()
        val pkg = offerings.current?.monthly ?: return@runCatching false
        val result = Purchases.sharedInstance.awaitPurchase(
            PurchaseParams.Builder(activity, pkg).build()
        )
        result.customerInfo.entitlements[ENTITLEMENT]?.isActive == true
    }.getOrDefault(false)
}
