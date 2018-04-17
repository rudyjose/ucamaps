package zero.ucamaps.tts;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import java.util.Locale;
import java.util.TreeMap;

public class TTSManager {

    private TextToSpeech mTts = null;
    private boolean isLoaded = false;

    public void init(Context context) {
        try {
            mTts = new TextToSpeech(context, onInitListener);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Initialize the speech parameters for downloading the spanish language
    private TextToSpeech.OnInitListener onInitListener = new TextToSpeech.OnInitListener() {
        Locale locSpanish = new Locale ("spa","US");

        @Override
        public void onInit(int status) {
            if (status == TextToSpeech.SUCCESS) {
                int result = mTts.setLanguage(locSpanish);
                isLoaded = true;

                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Log.e("error", "This Language is not supported");
                }
            } else {
                Log.e("error", "Initialization Failed!");
            }
        }
    };

    //Turns off the ttsmanager
    public void shutDown() {
        mTts.shutdown();
    }

    //Multiple line reading
    public void addQueue(String text) {
        if (isLoaded)
            mTts.speak(text, TextToSpeech.QUEUE_ADD, null);
        else
            Log.e("error", "TTS Not Initialized");
    }

    //One line reading
    public void initQueue(String text) {


        if (isLoaded){
            mTts.speak(replaceRomanNumber(text), TextToSpeech.QUEUE_FLUSH, null);
        }
        else
            Log.e("error", "TTS Not Initialized");
    }

    public String replaceRomanNumber(String text){

            if(text.equalsIgnoreCase("Magna I")){
              text = "Magna 1";
            }
        if(text.equalsIgnoreCase("Magna II")){
            text = "Magna 2";
        }
        if(text.equalsIgnoreCase("Magna III")){
            text = "Magna 3";
        }
        if(text.equalsIgnoreCase("Magna IV")){
            text = "Magna 4";
        }
        if(text.equalsIgnoreCase("Magna V")){
            text = "Magna 5";
        }
        if(text.equalsIgnoreCase("Magna VI")){
            text = "Magna 6";
        }

        return text;
    }
}