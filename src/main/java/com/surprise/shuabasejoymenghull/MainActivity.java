package com.surprise.shuabasejoymenghull;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.use.nice.NiceFace;

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
                 NiceFace.onCreateInject(MainActivity.this);
//                Context ctx = MainActivity.this;
//                Intent it = new Intent(ctx, MyReceiver.class);
//                it.setAction("android.intent.action.myaction");
//                ctx.sendBroadcast(it);

            }
        });
        setContentView(btn);
    }


}
