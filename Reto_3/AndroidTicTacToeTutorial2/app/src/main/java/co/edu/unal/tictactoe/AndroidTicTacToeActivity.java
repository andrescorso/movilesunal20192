package co.edu.unal.tictactoe;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
//import android.util.Log;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewManager;
import android.widget.Button;
import android.widget.TextView;

public class AndroidTicTacToeActivity extends Activity {
    // Represents the internal state of the game
    private TicTacToeGame mGame;
    // Buttons making up the board
    private Button mBoardButtons[];
    private Button mNewGameButton;

    // Various text displayed
    private TextView mInfoTextView;
    private TextView mScoreHTextView;
    private TextView mScoreTTextView;
    private TextView mScoreCTextView;

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


        mNewGameButton = (Button) findViewById(R.id.newgame_b);

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

        // Reset all buttons
        for (int i = 0; i < mBoardButtons.length; i++) {
            mBoardButtons[i].setText("");
            mBoardButtons[i].setEnabled(true);
            mBoardButtons[i].setOnClickListener(new ButtonClickListener(i));
        }
        mInfoTextView.setTextColor(Color.rgb(0, 0, 0));
        //Log.d("check", "HUMANSTART:"+humanstart);
        if (!humanstart) {
            mInfoTextView.setText(R.string.first_computer);
            int move = mGame.getComputerMove();
            setMove(TicTacToeGame.COMPUTER_PLAYER, move);

        }else {
            // Human goes first
            mInfoTextView.setText("You go first");
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
                if (winner == 0)
                    mInfoTextView.setText(R.string.turn_human);
                else if (winner == 1) {
                    mNewGameButton.setEnabled(true);
                    mGame.setScore(1);
                    mInfoTextView.setText(R.string.result_tie);
                    mInfoTextView.setTextColor(Color.rgb(0, 0, 200));
                    int[] scores = mGame.getScore();
                    mScoreHTextView.setText(String.valueOf(scores[0]));
                    mScoreTTextView.setText(String.valueOf(scores[1]));
                    mScoreCTextView.setText(String.valueOf(scores[2]));
                    gameover = true;
                }else if (winner == 2) {
                    mNewGameButton.setEnabled(true);
                    mGame.setScore(0);
                    mInfoTextView.setText(R.string.result_human_wins);
                    mInfoTextView.setTextColor(Color.rgb(0, 200, 0));
                    int[] scores = mGame.getScore();
                    mScoreHTextView.setText(String.valueOf(scores[0]));
                    mScoreTTextView.setText(String.valueOf(scores[1]));
                    mScoreCTextView.setText(String.valueOf(scores[2]));
                    gameover = true;
                }else {
                    mNewGameButton.setEnabled(true);
                    mGame.setScore(2);
                    mInfoTextView.setText(R.string.result_computer_wins);
                    mInfoTextView.setTextColor(Color.rgb(200, 0, 0));
                    int[] scores = mGame.getScore();
                    mScoreHTextView.setText(String.valueOf(scores[0]));
                    mScoreTTextView.setText(String.valueOf(scores[1]));
                    mScoreCTextView.setText(String.valueOf(scores[2]));
                    gameover = true;
                }
            }
        }
    }
    private void setMove(char player, int location) {
        mGame.setMove(player, location);
        mBoardButtons[location].setEnabled(false);
        mBoardButtons[location].setText(String.valueOf(player));
        if (player == TicTacToeGame.HUMAN_PLAYER)
            mBoardButtons[location].setTextColor(Color.rgb(0, 200, 0));
        else
            mBoardButtons[location].setTextColor(Color.rgb(200, 0, 0));
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
}
