/**
 * Copyright 2010 Neuroph Project http://neuroph.sourceforge.net
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package net.hardcodes.neuroid.imgrec;

import android.graphics.Bitmap;

import net.hardcodes.neuroid.imgrec.image.Image;
import net.hardcodes.neuroid.imgrec.image.ImageAndroid;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * Contains various utility methods used for OCR.
 *
 * @author Ivana Jovicic, Vladimir Kolarevic, Marko Ivanovic, Zoran Sevarac
 */
public class ImageUtilities {

    /**
     * This method cleans input image by replacing all non black pixels with
     * white pixels TODO: some should be used here
     *
     * @param image - input image that will be cleaned
     * @return - cleaned input image as ImageAndroid
     */
    public static ImageAndroid blackAndWhiteCleaning(ImageAndroid image) {
        for (int j = 0; j < image.getHeight(); j++) {
            for (int i = 0; i < image.getWidth(); i++) {
                if (image.getRGB(i, j) != -16777216) {
                    image.setRGB(i, j, -1);
                }
            }
        }
        return image;
    }

    /**
     * This method cleans input image by replacing all pixels with RGB values
     * from -4473925 (gray) to -1 (white) with white pixels and from -4473925
     * (gray) to -16777216 (black) with black pixels
     *
     * @param image - input image that will be cleaned
     * @return - cleaned input image as ImageAndroid
     */
    public static ImageAndroid blackAndGrayCleaning(ImageAndroid image) {
        for (int j = 0; j < image.getHeight(); j++) {
            for (int i = 0; i < image.getWidth(); i++) {
                if (image.getRGB(i, j) > -4473925) {
                    image.setRGB(i, j, -1);
                } else {
                    image.setRGB(i, j, -16777216);
                }
            }
        }
        return image;
    }

    /**
     * This method cleans input image by replacing all pixels with RGB values
     * from -3092272 (light gray) to -1 (white) with white pixels and from
     * -3092272 (light gray) to -16777216 (black) with black pixels
     *
     * @param image - input image that will be cleaned
     * @return - cleaned input image as ImageAndroid
     */
    public static ImageAndroid blackAndLightGrayCleaning(ImageAndroid image) {
        for (int j = 0; j < image.getHeight(); j++) {
            for (int i = 0; i < image.getWidth(); i++) {
                if (image.getRGB(i, j) > -4473925) {
                    image.setRGB(i, j, -1);
                } else {
                    image.setRGB(i, j, -16777216);
                }
            }
        }
        return image;
    }

    /**
     * This method cleans input image by replacing all pixels with RGB values
     * from RGBcolor input (the input color) to -1 (white) with white pixels and
     * from RGBcolor input (the input color) to -16777216 (black) with black
     * pixels
     *
     * @param image    - input image that will be cleaned
     * @param RGBcolor - input RGB value of wanted color as reference for
     *                 celaning
     * @return - cleaned input image as ImageAndroid
     */
    public static ImageAndroid colorCleaning(ImageAndroid image, int RGBcolor) {
        for (int j = 0; j < image.getHeight(); j++) {
            for (int i = 0; i < image.getWidth(); i++) {
                if (image.getRGB(i, j) == RGBcolor) {
                    image.setRGB(i, j, -16777216);
                } else {
                    image.setRGB(i, j, -1);
                }
            }
        }
        return image;
    }

    /**
     * This method loads the input Image and returns the cleaned version
     *
     * @param file - input file that will be loaded as image
     * @return - return cleaned loaded image as ImageAndroid
     */
    public static ImageAndroid loadAndCleanImage(File file) {
        ImageAndroid image = new ImageAndroid(file);
        return blackAndLightGrayCleaning(image);
    }

    /**
     * Loads image from the file.
     *
     * @param file image file
     * @return loaded image
     */
    public static ImageAndroid loadImage(File file) {
        return new ImageAndroid(file);
    }

    public static void save(ImageAndroid image, String filename) {
        try {
            FileOutputStream bitmapFile = new FileOutputStream(filename);
            image.getBitmap().compress(Bitmap.CompressFormat.PNG, 100, bitmapFile);
            bitmapFile.flush();
            bitmapFile.close();
        } catch (IOException ex) {
            throw new RuntimeException("IOException whle trying to save image file" + filename, ex);
        }
    }

    /**
     * This method reads the image pixels until it reads the first black pixel
     * by height and then returns that value
     *
     * @param img - input image that will be read
     * @return - returns the value of height when conditions are true
     */
    private static int trimLockup(ImageAndroid img) {
        for (int j = 0; j < img.getHeight(); j++) {
            for (int i = 0; i < img.getWidth(); i++) {
                if (img.getRGB(i, j) == -16777216) {
                    return j;
                }
            }
        }
        return 0;
    }

    /**
     * This method reads the input image from the input from start pixel height
     * (y1) until it reads the first next row where all pixel are white by
     * height and return that value
     *
     * @param img - input image that will be read
     * @param y1  - input start height pixel of image
     * @return - returns the value of height when conditions are true
     */
    private static int trimLockdown(ImageAndroid img, int y1) {
        for (int j = y1 + 1; j < img.getHeight(); j++) {
            int counterWhite = 0;
            for (int i = 0; i < img.getWidth(); i++) {
                if (img.getRGB(i, j) == -1) {
                    counterWhite++;
                }
            }
            if (counterWhite == img.getWidth()) {
                //this is a chek for dots over the letters i and j
                //so they wont be missread as dots
                if (j > (img.getHeight() / 2)) {
                    return j;
                }
            }
            if (j == img.getHeight() - 1) {
                return j + 1;
            }
        }
        return 0;
    }

    /**
     * This method trims the input image and returns it as a ImageAndroid
     *
     * @param imageToTrim input image that will be trimed
     * @return return trimed input image as ImageAndroid
     */
    public static ImageAndroid trimImage(ImageAndroid imageToTrim) {
        int y1 = trimLockup(imageToTrim);
        int y2 = trimLockdown(imageToTrim, y1);
        int x1 = 0; // why zero? search white pixels from left...
        int x2 = imageToTrim.getWidth();
        return cropImage(imageToTrim, x1, y1, x2, y2);
    }

    /**
     * Resize image to specified dimensions
     *
     * @param image  image to resize
     * @param width  new image width
     * @param height new image height
     * @return resized image
     */
    public static ImageAndroid resizeImage(ImageAndroid image, int width, int height) {
        ImageAndroid resizedImage = new ImageAndroid(image, width, height);
        return resizedImage;
    }

    public static Image resizeImage(Image image, int width, int height) {
        return image.resize(width, height);
    }

    /**
     * Crops (returns subimage) of specified input image at specified points.
     *
     * @param image image to crop
     * @param x1    top left x coordinate
     * @param y1    top left y coordinate
     * @param x2    bottom right x coordinate
     * @param y2    bottom right y coordinate
     * @return image croped at specified points
     */
    public static ImageAndroid cropImage(ImageAndroid image, int x1, int y1, int x2, int y2) {
        return image.getSubimage(x1, y1, x2 - x1, y2 - y1);
    }

    /**
     * Creates and returns image from the given text.
     *
     * @param text input text
     * @param font text font
     * @return image with input text
     */
//    public static ImageAndroid createImageFromText(String text, Font font) {
//        //You may want to change these setting, or make them parameters
//        boolean isAntiAliased = true;
//        boolean usesFractionalMetrics = false;
//        FontRenderContext frc = new FontRenderContext(null, isAntiAliased, usesFractionalMetrics);
//        TextLayout layout = new TextLayout(text, font, frc);
//        Rectangle2D bounds = layout.getBounds();
//        int w = (int) Math.ceil(bounds.getWidth());
//        int h = (int) Math.ceil(bounds.getHeight()) + 2;
//        ImageAndroid image = new ImageAndroid(w, h, ImageAndroid.TYPE_INT_RGB); //for example;
//        Graphics2D g = image.createGraphics();
//        g.setColor(Color.WHITE);
//        g.fillRect(0, 0, w, h);
//        g.setColor(Color.BLACK);
//        g.setFont(font);
//        Object antiAliased = isAntiAliased
//                ? RenderingHints.VALUE_TEXT_ANTIALIAS_ON : RenderingHints.VALUE_TEXT_ANTIALIAS_OFF;
//        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, antiAliased);
//        Object fractionalMetrics = usesFractionalMetrics
//                ? RenderingHints.VALUE_FRACTIONALMETRICS_ON : RenderingHints.VALUE_FRACTIONALMETRICS_OFF;
//        g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, fractionalMetrics);
//        g.drawString(text, (float) -bounds.getX(), (float) -bounds.getY());
//        g.dispose();
//
//        return image;
//    }
//    public static Bitmap createImageFromText(String text) {
//		TextView txtView = new TextView(null);
//		txtView.setBackgroundColor(Color.WHITE);
//		txtView.setTextColor(Color.BLACK);
//		txtView.setText(text);
//		txtView.setWidth(LayoutParams.WRAP_CONTENT);
//		txtView.setHeight(LayoutParams.WRAP_CONTENT);
//        Bitmap image = Bitmap.createBitmap(txtView.getWidth(), txtView.getHeight(), Bitmap.Config.ALPHA_8);
//        Canvas canvas = new Canvas(image);
//        txtView.draw(canvas);
//        
//        return image;
//    }

    /**
     * Returns RGB data for all input images
     *
     * @param imagesData data map with characters as keys and charcter images as
     *                   values
     * @return data map with characters as keys and image rgb data as values
     */
    public static Map<String, FractionRgbData> getFractionRgbDataForImages(HashMap<String, ImageAndroid> imagesData) {

        Map<String, FractionRgbData> rgbDataMap = new HashMap<String, FractionRgbData>();

        for (String imageName : imagesData.keySet()) {
            StringTokenizer st = new StringTokenizer(imageName, ".");
            ImageAndroid image = imagesData.get(imageName);
            rgbDataMap.put(st.nextToken(), new FractionRgbData(image));
        }

        return rgbDataMap;
    }

    public static int colorToRGB(int alpha, int red, int green, int blue) {

        int newPixel = 0;
        newPixel += alpha;
        newPixel = newPixel << 8;
        newPixel += red;
        newPixel = newPixel << 8;
        newPixel += green;
        newPixel = newPixel << 8;
        newPixel += blue;

        return newPixel;
    }

}
