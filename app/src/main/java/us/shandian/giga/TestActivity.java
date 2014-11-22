package us.shandian.giga;

import android.app.*;
import android.os.*;

import us.shandian.giga.get.DownloadManager;

public class TestActivity extends Activity 
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
		
		new DownloadManager().startMission("http://gdown.baidu.com/data/wisegame/2c6a60c5cb96c593/QQ_182.apk", "QQ.apk", "/storage/sdcard0/GigaGet");
    }
}
