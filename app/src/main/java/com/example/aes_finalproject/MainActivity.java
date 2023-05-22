package com.example.aes_finalproject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

public class MainActivity extends AppCompatActivity {

    private static final int READ_REQUEST_CODE = 1;
    private static final int WRITE_REQUEST_CODE = 2;
    private EditText normalText;
    private TextView fileNameView, cipherText;
    private MediaPlayer clickSound;

    public MainActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        normalText = findViewById(R.id.normalText);
        fileNameView = findViewById(R.id.fileName);
        cipherText = findViewById(R.id.cipherText);
        clickSound = MediaPlayer.create(this, R.raw.click);

        Button openFileButton = findViewById(R.id.uploadButton);
        openFileButton.setOnClickListener(view -> {
            cipherText.setText("");
            normalText.setText("");
            clickSound.start();
            performReadRequest();
        });


        Button downloadButton = findViewById(R.id.saveButton);
        downloadButton.setOnClickListener(view -> {
            clickSound.start();
            String text = cipherText.getText().toString();
            if (text.isEmpty()) {
                Toast.makeText(getApplicationContext(), "Text is empty", Toast.LENGTH_SHORT).show();
            }
            else {
                performWriteRequest();
            }
        });

        Button encrypt_btn = findViewById(R.id.encryptButton);
        encrypt_btn.setOnClickListener(v -> {
            clickSound.start();
            String plaintext = normalText.getText().toString();
            try {
                String ciphertext = AES_Crypt.encrypt(plaintext);
                cipherText.setText(ciphertext);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        Button decrypt_btn = findViewById(R.id.decryptButton);
        decrypt_btn.setOnClickListener(v -> {
            clickSound.start();
            String plaintext = normalText.getText().toString();
            try {
                String ciphertext = AES_Crypt.decrypt(plaintext);
                cipherText.setText(ciphertext);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        Button copyButton = findViewById(R.id.copyButton);
        copyButton.setOnClickListener(view -> {
            clickSound.start();

            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("cipherText",cipherText.getText().toString());
            clipboard.setPrimaryClip(clip);
            Toast.makeText(getApplicationContext(), "Text copied to clipboard", Toast.LENGTH_SHORT).show();

        });

        Button delete = findViewById(R.id.deleteButton);
        delete.setOnClickListener(view -> {
            clickSound.start();
            fileNameView.setText("");
            normalText.setText("");
            cipherText.setText("");
            Toast.makeText(getApplicationContext(), "Item deleted", Toast.LENGTH_SHORT).show();
        });
    }
    // to read and get file or document
    private void performReadRequest() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/plain");
            startActivityForResult(intent, READ_REQUEST_CODE);
            cipherText.setText("");
    }
        // to save or write files

        private void performWriteRequest() {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_STREAM, String.valueOf(cipherText));
        intent.putExtra(Intent.EXTRA_TITLE, cipherText.getText().toString());

            startActivityForResult(intent, WRITE_REQUEST_CODE);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        super.onActivityResult(requestCode, resultCode, resultData);

        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        Uri mUri;
        switch (requestCode) {
            case READ_REQUEST_CODE:
                if (resultData != null) {
                    mUri = resultData.getData();
                    String content = readTextFromUri(mUri);
                    normalText.setText(content);

                    // Get the file name from the URI
                    String fileName = getFileName(mUri);
                    fileNameView.setText("File Name: " + fileName);

                    Toast.makeText(getApplicationContext(), "File Uploaded", Toast.LENGTH_SHORT).show();
                }
                break;

            case WRITE_REQUEST_CODE:
                if (resultData != null) {
                    Uri uri = resultData.getData();
                    try {
                        OutputStream outputStream = getContentResolver().openOutputStream(uri);
                        if (outputStream != null) {
                            String inputText =cipherText.getText().toString();
                            outputStream.write(inputText.getBytes());
                            outputStream.close();
                            Toast.makeText(MainActivity.this, "Saved", Toast.LENGTH_SHORT).show();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;
        }
    }

    // read files and display the content
    private String readTextFromUri(Uri uri) {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line).append(' ');
            }
            reader.close();
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }

    // get file name and display it
    @SuppressLint("Range")
    private String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            try (Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst())
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }
}