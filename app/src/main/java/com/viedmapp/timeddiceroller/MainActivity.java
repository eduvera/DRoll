package com.viedmapp.timeddiceroller;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    int MAX_DICES = 20;
    //int MAX_DICES_VAL = 100;
    int [] DICES_OPTIONS = {4, 6, 8, 10, 12, 20, 30, 100};
    String[] DICES_OPT_STR = {"4 Caras", "6 Caras", "8 Caras", "10 Caras", "12 Caras"
    , "20 Caras", "30 Caras", "100 Caras"};

    ArrayList<Dice> DICES;
    CountDownTimer countDownTimer;
    TextView timerTextView, turnTextView;
    int _turn;

    private RecyclerAdapter diceAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //CountDownTimer Init
        countDownTimer = new CountDownTimer(63 * 1000,100) {
            @Override
            public void onTick(long millisUntilFinished) {
                int minute = (int)TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished);
                int second = (int)millisUntilFinished/1000;
                int millis = (int)millisUntilFinished/100;
                updateTimer(minute, second % 60, millis % 10);
            }

            @Override
            public void onFinish() {
                updateTimer(0,0,0);
            }
        };

        //Dice Array init
        DICES = new ArrayList<>();

        //RecyclerInit
        //Recycler
        RecyclerView diceRecyclerView = findViewById(R.id.recyclerView);
        diceRecyclerView.setHasFixedSize(true);

        //LayoutManager
        RecyclerView.LayoutManager diceLayoutManager = new GridAutoFitLayoutManager(this,200);
        diceRecyclerView.setLayoutManager(diceLayoutManager);

        //Adapter
        diceAdapter = new RecyclerAdapter(DICES, R.layout.dice_layout);
        diceRecyclerView.setAdapter(diceAdapter);

        //FloatingActionButton
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                countDownTimer.start();
                _turn++;
                updateTurn();
                for(int i = 0; i < DICES.size(); i++) DICES.get(i).roll();
                diceAdapter.notifyDataSetChanged();
                updateTotal();
            }
        });


    }

    private void updateTotal() {
        TextView totalView = findViewById(R.id.totalTextView);
        int total = 0;

        for(int i = 0; i < DICES.size(); i++) total += DICES.get(i).getVALUE();

        totalView.setText(String.format("Total: %s", String.valueOf(total)));
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void updateTimer(int minute, int second, int millis){
        String _second = (second<10)? "0" + String.valueOf(second): String.valueOf(second);
        timerTextView = findViewById(R.id.timerTextView);
        timerTextView.setText(String.format("%s:%s:%s", String.valueOf(minute), _second, String.valueOf(millis)));
    }

    private void updateTurn(){
        turnTextView = findViewById(R.id.turnTextView);
        turnTextView.setText(String.format("Turn: %s", String.valueOf(_turn)));
    }

    public void addDice(View view){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Pick a Dice");
        builder.setItems(DICES_OPT_STR, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (DICES.size() < MAX_DICES) DICES.add(new Dice(DICES_OPTIONS[which]));
                Toast.makeText(getApplicationContext(),DICES_OPT_STR[which],Toast.LENGTH_LONG).show();
                diceAdapter.notifyDataSetChanged();
            }
        });

        builder.show();
    }
}
