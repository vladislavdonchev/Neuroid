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
public class MeanFilter implements ImageFilter,Serializable{

    private transient ImageAndroid originalImage;
    private transient ImageAndroid filteredImage;
    
    private int radius;

    public MeanFilter() {
        radius = 4;
    }
    
    
    
    @Override
    public ImageAndroid processImage(ImageAndroid image) {
    
        originalImage = image;
        
        int width = originalImage.getWidth();
        int height = originalImage.getHeight();
        
        filteredImage = new ImageAndroid(width, height, originalImage.getType());
        
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                
                int color = findMean(i, j);
                int alpha = new Color(originalImage.getRGB(i, j)).getAlpha();
                
                int rgb = ImageUtilities.colorToRGB(alpha, color, color, color);
                filteredImage.setRGB(i, j, rgb);
                
            }
        }
        
        
        return filteredImage;
        
        
    }
    
    public int findMean (int x, int y) {       
        double sum = 0;
        int n = 0;  
        for (int i = x-radius; i <= x+radius; i++) {
            for (int j = y-radius; j <= y+radius; j++) {              
                if (i>0 && i<originalImage.getWidth() && j>0 && j<originalImage.getHeight()) {                
                    int color = new Color(originalImage.getRGB(i, j)).getRed();
                    sum = sum + color;
                    n++;
                }   
            }
        }
        return (int) Math.round(sum/n);      
    }

    @Override
    public String toString() {
        return "Mean Filter";
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }
    
    
}
