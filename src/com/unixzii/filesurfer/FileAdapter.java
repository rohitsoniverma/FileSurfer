package com.unixzii.filesurfer;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Date: 13-7-11
 * Time: ÉÏÎç11:47
 * To change this template use File | Settings | File Templates.
 */
public class FileAdapter extends BaseAdapter {

    private Context mContext;
    private String mPath;
    private ArrayList<String> mFileNames;

    public FileAdapter(Context context) {
        mContext = context;
    }

    @Override
    public int getCount() {
        return mFileNames.size();
    }

    @Override
    public Object getItem(int i) {
        return mFileNames.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
           view = View.inflate(mContext,R.layout.general_item_layout,null);
        }

        TextView textView = (TextView) view.findViewById(R.id.textView);

        textView.setText(mFileNames.get(i));

        return view;
    }

    private void resolvePath() {
        File dirFile = new File(mPath);
        File[] children = dirFile.listFiles();

        mFileNames = new ArrayList<String>();
        for (File child : children) {
            mFileNames.add(child.getName());
        }
    }

    public void setPath(String path) {
        mPath = path;
        resolvePath();
    }
}
