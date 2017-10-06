package io.github.harsh8398.scarnesdice;

import android.os.CountDownTimer;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private int userOverallScore = 0;
    private int computerOverallScore = 0;
    private int userTurnScore = 0;
    private int computerTurnScore = 0;
    private Random random = new Random();

    Handler timerHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button rollButton = (Button) findViewById(R.id.broll);
        rollButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int diceValue = throwDice();
                if(diceValue == 1) {
                    userTurnScore = 0;
                    showScore("GLOBAL");
                    timerHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            disableButtons();
                            computerTurn();
                        }
                    },1000);
                } else {
                    userTurnScore += diceValue;
                    showScore("USER_TURN");
                }
            }
        });

        Button resetButton = (Button) findViewById(R.id.breset);
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userOverallScore = computerOverallScore = userTurnScore = computerTurnScore = 0;
                showScore("GLOBAL");
            }
        });

        Button holdButton = (Button) findViewById(R.id.bhold);
        holdButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userOverallScore += userTurnScore;
                userTurnScore = 0;
                showScore("GLOBAL");

                if(userOverallScore >= 100) {
                    resetWhenWon("USER");
                }

                timerHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        disableButtons();
                        computerTurn();
                    }
                },1000);
            }
        });
    }

    public void computerTurn() {
        disableButtons();

        int diceValue = throwDice();
        boolean runAgain = true;

        if(diceValue == 1) {
            computerTurnScore = 0;
            runAgain = false;
            showScore("COMPUTER_TURN");
        }
        else {
            computerTurnScore += diceValue;
            showScore("COMPUTER_TURN");
        }

        if(computerTurnScore >= 20) {
            computerOverallScore += computerTurnScore;
            computerTurnScore = 0;
            runAgain = false;

            if(computerOverallScore >= 100) {
                resetWhenWon("COMPUTER");
            }
        }
        if(runAgain)
        {
            // Delaying each throw of the dice of computer's turn by 1 sec
            // to update the display to reflect all the rolled values on the dice
            timerHandler.postDelayed(new Runnable() {
                @Override
                public void run()
                {
                    // Disable the Roll and Hold buttons when it's computer's turn
                    disableButtons();
                    computerTurn();
                }
            },1000);
        }
        else {
            // Enable the Roll and Hold buttons when it's user's turn
            enableButtons();
            showScore("GLOBAL");
            Toast.makeText(getApplicationContext(),"Computer's turn has ended",
                    Toast.LENGTH_SHORT).show();
        }
    }

    public void showScore(String mode) {
        TextView score = (TextView) findViewById(R.id.score_info);
        if(mode == "GLOBAL") {
            score.setText("Your score: " + userOverallScore + " Computer score: "
                    + computerOverallScore);
        } else if (mode == "USER_TURN") {
            score.setText("Your score: " + userOverallScore + " Computer score: "
                    + computerOverallScore + " Your turn score: " + userTurnScore);
        } else if (mode == "COMPUTER_TURN") {
            score.setText("Your score: " + userOverallScore + " Computer score: "
                    + computerOverallScore + " Computer turn score: " + computerTurnScore);
        }
    }

    public void resetWhenWon(String who) {
        userOverallScore = userTurnScore = computerTurnScore = computerOverallScore = 0;
        if(who == "USER") {
            Toast.makeText(getApplicationContext(),"YOU WON!",
                    Toast.LENGTH_SHORT).show();
        } else if (who == "COMPUTER") {
            Toast.makeText(getApplicationContext(),"COMPUTER WON T-T",
                    Toast.LENGTH_SHORT).show();
        } else {
            Log.d("LOGIC: ", "Atleast one should have won");
        }
    }

    public int throwDice() {
        int diceValue = random.nextInt(6) + 1;
        ImageView diceImage = (ImageView) findViewById(R.id.dice_img);
        int resID = getResources().getIdentifier("dice" + diceValue, "drawable", getPackageName());
        diceImage.setImageResource(resID);
        return diceValue;
    }

    public void disableButtons() {
        Button rollButton = (Button) findViewById(R.id.broll);
        Button holdButton = (Button) findViewById(R.id.bhold);
        rollButton.setEnabled(false);
        holdButton.setEnabled(false);
    }

    public void enableButtons() {
        Button rollButton = (Button) findViewById(R.id.broll);
        Button holdButton = (Button) findViewById(R.id.bhold);
        rollButton.setEnabled(true);
        holdButton.setEnabled(true);
    }

}
