package chess;

import java.lang.reflect.Type;
import java.security.InvalidParameterException;
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
    private ChessPiece enPassantVulnerable;
    private ChessPiece promoção;

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

    public ChessPiece getEnPassantVulnerable() { return enPassantVulnerable; }

    public ChessPiece getPromoção() { return promoção; }

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

        ChessPiece movedPiece = (ChessPiece) board.piece(target);

        //Movimento especial promoção
        promoção = null;

        if(movedPiece instanceof Peao) {
            if(movedPiece.getColor() == Color.white && target.getRow() == 0 || movedPiece.getColor() == Color.black && target.getRow() == 7) {
                promoção = (ChessPiece)board.piece(target);
                promoção = replacePromotedPiece ("Q");
            }
        }

        check = (testCheck(opponent(currentPlayer))) ? true : false;

        if (testCheckMate(opponent(currentPlayer))) {
            checkMate = true;
        }
        else {
            nextTurn();

        }

        //Movimento especial en passant
        if (movedPiece instanceof Peao && (target.getRow() == source.getRow() -2 || target.getRow() == source.getRow() + 2)) {
            enPassantVulnerable = movedPiece;
        }
        else {
            enPassantVulnerable = null;
        }

        return (ChessPiece)capturedPiece;
    }

    public ChessPiece replacePromotedPiece (String type) {
        if (promoção == null) {
            throw new IllegalStateException("Não há peça para ser promovida");
        }
        if (!type.equals("B") && !type.equals("C") && !type.equals("T") && !type.equals("Q")) {
            throw new InvalidParameterException("Tipo de pormoção invalida");
        }

        Position pos = promoção.getChessPosition().toPosition();
        Piece p = board.removePiece(pos);
        piecesOntheBoard.remove(p);

        ChessPiece newPiece = newPiece(type, promoção.getColor());
        board.placePiece(newPiece, pos);
        piecesOntheBoard.add(newPiece);

        return newPiece;

        

    }

    private ChessPiece newPiece(String type, Color color) {
        if (type.equals("B")) return new Bispo(board,color);
        if (type.equals("C")) return new Cavalo(board,color);
        if (type.equals("Q")) return new Rainha(board,color);
        return new Torre(board,color);

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
        // Movimento especial roque pequeno
        if (p instanceof  Rei && target.getColumn() == source.getColumn() + 2) {
            Position sourceT = new Position(source.getRow(), source.getColumn() + 3);
            Position targetT = new Position(target.getRow(), target.getColumn() - 1);
            ChessPiece torre = (ChessPiece)board.removePiece(sourceT);
            board.placePiece(torre, targetT);
            torre.increaseMoveCount();
        }

        // Movimento especial roque grande
        if (p instanceof Rei && target.getColumn() == source.getColumn() - 2) {
            Position sourceT = new Position(source.getRow(), source.getColumn() - 4);
            Position targetT = new Position(source.getRow(), source.getColumn() - 1);
            ChessPiece torre = (ChessPiece)board.removePiece(sourceT);
            board.placePiece(torre, targetT);
            torre.increaseMoveCount();
        }

        // Movimento especial en passant
        if (p instanceof Peao) {
            if (source.getColumn() != target.getColumn() && capturedPiece == null) {
                Position posPeao;
                if (p.getColor() == Color.white) {
                    posPeao = new Position(target.getRow() + 1 , target.getColumn());
                }
                else {
                    posPeao = new Position(target.getRow() - 1, target.getColumn());
                }
                capturedPiece = board.removePiece(posPeao);
                capturedPieces.add(capturedPiece);
                piecesOntheBoard.remove(capturedPiece);

            }
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
        // Desfazendo o roque pequeno
        if (p instanceof  Rei && target.getColumn() == source.getColumn() + 2) {
            Position sourceT = new Position(source.getRow(), source.getColumn() + 3);
            Position targetT = new Position(target.getRow(), target.getColumn() + 1);
            ChessPiece torre = (ChessPiece)board.removePiece(targetT);
            board.placePiece(torre, sourceT);
            torre.decreaseMoveCount();
        }
        // Desfazendo o roque grande
        if (p instanceof Rei && target.getColumn() == source.getColumn() - 2) {
            Position sourceT = new Position(source.getRow(), source.getColumn() - 4);
            Position targetT = new Position(source.getRow(), source.getColumn() - 1);
            ChessPiece torre = (ChessPiece)board.removePiece(targetT);
            board.placePiece(torre, sourceT);
            torre.decreaseMoveCount();
        }
        // Desfazendo o en passant
        if (p instanceof Peao) {
            if (source.getColumn() != target.getColumn() && capturedPiece == null) {
                ChessPiece peao = (ChessPiece)board.removePiece(target);
                Position posPeao;
                if (p.getColor() == Color.white) {
                    posPeao = new Position(3 , target.getColumn());
                }
                else {
                    posPeao = new Position(4, target.getColumn());
                }
                board.placePiece(peao, posPeao);
                capturedPiece = board.removePiece(posPeao);
                capturedPieces.add(capturedPiece);
                piecesOntheBoard.remove(capturedPiece);

            }
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
            placeNewPiece('e', 1, new Rei(board, Color.white, this));
            placeNewPiece('a', 2, new Peao(board, Color.white, this));
            placeNewPiece('b', 2, new Peao(board, Color.white, this));
            placeNewPiece('c', 2, new Peao(board, Color.white, this));
            placeNewPiece('d', 2, new Peao(board, Color.white, this));
            placeNewPiece('e', 2, new Peao(board, Color.white, this));
            placeNewPiece('f', 2, new Peao(board, Color.white, this));
            placeNewPiece('g', 2, new Peao(board, Color.white, this));
            placeNewPiece('h', 2, new Peao(board, Color.white, this));
            placeNewPiece('c', 1, new Bispo(board, Color.white));
            placeNewPiece('f', 1, new Bispo(board, Color.white));
            placeNewPiece('b', 1, new Cavalo(board, Color.white));
            placeNewPiece('g', 1, new Cavalo(board, Color.white));
            placeNewPiece('d', 1, new Rainha(board, Color.white));




            placeNewPiece('a', 8, new Torre(board, Color.black));
            placeNewPiece('e', 8, new Rei(board, Color.black, this));
            placeNewPiece('h', 8, new Torre(board, Color.black));
            placeNewPiece('a', 7, new Peao(board, Color.black, this));
            placeNewPiece('b', 7, new Peao(board, Color.black, this));
            placeNewPiece('c', 7, new Peao(board, Color.black, this));
            placeNewPiece('d', 7, new Peao(board, Color.black, this));
            placeNewPiece('e', 7, new Peao(board, Color.black, this));
            placeNewPiece('f', 7, new Peao(board, Color.black, this));
            placeNewPiece('g', 7, new Peao(board, Color.black, this));
            placeNewPiece('h', 7, new Peao(board, Color.black, this));
            placeNewPiece('c', 8, new Bispo(board, Color.black));
            placeNewPiece('f', 8, new Bispo(board, Color.black));
            placeNewPiece('b', 8, new Cavalo(board, Color.black));
            placeNewPiece('g', 8, new Cavalo(board, Color.black));
            placeNewPiece('d', 8, new Rainha(board, Color.black));

        }



}