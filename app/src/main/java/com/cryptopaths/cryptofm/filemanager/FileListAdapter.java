package com.cryptopaths.cryptofm.filemanager;

import android.content.Context;
import android.database.DataSetObserver;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cryptopaths.cryptofm.R;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

/**
 * Created by tripleheader on 12/14/16.
 */

public class FileListAdapter extends BaseAdapter{
    private HashMap<Integer,String> mNumberOfFiles=new HashMap<>();
    private HashMap<Integer,String> mFolderSizes=new HashMap<>();
    private HashMap<Integer,String> mFoldersEncryptionStatus=new HashMap<>();
    private ArrayList<Integer> mFileIndices=new ArrayList<>();
    private List<String> mAdapter	=new ArrayList<>();

    private Context                 mContext;
    private LayoutInflater          mInflator;
    private	ViewHolder 			    mViewHodler;

        public FileListAdapter(Context context){
        mInflator=(LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
            this.mContext=context;
    }
        @Override
        public boolean areAllItemsEnabled() {
        return false;
    }

        @Override
        public boolean isEnabled(int i) {
        return true;
    }

        @Override
        public void registerDataSetObserver(DataSetObserver dataSetObserver) {

    }

        @Override
        public void unregisterDataSetObserver(DataSetObserver dataSetObserver) {

    }

        @Override
        public int getCount() {
        return mAdapter.size();
    }

        @Override
        public Object getItem(int i) {
        return null;
    }

        @Override
        public long getItemId(int i) {
        return 0;
    }

        @Override
        public boolean hasStableIds() {
        return false;
    }
        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
        if(view==null){
            view=mInflator.inflate(R.layout.filebrowse_lisrview,viewGroup,false);
            mViewHodler=new ViewHolder();
        }
        mViewHodler.mTextView=(TextView)view.findViewById(R.id.list_textview);
        mViewHodler.mImageView=(ImageView)view.findViewById(R.id.list_imageview);
        mViewHodler.mNumberFilesTextView=(TextView)view.findViewById(R.id.nofiles_textview);
        mViewHodler.mFolderSizeTextView=(TextView)view.findViewById(R.id.folder_size_textview);
        mViewHodler.mEncryptionSatusTextView=
                (TextView)view.findViewById(R.id.encryption_status_textview);

        mViewHodler.mTextView.setText(mAdapter.get(i));
        if(isFileIndex(i)){
            mViewHodler.mImageView.setImageDrawable(mContext.getDrawable(R.drawable.ic_insert_drive_file_white_48dp));

        }else{
            mViewHodler.mImageView.setImageDrawable(mContext.getDrawable(R.drawable.ic_folder_white_48dp));

        }
        mViewHodler.mNumberFilesTextView.setText(mNumberOfFiles.get(i));
        mViewHodler.mFolderSizeTextView.setText(mFolderSizes.get(i));
        mViewHodler.mEncryptionSatusTextView.setText(mFoldersEncryptionStatus.get(i));

        return view;
    }



        @Override
        public int getItemViewType(int i) {
        return 0;
    }

        @Override
        public boolean isEmpty() {
        return false;
    }



    private boolean isFileIndex(int index) {
        for (Integer i:
                mFileIndices) {
            if(i==index){
                return true;
            }
        }
        return false;
    }


    public void fillAdapter(String dirPath){
        //clear the adapter
        mAdapter.clear();
        mFoldersEncryptionStatus.clear();
        mNumberOfFiles.clear();
        mFolderSizes.clear();
        mFileIndices.clear();

        File file=new File(dirPath);
        if(file.isFile()){
            Log.d("google1","Filename is: "+file.getName());
            return;
        }
        File[] files=file.listFiles();
        // keep track of the index of a file not a folder
        int index=0;
        for (File f:
                files) {
            mAdapter.add(f.getName());
            if(f.isDirectory()){
                //file number of files adapter
                fillNumberofFiles(f,index++);
            }else{
                Log.d("google","Filename is: "+f.getName());
                mFileIndices.add(index);
                fillDataWithFile(file,index++);
            }

        }
    }

    private void fillDataWithFile(File file, int position) {
        mNumberOfFiles.put(position,""+getFileExtension(file));
        mFolderSizes.put(position,""+round((file.length()/1024f)/1024f,2)+"MBs");
        mFoldersEncryptionStatus.put(position,isEncryptedFolder(file));
    }

    private void fillNumberofFiles(File file,int folderPostion){
        mNumberOfFiles.put(folderPostion,""+file.listFiles().length +" items");
        mFolderSizes.put(folderPostion,""+round((getFolderSize(file)/1024f)/1024f,2)+"MBs");
        mFoldersEncryptionStatus.put(folderPostion,isEncryptedFolder(file));

    }
    private String getFileExtension(File f){
        if(!f.getName().contains(".")){
            return "file";
        }
        String name=f.getName().substring(0,f.getName().length()-3);

        if(f.getName().contains("pgp")){
            return name.substring(name.lastIndexOf('.'),name.length());
        }else{
            return name.substring(name.lastIndexOf('.'),name.length());

        }
    }


    private long getFolderSize(File dir) {
        long size = 0;
        for (File file : dir.listFiles()) {
            if (file.isFile()) {
                System.out.println(file.getName() + " " + file.length());
                size += file.length();
            }
            else
                size += getFolderSize(file);
        }
        return size;
    }
    private String isEncryptedFolder(File dir){
        //if file is not a directory but just a file
        if(dir.isFile()){
            if(dir.getName().contains("pgp")){
                return "Encrypted";
            }else{
                return "Not encrypted";
            }
        }
        //if all the files in folder are encrypted than this variable will be zero
        if(dir.listFiles().length<1){
            return "Cannot see";
        }
        int temp=dir.listFiles().length;
        for (File f:
                dir.listFiles()) {
            if(f.getName().contains("pgp")){
                temp--;
            }else{
                temp++;
            }
        }
        if(temp==0){
            return "Encrypted";
        }else{
            return "Not Encrypted";
        }
    }
    /**
     * Round to certain number of decimals
     *
     * @param d
     * @param decimalPlace the numbers of decimals
     * @return
     */

    public float round(float d, int decimalPlace) {
        return BigDecimal.valueOf(d).setScale(decimalPlace, BigDecimal.ROUND_HALF_UP).floatValue();
    }

    class ViewHolder{
        public ImageView mImageView;
        public TextView mTextView;
        public TextView mNumberFilesTextView;
        public TextView mFolderSizeTextView;
        public TextView mEncryptionSatusTextView;
    }
}