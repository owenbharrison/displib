package displib.testing;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import io.github.owenbharrison.displib.Display;
import io.github.owenbharrison.displib.maths.Maths;
import io.github.owenbharrison.displib.maths.geom.Poly2D;
import io.github.owenbharrison.displib.maths.vector.Vec2D;

public class CollisionDetect extends Display{
	private static final long serialVersionUID = -2329484177323016617L;
	
	private Poly2D poly;
	private boolean polyOverlap = false;
	
	private List<Poly2D> polys;
	
	public static void main(String[] args) {
		CollisionDetect t = new CollisionDetect();
		t.setPreferredSize(new Dimension(600, 600));
		t.start();
	}
	
	@Override
	protected void setup() {
		poly = new Poly2D.Ellipse(new Vec2D(200.0, 300.0), 64.0, 48.0, 3);
		
		polys = new ArrayList<>();
		
		double bfr = 20.0;
		for(int i=0;i<15;i++) {
			Vec2D pos = new Vec2D(
					Maths.map(Math.random(), 0.0, 1.0, bfr, (double)width-bfr),
					Maths.map(Math.random(), 0.0, 1.0, bfr, (double)height-bfr)
			);
			polys.add(new Poly2D.Ellipse(pos, Math.random()*20.0+20.0, Math.random()*20.0+20.0, (int)(Math.random()*12.0+4.0)));
		}
	}
	
	@Override
	protected void update(double dt) {
		double ta = Math.PI*dt;
		double spd = dt*128.0;
		
		poly.angle += KEYS[KeyEvent.VK_LEFT]?-ta:KEYS[KeyEvent.VK_RIGHT]?ta:0.0;
		double at = KEYS[KeyEvent.VK_UP]?spd:KEYS[KeyEvent.VK_DOWN]?-spd:0.0;
		poly.pos.add(Vec2D.mult(Vec2D.fromAngle(poly.angle), at));
		
		poly.update();
		polyOverlap = false;
		for(Poly2D p:polys) {
			if(!polyOverlap) {
				if(poly.isOverlapping(p))polyOverlap = true;
			}
		}
		setTitle("CollisonDetect @ "+getFps()+"fps");
	}
	
	@Override
	protected void draw(Graphics2D g) {
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		background(g, 170);
		
		g.setStroke(new BasicStroke(2));
		
		g.setColor(Color.WHITE);
		for(Poly2D p:polys)p.show(g);
		
		g.setColor(polyOverlap?Color.RED:Color.GREEN);
		poly.show(g);
	}
}
