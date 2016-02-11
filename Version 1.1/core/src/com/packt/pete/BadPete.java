package com.packt.pete;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;

public class BadPete {

	public static final int WIDTH = 16;
	public static final int HEIGHT = 15;


	private final Rectangle collisionRectangle;

	private float x = 384;
	private float y = 112;

	private int health=100;

	private final Animation walking;
	private final TextureRegion standing;

	public BadPete(Texture texture) {
		TextureRegion[] regions = TextureRegion.split(texture, WIDTH, HEIGHT)[0];

		walking = new Animation(0.25F, regions[0], regions[1]);
		walking.setPlayMode(Animation.PlayMode.LOOP);

		standing = regions[0];

		this.collisionRectangle = new Rectangle(x,y, WIDTH,HEIGHT);
	}

	/*public void update(float delta) {
		
	}*/

	public void drawDebug(ShapeRenderer shapeRenderer) {
		shapeRenderer.rect(collisionRectangle.x, collisionRectangle.y, collisionRectangle.width, collisionRectangle.height);
	}

	public void draw(Batch batch) {
		//TextureRegion toDraw = standing;

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

}



