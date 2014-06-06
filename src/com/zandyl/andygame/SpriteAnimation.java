package com.zandyl.andygame;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

public class SpriteAnimation {
	private Bitmap groundedBitmap;
	private Bitmap arialBitmap;
	
	private Rect sourceRect;
	
	private int numFramesG;
	private int currentFrameG;
	private int currentFrameA;
	
	private long frameTicker;
	
	private int framePeriod;
	
	private int spriteWidthG;
	private int spriteWidthA;
	private int spriteHeight;
	
	private int x;
	private int y;
	
	private boolean isGrounded;
	
	private int velY = 0;
	private int accelY = 5;
	private int maxVelY = 50;
	
	private int velX = 5;
	private int accelX = 0;
	
	public SpriteAnimation(Bitmap groundedBitmap, Bitmap arialBitmap, int x, int y, int fps, int numFramesG, int numFramesA){
		this.x = x;
		this.y = y;
		
		if (groundedBitmap!=null){
			this.groundedBitmap = groundedBitmap;
			currentFrameG = 0;
			this.numFramesG = numFramesG;
			spriteWidthG = groundedBitmap.getWidth() / numFramesG;
			spriteHeight = groundedBitmap.getHeight();
		}
		else{
			isGrounded = false;
		}

		if(arialBitmap != null){
			this.arialBitmap = arialBitmap;
			currentFrameA = 0;
			spriteWidthA = arialBitmap.getWidth() / numFramesA;
			spriteHeight = arialBitmap.getHeight();
		}
		else{
			isGrounded = true;
		}
		
		sourceRect = new Rect(0, 0, spriteWidthG, spriteHeight);
		framePeriod = 1000/fps;
		frameTicker = 1;
	}
	
	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}
	
	public boolean isGrounded() {
		return isGrounded;
	}

	public void setGrounded(boolean isGrounded) {
		this.isGrounded = isGrounded;
	}
	
	public int getSpriteWidth() {
		return spriteWidthG;
	}

	public void setSpriteWidth(int spriteWidth) {
		this.spriteWidthG = spriteWidth;
	}

	public int getSpriteHeight() {
		return spriteHeight;
	}

	public void setSpriteHeight(int spriteHeight) {
		this.spriteHeight = spriteHeight;
	}
	
	public int getVelY() {
		return velY;
	}

	public void setVelY(int velY) {
		this.velY = velY;
	}

	public int getVelX() {
		return velX;
	}

	public void setVelX(int velX) {
		this.velX = velX;
	}

	public int getAccelY() {
		return accelY;
	}

	public void setAccelY(int accelY) {
		this.accelY = accelY;
	}

	public int getMaxVelY() {
		return maxVelY;
	}

	public void setMaxVelY(int maxVelY) {
		this.maxVelY = maxVelY;
	}

	public int getAccelX() {
		return accelX;
	}

	public void setAccelX(int accelX) {
		this.accelX = accelX;
	}

	public void update(long gameTime){
		
		if (gameTime > frameTicker + framePeriod) {
			frameTicker = gameTime;
			if (isGrounded) {

				// frameupdate
				currentFrameG++;
				if (currentFrameG >= numFramesG) {
					currentFrameG = 0;
				}

				// velocity and position update
				velX += accelX;
				x += velX;

				this.sourceRect.left = currentFrameG * spriteWidthG + 6;
				this.sourceRect.right = this.sourceRect.left + spriteWidthG;
			} else {
				// velocity and position update
				velY += accelY;
				if (velY > maxVelY) {
					velY = maxVelY;
				}
				velX += accelX;

				y += velY;
				x += velX;

				if (velY < -4) {
					currentFrameA = 0;
				} else if (velY > 4) {
					currentFrameA = 2;
				} else {
					currentFrameA = 1;
				}

				this.sourceRect.left = currentFrameA * spriteWidthA + 4;
				this.sourceRect.right = this.sourceRect.left + spriteWidthA;
			}

		}
	}

	public void draw(Canvas canvas){
		Rect destRect = new Rect(x, y, x+spriteWidthG, y + spriteHeight);
		if(isGrounded){
			canvas.drawBitmap(groundedBitmap, sourceRect, destRect, null);
		}
		else{
			canvas.drawBitmap(arialBitmap, sourceRect, destRect, null);
		}
	}


}
