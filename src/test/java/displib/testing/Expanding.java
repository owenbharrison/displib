package displib.testing;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;

import io.github.owenbharrison.displib.Display;
import io.github.owenbharrison.displib.maths.Maths;
import io.github.owenbharrison.displib.maths.geom.AABB;
import io.github.owenbharrison.displib.maths.geom.Poly2D;
import io.github.owenbharrison.displib.maths.vector.Vec2D;

public class Expanding extends Display{
	private static final long serialVersionUID = -2329484177323016617L;
		
	private Poly2D[] polys = new Poly2D[25];
	
	private AABB bounds;
	
	public static void main(String[] args) {
		Expanding t = new Expanding();
		t.setPreferredSize(new Dimension(600, 600));
		t.start();
	}
	
	@Override
	protected void setup() {
		double b = 15.0;
		bounds = new AABB(new Vec2D(b, b), new Vec2D((double)width-b, (double)height-b));
		for(int i=0;i<polys.length;i++) {
			Vec2D pos = new Vec2D(
					Maths.map(Math.random(), 0.0, 1.0, b, (double)width-b),
					Maths.map(Math.random(), 0.0, 1.0, b, (double)height-b)
			);
			polys[i] = new Poly2D.Ellipse(pos, 12.0, 12.0, 48);
		}
	}
	
	@Override
	protected void update(double dt) {
		setTitle("TESTING!! @ "+getFps()+"fps");
		for(Poly2D p:polys) {
			p.update();
			
			double sz = 18.0*dt;
			for(int i=0;i<p.model.length;i++) {
				double[] m = p.model[i];
				Vec2D cp = Vec2D.fromAngle(m[0]+p.angle).mult(m[1]+sz).add(p.pos);
				boolean ready = true;
				for(Poly2D o:polys) {
					if(!p.equals(o)) {//dont check the same poly
						if(o.isVecInside(cp)) {//if point inside any poly
							ready = false;//dont do anythin
						}
					}
				}
				if(!bounds.isVecInside(cp))ready = false;
				p.model[i][1]+=ready?sz:0.0*dt;
			}
		}
	}
	
	@Override
	protected void draw(Graphics2D g) {
		background(g, 0);
		g.setColor(Color.BLUE);
		for(Poly2D p:polys)p.show(g);
	}
}
