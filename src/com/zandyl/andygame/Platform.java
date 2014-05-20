package com.zandyl.andygame;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

public class Platform {

	int width;
	int height;
	
	int x, y;
	

	
	public Platform(int width, int height, int x, int y) {
		super();
		this.width = width;
		this.height = height;
		this.x = x;
		this.y = y;
	}

	public void checkObject(SpriteAnimation sa){
		
		if(sa.getX() + sa.getSpriteWidth() > x && sa.getX() < x + width){
			
			if(y <= sa.getY() + sa.getSpriteHeight() && sa.getY() + sa.getSpriteHeight() < y + height && sa.getVelY() >= 0){
				sa.setVelY(0);
				sa.setY(y-sa.getSpriteHeight());
				sa.setGrounded(true);
			}
			
		}
		
		

	}
	
	public void draw(Canvas canvas, Paint painter){
		Rect r = new Rect(x, y, x+width, y + height);
		painter.setColor(Color.BLUE);
		canvas.drawRect(r, painter);
	}
	
	
}
