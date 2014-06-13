package art.leandrogarber.blowjob_cel;

import java.util.Timer;
import java.util.TimerTask;

import android.util.Log;

public class Secuenciador {

	public static final String[] macAddresses = {
		"20:59:a0:57:24:4d", //Philips
		"70:05:14:63:76:32", //LG botones gordos abajo
		"70:05:14:67:99:3A",//"f8:f1:b6:df:d4:cf", //LG plateado 2
		"58:a2:b5:c9:4e:38", //LG boton chino
		"58:a2:b5:c6:ee:d7", //LG 4 botones
		"70:05:14:6B:B8:C9" //LG plateado
	};
	
	public static final int[] tiempo = {
		8000,8000,5000,5000,5000,5000,5000,5000,5000,1400,1400,1400,1400,1400,1400,1400,1400,1400,1400,
		1400,1400,1400,1400,1400,1400,1400,1400,3000,3000,3000,3000,3000,1500,1500,1500,1500,1500,1500,1000,
		1000,1000,1000,1000,1000,800,800,800,800,800,800,500,500,500,500,500,500,300,300,300,300,300,300,300,300,
		300,300,300,300,300,300,300,300,300,300,300,300,300,300,300,300,300,200,200,200,200,200,200,200,200,200,200,
		200,200,200,200,200,200,200,200,200,200,200,200,200,200,200,200,200,200,200,200,200,200,200,200,200,200,200,200,
		200,200,200,200,200,200,200,200,200,200,200,200,200,200,200,200,200,200,200,200,200,200,200,200,200,200,200,200,200,
		200,200,200,200,200,200,200,200,200,200,200,200,200,200,200,200,200,200,200,200,200,200,200,200,200,200,200,200,200,
		200,200,200,200,200,200,10000
	};
	
	public static final int[][] secuencia = {
		{ 1,0,2,2,2,2,2,2,2,1,0,0,0,0,0,1,1,1,1,1,1,1,1,1,1,1,0,2,2,2,2,2,1,0,0,1,0,0,1,0,0,1,0,0,1,0,0,1,0,0,1,0,0,1,0,0,1,0,0,1,0,0,1,0,0,1,0,0,1,0,0,1,0,0,1,0,0,1,0,0,1,0,0,1,0,0,1,0,0,1,0,0,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0 },
		{ 1,0,2,2,2,2,2,2,2,0,1,0,0,0,0,0,1,1,1,1,1,0,0,0,0,0,0,2,2,2,2,2,0,1,0,0,1,0,0,1,0,0,1,0,0,1,0,0,1,0,0,1,0,0,1,0,0,1,0,0,1,0,0,1,0,0,1,0,0,1,0,0,1,0,0,1,0,0,1,0,0,1,0,0,1,0,0,1,0,0,1,0,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0 },
		{ 1,0,2,2,2,2,2,2,2,0,0,1,0,0,0,0,0,1,1,1,1,1,0,0,0,0,0,2,2,2,2,2,0,0,1,0,0,1,0,0,1,0,0,1,0,0,1,0,0,1,0,0,1,0,0,1,0,0,1,0,0,1,0,0,1,0,0,1,0,0,1,0,0,1,0,0,1,0,0,1,0,0,1,0,0,1,0,0,1,0,0,1,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0 },
		{ 1,0,2,2,2,2,2,2,2,0,0,0,1,0,0,0,0,0,1,1,1,1,1,0,0,0,0,2,2,2,2,2,1,0,0,1,0,0,1,0,0,1,0,0,1,0,0,1,0,0,1,0,0,1,0,0,1,0,0,1,0,0,1,0,0,1,0,0,1,0,0,1,0,0,1,0,0,1,0,0,1,0,0,1,0,0,1,0,0,1,0,0,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0 },
		{ 1,0,2,2,2,2,2,2,2,0,0,0,0,1,0,0,0,0,0,1,1,1,1,1,0,0,0,2,2,2,2,2,0,1,0,0,1,0,0,1,0,0,1,0,0,1,0,0,1,0,0,1,0,0,1,0,0,1,0,0,1,0,0,1,0,0,1,0,0,1,0,0,1,0,0,1,0,0,1,0,0,1,0,0,1,0,0,1,0,0,1,0,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0 },
		{ 1,0,2,2,2,2,2,2,2,0,0,0,0,0,1,0,0,0,0,0,1,1,1,1,1,0,0,2,2,2,2,2,0,0,1,0,0,1,0,0,1,0,0,1,0,0,1,0,0,1,0,0,1,0,0,1,0,0,1,0,0,1,0,0,1,0,0,1,0,0,1,0,0,1,0,0,1,0,0,1,0,0,1,0,0,1,0,0,1,0,0,1,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0 }
	};
	
	public static final int SEQ_PRENDIDO = 1;
	public static final int SEQ_APAGADO = 0;
	public static final int SEQ_RANDOM = 2;
	public static final int SEQ_NOOP = 3;

	//public static final int STEP_TIME = 2000;
	
	private int celIndex;
	private int seqCurrentIndex;
	private Timer seqTimer;
	private MainActivity activity;
	
	public Secuenciador( String macAddress, MainActivity activity ) {
		try {
			celIndex = calculateCelIndex(macAddress);
			seqCurrentIndex = 0;
			this.activity = activity;
		} catch (Exception e) {
			Log.e("SECUENCIADOR", e.getMessage());
			e.printStackTrace();
		}
	}
	
	public int getCelIndex()
	{
		return celIndex;
	}
	
	public static int calculateCelIndex(String macAddress) throws Exception	{
		int i;
		
		for ( i = 0 ; i < macAddresses.length ; i++ )
		{
			if ( macAddress.equals( macAddresses[i] ) )
				break;
		}
		
		if ( i == macAddresses.length )
			throw new Exception("Secuenciador - MAC Address not found");
		
		return i;
	}
	
	private int[] getSequence() {
		return secuencia[celIndex];
	}
	
	private int getStep()
	{
		return getSequence()[seqCurrentIndex];
	}
	
	public void start()
	{
		seqCurrentIndex = 0;
		seqTimer = new Timer();
		seqTimer.schedule(new SecuenciadorTask(), 0);
	}
	
	public void stop()
	{
		if ( seqTimer != null )
			seqTimer.cancel();
		
		activity.hideLoop();
	}
	
	class SecuenciadorTask extends TimerTask {

		@Override
		public void run() {
			
			Log.d("SECUENCIADOR", "Paso: " + seqCurrentIndex);
			
			switch( getStep() )
			{
				case SEQ_APAGADO:
					activity.runOnUiThread(new Runnable() {				
						@Override
						public void run() {
							activity.hideLoop();
							Log.d("SECUENCIADOR", "0");
						}
					});
					
					break;
				case SEQ_PRENDIDO:
					activity.runOnUiThread(new Runnable() {				
						@Override
						public void run() {
							activity.showLoop();
							Log.d("SECUENCIADOR", "1");
						}
					});
					break;
				case SEQ_RANDOM:
					Log.d("SECUENCIADOR", "2");
					if ( Math.random() > 0.5 )
					{
						activity.runOnUiThread(new Runnable() {				
							@Override
							public void run() {
								activity.showLoop();
							}
						});
					}
					else
					{
						activity.runOnUiThread(new Runnable() {				
							@Override
							public void run() {
								activity.hideLoop();
							}
						});
					}
					break;
			}
			
			seqCurrentIndex++;
			
			if ( seqCurrentIndex != getSequence().length )
				seqTimer.schedule(new SecuenciadorTask(), tiempo[seqCurrentIndex]);
		}
	}
}
