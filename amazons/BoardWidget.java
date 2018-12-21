package amazons;

import ucb.gui2.Pad;

import java.io.IOException;

import java.util.concurrent.ArrayBlockingQueue;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

import static amazons.Piece.*;
import static amazons.Square.sq;

/**
 * A widget that displays an Amazons game.
 *
 * @author Dayuan Tang
 */
class BoardWidget extends Pad {

    /* Parameters controlling sizes, speeds, colors, and fonts. */

    /**
     * Colors of empty squares and grid lines.
     */
    static final Color
            SPEAR_COLOR = new Color(64, 64, 64),
            LIGHT_SQUARE_COLOR = new Color(238, 207, 161),
            DARK_SQUARE_COLOR = new Color(205, 133, 63);

    /**
     * Locations of images of white and black queens and spear.
     */
    private static final String
            WHITE_QUEEN_IMAGE = "wq4.png",
            BLACK_QUEEN_IMAGE = "bq4.png",
            SPEAR_IMAGE = "spear.png",
            LAVA_IMAGE = "lava.png";

    /**
     * Size parameters.
     */
    private static final int
            SQUARE_SIDE = 30,
            BOARD_SIDE = SQUARE_SIDE * 10;

    /**
     * A graphical representation of an Amazons board that sends commands
     * derived from mouse clicks to COMMANDS.
     */
    BoardWidget(ArrayBlockingQueue<String> commands) {
        _commands = commands;
        setMouseHandler("click", this::mouseClicked);
        setPreferredSize(BOARD_SIDE, BOARD_SIDE);

        try {
            _whiteQueen = ImageIO.read(Utils.getResource(WHITE_QUEEN_IMAGE));
            _blackQueen = ImageIO.read(Utils.getResource(BLACK_QUEEN_IMAGE));
            _spear = ImageIO.read(Utils.getResource(SPEAR_IMAGE));
            _lavaSpear = ImageIO.read(Utils.getResource(LAVA_IMAGE));
        } catch (IOException excp) {
            System.err.println("Could not read queen images.");
            System.exit(1);
        }
        _acceptingMoves = false;
        _clickTimes = 0;
    }

    /**
     * Draw the bare board G.
     */
    private void drawGrid(Graphics2D g) {
        g.setColor(LIGHT_SQUARE_COLOR);
        g.fillRect(0, 0, BOARD_SIDE, BOARD_SIDE);
        g.setColor(new Color(0, 0, 0));
        for (int i = 0; i < BOARD_SIDE * 10; i += SQUARE_SIDE) {
            g.drawLine(i, BOARD_SIDE, i, cy(9));
        }
        for (int i = 0; i < BOARD_SIDE * 10; i += SQUARE_SIDE) {
            g.drawLine(0, i, BOARD_SIDE, i);
        }
    }

    @Override
    public synchronized void paintComponent(Graphics2D g) {
        drawGrid(g);
        for (int i = 0; i < SIZE * SIZE - 1; i++) {
            Piece p = _board.get(sq(i));
            if (p == WHITE || p == BLACK) {
                drawQueen(g, sq(i), p);
            }
            if (p == SPEAR) {
                drawSpear(g, sq(i));
            }
        }
    }

    /**
     * Draw a queen for side PIECE at square S on G.
     */
    private void drawQueen(Graphics2D g, Square s, Piece piece) {
        g.drawImage(piece == WHITE ? _whiteQueen : _blackQueen,
                cx(s.col()) + 2, cy(s.row()) + 4, null);
    }

    /**
     * Draw a spear.
     * @param g Graphics.
     * @param s Square in which to draw a spear.*/
    private void drawSpear(Graphics2D g, Square s) {
        g.drawImage(_lavaSpear,
                cx(s.col()), cy(s.row()), null);
    }

    /**
     * Handle a click on S.
     */
    private void click(Square s) {
        _clickTimes += 1;
        _clickCommand += s.toString() + " ";
        if (_clickTimes != 3) {
            return;
        }
        _commands.offer(_clickCommand);
        repaint();
        _clickTimes = 0;
        _clickCommand = "";
    }

    /**
     * Handle mouse click event E.
     */
    private synchronized void mouseClicked(String unused, MouseEvent e) {
        int xpos = e.getX(), ypos = e.getY();
        int x = xpos / SQUARE_SIDE,
                y = (BOARD_SIDE - ypos) / SQUARE_SIDE;
        if (_acceptingMoves
                && x >= 0 && x < Board.SIZE && y >= 0 && y < Board.SIZE) {
            click(sq(x, y));
        }
    }

    /**
     * Revise the displayed board according to BOARD.
     */
    synchronized void update(Board board) {
        _board.copy(board);
        repaint();
    }

    /**
     * Turn on move collection iff COLLECTING, and clear any current
     * partial selection.   When move collection is off, ignore clicks on
     * the board.
     */
    void setMoveCollection(boolean collecting) {
        _acceptingMoves = collecting;
        repaint();
    }

    /**
     * Return x-pixel coordinate of the left corners of column X
     * relative to the upper-left corner of the board.
     */
    private int cx(int x) {
        return x * SQUARE_SIDE;
    }

    /**
     * Return y-pixel coordinate of the upper corners of row Y
     * relative to the upper-left corner of the board.
     */
    private int cy(int y) {
        return (Board.SIZE - y - 1) * SQUARE_SIDE;
    }

    /**
     * Return x-pixel coordinate of the left corner of S
     * relative to the upper-left corner of the board.
     */
    private int cx(Square s) {
        return cx(s.col());
    }

    /**
     * Return y-pixel coordinate of the upper corner of S
     * relative to the upper-left corner of the board.
     */
    private int cy(Square s) {
        return cy(s.row());
    }

    /**
     * Queue on which to post move commands (from mouse clicks).
     */
    private ArrayBlockingQueue<String> _commands;
    /**
     * Board being displayed.
     */
    private final Board _board = new Board();

    /**
     * Size of the board.
     */
    private static final int SIZE = 10;

    /**
     * Image of white queen.
     */
    private BufferedImage _whiteQueen;
    /**
     * Image of black queen.
     */
    private BufferedImage _blackQueen;

    /**
     * Image of a spear.
     */
    private BufferedImage _spear;

    /**
     * Image of a lava spear.
     */
    private BufferedImage _lavaSpear;

    /**
     * True iff accepting moves from user.
     */
    private boolean _acceptingMoves;

    /** Keep track of how many times the mouse clicked.*/
    private int _clickTimes;

    /** Stores a complete move command.*/
    private String _clickCommand = "";
}
