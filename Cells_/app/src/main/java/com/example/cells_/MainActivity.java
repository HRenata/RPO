package com.example.cells_;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    enum State {ORIGIN, GREEN, RED, YELLOW}
    private State nextColor = State.ORIGIN;
    private State[][] cellStates;
    private int[][] actionsIds;
    private int N;
    private int M;
    private int score;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        N = 3;
        M = 3;

        actionsIds = new int[][]{
                {R.id.button_1,
                        R.id.button_2,
                        R.id.button_3},
                {R.id.button_4,
                        R.id.button_5,
                        R.id.button_6},
                {R.id.button_7,
                        R.id.button_8,
                        R.id.button_9}
        };

        cellStates = new State[N][M];

        for (int i = 0; i < N; ++i) {
            for (int j = 0; j < M; ++j) {
                cellStates[i][j] = State.ORIGIN;
            }
        }

        score = 0;
        setScore(score);

        initializeAllCellsWithOriginalState();
        initializeStartButton();
        setCellsUnenabled();
    }

    public void initializeAllCellsWithOriginalState() {
        for (int i = 0; i < N; ++i) {
            for (int j = 0; j < M; ++j) {
                initializeCellWithState(actionsIds[i][j], State.ORIGIN);
            }
        }
        score = 0;
        setScore(score);
    }

    public void initializeCellWithState(int cellId, State state) {
        Button button = (Button) findViewById(cellId);
        button.setTextColor(Color.BLACK);

        for (int i = 0; i < N; ++i) {
            for (int j = 0; j < M; ++j) {
                if(actionsIds[i][j] == cellId) {
                    cellStates[i][j] = state;
                }
            }
        }

        switch (state) {
            case ORIGIN:
                button.setBackgroundColor(Color.BLACK);
                button.setEnabled(true);
                break;
            case RED:
                button.setBackgroundColor(Color.RED);
                button.setText("red");
                button.setEnabled(false);
                break;
            case GREEN:
                button.setBackgroundColor(Color.GREEN);
                button.setText("green");
                button.setEnabled(false);
                break;
            case YELLOW:
                button.setBackgroundColor(Color.YELLOW);
                button.setText("yellow");
                button.setEnabled(false);
                break;
        }
    }

    public void initializeStartButton() {
        Button button = (Button) findViewById(R.id.buttonNextColor);
        button.setText("START");
        button.setEnabled(true);
        button.setBackgroundColor(Color.CYAN);
    }

    public void initializeVictory() {
        Button button = (Button) findViewById(R.id.buttonNextColor);
        button.setText("YOU WON\n SCORE: " + String.valueOf(score));
        button.setEnabled(true);
        button.setBackgroundColor(Color.CYAN);
        setCellsUnenabled();
    }

    public void initializeLoss() {
        Button button = (Button) findViewById(R.id.buttonNextColor);
        button.setText("YOU LOSE\n SCORE: " + String.valueOf(score));
        button.setEnabled(true);
        button.setBackgroundColor(Color.CYAN);
        setCellsUnenabled();
    }

    public void setCellsUnenabled() {
        for (int i = 0; i < N; ++i) {
            for (int j = 0; j < M; ++j) {
                Button button = (Button) findViewById(actionsIds[i][j]);
                button.setEnabled(false);
            }
        }
    }

    public void setCellsEnabled() {
        for (int i = 0; i < N; ++i) {
            for (int j = 0; j < M; ++j) {
                Button button = (Button) findViewById(actionsIds[i][j]);
                button.setEnabled(true);
            }
        }
    }

    public void  setScore(int score) {
        TextView textView = (TextView) findViewById(R.id.textViewScore);
        textView.setText("SCORE: " + String.valueOf(score));
    }

    public void clickOnStart(View view) {
        Button button = (Button) findViewById(R.id.buttonNextColor);
        button.setText("");
        button.setEnabled(false);

        initializeAllCellsWithOriginalState();
        setCellsEnabled();
        randNextColor();
    }

    public void clickOnCell(View view) {
        int cellId;
        cellId = view.getId();

        initializeCellWithState(cellId, nextColor);
        checkFilledLine();

        int gameStatus;
        gameStatus = checkGameStatus();

        if  (gameStatus == 0) {
            randNextColor();
        }
        else if (gameStatus == 1) {
            initializeVictory();
        }
        else if (gameStatus == -1) {
            initializeLoss();
        }
    }

    public void randNextColor() {
        Button button = (Button) findViewById(R.id.buttonNextColor);
        int[] colors;
        switch (nextColor) {
            case RED:
                colors = new int[]{
                        Color.GREEN,
                        Color.YELLOW
                };
                break;
            case GREEN:
                colors = new int[]{
                        Color.RED,
                        Color.YELLOW
                };
                break;
            case YELLOW:
                colors = new int[]{
                        Color.GREEN,
                        Color.RED
                };
                break;
            default:
                colors = new int[]{
                        Color.GREEN,
                        Color.YELLOW,
                        Color.RED
                };
                break;
        }

        int id;
        id = (int) (Math.random() * colors.length);

        switch (colors[id]) {
            case Color.RED:
                nextColor = State.RED;
                break;
            case Color.GREEN:
                nextColor = State.GREEN;
                break;
            case Color.YELLOW:
                nextColor = State.YELLOW;
                break;
            default:
                nextColor = State.ORIGIN;
                break;
        }
        button.setBackgroundColor(colors[id]);
    }

    public int checkGameStatus() {
        int numOrigins = 0;
        for (int i = 0; i < N; ++i) {
            for (int j = 0; j < M; ++j) {
                if (cellStates[i][j] == State.ORIGIN) {
                    ++numOrigins;
                }
            }
        }
        if (numOrigins == N*M) {
            return 1; //Victory
        }
        else if (numOrigins == 0) {
            return -1; //Loss
        }
        return 0;
    }

    public void checkFilledLine() {
        int idFilledVerticalLine = checkVerticalFilledLine();
        int idFilledHorizontalLine = checkHorizontalFilledLine();

        if (idFilledVerticalLine != -1) {
            eraseVerticalLine(idFilledVerticalLine);
            ++score;
            setScore(score);
        }
        if (idFilledHorizontalLine != -1) {
            eraseHorizontalLine(idFilledHorizontalLine);
            ++score;
            setScore(score);
        }
    }

    public int checkHorizontalFilledLine() {
        for (int i = 0; i < N; ++i) {
            boolean isFilled = true;
            for (int j = 0; j < M - 1; ++j) {
                if (cellStates[i][j] != cellStates[i][j + 1]) {
                    isFilled = false;
                }
                else if (cellStates[i][j] == State.ORIGIN) {
                    isFilled = false;
                }
            }
            if (isFilled) {
                return i;
            }
        }
        return -1;
    }

    public int checkVerticalFilledLine() {
        for (int j = 0; j < M; ++j) {
            boolean isFilled = true;
            for (int i = 0; i < N - 1; ++i) {
                if (cellStates[i][j] != cellStates[i + 1][j]) {
                    isFilled = false;
                }
                else if (cellStates[i][j] == State.ORIGIN) {
                    isFilled = false;
                }
            }
            if (isFilled) {
                return j;
            }
        }
        return -1;
    }

    public void eraseHorizontalLine(int i) {
        for (int j = 0; j < M; ++j) {
            initializeCellWithState(actionsIds[i][j], State.ORIGIN);
        }
    }

    public void eraseVerticalLine(int j) {
        for (int i = 0; i < N; ++i) {
            initializeCellWithState(actionsIds[i][j], State.ORIGIN);
        }
    }
}