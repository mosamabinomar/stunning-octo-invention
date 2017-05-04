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

package com.osama.cryptofmroot.filemanager.listview;


import android.graphics.drawable.Drawable;
import android.util.Log;

import com.osama.cryptofmroot.CryptoFM;
import com.osama.cryptofmroot.R;
import com.osama.cryptofmroot.filemanager.utils.MimeType;
import com.osama.cryptofmroot.utils.FileUtils;

import java.io.File;

/**
 * Created by tripleheader on 12/17/16.
 * Data model for the recyclerview
 */

public class DataModelFiles  {
    private String      fileName;
    private String      fileExtensionOrItems;
    private String fileDate;
    private Drawable    fileIcon;

    private Boolean     isSelected   = false;
    private Boolean     isFile       = false;
    private boolean     isEncrypted  = false;
    private String      mFilePath;

    public DataModelFiles(){}
    public DataModelFiles(File file) {
        this.fileName   = file.getName();
        this.mFilePath  = file.getPath();
        //check if i can read file
        if(FileUtils.isFile(file)){
            Log.d("rootF", "DataModelFiles: file name is: "+file.getName());
            //this.fileIcon=CryptoFM.getContext().getDrawable(R.drawable.ic_insert_drive);
            this.fileExtensionOrItems=FileUtils.getExtension(file.getName());
            this.fileIcon=MimeType.getIcon(fileExtensionOrItems);
            long size=FileUtils.getFileSize(file);
            this.fileDate =FileUtils.getReadableSize(size);
            this.isEncrypted=FileUtils.isEncryptedFile(file.getName());
            this.isFile=true;
        }else {
            this.fileIcon = CryptoFM.getContext().getDrawable(R.drawable.ic_default_folder);
            //in case of folder file extension will be number of items in folder
            this.fileExtensionOrItems = FileUtils.getNumberOfFiles(file) + " items";
            this.isEncrypted = false;
            this.fileDate = FileUtils.getLastModifiedDate(file);
        }
    }


    public String getFileName() {
        return fileName;
    }

    public Drawable getFileEncryptionStatus() {
        if(isEncrypted){
            return CryptoFM.getContext().getDrawable(R.drawable.ic_encrypt);
        }else{
            return null;
        }
    }

    public String getFileExtension() {
        return fileExtensionOrItems;
    }

    public String getFileDate() {
        return fileDate;
    }
    public Drawable getFileIcon(){
        return this.fileIcon;
    }
    public void setFileIcon(Drawable drawable){
        this.fileIcon=drawable;
    }

    public Boolean getSelected() {
        return isSelected;
    }

    public void setSelected(Boolean selected) {
        isSelected = selected;
    }
    public String getFilePath(){
        return mFilePath;
    }

    public Boolean getFile() {
        return isFile;
    }

    public void setmFilePath(String mFilePath) {
        this.mFilePath = mFilePath;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setFileExtensionOrItems(String fileExtensionOrItems) {
        this.fileExtensionOrItems = fileExtensionOrItems;
    }

    public void setFileDate(String fileDate) {
        this.fileDate = fileDate;
    }

    public void setFile(Boolean file) {
        isFile = file;
    }

    public void setEncrypted(boolean encrypted) {
        isEncrypted = encrypted;
    }
}