package com.zandyl.andygame;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;

public class MainActivity extends Activity {

	private MainGameView gameView;
	RelativeLayout frame;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_main);

		frame = (RelativeLayout) findViewById(R.id.frame);
		gameView = new MainGameView(getApplicationContext());
		frame.addView(gameView);
		
		gameView.requestFocus();

		// sound stuff?
		// Set the hardware buttons to control the music
		this.setVolumeControlStream(AudioManager.STREAM_MUSIC);

	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		
		// add save states later
		//SharedPreferences preferences = getPreferences(MODE_PRIVATE);
		//SharedPreferences.Editor editor = preferences.edit();
		
		
	}
	
	@Override
	protected void onResume() {
		super.onResume();
	}
	
	
	private class MainGameView extends SurfaceView implements
			SurfaceHolder.Callback, SensorEventListener{

		private final SurfaceHolder mSurfaceHolder;
		private final Paint mPainter = new Paint();
		private Thread mDrawingThread;
		private final DisplayMetrics mDisplay;
		private final int mDisplayWidth, mDisplayHeight;
		
		SensorManager sensorManager;
		Sensor accelerometer;
		Sensor magneticField;
		
		// sound stuff
		private SoundPool soundPool;
		private int soundID;
		boolean loaded = false;

		// Array for platforms
		ArrayList<Platform> platformList;

		// Temporary vars for testing, fill in with array later
		SpriteAnimation young_link = new SpriteAnimation(
				BitmapFactory.decodeResource(getResources(),
						R.drawable.walk_link), BitmapFactory.decodeResource(
						getResources(), R.drawable.jump_link), 100, 200, 30,
				10, 3);

		public MainGameView(Context context) {
			super(context);

			mDisplay = new DisplayMetrics();
			MainActivity.this.getWindowManager().getDefaultDisplay()
					.getMetrics(mDisplay);
			mDisplayWidth = mDisplay.widthPixels;
			mDisplayHeight = mDisplay.heightPixels;

			mPainter.setAntiAlias(true);

			mSurfaceHolder = getHolder();
			mSurfaceHolder.addCallback(this);

			platformList = new ArrayList<Platform>();

			// filling in platform array
			platformList.add(new Platform(mDisplayWidth + 100, 70, -50,
					mDisplayHeight - 70));
			platformList.add(new Platform(mDisplayWidth / 2, 30, 300,
					mDisplayHeight - 200));
			platformList.add(new Platform(mDisplayWidth / 2, 30, 50,
					mDisplayHeight - 330));
			platformList.add(new Platform(mDisplayWidth / 2, 30, 270,
					mDisplayHeight - 420));
			platformList.add(new Platform(mDisplayWidth / 3, 30, 500,
					mDisplayHeight - 540));

			// setting touch listener
			this.setOnTouchListener(new OnTouchListener() {
				@Override
				public boolean onTouch(View v, MotionEvent event) {

					int pointerIndex = event.getActionIndex();
					int pointerID = event.getPointerId(pointerIndex);

					switch (event.getActionMasked()) {

					case MotionEvent.ACTION_DOWN:
					case MotionEvent.ACTION_POINTER_DOWN:
					case MotionEvent.ACTION_MOVE: {

						if (young_link.isGrounded()) {
							// temp jump stuff

							young_link.setVelY(-40);
							young_link.setGrounded(false);

							// sound effects?
							// Getting the user sound settings
							AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
							float actualVolume = (float) audioManager
									.getStreamVolume(AudioManager.STREAM_MUSIC);
							float maxVolume = (float) audioManager
									.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
							float volume = actualVolume / maxVolume;
							// Is the sound loaded already?
							if (loaded) {
								soundPool.play(soundID, volume, volume, 1, 0,
										1f);
								Log.e("Test", "Played sound");
							}
						}
						break;
					}
					}
					return true;
				}
			});

			// sound stuff
			// Load the sound
			soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
			soundPool
					.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
						@Override
						public void onLoadComplete(SoundPool soundPool,
								int sampleId, int status) {
							loaded = true;
						}
					});
			soundID = soundPool.load(getApplicationContext(), R.raw.oddbounce,
					1);
		}
		
		@Override
		public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2,
				int arg3) {
			// TODO Auto-generated method stub

		}

		@Override
		public void surfaceCreated(SurfaceHolder holder) {

			sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
			
			accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
			magneticField = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
			
			sensorManager.registerListener(this, accelerometer,
					SensorManager.SENSOR_DELAY_GAME);
			sensorManager.registerListener(this, magneticField,
					SensorManager.SENSOR_DELAY_GAME);
			
			if (mDrawingThread == null) {
				mDrawingThread = new Thread(new Runnable() {
					public void run() {

						Canvas canvas = null;

						while (!Thread.currentThread().isInterrupted()) {
							canvas = mSurfaceHolder.lockCanvas();

							if (canvas != null) {

								// temp reset x after exiting screen
								if (young_link.getX() > mDisplayWidth) {
									young_link.setX(0);
								}
								
								else if (young_link.getX() < 0){
									young_link.setX(mDisplayWidth);
								}

								// update frames and draw

								young_link.update(System.currentTimeMillis());

								canvas.drawColor(Color.DKGRAY);

								// drawing platforms
								for (Platform p : platformList) {
									p.draw(canvas, mPainter);
								}

								young_link.setGrounded(false);
								for (Platform p : platformList) {
									p.checkObject(young_link);
									if (young_link.isGrounded()) {
										break;
									}
								}

								young_link.draw(canvas);
								if (null != canvas) {
									mSurfaceHolder.unlockCanvasAndPost(canvas);
								}

							}
						}

					}
				});
				mDrawingThread.start();
			}
		}

		@Override
		public void surfaceDestroyed(SurfaceHolder holder) {
			if (null != mDrawingThread)
				mDrawingThread.interrupt();
			
			sensorManager.unregisterListener(gameView);
		}

		
		 
		 
		@Override
		public void onAccuracyChanged(Sensor arg0, int arg1) {
			// TODO Auto-generated method stub
		}

		private float[] mGravity;
		private float[] mGeomagnetic;
		@Override
		public void onSensorChanged(SensorEvent se) {

		    if (se.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
		      mGravity = se.values;
		    if (se.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
		      mGeomagnetic = se.values;
		    if (mGravity != null && mGeomagnetic != null) {
		      float R[] = new float[9];
		      float I[] = new float[9];
		      boolean success = SensorManager.getRotationMatrix(R, I, mGravity, mGeomagnetic);
		      if (success&& !young_link.isGrounded()) {
		        float orientation[] = new float[3];
		        SensorManager.getOrientation(R, orientation);
		        young_link.setX(young_link.getX() + 2*Math.round(orientation[2]));
		        young_link.setY(young_link.getY() - 2*Math.round(orientation[1]));
		        
		      }
		    }			
		}

	}

}
