package chess.chess.pieces;

import boardgame.Board;
import chess.ChessPiece;
import chess.Color;

public class Rainha extends ChessPiece {
    public Rainha(Board board, Color color) {
        super(board, color);
    }
    @Override
    public String toString() {
        return "Ra";
    }

    @Override
    public boolean[][] possibleMoves() {
        boolean [][] mat = new boolean[getBoard().getRows()][getBoard().getRows()];
        return mat;
    }
}
