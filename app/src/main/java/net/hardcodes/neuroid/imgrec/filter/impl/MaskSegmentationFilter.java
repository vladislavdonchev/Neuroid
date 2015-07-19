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
 * Mask segmentation filter gives good results if the input is a grayscale image 
 * which surface has a very dark background and a brighter surface that represents 
 * the shape that we want to segment. Mask filter segmentation uses the  Otsu 
 * binarize filter in a first step, because after Otsu filter an image which has 
 * a black background and white shape. After that we set this image as the mask 
 * of the original grayscale image and the places that were black on mask will 
 * become white (background) on filtered image, and the places which were white 
 * on mask will have the same values in filtered image and in grayscale. If after 
 * this filter we launch Otsu binarizemethod again, we will get better results. 
 * Filter shows good results as part of image processing for mammography.
 *  
 * @author Mihailo Stupar
 */
public class MaskSegmentationFilter implements ImageFilter,Serializable{

    private transient ImageAndroid originalImage;
    private transient ImageAndroid filteredImage;
	
    @Override
    public ImageAndroid processImage(ImageAndroid image) {
		
        originalImage =  image;
		
	int width = originalImage.getWidth();
	int height = originalImage.getHeight();
		
	filteredImage = new ImageAndroid(width, height, originalImage.getType());
		
	OtsuBinarizeFilter obf = new OtsuBinarizeFilter();
	ImageAndroid tempImage = obf.processImage(originalImage);
		
	int gray;
	int alpha;
	int discreteColor;
	int newColor;
	int white = 255;
			
	for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
				
		gray = new Color(originalImage.getRGB(i, j)).getRed();
		alpha = new Color(originalImage.getRGB(i, j)).getAlpha();
				
		discreteColor = new Color(tempImage.getRGB(i, j)).getRed();
                if (discreteColor == white) {
                    newColor = gray;
		}
		else {
                    newColor = white;
		}
		newColor = ImageUtilities.colorToRGB(alpha, newColor, newColor, newColor);
		filteredImage.setRGB(i, j, newColor);
	
            }
	}
		
	return filteredImage;
    }

    @Override
    public String toString() {
        return "Mask Segmentation Filter";
    }
    
}
