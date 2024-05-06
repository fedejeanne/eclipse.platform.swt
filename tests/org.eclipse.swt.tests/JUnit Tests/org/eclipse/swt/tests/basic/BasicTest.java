package org.eclipse.swt.tests.basic;

import static org.eclipse.swt.tests.junit.SwtTestUtil.assertSWTProblem;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Sash;
import org.eclipse.swt.widgets.Scale;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Slider;
import org.eclipse.swt.widgets.Text;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

/**
 *  Some basic stuff.
 */
@RunWith(Parameterized.class)
public class BasicTest {

	private static Display display;
	private static Shell shell;

	@Parameters
	public static Collection<Class<? extends Control>> getControls() {
		return List.of(Button.class, //
				Label.class, //
				Link.class, //
				ProgressBar.class, //
				Sash.class, //
				Scale.class, //
				org.eclipse.swt.widgets.List.class, //
				Text.class, //
				Slider.class //
		);
	}

	@Parameter
	public static Class<Control> controlClass;

	private Control control;

	@BeforeClass
	public static void setUp() {
		display = Display.getDefault();
		shell = new Shell(display);
		shell.setBounds(0, 30, 240, 290);
	}

	@AfterClass
	public static void tearDown() {
		shell.dispose();
	}

	@Before
	public void before() throws NoSuchMethodException, SecurityException, InstantiationException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		control = controlClass.getDeclaredConstructor(Composite.class, int.class).newInstance(shell, SWT.NONE);
	}

	@Test
	public void testUpdateWidgetOutsideUIThread_ShouldFail() throws InterruptedException {
		AtomicReference<Exception> thrown = new AtomicReference<>();

		Thread thread = new Thread(() -> {
			try {
				control.setEnabled(true);
			} catch (Exception e) {
				thrown.set(e);
			}
		});

		thread.start();
		thread.join();

		assertNotNull(thrown.get());
		assertSWTProblem("Incorrect exception thrown", SWT.ERROR_THREAD_INVALID_ACCESS, thrown.get());
	}

	@Test
	public void testResetToSystemFont() {
		Font originalFont = control.getFont();

		assertNotNull(originalFont);

		// set new font
		Font newFont = new Font(display, new FontData("myFont", 10, SWT.NORMAL));
		control.setFont(newFont);
		assertEquals("Couldn't set the font", newFont, control.getFont());

		// reset to system font
		control.setFont(null);
		assertEquals("Setting the font back to null should restore the system font", originalFont, control.getFont());
	}
}
