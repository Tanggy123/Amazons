package amazons;

/**
 * NOTICE:
 * This file is a SUGGESTED skeleton.  NOTHING here or in any other source
 * file is sacred.
 * If any of it confuses you, throw it out and do it your way. */

import static java.lang.Math.*;

import static amazons.Piece.*;
import static amazons.Utils.iterable;

/**
 * A Player that automatically generates moves.
 *
 * @author Dayuan Tang
 */
class AI extends Player {

    /**
     * A position magnitude indicating a win (for white if positive, black
     * if negative).
     */
    private static final int WINNING_VALUE = Integer.MAX_VALUE - 1;
    /**
     * A magnitude greater than a normal value.
     */
    private static final int INFTY = Integer.MAX_VALUE;

    /**
     * A new AI with no piece or controller (intended to produce
     * a template).
     */
    AI() {
        this(null, null);
    }

    /**
     * A new AI playing PIECE under control of CONTROLLER.
     */
    AI(Piece piece, Controller controller) {
        super(piece, controller);
    }

    @Override
    Player create(Piece piece, Controller controller) {
        return new AI(piece, controller);
    }

    /**
     * Return either a String denoting either a legal move for me
     * or another command (which may be invalid).  Always returns the
     * latter if board().turn() is not myPiece() or if board.winner()
     * is not null.
     */
    @Override
    String myMove() {
        Move move = findMove();
        _controller.reportMove(move);
        return move.toString();
    }

    /**
     * Return a move for me from the current position, assuming there
     * is a move.
     */
    private Move findMove() {
        Board b = new Board(board());
        if (_myPiece == WHITE) {
            findMove(b, maxDepth(b), true, 1, -INFTY, INFTY);
        } else {
            findMove(b, maxDepth(b), true, -1, -INFTY, INFTY);
        }
        return _lastFoundMove;
    }

    /**
     * The move found by the last call to one of the ...FindMove methods
     * below.
     */
    private Move _lastFoundMove;

    /**
     * Find a move from position BOARD and return its value, recording
     * the move found in _lastFoundMove iff SAVEMOVE. The move
     * should have maximal value or have value > BETA if SENSE==1,
     * and minimal value or value < ALPHA if SENSE==-1. Searches up to
     * DEPTH levels.  Searching at level 0 simply returns a static estimate
     * of the board value and does not set _lastMoveFound.
     */
    private int findMove(Board board, int depth, boolean saveMove, int sense,
                         int alpha, int beta) {
        if (depth == 0 || board.winner() != null) {
            return staticScore(board);
        }
        Board bestSoFarMax = new Board();
        bestSoFarMax.setValue(-INFTY);
        Board bestSoFarMin = new Board();
        bestSoFarMin.setValue(INFTY);
        Board next;
        if (sense == 1) {
            for (Move m : iterable(board.legalMoves(WHITE))) {
                board.makeMove(m);
                next = new Board(board);
                board.undo();
                int response = findMove(next, depth - 1,
                        false, -1, alpha, beta);
                if (response >= bestSoFarMax.value()) {
                    bestSoFarMax = next;
                    next.setValue(response);
                    alpha = max(alpha, response);
                    if (beta <= alpha) {
                        break;
                    }
                }
            }
            if (saveMove) {
                _lastFoundMove = bestSoFarMax.moves().pop();
            }
            return bestSoFarMax.value();
        } else {
            for (Move m : iterable(board.legalMoves(BLACK))) {
                board.makeMove(m);
                next = new Board(board);
                board.undo();
                int response = findMove(next, depth - 1, false, 1, alpha, beta);
                if (response <= bestSoFarMin.value()) {
                    bestSoFarMin = next;
                    next.setValue(response);
                    beta = min(beta, response);
                    if (beta <= alpha) {
                        break;
                    }
                }
            }
            if (saveMove) {
                _lastFoundMove = bestSoFarMin.moves().pop();
            }
            return bestSoFarMin.value();
        }
    }

    /**
     * Return a heuristically determined maximum search depth
     * based on characteristics of BOARD.
     */
    private int maxDepth(Board board) {
        int N = board.numMoves();
        return (N + FACTOR) / FACTOR;
    }


    /**
     * Return a heuristic value for BOARD.
     */
    private int staticScore(Board board) {
        Piece winner = board.winner();
        if (winner == BLACK) {
            return -WINNING_VALUE;
        } else if (winner == WHITE) {
            return WINNING_VALUE;
        }
        if (board.numMoves() == 0) {
            return 0;
        }
        int score = 0;
        for (Square s : iterable(Square.iterator())) {
            if (board.get(s) == WHITE) {
                for (Square i : iterable(board.reachableFrom(s, null))) {
                    score++;
                }
            } else if (board.get(s) == BLACK) {
                for (Square i : iterable(board.reachableFrom(s, null))) {
                    score--;
                }
            }
        }
        return score;
    }

    /** Decides when to increase depth of search tree.*/
    private static final int FACTOR = 20;

}
