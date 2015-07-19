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
public class Dilation implements ImageFilter,Serializable{
  
    
    private transient ImageAndroid originalImage;
    private transient ImageAndroid filteredImage;

    private int width;
    private int height;
    
    private int [][] kernel;
    @Override
    public ImageAndroid processImage(ImageAndroid image) {
        
        originalImage = image;
        
        width = originalImage.getWidth();
        height = originalImage.getHeight();
        
        filteredImage = new ImageAndroid(width, height, originalImage.getType());
        
        kernel = createKernel();
        
        int white = 255;
        int black = 0;
        
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                int color = new Color(originalImage.getRGB(i, j)).getRed();
                if (color == black) {
                    convolve(i, j);
                }
                else {
                    int alpha = new Color(originalImage.getRGB(i, j)).getAlpha();
                    int rgb = ImageUtilities.colorToRGB(alpha, white, white, white);
                    filteredImage.setRGB(i, j, rgb);
                }         
            }
        }
        return filteredImage;
    }
    
    private int [][] createKernel () {
        int [][] kernel = { {0,1,1,1,0},
                            {1,1,1,1,1},
                            {1,1,1,1,1},
                            {1,1,1,1,1},
                            {0,1,1,1,0}
                          };
        return kernel;
    }
    
    private void convolve (int i, int j) {
        for (int x = i-2; x <= i+2; x++) {
            for (int y = j-2; y <= j+2; y++) {
                if (x>=0 && y>=0 && x<width && y<height) {
                    int black = 0;
                    int alpha = new Color(originalImage.getRGB(x, y)).getAlpha();
                    int rgb = ImageUtilities.colorToRGB(alpha, black, black, black);
                    filteredImage.setRGB(x, y, rgb);
                }
            }
        }
    }

    @Override
    public String toString() {
        return "Dilation";
    }
        
    
    
    
}
