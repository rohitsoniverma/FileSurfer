package com.unixzii.filesurfer;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Date: 13-7-11
 * Time: ÉÏÎç8:52
 * To change this template use File | Settings | File Templates.
 */
public class MainActivity extends Activity {

    private PageContainter mContainter;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);

        mContainter = (PageContainter) findViewById(R.id.view);

        ListView listView = new ListView(this);
        FileAdapter adapter = new FileAdapter(this);
        adapter.setPath("/mnt/sdcard/");
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        mContainter.addView(listView);
    }
}