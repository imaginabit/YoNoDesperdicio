package com.imaginabit.yonodesperdicion.api;

import com.imaginabit.yonodesperdicion.models.Offer;
import com.imaginabit.yonodesperdicion.models.Offers;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface OffersEndPoints {

    @GET("api/offers")
    Call<Offers> getOffers();

    @GET("api/offers/{id}")
    Call<Offer> getOffer(@Path("id") String offer);

//    Call<Offer> (@Path("id") int groupId, @Query("sort") String sort);

//    @GET("group/{id}/users")
//    Call<List<User>> groupList(@Path("id") int groupId, @Query("sort") String sort);
//
//    @POST("users/new")
//    Call<User> createUser(@Body User user);

}
