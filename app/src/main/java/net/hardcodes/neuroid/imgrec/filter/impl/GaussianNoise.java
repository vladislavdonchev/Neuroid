/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package net.hardcodes.neuroid.imgrec.filter.impl;


import net.hardcodes.neuroid.imgrec.ImageUtilities;
import net.hardcodes.neuroid.imgrec.filter.ImageFilter;
import net.hardcodes.neuroid.imgrec.image.Color;
import net.hardcodes.neuroid.imgrec.image.ImageAndroid;

import java.io.Serializable;

/**
 *
 * @author Mihailo Stupar
 */
public class GaussianNoise implements ImageFilter,Serializable{

    private transient ImageAndroid originalImage;
    private transient ImageAndroid filteredImage;
    
    private double mean;
    private double sigma;

    public GaussianNoise() {
        mean = 0;
        sigma = 30;
    }
    
    
    
    @Override
    public ImageAndroid processImage(ImageAndroid image) {
        
        
        double variance = sigma*sigma;
        
        originalImage = image;
        
        int width = originalImage.getWidth();
        int height = originalImage.getHeight();
        
        filteredImage = new ImageAndroid(width, height, originalImage.getType());
        
        double a = 0.0;
        double b = 0.0;
        
        
        
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                
                while (a == 0.0)
                    a = Math.random();
                b = Math.random();
                
                double x = Math.sqrt(-2*Math.log(a)) * Math.cos(2*Math.PI*b);
                double noise = mean + Math.sqrt(variance) * x;
                
                //
                //
                
                int gray = new Color(originalImage.getRGB(i, j)).getRed();
                int alpha = new Color(originalImage.getRGB(i, j)).getAlpha();
                
                double color = gray + noise;
                if (color > 255)
                    color = 255;
                if (color < 0)
                    color = 0;
                
                int newColor = (int) Math.round(color);
                
                int rgb = ImageUtilities.colorToRGB(alpha, newColor, newColor, newColor);
                
                filteredImage.setRGB(i, j, rgb);                
                
            }//j
        }//i
        
        
        return filteredImage;
    }

    public void setMean(double mean) {
        this.mean = mean;
    }

    public void setSigma(double sigma) {
        this.sigma = sigma;
    }

    @Override
    public String toString() {
        return "Gaussian noise";
    }

    
    
    
    
    
}
