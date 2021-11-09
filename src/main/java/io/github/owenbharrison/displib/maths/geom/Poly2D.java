package io.github.owenbharrison.displib.maths.geom;

import java.awt.Graphics2D;
import java.awt.geom.Line2D;

import io.github.owenbharrison.displib.maths.Maths;
import io.github.owenbharrison.displib.maths.vector.Vec2D;

public class Poly2D{
	public Vec2D pos = new Vec2D(0.0, 0.0);
	public double angle = 0.0;
	public int sides = 0;
	
	public double[][] model;
	public Vec2D[] points;
	
	private Poly2D() {}
	
	public Poly2D(Vec2D p, Vec2D[] m) {
		this.pos = p;
		this.sides = m.length;
		this.model = new double[this.sides][2];
		this.points = new Vec2D[this.sides];
		
		for(int i=0;i<this.sides;i++) {
			this.model[i] = new double[] {
					m[i].heading(),//dir
					m[i].mag()//mag
			};
		}
		this.update();
	}
	
	public void update() {
		for(int i=0;i<this.sides;i++) {
			double[] m = this.model[i];
			this.points[i] = Vec2D.fromAngle(m[0]+this.angle).mult(m[1]).add(this.pos);
		}
	}
	
	public void show(Graphics2D g) {
		for(int i=0;i<this.sides;i++) {
			Vec2D a = this.points[i];
			Vec2D b = this.points[(i+1)%this.sides];
			g.draw(new Line2D.Double(a.x, a.y, b.x, b.y));
		}
	}
	
	public boolean isVecInside(Vec2D p) {
		int n = 0;
		Vec2D d = new Vec2D(p.x+1.0, p.y);
		for(int i=0;i<this.sides;i++) {
			Vec2D a = this.points[i];
			Vec2D b = this.points[(i+1)%this.sides];
			double[] x = Maths.lineLineIntersection(a, b, p, d);//pointing right
			if(x[0]>=0.0&&x[0]<=1.0&&x[1]>=0.0) {//intersection on edge but for ray case
				n++;
			}
		}
		return n%2==1;
	}
	
	public boolean isOverlapping(Poly2D other) {
		for(int a=0;a<this.sides;a++) {
			int b = (a+1)%this.sides;
			Vec2D s = Vec2D.sub(this.points[b], this.points[a]);
			Vec2D j = new Vec2D(-s.y, s.x);
			
			double nr1 = Double.POSITIVE_INFINITY;
			double xr1 = -nr1;
			for(int p=0;p<this.sides;p++) {
				double q = this.points[p].dot(j);
				nr1 = Math.min(nr1, q);
				xr1 = Math.max(xr1, q);
			}
			
			double nr2 = Double.POSITIVE_INFINITY;
			double xr2 = -nr2;
			for(int p=0;p<other.sides;p++) {
				double q = other.points[p].dot(j);
				nr2 = Math.min(nr2, q);
				xr2 = Math.max(xr2, q);
			}
			
			if(!(xr2>=nr1&&xr1>=nr2))return false;
		}
		return true;
	}
	
	public static class Rect extends Poly2D{
		public Rect(Vec2D p, double w, double h) {
			super(p, new Vec2D[] {
				new Vec2D(-w/2.0, -h/2.0),
				new Vec2D(w/2.0, -h/2.0),
				new Vec2D(w/2.0, h/2.0),
				new Vec2D(-w/2.0, h/2.0)
			});
			this.update();
		}
	}
	
	public static class Ellipse extends Poly2D{
		public Ellipse(Vec2D p, double w, double h, int s) {
			super();
			this.pos = p;
			this.sides = s;
			this.model = new double[this.sides][2];
			this.points = new Vec2D[this.sides];
			
			for(int i=0;i<this.sides;i++) {
				double a = Maths.map((double)i, 0.0, (double)this.sides, 0.0, Math.PI*2.0);
				Vec2D v = new Vec2D(
					(w/2.0)*Math.cos(a),
					(h/2.0)*Math.sin(a)
				);
				this.model[i] = new double[] {
						v.heading(),//angle
						v.mag()//mag
				};
			}
			this.update();
		}
	}
}
