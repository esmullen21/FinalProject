package Test;

import static org.junit.Assert.*;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;

import org.junit.Assert;
import org.junit.Test;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.project.game.BadGuy;
import com.project.game.GoodGuy;

public class FiringTest {
	
	private AssetManager manager = new AssetManager();

	@Test
	public void testGoodFire() {
		manager.load("Monkey.png", Texture.class);
		manager.finishLoading();
		GoodGuy good = new GoodGuy(manager.get("Monkey.png", Texture.class));
		try {
			Robot robot = new Robot();
			robot.keyPress(KeyEvent.VK_SPACE);
		} catch (AWTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		boolean fire = good.getFire();
		boolean expectedFire = true;
		Assert.assertEquals(expectedFire, fire);	
	}
	

}
