# displib
a Java lib for making simple windows for simulations

# Usage

this is a java program that demonstrates the ease to use this library for graphics and simulations. It is inspired by Processing and p5.js. 

```java
public class Example extends Display{
	private static final long serialVersionUID = 1L;
	
	private int red, green, blue, increment;
	private double crSz, sizeX, sizeY, angle = 0.0, sign = 1.0, totalDt = 0.0;
	
	public static void main(String[] args){
		Example ex = new Example();//new displib extended instance
		ex.setPreferredSize(new Dimension(600, 400));//set size
		ex.start();//must use start command to launch window
	}
	
	@Override
	protected void setup(){
		//this is run once s.start() is called
		//put simulation setup code here
		
		setRandoms();
	}
	
	private void setRandoms() {
		red = (int)Maths.random(80, 255);
		green = (int)Maths.random(80, 255);
		blue = (int)Maths.random(80, 255);
		sizeX = Maths.random(30, 100);
		sizeY = Maths.random(30, 100);
		increment = (int)Maths.random(1.0, 3.0);
		crSz = Maths.random(4.0, 14.0);
		sign = Math.random()>0.5?-1.0:1.0;
	}
	
	@Override
	protected void update(double dt){
		//this is run every step before the render stage.
		//dt is the double value associated with the amount of time elapsed in seconds. (used well with physics simulations)
		
		//put simulation update code here
		
		//example
		if(totalDt%3.6<0.05) {
			setRandoms();
		}
		
		angle += sign*Maths.random(0.13, 4.4)*dt;
		totalDt += dt;
		
		setTitle("Example @ "+getFps()+"fps");
	}
	
	@Override
	protected void draw(Graphics2D graphics){
		//this is the renderer. there are many useful functions in graphics2d to show different types of graphics.
		
		//put render code here
		
		//example
		BufferedImage bfr = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics2D g2d = bfr.createGraphics();
		background(g2d, 0);
		int num = 8;
		//draw 2d grid of randomly coloured, spinning, and sized rectangles.
		for(int i=0;i<num;i++) {
			for(int j=0;j<num;j++) {
				double x = Maths.map(i, 0, num-1, 0, width);
				double y = Maths.map(j, 0, num-1, 0, height);
				//translate and rotate graphics
				translate(g2d, x, y);
				rotate(g2d, angle);
				
				stroke(g2d, red, green, blue);
				
				//fill or no fill?
				if((i+j)%increment==0) fillRect(g2d, 0, 0, sizeX, sizeY);
				else drawRect(g2d, 0, 0, sizeX, sizeY);
				
				//translate and rotate back
				rotate(g2d, -angle);
				translate(g2d, -x, -y);
			}
		}
		
		int tx = (int)(Math.cos(angle)*crSz);
		int ty = (int)(Math.sin(angle)*crSz);
		bfr = ImageFilters.chromaticAberration(bfr, tx, ty);//add chromatic aberration effect
		background(graphics, bfr);//display that image
	}
}
```
