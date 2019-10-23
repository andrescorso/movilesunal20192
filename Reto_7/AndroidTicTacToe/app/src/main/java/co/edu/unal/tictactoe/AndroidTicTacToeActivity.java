package co.edu.unal.tictactoe;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Bundle;
//import android.util.Log;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.provider.Settings.Secure;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.core.Constants;

import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class AndroidTicTacToeActivity extends Activity {

    // Represents the internal state of the game
    private TicTacToeGame mGame;
    // Buttons making up the board
    private Button mNewGameButton;
    private Button mSettingButton;
    private Button mQuitButton;
    private Button mAboutButton;

    private Button mOnlineButton;
    private Button mJoinButton;

    // Various text displayed
    private TextView mInfoTextView;
    private TextView mScoreHTextView;
    private TextView mScoreTTextView;
    private TextView mScoreCTextView;

    private Drawable d;

    private static final int DIALOG_DIFFICULTY_ID = 0;
    private static final int DIALOG_QUIT_ID = 1;
    private static final int DIALOG_ABOUT_ID = 2;
    private static final int DIALOG_WAIT_ID = 3;
    private static final int DIALOG_JOIN_ID = 4;


    private boolean gameover;
    private boolean humanstart;
    private boolean humanturn;

    private boolean mSoundOn;

    private BoardView mBoardView;


    private MediaPlayer mHumanMediaPlayer;
    private MediaPlayer mComputerMediaPlayer;

    private MediaPlayer mTieMediaPlayer;
    private MediaPlayer mWinMediaPlayer;
    private MediaPlayer mLoseMediaPlayer;

    private SharedPreferences mPrefs;
    private int mBoardColor;


    private String idPhone;
    private String idGame;
    private DatabaseReference mDatabase;
    private DatabaseReference mDatabaseGame;
    private Dialog mDialog;
    private ChildEventListener mFirebaseListener;
    private ArrayList<String> games;
    private Boolean onlineGame;
    private Boolean isJoin;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        setContentView(R.layout.activity_main);


        idPhone = UUID.randomUUID().toString();

        //Toast.makeText(getApplicationContext(),idPhone,Toast.LENGTH_LONG).show();
        //mDatabase.child("users").child("prueba").setValue("puser");

        //setupConnection();

 /*
        mBoardButtons = new Button[TicTacToeGame.BOARD_SIZE];
        mBoardButtons[0] = (Button) findViewById(R.id.one);
        mBoardButtons[1] = (Button) findViewById(R.id.two);
        mBoardButtons[2] = (Button) findViewById(R.id.three);
        mBoardButtons[3] = (Button) findViewById(R.id.four);
        mBoardButtons[4] = (Button) findViewById(R.id.five);
        mBoardButtons[5] = (Button) findViewById(R.id.six);
        mBoardButtons[6] = (Button) findViewById(R.id.seven);
        mBoardButtons[7] = (Button) findViewById(R.id.eight);
        mBoardButtons[8] = (Button) findViewById(R.id.nine);
*/
        mInfoTextView = (TextView) findViewById(R.id.information);
        mScoreHTextView = (TextView) findViewById(R.id.scoreH);
        mScoreTTextView = (TextView) findViewById(R.id.scoreT);
        mScoreCTextView = (TextView) findViewById(R.id.scoreC);
        /*
        d = mBoardButtons[0].getBackground();
         */
        mNewGameButton = (Button) findViewById(R.id.newgame_b);
        mSettingButton = (Button) findViewById(R.id.setting_b);
        mQuitButton = (Button) findViewById(R.id.quit_b);
        mAboutButton = (Button) findViewById(R.id.about_b);

        mOnlineButton = (Button) findViewById(R.id.online_b);
        mJoinButton = (Button) findViewById(R.id.join_b);

        mBoardView = (BoardView) findViewById(R.id.board);

        // Listen for touches on the board
        mBoardView.setOnTouchListener(mTouchListener);

        mGame = new TicTacToeGame();
        mBoardView.setGame(mGame);
        humanstart = false;
        mSoundOn = true;

        // Restore the scores from the persistent preference data source
        mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        mSoundOn = mPrefs.getBoolean("sound", true);
        String difficultyLevel = mPrefs.getString("difficulty_level",
        getResources().getString(R.string.difficulty_harder));
        if (difficultyLevel.equals(getResources().getString(R.string.difficulty_easy)))
            mGame.setDifficultyLevel(TicTacToeGame.DifficultyLevel.Easy);
        else if (difficultyLevel.equals(getResources().getString(R.string.difficulty_harder)))
            mGame.setDifficultyLevel(TicTacToeGame.DifficultyLevel.Harder);
        else
            mGame.setDifficultyLevel(TicTacToeGame.DifficultyLevel.Expert);
        mBoardView.setmColor(mPrefs.getInt("board_color",Color.LTGRAY));

        mNewGameButton.setOnClickListener(new ButtonNewGame());
        mSettingButton.setOnClickListener(new ButtonSetting());
        mQuitButton.setOnClickListener(new ButtonQuit());
        mAboutButton.setOnClickListener(new ButtonAbout());

        mOnlineButton.setOnClickListener(new ButtonOnline());
        mJoinButton.setOnClickListener(new ButtonJoin());


        onlineGame =false;


        startNewGame();

    }
    // Set up the game board.
    private void startNewGame() {
        gameover = false;
        humanstart = !humanstart;


        mGame.clearBoard();
        mBoardView.invalidate();

        mNewGameButton.setEnabled(false);

        mInfoTextView.setTextColor(Color.rgb(0, 0, 0));
        //Log.d("check", "HUMANSTART:"+humanstart);
        if (!humanstart) {
            mInfoTextView.setTypeface(null, Typeface.NORMAL);
            mInfoTextView.setText(R.string.turn_computer);
            humanturn = false;
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    int move = mGame.getComputerMove();
                    setMove(TicTacToeGame.COMPUTER_PLAYER, move);
                    if(mSoundOn){mComputerMediaPlayer.start();}
                    mInfoTextView.setText(R.string.turn_human);
                    mBoardView.invalidate();
                    humanturn = true;

                }
            }, 700);
        }else {
            humanturn = true;
            // Human goes first
            mInfoTextView.setText("You go first");
            mInfoTextView.setTypeface(null, Typeface.NORMAL);
        }
    }

    private class  ButtonNewGame implements View.OnClickListener{
        public void onClick(View view){
            onlineGame =false;
            startNewGame();
        }
    }


    // Listen for touches on the board
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        public boolean onTouch(View v, MotionEvent event) {
// Determine which cell was touched

            int col = (int) event.getX() / mBoardView.getBoardCellWidth();
            int row = (int) event.getY() / mBoardView.getBoardCellHeight();
            int pos = row * 3 + col;
            //Log.i("hola","Entra"+pos+humanturn);
            if (humanturn) {
                if (!gameover && setMove(TicTacToeGame.HUMAN_PLAYER, pos)) {
                    // If no winner yet, let the computer make a move
                    //setMove(TicTacToeGame.HUMAN_PLAYER, pos);
                    // If no winner yet, let the computer make a move
                    if(mSoundOn){mHumanMediaPlayer.start();}
                    if(onlineGame) {
                        mDatabaseGame.child(String.valueOf(isJoin)).setValue(pos);
                    }
                    int winner = mGame.checkForWinner();
                    if (winner == 0) {
                        humanturn = false;
                        if(onlineGame){
                            mInfoTextView.setText(R.string.turn_other);
                        }else{
                            mInfoTextView.setText(R.string.turn_computer);
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                public void run() {
                                    int move = mGame.getComputerMove();
                                    moveComputer(move);
                                }
                            }, 700);
                        }

                        //Log.d("check", "winner:"+winner);
                    } else if (winner == 1) {
                        if(mSoundOn){mTieMediaPlayer.start();}
                        updateTextViews(1, R.string.result_tie, Color.rgb(0, 0, 200));
                    } else if (winner == 2) {
                        if(mSoundOn){mWinMediaPlayer.start();}
                        setwinner(mGame.getWinline(), TicTacToeGame.HUMAN_PLAYER_WIN);
                        updateTextViews(0, R.string.result_human_wins, Color.rgb(68, 191, 135));
                        String defaultMessage = getResources().getString(R.string.result_human_wins);
                        mInfoTextView.setText(mPrefs.getString("victory_message", defaultMessage));
                    } else {
                        if(mSoundOn){mLoseMediaPlayer.start();}
                        setwinner(mGame.getWinline(), TicTacToeGame.COMPUTER_PLAYER_WIN);
                        updateTextViews(2, R.string.result_computer_wins, Color.rgb(163, 68, 191));
                    }
                }
            }
// So we aren&#39;t notified of continued events when finger is moved
            return false;
        }
    };

    private void moveComputer(int move){
        setMove(TicTacToeGame.COMPUTER_PLAYER, move);
        if(mSoundOn){mComputerMediaPlayer.start();}
        mInfoTextView.setText(R.string.turn_human);
        mBoardView.invalidate();
        int winner = mGame.checkForWinner();
        if (winner == 0) {
            mInfoTextView.setText(R.string.turn_human);
            mInfoTextView.setTypeface(null, Typeface.NORMAL);
            humanturn = true;
        } else if (winner == 1) {
            if(mSoundOn){mTieMediaPlayer.start();}
            updateTextViews(1, R.string.result_tie, Color.rgb(0, 0, 200));
        } else if (winner == 2) {
            if(mSoundOn){mWinMediaPlayer.start();}
            setwinner(mGame.getWinline(), TicTacToeGame.HUMAN_PLAYER_WIN);
            updateTextViews(0, R.string.result_human_wins, Color.rgb(68, 191, 135));
            String defaultMessage = getResources().getString(R.string.result_human_wins);
            mInfoTextView.setText(mPrefs.getString("victory_message", defaultMessage));
        } else {
            if(mSoundOn){mLoseMediaPlayer.start();}
            setwinner(mGame.getWinline(), TicTacToeGame.COMPUTER_PLAYER_WIN);
            updateTextViews(2, R.string.result_computer_wins, Color.rgb(163, 68, 191));
        }
    }
    private void updateTextViews(int winner,int result,int color){
        mNewGameButton.setEnabled(true);
        mOnlineButton.setEnabled(true);
        mJoinButton.setEnabled(true);
        mGame.setScore(winner);
        mInfoTextView.setText(result);
        mInfoTextView.setTypeface(null, Typeface.BOLD);
        mInfoTextView.setTextColor(color);
        int[] scores = mGame.getScore();
        mScoreHTextView.setText(String.valueOf(scores[0]));
        mScoreTTextView.setText(String.valueOf(scores[1]));
        mScoreCTextView.setText(String.valueOf(scores[2]));
        gameover = true;
        if (mDatabaseGame != null) {
            mDatabaseGame.removeEventListener(mFirebaseListener);
        }

    }
    private boolean setMove(char player, int location) {
        if (mGame.setMove(player, location)) {
            mBoardView.invalidate(); // Redraw the board
            return true;
        }
        return false;
    }

    /*
    private void setMove(char player, int location) {
        mGame.setMove(player, location);
        mBoardButtons[location].setEnabled(false);
        mBoardButtons[location].setText(String.valueOf(player));
        if (player == TicTacToeGame.HUMAN_PLAYER)
            mBoardButtons[location].setTextColor(Color.rgb(255, 201, 0));
        else
            mBoardButtons[location].setTextColor(Color.rgb(255, 0, 43));
    }

     */
    private void setwinner(int line,char winner) {
        switch (line){
            case 1:
                setline(0,1,2, winner);
                break;
            case 2:
                setline(3,4,5, winner);
                break;
            case 3:
                setline(6,7,8, winner);
                break;
            case 4:
                setline(0,3,6, winner);
                break;
            case 5:
                setline(1,4,7, winner);
                break;
            case 6:
                setline(2,5,8, winner);
                break;
            case 7:
                setline(0,4,8, winner);
                break;
            case 8:
                setline(2,4,6, winner);
                break;
        }

    }

    private void setline(int a,int b,int c,char winner){
        setMove(winner,a);
        setMove(winner,b);
        setMove(winner,c);
    }
    /*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add("New Game");
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        startNewGame();
        return true;
    }
    */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Log.i("hola","Entro");
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
        return true;
    }

    private class ButtonSetting implements View.OnClickListener{
        public void onClick(View view){
            Intent mSetting = new Intent(getApplicationContext(), Settings.class);

            startActivityForResult(mSetting,0);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RESULT_CANCELED) {
// Apply potentially new settings
            mSoundOn = mPrefs.getBoolean("sound", true);
            String difficultyLevel = mPrefs.getString("difficulty_level",
            getResources().getString(R.string.difficulty_harder));
            if (difficultyLevel.equals(getResources().getString(R.string.difficulty_easy)))
                mGame.setDifficultyLevel(TicTacToeGame.DifficultyLevel.Easy);
            else if (difficultyLevel.equals(getResources().getString(R.string.difficulty_harder)))
                mGame.setDifficultyLevel(TicTacToeGame.DifficultyLevel.Harder);
            else
                mGame.setDifficultyLevel(TicTacToeGame.DifficultyLevel.Expert);
            mBoardView.setmColor(mPrefs.getInt("board_color",Color.LTGRAY));
        }
    }

    
    private class ButtonQuit implements View.OnClickListener{
        public void onClick(View view){
            createdDialog(DIALOG_QUIT_ID).show();
        }
    }

    private class ButtonAbout implements View.OnClickListener{
        public void onClick(View view){
            createdDialog(DIALOG_ABOUT_ID).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.new_game:
                startNewGame();
                return true;
            case R.id.ai_difficulty:
                createdDialog(DIALOG_DIFFICULTY_ID).show();
                return true;
            case R.id.quit:
                createdDialog(DIALOG_QUIT_ID).show();
                return true;
        }
        return false;
    }


    protected Dialog createdDialog(int id) {
        Dialog dialog = null;
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        switch (id) {
            case DIALOG_DIFFICULTY_ID:
                builder.setTitle(R.string.difficulty_choose);
                final CharSequence[] levels = {
                        getResources().getString(R.string.difficulty_easy),
                        getResources().getString(R.string.difficulty_harder),
                        getResources().getString(R.string.difficulty_expert)};

// selected is the radio button that should be selected.
                int selected = mGame.getDifficultyLevel().ordinal();

                builder.setSingleChoiceItems(levels, selected,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int item) {
                                dialog.dismiss(); // Close dialog

                                mGame.setDifficultyLevel(TicTacToeGame.DifficultyLevel.valueOf((String) levels[item]));
// Display the selected difficulty level
                                //Toast.makeText(getApplicationContext(), levels[item],
                                //        Toast.LENGTH_SHORT).show();
                                LayoutInflater inflater = getLayoutInflater();
                                View layout = inflater.inflate(R.layout.custom_toast,
                                        (ViewGroup) findViewById(R.id.custom_toast_container));

                                TextView text = (TextView) layout.findViewById(R.id.text);
                                text.setText(levels[item]);

                                Toast toast = new Toast(getApplicationContext());
                                toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                                toast.setDuration(Toast.LENGTH_SHORT);
                                toast.setView(layout);
                                toast.show();



                            }
                        });
                dialog = builder.create();

                break;
            case DIALOG_QUIT_ID:
// Create the quit confirmation dialog
                builder.setMessage(R.string.quit_question)
                        .setCancelable(false)
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                if(mDatabase !=null) {
                                    mDatabase.child("games").child(idGame).removeValue();
                                }
                                AndroidTicTacToeActivity.this.finish();
                            }
                        })
                        .setNegativeButton(R.string.no, null);
                dialog = builder.create();
                break;
            case DIALOG_ABOUT_ID:

                Context context = getApplicationContext();
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
                View layout = inflater.inflate(R.layout.about_dialog, null);
                builder.setView(layout);
                builder.setPositiveButton(R.string.ok, null);
                dialog = builder.create();
                break;
            case DIALOG_WAIT_ID:
// Create the quit confirmation dialog

                builder.setMessage(getResources().getString(R.string.wait_player)+"\nID: "+idGame)
                        .setCancelable(false)
                        .setPositiveButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                mDatabase.child("games").child(idGame).removeValue();
                            }
                        });
                dialog = builder.create();
                break;
            case DIALOG_JOIN_ID:


                builder.setTitle(R.string.choose_player);
                final CharSequence[] nGames = games.toArray(new String[games.size()]);
                builder.setItems(nGames, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                idGame = games.get(which);
                                Log.i("Game",idGame);
                                newOnlineGame(true);
                            }
                        });
                dialog = builder.create();

                break;
        }
        return dialog;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mHumanMediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.pew_pew);
        mComputerMediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.fire_bow);

        mTieMediaPlayer = MediaPlayer.create(getApplicationContext(),R.raw.tie);
        mWinMediaPlayer = MediaPlayer.create(getApplicationContext(),R.raw.win);
        mLoseMediaPlayer = MediaPlayer.create(getApplicationContext(),R.raw.lose);
    }
    @Override
    protected void onPause() {
        super.onPause();
        mHumanMediaPlayer.release();
        mComputerMediaPlayer.release();

        mTieMediaPlayer.release();
        mWinMediaPlayer.release();
        mLoseMediaPlayer.release();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mDatabase !=null) {
            mDatabase.child("games").child(idGame).removeValue();
        }
    }

    private void setupConnection() {

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference mReference = database.getReference();

        mReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("CHAT","SUCCESS!");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("CHAT","ERROR: " + databaseError.getMessage());
            }
        });
    }

    private class  ButtonOnline implements View.OnClickListener{
        public void onClick(View view){
            idGame = idPhone;
            mDatabase.child("games").child(idGame).removeValue();
            mDatabase.child("games").child(idGame).child("wait").setValue(true);

            mDatabaseGame = FirebaseDatabase.getInstance().getReference().child("games").child(idGame);
            mFirebaseListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    String key = dataSnapshot.getKey();
                    String value =String.valueOf(dataSnapshot.getValue());
                    Log.i("change0", String.valueOf(dataSnapshot));
                    Log.i("change",key+"  " +value);

                    if(key.equals("wait")){
                        if (value.equals("false")){
                            newOnlineGame(false);

                        }
                    }else if(key.equals(String.valueOf(!isJoin))){
                        moveComputer(Integer.parseInt(value));
                        humanturn = true;
                    }

                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }

            };

            mDatabaseGame.addChildEventListener(mFirebaseListener);

            mDialog=createdDialog(DIALOG_WAIT_ID);
            mDialog.show();


        }
    }
    private class  ButtonJoin implements View.OnClickListener {
        public void onClick(View view) {
            FirebaseDatabase.getInstance().getReference().child("games").addListenerForSingleValueEvent(
                    new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            //Get map of users in datasnapshot
                            games = new ArrayList<String>();
                            for (DataSnapshot dsp : dataSnapshot.getChildren()) {

                                if (String.valueOf(dsp.child("wait").getValue()).equals("true")){
                                    games.add(String.valueOf(dsp.getKey())); //add result into array list
                                }
                            }
                            createdDialog(DIALOG_JOIN_ID).show();

                        }


                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            //handle databaseError
                        }
                    });
            //mDatabase.child("games").child("bd1490f71d3a7951").child("wait").setValue(false);


        }
    }
    private void newOnlineGame(boolean join){
        onlineGame = true;
        if (join) {
            mDatabaseGame = FirebaseDatabase.getInstance().getReference().child("games").child(idGame);
            mDatabaseGame.child("wait").setValue(false);
            mDatabaseGame.child(String.valueOf(true)).setValue(-1);
            mDatabaseGame.child(String.valueOf(false)).setValue(-1);
            mFirebaseListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    String key = dataSnapshot.getKey();
                    String value =String.valueOf(dataSnapshot.getValue());
                    Log.i("change0", String.valueOf(dataSnapshot));
                    Log.i("change",key+"  " +value);

                    if(key.equals("wait")){
                        if (value.equals("false")){
                            newOnlineGame(false);

                        }
                    }else if(key.equals(String.valueOf(!isJoin))){
                        moveComputer(Integer.parseInt(value));
                        humanturn = true;
                    }

                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }

            };

            mDatabaseGame.addChildEventListener(mFirebaseListener);
        }else{
            mDialog.dismiss();
        }

        gameover = false;
        humanstart = !join;
        isJoin = join;


        mGame.clearBoard();
        mBoardView.invalidate();

        mNewGameButton.setEnabled(false);
        mOnlineButton.setEnabled(false);
        mJoinButton.setEnabled(false);

        mInfoTextView.setTextColor(Color.rgb(0, 0, 0));
        //Log.d("check", "HUMANSTART:"+humanstart);
        if (!humanstart) {
            mInfoTextView.setTypeface(null, Typeface.NORMAL);
            mInfoTextView.setText(R.string.first_other);
            humanturn = false;
        }else {
            humanturn = true;
            // Human goes first
            mInfoTextView.setText("You go first");
            mInfoTextView.setTypeface(null, Typeface.NORMAL);
        }

        //mDatabaseGame.removeEventListener(mFirebaseListener);

    }
}
