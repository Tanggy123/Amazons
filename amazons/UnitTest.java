package amazons;

import org.junit.Test;

import static amazons.Piece.*;
import static org.junit.Assert.*;

import ucb.junit.textui;

import java.util.Iterator;

/**
 * The suite of all JUnit tests for the enigma package.
 *
 * @author Dayuan Tang
 */
public class UnitTest {

    /**
     * Run the JUnit tests in this package. Add xxxTest.class entries to
     * the arguments of runClasses to run other JUnit tests.
     */
    public static void main(String[] ignored) {
        textui.runClasses(UnitTest.class);
    }

    /**
     * Tests basic correctness of put and get on the initialized board.
     */
    @Test
    public void testBasicPutGet() {
        Board b = new Board();
        b.put(BLACK, Square.sq(3, 5));
        assertEquals(b.get(3, 5), BLACK);
        b.put(WHITE, Square.sq(9, 9));
        assertEquals(b.get(9, 9), WHITE);
        b.put(EMPTY, Square.sq(3, 5));
        assertEquals(b.get(3, 5), EMPTY);
    }

    /**
     * Tests proper identification of legal/illegal queen moves.
     */
    @Test
    public void testIsQueenMove() {
        assertFalse(Square.sq(1, 5).isQueenMove(Square.sq(1, 5)));
        assertFalse(Square.sq(1, 5).isQueenMove(Square.sq(2, 7)));
        assertFalse(Square.sq(0, 0).isQueenMove(Square.sq(5, 1)));
        assertTrue(Square.sq(1, 1).isQueenMove(Square.sq(9, 9)));
        assertTrue(Square.sq(2, 7).isQueenMove(Square.sq(8, 7)));
        assertTrue(Square.sq(3, 0).isQueenMove(Square.sq(2, 1)));
        assertTrue(Square.sq(7, 9).isQueenMove(Square.sq(0, 2)));
    }

    @Test
    public void testDirection() {
        assertEquals(0, Square.sq(1, 1).direction(Square.sq(1, 5)));
        assertEquals(1, Square.sq(1, 1).direction(Square.sq(7, 7)));
        assertEquals(2, Square.sq(2, 3).direction(Square.sq(5, 3)));
        assertEquals(4, Square.sq(2, 3).direction(Square.sq(2, 1)));
    }

    @Test
    public void testIsUnblockedMove() {
        Board c = new Board();
        c.put(BLACK, Square.sq(3, 5));
        c.put(WHITE, Square.sq(4, 9));
        assertFalse(c.isUnblockedMove(Square.sq(1, 8), Square.sq(5, 9), null));
        assertFalse(c.isUnblockedMove(Square.sq(1, 9), Square.sq(5, 9), null));
        assertFalse(c.isUnblockedMove(Square.sq(1, 5), Square.sq(3, 5), null));
        assertFalse(c.isUnblockedMove(Square.sq(2, 7), Square.sq(4, 9), null));
        assertTrue(c.isUnblockedMove(Square.sq(1, 1), Square.sq(2, 2), null));
        assertTrue(c.isUnblockedMove(Square.sq(2, 7),
                Square.sq(4, 9), Square.sq(4, 9)));
        assertTrue(c.isUnblockedMove(Square.sq(1, 5),
                Square.sq(5, 5), Square.sq(3, 5)));
        assertTrue(c.isUnblockedMove(Square.sq(1, 7),
                Square.sq(4, 4), Square.sq(3, 5)));
    }

    @Test
    public void testIsLegal() {
        Board d = new Board();
        assertFalse(d.isLegal(Square.sq(0, 6),
                Square.sq(1, 6), Square.sq(2, 6)));
        assertFalse(d.isLegal(Square.sq(3, 0),
                Square.sq(3, 9), Square.sq(4, 9)));
        assertFalse(d.isLegal(Square.sq(3, 0),
                Square.sq(4, 5), Square.sq(7, 5)));
        assertFalse(d.isLegal(Square.sq(3, 0),
                Square.sq(3, 8), Square.sq(3, 9)));
        assertTrue(d.isLegal(Square.sq(3, 0),
                Square.sq(3, 5), Square.sq(5, 5)));
        assertTrue(d.isLegal(Square.sq(6, 0),
                Square.sq(4, 2), Square.sq(2, 2)));
    }

    @Test
    public void testReachableFrom() {
        Board b = new Board();
        Iterator<Square> iter1 = b.reachableFrom(Square.sq(3, 9), null);
        assertTrue(iter1.hasNext());
        assertEquals(Square.sq(4, 9), iter1.next());
        assertEquals(Square.sq(5, 9), iter1.next());
        assertEquals(Square.sq(4, 8), iter1.next());
        assertEquals(Square.sq(5, 7), iter1.next());

        b.put(WHITE, Square.sq(0, 7));
        b.put(WHITE, Square.sq(1, 7));
        b.put(WHITE, Square.sq(3, 6));
        b.put(WHITE, Square.sq(1, 5));
        b.put(WHITE, Square.sq(0, 4));
        Iterator<Square> iter2 = b.reachableFrom(Square.sq(0, 6), null);
        assertEquals(Square.sq(1, 6), iter2.next());
        assertEquals(Square.sq(2, 6), iter2.next());
        assertEquals(Square.sq(0, 5), iter2.next());
        assertFalse(iter2.hasNext());
    }

    @Test
    public void testLegalMoves() {
        Board b = new Board();
        b.put(EMPTY, 3, 0);
        b.put(EMPTY, 6, 0);
        b.put(EMPTY, 0, 3);
        b.put(EMPTY, 9, 3);
        b.put(EMPTY, 9, 6);
        b.put(EMPTY, 3, 9);
        b.put(EMPTY, 6, 9);
        b.put(WHITE, 0, 7);
        b.put(WHITE, 1, 7);
        b.put(WHITE, 2, 8);
        b.put(WHITE, 3, 8);
        b.put(WHITE, 4, 8);
        b.put(WHITE, 3, 6);
        b.put(WHITE, 1, 5);
        b.put(WHITE, 0, 4);
        b.put(WHITE, 3, 4);
        b.put(WHITE, 4, 4);
        b.put(WHITE, 2, 3);
        Iterator<Move> iter1 = b.legalMoves(BLACK);
        Square from = Square.sq(0, 6);
        assertTrue(iter1.hasNext());
        assertEquals(Move.mv(from, Square.sq(1, 6),
                Square.sq(2, 7)), iter1.next());
        assertEquals(Move.mv(from, Square.sq(1, 6),
                Square.sq(2, 6)), iter1.next());
        assertEquals(Move.mv(from, Square.sq(1, 6),
                Square.sq(2, 5)), iter1.next());
        assertEquals(Move.mv(from, Square.sq(1, 6),
                Square.sq(0, 5)), iter1.next());
        assertEquals(Move.mv(from, Square.sq(1, 6),
                Square.sq(0, 6)), iter1.next());

        assertEquals(Move.mv(from, Square.sq(2, 6),
                Square.sq(2, 7)), iter1.next());
        assertEquals(Move.mv(from, Square.sq(2, 6),
                Square.sq(3, 7)), iter1.next());
        assertEquals(Move.mv(from, Square.sq(2, 6),
                Square.sq(3, 5)), iter1.next());
        assertEquals(Move.mv(from, Square.sq(2, 6),
                Square.sq(2, 5)), iter1.next());
        assertEquals(Move.mv(from, Square.sq(2, 6),
                Square.sq(2, 4)), iter1.next());
        assertEquals(Move.mv(from, Square.sq(2, 6),
                Square.sq(1, 6)), iter1.next());
        assertEquals(Move.mv(from, Square.sq(2, 6),
                Square.sq(0, 6)), iter1.next());

        assertEquals(Move.mv(from, Square.sq(0, 5),
                Square.sq(0, 6)), iter1.next());
        assertEquals(Move.mv(from, Square.sq(0, 5),
                Square.sq(1, 6)), iter1.next());
        assertEquals(Move.mv(from, Square.sq(0, 5),
                Square.sq(2, 7)), iter1.next());
        assertEquals(Move.mv(from, Square.sq(0, 5),
                Square.sq(1, 4)), iter1.next());
        assertFalse(iter1.hasNext());
    }

    /**
     * Tests toString for initial board state and a smiling board state. :)
     */
    @Test
    public void testToString() {
        Board b = new Board();
        assertEquals(INIT_BOARD_STATE, b.toString());
        makeSmile(b);
        assertEquals(SMILE, b.toString());
    }

    private void makeSmile(Board b) {
        b.put(EMPTY, Square.sq(0, 3));
        b.put(EMPTY, Square.sq(0, 6));
        b.put(EMPTY, Square.sq(9, 3));
        b.put(EMPTY, Square.sq(9, 6));
        b.put(EMPTY, Square.sq(3, 0));
        b.put(EMPTY, Square.sq(3, 9));
        b.put(EMPTY, Square.sq(6, 0));
        b.put(EMPTY, Square.sq(6, 9));
        for (int col = 1; col < 4; col += 1) {
            for (int row = 6; row < 9; row += 1) {
                b.put(SPEAR, Square.sq(col, row));
            }
        }
        b.put(EMPTY, Square.sq(2, 7));
        for (int col = 6; col < 9; col += 1) {
            for (int row = 6; row < 9; row += 1) {
                b.put(SPEAR, Square.sq(col, row));
            }
        }
        b.put(EMPTY, Square.sq(7, 7));
        for (int lip = 3; lip < 7; lip += 1) {
            b.put(WHITE, Square.sq(lip, 2));
        }
        b.put(WHITE, Square.sq(2, 3));
        b.put(WHITE, Square.sq(7, 3));
    }

    static final String INIT_BOARD_STATE =
            "   - - - B - - B - - -\n"
                    + "   - - - - - - - - - -\n"
                    + "   - - - - - - - - - -\n"
                    + "   B - - - - - - - - B\n"
                    + "   - - - - - - - - - -\n"
                    + "   - - - - - - - - - -\n"
                    + "   W - - - - - - - - W\n"
                    + "   - - - - - - - - - -\n"
                    + "   - - - - - - - - - -\n"
                    + "   - - - W - - W - - -\n";

    static final String SMILE =
            "   - - - - - - - - - -\n"
                    + "   - S S S - - S S S -\n"
                    + "   - S - S - - S - S -\n"
                    + "   - S S S - - S S S -\n"
                    + "   - - - - - - - - - -\n"
                    + "   - - - - - - - - - -\n"
                    + "   - - W - - - - W - -\n"
                    + "   - - - W W W W - - -\n"
                    + "   - - - - - - - - - -\n"
                    + "   - - - - - - - - - -\n";


}


