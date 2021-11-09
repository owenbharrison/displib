package io.github.owenbharrison.displib.maths.geom;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

import io.github.owenbharrison.displib.maths.vector.Vec2D;

public class AABB{
	public Vec2D min, max;
	
	public AABB(Vec2D min, Vec2D max){
		this.min = min.copy();
		this.max = max.copy();
	}
	
	public boolean isVecInside(Vec2D v) {
		return v.x>=this.min.x&&v.x<=this.max.x
				&&v.y>=this.min.x&&v.y<=this.max.y;
	}
	
	public void show(Graphics2D g) {
		g.draw(new Rectangle2D.Double(this.min.x, this.min.y, this.max.x-this.min.x, this.max.y-this.min.y));
	}
}
