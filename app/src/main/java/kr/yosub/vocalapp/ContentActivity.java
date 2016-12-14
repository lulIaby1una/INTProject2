package kr.yosub.vocalapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

/**
 * Created by Luna on 2016. 11. 28..
 */

public class ContentActivity extends Activity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content);

        Button btCscale = (Button) findViewById(R.id.btCscale);

        btCscale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ContentActivity.this, StartActivity.class);
                startActivity(intent);
            }
        });
    }
}
