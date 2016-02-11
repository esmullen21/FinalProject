package com.project.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class LoadingScreen extends ScreenAdapter{
	private static final float WORLD_WIDTH = 640;
    private static final float WORLD_HEIGHT = 480;
    
    private static final float PROGRESS_BAR_WIDTH = 100;
    private static final float PROGRESS_BAR_HEIGHT = 25;

    private ShapeRenderer shapeRenderer;
    private Viewport viewport;
    private Camera camera;

    private float progress = 0;
    
    private final MyProjectGame projectGame;

    public LoadingScreen(MyProjectGame projectGame) {
        this.projectGame = projectGame;
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

        projectGame.getAssetManager().load("Monkey.png", Texture.class);
        projectGame.getAssetManager().load("bullet.png", Texture.class);
        projectGame.getAssetManager().load("Level1.tmx", TiledMap.class);
        projectGame.getAssetManager().load("BadMonkey.png", Texture.class);
        projectGame.getAssetManager().load("banana.png", Texture.class);
        projectGame.getAssetManager().load("bananaBunch.png", Texture.class);
    }

    @Override
    public void render(float delta) {
        super.render(delta);
        update();
        clearScreen();
        draw();
    }

    @Override
    public void dispose() {
        super.dispose();
        shapeRenderer.dispose();
    }

    private void update() {
        if (projectGame.getAssetManager().update()) {
            projectGame.setScreen(new GameScreen(projectGame));
        } else {
            progress = projectGame.getAssetManager().getProgress();
        }
    }

    private void clearScreen() {
    	Gdx.gl.glClearColor(0.2f, 0.75f, 1.0f, 1.0f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }

    private void draw() {
        shapeRenderer.setProjectionMatrix(camera.projection);
        shapeRenderer.setTransformMatrix(camera.view);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.WHITE);
        shapeRenderer.rect(
                WORLD_WIDTH / 2 - PROGRESS_BAR_WIDTH / 2, WORLD_HEIGHT / 2 - PROGRESS_BAR_HEIGHT / 2,
                progress * PROGRESS_BAR_WIDTH, PROGRESS_BAR_HEIGHT);
        shapeRenderer.end();
    }  


}
