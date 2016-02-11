package com.packt.pete;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.Iterator;

/**
 * Created by James on 17/03/2015.
 */
public class GameScreen extends ScreenAdapter {

	private static final float CELL_SIZE = 16;

	private static final float WORLD_WIDTH = 640;
	private static final float WORLD_HEIGHT = 480;

	private ShapeRenderer shapeRenderer;
	private Viewport viewport;
	private OrthographicCamera camera;
	private BitmapFont bitmapFont;

	private SpriteBatch batch;
	private BadPete badPete;

	private TiledMap tiledMap;
	private OrthogonalTiledMapRenderer orthogonalTiledMapRenderer;
	private int score=100;
	private int timer=50;
	private boolean ok=true;

	private Pete pete;
	private Array<Acorn> acorns = new Array<Acorn>();
	private boolean between = false;

	private final PeteGame peteGame;
	private Array<BadBullet> bullets = new Array<BadBullet>();
	private Array<GoodBullet> goodBullets = new Array<GoodBullet>();
	private boolean fire = false;

	public GameScreen(PeteGame peteGame) {
		this.peteGame = peteGame;
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
		viewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);
		viewport.apply(true);

		shapeRenderer = new ShapeRenderer();
		batch = new SpriteBatch();

		tiledMap = peteGame.getAssetManager().get("newMap.tmx");
		orthogonalTiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap, batch);
		orthogonalTiledMapRenderer.setView(camera);

		bitmapFont = new BitmapFont();
		pete = new Pete(peteGame.getAssetManager().get("pete.png", Texture.class));
		pete.setPosition(0, WORLD_HEIGHT / 20);
		badPete = new BadPete(peteGame.getAssetManager().get("badPete.png", Texture.class));
	}

	private void populateAcorns() {

		MapLayer mapLayer = tiledMap.getLayers().get("CollectableAcorns");
		for (MapObject mapObject : mapLayer.getObjects()) {
			acorns.add(
					new Acorn(
							peteGame.getAssetManager().get("acorn.png", Texture.class),
							mapObject.getProperties().get("x", Float.class),
							mapObject.getProperties().get("y", Float.class)
							)
					);
		}
	}

	private Array<CollisionCell> whichCellsDoesPeteCover() {
		float x = pete.getX();
		float y = pete.getY();

		Array<CollisionCell> cellsCovered = new Array<CollisionCell>();

		float cellX = x / CELL_SIZE;
		float cellY = y / CELL_SIZE;

		int bottomLeftCellX = MathUtils.floor(cellX);
		int bottomLeftCellY = MathUtils.floor(cellY);

		TiledMapTileLayer tiledMapTileLayer = (TiledMapTileLayer) tiledMap.getLayers().get(0);

		cellsCovered.add(new CollisionCell(tiledMapTileLayer.getCell(bottomLeftCellX, bottomLeftCellY), bottomLeftCellX, bottomLeftCellY));

		if (cellX % 1 != 0 && cellY % 1 != 0) {
			int topRightCellX = bottomLeftCellX + 1;
			int topRightCellY = bottomLeftCellY + 1;
			cellsCovered.add(new CollisionCell(tiledMapTileLayer.getCell(topRightCellX, topRightCellY), topRightCellX, topRightCellY));
		}

		if (cellX % 1 != 0) {
			int bottomRightCellX = bottomLeftCellX + 1;
			int bottomRightCellY = bottomLeftCellY;
			cellsCovered.add(new CollisionCell(tiledMapTileLayer.getCell(bottomRightCellX, bottomRightCellY), bottomRightCellX, bottomRightCellY));
		}

		if (cellY % 1 != 0) {
			int topLeftCellX = bottomLeftCellX;
			int topLeftCellY = bottomLeftCellY + 1;
			cellsCovered.add(new CollisionCell(tiledMapTileLayer.getCell(topLeftCellX, topLeftCellY), topLeftCellX, topLeftCellY));
		}

		return cellsCovered;
	}

	private Array<CollisionCell> filterOutNonTiledCells(Array<CollisionCell> cells) {
		for (Iterator<CollisionCell> iter = cells.iterator(); iter.hasNext(); ) {
			CollisionCell collisionCell = iter.next();
			if (collisionCell.isEmpty()) {
				iter.remove();
			}
		}
		return cells;
	}

	@Override
	public void render(float delta) {
		super.render(delta);
		update(delta);
		clearScreen();
		draw();
	}

	private void update(float delta) {
		pete.update(delta);
		if((ok)&&(pete.getX()>=160)){
			this.between=true;			
		}
		checkEnd();
		stopPeteLeavingTheScreen();
		handlePeteCollision();
		handlePeteCollisionWithAcorn();		
		for(BadBullet bullet:bullets){
			bullet.update();
		}
		for(GoodBullet gbullet:goodBullets){
			gbullet.update();
		}
		if(badPete!=null){
			updateTimer();
		}
		goodCollideWithBullet();
		badCollideWithBullet();
		checkBulletEnd();
		updateCameraX();
		badReadyToFire();
		goodReadyToFire();
	}
	
	private void checkEnd(){
		if(this.score==0){
			peteGame.setScreen(new GameOver());
			
		}
		else if (badPete.getHealth()==0){
			this.ok=false;
			this.between=false;
			populateAcorns();
		}
	}

	private void handlePeteCollision() {
		Array<CollisionCell> peteCells = whichCellsDoesPeteCover();
		peteCells = filterOutNonTiledCells(peteCells);

		for (CollisionCell cell : peteCells) {
			float cellLevelX = cell.cellX * CELL_SIZE;
			float cellLevelY = cell.cellY * CELL_SIZE;

			Rectangle intersection = new Rectangle();
			Intersector.intersectRectangles(pete.getCollisionRectangle(), new Rectangle(cellLevelX, cellLevelY, CELL_SIZE, CELL_SIZE), intersection);

			if (intersection.getHeight() < intersection.getWidth()) {
				pete.setPosition(pete.getX(), intersection.getY() + intersection.getHeight());
				pete.landed();
			} else if (intersection.getWidth() < intersection.getHeight()) {
				if (intersection.getX() == pete.getX()) {
					pete.setPosition(intersection.getX() + intersection.getWidth(), pete.getY());
				}
				if (intersection.getX() > pete.getX()) {
					pete.setPosition(intersection.getX() - Pete.WIDTH, pete.getY());
				}
			}
		}
	}

	private void stopPeteLeavingTheScreen() {
		if (pete.getY() < 0) {
			pete.setPosition(pete.getX(), 0);
			pete.landed();
		}
		if (pete.getX() < 0) {
			pete.setPosition(0, pete.getY());
		}

		TiledMapTileLayer tiledMapTileLayer = (TiledMapTileLayer) tiledMap.getLayers().get(0);
		float levelWidth = tiledMapTileLayer.getWidth() * tiledMapTileLayer.getTileWidth();
		if (pete.getX() + Pete.WIDTH > levelWidth) {
			pete.setPosition(levelWidth - Pete.WIDTH, pete.getY());
		}
	}

	private void updateCameraX() {
		TiledMapTileLayer tiledMapTileLayer = (TiledMapTileLayer) tiledMap.getLayers().get(0);
		float levelWidth = tiledMapTileLayer.getWidth() * tiledMapTileLayer.getTileWidth();
		if ( (pete.getX() > WORLD_WIDTH / 2f) && (pete.getX() < (levelWidth - WORLD_WIDTH / 2f)) ) {
			camera.position.set(pete.getX(), camera.position.y, camera.position.z);
			camera.update();
			orthogonalTiledMapRenderer.setView(camera);
		}
	}

	private void handlePeteCollisionWithAcorn() {
		for (Iterator<Acorn> iter = acorns.iterator(); iter.hasNext(); ) {
			Acorn acorn = iter.next();
			if (pete.getCollisionRectangle().overlaps(acorn.getCollisionRectangle())) {
				peteGame.setScreen(new WinScreen());
			}
		}
	}
	
	private void goodCollideWithBullet(){
		for (Iterator<BadBullet> iter = bullets.iterator(); iter.hasNext(); ) {
			BadBullet bbullet = iter.next();
			if (pete.getCollisionRectangle().overlaps(bbullet.getCollisionRectangle())) {
				iter.remove();
				this.score-=10;
			}
		}
	}
	
	private void badCollideWithBullet(){
		for (Iterator<GoodBullet> iter = goodBullets.iterator(); iter.hasNext(); ) {
			GoodBullet gbullet = iter.next();
			if (badPete.getCollisionRectangle().overlaps(gbullet.getCollisionRectangle())) {
				iter.remove();
				badPete.setHealth(badPete.getHealth()-10);
			}
		}
	}
	
	private void checkBulletEnd(){
		for (Iterator<GoodBullet> iter = goodBullets.iterator(); iter.hasNext(); ) {
			GoodBullet gbullet = iter.next();
			if (gbullet.getTerminate()) {
				iter.remove();
			}
		}
		for (Iterator<BadBullet> iter2 = bullets.iterator(); iter2.hasNext(); ) {
			BadBullet bbullet = iter2.next();
			if (bbullet.getTerminate()) {
				iter2.remove();
			}
		}
	}

	private void updateTimer(){
		if(timer==0){
			timer=75;
		}
		else{
			timer-=1;
		}
	}
	private void badReadyToFire(){
		if((timer==0)&&(ok)){
			if((pete.getY()==112)&&(pete.getX()>160)){
				BadBullet bullet = new BadBullet(peteGame.getAssetManager().get("bullet.png", Texture.class),badPete.getX(), badPete.getY());
				fire = true;
				bullets.add(bullet);
			}
		}
	}

	private void goodReadyToFire(){
		if(pete.getPeteFire()){
			GoodBullet bullet = new GoodBullet(peteGame.getAssetManager().get("bullet.png", Texture.class),pete.getX(), pete.getY(), pete.getFacing());
			goodBullets.add(bullet);
		}
	}


	private void clearScreen() {
		Gdx.gl.glClearColor(Color.CYAN.r, Color.CYAN.g, Color.CYAN.b, Color.CYAN.a);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
	}

	private void draw() {
		batch.setProjectionMatrix(camera.projection);
		batch.setTransformMatrix(camera.view);
		orthogonalTiledMapRenderer.render();
		batch.begin();
		for (Acorn acorn : acorns) {
			acorn.draw(batch);
		}
		if(between){
			badPete.draw(batch);
		}
		if(fire){
			for(BadBullet bullet:bullets){
				bullet.draw(batch);
			}
		}
		for(GoodBullet gBullet: goodBullets){
			gBullet.draw(batch);
		}
		pete.draw(batch);
		drawScore();
		batch.end();
	}

	private void drawDebug() {
		shapeRenderer.setProjectionMatrix(camera.projection);
		shapeRenderer.setTransformMatrix(camera.view);
		shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
		pete.drawDebug(shapeRenderer);
		shapeRenderer.end();
	}
	
	private void drawScore(){
		String scoreAsString = Integer.toString(this.score);
		bitmapFont.setColor(0f, 0f, 0f, 1.0f);
		bitmapFont.draw(batch, scoreAsString, (pete.getX())+5,(pete.getY())+40);		
	}
	
	private class CollisionCell {

		private final TiledMapTileLayer.Cell cell;
		private final int cellX;
		private final int cellY;

		public CollisionCell(TiledMapTileLayer.Cell cell, int cellX, int cellY) {
			this.cell = cell;
			this.cellX = cellX;
			this.cellY = cellY;
		}

		public boolean isEmpty() {
			return cell == null;
		}

	}
}


