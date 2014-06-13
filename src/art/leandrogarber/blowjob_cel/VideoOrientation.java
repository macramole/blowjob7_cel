package art.leandrogarber.blowjob_cel;

import android.content.Context;
import android.content.pm.ActivityInfo;

public class VideoOrientation {
	
	private static boolean[] changeOrientation = { true, true, true, false, false, false };
	
	public static void setOrientation(Context context)
	{
		try
		{
			if ( changeOrientation[ Secuenciador.calculateCelIndex( Utils.getMacAddress(context) ) ] )
				((MainActivity)context).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
		} catch ( Exception e ) {
			e.printStackTrace();
		}
	}
}
