package tictactoe;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.Random;

public class TicTacToe extends JFrame {

    private static final String[][] BUTTON_NAMES = {
            new String[]{"A3", "B3", "C3"},
            new String[]{"A2", "B2", "C2"},
            new String[]{"A1", "B1", "C1"}
    };

    private static final String[] ICONS = {"X", "O", " "};

    private final JButton[][] grid = new JButton[3][3];

    private final JLabel statusLabel = new JLabel("Game is not started");
    private final JButton startResetButton = new JButton("Start");
    private final JButton player1Button = new JButton("Human");
    private final JButton player2Button = new JButton("Human");
    private final JMenu gameMenu = new JMenu("Game");
    private final JMenuItem humanHumanMenuItem = new JMenuItem("Human vs Human");
    private final JMenuItem humanRobotMenuItem = new JMenuItem("Human vs Robot");
    private final JMenuItem robotHumanMenuItem = new JMenuItem("Robot vs Human");
    private final JMenuItem robotRobotMenuItem = new JMenuItem("Robot vs Robot");
    private final JMenuItem exitMenuItem = new JMenuItem("Exit");

    private final Player[] players = {new HumanPlayer(ICONS[0]), new HumanPlayer(ICONS[1])};

    private int currentPlayer = 1;
    private boolean gameOver = false;

    public TicTacToe() {
        initializeGame();
    }

    private void initializeGame() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Tic Tac Toe");
        setLayout(new BorderLayout());
        setSize(350, 395);

        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);

        startResetButton.addActionListener(e -> startResetGame());
        startResetButton.setFont(StyleSheet.FONT_ARIAL_BOLD_12);
        startResetButton.setName("ButtonStartReset");

        player1Button.addActionListener(e -> togglePlayerType(player1Button, 0));
        player1Button.setName("ButtonPlayer1");
        player1Button.setFont(StyleSheet.FONT_ARIAL_BOLD_12);

        player2Button.addActionListener(e -> togglePlayerType(player2Button, 1));
        player2Button.setName("ButtonPlayer2");
        player2Button.setFont(StyleSheet.FONT_ARIAL_BOLD_12);

        statusLabel.setFont(StyleSheet.FONT_ARIAL_BOLD_12);
        statusLabel.setName("LabelStatus");

        humanHumanMenuItem.setName("MenuHumanHuman");
        humanHumanMenuItem.setMnemonic(KeyEvent.VK_H);
        humanHumanMenuItem.addActionListener(e -> startQuickGame("Robot", "Robot"));

        humanRobotMenuItem.setName("MenuHumanRobot");
        humanRobotMenuItem.setMnemonic(KeyEvent.VK_R);
        humanRobotMenuItem.addActionListener(e -> startQuickGame("Robot", "Human"));

        robotHumanMenuItem.setName("MenuRobotHuman");
        robotHumanMenuItem.setMnemonic(KeyEvent.VK_U);
        robotHumanMenuItem.addActionListener(e -> startQuickGame("Human", "Robot"));

        robotRobotMenuItem.setName("MenuRobotRobot");
        robotRobotMenuItem.setMnemonic(KeyEvent.VK_O);
        robotRobotMenuItem.addActionListener(e -> startQuickGame("Human", "Human"));

        exitMenuItem.setName("MenuExit");
        exitMenuItem.setMnemonic(KeyEvent.VK_X);
        exitMenuItem.addActionListener(e -> System.exit(0));

        gameMenu.setName("MenuGame");
        gameMenu.add(humanHumanMenuItem);
        gameMenu.add(humanRobotMenuItem);
        gameMenu.add(robotHumanMenuItem);
        gameMenu.add(robotRobotMenuItem);
        gameMenu.addSeparator();
        gameMenu.add(exitMenuItem);

        Panel statusPanel = new Panel();
        statusPanel.setSize(300, 30);
        statusPanel.setPreferredSize(new Dimension(300, 30));
        statusPanel.setLayout(new GridLayout(1, 1));
        statusPanel.add(statusLabel);

        Panel gameControlPanel = new Panel();
        gameControlPanel.setSize(300, 35);
        gameControlPanel.setPreferredSize(new Dimension(300, 40));
        gameControlPanel.setLayout(new GridLayout(1, 3));
        gameControlPanel.add(player1Button);
        gameControlPanel.add(startResetButton);
        gameControlPanel.add(player2Button);

        Panel mainPanel = new Panel();
        mainPanel.setPreferredSize(new Dimension(300, 270));
        mainPanel.setLayout(new GridLayout(3, 3));
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 3; x++) {
                JButton button = new JButton(" ");
                grid[y][x] = button;
                button.setName("Button" + BUTTON_NAMES[y][x]);
                button.setFont(StyleSheet.FONT_ARIAL_BOLD_36);
                button.setEnabled(false);
                button.addActionListener(e -> endTurn((JButton) e.getSource()));
                mainPanel.add(button);
                button.setFocusPainted(false);
            }
        }

        menuBar.add(gameMenu);
        getContentPane().add(gameControlPanel, BorderLayout.NORTH);
        getContentPane().add(mainPanel, BorderLayout.CENTER);
        getContentPane().add(statusPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    private void startQuickGame(String player1Type, String player2Type) {
        player1Button.setText(player1Type);
        togglePlayerType(player1Button, 0);
        player2Button.setText(player2Type);
        togglePlayerType(player2Button, 1);
        startResetButton.setText("Start");
        startResetGame();
    }

    private void nextPlayer() {
        currentPlayer = (currentPlayer + 1) % 2;
        statusLabel.setText(String.format("The turn of %s Player (%s)", (players[currentPlayer] instanceof HumanPlayer ? "Human" : "Robot"), players[currentPlayer].getIcon()));
        players[currentPlayer].makeMove(this);
    }

    private void endTurn(JButton button) {
        if (!gameOver && button.getText().equals(" ")) {
            button.setText(players[currentPlayer].getIcon());
            gameOver = checkForGameOver();
            if (!gameOver) {
                nextPlayer();
            }
        }
    }

    private void togglePlayerType(JButton playerTypeButton, int playerNumber) {
        playerTypeButton.setText(playerTypeButton.getText().equals("Human") ? "Robot" : "Human");
        if (playerTypeButton.getText().equals("Human")) {
            players[playerNumber] = new HumanPlayer(ICONS[playerNumber]);
        } else {
            players[playerNumber] = new ComputerPlayer(ICONS[playerNumber]);
        }
    }

    private void toggleGameBoardButtons(boolean enabled, boolean clearValues) {
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 3; x++) {
                if (clearValues) {
                    grid[y][x].setText(" ");
                }
                grid[y][x].setEnabled(enabled);
            }
        }
    }

    private void startResetGame() {
        toggleGameBoardButtons(startResetButton.getText().equals("Start"), true);
        if (startResetButton.getText().equals("Start")) {
            startResetButton.setText("Reset");
            player1Button.setEnabled(false);
            player2Button.setEnabled(false);
//            statusLabel.setText("Game in progress");
            gameOver = false;
            currentPlayer = 1;
            nextPlayer();
        } else {
            startResetButton.setText("Start");
            player1Button.setEnabled(true);
            player2Button.setEnabled(true);
            statusLabel.setText("Game is not started");
            gameOver = false;
            currentPlayer = 1;
        }
    }

    // To check for win assign a power of 2 to each cell on the grid (starting with the top-right corner). For example:
    // -------------------      -------------------
    // | 2^0 | 2^1 | 2^2 |      |   1 |   2 |   4 |
    // |-----------------|      |-----------------|
    // | 2^3 | 2^4 | 2^5 |  or  |   8 |  16 |  32 |
    // |-----------------|      |-----------------|
    // | 2^6 | 2^7 | 2^8 |      |  64 | 128 | 256 |
    // -------------------      -------------------
    //
    // Add up all the rows, columns, and diagonals that constitute a win (e.g. a win across the middle row sums to 56).
    // There are 8 winning combinations with values of (7, 56, 448, 73, 146, 292, 273, and 84).
    //
    // Calculate an 'X' or 'O' win by getting the sum of each 'X' and 'O' using the power of 2 value for the cell
    // the 'X' or 'O' is in. For example:
    // -------------------
    // |  X  |  O  |   X |    Sum X: 1 + 4 + 16 + 256 = 277
    // |-----------------|    Sum O: 2 + 8 + 128 = 138
    // |  O  |  X  |     |
    // |-----------------|
    // |     |  O  |  X  |
    // -------------------
    //
    // Do a bit-wise AND (&) with each of the sums to each winning combination.  If the AND result is equal to the
    // winning combination, then a win was found.  In the example above:
    // When a bit-wise AND is done with X's value of 277 and the diagonal value of 273 the result is 273:
    //
    //         000100010101 = 277
    //       & 000100010001 = 273
    //         ------------------
    //         000100010001 = 273
    //
    private boolean checkForGameOver() {
        int occupiedCellCount = 0;
        int[] wins = {7, 56, 448, 73, 146, 292, 273, 84};
        int sumX = 0;
        int sumO = 0;
        int index = 0;
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 3; x++) {
                if (!grid[y][x].getText().equals(" ")) {
                    occupiedCellCount++;
                    if (grid[y][x].getText().equals("X")) {
                        sumX += (int) Math.pow(2, index);
                    } else {
                        sumO += (int) Math.pow(2, index);
                    }
                }
                index++;
            }
        }
        for (int win : wins) {
            if ((win & sumX) == win) {
                statusLabel.setText(String.format("The %s Player (X) wins", (players[0] instanceof HumanPlayer ? "Human" : "Robot")));
                toggleGameBoardButtons(false, false);
                return true;
            }
            if ((win & sumO) == win) {
                statusLabel.setText(String.format("The %s Player (O) wins", (players[1] instanceof HumanPlayer ? "Human" : "Robot")));
                toggleGameBoardButtons(false, false);
                return true;
            }
        }
        if (occupiedCellCount == 9) {
            statusLabel.setText("Draw");
            toggleGameBoardButtons(false, false);
            return true;
        }
        return false;
    }

    interface Player {
        void makeMove(TicTacToe game);

        String getIcon();
    }

    static abstract class AbstractPlayer implements Player {
        private final String icon;

        AbstractPlayer(String icon) {
            this.icon = icon;
        }

        public String getIcon() {
            return icon;
        }
    }

    static class HumanPlayer extends AbstractPlayer {
        HumanPlayer(String icon) {
            super(icon);
        }

        @Override
        public void makeMove(TicTacToe game) {

        }
    }

    static class ComputerPlayer extends AbstractPlayer {
        ComputerPlayer(String icon) {
            super(icon);
        }

        @Override
        public void makeMove(TicTacToe game) {
            Random rng = new Random();
            int row;
            int col;

            do {
                row = rng.nextInt(3);
                col = rng.nextInt(3);
            } while (!game.grid[row][col].getText().equals(" "));
            game.endTurn(game.grid[row][col]);
        }
    }
}
