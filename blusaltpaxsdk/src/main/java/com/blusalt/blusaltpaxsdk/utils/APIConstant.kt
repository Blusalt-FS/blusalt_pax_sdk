package com.blusalt.blusaltpaxsdk.utils

import com.blusalt.blusaltpaxsdk.models.DesirailizeGeneric
import com.blusalt.blusaltpaxsdk.models.TerminalResponse
import com.blusalt.blusaltpaxsdk.network.BaseData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import retrofit2.Response
/**
 * Created by AYODEJI on 10/10/2020.
 *
 */
object APIConstant {
    const val BASE_LIVE_URL = "https://wallets.blusalt.net/"
//    const val BASE_LIVE_URL = "https://dev-wallets.blusalt.net/"

    const val POST_TRANSACTION = "pos"

    const val BASE_DEV_URL_OMNI = "https://dev-omnipro-api.blusalt.net/"
    const val BASE_LIVE_URL_OMNI_TMS = "https://omnipro-api.blusalt.net/tms/"


    const val ERROR = "Unable to initialize Blusalt SDK, API Key not found."
const val  incompleteParameters = "408"
    const val initializationError= "409"
     fun getBaseError(response: Response<*>): BaseData<TerminalResponse>? {
        val gson = Gson()
        val type =
            object : TypeToken<BaseData<*>?>() {}.type
        return gson.fromJson(response.errorBody()?.charStream(), type)
    }

//    fun sortCustom(custom:ArrayList<HorizonGenericPrinting>):List<HorizonGenericPrinting>{
//        return  custom.sortedBy { it.serialNumber }
//    }

    private fun prepareData(covidResponse: DesirailizeGeneric) {
        for (field in covidResponse.javaClass.declaredFields){


        }
    }
}