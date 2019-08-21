package mm.com.naingpyaehlyan.Activity;

import androidx.appcompat.app.AppCompatActivity;
import mm.com.naingpyaehlyan.R;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class ErrorActivity extends AppCompatActivity {

    private Runnable runnable;
    private Handler handler = new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_error);
        getSupportActionBar().hide();

        errorDelay();
    }
    public void errorDelay(){
        runnable = new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(ErrorActivity.this, MainActivity.class));
                overridePendingTransition(0,0);
                finish();
            }
        };
        handler.postDelayed(runnable, 5000);
    }
    public void onBackPressed(){
        super.onBackPressed();
        handler.removeCallbacks(runnable);
    }
}
