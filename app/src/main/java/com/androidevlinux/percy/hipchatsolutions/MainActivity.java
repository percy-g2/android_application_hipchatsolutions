package com.androidevlinux.percy.hipchatsolutions;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;

import java.net.JarURLConnection;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    EditText editText;
    TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText = (EditText)findViewById(R.id.editText);
        textView = (TextView)findViewById(R.id.outputtextview);

        Button button = (Button)findViewById(R.id.button);
        button.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
                case R.id.button:

                    JSONObject jsonObject = new JSONObject();
                    try {

                        ArrayList<String> mentionlist = new ArrayList<String>();
                        ArrayList<String> emoticonslist = new ArrayList<String>();
                        ArrayList<String> urllist = new ArrayList<String>();


                        String yourString = editText.getText().toString();

                        Matcher mentionmatcher = Pattern.compile("@\\s*(\\w+)").matcher(yourString);
                        Matcher emoticonsmatcher = Pattern.compile("\\((.*?)\\)").matcher(yourString);
                        String regex = "\\b(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
                        Matcher urlsmatcher = Pattern.compile(regex).matcher(yourString);


                        while (mentionmatcher.find()) {
                            mentionlist.add(mentionmatcher.group().substring(1));
                             }
                        while (emoticonsmatcher.find()) {
                            emoticonslist.add(emoticonsmatcher.group().substring(1,emoticonsmatcher.group().length()-1));
                        }
                        while (urlsmatcher.find()) {
                            urllist.add(urlsmatcher.group());
                            System.out.println(urlsmatcher.group());
                        }

                        jsonObject.put("mentions",mentionlist);
                        jsonObject.put("emoticons",emoticonslist);
                        JSONObject xyz = new JSONObject();
                        xyz.put("url",urllist);

                        jsonObject.put("links",xyz);
                        System.out.println(jsonObject.toString());
                        System.out.println(jsonObject.get("mentions"));
                        System.out.println(jsonObject.get("emoticons"));
                        System.out.println(jsonObject.get("links"));

                        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);

                        textView.setText(jsonObject.toString());

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                break;
                default:
                break;
        }
    }
}
