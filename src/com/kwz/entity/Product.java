package com.kwz.entity;

import org.apache.commons.lang.StringUtils;

import com.kwz.util.KwzUtil;

//@Entity
public class Product extends KwzBaseBean {

    private static final long serialVersionUID = 1L;
    private static final int BRIEF_SIZE = 50;
    private boolean popular;
    private boolean discount;
    private String imageLink;
    private String shortDescription;
    private String longDescription;
    public String getShortDescription() {
        return shortDescription;
    }

//    @Transient
    public String getShortDescriptionInHtml() {
        return KwzUtil.textToHtml(shortDescription);
    }

//    @Transient
    public String getPartialShortDescriptionInHtml() {
        String tmp = shortDescription;
        if (StringUtils.length(shortDescription)>BRIEF_SIZE)
            tmp = shortDescription.substring(1,BRIEF_SIZE)+"...";
        return KwzUtil.textToHtml(tmp);
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

//    @Transient
    public String getLongDescriptionInHtml() {
        return KwzUtil.textToHtml(longDescription);
    }

    public String getLongDescription() {
        return longDescription;
    }

    public void setLongDescription(String longDescription) {
        this.longDescription = longDescription;
    }

    public String getImageLink() {
        return imageLink;
    }

    public void setImageLink(String imageLink) {
        this.imageLink = imageLink;
    }

    public boolean isPopular() {
        return popular;
    }

    public void setPopular(boolean popular) {
        this.popular = popular;
    }

    public boolean isDiscount() {
        return discount;
    }

    public void setDiscount(boolean discount) {
        this.discount = discount;
    }

}
