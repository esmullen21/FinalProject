package com.project.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;

public class StartUp implements Screen{
	
	private OrthographicCamera camera;
	private SpriteBatch batch;
	private Texture start, arrows, space;
	private final MyProjectGame projectGame;
	
	public StartUp(MyProjectGame project){
		this.projectGame = project;
		
	}

	@Override
	public void show() {
		batch = new SpriteBatch();
		camera = new OrthographicCamera();
        camera.setToOrtho(false, 640, 480);	
        projectGame.getAssetManager().load("startButton.png", Texture.class);
        projectGame.getAssetManager().load("arrows.png", Texture.class);
        projectGame.getAssetManager().load("spaceBar.png", Texture.class);
        projectGame.getAssetManager().finishLoading();
        this.start = projectGame.getAssetManager().get("startButton.png", Texture.class);
        this.arrows = projectGame.getAssetManager().get("arrows.png", Texture.class);
        this.space = projectGame.getAssetManager().get("spaceBar.png", Texture.class);
        
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0.627f, 1f, 0.788f,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        camera.update();
        batch.begin();
        batch.setProjectionMatrix(camera.combined);                
        batch.draw(start, 150, 250);
        batch.draw(arrows, 280,50);
        batch.draw(space, 20, 50);
        batch.end();
  
        isTouched();
	}
	
	public void isTouched(){
		if (Gdx.input.isTouched()) {
        	Rectangle bounds = new Rectangle(165, 265, 310, 108);
        	Vector3 tmp = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
        	camera.unproject(tmp);
        	if (bounds.contains(tmp.x, tmp.y)) {
        		projectGame.setScreen(new LoadingScreen(projectGame));
                dispose();
        	    }            
        }		
	}
	
	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	}
}

	
