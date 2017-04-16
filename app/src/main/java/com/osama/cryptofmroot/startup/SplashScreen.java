/*
 * Copyright (c) 2017. Osama Bin Omar
 *    This file is part of Crypto File Manager also known as Crypto FM
 *
 *     Crypto File Manager is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Crypto File Manager is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with Crypto File Manager.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.osama.cryptofmroot.startup;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;


/**
 * Created by patriot on 1/14/17.
 * Splash activity which just starts another activity
 */

public class SplashScreen extends AppCompatActivity implements EasyPermissions.PermissionCallbacks{
    private static final int RC_PERMISSION=454;
    private static final String TAG=SplashScreen.class.getCanonicalName();
    String[] perms={Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private boolean permissionGranted=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //check storage permissions
        if(!EasyPermissions.hasPermissions(this,perms)){
            EasyPermissions.requestPermissions(this,"App needs access to phone storage to work.",RC_PERMISSION,perms);
        }else {
            performTask();
        }


    }

    @Override
    protected void onResume() {
        super.onResume();
        if(permissionGranted){
            performTask();
        }
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
           Log.d(TAG, "onPermissionsGranted: permission granted.");
           permissionGranted=true;
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        askPermissionAgain();
    }
    private void askPermissionAgain(){
        Toast.makeText(this,"I can't work without storage permission.",Toast.LENGTH_LONG).show();
        EasyPermissions.requestPermissions(this,"This app needs access to phone storage to work.",RC_PERMISSION,this.perms);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode==RC_PERMISSION){
            if(grantResults.length>0 && grantResults[0]== PackageManager.PERMISSION_GRANTED){
                performTask();
            }else{
                askPermissionAgain();
            }
        }
    }

    private void performTask(){
        SharedPreferences prefs=getSharedPreferences("done",Context.MODE_PRIVATE);
        if(prefs.getBoolean("done",false)){
            Intent intent = new Intent(this,UnlockDbActivity.class);
            intent.putExtra("username",prefs.getString("username","test"));
            //clear the stack
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivityForResult(intent,1);
            finish();
            return;
        }

        SharedPreferences preferences=getPreferences(Context.MODE_PRIVATE);
        boolean isFirstRun=preferences.getBoolean("run",true);
        if(isFirstRun){
            SharedPreferences.Editor editor=preferences.edit();
            editor.putBoolean("run",false);
            editor.apply();
            editor.commit();
            Intent i = new Intent(SplashScreen.this, PreStartActivity.class);
            startActivity(i);

        }else{
            Intent i = new Intent(SplashScreen.this, InitActivity.class);
            startActivity(i);
        }
        // close this activity
        finish();

    }
}
