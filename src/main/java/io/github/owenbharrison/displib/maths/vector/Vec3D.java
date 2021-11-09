package io.github.owenbharrison.displib.maths.vector;

public class Vec3D{
	public double x, y, z;
	
	public Vec3D(final double x, final double y, final double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public Vec3D add(final Vec3D v) {
		this.x += v.x;
		this.y += v.y;
		this.z += v.z;
		return this;
	}
	
	public Vec3D sub(final Vec3D v) {
		this.x -= v.x;
		this.y -= v.y;
		this.z -= v.z;
		return this;
	}
	
	public Vec3D mult(final double d) {
		this.x *= d;
		this.y *= d;
		this.z *= d;
		return this;
	}
	
	public Vec3D div(final double d) {
		this.x /= d;
		this.y /= d;
		this.z /= d;
		return this;
	}
	
	public double dot(final Vec3D v) {
		return this.x*v.x + this.y*v.y + this.z*v.z;
	}
	
	public Vec3D cross(final Vec3D v) {
		return new Vec3D(
			this.y*v.z - this.z*v.y,
			this.z*v.x - this.x*v.z,
			this.x*v.y - this.y*v.x
		);
	}
	
	public double mag() {
		return Math.sqrt(Vec3D.dot(this, this));
	}
	
	public Vec3D normalize() {
		this.div(this.mag());
		return this;
	}
	
	public Vec3D copy() {
		return new Vec3D(this.x, this.y, this.z);
	}
	
	public static Vec3D add(final Vec3D a, final Vec3D b) {
		return a.copy().add(b);
	}
	
	public static Vec3D sub(final Vec3D a, final Vec3D b) {
		return a.copy().sub(b);
	}
	
	public static Vec3D mult(final Vec3D a, final double d) {
		return a.copy().mult(d);
	}
	
	public static Vec3D div(final Vec3D a, final double d) {
		return a.copy().div(d);
	}
	
	public static double dot(final Vec3D a, final Vec3D b) {
		return a.dot(b);
	}
	
	public static Vec3D cross(final Vec3D a, final Vec3D b) {
		return a.cross(b);
	}
	
	public static Vec3D normalize(final Vec3D a) {
		return a.copy().normalize();
	}
	
	public static Vec3D lerpVec(final Vec3D a, final Vec3D b, final double t) {
		return Vec3D.sub(b, a).mult(t).add(a);
	}
	
	public String toString() {
		return "[x: "+this.x+", y: "+this.y+"]";
	}
}