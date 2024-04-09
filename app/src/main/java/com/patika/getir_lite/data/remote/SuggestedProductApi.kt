package com.patika.getir_lite.data.remote

import com.patika.getir_lite.data.model.SuggestedProductContainerDto
import retrofit2.Response
import retrofit2.http.GET

interface SuggestedProductApi {
    @GET("suggestedProducts")
    suspend fun getSuggestedProducts(): Response<List<SuggestedProductContainerDto>>
}
