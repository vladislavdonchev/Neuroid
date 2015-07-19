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
 * Histogram equalization filter serves to reduce the contrast of the grayscale 
 * image.For example, if the image before histogram equalization filter has too 
 * many dark pixels and a little light pixels,after this filter the difference 
 * will alleviate. If the plan is, after this filter, to use Otsu binarized filter, 
 * this filter will not influence on him.
 * 
 * @author Mihailo Stupar
 */
public class HistogramEqualizationFilter implements ImageFilter,Serializable {

    private transient ImageAndroid originalImage;
    private transient ImageAndroid filteredImage;
	
    @Override
    public ImageAndroid processImage(ImageAndroid image) {
		
	originalImage = image;
		
	int width = originalImage.getWidth();
	int height = originalImage.getHeight();
		
	filteredImage = new ImageAndroid(width, height, originalImage.getType());
		
	int [] histogram = imageHistogram(originalImage);
		
	int [] histogramCumulative = new int[histogram.length];
		
	histogramCumulative[0] = histogram[0];
	for (int i = 1; i < histogramCumulative.length; i++) {
            histogramCumulative[i] = histogramCumulative[i-1] + histogram[i];
	}
		
	int G = 256;
	int gray;
	int alpha;
		
	int newColor;
		
	for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
				
		gray = new Color(originalImage.getRGB(i, j)).getRed();
		alpha = new Color(originalImage.getRGB(i, j)).getAlpha();
				
		newColor = (G-1)*histogramCumulative[gray]/(width*height); //zaokruziti izbeci celobrojno deljenje

				
		newColor = ImageUtilities.colorToRGB(alpha, newColor, newColor, newColor);
		filteredImage.setRGB(i, j, newColor);
            }
	}
		
	return filteredImage;
    }
	
	
    public int[] imageHistogram(ImageAndroid image) {

	int[] histogram = new int[256];

	for (int i = 0; i < histogram.length; i++)
            histogram[i] = 0;

	for (int i = 0; i < image.getWidth(); i++) {
            for (int j = 0; j < image.getHeight(); j++) {
		int gray = new Color(image.getRGB(i, j)).getRed();
		histogram[gray]++;
            }
	}

	return histogram;
    }
    @Override
    public String toString() {
        return "Histogram Equalization Filter";
    }
}
