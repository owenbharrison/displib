package io.github.owenbharrison.displib;

import javax.swing.JFrame;

import java.awt.BasicStroke;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class Display extends Canvas implements Runnable, MouseListener, MouseMotionListener, MouseWheelListener, KeyListener{		
	private static final long serialVersionUID = 1L;
	
	
	//frame info
	private Thread thread;
	private JFrame frame;
	private String title = "Display";
	private boolean running = false;	
	
	
	//frame stats
	private double framesPerSecond = 0;
	private List<Double> lastDeltas = new ArrayList<>();
	protected int updateCount = 0;
	protected int drawCount = 0;
	private long currUpdateTime, prevUpdateTime;
	
	
	//input properties
	private int _mouseX_ = 0;
	private int _mouseY_ = 0;
	protected int mouseX = 0;
	protected int mouseY = 0;
	protected int prevMouseX = 0;
	protected int prevMouseY = 0;
	protected int mouseScroll = 0;
	protected boolean leftMouseButton = false;
	protected boolean rightMouseButton = false;
	protected boolean middleMouseButton = false;
	protected final boolean[] KEYS = new boolean[255];
	
	
	//size properties
	protected int width, height;
	protected int screenWidth, screenHeight;
	
	protected Display() {
		this.frame = new JFrame();
	}
	
	
	//getters and setters
	protected final String getTitle() {
		return this.title;
	}
	
	protected final void setTitle(String s) {
		this.title = s;
		this.frame.setTitle(this.title);
	}
	
	protected final int getFps() {
		return (int)this.framesPerSecond;
	}
	
	
	//run this to start window
	protected final synchronized void start() {
		this.frame.setTitle(this.title);
		this.frame.add(this);
		this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.frame.pack();
		this.frame.setLocationRelativeTo(null);
		this.frame.setResizable(false);
		this.frame.setVisible(true);
		
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
		this.addMouseWheelListener(this);
		
		this.addKeyListener(this);
		Arrays.fill(this.KEYS, false);
		
		this.running = true;
		this.thread = new Thread(this);
		this.thread.start();
		
		this.width = this.getWidth();
		this.height = this.getHeight();
		
		Dimension ss = Toolkit.getDefaultToolkit().getScreenSize();
		this.screenWidth = (int)ss.getWidth();
		this.screenHeight = (int)ss.getHeight();
	}
	
	protected final synchronized void stop() {
		this.running = false;
		try {
			this.thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public final void run() {
		this.setup();
		
		while(this.running) {
			this._update_();
			this._draw_();
		}
		
		this.stop();
	}
	
	private final void _update_() {
		this.currUpdateTime = System.currentTimeMillis();
		
		this.mouseX = this._mouseX_;
		this.mouseY = this._mouseY_;
		
		double deltaTime = (double)(this.currUpdateTime-this.prevUpdateTime)/1000.0;
		
		this.lastDeltas.add(1.0/deltaTime);
		if(this.lastDeltas.size()>5) {
			this.lastDeltas.remove(0);
		}
		this.framesPerSecond = 0.0;
		for(Double d:this.lastDeltas) {
			this.framesPerSecond += d;
		}
		this.framesPerSecond /= this.lastDeltas.size();
		
		this.update(deltaTime);
		
		this.prevMouseX = this.mouseX;
		this.prevMouseY = this.mouseY;
		this.mouseScroll = 0;
		
		this.updateCount++;
		this.prevUpdateTime = this.currUpdateTime;
	}
	
	private final void _draw_() {
		BufferStrategy bs = getBufferStrategy();
		if(bs==null) {
			createBufferStrategy(3);
			return;
		}
		
		Graphics2D g = (Graphics2D) bs.getDrawGraphics();
		this.draw(g);
		
		g.dispose();
		bs.show();
		
		this.drawCount++;
	}
	
	
	//display main use methods
	protected abstract void setup();
	
	protected abstract void update(double dt);
	
	protected abstract void draw(Graphics2D g);
	
	
	//display ease of use methods
	protected void showCursor() {
		this.frame.getContentPane().setCursor(Cursor.getDefaultCursor());
	}
	
	protected final void hideCursor() {
		this.frame.getContentPane().setCursor(Toolkit.getDefaultToolkit().createCustomCursor(new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB), new Point(0, 0), ""));
	}
	
	
	//drawing type ease of use methods
	protected final void stroke(Graphics2D g, int v) {
		this.stroke(g, new Color(v, v, v));
	}
	
	protected final void stroke(Graphics2D g2d, int r, int g, int b) {
		this.stroke(g2d, new Color(r, g, b));
	}
	
	protected final void stroke(Graphics2D g, Color c) {
		g.setColor(c);
	}
	
	protected final void strokeSize(Graphics2D g, int v) {
		g.setStroke(new BasicStroke(v));
	}
	
	protected final void antialiasOn(Graphics2D g) {
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
	}
	
	protected final void antialiasOff(Graphics2D g) {
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
	}
	
	protected final void renderQuality(Graphics2D g) {
		g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
	}
	
	protected final void renderDefault(Graphics2D g) {
		g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_DEFAULT);
	}
	
	protected final void renderSpeed(Graphics2D g) {
		g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
	}
	
	protected final void rotate(Graphics2D g, double v) {
		g.rotate(v);
	}
	
	protected final void translate(Graphics2D g, double x, double y) {
		g.translate(x, y);
	}
	
	
	//background ease of use
	protected final void background(Graphics2D g2, int... v) {//for all ints
		int r=0,g=0,b=0,a=-1,n=v.length;
		switch(n){
			case 2:a=v[1];n=1;//do same as next just add alpha
			case 1:r=v[0];g=r;b=r;break;
			case 4:a=v[3];n=3;//do same as next just add alpha
			case 3:r=v[0];g=v[1];b=v[2];break;
		}
		this.background(g2,a==-1?new Color(r,g,b):new Color(r,g,b,a));
	}
	
	protected final void background(Graphics2D g, BufferedImage img) {//for img
		this.background(g, Color.BLACK);
		this.image(g, img, 0, 0, this.width, this.height);
	}
	
	protected final void background(Graphics2D g, Color c) {//for colors
		Color init = g.getColor();
		this.stroke(g, c);
		this.fillRect(g, 0, 0, this.width*2, this.height*2);
		this.stroke(g, init);
	}
	
	
	//drawing ease of use
	protected final void image(Graphics2D g, BufferedImage img, int x, int y, int w, int h) {
		g.drawImage(img, x, y, w, h, null);
	}
	
	protected final void image(Graphics2D g, BufferedImage img, int x, int y) {
		g.drawImage(img, x, y, null);
	}
	
	protected final void drawRect(Graphics2D g, double x, double y, double w, double h) {
		g.draw(new Rectangle2D.Double(x-w/2.0, y-h/2.0, w, h));
	}
	
	protected final void drawSquare(Graphics2D g, double x, double y, double r) {
		this.drawRect(g, x, y, r*2.0, r*2.0);
	}
	
	protected final void drawEllipse(Graphics2D g, double x, double y, double w, double h) {
		g.draw(new Ellipse2D.Double(x-w/2.0, y-h/2.0, w, h));
	}
	
	protected final void drawCircle(Graphics2D g, double x, double y, double r) {
		this.drawEllipse(g, x, y, r*2.0, r*2.0);
	}
	
	protected final void fillRect(Graphics2D g, double x, double y, double w, double h) {
		g.fill(new Rectangle2D.Double(x-w/2.0, y-h/2.0, w, h));
	}
	
	protected final void fillSquare(Graphics2D g, double x, double y, double r) {
		this.fillRect(g, x, y, r*2.0, r*2.0);
	}
	
	protected final void fillEllipse(Graphics2D g, double x, double y, double w, double h) {
		g.fill(new Ellipse2D.Double(x-w/2.0, y-h/2.0, w, h));
	}
	
	protected final void fillCircle(Graphics2D g, double x, double y, double r) {
		this.fillEllipse(g, x, y, r*2.0, r*2.0);
	}
	
	protected final void line(Graphics2D g, double x1, double y1, double x2, double y2) {
		g.draw(new Line2D.Double(x1, y1, x2, y2));
	}
	
	protected final void text(Graphics2D g, String str, int x, int y) {
		g.drawString(str, x, y);
	}
	
	
	//key methods
	@Override
	public final void keyTyped(KeyEvent e) {}
	
	@Override
	public final void keyPressed(KeyEvent e) {
		int kc=e.getKeyCode();
		if(kc>=0&&kc<=this.KEYS.length)this.KEYS[kc]=true;
		this.keyDown(e);
	}
	
	@Override
	public final void keyReleased(KeyEvent e) {
		int kc=e.getKeyCode();
		if(kc>=0&&kc<=this.KEYS.length)this.KEYS[kc]=false;
		this.keyUp(e);
	}
	
	protected void keyDown(KeyEvent e) {}
	
	protected void keyUp(KeyEvent e) {}
	
	
	//mouse movement methods
	@Override
	public final void mouseMoved(MouseEvent e) {
		this._mouseX_ = e.getX();
		this._mouseY_ = e.getY();
	}
	
	@Override
	public final void mouseDragged(MouseEvent e) {
		this._mouseX_ = e.getX();
		this._mouseY_ = e.getY();
	}
	
	@Override
	public void mouseEntered(MouseEvent e) {}
	
	@Override
	public void mouseExited(MouseEvent e) {}
	
	
	//mouse wheel methods
	@Override
	public final void mouseWheelMoved(MouseWheelEvent e) {
		this.mouseScroll = e.getWheelRotation();
		this.mouseWheel(e);
	}
	
	protected void mouseWheel(MouseWheelEvent e) {}
	
	
	//mouse button methods
	@Override
	public final void mouseClicked(MouseEvent e) {}
	
	@Override
	public final void mousePressed(MouseEvent e) {
		switch(e.getButton()) {
			case 1: this.leftMouseButton=true; break;
			case 2: this.middleMouseButton=true; break;
			case 3: this.rightMouseButton=true; break;
		}
		this.mouseDown(e);
	}
	
	@Override
	public final void mouseReleased(MouseEvent e) {
		switch(e.getButton()) {
			case 1: this.leftMouseButton=false; break;
			case 2: this.middleMouseButton=false; break;
			case 3: this.rightMouseButton=false; break;
		}
		this.mouseUp(e);
	}
	
	protected void mouseDown(MouseEvent e) {}
	
	protected void mouseUp(MouseEvent e) {}
}