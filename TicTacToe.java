package game;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

public class TicTacToe extends JFrame {
    private static final int WIDTH = 1000;
    private static final int HEIGHT = 750;
    private JButton[][] buttons = new JButton[3][3];
    private boolean player1Turn = true;
    private boolean singlePlayer = false;
    private Random random = new Random();
    private int player1Wins = 0;
    private int player2Wins = 0;
    private JLabel player1Score;
    private JLabel player2Score;
    private JLabel gameStatus;
    private JLabel modeLabel;

    public TicTacToe() {
        setTitle("Tic Tac Toe");
        setSize(WIDTH, HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel panel = new JPanel(new GridLayout(3, 3));
        panel.setBackground(new Color(220, 198, 255));
        panel.setBorder(BorderFactory.createLineBorder(new Color(220, 198, 255), 5));
        panel.setBorder(BorderFactory.createEmptyBorder());
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                buttons[i][j] = new JButton("");
                buttons[i][j].setFont(new Font("Arial", Font.PLAIN, 80));
                buttons[i][j].setFocusPainted(false);
                buttons[i][j].addActionListener(new ButtonClickListener(i, j));
                panel.add(buttons[i][j]);
            }
        }

        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setBackground(new Color(220, 198, 255));
        rightPanel.setBorder(BorderFactory.createLineBorder(new Color(220, 198, 255), 5));
        rightPanel.setBorder(BorderFactory.createEmptyBorder());
        rightPanel.setPreferredSize(new Dimension(300, HEIGHT));

        JButton newGameButton = new JButton("New Game");
        newGameButton.setFont(new Font("Arial", Font.PLAIN, 18));
        newGameButton.addActionListener(e -> resetBoard());

        player1Score = new JLabel("X: " + player1Wins);
        player1Score.setFont(new Font("Arial", Font.PLAIN, 30));
        player1Score.setAlignmentX(Component.CENTER_ALIGNMENT);

        player2Score = new JLabel("O: " + player2Wins);
        player2Score.setFont(new Font("Arial", Font.PLAIN, 30));
        player2Score.setAlignmentX(Component.CENTER_ALIGNMENT);

        gameStatus = new JLabel("");
        gameStatus.setFont(new Font("Arial", Font.PLAIN, 30));
        gameStatus.setAlignmentX(Component.CENTER_ALIGNMENT);

        modeLabel = new JLabel("Mode: 2 Players");
        modeLabel.setFont(new Font("Arial", Font.PLAIN, 30));
        modeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton onePlayerButton = new JButton("1 Player");
        onePlayerButton.setFont(new Font("Arial", Font.PLAIN, 18));
        onePlayerButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        onePlayerButton.addActionListener(e -> {
            singlePlayer = true;
            modeLabel.setText("Mode: 1 Player");
            resetBoard();
        });

        JButton twoPlayerButton = new JButton("2 Players");
        twoPlayerButton.setFont(new Font("Arial", Font.PLAIN, 18));
        twoPlayerButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        twoPlayerButton.addActionListener(e -> {
            singlePlayer = false;
            modeLabel.setText("Mode: 2 Players");
            resetBoard();
        });

        rightPanel.add(Box.createVerticalGlue());
        rightPanel.add(newGameButton);
        rightPanel.add(Box.createVerticalStrut(20));
        rightPanel.add(player1Score);
        rightPanel.add(player2Score);
        rightPanel.add(Box.createVerticalStrut(20));
        rightPanel.add(gameStatus);
        rightPanel.add(Box.createVerticalStrut(20));
        rightPanel.add(modeLabel);
        rightPanel.add(Box.createVerticalStrut(20));
        rightPanel.add(onePlayerButton);
        rightPanel.add(Box.createVerticalStrut(10));
        rightPanel.add(twoPlayerButton);
        rightPanel.add(Box.createVerticalGlue());

        add(panel, BorderLayout.CENTER);
        add(rightPanel, BorderLayout.EAST);
    }

    private class ButtonClickListener implements ActionListener {
        private int row;
        private int col;

        public ButtonClickListener(int row, int col) {
            this.row = row;
            this.col = col;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (!buttons[row][col].getText().equals("")) {
                return;
            }

            if (player1Turn) {
                buttons[row][col].setText("X");
            } else {
                buttons[row][col].setText("O");
            }

            if (checkForWin()) {
                if (player1Turn) {
                    player1Wins++;
                    JOptionPane.showMessageDialog(null, "Player 1 wins!");
                } else {
                    player2Wins++;
                    JOptionPane.showMessageDialog(null, "Player 2 wins!");
                }
                updateScores();
                resetBoard();
            } else if (isBoardFull()) {
                JOptionPane.showMessageDialog(null, "The game is a tie!");
                resetBoard();
            } else {
                player1Turn = !player1Turn;

                if (singlePlayer && !player1Turn) {
                    computerMove();
                    if (checkForWin()) {
                        player2Wins++;
                        JOptionPane.showMessageDialog(null, "Computer wins!");
                        updateScores();
                        resetBoard();
                    } else if (isBoardFull()) {
                        JOptionPane.showMessageDialog(null, "The game is a tie!");
                        resetBoard();
                    } else {
                        player1Turn = !player1Turn;
                    }
                }
            }
        }
    }

    private void computerMove() {
        int row, col;
        do {
            row = random.nextInt(3);
            col = random.nextInt(3);
        } while (!buttons[row][col].getText().equals(""));
        buttons[row][col].setText("O");
    }

    private boolean checkForWin() {
        // Check rows and columns
        for (int i = 0; i < 3; i++) {
            if (buttons[i][0].getText().equals(buttons[i][1].getText()) &&
                buttons[i][1].getText().equals(buttons[i][2].getText()) &&
                !buttons[i][0].getText().equals("")) {
                return true;
            }
            if (buttons[0][i].getText().equals(buttons[1][i].getText()) &&
                buttons[1][i].getText().equals(buttons[2][i].getText()) &&
                !buttons[0][i].getText().equals("")) {
                return true;
            }
        }

        // Check diagonals
        if (buttons[0][0].getText().equals(buttons[1][1].getText()) &&
            buttons[1][1].getText().equals(buttons[2][2].getText()) &&
            !buttons[0][0].getText().equals("")) {
            return true;
        }
        if (buttons[0][2].getText().equals(buttons[1][1].getText()) &&
            buttons[1][1].getText().equals(buttons[2][0].getText()) &&
            !buttons[0][2].getText().equals("")) {
            return true;
        }

        return false;
    }

    private boolean isBoardFull() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (buttons[i][j].getText().equals("")) {
                    return false;
                }
            }
        }
        return true;
    }

    private void resetBoard() {
        player1Turn = true;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                buttons[i][j].setText("");
            }
        }
        gameStatus.setText("");
    }

    private void updateScores() {
        player1Score.setText("X: " + player1Wins);
        player2Score.setText("O: " + player2Wins);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            TicTacToe game = new TicTacToe();
            game.setVisible(true);
        });
    }
}

                       
