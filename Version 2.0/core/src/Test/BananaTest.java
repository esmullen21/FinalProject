package Test;

import static org.junit.Assert.*;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;

import org.junit.Assert;
import org.junit.Test;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.project.game.Banana;
import com.project.game.GameScreen;
import com.project.game.GoodGuy;
import com.project.game.MyProjectGame;

public class BananaTest {

	private AssetManager manager = new AssetManager();
	
	@Test
	public void testBananaPickUp() {
		manager.load("Monkey.png", Texture.class);
		manager.load("Banana.png", Texture.class);
		manager.finishLoading();
		GoodGuy good = new GoodGuy(manager.get("Monkey.png", Texture.class));
		Banana banana = new Banana(manager.get("Banana.png", Texture.class),0,144);
		int total = 0;
		if (good.getCollisionRectangle().overlaps(banana.getCollisionRectangle())) {
			total+=1;
		}
		int expectedTotal = 1;
		Assert.assertEquals(expectedTotal, total);
		
	}

}
