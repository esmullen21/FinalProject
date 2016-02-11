package com.project.game;

import java.util.Iterator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
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

public class GameScreen extends ScreenAdapter{

	private static final float CELL_SIZE = 16;

	private static final float WORLD_WIDTH = 640;
	private static final float WORLD_HEIGHT = 480;

	private ShapeRenderer shapeRenderer;
	private Viewport viewport;
	private OrthographicCamera camera;
	private BitmapFont bitmapFont;

	private SpriteBatch batch;

	private TiledMap tiledMap;
	private OrthogonalTiledMapRenderer orthogonalTiledMapRenderer;
	private int timer=50;
	private int total = 0;

	private GoodGuy goodGuy;
	private BananaBunch bananaBunch;
	private Array<Banana> bananas = new Array<Banana>();
	private Array<BadGuy> badGuys = new Array<BadGuy>();
	

	private final MyProjectGame projectGame;
	private Array<BadBullet> bullets = new Array<BadBullet>();
	private Array<GoodBullet> goodBullets = new Array<GoodBullet>();
	private boolean fire = false;

	public GameScreen(MyProjectGame projectGame) {
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
		viewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);
		viewport.apply(true);

		shapeRenderer = new ShapeRenderer();
		batch = new SpriteBatch();

		tiledMap = projectGame.getAssetManager().get("Level1.tmx");
		orthogonalTiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap, batch);
		orthogonalTiledMapRenderer.setView(camera);

		bitmapFont = new BitmapFont();
		goodGuy = new GoodGuy(projectGame.getAssetManager().get("Monkey.png", Texture.class));
		goodGuy.setPosition(0, WORLD_HEIGHT / 3);
		badGuys.add(new BadGuy(projectGame.getAssetManager().get("BadMonkey.png", Texture.class), 568, 144));
		badGuys.add(new BadGuy(projectGame.getAssetManager().get("BadMonkey.png", Texture.class), 960 ,224));
		badGuys.add(new BadGuy(projectGame.getAssetManager().get("BadMonkey.png", Texture.class), 1282 ,144));
		badGuys.add(new BadGuy(projectGame.getAssetManager().get("BadMonkey.png", Texture.class), 1845 ,144));
		badGuys.add(new BadGuy(projectGame.getAssetManager().get("BadMonkey.png", Texture.class), 2326 ,144));
		badGuys.add(new BadGuy(projectGame.getAssetManager().get("BadMonkey.png", Texture.class), 2628 ,144));
		badGuys.add(new BadGuy(projectGame.getAssetManager().get("BadMonkey.png", Texture.class), 3030 ,144));
		badGuys.add(new BadGuy(projectGame.getAssetManager().get("BadMonkey.png", Texture.class), 3126 ,224));
		
		populateBananas();
	}

	private void populateBananas() {

		MapLayer mapLayer = tiledMap.getLayers().get("BananaLayer");
		for (MapObject mapObject : mapLayer.getObjects()) {
			bananas.add(
					new Banana(
							projectGame.getAssetManager().get("banana.png", Texture.class),
							mapObject.getProperties().get("x", Float.class),
							mapObject.getProperties().get("y", Float.class)
							)
					);
		}
	}

	private Array<CollisionCell> whichCellsDoesGoodGuyCover() {
		float x = goodGuy.getX();
		float y = goodGuy.getY();

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
		goodGuy.update(delta);
		checkEnd();
		stopGoodGuyLeavingTheScreen();
		handleGoodGuyCollision();
		handleGoodGuyCollisionWithBanana();	
		for(BadGuy badGuy:badGuys){
			badGuy.update();
		}
		for(BadBullet bullet:bullets){
			bullet.update();
		}
		for(GoodBullet gbullet:goodBullets){
			gbullet.update();
		}
		if(badGuys!=null){
			updateTimer();
		}
		goodCollideWithBullet();
		badCollideWithBullet();
		checkBulletEnd();
		updateCameraX();
		badReadyToFire();
		goodReadyToFire();
		goodCollisionWithWinBanana();
	}

	private void checkEnd(){
		int alive = badGuys.size;
		if(goodGuy.getHealth()==0){
			projectGame.setScreen(new GameOverScreen());
		}		
		else if (alive==0){
			populateWinBanana();
		}
	}

	public void populateWinBanana(){
		MapLayer mapLayer = tiledMap.getLayers().get("WinBananaLayer");
		for (MapObject mapObject : mapLayer.getObjects()) {
			this.bananaBunch = new BananaBunch(
					projectGame.getAssetManager().get("bananaBunch.png", Texture.class),
					mapObject.getProperties().get("x", Float.class),
					mapObject.getProperties().get("y", Float.class)
					);
		}
	}

	private void handleGoodGuyCollision() {
		Array<CollisionCell> GoodGuyCells = whichCellsDoesGoodGuyCover();
		GoodGuyCells = filterOutNonTiledCells(GoodGuyCells);

		for (CollisionCell cell : GoodGuyCells) {
			float cellLevelX = cell.cellX * CELL_SIZE;
			float cellLevelY = cell.cellY * CELL_SIZE;

			Rectangle intersection = new Rectangle();
			Intersector.intersectRectangles(goodGuy.getCollisionRectangle(), new Rectangle(cellLevelX, cellLevelY, CELL_SIZE, CELL_SIZE), intersection);

			if (intersection.getHeight() < intersection.getWidth()) {
				goodGuy.setPosition(goodGuy.getX(), intersection.getY() + intersection.getHeight());
				goodGuy.landed();
			} else if (intersection.getWidth() < intersection.getHeight()) {
				if (intersection.getX() == goodGuy.getX()) {
					goodGuy.setPosition(intersection.getX() + intersection.getWidth(), goodGuy.getY());
				}
				if (intersection.getX() > goodGuy.getX()) {
					goodGuy.setPosition(intersection.getX() - GoodGuy.WIDTH, goodGuy.getY());
				}
			}
		}
	}

	private void stopGoodGuyLeavingTheScreen() {
		if (goodGuy.getY() < 0) {
			goodGuy.setPosition(goodGuy.getX(), 0);
			goodGuy.landed();
		}
		if (goodGuy.getX() < 0) {
			goodGuy.setPosition(0, goodGuy.getY());
		}

		TiledMapTileLayer tiledMapTileLayer = (TiledMapTileLayer) tiledMap.getLayers().get(0);
		float levelWidth = tiledMapTileLayer.getWidth() * tiledMapTileLayer.getTileWidth();
		if (goodGuy.getX() + GoodGuy.WIDTH > levelWidth) {
			goodGuy.setPosition(levelWidth - GoodGuy.WIDTH, goodGuy.getY());
		}
	}

	private void updateCameraX() {
		TiledMapTileLayer tiledMapTileLayer = (TiledMapTileLayer) tiledMap.getLayers().get(0);
		float levelWidth = tiledMapTileLayer.getWidth() * tiledMapTileLayer.getTileWidth();
		if ( (goodGuy.getX() > WORLD_WIDTH / 2f) && (goodGuy.getX() < (levelWidth - WORLD_WIDTH / 2f)) ) {
			camera.position.set(goodGuy.getX(), camera.position.y, camera.position.z);
			camera.update();
			orthogonalTiledMapRenderer.setView(camera);
		}
	}

	private void handleGoodGuyCollisionWithBanana() {
		for (Iterator<Banana> iter = bananas.iterator(); iter.hasNext(); ) {
			Banana banana = iter.next();
			if (goodGuy.getCollisionRectangle().overlaps(banana.getCollisionRectangle())) {
				iter.remove();
				this.total+=1;
			}
		}
	}
	private void goodCollisionWithWinBanana(){
		if(bananaBunch!=null){
			if(goodGuy.getCollisionRectangle().overlaps(bananaBunch.getCollisionRectangle())){
				projectGame.setScreen(new WinScreen(total, goodGuy.getHealth()));
			}			
		}		
	}

	private void goodCollideWithBullet(){
		for (Iterator<BadBullet> iter = bullets.iterator(); iter.hasNext(); ) {
			BadBullet bbullet = iter.next();
			if (goodGuy.getCollisionRectangle().overlaps(bbullet.getCollisionRectangle())) {
				iter.remove();
				goodGuy.setHealth(goodGuy.getHealth()-10);;
			}
		}
	}

	private void badCollideWithBullet(){
		for(Iterator<BadGuy> iter1 = badGuys.iterator(); iter1.hasNext();){
			BadGuy badGuy = iter1.next();
			for (Iterator<GoodBullet> iter2 = goodBullets.iterator(); iter2.hasNext(); ) {
				GoodBullet gbullet = iter2.next();
				if (badGuy.getCollisionRectangle().overlaps(gbullet.getCollisionRectangle())) {
					iter2.remove();
					badGuy.setHealth(badGuy.getHealth()-20);
				}
			}
			if(badGuy.getAlive()==false){
				iter1.remove();
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
			timer=70;
		}
		else{
			timer-=1;
		}
	}
	private void badReadyToFire(){
		for(BadGuy badGuy : badGuys){
			if((timer==0)&&(badGuy.getAlive())){
				if((goodGuy.getY()==badGuy.getY())&&(goodGuy.getX()>=(badGuy.getX()-160))&&(goodGuy.getX()<=badGuy.getX())){
					BadBullet bullet = new BadBullet(projectGame.getAssetManager().get("bullet.png", Texture.class),badGuy.getX(), badGuy.getY());
					fire = true;
					bullets.add(bullet);
				}
			}
		}
	}

	private void goodReadyToFire(){
		if(goodGuy.getFire()){
			GoodBullet bullet = new GoodBullet(
					projectGame.getAssetManager().get("bullet.png", Texture.class),
					goodGuy.getX(), 
					goodGuy.getY(),
					goodGuy.getFacing());
			goodBullets.add(bullet);
		}
	}
	
	private void clearScreen() {
		Gdx.gl.glClearColor(0.2f, 0.75f, 1.0f, 1.0f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
	}

	private void draw() {
		batch.setProjectionMatrix(camera.projection);
		batch.setTransformMatrix(camera.view);
		orthogonalTiledMapRenderer.render();
		batch.begin();
		for (Banana banana : bananas) {
			banana.draw(batch);
		}
		for(BadGuy badGuy : badGuys){
			badGuy.draw(batch);
		}
		if(fire){
			for(BadBullet bullet:bullets){
				bullet.draw(batch);
			}
		}
		for(GoodBullet gBullet: goodBullets){
			gBullet.draw(batch);
		}
		if(bananaBunch!=null){
			bananaBunch.draw(batch);
		}
		goodGuy.draw(batch);
		drawScore();
		batch.end();


	}
	
	private void drawScore(){
		String healthAsString = "Health =  "+Integer.toString(goodGuy.getHealth());
		String totalAsString = "Bananas = "+Integer.toString(this.total);
		int xHealth=550;
		int xTotal=2;
		int diff = 0;
		TiledMapTileLayer tiledMapTileLayer = (TiledMapTileLayer) tiledMap.getLayers().get(0);
		float levelWidth = tiledMapTileLayer.getWidth() * tiledMapTileLayer.getTileWidth();
		if ( (goodGuy.getX() > WORLD_WIDTH / 2f) && (goodGuy.getX() <= (levelWidth - WORLD_WIDTH / 2f)) ) {
			diff=(int)goodGuy.getX()-(xHealth-230);
			xHealth+=diff;
			xTotal+=diff;
		}
		else if(goodGuy.getX() >2880){
			xHealth=3110;
			xTotal=2562;			
		}
		
		bitmapFont.setColor(0f, 0f, 0f, 1.0f);	
		bitmapFont.draw(batch, healthAsString, xHealth,478);
		bitmapFont.draw(batch, totalAsString, xTotal, 478);
	}

	

	private void drawDebug() {
		shapeRenderer.setProjectionMatrix(camera.projection);
		shapeRenderer.setTransformMatrix(camera.view);
		shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
		goodGuy.drawDebug(shapeRenderer);
		shapeRenderer.end();
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
