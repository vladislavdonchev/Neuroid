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
 * Grayscale filter from image in RGB format makes grayscale image in way that
 * for each pixel, using value of red, green and blue color, calculates new
 * value using formula: gray = 0.21*red + 0.71*green + 0.07*blue Grayscale
 * filter is commonly used as first filter in Filter Chain and on that grayscale
 * image other filters are added.
 *
 * @author Mihailo Stupar
 */
public class GrayscaleFilter implements ImageFilter,Serializable {

    private transient ImageAndroid originalImage;
    private transient ImageAndroid filteredImage;

    @Override
    public ImageAndroid processImage(ImageAndroid image) {

        originalImage = image;

        int alpha;
        int red;
        int green;
        int blue;

        int gray;

        int width = originalImage.getWidth();
        int height = originalImage.getHeight();

        filteredImage = new ImageAndroid(width, height, originalImage.getType());

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {

                alpha = new Color(originalImage.getRGB(i, j)).getAlpha();
                red = new Color(originalImage.getRGB(i, j)).getRed();
                green = new Color(originalImage.getRGB(i, j)).getGreen();
                blue = new Color(originalImage.getRGB(i, j)).getBlue();

                gray = (int) (0.21 * red + 0.71 * green + 0.07 * blue);

                gray = ImageUtilities.colorToRGB(alpha, gray, gray, gray);

                filteredImage.setRGB(i, j, gray);

            }
        }

        return filteredImage;
    }

    @Override
    public String toString() {
        return "Grayscale Filter";
    }
    
    
}
