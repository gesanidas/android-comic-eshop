package com.example.gesanidas.unipipmsplishopping;

import android.content.Context;
import android.speech.tts.TextToSpeech;

import java.util.Locale;


public class TTS
{
    private TextToSpeech textToSpeech;
    protected TextToSpeech.OnInitListener initListener=new TextToSpeech.OnInitListener()
    {
        @Override
        public void onInit(int i)
        {
            if (i==TextToSpeech.SUCCESS)
            {
                textToSpeech.setLanguage(Locale.US);
            }

        }
    };

    public TTS(Context context)
    {
        textToSpeech=new TextToSpeech(context,initListener);
    }

    public void speak(String input)
    {
        textToSpeech.speak(input,TextToSpeech.QUEUE_ADD,null);
    }
}
