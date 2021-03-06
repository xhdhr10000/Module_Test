package com.nuautotest.Activity;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;
import com.nuautotest.Adapter.NuAutoTestAdapter;
import com.nuautotest.application.ModuleTestApplication;
import java.io.FileWriter;
import java.io.IOException;

/**
 * 按键测试
 *
 * @author xie-hang
 *
 */

public class ButtonTestActivity extends Activity {

	private TextView textKeyUp;
	private TextView textKeyDown;
	private TextView textKeyBack;
	private TextView textKeyMenu;
	private FileWriter mLogWriter;
	private static final int MSG_TIMEOUT = 0x101;
	private int mTimeout;
	private TimerHandler mTimerHandler;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.button_test);
		textKeyUp = (TextView) findViewById(R.id.keySoundUp);
		textKeyDown = (TextView) findViewById(R.id.keySoundDown);
		textKeyBack = (TextView) findViewById(R.id.keyBack);
		textKeyMenu = (TextView) findViewById(R.id.keyMenu);

		if (ModuleTestApplication.LOG_ENABLE) {
			try {
				mLogWriter = new FileWriter(ModuleTestApplication.LOG_DIR + "/ModuleTest/log_button.txt");
			} catch (IOException e) {
				e.printStackTrace();
			}
			ModuleTestApplication.getInstance().recordLog(null);
		}
		Log.i(ModuleTestApplication.TAG, "---Button Test---");
	}

	@Override
	public void onResume() {
		super.onResume();
		boolean mAutomatic = this.getIntent().getBooleanExtra("Auto", false);
		if (mAutomatic) {
			mTimeout = 20;
			mTimerHandler = new TimerHandler();
			TimerThread mTimer = new TimerThread();
			mTimer.start();
		}
	}

	@Override
	public void onDestroy() {
		if (ModuleTestApplication.LOG_ENABLE) {
			ModuleTestApplication.getInstance().recordLog(mLogWriter);
			try {
				mLogWriter.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		super.onDestroy();
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		Log.d(ModuleTestApplication.TAG, ""+keyCode);
		switch (keyCode) {
			case KeyEvent.KEYCODE_VOLUME_DOWN:
				textKeyDown.setBackgroundColor(Color.GREEN);
				return true;
			case KeyEvent.KEYCODE_VOLUME_UP:
				textKeyUp.setBackgroundColor(Color.GREEN);
				return true;
//		case KeyEvent.KEYCODE_F1:
//			textView.setText("U盘模式键");
//			return true;
//		case KeyEvent.KEYCODE_HOME:
//			textView.setText("Home");
//			return true;
			case KeyEvent.KEYCODE_BACK:
				textKeyBack.setBackgroundColor(Color.GREEN);
				return true;
			case KeyEvent.KEYCODE_MENU:
				textKeyMenu.setBackgroundColor(Color.GREEN);
				return true;
//		case KeyEvent.KEYCODE_UNKNOWN:
//			textView.setText("未知按键");
//			return true;
		}

		return super.onKeyDown(keyCode, event);
	}

	//成功失败按钮
	public void onbackbtn(View view) {

		switch (view.getId()) {
			case R.id.fail:
				NuAutoTestAdapter.getInstance().setTestState(getString(R.string.button_test), NuAutoTestAdapter.TestState.TEST_STATE_FAIL);
				this.finish();
				break;
			case R.id.success:
				NuAutoTestAdapter.getInstance().setTestState(getString(R.string.button_test), NuAutoTestAdapter.TestState.TEST_STATE_SUCCESS);
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
				if (NuAutoTestAdapter.getInstance().getTestState(getString(R.string.button_test))
						== NuAutoTestAdapter.TestState.TEST_STATE_NONE) {
					NuAutoTestAdapter.getInstance().setTestState(getString(R.string.button_test),
							NuAutoTestAdapter.TestState.TEST_STATE_TIME_OUT);
					ButtonTestActivity.this.finish();
				}
			}
		}
	}
}