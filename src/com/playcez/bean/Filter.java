/*
 * 
 */
package com.playcez.bean;

import java.util.Random;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

/**
 * The Class Filter.
 */
public class Filter{

	/** The Constant FROM_COLOR. */
	private static final int[] FROM_COLOR = new int[]{49, 179, 110};
	
	/** The Constant THRESHOLD. */
	private static final int THRESHOLD = 3;

	/**
	 * Effects.
	 *
	 * @param src the src
	 * @param position the position
	 * @return the drawable
	 */
	public Drawable effects(Bitmap src, int position){
		
		Bitmap dst;
		Drawable d;
	   	switch(position){
    	
		case 0:
			d = new BitmapDrawable(src);
			break;
		case 1:
			d = new BitmapDrawable(doGamma(src, 1.8, 1.8, 1.8));
			break;
		case 2:
			d = new BitmapDrawable(createContrast(src, 1));
			break;
		case 3:
			d = new BitmapDrawable(doColorFilter(src,0.5,0.5,0.5));
			break;
		case 4:
			d = new BitmapDrawable(applySaturationFilter(src, 1));
			break;
		case 5:
			d = new BitmapDrawable(doColorFilter(src,1,0,0));
			break;
		case 6:
			d = new BitmapDrawable(doColorFilter(src,0,1,0));
			break;
		case 7:
			d = new BitmapDrawable(doColorFilter(src,1,1,0));
			break;
		case 8:
			d = new BitmapDrawable(doGreyscale(src));
			break;
		case 9:
			d = new BitmapDrawable(doBrightness(src,70));
			break;
		case 10:
			d = new BitmapDrawable(applySnowEffect(src));
			break;
		case 11:
			d = new BitmapDrawable(rotate(src, 45));
			break;
		case 12:
			d = new BitmapDrawable(rotate(src, -45));
			break;
		default:
			d = new BitmapDrawable(src);
			break;
	}
		return d;
	}
	
	/**
	 * Rotate.
	 *
	 * @param src the src
	 * @param degree the degree
	 * @return the bitmap
	 */
	public static Bitmap rotate(Bitmap src, float degree) {
			    // create new matrix
			    Matrix matrix = new Matrix();
			    // setup rotation degree
			    matrix.postRotate(degree);
			 
			    // return new bitmap rotated using matrix
			    return Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, true);
	}
	
	/**
	 * Do gamma.
	 *
	 * @param src the src
	 * @param red the red
	 * @param green the green
	 * @param blue the blue
	 * @return the bitmap
	 */
	public static Bitmap doGamma(Bitmap src, double red, double green, double blue) {
		// create output image
		Bitmap bmOut = Bitmap.createBitmap(src.getWidth(), src.getHeight(), src.getConfig());
		// get image size
		int width = src.getWidth();
		int height = src.getHeight();
		// color information
		int A, R, G, B;
		int pixel;
		// constant value curve
		final int    MAX_SIZE = 256;
		final double MAX_VALUE_DBL = 255.0;
		final int    MAX_VALUE_INT = 255;
		final double REVERSE = 1.0;

		// gamma arrays
		int[] gammaR = new int[MAX_SIZE];
		int[] gammaG = new int[MAX_SIZE];
		int[] gammaB = new int[MAX_SIZE];

		// setting values for every gamma channels
		for(int i = 0; i < MAX_SIZE; ++i) {
			gammaR[i] = (int)Math.min(MAX_VALUE_INT,
					(int)((MAX_VALUE_DBL * Math.pow(i / MAX_VALUE_DBL, REVERSE / red)) + 0.5));
			gammaG[i] = (int)Math.min(MAX_VALUE_INT,
					(int)((MAX_VALUE_DBL * Math.pow(i / MAX_VALUE_DBL, REVERSE / green)) + 0.5));
			gammaB[i] = (int)Math.min(MAX_VALUE_INT,
					(int)((MAX_VALUE_DBL * Math.pow(i / MAX_VALUE_DBL, REVERSE / blue)) + 0.5));
		}

		// apply gamma table
		for(int x = 0; x < width; ++x) {
			for(int y = 0; y < height; ++y) {
				// get pixel color
				pixel = src.getPixel(x, y);
				A = Color.alpha(pixel);
				// look up gamma
				R = gammaR[Color.red(pixel)];
				G = gammaG[Color.green(pixel)];
				B = gammaB[Color.blue(pixel)];
				// set new color to output bitmap
				bmOut.setPixel(x, y, Color.argb(A, R, G, B));
			}
		}

		// return final image
		return bmOut;
	}


	/**
	 * Creates the contrast.
	 *
	 * @param src the src
	 * @param value the value
	 * @return the bitmap
	 */
	public static Bitmap createContrast(Bitmap src, double value) {
		// image size
		int width = src.getWidth();
		int height = src.getHeight();
		// create output bitmap
		Bitmap bmOut = Bitmap.createBitmap(width, height, src.getConfig());
		// color information
		int A, R, G, B;
		int pixel;
		// get contrast value
		double contrast = Math.pow((100 + value) / 100, 2);

		// scan through all pixels
		for(int x = 0; x < width; ++x) {
			for(int y = 0; y < height; ++y) {
				// get pixel color
				pixel = src.getPixel(x, y);
				A = Color.alpha(pixel);
				// apply filter contrast for every channel R, G, B
				R = Color.red(pixel);
				R = (int)(((((R / 255.0) - 0.5) * contrast) + 0.5) * 255.0);
				if(R < 0) { R = 0; }
				else if(R > 255) { R = 255; }

				G = Color.red(pixel);
				G = (int)(((((G / 255.0) - 0.5) * contrast) + 0.5) * 255.0);
				if(G < 0) { G = 0; }
				else if(G > 255) { G = 255; }

				B = Color.red(pixel);
				B = (int)(((((B / 255.0) - 0.5) * contrast) + 0.5) * 255.0);
				if(B < 0) { B = 0; }
				else if(B > 255) { B = 255; }

				// set new pixel color to output bitmap
				bmOut.setPixel(x, y, Color.argb(A, R, G, B));
			}
		}

		// return final image
		return bmOut;
	}

	/**
	 * Do brightness.
	 *
	 * @param src the src
	 * @param value the value
	 * @return the bitmap
	 */
	public static Bitmap doBrightness(Bitmap src, int value) {
		// image size
		int width = src.getWidth();
		int height = src.getHeight();
		// create output bitmap
		Bitmap bmOut = Bitmap.createBitmap(width, height, src.getConfig());
		// color information
		int A, R, G, B;
		int pixel;

		// scan through all pixels
		for(int x = 0; x < width; ++x) {
			for(int y = 0; y < height; ++y) {
				// get pixel color
				pixel = src.getPixel(x, y);
				A = Color.alpha(pixel);
				R = Color.red(pixel);
				G = Color.green(pixel);
				B = Color.blue(pixel);

				// increase/decrease each channel
				R += value;
				if(R > 255) { R = 255; }
				else if(R < 0) { R = 0; }

				G += value;
				if(G > 255) { G = 255; }
				else if(G < 0) { G = 0; }

				B += value;
				if(B > 255) { B = 255; }
				else if(B < 0) { B = 0; }

				// apply new pixel color to output bitmap
				bmOut.setPixel(x, y, Color.argb(A, R, G, B));
			}
		}

		// return final image
		return bmOut;
	}

	/**
	 * Apply snow effect.
	 *
	 * @param source the source
	 * @return the bitmap
	 */
	public static Bitmap applySnowEffect(Bitmap source) {
		
		int COLOR_MAX = 200;
		
		// get image size
		int width = source.getWidth();
		int height = source.getHeight();
		int[] pixels = new int[width * height];
		// get pixel array from source
		source.getPixels(pixels, 0, width, 0, 0, width, height);
		// random object
		Random random = new Random();
		
		int R, G, B, index = 0, thresHold = 50;
		// iteration through pixels
		for(int y = 0; y < height; ++y) {
			for(int x = 0; x < width; ++x) {
				// get current index in 2D-matrix
				index = y * width + x;				
				// get color
				R = Color.red(pixels[index]);
				G = Color.green(pixels[index]);
				B = Color.blue(pixels[index]);
				// generate threshold
				thresHold = random.nextInt(COLOR_MAX);
				if(R > thresHold && G > thresHold && B > thresHold) {
					pixels[index] = Color.rgb(COLOR_MAX, COLOR_MAX, COLOR_MAX);
				}							
			}
		}
		// output bitmap				
		Bitmap bmOut = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
		bmOut.setPixels(pixels, 0, width, 0, 0, width, height);
		return bmOut;
	}

	/**
	 * Apply shading filter.
	 *
	 * @param source the source
	 * @param shadingColor the shading color
	 * @return the bitmap
	 */
	public static Bitmap applyShadingFilter(Bitmap source, int shadingColor) {
		// get image size
		int width = source.getWidth();
		int height = source.getHeight();
		int[] pixels = new int[width * height];
		// get pixel array from source
		source.getPixels(pixels, 0, width, 0, 0, width, height);

		int index = 0;
		// iteration through pixels
		for(int y = 0; y < height; ++y) {
			for(int x = 0; x < width; ++x) {
				// get current index in 2D-matrix
				index = y * width + x;
				// AND
				pixels[index] &= shadingColor;
			}
		}
		// output bitmap
		Bitmap bmOut = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		bmOut.setPixels(pixels, 0, width, 0, 0, width, height);
		return bmOut;
	}

	/**
	 * Apply saturation filter.
	 *
	 * @param source the source
	 * @param level the level
	 * @return the bitmap
	 */
	public static Bitmap applySaturationFilter(Bitmap source, int level) {
		// get image size
		int width = source.getWidth();
		int height = source.getHeight();
		int[] pixels = new int[width * height];
		float[] HSV = new float[3];
		// get pixel array from source
		source.getPixels(pixels, 0, width, 0, 0, width, height);

		int index = 0;
		// iteration through pixels
		for(int y = 0; y < height; ++y) {
			for(int x = 0; x < width; ++x) {
				// get current index in 2D-matrix
				index = y * width + x;
				// convert to HSV
				Color.colorToHSV(pixels[index], HSV);
				// increase Saturation level
				HSV[1] *= level;
				HSV[1] = (float) Math.max(0.0, Math.min(HSV[1], 1.0));
				// take color back
				pixels[index] |= Color.HSVToColor(HSV);
			}
		}
		// output bitmap
		Bitmap bmOut = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		bmOut.setPixels(pixels, 0, width, 0, 0, width, height);
		return bmOut;
	}

	/**
	 * Apply hue filter.
	 *
	 * @param source the source
	 * @param level the level
	 * @return the bitmap
	 */
	public static Bitmap applyHueFilter(Bitmap source, int level) {
		// get image size
		int width = source.getWidth();
		int height = source.getHeight();
		int[] pixels = new int[width * height];
		float[] HSV = new float[3];
		// get pixel array from source
		source.getPixels(pixels, 0, width, 0, 0, width, height);
		
		int index = 0;
		// iteration through pixels
		for(int y = 0; y < height; ++y) {
			for(int x = 0; x < width; ++x) {
				// get current index in 2D-matrix
				index = y * width + x;				
				// convert to HSV
				Color.colorToHSV(pixels[index], HSV);
				// increase Saturation level
				HSV[0] *= level;
				HSV[0] = (float) Math.max(0.0, Math.min(HSV[0], 360.0));
				// take color back
				pixels[index] |= Color.HSVToColor(HSV);
			}
		}
		// output bitmap				
		Bitmap bmOut = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		bmOut.setPixels(pixels, 0, width, 0, 0, width, height);
		return bmOut;		
	}
	
	/**
	 * Do greyscale.
	 *
	 * @param src the src
	 * @return the bitmap
	 */
	public static Bitmap doGreyscale(Bitmap src) {
		// constant factors
		final double GS_RED = 0.299;
		final double GS_GREEN = 0.587;
		final double GS_BLUE = 0.114;

		// create output bitmap
		Bitmap bmOut = Bitmap.createBitmap(src.getWidth(), src.getHeight(), src.getConfig());
		// pixel information
		int A, R, G, B;
		int pixel;

		// get image size
		int width = src.getWidth();
		int height = src.getHeight();

		// scan through every single pixel
		for(int x = 0; x < width; ++x) {
			for(int y = 0; y < height; ++y) {
				// get one pixel color
				pixel = src.getPixel(x, y);
				// retrieve color of all channels
				A = Color.alpha(pixel);
				R = Color.red(pixel);
				G = Color.green(pixel);
				B = Color.blue(pixel);
				// take conversion up to one single value
				R = G = B = (int)(GS_RED * R + GS_GREEN * G + GS_BLUE * B);
				// set new pixel color to output bitmap
				bmOut.setPixel(x, y, Color.argb(A, R, G, B));
			}
		}

		// return final image
		return bmOut;
	}

	/**
	 * Do color filter.
	 *
	 * @param src the src
	 * @param red the red
	 * @param green the green
	 * @param blue the blue
	 * @return the bitmap
	 */
	public static Bitmap doColorFilter(Bitmap src, double red, double green, double blue) {
		// image size
		int width = src.getWidth();
		int height = src.getHeight();
		// create output bitmap
		Bitmap bmOut = Bitmap.createBitmap(width, height, src.getConfig());
		// color information
		int A, R, G, B;
		int pixel;

		// scan through all pixels
		for(int x = 0; x < width; ++x) {
			for(int y = 0; y < height; ++y) {
				// get pixel color
				pixel = src.getPixel(x, y);
				// apply filtering on each channel R, G, B
				A = Color.alpha(pixel);
				R = (int)(Color.red(pixel) * red);
				G = (int)(Color.green(pixel) * green);
				B = (int)(Color.blue(pixel) * blue);
				// set new color pixel to output bitmap
				bmOut.setPixel(x, y, Color.argb(A, R, G, B));
			}
		}

		// return final image
		return bmOut;
	}


	/**
	 * Adjust.
	 *
	 * @param d the d
	 * @return the drawable
	 */
	private Drawable adjust(Drawable d)
	{
	    int to = Color.RED + Color.BLUE + Color.GREEN;

	    //Need to copy to ensure that the bitmap is mutable.
	    Bitmap src = ((BitmapDrawable) d).getBitmap();
	    Bitmap bitmap = src.copy(Bitmap.Config.ARGB_8888, true);
	    for(int x = 0;x < bitmap.getWidth();x++)
	        for(int y = 0;y < bitmap.getHeight();y++)
	            if(match(bitmap.getPixel(x, y))) 
	                bitmap.setPixel(x, y, to);

	    return new BitmapDrawable(bitmap);
	}

	/**
	 * Match.
	 *
	 * @param pixel the pixel
	 * @return true, if successful
	 */
	private boolean match(int pixel)
	{
	    //There may be a better way to match, but I wanted to do a comparison ignoring
	    //transparency, so I couldn't just do a direct integer compare.
	    return Math.abs(Color.red(pixel) - FROM_COLOR[0]) < THRESHOLD;//Math.abs(Color.green(pixel) - FROM_COLOR[1]) < THRESHOLD &&
        //Math.abs(Color.blue(pixel) - FROM_COLOR[2]) < THRESHOLD;
	        
	}

}
