package com.project.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class WinScreen extends ScreenAdapter{
	private static final float WORLD_WIDTH = 640;
	private static final float WORLD_HEIGHT = 480;

	private OrthographicCamera camera;
	private FitViewport viewport;
	private ShapeRenderer shapeRenderer;
	private BitmapFont bitmapFont;
	private SpriteBatch batch;
	
	private final int total;
	private final int health;
	
	
	public WinScreen(int total, int health){
		this.total=total;
		this.health=health;
	}
	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
		viewport.update(width, height);
	}
	@Override
    public void show() {
        super.show();
        camera = new OrthographicCamera();
        camera.position.set(WORLD_WIDTH / 2, WORLD_HEIGHT / 2, 0);
        camera.update();
        viewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);
        shapeRenderer = new ShapeRenderer();
        batch = new SpriteBatch();
        bitmapFont = new BitmapFont();

        }
	@Override
	public void render(float delta) {
		super.render(delta);
		clearScreen();
		draw();
	}
	
	private void draw() {
		shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.BLACK);        
        shapeRenderer.end();
        batch.setProjectionMatrix(camera.combined); 
        batch.begin();
        bitmapFont.setColor(Color.BLACK);
        bitmapFont.draw(batch, "You Win!!", 240, 300);
        bitmapFont.draw(batch, "Total bananas collected: "+Integer.toString(total), 240, 280);
        bitmapFont.draw(batch, "Health left: "+Integer.toString(health), 240, 260);
        bitmapFont.draw(batch, "-----------------------------------", 240, 250);
        bitmapFont.draw(batch, "Final Score: "+Integer.toString(total+health), 240, 240);
        batch.end();

	}
	
	private void clearScreen() {
    	Gdx.gl.glClearColor(Color.CHARTREUSE.r, Color.CHARTREUSE.g, Color.CHARTREUSE.b, Color.CHARTREUSE.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }
	
}

