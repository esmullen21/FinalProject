package com.project.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;

public class BadGuy {
	public static final int WIDTH = 30;
	public static final int HEIGHT = 30;

	private final Rectangle collisionRectangle;

	private float x = 0;
	private float y = 0;

	private boolean alive = true;
	private int health=100;

	private final TextureRegion standing;

	public BadGuy(Texture texture, float x, float y) {
		TextureRegion[] regions = TextureRegion.split(texture, WIDTH, HEIGHT)[0];

		standing = regions[2];

		this.x = x;
		this.y=y;
		this.collisionRectangle = new Rectangle(x,y, WIDTH,HEIGHT);
	}

	public void update() {
		if(health <= 0){
			this.alive=false;
		}
	}

	public void drawDebug(ShapeRenderer shapeRenderer) {
		shapeRenderer.rect(collisionRectangle.x, collisionRectangle.y, collisionRectangle.width, collisionRectangle.height);
	}

	public void draw(Batch batch) {
		batch.draw(standing, x, y);
	}

	public void setPosition(float x) {
		this.x = x;
		updateCollisionRectangle();
	}

	public float getX() {
		return x;
	}

	public float getY() {
		return y;
	}

	public Rectangle getCollisionRectangle() {
		return collisionRectangle;
	}

	private void updateCollisionRectangle() {
		collisionRectangle.setPosition(x, y);
	}

	public int getHealth(){
		return health;
	}
	
	public void setHealth(int newHealth){
		this.health=newHealth;
	}
	
	public void setAlive(boolean alive){
		this.alive=alive;
	}
	
	public boolean getAlive(){
		return this.alive;
	}

}



