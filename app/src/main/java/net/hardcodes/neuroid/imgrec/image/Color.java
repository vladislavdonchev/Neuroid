/**
 * Copyright 2010 Neuroph Project http://neuroph.sourceforge.net
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.hardcodes.neuroid.imgrec.image;

/**
 * This class holds color information and allows getting its RGB components
 * @author dmicic
 */
public class Color {

    private int color;

    public Color(int color) {
        this.color = color;
    }

    public Color(int r, int g, int b) {
        color = r;
        color = (color << 8) + g;
        color = (color << 8) + b;
    }

    public int getColor() {
        return color;
    }

    public static int getRed(int color) {
        return (color >> 16) & 0xFF;
    }

    public static int getGreen(int color) {
        return (color >> 8) & 0xFF;
    }

    public static int getBlue(int color) {
        return (color >> 0) & 0xFF;
    }

    public int getAlpha() {
        return (color >> 24) & 0xFF;
    }

    public int getRed() {
        return (color >> 16) & 0xFF;
    }

    public int getGreen() {
        return (color >> 8) & 0xFF;
    }

    public int getBlue() {
        return (color >> 0) & 0xFF;
    }
}
