package ch.parisi.e4.junit.advancedlaunch;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.swt.finder.SWTBotTestCase;
import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(SWTBotJunit4ClassRunner.class)
public class UITest extends SWTBotTestCase {

	@BeforeClass
	public static void beforeClass() {
		SWTWorkbenchBot bot = new SWTWorkbenchBot();
		try {
			bot.viewByTitle("Welcome").close();
		} catch (WidgetNotFoundException e) {
			// ignore
		}
	}

	@Test
	public void testUI() {
		SWTWorkbenchBot bot = new SWTWorkbenchBot();
		boolean found = false;
		for (SWTBotShell shell : bot.shells()) {
			if (shell.getText().contains("Eclipse")) {
				found = true;
				break;
			}
		}
		assertTrue(found);
	}

	@Test
	public void islaunchConfigurationTypeLaunchGroupExisting() {
		bot.menu("Window").menu("Perspective").menu("Open Perspective").menu("Java").click();
		bot.toolbarDropDownButtonWithTooltip("Debug").menuItem("Debug Configurations...").click();
		bot.tree().getTreeItem("Launch Group").select();
		bot.button("Close").click();
	}


}
