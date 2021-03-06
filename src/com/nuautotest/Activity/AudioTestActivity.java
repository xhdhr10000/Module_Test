package com.nuautotest.Activity;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import com.nuautotest.Adapter.NuAutoTestAdapter;
import com.nuautotest.application.ModuleTestApplication;

import java.io.FileWriter;
import java.io.IOException;

/**
 * 音频测试
 *
 * @author xie-hang
 *
 */

public class AudioTestActivity extends Activity {
	private TextView text;
	private Button startButton, stopButton, playButton, stopPlayButton, playFileButton;
	private static String mRecordName = null;

	private MediaRecorder mRecorder = null;
	private MediaPlayer mPlayer = null;

	private boolean mStartRecording = false;
	private boolean mStopRecording = false;
	private boolean mStartPlaying = false;
	private boolean mStopPlaying = false;
	private boolean mPlayingFile = false;

//	private static final int START = 0x101;
//	private static final int STOP = 0x102;
//	private static final int PLAY = 0x103;
//	private static final int STOPPLAY = 0x104;
//	private static final int PLAYFILE = 0x105;

//	public Thread thread;

	private boolean isFinished;
	private int time;
	private Context mContext;
	private Handler mHandler;
	private FileWriter mLogWriter;

	private static final int MSG_TIMEOUT = 0x106;
	private int mTimeout;
	private TimerHandler mTimerHandler;

//	Handler handler = new Handler() {
//		public void handleMessage(Message msg) {
//			switch (msg.what) {
//				case AudioTestActivity.START:
//					onRecord();
//					break;
//				case AudioTestActivity.STOP:
//					stopRecord();
//					break;
//				case AudioTestActivity.PLAY:
//					onPlay();
//					break;
//				case AudioTestActivity.STOPPLAY:
//					stopPlay();
//					break;
//				case AudioTestActivity.PLAYFILE:
//					playFile();
//					break;
//			}
//
//			super.handleMessage(msg);
//		}
//	};
// --Commented out by Inspection STOP (15-7-20 上午11:40)

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		mContext = this;
		setContentView(R.layout.audio_test);
		initCreate();
		InitPathRecord();
		text = (TextView) findViewById(R.id.sound);
		startButton = (Button) findViewById(R.id.start);
		stopButton = (Button) findViewById(R.id.stop);
		playButton = (Button) findViewById(R.id.play);
		stopPlayButton = (Button) findViewById(R.id.stopPlay);
		playFileButton = (Button) findViewById(R.id.playFile);

		stopButton.setVisibility(View.INVISIBLE);
		playButton.setVisibility(View.INVISIBLE);
		stopPlayButton.setVisibility(View.INVISIBLE);

		startButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (!mStartRecording) {
					mStopRecording = false;
					onRecord();
					mStartRecording = true;
				}
			}
		});

		stopButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (mStartRecording) {
					mStartRecording = false;
					stopRecord();
					mStopRecording = true;
				}
			}
		});

		playButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (mStopRecording) {
					mStopPlaying = false;
					onPlay();
					mStartPlaying = true;
				}
			}
		});

		stopPlayButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (mStartPlaying) {
					mStartPlaying = false;
					stopPlay();
					mStopPlaying = true;
				}
			}
		});

		playFileButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				playFile();
				mPlayingFile = !mPlayingFile;
			}
		});

//		thread = new Thread(new myThread());
//		thread.start();
	}

	@Override
	public void onResume() {
		super.onResume();
		boolean mAutomatic = AudioTestActivity.this.getIntent().getBooleanExtra("Auto", false);
		if (mAutomatic) {
			mTimeout = 30;
			mTimerHandler = new TimerHandler();
			TimerThread mTimer = new TimerThread();
			mTimer.start();
		}
	}

//	class myThread implements Runnable {
//
//		public void run() {
//			try {
//				Thread.sleep(1000);
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
//			// 开始录音
//			if (!mStartRecording && !mStopPlaying) {
//				mStartRecording = true;
//				Message message = new Message();
//				message.what = AudioTestActivity.START;
//				AudioTestActivity.this.handler.sendMessage(message);
//			}
//
//			try {
//				Thread.sleep(3000);
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
//			// 停止录音
//			if (!mStopRecording && mStartRecording) {
//				mStopRecording = true;
//				Message message = new Message();
//				message.what = AudioTestActivity.STOP;
//				AudioTestActivity.this.handler.sendMessage(message);
//			}
//
//			try {
//				Thread.sleep(1000);
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
//			// 开始播放
//			if (!mStartPlaying && mStopRecording) {
//				mStartPlaying = true;
//				Message message = new Message();
//				message.what = AudioTestActivity.PLAY;
//				AudioTestActivity.this.handler.sendMessage(message);
//			}
//
//			try {
//				Thread.sleep(3000);
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
//
//			// 停止播放
//			if (!mStopPlaying && mStartPlaying) {
//				mStopPlaying = true;
//				Message message = new Message();
//				message.what = AudioTestActivity.STOPPLAY;
//				AudioTestActivity.this.handler.sendMessage(message);
//			}
//		}
//	}

	void InitPathRecord() {
		mRecordName = ModuleTestApplication.LOG_DIR + "/audiorecordtest.3gp";
		System.out.println(mRecordName);
	}

	private void onRecord() {
		startRecording();
	}

	private void stopRecord() {
		stopRecording();
	}

	private void onPlay() {
		startPlaying();
	}

	private void stopPlay() {
		stopPlaying();
	}

	private void startPlaying() {
		try {
			mPlayer = new MediaPlayer();
			mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mPlayer.setDataSource(mRecordName);
			mPlayer.prepare();
			mPlayer.start();
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}

		text.setText(mContext.getString(R.string.start_play));
		stopPlayButton.setVisibility(View.VISIBLE);
		playButton.setVisibility(View.INVISIBLE);
		startButton.setVisibility(View.INVISIBLE);
		stopButton.setVisibility(View.INVISIBLE);
		playFileButton.setVisibility(View.INVISIBLE);
	}

	private void stopPlaying() {
		if (mPlayer != null) {
			mPlayer.release();
			mPlayer = null;
		}

		text.setText(mContext.getString(R.string.stop_play));
		startButton.setVisibility(View.VISIBLE);
		playButton.setVisibility(View.VISIBLE);
		stopButton.setVisibility(View.INVISIBLE);
		stopPlayButton.setVisibility(View.INVISIBLE);
		playFileButton.setVisibility(View.VISIBLE);
		mStartRecording=false;
	}

	private void startRecording() {
		try {
			mRecorder = new MediaRecorder();
			mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
			mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
			mRecorder.setOutputFile(mRecordName);
			mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
			mRecorder.prepare();
			mRecorder.start();
		} catch (Exception e) {
			e.printStackTrace();
			Log.e(ModuleTestApplication.TAG, "start recording failed");
			return;
		}

		text.setText(mContext.getString(R.string.start_record));
		stopButton.setVisibility(View.VISIBLE);
		playButton.setVisibility(View.INVISIBLE);
		startButton.setVisibility(View.INVISIBLE);
		stopPlayButton.setVisibility(View.INVISIBLE);
	}

	private void stopRecording() {
		if (mRecorder != null) {
			try {
				mRecorder.stop();
				mRecorder.release();
			} catch (Exception e) {
				e.printStackTrace();
				Log.e(ModuleTestApplication.TAG, "stop recording failed");
				return;
			}
			mRecorder = null;
		}

		text.setText(mContext.getString(R.string.stop_record));
		stopButton.setVisibility(View.INVISIBLE);
		startButton.setVisibility(View.VISIBLE);
		if (!mPlayingFile) playButton.setVisibility(View.VISIBLE);
		stopPlayButton.setVisibility(View.INVISIBLE);
	}

	private void playFile() {
		if (!mPlayingFile) {
			try {
				mPlayer = MediaPlayer.create(this, R.raw.guitar_love);
				mPlayer.start();
			} catch (Exception e) {
				e.printStackTrace();
				Log.e(ModuleTestApplication.TAG, "start playing failed");
				return;
			}
			text.setText(mContext.getString(R.string.audio_play_file));
			playFileButton.setText(mContext.getString(R.string.stop_play));
			playButton.setVisibility(View.INVISIBLE);
		} else {
			if (mPlayer != null) {
				mPlayer.release();
				mPlayer = null;
			}

			text.setText(mContext.getString(R.string.stop_play));
			playFileButton.setText(mContext.getString(R.string.audio_play_file));
			if (mStopRecording) playButton.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void onPause() {
		if (mRecorder != null) {
			mRecorder.release();
			mRecorder = null;
		}
		if (mPlayer != null) {
			mPlayer.release();
			mPlayer = null;
		}

		super.onPause();
	}

	@Override
	public void onDestroy() {
		releaseDestroy();
		super.onDestroy();
	}

	// 成功失败按钮
	public void onbackbtn(View view) {
		switch (view.getId()) {
			case R.id.fail:
				NuAutoTestAdapter.getInstance().setTestState(getString(R.string.audio_test), NuAutoTestAdapter.TestState.TEST_STATE_FAIL);
				this.finish();
				break;
			case R.id.success:
				NuAutoTestAdapter.getInstance().setTestState(getString(R.string.audio_test), NuAutoTestAdapter.TestState.TEST_STATE_SUCCESS);
				this.finish();
				break;
		}

	}

	@Override
	public boolean onNavigateUp() {
		onBackPressed();
		return true;
	}

	private class TimerThread extends Thread {
		@Override
		public void run() {
			while (mTimeout > 0) {
				try {
					sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				mTimeout--;
			}
			mTimerHandler.sendEmptyMessage(MSG_TIMEOUT);
		}
	}

	private class TimerHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == MSG_TIMEOUT) {
				if (NuAutoTestAdapter.getInstance().getTestState(getString(R.string.audio_test))
						== NuAutoTestAdapter.TestState.TEST_STATE_NONE) {
					NuAutoTestAdapter.getInstance().setTestState(getString(R.string.audio_test),
							NuAutoTestAdapter.TestState.TEST_STATE_TIME_OUT);
					AudioTestActivity.this.finish();
				}
			}
		}
	}

	void initCreate() {
		if (ModuleTestApplication.LOG_ENABLE) {
			try {
				mLogWriter = new FileWriter(ModuleTestApplication.LOG_DIR + "/ModuleTest/log_record.txt");
			} catch (IOException e) {
				e.printStackTrace();
			}
			ModuleTestApplication.getInstance().recordLog(null);
		}
		Log.i(ModuleTestApplication.TAG, "---Record Test---");
	}

	void releaseDestroy() {
		if (ModuleTestApplication.LOG_ENABLE) {
			ModuleTestApplication.getInstance().recordLog(mLogWriter);
			try {
				mLogWriter.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	void startAutoTest() {
		isFinished = false;

		initCreate();

		NuAutoTestAdapter.getInstance().setTestState(mContext.getString(R.string.audio_test), NuAutoTestAdapter.TestState.TEST_STATE_ON_GOING);
		mHandler.sendEmptyMessage(NuAutoTestActivity.MSG_REFRESH);

		mPlayer = new MediaPlayer();
		mPlayer = MediaPlayer.create(mContext, R.raw.guitar_love);
		if (mPlayer == null) stopAutoTest();
		mPlayer.start();
	}

	void stopAutoTest() {
		NuAutoTestAdapter.getInstance().setTestState(mContext.getString(R.string.audio_test), NuAutoTestAdapter.TestState.TEST_STATE_FAIL);
		mHandler.sendEmptyMessage(NuAutoTestActivity.MSG_REFRESH);
		isFinished = true;

		if (mPlayer != null) {
			mPlayer.stop();
			mPlayer.release();
			mPlayer = null;
		}

		releaseDestroy();
		this.finish();
	}

	private class AutoTestThread extends Handler implements Runnable {

		public AutoTestThread(Context context, Handler handler) {
			super();
			mContext = context;
			mHandler = handler;
		}

		public void run() {
			startAutoTest();
			while ( (!isFinished) && (time<2000) ) {
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				time++;
			}
			if (time >= 2000) {
				stopAutoTest();
			}
		}
	}
}