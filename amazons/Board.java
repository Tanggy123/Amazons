package amazons;

import java.util.Collections;
import java.util.Iterator;
import java.util.Arrays;
import java.util.Stack;
import java.util.NoSuchElementException;

import static amazons.Piece.*;
import static amazons.Move.mv;


/** The state of an Amazons Game.
 *  @author Dayuan Tang
 */
class Board {

    /** The number of squares on a side of the board. */
    static final int SIZE = 10;

    /** Structure to store the status/pieces of teh board.*/
    private Piece[][] boardSet;

    /** Initializes a game board with SIZE squares on a side in the
     *  initial position. */
    Board() {
        init();
    }

    /** Initializes a copy of MODEL. */
    Board(Board model) {
        copy(model);
    }

    /** Copies MODEL into me. */
    void copy(Board model) {
        init();
        for (int i = 0; i < 100; i++) {
            put(model.get(Square.sq(i)), Square.sq(i));
        }
        _turn = model._turn;
        _winner = model._winner;
        _value = model._value;
        Move[] moveArr = new Move[model.moves.size()];
        int i = model.moves.size() - 1;
        while (!model.moves.empty()) {
            moveArr[i] = model.moves.pop();
            i--;
        }
        for (int a = 0; a < moveArr.length; a++) {
            moves.push(moveArr[a]);
            model.moves.push(moveArr[a]);
        }
    }

    /** Clears the board to the initial position. */
    void init() {
        boardSet = new Piece[10][10];
        for (Piece[] col : boardSet) {
            Arrays.fill(col, EMPTY);
        }
        put(WHITE, 3, 0);
        put(WHITE, 6, 0);
        put(WHITE, 0, 3);
        put(WHITE, 9, 3);
        put(BLACK, 0, 6);
        put(BLACK, 9, 6);
        put(BLACK, 3, 9);
        put(BLACK, 6, 9);
        _turn = WHITE;
        _winner = EMPTY;
        _value = 0;
    }

    /** Return the Piece whose move it is (WHITE or BLACK). */
    Piece turn() {
        return _turn;
    }

    /** Return the number of moves (that have not been undone) for this
     *  board. */
    int numMoves() {
        return moves.size();
    }

    /** Return the winner in the current position, or null if the game is
     *  not yet finished. */
    Piece winner() {
        if (_winner == EMPTY) {
            return null;
        } else {
            return _winner;
        }
    }

    /** Return the contents the square at S. */
    final Piece get(Square s) {
        return get(s.col(), s.row());
    }

    /** Return the contents of the square at (COL, ROW), where
     *  0 <= COL, ROW <= 9. */
    final Piece get(int col, int row) {
        return boardSet[col][row];
    }

    /** Return the contents of the square at COL ROW. */
    final Piece get(char col, char row) {
        return get(col - 'a', row - '1');
    }

    /** Set square S to P. */
    final void put(Piece p, Square s) {
        put(p, s.col(), s.row());
    }

    /** Set square (COL, ROW) to P. */
    final void put(Piece p, int col, int row) {
        boardSet[col][row] = p;
        _winner = EMPTY;
    }

    /** Set square COL ROW to P. */
    final void put(Piece p, char col, char row) {
        put(p, col - 'a', row - '1');
    }

    /** Return true iff FROM - TO is an unblocked queen move on the current
     *  board, ignoring the contents of ASEMPTY, if it is encountered.
     *  For this to be true, FROM-TO must be a queen move and the
     *  squares along it, other than FROM and ASEMPTY, must be
     *  empty. ASEMPTY may be null, in which case it has no effect. */
    boolean isUnblockedMove(Square from, Square to, Square asEmpty) {
        if (!from.isQueenMove(to) || (get(to) != EMPTY && to != asEmpty)) {
            return false;
        } else {
            Square nextPos = from.queenMove(from.direction(to), 1);
            int i = 2;
            while (nextPos != to) {
                if (get(nextPos) != EMPTY && nextPos != asEmpty) {
                    return false;
                }
                nextPos = from.queenMove(from.direction(to), i);
                i++;
            }
        }
        return true;
    }

    /** Return true iff FROM is a valid starting square for a move. */
    boolean isLegal(Square from) {
        return get(from) == _turn;
    }

    /** Return true iff FROM-TO is a valid first part of move, ignoring
     *  spear throwing. */
    boolean isLegal(Square from, Square to) {
        return isLegal(from) && isUnblockedMove(from, to, null);
    }

    /** Return true iff FROM-TO(SPEAR) is a legal move in the current
     *  position. */
    boolean isLegal(Square from, Square to, Square spear) {
        return isLegal(from, to) && isUnblockedMove(to, spear, from);

    }

    /** Return true iff MOVE is a legal move in the current
     *  position. */
    boolean isLegal(Move move) {
        if (move == null) {
            return false;
        }
        return isLegal(move.from(), move.to(), move.spear());
    }

    /** Move FROM-TO(SPEAR), assuming this is a legal move. */
    void makeMove(Square from, Square to, Square spear) {
        put(get(from), to);
        put(EMPTY, from);
        put(SPEAR, spear);
        moves.push(mv(from, to, spear));
        if (!legalMoves(_turn.opponent()).hasNext()) {
            _winner = _turn;
        }
        _turn = _turn.opponent();
    }

    /** Move according to MOVE, assuming it is a legal move. */
    void makeMove(Move move) {
        makeMove(move.from(), move.to(), move.spear());
    }

    /** Undo one move.  Has no effect on the initial board. */
    void undo() {
        Move reverse = moves.pop();
        put(EMPTY, reverse.spear());
        put(get(reverse.to()), reverse.from());
        put(EMPTY, reverse.to());
        _turn = _turn.opponent();
        _winner = EMPTY;
    }

    /** Return an Iterator over the Squares that are reachable by an
     *  unblocked queen move from FROM. Does not pay attention to what
     *  piece (if any) is on FROM, nor to whether the game is finished.
     *  Treats square ASEMPTY (if non-null) as if it were EMPTY.  (This
     *  feature is useful when looking for Moves, because after moving a
     *  piece, one wants to treat the Square it came from as empty for
     *  purposes of spear throwing.) */
    Iterator<Square> reachableFrom(Square from, Square asEmpty) {
        return new ReachableFromIterator(from, asEmpty);
    }

    /** Return an Iterator over all legal moves on the current board. */
    Iterator<Move> legalMoves() {
        return new LegalMoveIterator(_turn);
    }

    /** Return an Iterator over all legal moves on the current board for
     *  SIDE (regardless of whose turn it is). */
    Iterator<Move> legalMoves(Piece side) {
        return new LegalMoveIterator(side);
    }

    /** An iterator used by reachableFrom. */
    private class ReachableFromIterator implements Iterator<Square> {

        /** Iterator of all squares reachable by queen move from FROM,
         *  treating ASEMPTY as empty. */
        ReachableFromIterator(Square from, Square asEmpty) {
            _from = from;
            _dir = -1;
            _steps = 0;
            _asEmpty = asEmpty;
            toNext();
        }

        @Override
        public boolean hasNext() {
            return _dir < 8;
        }

        @Override
        public Square next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            Square result =  _from.queenMove(_dir, _steps);
            toNext();
            return result;
        }

        /** Advance _dir and _steps, so that the next valid Square is
         *  _steps steps in direction _dir from _from. */
        private void toNext() {
            if (!hasNext()) {
                return;
            }
            Square next = _from.queenMove(_dir, _steps + 1);
            if (next == null) {
                _dir += 1;
                _steps = 0;
                toNext();
            } else {
                if (isUnblockedMove(_from, next, _asEmpty)) {
                    _steps += 1;
                    return;
                }
                _dir += 1;
                _steps = 0;
                toNext();
            }
        }

        /** Starting square. */
        private Square _from;
        /** Current direction. */
        private int _dir;
        /** Current distance. */
        private int _steps;
        /** Square treated as empty. */
        private Square _asEmpty;

    }

    /** An iterator used by legalMoves. */
    private class LegalMoveIterator implements Iterator<Move> {

        /** All legal moves for SIDE (WHITE or BLACK). */
        LegalMoveIterator(Piece side) {
            _startingSquares = Square.iterator();
            _spearThrows = NO_SQUARES;
            _pieceMoves = NO_SQUARES;
            _fromPiece = side;
            toNext();
        }

        @Override
        public boolean hasNext() {
            return _startingSquares.hasNext();
        }

        @Override
        public Move next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            Move result = mv(_start, _nextSquare, _spearThrows.next());
            toNext();
            return result;
        }

        /** Advance so that the next valid Move is
         *  _start-_nextSquare(sp), where sp is the next value of
         *  _spearThrows. */
        private void toNext() {
            if (!hasNext()) {
                return;
            }
            if (!_pieceMoves.hasNext() && !_spearThrows.hasNext()) {
                _start = _startingSquares.next();
            }
            if (get(_start) != _fromPiece) {
                toNext();
            } else {
                if (!_pieceMoves.hasNext() && !_spearThrows.hasNext()) {
                    _pieceMoves = reachableFrom(_start, null);
                    if (!_pieceMoves.hasNext()) {
                        toNext();
                    }
                }
                if (_pieceMoves.hasNext() && !_spearThrows.hasNext()) {
                    _nextSquare = _pieceMoves.next();
                    _spearThrows = reachableFrom(_nextSquare, _start);
                    if (!_spearThrows.hasNext()) {
                        toNext();
                    }
                }
            }
        }

        /** Color of side whose moves we are iterating. */
        private Piece _fromPiece;
        /** Current starting square. */
        private Square _start;
        /** Remaining starting squares to consider. */
        private Iterator<Square> _startingSquares;
        /** Current piece's new position. */
        private Square _nextSquare;
        /** Remaining moves from _start to consider. */
        private Iterator<Square> _pieceMoves;
        /** Remaining spear throws from _piece to consider. */
        private Iterator<Square> _spearThrows;
    }

    /** Retrieve value of the board.
     * @return value of the board.*/
    int value() {
        return _value;
    }

    /** Set value of the board.
     * @param val value of the board.*/
    void setValue(int val) {
        _value = val;
    }

    @Override
    public String toString() {
        String result = "";
        for (int rowNum = 9; rowNum >= 0; rowNum -= 1) {
            result += "  ";
            for (int colNum = 0; colNum < SIZE; colNum += 1) {
                result += " " + get(Square.sq(colNum, rowNum)).toString();
            }
            result += "\n";
        }
        return result;
    }

    /** Retrieve moves that have been placed.
     * @return moves*/
    Stack<Move> moves() {
        return moves;
    }

    /** An empty iterator for initialization. */
    private static final Iterator<Square> NO_SQUARES =
        Collections.emptyIterator();

    /** Piece whose turn it is (BLACK or WHITE). */
    private Piece _turn;
    /** Cached value of winner on this board, or EMPTY if it has not been
     *  computed. */
    private Piece _winner;
    /** Keep track of moves that have been made.*/
    private Stack<Move> moves = new Stack<>();
    /** Heuristic value of the board.*/
    private int _value;
}
