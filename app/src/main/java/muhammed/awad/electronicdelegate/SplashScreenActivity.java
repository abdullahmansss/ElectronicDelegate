package muhammed.awad.electronicdelegate;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Timer;
import java.util.TimerTask;

public class SplashScreenActivity extends AppCompatActivity
{
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        imageView = findViewById(R.id.splash);

        //bindLogo();
        imageView.setScaleX(0);
        imageView.setScaleY(0);

        imageView.animate().scaleXBy(1).scaleYBy(1).setDuration(3000);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null)
        {
            TimerTask task = new TimerTask()
            {
                @Override
                public void run()
                {
                    // go to the main activity
                    Intent i = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(i);
                    // kill current activity
                    finish();
                }
            };
            // Show splash screen for 3 seconds
            new Timer().schedule(task, 3000);
        } else
        {
            TimerTask task = new TimerTask()
            {
                @Override
                public void run()
                {
                    // go to the main activity
                    Intent i = new Intent(getApplicationContext(), Register2Activity.class);
                    startActivity(i);
                    // kill current activity
                    finish();
                }
            };
            // Show splash screen for 3 seconds
            new Timer().schedule(task, 3000);
        }
    }

    @Override
    public void onBackPressed() { }
}
