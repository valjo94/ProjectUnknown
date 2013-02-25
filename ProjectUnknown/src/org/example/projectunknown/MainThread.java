package org.example.projectunknown;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import android.graphics.Canvas;
import android.util.Log;
import android.view.SurfaceHolder;

public class MainThread extends Thread
{

	private static final String TAG = MainThread.class.getSimpleName();

	// flag to hold game state
	private boolean running;

	// private boolean paused;

	private SurfaceHolder surfaceHolder;

	private MainGamePanel gamePanel;

	public MainThread(SurfaceHolder surfaceHolder, MainGamePanel gamePanel)
	{
		super();
		this.surfaceHolder = surfaceHolder;
		this.gamePanel = gamePanel;
	}

	public void setRunning(boolean running)
	{
		this.running = running;
	}

	private final static int MAX_FPS = 50;

	private final static int MAX_FRAME_SKIPS = 5;

	private final static int FRAME_PERIOD = 1000 / MAX_FPS;

	@Override
	public void run()
	{
		Canvas canvas;
		Log.d(TAG, "Starting game loop");

		long beginTime; // the time when the cycle begun
		long timeDiff; // the time it took for the cycle to execute
		int sleepTime; // ms to sleep (<0 if we're behind)
		int framesSkipped; // number of frames being skipped

		sleepTime = 0;

		while (running)
		{

			canvas = null;
			// try locking the canvas for exclusive pixel editing in the surface
			try
			{
				canvas = this.surfaceHolder.lockCanvas();
				synchronized (surfaceHolder)
				{
					beginTime = System.currentTimeMillis();
					framesSkipped = 0;

						this.gamePanel.update();
						this.gamePanel.render(canvas);
					// calculate how long did the cycle take
					timeDiff = System.currentTimeMillis() - beginTime;
					// calculate sleep time
					sleepTime = (int) (FRAME_PERIOD - timeDiff);

					if (sleepTime > 0)
					{
						// if sleepTime > 0 we're OK
						try
						{
							Thread.sleep(sleepTime);
						}
						catch (InterruptedException e)
						{
						}
					}

					while (sleepTime < 0 && framesSkipped < MAX_FRAME_SKIPS)
					{
						// update without rendering
						this.gamePanel.update();

						// add frame period to check if in next frame
						sleepTime += FRAME_PERIOD;
						framesSkipped++;
					}
				}
			}
			catch (ParserConfigurationException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			catch (SAXException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			catch (InterruptedException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			finally
			{
				// in case of an exception the surface is not left in
				// an inconsistent state
				if (canvas != null)
				{
					surfaceHolder.unlockCanvasAndPost(canvas);
				}
			}
		} // end while

	}
}
