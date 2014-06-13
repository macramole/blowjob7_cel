package art.leandrogarber.blowjob_cel;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.KeyguardManager;
import android.app.KeyguardManager.KeyguardLock;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.Vibrator;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.VideoView;

public class MainActivity extends Activity {
	
	MediaPlayer oMediaPlayer;
	VideoView oVideo;
	View backgroundView;
	View overlayView;
	private final long VIDEO_RESET_TIME = 1000 * 60 * 40; //40 minutos;
	
	private Handler TCPMessageHandler;
	private String TCP_IP = "192.168.2.150";
	private int TCP_PORT = 3399;
	private ClientTCP tcpClient;
	private final byte TCP_MESSAGE_OFF = '0';
	private final byte TCP_MESSAGE_ON = '1'; 
	private final byte TCP_MESSAGE_STATUS = '2';
	
	private Timer alarm;
	
	private Vibrator vibe;
	
	private boolean magicIsOn;
	private Secuenciador secuenciador;
	
	
	WakeLock wakeLock;
	public BroadcastReceiver mReceiverPowerOff = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            if (Intent.ACTION_SCREEN_OFF.equals(intent.getAction())) {
                Log.d("BLOWJOB", "Poweroff");
                
                PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
                wakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.ON_AFTER_RELEASE, "BLOWJOB");
                wakeLock.acquire();
                
                AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                Intent inten = new Intent(context,MainActivity.class);
                
                PendingIntent pi = PendingIntent.getActivity(context, 0, inten, 0);
                
                alarmMgr.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,  100, pi);
                
                //unregisterReceiver(mReceiver);
            }
        }
    };
    public BroadcastReceiver mReceiverReboot = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
                Log.d("BLOWJOB", "Reboot");
                
                
                Intent mIntent = new Intent(context,MainActivity.class);
                context.startActivity(mIntent);
            }
        }
    };
    public BroadcastReceiver mReceiverPowerDisconnect = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            if (Intent.ACTION_POWER_DISCONNECTED.equals(intent.getAction())) {
                Log.d("BLOWJOB", "ALARM ALARM");
                
                final Context con = context;
                
                alarm = new Timer();
                alarm.schedule(new TimerTask() {
					
					@Override
					public void run() {
						Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
						Ringtone r = RingtoneManager.getRingtone(con, notification);
						r.play();
					}
				}, 0, 1000);
            }
        }
    };
    public BroadcastReceiver mReceiverPowerConnect = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            if (Intent.ACTION_POWER_CONNECTED.equals(intent.getAction())) {
                Log.d("BLOWJOB", "ALARM OFF");
                
                if ( alarm != null )
                	alarm.cancel();
                
            }
        }
    };
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d("BLOWJOB", "Creating app...");
		
		setupNetwork();
		setupUI();
		setupAlarm();
		disablePowerButton();
	}
	
	private void vibrateOn()
	{
		if ( magicIsOn )
			vibe.vibrate(1000); // 1000 * 60 * 5 = 300000
	}
	
	private void vibrateOff()
	{
		vibe.cancel();
	}
	
	private void startTheMagic()
	{
		if ( !magicIsOn ) {
			magicIsOn = true;
			turnScreenOn();
			secuenciador.start();
		}
	}
	
	private void stopTheMagic()
	{
		secuenciador.stop();
		turnScreenOff();
		
		magicIsOn = false;
	}
	
	private void turnScreenOn()
	{
		Log.d("BLOWJOB","Screen On");
		
		/*
		WindowManager.LayoutParams params = getWindow().getAttributes();
		//params.flags = LayoutParams.FLAG_KEEP_SCREEN_ON;
		params.screenBrightness = 100;
		getWindow().setAttributes(params);
		
		PowerManager manager = (PowerManager) getSystemService(Context.POWER_SERVICE);
		manager.
		
		if ( wakeLock != null && !wakeLock.isHeld() )
			wakeLock.acquire();*/
	}
	
	private void turnScreenOff()
	{
		Log.d("BLOWJOB","Screen Off");
		
		/*if ( wakeLock != null && wakeLock.isHeld() )
			wakeLock.release();
		
		WindowManager.LayoutParams params = getWindow().getAttributes();
		//params.flags |= LayoutParams.FLAG_KEEP_SCREEN_ON;
		params.screenBrightness = 1;
		getWindow().setAttributes(params);*/
	}
	
	public void showLoop()
	{
		if ( magicIsOn )
		{
			overlayView.setVisibility(View.INVISIBLE);
		}
	}
	
	public void hideLoop()
	{
		if ( magicIsOn )
		{
			overlayView.setVisibility(View.VISIBLE);
		}
	}
	
	private void setupAlarm()
	{
		registerReceiver(mReceiverPowerDisconnect, new IntentFilter(Intent.ACTION_POWER_DISCONNECTED));
		registerReceiver(mReceiverPowerConnect, new IntentFilter(Intent.ACTION_POWER_CONNECTED));
	}
	
	private void setupVideo()
	{
		final Activity activity = this;
		oVideo = (VideoView)findViewById(R.id.videoView1);
		oVideo.setOnPreparedListener(new OnPreparedListener() {
			@Override
			public void onPrepared(MediaPlayer mp) {
				mp.setLooping(true);
				oMediaPlayer = mp;
			} 
		});
		
		Log.d("BLOWJOB", "Reseting video...");
        oVideo.setVideoURI( Uri.parse("android.resource://art.leandrogarber.blowjob_cel/" + getResources().getIdentifier("raw/loop" + secuenciador.getCelIndex(), "raw", getPackageName()) ) );
        oVideo.start();
		
        /*
		Timer timerResetVideo = new Timer();
		timerResetVideo.schedule(new TimerTask() {
			
			@Override
			public void run() {
				
				activity.runOnUiThread(new Runnable() {				
					@Override
					public void run() {
						if ( oMediaPlayer != null )
							oMediaPlayer.reset();
						
						Log.d("BLOWJOB", "Reseting video...");
						oVideo.setVideoURI( Uri.parse("android.resource://art.leandrogarber.blowjob_cel/" + getResources().getIdentifier("raw/loop" + secuenciador.getCelIndex(), "raw", getPackageName()) ) );
						oVideo.start();
					}
				});
			}
		}, 0, VIDEO_RESET_TIME);*/
	}
	
	private void setupUI()
	{
		requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_main);
		
		setupVideo();
		
		backgroundView = (View)findViewById(R.id.background);
		overlayView = (View)findViewById(R.id.overlay);
		
		backgroundView.setOnTouchListener( new OnTouchListener() {		
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				
				if ( event.getAction() == MotionEvent.ACTION_UP )
					vibrateOff();
				else if ( event.getAction() == MotionEvent.ACTION_MOVE )
					vibrateOn();
				
				return true;
			}
		});
		
		vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		
		overlayView.setVisibility(View.VISIBLE);
		turnScreenOff();
	}
	
	private void setupNetwork()
	{
		secuenciador = new Secuenciador(Utils.getMacAddress(this), this);
		
		Log.d("NETWORK", "My IP is: " + Utils.getIPAddress(true));
		Log.d("NETWORK", "My MAC address is: " + Utils.getMacAddress(this));
		Log.d("NETWORK", "My Server IP is: " + TCP_IP); //Utils.getServerIP(this));
		Log.d("NETWORK", "My Index is: " + secuenciador.getCelIndex());
		
		if ( tcpClient == null && TCPMessageHandler == null ) //Cuando se apaga y prende el celular quería crearlo de nuevo.
		{
			Log.d("BLOWJOB", "tcpClient is null... creating !");
			
			TCPMessageHandler = new Handler() {
				
				@Override 
				public void handleMessage(Message msg) {
					String text = (String)msg.obj;
					
					switch ( text.getBytes()[0] )
					{
						case TCP_MESSAGE_OFF:
							stopTheMagic();
							break;
						case TCP_MESSAGE_ON:
							startTheMagic();
							break;
						case TCP_MESSAGE_STATUS:
							sendStatusMessage();
							break;
					}
				}
			};
			
			tcpClient = new ClientTCP(TCP_IP, TCP_PORT,TCPMessageHandler); //Utils.getServerIP(this)
			new Thread(tcpClient).start();
		}
	}
	
	private void sendStatusMessage() {
		Intent batteryStatus = registerReceiver(null,  new IntentFilter(Intent.ACTION_BATTERY_CHANGED) );
		int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
		
		boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
							 status == BatteryManager.BATTERY_STATUS_FULL;
		
		
		int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
		int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
		
		int batteryLevel = (int) Math.floor( (level / (float)scale) * 10 );
		
		tcpClient.sendMessage("Soy: " + Utils.getMacAddress(this) + " " + String.valueOf(isCharging) + " | " + batteryLevel );
	}
	
	private void disablePowerButton()
	{
		KeyguardManager mKeyGuardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE); 
        KeyguardLock mLock = mKeyGuardManager.newKeyguardLock("MainActivity"); 
        mLock.disableKeyguard();
        
        if ( Build.VERSION.SDK_INT != Build.VERSION_CODES.JELLY_BEAN )
        {
        	PowerManager pm = (PowerManager) this.getSystemService(Context.POWER_SERVICE);
            wakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.ON_AFTER_RELEASE, "BLOWJOB");
        }
        else
        {
        	registerReceiver(mReceiverPowerOff, new IntentFilter(Intent.ACTION_SCREEN_OFF));
        	registerReceiver(mReceiverReboot, new IntentFilter(Intent.ACTION_BOOT_COMPLETED));
        }
        	
	}
	
	@Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            unregisterReceiver(mReceiverPowerConnect);
            unregisterReceiver(mReceiverPowerDisconnect);
            //unregisterReceiver(mReceiverPowerOff);
            //unregisterReceiver(mReceiverReboot);
            
            AlarmManager alarmMgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            Intent inten = new Intent(this,MainActivity.class);
            
            PendingIntent pi = PendingIntent.getActivity(this, 0, inten, 0);
            
            alarmMgr.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,  2000, pi);
        }
        else if ((keyCode == KeyEvent.KEYCODE_CALL)) {
            return true;
        }
        else if ((keyCode == KeyEvent.KEYCODE_VOLUME_UP)) {
            return true;
        }
        else if ((keyCode == KeyEvent.KEYCODE_VOLUME_DOWN)) {
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }
	
	@Override
	protected void onResume ()
	{
		Log.d("BLOWJOB", "Resuming app...");
		
		if ( wakeLock != null && !wakeLock.isHeld() )
			wakeLock.acquire();
		
		super.onResume();
	}
	
	@Override
	protected void onPause ()
	{
		Log.d("BLOWJOB", "Pausing app...");
		
		if ( wakeLock != null && wakeLock.isHeld() )
			wakeLock.release();
		
		super.onPause();
	}
	
	@Override
	protected void onStop ()
	{
		Log.d("BLOWJOB", "Stoping app...");
		
		super.onStop();
	}
	
	@Override
	protected void onDestroy()
	{
		Log.d("BLOWJOB", "Destroying app...");
		//stopTCPServer();

		super.onDestroy();
	}
	
	//Asi no se puede apagar jamás
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
	    super.onWindowFocusChanged(hasFocus);
	    if(!hasFocus) {
	       Intent closeDialog = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
	        sendBroadcast(closeDialog);
	    }
	}
}

