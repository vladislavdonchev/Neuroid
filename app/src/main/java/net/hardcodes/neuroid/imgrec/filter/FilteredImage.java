/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.hardcodes.neuroid.imgrec.filter;

import net.hardcodes.neuroid.imgrec.image.ImageAndroid;

/**
 * Contains image and name of applied filter.
 *
 * @author Aleksandar
 */
public class FilteredImage {

    private ImageAndroid image;
    private String filterName;

    public FilteredImage(ImageAndroid image, String filterName) {
        this.image = image;
        this.filterName = filterName;
    }

    public ImageAndroid getImage() {
        return image;
    }

    public void setImage(ImageAndroid image) {
        this.image = image;
    }

    public String getFilterName() {
        return filterName;
    }

    public void setFilterName(String filterName) {
        this.filterName = filterName;
    }

}
