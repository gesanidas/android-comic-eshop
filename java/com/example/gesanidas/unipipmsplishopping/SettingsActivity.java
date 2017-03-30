package com.example.gesanidas.unipipmsplishopping;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class SettingsActivity extends AppCompatActivity
{
    SharedPreferences sharedPreferences;
    TextView textView;
    EditText radius,font,color;
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        sharedPreferences= PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        textView=(TextView)findViewById(R.id.textView5);
        textView.setText(sharedPreferences.getString("username","null"));
        radius=(EditText)findViewById(R.id.editTextRadius);
        color=(EditText)findViewById(R.id.editTextColor);
        font=(EditText)findViewById(R.id.editTextFont);
        button=(Button)findViewById(R.id.button);
        radius.setText(String.valueOf(sharedPreferences.getInt("radius",150)));
        color.setText(sharedPreferences.getString("color","purple"));
        font.setText(String.valueOf(sharedPreferences.getInt("font",12)));

        //this is the code that enables or disables the notification service
        Switch onOffSwitch = (Switch) findViewById(R.id.on_off_switch);
        onOffSwitch.setChecked(sharedPreferences.getBoolean("notification",false));
        onOffSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                SharedPreferences.Editor editor=sharedPreferences.edit();
                editor.putBoolean("notification",isChecked);
                editor.commit();
            }

        });
    }

    public void submit(View view)
    {
        SharedPreferences.Editor editor=sharedPreferences.edit();
        int fontNum=0;
        String colorName=null;
        int radiusNum=0;
        //a few checks to ensure user that try to enter invalid or edge values
        try
        {
            fontNum=Integer.valueOf(font.getText().toString());
            colorName=color.getText().toString();
            radiusNum=Integer.valueOf(radius.getText().toString());
            if (fontNum>5 && fontNum<40)
            {
                editor.putInt("font",fontNum);
                Toast.makeText(this,"Font changed!",Toast.LENGTH_LONG).show();
            }
            else
            {
                editor.putInt("font",12);
                Toast.makeText(this,"Invalid font size",Toast.LENGTH_LONG).show();
            }
            if(Color.parseColor(colorName.toUpperCase())!=0)
            {
                editor.putString("color",colorName);
                Toast.makeText(this,"Color changed!",Toast.LENGTH_LONG).show();
            }
            else
            {
                editor.putString("color",colorName);
                Toast.makeText(this,"Invalid color name ",Toast.LENGTH_LONG).show();
            }
            if (radiusNum!=0)
            {
                editor.putInt("radius",radiusNum);
                Toast.makeText(this,"Radius changed!",Toast.LENGTH_LONG).show();
            }
            else
            {
                Toast.makeText(this,"Invalid radius number",Toast.LENGTH_LONG).show();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        try
        {
            editor.commit();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }
}
