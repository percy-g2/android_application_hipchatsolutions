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

import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    EditText editText;
    TextView textView;
    ExecuteConvertingStrings executeConvertingStrings;
    String strConvert;
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
                    if (!editText.getText().toString().isEmpty()) {
                        executeConvertingStrings = new ExecuteConvertingStrings();
                        executeConvertingStrings.execute();
                    } else {
                        editText.setError("Enter Some Text");
                    }
                break;
                default:
                break;
        }
    }

    class ExecuteConvertingStrings extends AsyncTask<Void, Integer, Void> {

        boolean running;
        ProgressDialog progressDialog;

        @Override
        protected Void doInBackground(Void... params) {

            jsonObject = new JSONObject();
            try {
                if (strConvert!=null) {
                    ArrayList<String> mentionlist = new ArrayList<String>();
                    ArrayList<String> emoticonslist = new ArrayList<String>();
                    ArrayList<String> urllist = new ArrayList<String>();
                    ArrayList<String> titlelist = new ArrayList<String>();

                    Matcher mentionmatcher = Pattern.compile("@\\s*(\\w+)").matcher(strConvert);
                    Matcher emoticonsmatcher = Pattern.compile("\\((.*?)\\)").matcher(strConvert);
                    Matcher urlsmatcher = Pattern.compile("\\b(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]").matcher(strConvert);

                    while (mentionmatcher.find()) {
                        mentionlist.add(mentionmatcher.group().substring(1));
                    }
                    while (emoticonsmatcher.find()) {
                        emoticonslist.add(emoticonsmatcher.group().substring(1, emoticonsmatcher.group().length() - 1).replaceAll("[^\\w]", ""));
                    }
                    while (urlsmatcher.find()) {
                        urllist.add(urlsmatcher.group());
                        titlelist.add(TitleExtractor.getPageTitle(urlsmatcher.group()));
                    }

                    JSONObject URL = new JSONObject();
                    JSONObject TITLE = new JSONObject();
                    jsonObject.put("mentions", mentionlist);
                    jsonObject.put("emoticons", emoticonslist);
                    URL.put("url", urllist);
                    TITLE.put("title", titlelist);
                    JSONArray linksjsonArray = new JSONArray();

                    linksjsonArray.put(URL);
                    linksjsonArray.put(TITLE);
                    jsonObject.put("links", linksjsonArray);
                    System.out.println(jsonObject.toString());

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
            strConvert = editText.getText().toString();
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
