/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package net.hardcodes.neuroid.imgrec.filter;

import net.hardcodes.neuroid.imgrec.image.ImageAndroid;

/**
 * Interface for image filter 
 * @author Sanja
 */
public interface ImageFilter {
    public ImageAndroid processImage(ImageAndroid image);
}
