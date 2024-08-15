package chess;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import boardgame.Board;
import boardgame.Piece;
import boardgame.Position;
import chess.chess.pieces.*;

public class ChessMatch {

    private int turn;
    private Color currentPlayer;
    private Board board;
    private boolean check;
    private boolean checkMate;

    private List<Piece> piecesOntheBoard = new ArrayList<>();
    private List<Piece> capturedPieces = new ArrayList<>();


    public ChessMatch() {

        board = new Board(8, 8);
        turn = 1;
        currentPlayer = Color.white;
        initialSetup();
    }

    public int getTurn() {
        return turn;
    }

    public Color getCurrentPlayer() {
        return currentPlayer;
    }

    public boolean getCheck() { return  check; }

    public boolean getCheckMate() { return checkMate; }

    public ChessPiece[][] getPieces() {
        ChessPiece[][] mat = new ChessPiece[board.getRows()][board.getColumns()];
        for (int i = 0; i < board.getRows(); i++) {
            for (int j = 0; j < board.getColumns(); j++) {
                mat[i][j] = (ChessPiece) board.piece(i, j);
            }
        }
        return mat;
    }

    public boolean[][] possibleMoves(ChessPosition sourcePosition) {
        Position position = sourcePosition.toPosition();
        validateSourcePosition(position);
        return board.piece(position).possibleMoves();
    }

    public ChessPiece performChessMove(ChessPosition sourcePosition, ChessPosition targertPosition) {
        Position source = sourcePosition.toPosition();
        Position target = targertPosition.toPosition();
        validateSourcePosition(source);
        validateTargetPosition(source, target);
        Piece capturedPiece = makeMove(source, target);

        if (testCheck(currentPlayer)) {
            undoMove(source, target, capturedPiece);
            throw new ChessException("Você não pode se colocar em cheque");
        }
        check = (testCheck(opponent(currentPlayer))) ? true : false;

        if (testCheckMate(opponent(currentPlayer))) {
            checkMate = true;
        }
        else {
            nextTurn();

        }

        return (ChessPiece)capturedPiece;
    }

    private Piece makeMove(Position source, Position target) {
        ChessPiece p = (ChessPiece) board.removePiece(source);
        p.increaseMoveCount();
        Piece capturedPiece = board.removePiece(target);
        board.placePiece(p, target);

        if (capturedPiece != null) {
            piecesOntheBoard.remove(capturedPiece);
            capturedPieces.add(capturedPiece);
        }

        return capturedPiece;
    }

    private void undoMove(Position source, Position target, Piece capturedPiece) {
        ChessPiece p = (ChessPiece) board.removePiece(target);
        p.decreaseMoveCount();
        board.placePiece(p, source);

        if (capturedPiece != null) {
            board.placePiece(capturedPiece, target);
            capturedPieces.remove(capturedPiece);
            piecesOntheBoard.add(capturedPiece);
        }
    }

    private void validateSourcePosition(Position position){
        if (!board.thereIsAPiece(position)){
            throw new ChessException("Não existe peça na posição de origem");
        }
        if (currentPlayer != ((ChessPiece)board.piece(position)).getColor()) {
            throw new ChessException("A peça escolhida não é sua!");
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

    private void nextTurn() {
        turn++;
        currentPlayer = (currentPlayer == Color.white) ? Color.black : Color.white;
    }

    private Color opponent(Color color) {
        return (color == Color.white) ? Color.black : Color.white;
    }

    private ChessPiece king(Color color) {
        List<Piece> list = piecesOntheBoard.stream().filter(x -> ((ChessPiece)x).getColor() == color).collect(Collectors.toList());

        for (Piece p : list) {
            if (p instanceof Rei) {
                return(ChessPiece) p;
            }
        }
        throw new IllegalThreadStateException("Não há rei da cor " + color + " no tabuleiro!");
    }

    private boolean testCheck(Color color) {
        Position kingPosition = king(color).getChessPosition().toPosition();
        List<Piece> opponentPiece = piecesOntheBoard.stream().filter(x -> ((ChessPiece)x).getColor() == opponent(color)).collect(Collectors.toList());

        for (Piece p : opponentPiece) {
            boolean[][] mat = p.possibleMoves();
            if (mat[kingPosition.getRow()][kingPosition.getColumn()]) {
                return  true;
            }
        }
        return false;
    }
    private boolean testCheckMate(Color color) {
        if (!testCheck(color)) {
            return false;
        }
        List<Piece> list = piecesOntheBoard.stream().filter(x -> ((ChessPiece)x).getColor() == color).collect(Collectors.toList());
        for (Piece p : list) {
            boolean[][] mat = p.possibleMoves();
            for (int i = 0; i< board.getRows(); i++) {
                for (int j = 0; j< board.getColumns(); j++) {
                    if (mat [i][j]) {
                        Position source =  ((ChessPiece)p).getChessPosition().toPosition();
                        Position target = new Position(i, j);
                        Piece capturedPiece = makeMove(source, target);
                        boolean testCheck = testCheck(color);
                        undoMove(source, target, capturedPiece);
                        if (!testCheck) {
                            return false;
                        }
                    }
                }
            }


        }
        return true;
    }


    private void placeNewPiece(char column, int row, ChessPiece piece) {
        board.placePiece(piece, new ChessPosition(column, row).toPosition());
        piecesOntheBoard.add(piece);
    }

        private void initialSetup() {
            placeNewPiece('a', 1, new Torre(board, Color.white));
            placeNewPiece('h', 1, new Torre(board, Color.white));
            placeNewPiece('e', 1, new Rei(board, Color.white));
            placeNewPiece('a', 2, new Peao(board, Color.white));
            placeNewPiece('b', 2, new Peao(board, Color.white));
            placeNewPiece('c', 2, new Peao(board, Color.white));
            placeNewPiece('d', 2, new Peao(board, Color.white));
            placeNewPiece('e', 2, new Peao(board, Color.white));
            placeNewPiece('f', 2, new Peao(board, Color.white));
            placeNewPiece('g', 2, new Peao(board, Color.white));
            placeNewPiece('h', 2, new Peao(board, Color.white));
            placeNewPiece('c', 1, new Bispo(board, Color.white));
            placeNewPiece('f', 1, new Bispo(board, Color.white));
            placeNewPiece('b', 1, new Cavalo(board, Color.white));
            placeNewPiece('g', 1, new Cavalo(board, Color.white));




            placeNewPiece('a', 8, new Torre(board, Color.black));
            placeNewPiece('e', 8, new Rei(board, Color.black));
            placeNewPiece('h', 8, new Torre(board, Color.black));
            placeNewPiece('a', 7, new Peao(board, Color.black));
            placeNewPiece('b', 7, new Peao(board, Color.black));
            placeNewPiece('c', 7, new Peao(board, Color.black));
            placeNewPiece('d', 7, new Peao(board, Color.black));
            placeNewPiece('e', 7, new Peao(board, Color.black));
            placeNewPiece('f', 7, new Peao(board, Color.black));
            placeNewPiece('g', 7, new Peao(board, Color.black));
            placeNewPiece('h', 7, new Peao(board, Color.black));
            placeNewPiece('c', 8, new Bispo(board, Color.black));
            placeNewPiece('f', 8, new Bispo(board, Color.black));
            placeNewPiece('b', 8, new Cavalo(board, Color.black));
            placeNewPiece('g', 8, new Cavalo(board, Color.black));

        }



}