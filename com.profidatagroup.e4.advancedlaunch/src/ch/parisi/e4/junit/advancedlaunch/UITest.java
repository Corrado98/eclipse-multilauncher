package ch.parisi.e4.junit.advancedlaunch;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(SWTBotJunit4ClassRunner.class)
public class UITest {

	private static SWTWorkbenchBot bot;

	@BeforeClass
	public static void beforeClass() {
		bot = new SWTWorkbenchBot();
		try {
			bot.viewByTitle("Welcome").close();
		} catch (WidgetNotFoundException e) {
			// ignore
		}
	}

//	@Test
//	public void testUI() {
//		SWTWorkbenchBot bot = new SWTWorkbenchBot();
//		boolean found = false;
//		for (SWTBotShell shell : bot.shells()) {
//			if (shell.getText().contains("Eclipse")) {
//				found = true;
//				break;
//			}
//		}
//		assertTrue(found);
//	}

	@Test
	public void createProject() {
		SWTWorkbenchBot bot = new SWTWorkbenchBot();
		bot.menu("File").menu("Project...").click();
		SWTBotShell shell = bot.shell("New Project");
		shell.activate();
		bot.tree().expandNode("General").select("Project");
		bot.button("Next >").click();
		bot.textWithLabel("Project name:").setText("SWTBot Test Project");
		bot.button("Finish").click();
	}

}
