package co.edu.unal.tictactoe;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
//import android.util.Log;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.concurrent.TimeUnit;

public class AndroidTicTacToeActivity extends Activity {
    // Represents the internal state of the game
    private TicTacToeGame mGame;
    // Buttons making up the board
    private Button mBoardButtons[];
    private Button mNewGameButton;
    private Button mDifficultyButton;
    private Button mQuitButton;
    private Button mAbout;

    // Various text displayed
    private TextView mInfoTextView;
    private TextView mScoreHTextView;
    private TextView mScoreTTextView;
    private TextView mScoreCTextView;

    private Drawable d;

    static final int DIALOG_DIFFICULTY_ID = 0;
    static final int DIALOG_QUIT_ID = 1;
    static final int DIALOG_ABOUT_ID = 2;

    private boolean gameover;
    private boolean humanstart;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

        mInfoTextView = (TextView) findViewById(R.id.information);
        mScoreHTextView = (TextView) findViewById(R.id.scoreH);
        mScoreTTextView = (TextView) findViewById(R.id.scoreT);
        mScoreCTextView = (TextView) findViewById(R.id.scoreC);

        d = mBoardButtons[0].getBackground();
        mNewGameButton = (Button) findViewById(R.id.newgame_b);
        mDifficultyButton = (Button) findViewById(R.id.difficulty_b);
        mQuitButton = (Button) findViewById(R.id.quit_b);
        mAbout = (Button) findViewById(R.id.about_b);

        mGame = new TicTacToeGame();
        humanstart = false;
        startNewGame();

    }
    // Set up the game board.
    private void startNewGame() {
        gameover = false;
        humanstart = !humanstart;

        mGame.clearBoard();
        mNewGameButton.setEnabled(false);
        mNewGameButton.setOnClickListener(new ButtonNewGame());
        mDifficultyButton.setOnClickListener(new ButtonDifficulty());
        mQuitButton.setOnClickListener(new ButtonQuit());
        mAbout.setOnClickListener(new ButtonAbout());

        // Reset all buttons
        for (int i = 0; i < mBoardButtons.length; i++) {
            mBoardButtons[i].setText("");
            mBoardButtons[i].setEnabled(true);
            mBoardButtons[i].setOnClickListener(new ButtonClickListener(i));
            //mBoardButtons[i].setBackgroundDrawable(d);
            mBoardButtons[i].setBackgroundResource(android.R.drawable.btn_default);

        }
        mInfoTextView.setTextColor(Color.rgb(0, 0, 0));
        //Log.d("check", "HUMANSTART:"+humanstart);
        if (!humanstart) {
            mInfoTextView.setTypeface(null, Typeface.NORMAL);
            mInfoTextView.setText(R.string.first_computer);
            int move = mGame.getComputerMove();
            setMove(TicTacToeGame.COMPUTER_PLAYER, move);

        }else {
            // Human goes first
            mInfoTextView.setText("You go first");
            mInfoTextView.setTypeface(null, Typeface.NORMAL);

        }
    }

    private class  ButtonNewGame implements View.OnClickListener{
        public void onClick(View view){
            startNewGame();
        }
    }
    // Handles clicks on the game board buttons
    private class ButtonClickListener implements View.OnClickListener {
        int location;
        public ButtonClickListener(int location) {
            this.location = location;
        }
        public void onClick(View view) {
            if (mBoardButtons[location].isEnabled() && !gameover) {
                setMove(TicTacToeGame.HUMAN_PLAYER, location);
                // If no winner yet, let the computer make a move
                int winner = mGame.checkForWinner();
                if (winner == 0) {
                    mInfoTextView.setText(R.string.turn_computer);
                    int move = mGame.getComputerMove();
                    setMove(TicTacToeGame.COMPUTER_PLAYER, move);
                    winner = mGame.checkForWinner();
                    //Log.d("check", "winner:"+winner);
                }
                if (winner == 0) {
                    mInfoTextView.setText(R.string.turn_human);
                    mInfoTextView.setTypeface(null, Typeface.NORMAL);
                }else if (winner == 1) {
                    updateTextViews(1,R.string.result_tie,Color.rgb(0,0,200));
                }else if (winner == 2) {
                    setwinner(mGame.getWinline());
                    updateTextViews(0,R.string.result_human_wins,Color.rgb(68, 191, 135));
                }else {
                    setwinner(mGame.getWinline());
                    updateTextViews(2,R.string.result_computer_wins,Color.rgb(163, 68, 191));
                }
            }
        }
    }
    private void updateTextViews(int winner,int result,int color){
        mNewGameButton.setEnabled(true);
        mGame.setScore(winner);
        mInfoTextView.setText(result);
        mInfoTextView.setTypeface(null, Typeface.BOLD);
        mInfoTextView.setTextColor(color);
        int[] scores = mGame.getScore();
        mScoreHTextView.setText(String.valueOf(scores[0]));
        mScoreTTextView.setText(String.valueOf(scores[1]));
        mScoreCTextView.setText(String.valueOf(scores[2]));
        gameover = true;
    }
    private void setMove(char player, int location) {
        mGame.setMove(player, location);
        mBoardButtons[location].setEnabled(false);
        mBoardButtons[location].setText(String.valueOf(player));
        if (player == TicTacToeGame.HUMAN_PLAYER)
            mBoardButtons[location].setTextColor(Color.rgb(255, 201, 0));
        else
            mBoardButtons[location].setTextColor(Color.rgb(255, 0, 43));
    }
    private void setwinner(int line) {
        switch (line){
            case 1:
                setline(0,1,2);
                break;
            case 2:
                setline(3,4,5);
                break;
            case 3:
                setline(6,7,8);
                break;
            case 4:
                setline(0,3,6);
                break;
            case 5:
                setline(1,4,7);
                break;
            case 6:
                setline(2,5,8);
                break;
            case 7:
                setline(0,4,8);
                break;
            case 8:
                setline(2,4,6);
                break;
        }

    }
    private void setline(int a,int b,int c){
        mBoardButtons[a].setBackgroundResource(android.R.drawable.btn_star_big_off);
        mBoardButtons[b].setBackgroundResource(android.R.drawable.btn_star_big_off);
        mBoardButtons[c].setBackgroundResource(android.R.drawable.btn_star_big_off);

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
        Log.i("hola","Entro");
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
        return true;
    }

    private class ButtonDifficulty implements View.OnClickListener{
        public void onClick(View view){
            showDialog(DIALOG_DIFFICULTY_ID);
        }
    }
    private class ButtonQuit implements View.OnClickListener{
        public void onClick(View view){
            showDialog(DIALOG_QUIT_ID);
        }
    }
    private class ButtonAbout implements View.OnClickListener{
        public void onClick(View view){
            showDialog(DIALOG_ABOUT_ID);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.new_game:
                startNewGame();
                return true;
            case R.id.ai_difficulty:
                showDialog(DIALOG_DIFFICULTY_ID);
                return true;
            case R.id.quit:
                showDialog(DIALOG_QUIT_ID);
                return true;
        }
        return false;
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        Dialog dialog = null;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        switch (id) {
            case DIALOG_DIFFICULTY_ID:
                builder.setTitle(R.string.difficulty_choose);
                final CharSequence[] levels = {
                        getResources().getString(R.string.difficulty_easy),
                        getResources().getString(R.string.difficulty_harder),
                        getResources().getString(R.string.difficulty_expert)};
// TODO: Set selected, an integer (0 to n-1), for the Difficulty dialog.
// selected is the radio button that should be selected.
                int selected = mGame.getDifficultyLevel().ordinal();

                builder.setSingleChoiceItems(levels, selected,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int item) {
                                dialog.dismiss(); // Close dialog
// TODO: Set the diff level of mGame based on which item was selected.
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
        }
        return dialog;
    }

}
