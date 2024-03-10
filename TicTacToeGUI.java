package game;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TicTacToeGUI extends JFrame {
    private JButton[][] buttons;
    private char[][] board;
    private char currentPlayer;
    private JLabel statusLabel;
    private boolean isSinglePlayer;
    private AIPlayer aiPlayer;
    private boolean gamePaused;

    public TicTacToeGUI(boolean isSinglePlayer) {
        this.isSinglePlayer = isSinglePlayer;

        setTitle("Tic Tac Toe");
        setSize(300, 400); // Set the size to fit an average smartphone
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(173, 216, 230)); // Light blue background

        JPanel gamePanel = new JPanel();
        gamePanel.setLayout(new GridLayout(3, 3));
        gamePanel.setBackground(new Color(255, 255, 255)); // White background
        buttons = new JButton[3][3];
        board = new char[3][3];
        currentPlayer = 'X';

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                buttons[i][j] = new JButton("");
                buttons[i][j].setFont(new Font("Arial", Font.BOLD, 60));
                buttons[i][j].setPreferredSize(new Dimension(100, 100));
                buttons[i][j].setBorder(BorderFactory.createLineBorder(new Color(0, 0, 0), 5)); // Black border
                buttons[i][j].setFocusPainted(false);
                buttons[i][j].addActionListener(new ButtonClickListener(i, j));
                gamePanel.add(buttons[i][j]);
                board[i][j] = '-';
            }
        }

        add(gamePanel, BorderLayout.CENTER);

        JPanel statusPanel = new JPanel();
        statusPanel.setBackground(new Color(255, 255, 255)); // White background
        statusLabel = new JLabel("Player " + currentPlayer + "'s turn");
        statusLabel.setFont(new Font("Arial", Font.BOLD, 20));
        statusPanel.add(statusLabel);
        add(statusPanel, BorderLayout.SOUTH);

        setVisible(true);

        if (isSinglePlayer) {
            aiPlayer = new AIPlayer();
            if (currentPlayer == 'O') {
                // AI's turn
                playAI();
            }
        }
    }

    private class ButtonClickListener implements ActionListener {
        private int row, col;

        public ButtonClickListener(int row, int col) {
            this.row = row;
            this.col = col;
        }

        public void actionPerformed(ActionEvent e) {
            if (!gamePaused && board[row][col] == '-' && !checkForWin()) {
                board[row][col] = currentPlayer;
                buttons[row][col].setText(String.valueOf(currentPlayer));
                buttons[row][col].setEnabled(false);
                if (checkForWin()) {
                    displayMessage("Player " + currentPlayer + " wins!");
                } else if (isBoardFull()) {
                    displayMessage("It's a draw!");
                } else {
                    currentPlayer = (currentPlayer == 'X') ? 'O' : 'X';
                    statusLabel.setText("Player " + currentPlayer + "'s turn");
                    if (isSinglePlayer && currentPlayer == 'O') {
                        // AI's turn
                        playAI();
                    }
                }
            } else {
                if (gamePaused) {
                    displayMessage("Game is paused. Click 'Play' to resume.");
                } else {
                    displayMessage("Invalid move! Try again.");
                }
            }
        }
    }

    private boolean isBoardFull() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j] == '-') {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean checkForWin() {
        return (checkRowsForWin() || checkColumnsForWin() || checkDiagonalsForWin());
    }

    private boolean checkRowsForWin() {
        for (int i = 0; i < 3; i++) {
            if (checkRowCol(board[i][0], board[i][1], board[i][2])) {
                return true;
            }
        }
        return false;
    }

    private boolean checkColumnsForWin() {
        for (int i = 0; i < 3; i++) {
            if (checkRowCol(board[0][i], board[1][i], board[2][i])) {
                return true;
            }
        }
        return false;
    }

    private boolean checkDiagonalsForWin() {
        return (checkRowCol(board[0][0], board[1][1], board[2][2]) || checkRowCol(board[0][2], board[1][1], board[2][0]));
    }

    private boolean checkRowCol(char c1, char c2, char c3) {
        return ((c1 != '-') && (c1 == c2) && (c2 == c3));
    }

    private void playAI() {
        int[] move = aiPlayer.findBestMove(board);
        if (move != null) {
            int row = move[0];
            int col = move[1];
            board[row][col] = currentPlayer;
            buttons[row][col].setText(String.valueOf(currentPlayer));
            buttons[row][col].setEnabled(false);
            if (checkForWin()) {
                displayMessage("Player " + currentPlayer + " wins!");
            } else if (isBoardFull()) {
                displayMessage("It's a draw!");
            } else {
                currentPlayer = (currentPlayer == 'X') ? 'O' : 'X';
                statusLabel.setText("Player " + currentPlayer + "'s turn");
            }
        }
    }

    private void displayMessage(String message) {
        // Create a dialog to display the message
        JDialog dialog = new JDialog(this, "Game Over", true);
        dialog.setLayout(new FlowLayout());
        dialog.setSize(200, 100);
        JLabel label = new JLabel(message);
        dialog.add(label);
        JButton okButton = new JButton("OK");
        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dialog.dispose();
                if (message.contains("win") || message.contains("draw")) {
                    restartGame(); // If game over, restart the game
                }
            }
        });
        dialog.add(okButton);
        dialog.setVisible(true);
    }

    private void restartGame() {
        // Reset board and currentPlayer
        currentPlayer = 'X';
        statusLabel.setText("Player " + currentPlayer + "'s turn");
        gamePaused = false;
        resetBoard();
    }

    private void resetBoard() {
        // Clear board and reset buttons
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                board[i][j] = '-';
                buttons[i][j].setText("");
                buttons[i][j].setEnabled(true);
            }
        }
    }

class AIPlayer {
    private static final char AI_PLAYER = 'O'; // AI Player is represented by 'O'
    private static final char HUMAN_PLAYER = 'X'; // Human Player is represented by 'X'

    public int[] findBestMove(char[][] board) {
        int bestScore = Integer.MIN_VALUE;
        int[] bestMove = new int[]{-1, -1};

        // Traverse all cells, evaluate minimax function for all empty cells
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                if (board[row][col] == '-') {
                    board[row][col] = AI_PLAYER; // Try the move
                    int score = minimax(board, 0, false); // Minimax search
                    board[row][col] = '-'; // Undo the move
                    if (score > bestScore) {
                        bestScore = score;
                        bestMove[0] = row;
                        bestMove[1] = col;
                    }
                }
            }
        }

        return bestMove;
    }

    private int minimax(char[][] board, int depth, boolean isMaximizing) {
        int result = evaluate(board);

        // If the game has ended, return the score
        if (result != 0) {
            return result;
        }

        // If it's maximizing player's turn
        if (isMaximizing) {
            int bestScore = Integer.MIN_VALUE;
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    if (board[i][j] == '-') {
                        board[i][j] = AI_PLAYER; // Try the move
                        int score = minimax(board, depth + 1, false);
                        board[i][j] = '-'; // Undo the move
                        bestScore = Math.max(score, bestScore);
                    }
                }
            }
            return bestScore;
        } else { // If it's minimizing player's turn
            int bestScore = Integer.MAX_VALUE;
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    if (board[i][j] == '-') {
                        board[i][j] = HUMAN_PLAYER; // Try the move
                        int score = minimax(board, depth + 1, true);
                        board[i][j] = '-'; // Undo the move
                        bestScore = Math.min(score, bestScore);
                    }
                }
            }
            return bestScore;
        }
    }

    private int evaluate(char[][] board) {
        // Checking for Rows for X or O victory.
        for (int row = 0; row < 3; row++) {
            if (board[row][0] == board[row][1] && board[row][1] == board[row][2]) {
                if (board[row][0] == AI_PLAYER)
                    return +10;
                else if (board[row][0] == HUMAN_PLAYER)
                    return -10;
            }
        }

        // Checking for Columns for X or O victory.
        for (int col = 0; col < 3; col++) {
            if (board[0][col] == board[1][col] && board[1][col] == board[2][col]) {
                if (board[0][col] == AI_PLAYER)
                    return +10;
                else if (board[0][col] == HUMAN_PLAYER)
                    return -10;
            }
        }

        // Checking for Diagonals for X or O victory.
        if (board[0][0] == board[1][1] && board[1][1] == board[2][2]) {
            if (board[0][0] == AI_PLAYER)
                return +10;
            else if (board[0][0] == HUMAN_PLAYER)
                return -10;
        }
        if (board[0][2] == board[1][1] && board[1][1] == board[2][0]) {
            if (board[0][2] == AI_PLAYER)
                return +10;
            else if (board[0][2] == HUMAN_PLAYER)
                return -10;
        }

        // No winner yet
        return 0;
    }
}

public static void main(String[] args) {
    SwingUtilities.invokeLater(new Runnable() {
        public void run() {
            String[] options = {"1 Player vs. Computer", "2 Players"};
            int choice = JOptionPane.showOptionDialog(null, "Choose game mode:", "Game Mode",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
            if (choice == 0) {
                // Set game mode to 1 Player vs. Computer
                new TicTacToeGUI(true);
            } else if (choice == 1) {
                // Set game mode to 2 Players
                new TicTacToeGUI(false);
            }
        }
    });
}
}
