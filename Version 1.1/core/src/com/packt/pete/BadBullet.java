package com.packt.pete;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Rectangle;

public class BadBullet {
	private float x;
	private float y;
	private float endX;
	private Texture texture;
	private Boolean terminate = false;
	
	public static final int WIDTH = 7;
	public static final int HEIGHT = 7;
	private  Rectangle collisionRectangle;
	
	public BadBullet(Texture texture, float x1, float y){
		this.texture=texture;
		this.y = y+5;
		this.x=x1;
		this.collisionRectangle = new Rectangle(x,y, WIDTH,HEIGHT);		
		this.endX=x1-100;
	}
	
	public void update() {
		x-=1;
		this.collisionRectangle.setPosition(x, y);
		if(x<endX){
			this.terminate=true;
		}
				
	}
	
	public void draw(Batch batch) {
		batch.draw(texture, x, y);
	}
	
	public Rectangle getCollisionRectangle() {
		return collisionRectangle;
	}
	
	public boolean getTerminate(){
		return terminate;
	}

}
