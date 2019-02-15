package com.viedmapp.timeddiceroller;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class MainActivity extends AppCompatActivity {
    protected int MAX_DICES = 20;
    protected String DICE_KEY = "DICES_SAVE";

    int [] DICES_OPTIONS = {4, 6, 8, 10, 12, 20, 30, 100};
    String[] DICES_OPT_STR = {"4 Caras", "6 Caras", "8 Caras", "10 Caras", "12 Caras"
    , "20 Caras", "30 Caras", "100 Caras"};

    ArrayList<Dice> DICES;
    CountDownTimer countDownTimer;
    TextView timerTextView, turnTextView;
    private int _turn;
    private int countDownSeconds = 120;
    private int spinMinutes = 0;
    private int spinSeconds = 0;

    private RecyclerAdapter diceAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //CountDownTimer Init
        setNewTimer(countDownSeconds);

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

    public void setTurn(View view){

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

    public void setCountDownTimer(View view){
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_timer,(ConstraintLayout)findViewById(R.id.dialogConstraint));

        Spinner spinner = dialogView.findViewById(R.id.minuteSpinner);
        spinner.setOnItemSelectedListener(new SpinnerAdapter("minuteSpinner"));
        Spinner secondsSpinner = dialogView.findViewById(R.id.secondsSpinner);
        secondsSpinner.setOnItemSelectedListener(new SpinnerAdapter("secondsSpinner"));


        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Set timer");
        builder.setView(dialogView);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                countDownSeconds = spinMinutes * 60 + spinSeconds;
                countDownTimer.cancel();
                setNewTimer(countDownSeconds);
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void setNewTimer(int seconds){
        int countDownMillis = seconds * 1000;
        countDownTimer = new CountDownTimer(countDownMillis,100) {
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
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (!DICES.isEmpty()){
            outState.putInt("DICES_SIZE",DICES.size());
            for(int i = 0; i < DICES.size();i++){
                outState.putInt(String.format("DICE_FACE%s",String.valueOf(i)),DICES.get(i).getFACES());
                outState.putInt(String.format("DICE_VAL%s",String.valueOf(i)),DICES.get(i).getVALUE());
            }
        }

        // call superclass to save any view hierarchy
        super.onSaveInstanceState(outState);
    }

    public void onRestoreInstanceState(Bundle savedInstanceState) {
        // Always call the superclass so it can restore the view hierarchy
        super.onRestoreInstanceState(savedInstanceState);

        if (DICES.isEmpty()){
            for(int i = 0; i < savedInstanceState.getInt("DICES_SIZE");i++){
                DICES.add(new Dice(
                        savedInstanceState.getInt(String.format("DICE_FACE%s",String.valueOf(i))),
                        savedInstanceState.getInt(String.format("DICE_VAL%s",String.valueOf(i)))
                ));

            }
        }

    }


    //SPINNER LISTENER
    class SpinnerAdapter implements AdapterView.OnItemSelectedListener {
        private String spinnerId;
        SpinnerAdapter(String spinnerId) {
            this.spinnerId=spinnerId;
        }

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            if(spinnerId.equalsIgnoreCase("minuteSpinner"))
                spinMinutes = Integer.valueOf(parent.getItemAtPosition(position).toString());
            if(spinnerId.equalsIgnoreCase("secondsSpinner"))
                spinSeconds = Integer.valueOf(parent.getItemAtPosition(position).toString());
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }

}
