package com.blusalt.blusaltpaxsdk.models

import android.os.Parcelable
import androidx.annotation.Keep
import com.blusalt.blusaltpaxsdk.models.printing.MetaDataMerchant
import kotlinx.android.parcel.Parcelize

@Parcelize
@Keep
data class DesirailizeGeneric (
    val customerName:String?,
    val customerEmail:String?, val reference:String?,
    val narration:String?,
    val amount:Double?,
    val settled_amount:Double?, val currency:String?,
    val description:String?,
    val type:String?, val source:String?, val status:String?, val metadata: MetaDataMerchant?,
    val createdAt:String?, val updatedAt:String?,
    ): Parcelable{



}