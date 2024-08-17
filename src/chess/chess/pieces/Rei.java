package chess.chess.pieces;

import boardgame.Board;
import boardgame.Position;
import chess.ChessMatch;
import chess.ChessPiece;
import chess.Color;

public class Rei extends ChessPiece {

    private ChessMatch chessMatch;

    public Rei(Board board, Color color, ChessMatch chessMatch) {
        super(board, color);
        this.chessMatch = chessMatch;
    }
    @Override
    public String toString() {
        return "K";
    }

    private boolean testRoque(Position position) {
        ChessPiece p = (ChessPiece) getBoard().piece(position);
        return p != null && p instanceof Torre && p.getColor() == getColor() && p.getMoveCount() == 0;
    }

    private boolean canMove(Position position) {
        ChessPiece p = (ChessPiece) getBoard().piece(position);
        return p == null || p.getColor() != getColor();
    }

    @Override
    public boolean[][] possibleMoves() {
        boolean [][] mat = new boolean[getBoard().getRows()][getBoard().getRows()];
        Position p = new Position(0, 0);

        //Acima
        p.setValues(position.getRow() - 1, position.getColumn());
        if(getBoard().positionExists(p) && canMove(p)) {
            mat [p.getRow()] [p.getColumn()] = true;
        }
        //Abaixo
        p.setValues(position.getRow() + 1, position.getColumn());
        if(getBoard().positionExists(p) && canMove(p)) {
            mat [p.getRow()] [p.getColumn()] = true;
        }
        //Esquerda
        p.setValues(position.getRow(), position.getColumn() - 1);
        if(getBoard().positionExists(p) && canMove(p)) {
            mat [p.getRow()] [p.getColumn()] = true;
        }
        //Direita
        p.setValues(position.getRow(), position.getColumn() + 1);
        if(getBoard().positionExists(p) && canMove(p)) {
            mat [p.getRow()] [p.getColumn()] = true;
        }
        //Diagonal NW
        p.setValues(position.getRow() - 1, position.getColumn() - 1);
        if(getBoard().positionExists(p) && canMove(p)) {
            mat [p.getRow()] [p.getColumn()] = true;
        }
        //Diagonal NE
        p.setValues(position.getRow() - 1, position.getColumn() + 1);
        if(getBoard().positionExists(p) && canMove(p)) {
            mat [p.getRow()] [p.getColumn()] = true;
        }
        // Diagonal Sw
        p.setValues(position.getRow() + 1, position.getColumn() - 1);
        if(getBoard().positionExists(p) && canMove(p)) {
            mat [p.getRow()] [p.getColumn()] = true;
        }
        // Diagonal Se
        p.setValues(position.getRow() + 1, position.getColumn() + 1);
        if(getBoard().positionExists(p) && canMove(p)) {
            mat [p.getRow()] [p.getColumn()] = true;
        }

        // Movimento especial roque

        if (getMoveCount() == 0 && !chessMatch.getCheck()) {
            // Movimento especial roque pequeno
            Position posT1 = new Position(position.getRow(), position.getColumn() + 3);
            if (testRoque(posT1)) {
                Position p1 = new Position(position.getRow(), position.getColumn() + 1);
                Position p2 = new Position(position.getRow(), position.getColumn() + 2);
                if (getBoard().piece(p1) == null && getBoard().piece(p2) == null) {
                    mat[position.getRow()][position.getColumn() + 2] = true;
                }

            }
            // Movimento especial roque grande
            Position posT2 = new Position(position.getRow(), position.getColumn() - 4);
            if (testRoque(posT2)) {
                Position p1 = new Position(position.getRow(), position.getColumn() - 1);
                Position p2 = new Position(position.getRow(), position.getColumn() - 2);
                Position p3 = new Position(position.getRow(), position.getColumn() - 3);

                if (getBoard().piece(p1) == null && getBoard().piece(p2) == null && getBoard().piece(p3) == null) {
                    mat[position.getRow()][position.getColumn() - 2] = true;
                }
            }
        }

        return mat;
    }
}
