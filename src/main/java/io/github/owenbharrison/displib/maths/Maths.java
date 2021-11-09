package io.github.owenbharrison.displib.maths;

import io.github.owenbharrison.displib.maths.vector.Vec2D;

public class Maths {
	private Maths() {}
	
	public static double map(double x, double a, double b, double c, double d) {
		return (x-a)*(d-c)/(b-a)+c;
	}
	
	public static double clamp(double t, double a, double b) {
		return Math.min(Math.max(t, a), b);
	}
	
	public static double lerp(double t, double a, double b) {
		return t*(b-a)+a;
	}
	
	public static double snapTo(double a, double b) {
		return Math.round(a/b)*b;
	}
	
	public static double[] lineLineIntersection(Vec2D a, Vec2D b, Vec2D c, Vec2D d){
		double q = (a.x-b.x)*(c.y-d.y)-(a.y-b.y)*(c.x-d.x);
		double t = ((a.x-c.x)*(c.y-d.y)-(a.y-c.y)*(c.x-d.x))/q;
		double u = ((b.x-a.x)*(a.y-c.y)-(b.y-a.y)*(a.x-c.x))/q;
		return new double[] {t, u};
	}
	
	public static boolean lineLineIntersect(Vec2D a, Vec2D b, Vec2D c, Vec2D d) {
		double[] r = Maths.lineLineIntersection(a, b, c, d);
		return r[0]>=0.0&&r[0]<=1.0
				&&r[1]>=0.0&&r[1]<=1.0;
	}
	
	public static double random(double amt) {
		return Math.random()*amt;
	}
	
	public static double random(double min, double max) {
		return Maths.map(Math.random(), 0.0, 1.0, min, max);
	}
}
