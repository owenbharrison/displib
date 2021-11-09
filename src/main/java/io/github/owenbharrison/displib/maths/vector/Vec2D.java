package io.github.owenbharrison.displib.maths.vector;

public class Vec2D{
	public double x, y;
	
	public Vec2D(final double x, final double y) {
		this.x = x;
		this.y = y;
	}
	
	public Vec2D add(final Vec2D v) {
		this.x += v.x;
		this.y += v.y;
		return this;
	}
	
	public Vec2D sub(final Vec2D v) {
		this.x -= v.x;
		this.y -= v.y;
		return this;
	}
	
	public Vec2D mult(final double d) {
		this.x *= d;
		this.y *= d;
		return this;
	}
	
	public Vec2D div(final double d) {
		this.x /= d;
		this.y /= d;
		return this;
	}
	
	public double dot(final Vec2D v) {
		return this.x*v.x + this.y*v.y;
	}
	
	public double cross(final Vec2D v) {
		return this.x*v.y - this.y*v.x;
	}
	
	public double mag() {
		return Math.sqrt(Vec2D.dot(this, this));
	}
	
	public double heading() {
		return Math.atan2(this.y, this.x);
	}
	
	public Vec2D normalize() {
		this.div(this.mag());
		return this;
	}
	
	public Vec2D copy() {
		return new Vec2D(this.x, this.y);
	}
	
	public static Vec2D add(final Vec2D a, final Vec2D b) {
		return a.copy().add(b);
	}
	
	public static Vec2D sub(final Vec2D a, final Vec2D b) {
		return a.copy().sub(b);
	}
	
	public static Vec2D mult(final Vec2D a, final double d) {
		return a.copy().mult(d);
	}
	
	public static Vec2D div(final Vec2D a, final double d) {
		return a.copy().div(d);
	}
	
	public static double dot(final Vec2D a, final Vec2D b) {
		return a.dot(b);
	}
	
	public static double cross(final Vec2D a, final Vec2D b) {
		return a.cross(b);
	}
	
	public static Vec2D normalize(final Vec2D a) {
		return a.copy().normalize();
	}
	
	public static Vec2D fromAngle(final double angle){
		return new Vec2D(Math.cos(angle), Math.sin(angle));
	}
	
	public static Vec2D lerpVec(final Vec2D a, final Vec2D b, final double t) {
		return Vec2D.sub(b, a).mult(t).add(a);
	}
	
	public String toString() {
		return "[x: "+this.x+", y: "+this.y+"]";
	}
}