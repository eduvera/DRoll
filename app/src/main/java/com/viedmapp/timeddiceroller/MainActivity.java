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
import android.text.InputFilter;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.speech.tts.TextToSpeech;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    protected final int MAX_DICES = 20;
    TextToSpeech tts;

    private final int [] DICES_OPTIONS = {4, 6, 8, 10, 12, 20, 30, 100};
    private final String[] DICES_OPT_STR = {"4 Caras", "6 Caras", "8 Caras", "10 Caras", "12 Caras"
    , "20 Caras", "30 Caras", "100 Caras"};

    private ArrayList<Dice> DICES;
    private CountDownTimer countDownTimer;
    private int turn;
    private int countDownSeconds = 120;
    private long countMillisLeft;
    private int spinMinutes;
    private int spinSeconds;

    private RecyclerAdapter diceAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //TextToSpeech
        tts = new TextToSpeech(this, new TextToSpeechListener());

        //Dice Array init
        DICES = new ArrayList<>();

        //RecyclerInit
        //Recycler
        RecyclerView diceRecyclerView = findViewById(R.id.recyclerView);
        diceRecyclerView.setHasFixedSize(true);

        //LayoutManager
        int columnWidth = 200;
        RecyclerView.LayoutManager diceLayoutManager = new GridAutoFitLayoutManager(this, columnWidth);
        diceRecyclerView.setLayoutManager(diceLayoutManager);

        //Adapter
        diceAdapter = new RecyclerAdapter(DICES, R.layout.dice_layout);
        diceAdapter.setOnItemClickListener(new RecyclerAdapter.ClickListener() {
            @Override
            public void onItemClick(final int position, View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setTitle("Remove dice?");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        DICES.remove(position);
                        diceAdapter.notifyDataSetChanged();
                        dialogInterface.dismiss();
                    }
                });
                builder.show();
            }
        });
        diceRecyclerView.setAdapter(diceAdapter);

        //FloatingActionButton
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(countDownTimer!=null)countDownTimer.cancel();
                setNewTimer(countDownSeconds);
                countDownTimer.start();
                turn++;
                tts.stop();
                updateTurn();
                for(int i = 0; i < DICES.size(); i++) DICES.get(i).roll();
                diceAdapter.notifyDataSetChanged();
                updateTotal();
            }
        });

        updateTurn();

    }

    private void updateTotal() {
        TextView totalView = findViewById(R.id.totalTextView);
        int total = 0;

        for(int i = 0; i < DICES.size(); i++) total += DICES.get(i).getValue();

        totalView.setText(String.format("Total: %s", String.valueOf(total)));

        tts.speak("Total" + total, TextToSpeech.QUEUE_ADD,null,null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NotNull MenuItem item) {
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
        String _second = (second<10)? "0" + second : String.valueOf(second);
        TextView timerTextView = findViewById(R.id.timerTextView);
        timerTextView.setText(String.format("%s:%s:%s", String.valueOf(minute), _second, String.valueOf(millis)));
        if (second <= 7 && millis == 9) tts.speak(String.valueOf(second), TextToSpeech.QUEUE_FLUSH, null, null);
    }

    private void updateTurn(){
        TextView turnTextView = findViewById(R.id.turnTextView);
        turnTextView.setText(String.format("Turn: %s", String.valueOf(turn)));

        tts.speak("Turn"+ turn, TextToSpeech.QUEUE_ADD,null,String.valueOf(turn));
    }

    public void setTurn(View view){
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        input.setFilters(new InputFilter[]{new InputFilter.LengthFilter(5)});
        input.setMaxWidth(5);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Set turn");
        builder.setView(input);


        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                turn = Integer.parseInt(input.getText().toString());
                updateTurn();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });

        builder.show();


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
                if(countDownTimer!=null) countDownTimer.cancel();
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
                countMillisLeft = millisUntilFinished;
                int minute = (int)TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished);
                int second = (int)TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished);
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
                outState.putInt(String.format("DICE_FACE%s",String.valueOf(i)),DICES.get(i).getFaces());
                outState.putInt(String.format("DICE_VAL%s",String.valueOf(i)),DICES.get(i).getValue());
            }
        }

        outState.putInt("TURN_VAL", turn);

        outState.putInt("COUNTDOWN_SEC", countDownSeconds);
        outState.putLong("COUNTDOWN_MILLIS_LEFT", countMillisLeft);
        // call superclass to save any view hierarchy
        super.onSaveInstanceState(outState);
    }

    public void onRestoreInstanceState(Bundle savedInstanceState) {
        // Always call the superclass so it can restore the view hierarchy
        super.onRestoreInstanceState(savedInstanceState);

        if (DICES.isEmpty()){
            for(int i = 0; i < savedInstanceState.getInt("DICES_SIZE");i++)
                DICES.add(new Dice(
                        savedInstanceState.getInt(String.format("DICE_FACE%s", String.valueOf(i))),
                        savedInstanceState.getInt(String.format("DICE_VAL%s", String.valueOf(i)))
                ));
        }

        turn = savedInstanceState.getInt("TURN_VAL");
        updateTurn();


        setNewTimer((int)TimeUnit.MILLISECONDS.toSeconds(savedInstanceState.getLong("COUNTDOWN_MILLIS_LEFT")));
        countDownTimer.start();

        countDownSeconds = savedInstanceState.getInt("COUNTDOWN_SEC");

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        tts.shutdown();
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

    class TextToSpeechListener implements android.speech.tts.TextToSpeech.OnInitListener
    {

        @Override
        public void onInit(int i) {

        }
    }

}
