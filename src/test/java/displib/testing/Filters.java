package displib.testing;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JFrame;

import io.github.owenbharrison.displib.Display;
import io.github.owenbharrison.displib.image.ImageFilters;
import io.github.owenbharrison.displib.maths.Maths;

public class Filters extends Display{
	private static final long serialVersionUID = -3473880969372424792L;
		
	private BufferedImage baseImage;
	
	public double delta = 0.0;
	
	private int fNum = 0;
	
	public static void main(String[] args) {
		Filters f = new Filters();
		f.setPreferredSize(new Dimension(800, 600));
		f.start();
	}

	@Override
	protected void setup() {
		try {
			String fileName = "res/"+(int)Math.round(Math.random()*7.0+1.0)+".jpg";
			baseImage = ImageIO.read(Filters.class.getResourceAsStream(fileName));
			baseImage = ImageFilters.resize(baseImage, width, height);
		} catch (IOException e) {
			e.printStackTrace();
		}
//		JFileChooser jfc = new JFileChooser();
//		int rv = jfc.showOpenDialog(new JFrame());
//		if(rv==JFileChooser.APPROVE_OPTION) {
//			File f = jfc.getSelectedFile();
//			try {
//				baseImage = ImageIO.read(f);
//				baseImage = ImageFilters.resize(baseImage, width, height);
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//		}
	}

	@Override
	protected void update(double dt) {
		setTitle("test @ "+getFps()+"fps");
	}

	@Override
	protected void draw(Graphics2D g) {
		renderQuality(g);
		antialiasOn(g);
		
		BufferedImage fImage = baseImage;
		String fName = "none";
		switch(fNum) {
			case 1:
				fName = "grayscale";
				fImage = ImageFilters.grayscale(baseImage);
				break;
			case 2:
				fName = "inverted";
				fImage = ImageFilters.invert(baseImage);
				break;
			case 3:
				fName = "contrast less palete";
				fImage = ImageFilters.contrast(baseImage, (int)Maths.map(Math.sin(delta), -1.0, 1.0, 1.0, 20.0));
				break;
			case 4:
				fName = "bulge at mousepos";
				fImage = ImageFilters.bulge(baseImage, mouseX, mouseY, 0.7, 45.0);
				break;
			case 5:
				fName = "rainbow";
				fImage = ImageFilters.rainbow(baseImage);
				break;
			case 6:
				fName = "chromatic aberration";
				fImage = ImageFilters.chromaticAberration(baseImage, (int)(Math.cos(delta)*3.0), (int)(Math.sin(delta)*3.0));
				break;
			case 7:
				fName = "tint";
				int red = (int)Maths.map(Math.sin(delta), -1.0, 1.0, 0, 255);
				int green = (int)Maths.map(Math.sin(delta)*Math.cos(delta), -1.0, 1.0, 0, 255);
				int blue = (int)Maths.map(Math.cos(delta), -1.0, 1.0, 0, 255);
				fImage = ImageFilters.tint(baseImage, red, green, blue);
				break;
			case 8:
				fName = "dither";
				fImage = ImageFilters.dither(baseImage);
				break;
			case 9:
				fName = "glitch";
				fImage = ImageFilters.glitch(baseImage, (int)Maths.map(Math.cos(delta), -1.0, 1.0, 0.0, 5.0));
		}		
		background(g, fImage);
		
		stroke(g, 255);
		fillRect(g, 70, 16, 140, 32);
		stroke(g, 0, 120, 255);
		text(g, "Filter:", 1, 14);
		text(g, fName, 1, 30);
		delta += Math.PI/72.0;
	}
	
	@Override
	protected void keyDown(KeyEvent e) {
		int kc = e.getKeyCode();
		fNum+=kc==KeyEvent.VK_UP?1:kc==KeyEvent.VK_DOWN?-1:0;
		fNum = (int)Maths.clamp(fNum, 0, 9);
	}
}
