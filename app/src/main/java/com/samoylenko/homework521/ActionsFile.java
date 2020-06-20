package com.samoylenko.homework521;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Environment;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;

@SuppressLint("Registered")
public class ActionsFile extends AppCompatActivity {

//    public static void copyFile(File in, File out, Context ctx) throws IOException {
//        if(!out.exists() && out.isFile()) {
//            Toast.makeText(ctx, "файл уже существует"
//                    , Toast.LENGTH_SHORT)
//                    .show();
//        }else{
//            FileChannel source = null;
//            FileChannel destination = null;
//            try {
//                source = new FileInputStream(in).getChannel();
//                destination = new FileOutputStream(out).getChannel();
//                destination.transferFrom(source, 0, source.size());
//            }
//            finally {
//                if(source != null) {
//                    source.close();
//                }
//                if(destination != null) {
//                    destination.close();
//                }
//            }
//        }
//    }


//    /* Проверка внутреннего хранилища на доступность записи */
//    public boolean isExternalStorageWritable() {
//        String state = Environment.getExternalStorageState();
//        if (Environment.MEDIA_MOUNTED.equals(state)) {
//            return true;
//        }
//        return false;
//    }
//
//    /* Проверка внутреннего хранилища на доступность чтения */
//    public boolean isExternalStorageReadable() {
//        String state = Environment.getExternalStorageState();
//        if (Environment.MEDIA_MOUNTED.equals(state) ||
//                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
//            return true;
//        }
//        return false;
//    }
}
