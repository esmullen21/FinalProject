package com.packt.pete;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Rectangle;

public class GoodBullet {
	private float endX;
	private float x;
	private float y;
	private Texture texture;
	private String facing;
	private Boolean terminate =false;
	
	public static final int WIDTH = 7;
	public static final int HEIGHT = 7;
	private final Rectangle collisionRectangle;
	
	public GoodBullet(Texture texture, float x1, float y, String facing){
		this.texture=texture;
		this.y = y+5;
		this.x=x1;
		this.facing=facing;
		this.collisionRectangle = new Rectangle(x,y, WIDTH,HEIGHT);
		if(facing=="Right"){
			this.endX=x+100;
		}
		else if(facing=="Left"){
			this.endX= x-100;
		}
	}	
	
	public void update() {
		if((facing == "Right")&&(x<=endX)){
			x+=1;
			this.terminate=false;
		}
		else if((facing =="Left")&&(x>=endX)){
			x-=1;
			this.terminate=false;
		}
		else{
			this.terminate=true;
		}
		this.collisionRectangle.setPosition(x,y);	
	}
	
	public void draw(Batch batch) {
		batch.draw(texture, x, y);
	}
	
	public Rectangle getCollisionRectangle() {
		return collisionRectangle;
	}
	
	public Boolean getTerminate(){
		return this.terminate;
	}

}
