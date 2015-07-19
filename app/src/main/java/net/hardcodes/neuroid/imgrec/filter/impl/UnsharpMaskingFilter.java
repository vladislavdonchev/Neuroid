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
public class UnsharpMaskingFilter implements ImageFilter,Serializable{

    private transient ImageAndroid originalImage;
    private transient ImageAndroid filteredImage;
    
    
    @Override
    public ImageAndroid processImage(ImageAndroid image) {
        
        originalImage = image;
        
        
        
        
        ImageAndroid bluredImage = getBluredImage();
        
        ImageAndroid unsharpMask = getUnsharpMask(originalImage, bluredImage);
        
        filteredImage = getSharpImage(originalImage, unsharpMask);
  
        return filteredImage;
    }
    
    
    public ImageAndroid getBluredImage() {
        
        int width = originalImage.getWidth();
        int height = originalImage.getHeight();
        
        ImageAndroid bluredImage = new ImageAndroid(width, height, originalImage.getType());
        int alpha;
        int newColor;
        
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                newColor = getAverageBluring(i, j);
                alpha = new Color(originalImage.getRGB(i, j)).getAlpha();
                int rgb = ImageUtilities.colorToRGB(alpha, newColor, newColor, newColor);
                bluredImage.setRGB(i, j, rgb);
            }
        }
        
        return bluredImage;
    }
    
    public int getAverageBluring (int i, int j) {
        
        double sum = 0;
        int n = 0;
        
        for (int x = i-1; x <= i+1; x++) {
            for (int y = j-1; y <= j+1; y++) {
                if (x>=0 && x<originalImage.getWidth() && y>=0 && y<originalImage.getHeight()) {
                    int color = new Color(originalImage.getRGB(x, y)).getRed();
                    sum = sum+color;
                    n++;
                }
            }
        }
        
        int average = (int) Math.round(sum/n);
        return average;
    }
    
    public ImageAndroid getUnsharpMask (ImageAndroid originalImage, ImageAndroid bluredImage) {
        
        int width = originalImage.getWidth();
        int height = originalImage.getHeight();
        
        ImageAndroid unsharpMask =
                new ImageAndroid(width, height, originalImage.getType());
        
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                int originalColor = new Color(originalImage.getRGB(i, j)).getRed();
                int blurColor = new Color(bluredImage.getRGB(i, j)).getRed();
                int alpha = new Color(originalImage.getRGB(i, j)).getAlpha(); 
                int newColor = originalColor - blurColor;
                int rgb = ImageUtilities.colorToRGB(alpha, newColor, newColor, newColor);
                unsharpMask.setRGB(i, j, rgb);
            }
        } 
        return unsharpMask;
    }
    
    public ImageAndroid getSharpImage (ImageAndroid originalImage, ImageAndroid unsharpMask) {
        
        int width = originalImage.getWidth();
        int height = originalImage.getHeight();
        
        ImageAndroid sharpImage =
                new ImageAndroid(width, height, originalImage.getType());
        
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                int originalColor = new Color(originalImage.getRGB(i, j)).getRed();
                int unsharpColor = new Color(unsharpMask.getRGB(i, j)).getRed();
                int alpha = new Color(originalImage.getRGB(i, j)).getAlpha(); 
                int newColor = originalColor + unsharpColor;
                int rgb = ImageUtilities.colorToRGB(alpha, newColor, newColor, newColor);
                sharpImage.setRGB(i, j, rgb);
            }
        } 
        return sharpImage;
  
    } 

    @Override
    public String toString() {
        return "Unsharp Masking Filter";
    }
    
    
}
