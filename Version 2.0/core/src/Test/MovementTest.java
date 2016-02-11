package Test;

import static org.junit.Assert.*;
import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;

import org.junit.Assert;
import org.junit.Test;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.project.game.GoodGuy;

public class MovementTest {
	
	private AssetManager manager = new AssetManager();
	
	@Test
	public void testMoveRight() {	
		manager.load("Monkey.png", Texture.class);
		manager.finishLoading();
		GoodGuy good = new GoodGuy(manager.get("Monkey.png", Texture.class));
		try {
			Robot robot = new Robot();
			robot.keyPress(KeyEvent.VK_RIGHT);
		} catch (AWTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		float x = good.getX();
		float expectedX = 1;
		Assert.assertEquals(expectedX, x,0.1f);	
	}
	
	@Test
	public void testMoveLeft(){
		manager.load("Monkey.png", Texture.class);
		manager.finishLoading();
		GoodGuy good = new GoodGuy(manager.get("Monkey.png", Texture.class));
		try {
			Robot robot = new Robot();
			robot.keyPress(KeyEvent.VK_LEFT);
			robot.keyRelease(KeyEvent.VK_LEFT);
		} catch (AWTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		float x = good.getX();
		float expectedX = -1;
		Assert.assertEquals(expectedX, x,0.1f);	
	}
	
	@Test
	public void testJump(){
		manager.load("Monkey.png", Texture.class);
		manager.finishLoading();
		GoodGuy good = new GoodGuy(manager.get("Monkey.png", Texture.class));
		 
		try {
			Robot robot = new Robot();
			robot.keyPress(KeyEvent.VK_UP);
			robot.keyRelease(KeyEvent.VK_UP);
		} catch (AWTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		float y = good.getY();
		float expectedX = 1;
		Assert.assertEquals(expectedX, y,0.1f);	
	}
	

}


