package chess;

import boardgame.Board;
import boardgame.Piece;
import boardgame.Position;
import chess.chess.pieces.*;

import java.lang.annotation.Target;

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

    public ChessPiece performChessMove(ChessPosition sourcePosition, ChessPosition targertPosition) {
        Position source = sourcePosition.toPosition();
        Position target = targertPosition.toPosition();
        validateSourcePosition(source);
        validateTargetPosition(source, target);
        Piece capturedPiece = makeMove(source, target);
        return (ChessPiece)capturedPiece;
    }

    private Piece makeMove (Position source, Position target) {
        Piece p = board.removePiece(source);
        Piece capturedPiece = board.removePiece(target);
        board.placePiece(p, target);
        return capturedPiece;
    }
    private void validateSourcePosition(Position position){
        if (!board.thereIsAPiece(position)){
            throw new ChessException("Não existe peça na posição de origem");
        }
        if (!board.piece(position).isThereAnyPossibleMove()) {
            throw new ChessException("Não existem movimentos possiveis para a peça escolhida");
        }
    }

    private void validateTargetPosition(Position source, Position target) {
        if (!board.piece(source).possibleMove(target)) {
            throw new ChessException("A peça escolhida não pode se mover para a posição de destino");
        }
    }

    private void placeNewPiece(char column, int row, ChessPiece piece) {
        board.placePiece(piece, new ChessPosition(column, row).toPosition());
    }

        private void initialSetup() {
            placeNewPiece('c', 1, new Torre(board, Color.white));
            placeNewPiece('c', 2, new Torre(board, Color.white));
            placeNewPiece('d', 2, new Torre(board, Color.white));
            placeNewPiece('e', 2, new Torre(board, Color.white));
            placeNewPiece('e', 1, new Torre(board, Color.white));
            placeNewPiece('d', 1, new Rei(board, Color.white));

            placeNewPiece('c', 7, new Torre(board, Color.black));
            placeNewPiece('c', 8, new Torre(board, Color.black));
            placeNewPiece('d', 7, new Torre(board, Color.black));
            placeNewPiece('e', 7, new Torre(board, Color.black));
            placeNewPiece('e', 8, new Torre(board, Color.black));
            placeNewPiece('d', 8, new Rei(board, Color.black));

        }



}