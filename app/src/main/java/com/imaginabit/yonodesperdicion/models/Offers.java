
package com.imaginabit.yonodesperdicion.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Offers {

    @SerializedName("total_offers")
    @Expose
    private Integer totalOffers;
    @SerializedName("pages")
    @Expose
    private Integer pages;
    @SerializedName("offers")
    @Expose
    private List<Offer> offers = null;

    public Integer getTotalOffers() {
        return totalOffers;
    }

    public void setTotalOffers(Integer totalOffers) {
        this.totalOffers = totalOffers;
    }

    public Integer getPages() {
        return pages;
    }

    public void setPages(Integer pages) {
        this.pages = pages;
    }

    public List<Offer> getOffers() {
        return offers;
    }

    public void setOffers(List<Offer> offers) {
        this.offers = offers;
    }

}