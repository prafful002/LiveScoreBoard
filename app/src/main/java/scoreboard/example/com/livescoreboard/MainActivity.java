package scoreboard.example.com.livescoreboard;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button updateScore = (Button) findViewById(R.id.buttonUpdate);
        Button viewScore = (Button) findViewById(R.id.buttonView);
        updateScore.setOnClickListener(this);
        viewScore.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        Intent i = null;


        switch (v.getId()) {

            case R.id.buttonUpdate:
                i = new Intent(MainActivity.this, UpdateActivity.class);
                break;

            case R.id.buttonView:
                i = new Intent(MainActivity.this, ViewScoreActivity.class);
                break;
        }
        startActivity(i);


    }
}
