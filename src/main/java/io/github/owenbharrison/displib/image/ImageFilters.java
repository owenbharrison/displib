package io.github.owenbharrison.displib.image;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.util.Arrays;


import io.github.owenbharrison.displib.maths.Maths;

public class ImageFilters {
	public static BufferedImage invert(BufferedImage bfr) {
		int[] data = ImageFilters.imageToPixels(bfr);

		for(int i=0;i<data.length;i+=3) {
			data[i] = 255-data[i];
			data[i+1] = 255-data[i+1];
			data[i+2] = 255-data[i+2];
		}

		return ImageFilters.pixelsToImage(data, bfr.getWidth(), bfr.getHeight());
	}
	
	public static BufferedImage grayscale(BufferedImage bfr) {
		int[] data = ImageFilters.imageToPixels(bfr);

		for(int i=0;i<data.length;i+=3) {
			int v = (int)(data[i]*0.299+data[i+1]*0.587+data[i+2]*0.114);
			data[i] = v;
			data[i+1] = v;
			data[i+2] = v;
		}

		return ImageFilters.pixelsToImage(data, bfr.getWidth(), bfr.getHeight());
	}
	
	public static BufferedImage rainbow(BufferedImage bfr) {
		int[] data = ImageFilters.imageToPixels(bfr);
		
		int w = bfr.getWidth();
		int h = bfr.getHeight();
		for(int x=0;x<w;x++) {
			for(int y=0;y<h;y++) {
				int i = (x+y*w)*3;
				data[i] = (int)Maths.map((double)x, 0.0, (double)w, 0.0, 255.0);
				data[i+2] = (int)Maths.map((double)y, 0.0, (double)h, 0.0, 255.0);
			}
		}

		return ImageFilters.pixelsToImage(data, w, h);
	}
	
	public static BufferedImage contrast(BufferedImage bfr, int pn) {
		double st = 255.0/pn;
		int[] data = ImageFilters.imageToPixels(bfr);

		for(int i=0;i<data.length;i+=3) {
			data[i] = (int)Maths.snapTo(data[i], st);
			data[i+1] = (int)Maths.snapTo(data[i+1], st);
			data[i+2] = (int)Maths.snapTo(data[i+2], st);
		}

		return ImageFilters.pixelsToImage(data, bfr.getWidth(), bfr.getHeight());
	}
	
	public static BufferedImage tint(BufferedImage bfr, int r, int g, int b) {
		int[] data = ImageFilters.imageToPixels(bfr);

		for(int i=0;i<data.length;i+=3) {
			data[i] = (int)Maths.map(data[i], 0, 255, 0, r);
			data[i+1] = (int)Maths.map(data[i+1], 0, 255, 0, g);
			data[i+2] = (int)Maths.map(data[i+2], 0, 255, 0, b);
		}

		return ImageFilters.pixelsToImage(data, bfr.getWidth(), bfr.getHeight());
	}
	
	public static BufferedImage bulge(BufferedImage bfr, int bx, int by, double bs, double br){
		int w = bfr.getWidth();
		int h = bfr.getHeight();	
		
		int data[] = ImageFilters.imageToPixels(bfr);
		int newData[] = new int[w*h*3];
		Arrays.fill(newData, 0);
		for(int x=0;x<w;x++){
			for(int y=0;y<h;y++){
				int dx = x-bx;
				int dy = y-by;
				double dsq = dx*dx+dy*dy;
				int sx = x;
				int sy = y;
				if(dsq<br*br){
					double dst = Math.sqrt(dsq);
					double rx = dx/dst;
					double ry = dy/dst;
					double df = dst*Math.pow(1.0-dst/br,1.0/bs);
					sx -= df*rx;
					sy -= df*ry;
				}
				if(sx>=0&&sx<w&&sy>=0&&sy<h){
					int si = (sx+sy*w)*3;
					int i = (x+y*w)*3;
					newData[i] = data[si];
					newData[i+1] = data[si+1];
					newData[i+2] = data[si+2];
				}
			}
		}
		
		return ImageFilters.pixelsToImage(newData, w, h);
	}
	
	public static BufferedImage dither(BufferedImage bfr) {
		int[] data = ImageFilters.imageToPixels(bfr);
		
		double ss = 0.4375;
		double ts = 0.1875;
		double fs = 0.3125;
		double os = 0.0625;
		
		int w = bfr.getWidth();
		int h = bfr.getHeight();
		for(int y=0;y<h-1;y++) {
			for(int x=1;x<w-1;x++) {
				int i = (x+y*w)*3;
				double or = data[i];
				double og = data[i+1];
				double ob = data[i+2];
				double nr = Math.round(or/255.0)*255.0;
				double ng = Math.round(og/255.0)*255.0;
				double nb = Math.round(ob/255.0)*255.0;
				data[i] = (int)nr;
				data[i+1] = (int)ng;
				data[i+2] = (int)nb;
				
				double er = or-nr;
				double eg = og-ng;
				double eb = ob-nb;
				
				int ir = (x+1+y*w)*3;
				data[ir] += er*ss;
				data[ir+1] += eg*ss;
				data[ir+2] += eb*ss;
				
				int ibl = (x-1+(y+1)*w)*3;
				data[ibl] += er*ts;
				data[ibl+1] += eg*ts;
				data[ibl+2] += eb*ts;
				
				int ib = (x+(y+1)*w)*3;
				data[ib] += er*fs;
				data[ib+1] += eg*fs;
				data[ib+2] += eb*fs;
				
				int ibr = (x+1+(y+1)*w)*3;
				data[ibr] += er*os;
				data[ibr+1] += eg*os;
				data[ibr+2] += eb*os;
			}
		}

		return ImageFilters.pixelsToImage(data, w, h);
	}
	
	public static BufferedImage glitch(BufferedImage bfr, int v) {
		int[] data = ImageFilters.imageToPixels(bfr);
		int w = bfr.getWidth();
		int h = bfr.getHeight();
		
		int[] newData = new int[data.length];
		Arrays.fill(newData, 0);
		
		for(int x=v;x<w-v;x++) {
			for(int y=0;y<h;y+=2) {
				int i = (x+y*w)*3;
				newData[(x-v+y*w)*3] += data[i];
				newData[i+1] += data[i+1];
				newData[(x+v+y*w)*3+2] += data[i+2];
			}
		}
		
		return ImageFilters.pixelsToImage(newData, bfr.getWidth(), bfr.getHeight());
	}
	
	public static BufferedImage shiftX(BufferedImage bfr, int amt, int step) {
		int[] data = ImageFilters.imageToPixels(bfr);
		int w = bfr.getWidth();
		int h = bfr.getHeight();
		int[] newData = data.clone();
		
		for(int y=0;y<h;y++) {
			boolean shift = (y%(step*2)<step);
			for(int x=0;x<w;x++) {
				int t = (x+y*w)*3;
				if(shift) {
					int n = (((x+amt)%w)+y*w)*3;
					newData[t] = data[n];
					newData[t+1] = data[n+1];
					newData[t+2] = data[n+2];
				}
			}
		}
		
		return ImageFilters.pixelsToImage(newData, w, h);
	}
	
	public static BufferedImage shiftY(BufferedImage bfr, int amt, int step) {
		int[] data = ImageFilters.imageToPixels(bfr);
		int w = bfr.getWidth();
		int h = bfr.getHeight();
		int[] newData = data.clone();
		
		for(int x=0;x<w;x++) {
			boolean shift = (x%(step*2)<step);
			for(int y=0;y<h;y++) {
				int t = (x+y*w)*3;
				if(shift) {
					int n = (x+((y+amt)%h)*w)*3;
					newData[t] = data[n];
					newData[t+1] = data[n+1];
					newData[t+2] = data[n+2];
				}
			}
		}
		
		return ImageFilters.pixelsToImage(newData, w, h);
	}
	
	public static BufferedImage chromaticAberration(BufferedImage bfr, int xv, int yv) {
		int[] data = ImageFilters.imageToPixels(bfr);
		int w = bfr.getWidth();
		int h = bfr.getHeight();
		
		int[] newData = new int[w*h*3];
		
		for(int x=0;x<w;x++) {
			for(int y=0;y<h;y++) {
				int i = (x+y*w)*3;
				int rxa = x+xv;
				int rya = y+yv;
				int rxs = x-xv;
				int rys = y-yv;
				if(rxa>=0&&rxa<w&&rxs>=0&&rxs<w&&rya>=0&&rya<h&&rys>=0&&rys<h) {
					newData[(rxs+rya*w)*3] += data[i];
					newData[i+1] += data[i+1];
					newData[(rxa+rys*w)*3+2] += data[i+2];
				}
				else {
					newData[i] += data[i];
					newData[i+1] += data[i+1];
					newData[i+2] += data[i+2];
				}
			}
		}
		
		return ImageFilters.pixelsToImage(newData, bfr.getWidth(), bfr.getHeight());
	}
	
	public static final BufferedImage resize(BufferedImage bfr, int w, int h) {
		BufferedImage resizedImage = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
		
		Graphics2D g = resizedImage.createGraphics();
		g.setComposite(AlphaComposite.Src);
		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.drawImage(bfr.getScaledInstance(w, h, Image.SCALE_DEFAULT), 0, 0, w, h, null);
		g.dispose();
		
		return resizedImage;
	}
	
	public static final int[] imageToPixels(BufferedImage bfr) {
		int w = bfr.getWidth();
		int h = bfr.getHeight();
		WritableRaster wr = bfr.getRaster();
		return wr.getPixels(0, 0, w, h, new int[w*h*3]);
	}
	
	public static final BufferedImage pixelsToImage(int[] data, int w, int h) {
		BufferedImage bfr = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
		WritableRaster wr = bfr.getRaster();
		wr.setPixels(0, 0, w, h, data);
		ColorModel cm = bfr.getColorModel();
		return new BufferedImage(bfr.getColorModel(), wr, cm.isAlphaPremultiplied(), null);
	}
}
