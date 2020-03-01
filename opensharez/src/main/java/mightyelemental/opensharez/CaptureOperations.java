package mightyelemental.opensharez;

import java.awt.AWTException;
import java.awt.DisplayMode;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.MouseInfo;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.image.BufferedImage;

public class CaptureOperations {

	// public static final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	private static Robot           robot;
	public static GraphicsDevice[] screens         = GraphicsEnvironment
			.getLocalGraphicsEnvironment().getScreenDevices();
	public static Rectangle        maxWindowBounds = new Rectangle( 0, 0, 0, 0 );

	static {
		try {
			robot = new Robot();
		} catch (AWTException e) {
			e.printStackTrace();
		}
		System.out.println( getMonitorSizes() );
		for (GraphicsDevice gd : screens) {
			maxWindowBounds = maxWindowBounds.union( gd.getDefaultConfiguration().getBounds() );
		}
	}

	private static String getMonitorSizes() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < screens.length; i++) {
			DisplayMode dm = screens[i].getDisplayMode();
			sb.append( String.format( "Screen %d: width: %d, height: %d\n", i, dm.getWidth(),
					dm.getHeight() ) );
		}
		return sb.toString();
	}

	public static Rectangle getScreenBounds(int screenNum) {
		if (screenNum > screens.length - 1 || screenNum < 0) return null;
		return screens[screenNum].getDefaultConfiguration().getBounds();
	}

	public static BufferedImage captureScreen(int screenNum) {
		return robot.createScreenCapture( getScreenBounds( screenNum ) );
	}

	public static BufferedImage captureAllDisplays() {
		return robot.createScreenCapture( maxWindowBounds );
	}

	public static BufferedImage subImage(BufferedImage img, Rectangle rect) {
		return img.getSubimage( rect.x, rect.y, rect.width, rect.height );
	}

	public static BufferedImage captureRegion() {
		int i = 0;
		for (i = 0; i < screens.length; i++) {
			GraphicsDevice gd = screens[i];
			if (gd.getDefaultConfiguration().getBounds()
					.contains( MouseInfo.getPointerInfo().getLocation() )) {
				break;
			}
		}

		BufferedImage img = captureScreen( i );

		FullscreenRegionSelectionWindow frame = new FullscreenRegionSelectionWindow( img );
		screens[i].setFullScreenWindow( frame );

		while ((frame == null || !frame.regionSelected) && !frame.cancelled) {
			try {
				Thread.sleep( 50 );
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		screens[1].setFullScreenWindow( null );
		Rectangle sel = frame.selection;
		if (!frame.cancelled) {
			OpenShareZ.CAPTURE.play();
			frame.dispose();
			return subImage( img, sel );
		} else {
			frame.dispose();
			return null;
		}
	}

}
