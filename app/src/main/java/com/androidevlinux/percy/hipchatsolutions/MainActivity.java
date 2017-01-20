package com.androidevlinux.percy.hipchatsolutions;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONObject;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    EditText editText;
    TextView textView;
    MyAsyncTask myAsyncTask;
    String yourString;
    JSONObject jsonObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Fabric.with(this, new Crashlytics());
        editText = (EditText)findViewById(R.id.editText);
        textView = (TextView)findViewById(R.id.outputtextview);

        Button button = (Button)findViewById(R.id.button);
        button.setOnClickListener(this);

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
                case R.id.button:
                    myAsyncTask = new MyAsyncTask();
                    myAsyncTask.execute();
                break;
                default:
                break;
        }
    }

    class MyAsyncTask extends AsyncTask<Void, Integer, Void> {

        boolean running;
        ProgressDialog progressDialog;

        @Override
        protected Void doInBackground(Void... params) {
            jsonObject = new JSONObject();
            try {
                if (yourString!=null&& !yourString.isEmpty()) {
                    ArrayList<String> mentionlist = new ArrayList<String>();
                    ArrayList<String> emoticonslist = new ArrayList<String>();
                    ArrayList<String> urllist = new ArrayList<String>();

                    Matcher mentionmatcher = Pattern.compile("@\\s*(\\w+)").matcher(yourString);
                    Matcher emoticonsmatcher = Pattern.compile("\\((.*?)\\)").matcher(yourString);
                    String regex = "\\b(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
                    Matcher urlsmatcher = Pattern.compile(regex).matcher(yourString);


                    while (mentionmatcher.find()) {
                        mentionlist.add(mentionmatcher.group().substring(1));
                    }
                    while (emoticonsmatcher.find()) {
                        emoticonslist.add(emoticonsmatcher.group().substring(1, emoticonsmatcher.group().length() - 1));
                    }
                    while (urlsmatcher.find()) {
                        urllist.add(urlsmatcher.group());
                        System.out.println(urlsmatcher.group());
                    }

                    jsonObject.put("mentions", mentionlist);
                    jsonObject.put("emoticons", emoticonslist);
                    JSONObject xyz = new JSONObject();
                    xyz.put("url", urllist);

                    jsonObject.put("links", xyz);
                    System.out.println(jsonObject.toString());
                    System.out.println(jsonObject.get("mentions"));
                    System.out.println(jsonObject.get("emoticons"));
                    System.out.println(jsonObject.get("links"));

                    InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);


                }
            } catch (Exception e) {
                Crashlytics.logException(new RuntimeException(e.getMessage()));
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            progressDialog.setMessage(String.valueOf(values[0]));
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            running = true;
            yourString = editText.getText().toString();
            progressDialog = ProgressDialog.show(MainActivity.this,
                    "Converting String",
                    "Wait!");

            progressDialog.setCanceledOnTouchOutside(true);
            progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    running = false;
                }
            });

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            textView.setText(jsonObject.toString());
            progressDialog.dismiss();
        }

    }
}
