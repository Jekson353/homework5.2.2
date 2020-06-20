package com.samoylenko.homework521;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.channels.FileChannel;

public class MainActivity extends AppCompatActivity {

    private SharedPreferences myloginMethod;
    private static String LOGIN_METHOD = "login_method";
    private static String SAVE_INTERNAL_STORAGE = "InternalStorage";
    private static String SAVE_EXTERNAL_STORAGE = "ExternalStorage";
    private static String LOGIN_FILE = "login.txt";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
    }

    private void init() {
        final EditText mLoginEdTxt = findViewById(R.id.text_login);
        final EditText mPasswdEdTxt = findViewById(R.id.text_password);
        final CheckBox checkBox = findViewById(R.id.checkBox);

        Button btnLogin = findViewById(R.id.btn_login);
        Button btnReg = findViewById(R.id.btn_reg);

        myloginMethod = getSharedPreferences("loginMethod", MODE_PRIVATE);


        if (getDateFromSharedPref().equals(SAVE_INTERNAL_STORAGE)) {
            checkBox.setChecked(false);
        } else if (getDateFromSharedPref().equals(SAVE_EXTERNAL_STORAGE)) {
            checkBox.setChecked(true);
        }


        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                getSharedPreferences("loginMethod", Context.MODE_PRIVATE).edit().clear().apply();
                if (checkBox.isChecked()) {
                    SharedPreferences.Editor myEditor = myloginMethod.edit();
                    myEditor.putString(LOGIN_METHOD, SAVE_EXTERNAL_STORAGE);
                    myEditor.apply();
                    moveFile(SAVE_EXTERNAL_STORAGE);
                } else if (!checkBox.isChecked()) {
                    SharedPreferences.Editor myEditor = myloginMethod.edit();
                    myEditor.putString(LOGIN_METHOD, SAVE_INTERNAL_STORAGE);
                    myEditor.apply();
                    moveFile(SAVE_INTERNAL_STORAGE);
                }
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String login = mLoginEdTxt.getText().toString();
                String password = mPasswdEdTxt.getText().toString();
                login(login, password, getDateFromSharedPref());
            }
        });

        btnReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String login = mLoginEdTxt.getText().toString();
                String password = mPasswdEdTxt.getText().toString();
                registration(login, password, getDateFromSharedPref());
            }
        });
    }

    private String getDateFromSharedPref() {
        return myloginMethod.getString(LOGIN_METHOD, SAVE_INTERNAL_STORAGE);
    }

    public void login(String login, String passwd, String shared) {
        if (login.isEmpty() | passwd.isEmpty()) {
            Toast.makeText(MainActivity.this, R.string.no_all_value
                    , Toast.LENGTH_LONG)
                    .show();
        } else {
            String pwd = fromFile(login, shared);
            if (pwd != null) {
                if (passwd.equals(pwd)) {
                    Intent intent = new Intent(getApplicationContext(), SuccessActivity.class);
                    intent.putExtra("login", login);
                    startActivity(intent);
                    Toast.makeText(MainActivity.this, R.string.login_ok
                            , Toast.LENGTH_LONG)
                            .show();
                } else if (pwd.equals("noLogin")) {
                    Toast.makeText(MainActivity.this, R.string.no_login
                            , Toast.LENGTH_LONG)
                            .show();
                } else if (pwd.equals("0")) { //даже если файла не существует
                    Toast.makeText(MainActivity.this, R.string.no_login
                            , Toast.LENGTH_LONG)
                            .show();
                } else {
                    Toast.makeText(MainActivity.this, R.string.password_bad
                            , Toast.LENGTH_LONG)
                            .show();
                }
            } else {
                Toast.makeText(MainActivity.this, R.string.no_login
                        , Toast.LENGTH_LONG)
                        .show();
            }
        }
    }

    public void registration(String login, String passwd, String noteTxt) {
        if (login.isEmpty() | passwd.isEmpty()) {
            Toast.makeText(MainActivity.this, R.string.no_all_value
                    , Toast.LENGTH_LONG)
                    .show();
        } else {
            int result = toFile(login, passwd, noteTxt);
            if (result == 1) {
                Toast.makeText(MainActivity.this, R.string.reg_ok
                        , Toast.LENGTH_LONG)
                        .show();
            } else if (result == 2) {
                Toast.makeText(MainActivity.this, R.string.login_is_active
                        , Toast.LENGTH_SHORT)
                        .show();
            } else if (result == 3) {
                Toast.makeText(MainActivity.this, R.string.error_reg
                        , Toast.LENGTH_SHORT)
                        .show();
            } else {
                Toast.makeText(MainActivity.this, R.string.error_unknown
                        , Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }

    public int toFile(String login, String passwd, String noteTxt) {
        File file = null;
        FileOutputStream outputStream;
        if (noteTxt.equals(SAVE_INTERNAL_STORAGE)) {
            file = new File(getFilesDir(), LOGIN_FILE);
        } else if (noteTxt.equals(SAVE_EXTERNAL_STORAGE)) {
            if (isExternalStorageWritable()) {
                file = new File(getApplicationContext().getExternalFilesDir(null), LOGIN_FILE);
            } else {
                return 0;
            }
        } else {
            return 0;
        }

        if (fromFile(login, noteTxt).equals("0") | fromFile(login, noteTxt).equals("noLogin")) {
            try {
                String lineSeparator = System.getProperty("line.separator");
                outputStream = new FileOutputStream(file, true);
                outputStream.write((lineSeparator + login + "=" + passwd).getBytes());
                outputStream.close();
                return 1;
            } catch (Exception e) {
                e.printStackTrace();
                return 3;
            }
        } else {
            return 2;
        }
    }

    //перемещение файла с логинами/паролями
    public void moveFile(String to) {
        if (to.equals(SAVE_INTERNAL_STORAGE)) {
            if (isExternalStorageReadable()) {
                File source = new File(getApplicationContext().getExternalFilesDir(null), LOGIN_FILE);
                File destination = new File(getFilesDir(), LOGIN_FILE);
                try {
                    copyFile(source, destination);
                    source.delete();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(MainActivity.this, "Не удалось применить"
                        , Toast.LENGTH_SHORT)
                        .show();
            }
        } else if (to.equals(SAVE_EXTERNAL_STORAGE)) {
            if (isExternalStorageWritable()) {
                File source = new File(getFilesDir(), LOGIN_FILE);
                File destination = new File(getApplicationContext().getExternalFilesDir(null), LOGIN_FILE);
                try {
                    copyFile(source, destination);
                    source.delete();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(MainActivity.this, "Не удалось применить"
                        , Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }

    public String fromFile(String login, String noteTxt) {
        File file = null;
        String[] newLogin;
        if (noteTxt.equals(SAVE_INTERNAL_STORAGE)) {
            file = new File(getFilesDir(), LOGIN_FILE);
        } else if (noteTxt.equals(SAVE_EXTERNAL_STORAGE)) {
            if (isExternalStorageReadable()) {
                file = new File(getApplicationContext().getExternalFilesDir(null), LOGIN_FILE);
            } else {
                return null;
            }
        } else {
            return null;
        }

        try {
            InputStreamReader inputStreamReader = new InputStreamReader(new FileInputStream(file));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();

            while (line != null) {
                line = reader.readLine();
                newLogin = line.split("=");
                if (newLogin[0].equals(login)) {
                    return newLogin[1];
                }
            }
            return "noLogin";
        } catch (Exception e) {
            return "0";
        }
    }

    public void copyFile(File in, File out) throws IOException {
        if (!out.exists() && out.isFile()) {
            Toast.makeText(MainActivity.this, "файл уже существует"
                    , Toast.LENGTH_SHORT)
                    .show();
        } else {
            FileChannel source = null;
            FileChannel destination = null;
            try {
                source = new FileInputStream(in).getChannel();
                destination = new FileOutputStream(out).getChannel();
                destination.transferFrom(source, 0, source.size());
            } finally {
                if (source != null) {
                    source.close();
                }
                if (destination != null) {
                    destination.close();
                }
            }
        }
    }

    /* Проверка внутреннего хранилища на доступность записи */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /* Проверка внутреннего хранилища на доступность чтения */
    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }
}
