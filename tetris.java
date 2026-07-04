import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.Random;

public class Tetris extends JPanel implements ActionListener, KeyListener {

    private final int ROWS = 20;
    private final int COLS = 10;
    private final int BLOCK_SIZE = 30;

    private Timer timer;
    private int[][] board = new int[ROWS][COLS];

    private int[][] currentPiece;
    private int pieceRow = 0;
    private int pieceCol = 3;

    Random random = new Random();

    private final int[][][] pieces = {
            {{1, 1, 1, 1}}, // I

            {{1, 1},
             {1, 1}},       // O

            {{0, 1, 0},
             {1, 1, 1}},   // T

            {{1, 0, 0},
             {1, 1, 1}},   // L

            {{0, 0, 1},
             {1, 1, 1}},   // J

            {{0, 1, 1},
             {1, 1, 0}},   // S

            {{1, 1, 0},
             {0, 1, 1}}    // Z
    };

    public Tetris() {
        setPreferredSize(new Dimension(COLS * BLOCK_SIZE,
                ROWS * BLOCK_SIZE));

        setBackground(Color.BLACK);

        addKeyListener(this);
        setFocusable(true);

        spawnPiece();

        timer = new Timer(500, this);
        timer.start();
    }

    private void spawnPiece() {
        currentPiece = pieces[random.nextInt(pieces.length)];
        pieceRow = 0;
        pieceCol = 3;

        if (!canMove(pieceRow, pieceCol)) {
            timer.stop();
            JOptionPane.showMessageDialog(this, "Game Over!");
        }
    }

    private boolean canMove(int newRow, int newCol) {

        for (int r = 0; r < currentPiece.length; r++) {
            for (int c = 0; c < currentPiece[r].length; c++) {

                if (currentPiece[r][c] == 1) {

                    int boardRow = newRow + r;
                    int boardCol = newCol + c;

                    if (boardCol < 0 || boardCol >= COLS ||
                            boardRow >= ROWS)
                        return false;

                    if (boardRow >= 0 &&
                            board[boardRow][boardCol] == 1)
                        return false;
                }
            }
        }
        return true;
    }

    private void mergePiece() {

        for (int r = 0; r < currentPiece.length; r++) {
            for (int c = 0; c < currentPiece[r].length; c++) {

                if (currentPiece[r][c] == 1) {
                    board[pieceRow + r][pieceCol + c] = 1;
                }
            }
        }

        clearLines();
        spawnPiece();
    }

    private void clearLines() {

        for (int row = ROWS - 1; row >= 0; row--) {

            boolean full = true;

            for (int col = 0; col < COLS; col++) {
                if (board[row][col] == 0) {
                    full = false;
                    break;
                }
            }

            if (full) {

                for (int r = row; r > 0; r--) {
                    board[r] = board[r - 1].clone();
                }

                board[0] = new int[COLS];
                row++;
            }
        }
    }

    private void rotatePiece() {

        int rows = currentPiece.length;
        int cols = currentPiece[0].length;

        int[][] rotated = new int[cols][rows];

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                rotated[c][rows - 1 - r] =
                        currentPiece[r][c];
            }
        }

        int[][] oldPiece = currentPiece;
        currentPiece = rotated;

        if (!canMove(pieceRow, pieceCol)) {
            currentPiece = oldPiece;
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Board
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {

                if (board[r][c] == 1) {
                    g.setColor(Color.CYAN);
                    g.fillRect(c * BLOCK_SIZE,
                            r * BLOCK_SIZE,
                            BLOCK_SIZE,
                            BLOCK_SIZE);
                }

                g.setColor(Color.GRAY);
                g.drawRect(c * BLOCK_SIZE,
                        r * BLOCK_SIZE,
                        BLOCK_SIZE,
                        BLOCK_SIZE);
            }
        }

        // Current Piece
        g.setColor(Color.RED);

        for (int r = 0; r < currentPiece.length; r++) {
            for (int c = 0; c < currentPiece[r].length; c++) {

                if (currentPiece[r][c] == 1) {

                    g.fillRect(
                            (pieceCol + c) * BLOCK_SIZE,
                            (pieceRow + r) * BLOCK_SIZE,
                            BLOCK_SIZE,
                            BLOCK_SIZE
                    );
                }
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (canMove(pieceRow + 1, pieceCol)) {
            pieceRow++;
        } else {
            mergePiece();
        }

        repaint();
    }

    @Override
    public void keyPressed(KeyEvent e) {

        int key = e.getKeyCode();

        if (key == KeyEvent.VK_LEFT &&
                canMove(pieceRow, pieceCol - 1)) {

            pieceCol--;

        } else if (key == KeyEvent.VK_RIGHT &&
                canMove(pieceRow, pieceCol + 1)) {

            pieceCol++;

        } else if (key == KeyEvent.VK_DOWN &&
                canMove(pieceRow + 1, pieceCol)) {

            pieceRow++;

        } else if (key == KeyEvent.VK_UP) {

            rotatePiece();
        }

        repaint();
    }

    @Override
    public void keyReleased(KeyEvent e) {}

    @Override
    public void keyTyped(KeyEvent e) {}

    public static void main(String[] args) {

        JFrame frame = new JFrame("Tetris");

        Tetris game = new Tetris();

        frame.add(game);
        frame.pack();

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}