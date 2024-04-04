package chess;

import boardgame.Board;
import boardgame.Piece;
import chess.chess.pieces.*;

public class ChessMatch {
    private Board board;

    public ChessMatch() {

        board = new Board(8, 8);
        initialSetup();
    }

    public ChessPiece[][] getPieces() {
        ChessPiece[][] mat = new ChessPiece[board.getRows()][board.getColumns()];
        for (int i = 0; i < board.getRows(); i++) {
            for (int j = 0; j < board.getColumns(); j++) {
                mat[i][j] = (ChessPiece) board.piece(i, j);
            }
        }
        return mat;
    }

    private void placeNewPiece(char column, int row, ChessPiece piece) {
        board.placePiece(piece, new ChessPosition(column, row).toPosition());
    }

        private void initialSetup() {
            placeNewPiece('c', 1, new Peao(board, Color.white));
            placeNewPiece('c', 2, new Peao(board, Color.white));
            placeNewPiece('d', 2, new Peao(board, Color.white));
            placeNewPiece('e', 2, new Peao(board, Color.white));
            placeNewPiece('e', 1, new Peao(board, Color.white));
            placeNewPiece('d', 1, new Rei(board, Color.white));

            placeNewPiece('c', 7, new Peao(board, Color.black));
            placeNewPiece('c', 8, new Peao(board, Color.black));
            placeNewPiece('d', 7, new Peao(board, Color.black));
            placeNewPiece('e', 7, new Peao(board, Color.black));
            placeNewPiece('e', 8, new Peao(board, Color.black));
            placeNewPiece('d', 8, new Rei(board, Color.black));




        }

}