package com.google.android.gms.example.interstitialexample;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.doubleclick.PublisherAdRequest;
import com.google.android.gms.ads.doubleclick.PublisherInterstitialAd;

/**
 * Main Activity. Inflates main activity xml and child fragments.
 */
public class MyActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.my, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A fragment containing the game logic.
     * Loads an interstitial at the beginning of the game, and shows between retries.
     */
    public static class GameFragment extends Fragment {
        private PublisherInterstitialAd mInterstitialAd;
        private CountDownTimer mCountDownTimer;
        private Button mRetryButton;

        public GameFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_my, container, false);
            return rootView;
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            // Initialize the game and the ad.
            super.onActivityCreated(savedInstanceState);
            initButton();
            initTimer();
            initAd();
        }

       @Override
        public void onResume() {
            // Initialize the timer if it hasn't been initialized yet.
            // Start the game.
            super.onResume();
            if (mCountDownTimer == null) {
                initTimer();
            }
            startGame();
        }

        @Override
        public void onPause() {
            // Cancel the timer if the game is paused.
            if (mCountDownTimer != null) {
                mCountDownTimer.cancel();
            }
            super.onPause();
        }
 
        private void initAd() {
            // Create the InterstitialAd and set the adUnitId.
            mInterstitialAd = new PublisherInterstitialAd(getActivity());
            // Defined in values/strings.xml
            mInterstitialAd.setAdUnitId(getString(R.string.ad_unit_id));
        }

        private void displayAd() {
            // Show the ad if it's ready. Otherwise toast and restart the game. 
            if (mInterstitialAd != null && mInterstitialAd.isLoaded()) {
                mInterstitialAd.show();
            } else {
                Toast.makeText(getActivity(), "Ad did not load", Toast.LENGTH_SHORT).show();
                startGame();
            }
        }

        private void startGame() {
            // Hide the retry button, load the ad, and start the timer.
            mRetryButton.setVisibility(View.INVISIBLE);
            PublisherAdRequest publisherAdRequest = new PublisherAdRequest.Builder().build();
            mInterstitialAd.loadAd(publisherAdRequest);
            mCountDownTimer.start();
        }

        private void initButton() {
            // Create the "retry" button, which tries to show an interstitial between game plays.
            mRetryButton = ((Button) getView().findViewById(R.id.retry_button));
            mRetryButton.setVisibility(View.INVISIBLE);
            mRetryButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    displayAd();
                }
            });
        }

        private void initTimer() {
            // Create the game timer, which counts down to the end of the level
            // and shows the "retry" button.
            final TextView textView = ((TextView) getView().findViewById(R.id.timer));
            mCountDownTimer = new CountDownTimer(4000, 1000) {
                @Override
                public void onTick(long millisUnitFinished) {
                    textView.setText("seconds remaining: " + millisUnitFinished / 1000);
                }

                @Override
                public void onFinish() {
                    textView.setText("done!");
                    mRetryButton.setVisibility(View.VISIBLE);
                }
            };
        }

    }

}
