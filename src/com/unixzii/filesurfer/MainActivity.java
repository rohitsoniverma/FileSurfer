package com.unixzii.filesurfer;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Date: 13-7-11
 * Time: ионГ8:52
 * To change this template use File | Settings | File Templates.
 */
public class MainActivity extends Activity {

    private PageContainter mContainter;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);

        mContainter = (PageContainter) findViewById(R.id.view);
        for (int i = 0; i < 10; i++) {
            TextView tv = new TextView(this);
            tv.setText("This is a TextView" + String.valueOf(i));
            mContainter.addView(tv);
        }
    }
}