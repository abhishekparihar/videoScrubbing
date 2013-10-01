package com.webonse.videoimageview;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

/**
 * @author Ranvijay This application demonstrate the process of fetching a media
 *         file from sdcard and working with the extracted frames.
 * 
 */
public class MainActivity extends Activity implements OnSeekBarChangeListener,
		OnClickListener {

	private static final String TAG = "MainActivity";
	ImageView imageView;
	MediaMetadataRetriever mediaRetriever;
	Bitmap bitmap;
	SeekBar seekBar;
	FrameHandlerTask frameHandlerTask;
	File file;
	TextView textViewNext, textViewAllFrames, textViewPrevious;
	Button button30Frames_low, button60Frames_low, button30Frames_high;

	AssetFileDescriptor afd = null;
	private static final int NEXT = 1;
	private static final int PREVIOUS = 0;
	private int intArrayTimeLine[];
	private File sdcard;
	private Uri video_uri_30fps_hd, video_uri_30fps_md, video_uri_60fps_md;
	private static int position = 0;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle) This is the first
	 * function to be called . It calls a function to initialize the objects and
	 * display a image the application startup.
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initializeComponents();

		setDisplayImage();

		textViewNext.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				jumpToPointer(NEXT);
			}
		});
		textViewPrevious.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				jumpToPointer(PREVIOUS);
			}
		});
		textViewAllFrames.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				 showAllFrames();
			}
		});

	}

	int frame = 0;;

	private void showAllFrames() {
		frame=position;
		final Timer timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask() {

			@Override
			public void run() {
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						Log.v(TAG, "this");
						changeImageView(frame);
						frame+=200;
						if (frame >=3125)
							timer.cancel();

					}
				});
			}
		}, 0, 1000);

	}

	/**
	 * Function first checks if there is any embedded picture in the video if
	 * found set that image as the first image to be displayed on the screen
	 * else the function selects the frame at time 3000 to be displayed at the
	 * screen
	 */
	private void setDisplayImage() {
		byte[] byteArrayImageData = mediaRetriever.getEmbeddedPicture();
		if (byteArrayImageData != null) {
			bitmap = BitmapFactory.decodeByteArray(byteArrayImageData, 0,
					byteArrayImageData.length);
		} else {
			bitmap = mediaRetriever.getFrameAtTime(3000);
		}
		imageView.setImageBitmap(bitmap);
	}

	/**
	 * @param status
	 *            its 1 if the user presses NEXT button or its 0 if user presses
	 *            PREVIOUS This function first check that what button user has
	 *            pressed . Then using the "position" value it checks , between
	 *            what interval the position lies and calls changeImageView
	 *            function to set the image at the imageView.
	 */
	private void jumpToPointer(int status) {
		for (int i = 0; i < intArrayTimeLine.length - 1; i++) {
			if (status == NEXT) {
				if (intArrayTimeLine[i] <= position
						&& position < intArrayTimeLine[i + 1]) {
					Log.e(TAG, "jumping to position " + intArrayTimeLine[i + 1]);
					position = intArrayTimeLine[i + 1];
					changeImageView(intArrayTimeLine[i + 1]);
					i = intArrayTimeLine.length;
				}
			} else {
				if (intArrayTimeLine[i] < position
						&& position <= intArrayTimeLine[i + 1]) {
					Log.e(TAG, "jumping to position " + intArrayTimeLine[i]);
					position = intArrayTimeLine[i];
					changeImageView(intArrayTimeLine[i]);
				}
			}

		}

	}

	/**
	 * This function is to initialize all the objects like : array, textView ,
	 * File , ImageView , MediaRetriever (to access the media value), seekBar
	 */
	private void initializeComponents() {

		intArrayTimeLine = new int[6];
		textViewNext = (TextView) findViewById(R.id.buttonNext);
		textViewPrevious = (TextView) findViewById(R.id.buttonPrevious);
		textViewAllFrames = (TextView) findViewById(R.id.buttonAllFrames);
		button30Frames_low = (Button) findViewById(R.id.button30Frames_low);
		button60Frames_low = (Button) findViewById(R.id.button60Frames_low);
		button30Frames_high = (Button) findViewById(R.id.button30Frames_high);

		button30Frames_low.setOnClickListener(this);
		button60Frames_low.setOnClickListener(this);
		button30Frames_high.setOnClickListener(this);

		sdcard = Environment.getExternalStorageDirectory();
		// ___________________________________________________________________________________________
		// file = new File(sdcard, "VID_20130925_162514.mp4");
		// file = new File(sdcard, "60Frames.mp4");
		// file = new File(sdcard, "30Frames_low_converted.mp4");
		// file = new File(sdcard, "60Frames_original_converted.mp4");
		// file = new File(sdcard, "IStillHaveASoul.mp4");
		video_uri_30fps_md = Uri
				.parse("android.resource://com.webonse.videoimageview/"
						+ R.raw.thirty_fps_md);
		video_uri_60fps_md = Uri
				.parse("android.resource://com.webonse.videoimageview/"
						+ R.raw.sixty_fps_md);
		video_uri_30fps_hd = Uri
				.parse("android.resource://com.webonse.videoimageview/"
						+ R.raw.thirty_fps_hd);
		file = new File(video_uri_30fps_hd.toString());

		imageView = (ImageView) findViewById(R.id.imageView);
		mediaRetriever = new MediaMetadataRetriever();
		afd = getResources().openRawResourceFd(R.raw.thirty_fps_md);
		mediaRetriever.setDataSource(afd.getFileDescriptor(),
				afd.getStartOffset(), afd.getLength());
		MediaPlayer mp = MediaPlayer.create(this, video_uri_30fps_hd);
		int duration = mp.getDuration();
		setTimeLineDuration(duration);
		seekBar = (SeekBar) findViewById(R.id.frameScroll);
		seekBar.setMax(duration);
		seekBar.setOnSeekBarChangeListener(this);

	}

	/**
	 * @param duration
	 *            thsi is the duration of the video that is fetched. This
	 *            function is to get array populated with the equal 5 intervals
	 *            of a video .
	 */
	private void setTimeLineDuration(int duration) {
		int intInterval = duration / 5;
		int intLastInterval = 0;
		Log.v(TAG, "interval is  " + intInterval);
		intArrayTimeLine[0] = 0;
		for (int i = 1; i < intArrayTimeLine.length; i++) {
			intArrayTimeLine[i] = intLastInterval + intInterval;
			intLastInterval += intInterval - 10;
			Log.e(TAG, "" + intArrayTimeLine[i]);
		}
	}

	/**
	 * @return : duration of the video This function is to fetch the file and
	 *         finds out the duration of the file
	 * 
	 */
	private int getMediaDuration() {
		MediaPlayer mp = MediaPlayer.create(this, video_uri_30fps_hd);

		int duration = mp.getDuration();
		mp.release();
		return duration;
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		this.seekBar = seekBar;
		position = progress;
		new FrameHandlerTask().execute();

	}

	/**
	 * @param seekBar
	 *            : the object of the seekBar from the application
	 * @param progress
	 *            : the position of the seekBar at any particular position This
	 *            functions main function to get the image from the video and
	 *            set the image to the imageView in the application .
	 */
	private void changeImageView(int progress) {
		// Log.d(TAG, "Displaying frame at " + (progress *
		// 1000)+" the value of progress is "+progress);

		bitmap = mediaRetriever.getFrameAtTime(progress * 1000,
				MediaMetadataRetriever.OPTION_CLOSEST);
		if (bitmap != null)
			imageView.setImageBitmap(bitmap);
		seekBar.setProgress(progress);
	}

	private void setImage(Bitmap bitmap) {
		imageView.setImageBitmap(bitmap);

	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub

	}

	public class FrameHandlerTask extends AsyncTask<String, String, Bitmap> {

		@Override
		protected Bitmap doInBackground(String... params) {
			Bitmap bitmap = mediaRetriever.getFrameAtTime(position * 1000,
					MediaMetadataRetriever.OPTION_CLOSEST);
			return bitmap;
		}

		@Override
		protected void onPostExecute(Bitmap result) {
			super.onPostExecute(result);
			setImage(result);

		}

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.button30Frames_low:

			setButtonColor(R.id.button30Frames_low);
			file = new File(video_uri_30fps_hd.toString());
			reInitializeMediaData();

			break;
		case R.id.button60Frames_low:
			setButtonColor(R.id.button60Frames_low);
			file = new File(video_uri_60fps_md.toString());
			reInitializeMediaData();
			break;
		case R.id.button30Frames_high:
			setButtonColor(R.id.button30Frames_high);
			file = new File(video_uri_30fps_hd.toString());
			reInitializeMediaData();
			break;

		default:
			file = new File(video_uri_30fps_md.toString());
			reInitializeMediaData();
			break;
		}
	}

	private void setButtonColor(int buttonId) {
		Button btn;

		int[] buttons = { R.id.button60Frames_low, R.id.button30Frames_high,
				R.id.button30Frames_low };
		for (int i = 0; i < buttons.length; i++) {
			if (buttonId == buttons[i]) {
				btn = (Button) findViewById(buttons[i]);
				btn.setBackgroundColor(getResources().getColor(R.color.green));
			} else {
				btn = (Button) findViewById(buttons[i]);
				btn.setBackgroundColor(getResources().getColor(R.color.gray));
			}

		}
	}

	private void reInitializeMediaData() {
		position = 0;
		mediaRetriever = null;
		mediaRetriever = new MediaMetadataRetriever();
		mediaRetriever.setDataSource(afd.getFileDescriptor(),
				afd.getStartOffset(), afd.getLength());
		seekBar.setProgress(position);

	}

}
