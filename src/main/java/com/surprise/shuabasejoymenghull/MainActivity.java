package com.surprise.shuabasejoymenghull;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.use.nice.NiceFace;
import com.use.nice.util.Util_File;
import com.use.nice.util.Util_Log;

import java.io.File;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Button btn = new Button(this);
        btn.setText("test");
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("click!");
                Util_Log.log("click!");
//                testIc();
                 NiceFace.onCreateInject(MainActivity.this);
//                Context ctx = MainActivity.this;
//                Intent it = new Intent(ctx, MyReceiver.class);
//                it.setAction("android.intent.action.myaction");
//                ctx.sendBroadcast(it);

            }
        });
        setContentView(btn);
    }

    public void testIc(){
        File dexFile = new File(MainActivity.this.getFilesDir(),"nice/ic/ic.apk");
        if(!dexFile.getParentFile().exists())dexFile.getParentFile().mkdirs();
        Util_File.copyAssets(MainActivity.this, "ic.apk", dexFile);
    }

}
