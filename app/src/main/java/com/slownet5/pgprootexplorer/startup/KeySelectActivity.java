/*
 * Copyright (c) 2017. slownet5
 *  This file is part of RootPGPExplorer also known as CryptoFM
 *
 *       RootPGPExplorer a is free software: you can redistribute it and/or modify
 *       it under the terms of the GNU General Public License as published by
 *       the Free Software Foundation, either version 3 of the License, or
 *       (at your option) any later version.
 *
 *       RootPGPExplorer is distributed in the hope that it will be useful,
 *       but WITHOUT ANY WARRANTY; without even the implied warranty of
 *       MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *       GNU General Public License for more details.
 *
 *       You should have received a copy of the GNU General Public License
 *       along with RootPGPExplorer.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.slownet5.pgprootexplorer.startup;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.slownet5.pgprootexplorer.R;
import com.slownet5.pgprootexplorer.encryption.DatabaseHandler;
import com.slownet5.pgprootexplorer.encryption.MyPGPUtil;
import com.slownet5.pgprootexplorer.filemanager.ui.FileSelectionActivity;
import com.slownet5.pgprootexplorer.filemanager.utils.SharedData;
import com.slownet5.pgprootexplorer.utils.ActionHandler;

import org.spongycastle.bcpg.ArmoredOutputStream;
import org.spongycastle.openpgp.PGPPublicKey;
import org.spongycastle.openpgp.PGPSecretKey;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;


/**
 * Created by tripleheader on 1/16/17.
 * activity
 */

public class KeySelectActivity extends AppCompatActivity {

    private static final int        GET_SECRET_KEY_CODE     =20;
    private static final String     TAG                     ="keySelectActivity";
    private String                  mSecretKeyFilename;
    private TextView                mSecKeyEditText;
    private ProgressDialog          mProgressDialog;
    private AppCompatButton mBrwoseButton;
    private AppCompatButton mLetsgoButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.key_select);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ContextCompat.getColor(this,R.color.colorAccent));
        }
        mProgressDialog=new ProgressDialog(this);
        mSecKeyEditText=(TextView) findViewById(R.id.sec_key_edit_text);
        mBrwoseButton=(AppCompatButton)findViewById(R.id.button_select_secret_key);
        mLetsgoButton=(AppCompatButton)findViewById(R.id.button_letsgo_keys_select);
        mBrwoseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBrowseButtonClick(view);
            }
        });
        mLetsgoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onImportKeys(view);
            }
        });

    }
    @ActionHandler(layoutResource = R.id.button_letsgo_keys_select)
    public void onImportKeys(View view) {
        new KeysSetupTask().execute();

    }
    public void onBrowseButtonClick(View view){

           if(view.getId()==R.id.button_select_secret_key){
                startFileBrowsing(GET_SECRET_KEY_CODE);
            }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        SharedData.STARTED_IN_SELECTION_MODE=false;
        Log.d(TAG, "onActivityResult: Got the result");
        if(resultCode!=RESULT_OK){
            return;
        }
        String filename=data.getExtras().getString("filename");
        switch (requestCode){

            case GET_SECRET_KEY_CODE:{
                Log.d(TAG, "onActivityResult: Got the result back i.e secret key");
                this.mSecKeyEditText.setText("filename: "+filename);
                this.mSecretKeyFilename=filename;
                break;
            }
        }

    }

    private void startFileBrowsing(int requestCode){
        Intent intent=new Intent(this, FileSelectionActivity.class);
        intent.putExtra("select",true);
        startActivityForResult(intent,requestCode);
    }

    class KeysSetupTask extends AsyncTask<Void,String,Boolean>{

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                PGPSecretKey key=MyPGPUtil.readSecretKey(mSecretKeyFilename);
                // if secret key is correct get the public key from it and save it
                publishProgress("Getting public key");
                PGPPublicKey pub=key.getPublicKey();
                //output keys in ascii armored format
                File file                   = new File(getFilesDir(),"pub.asc");
                ArmoredOutputStream pubOut  = new ArmoredOutputStream(new FileOutputStream(file));
                pub.encode(pubOut);
                pubOut.close();

                publishProgress("Setting up database");
                DatabaseHandler db=new DatabaseHandler(KeySelectActivity.this,SharedData.DB_PASSWORD,true);
                ByteArrayOutputStream outputStream  = new ByteArrayOutputStream();
                ArmoredOutputStream secOut          = new ArmoredOutputStream(outputStream);
                key.encode(secOut);
                secOut.close();

                byte[] test=outputStream.toByteArray();
                //call the db methods to store
                db.insertSecKey(SharedData.USERNAME,test);
                //save shared preferences
                SharedPreferences prefs=getSharedPreferences("done", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor=prefs.edit();
                editor.putBoolean("keys_gen",true);
                editor.apply();
                editor.commit();
                return true;

            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            mProgressDialog.dismiss();
            if(aBoolean){
                Toast.makeText(
                        KeySelectActivity.this,
                        "Successfully generated keys. Now you can encrypt files",
                        Toast.LENGTH_LONG).show();
                //start the unlock db activity
                SharedData.KEYS_GENERATED=true;
                setResult(RESULT_OK);
                finish();
            }else{
                Toast.makeText(
                        KeySelectActivity.this,
                        "Cannot read secret key, make sure you have choose the right file",
                        Toast.LENGTH_LONG
                ).show();
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog.setTitle("Generating keys");
            mProgressDialog.setMessage("Pleas wait it can take a while");
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();
        }
    }

}
