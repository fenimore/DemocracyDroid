package com.workingagenda.another.freedemocracynow;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.util.Linkify;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import org.w3c.dom.Text;


public class aboutActivity extends Activity {

    /**
     * TODO:
     */

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_view);
        TextView aboutText = (TextView) findViewById(R.id.about_text);
        String s = getResources().getString(R.string.about_string);
        final SpannableString span = new SpannableString(s);
        Linkify.addLinks(span, Linkify.WEB_URLS);
        ImageButton btnGit = (ImageButton) findViewById(R.id.btnGitHub);
        btnGit.setImageResource(R.drawable.octocat);

        btnGit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse("https://github.com/ExorableLudos/democracynow-android");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
                return;
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        setResult(RESULT_OK);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //get the CurrPos and play from there
    }
}