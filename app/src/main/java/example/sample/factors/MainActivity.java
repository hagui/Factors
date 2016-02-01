package example.sample.factors;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.Surface;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ProgressBar mProgressBar;
    private TextView mTextView;
    private final static String KEY_TEXT_VALUE = "state";
    private ProgressDialog mProgress;
    private Activity mActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        mActivity = this;
        // inputFilter to edit text in ordre to get only integer
        // this an example for limiting special caract
        InputFilter filter = new InputFilter() {
            public CharSequence filter(CharSequence source, int start, int end,
                                       Spanned dest, int dstart, int dend) {
                String[] specialChar = new String[]{"'", "-", "_", "@", "."};
                for (int i = start; i < end; i++) {
                    if (!(Character.isDigit(source.charAt(i)))) {
                        return "";
                    }
                }
                return null;
            }
        };

        Button calculateButton = (Button) findViewById(R.id.submit);
        // implement a filter for the editText
        final EditText numberToEnter = (EditText) findViewById(R.id.editText1);
        mTextView = (TextView) findViewById(R.id.textView00);

        //the onclicklistener of the button
        calculateButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String value = numberToEnter.getText().toString();

                    CalculateOperation calculateOperation = new CalculateOperation();
                    calculateOperation.execute(value);

            }
        });
        // to get the state after rotation
        Bundle extras;
        String newString;

        if (savedInstanceState == null) {
            extras = getIntent().getExtras();

            if (extras == null) {
                newString = null;
            } else {
                newString = extras.getString(KEY_TEXT_VALUE);
            }
        } else {
            newString = (String) savedInstanceState.getSerializable(KEY_TEXT_VALUE);
            mTextView.setText(newString);
        }
    }

    //save the value of textview
    @Override
    protected void onSaveInstanceState (Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putCharSequence(KEY_TEXT_VALUE, mTextView.getText().toString());
    }



    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            android.os.Process.killProcess(android.os.Process.myPid());
            Intent startMain = new Intent(Intent.ACTION_MAIN);
            startMain.addCategory(Intent.CATEGORY_HOME);
            startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(startMain);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // we use asyncktask because w need to do operation in background and use the progressbar
    private class CalculateOperation extends AsyncTask<String, Void, ArrayList<Float>> {

        float n ;

        @Override
        protected void onPreExecute() {
            // to block rotation in asynchtask
            int rotation = getWindowManager().getDefaultDisplay().getRotation();
            int orientation = getResources().getConfiguration().orientation;
            int deviceDefaultOrientation;

            if (((rotation == Surface.ROTATION_0 || rotation == Surface.ROTATION_180) &&
                    orientation == Configuration.ORIENTATION_LANDSCAPE)
                    || ((rotation == Surface.ROTATION_90 || rotation == Surface.ROTATION_270) &&
                    orientation == Configuration.ORIENTATION_PORTRAIT)) {
                deviceDefaultOrientation = Configuration.ORIENTATION_LANDSCAPE;
            } else {
                deviceDefaultOrientation = Configuration.ORIENTATION_PORTRAIT;
            }

            if (deviceDefaultOrientation == Configuration.ORIENTATION_PORTRAIT) {
                switch (rotation) {
                    case Surface.ROTATION_180:
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT);
                        break;
                    case Surface.ROTATION_270:
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
                        break;
                    case Surface.ROTATION_0:
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                        break;
                    case Surface.ROTATION_90:
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                        break;
                }
            } else {
                switch (rotation) {
                    case Surface.ROTATION_180:
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
                        break;
                    case Surface.ROTATION_270:
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                        break;
                    case Surface.ROTATION_0:
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                        break;
                    case Surface.ROTATION_90:
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT);
                        break;
                }
            }

            mProgress = new ProgressDialog(mActivity);
            mProgress.setMessage(getResources().getString(R.string.progress));
            mProgress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mProgress.setIndeterminate(true);
            mProgress.setProgress(0);
            mProgress.setButton(Dialog.BUTTON_NEGATIVE, getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = getIntent();
                    mActivity.finish();
                    startActivity(intent);
                }
            });
            mProgress.show();


        }

        @Override
        protected void onProgressUpdate(Void... values) {
            // the progressUpdate of my Progressdialog

        }

        @Override
        protected ArrayList<Float> doInBackground(String... params) {
            try{
                n =   Float.parseFloat(params[0]);
            } catch (Exception e){
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                     Toast.makeText(getApplicationContext(), getResources().getString(R.string.invalid_value) , Toast.LENGTH_LONG).show();
                    }
                });
            }

            List<Float> factors = new ArrayList<Float>();
            for (Float i = Float.valueOf(2); i <= n / i; i++) {
                mProgress.setProgress(Math.round(i));
                while (n % i == 0) {
                    factors.add(i);
                    n /= i;
                }
            }
            if (n > 1) {
                factors.add(n);
            }
            return (ArrayList<Float>) factors;

        }

        @Override
        protected void onPostExecute(ArrayList<Float> result) {

            mTextView.setVisibility(View.VISIBLE);
            ArrayList<Integer> list = new ArrayList<>();
            for (Float number : result ) {

                list.add(Math.round(number));
            }
            mTextView.setText(getResources().getString(R.string.prime_fac) + String.valueOf(list));
            //to free
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
            mProgress.dismiss();
        }


    }


}
