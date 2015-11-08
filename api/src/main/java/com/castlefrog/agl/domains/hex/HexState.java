package com.castlefrog.agl.domains.hex;

import com.castlefrog.agl.State;

import java.io.Serializable;

public final class HexState implements State<HexState>, Serializable {
    private byte[][] bitBoards_;
    private byte boardSize_;
    private byte agentTurn_;
    private BoardState boardState_;

    public enum BoardState {
        EMPTY,
        FIRST_MOVE,
        SECOND_MOVE,
        OTHER
    }

    public static final int
        LOCATION_EMPTY = 0,
        LOCATION_BLACK = 1,
        LOCATION_WHITE = 2;

    public static final int
        TURN_BLACK = 0,
        TURN_WHITE = 1;

    public HexState(int boardSize,
                    byte[][] bitBoards,
                    int agentTurn,
                    BoardState boardState) {
        boardSize_ = (byte) boardSize;
        bitBoards_ = new byte[bitBoards.length][bitBoards[0].length];
        for (int i = 0; i < bitBoards.length; i += 1) {
            System.arraycopy(bitBoards[i], 0, bitBoards_[i], 0, bitBoards[0].length);
        }
        agentTurn_ = (byte) agentTurn;
        boardState_ = boardState;
    }

    public HexState copy() {
        return new HexState(boardSize_, bitBoards_, agentTurn_, boardState_);
    }

    public byte[][] getBitBoards() {
        return bitBoards_;
    }

    public byte[][] getLocations() {
        byte[][] locations = new byte[boardSize_][boardSize_];
        for (int i = 0; i < boardSize_; i += 1) {
            for (int j = 0; j < boardSize_; j += 1) {
                locations[i][j] = (byte) getLocation(i, j);
            }
        }
        return locations;
    }

    public int getLocation(int x, int y) {
        int bitLocation = y * boardSize_ + x;
        int byteLocation = bitLocation / Byte.SIZE;
        if ((bitBoards_[0][byteLocation] & (1 << bitLocation % Byte.SIZE)) != 0) {
            return LOCATION_BLACK;
        } else if ((bitBoards_[1][byteLocation] & ( 1 << bitLocation % Byte.SIZE)) != 0) {
            return  LOCATION_WHITE;
        } else {
            return LOCATION_EMPTY;
        }
    }

    public int getBoardSize() {
        return boardSize_;
    }

    public byte getAgentTurn() {
        return agentTurn_;
    }

    public BoardState getBoardState() {
        return boardState_;
    }

    public boolean isLocationEmpty(int x, int y) {
        int bitLocation = y * boardSize_ + x;
        int byteLocation = bitLocation / Byte.SIZE;
        return ((bitBoards_[0][byteLocation] | bitBoards_[1][byteLocation]) &
                (1 << bitLocation % Byte.SIZE)) == LOCATION_EMPTY;
    }

    public int getNPieces() {
        int nPieces = 0;
        for (int i = 0; i < bitBoards_[0].length; i += 1) {
            int value = bitBoards_[0][i] | bitBoards_[1][i];
            for (int j = 0; j < Byte.SIZE; j += 1) {
                if ((value & 0b1) != 0) {
                    nPieces += 1;
                }
                value = value >>> 1;
            }
        }
        return nPieces;
    }

    public void setLocation(int x, int y, int value) {
        int bitLocation = y * boardSize_ + x;
        int byteLocation = bitLocation / Byte.SIZE;
        int byteShift = bitLocation % Byte.SIZE;
        if (value == LOCATION_EMPTY) {
            bitBoards_[0][byteLocation] &= ((1 << byteShift) ^ 0xff);
            bitBoards_[1][byteLocation] &= ((1 << byteShift) ^ 0xff);
        } else if (value == LOCATION_BLACK) {
            bitBoards_[0][byteLocation] |= 1 << byteShift;
            bitBoards_[1][byteLocation] &= ((1 << byteShift) ^ 0xff);
        } else if (value == LOCATION_WHITE) {
            bitBoards_[0][byteLocation] &= ((1 << byteShift) ^ 0xff);
            bitBoards_[1][byteLocation] |= 1 << byteShift;
        }
    }

    public void setAgentTurn(int agentTurn) {
        agentTurn_ = (byte) agentTurn;
    }

    public void setBoardState(BoardState boardState) {
        boardState_ = boardState;
    }

    @Override
    public int hashCode() {
        int hashCode = 17;
        hashCode = hashCode * 13 + boardSize_;
        hashCode = hashCode * 23 + agentTurn_;
        for (byte[] row : bitBoards_) {
            for (byte location : row) {
                hashCode = 31 * hashCode + location;
            }
        }
        return hashCode;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof HexState)) {
            return false;
        }
        HexState state = (HexState) object;
        byte[][] bitBoards = state.getBitBoards();
        for (int i = 0; i < bitBoards_.length; i += 1) {
            for (int j = 0; j < bitBoards_.length; j += 1) {
                if (bitBoards_[i][j] != bitBoards[i][j]) {
                    return false;
                }
            }
        }
        return agentTurn_ == state.getAgentTurn() &&
               boardSize_ == state.getBoardSize();
    }

    @Override
    public String toString() {
        StringBuilder output = new StringBuilder();
        for (int i = boardSize_ - 1; i >= 0; i -= 1) {
            for (int j = i; j < boardSize_ - 1; j += 1) {
                output.append(" ");
            }
            for (int j = 0; j < boardSize_; j += 1) {
                int location = getLocation(j, i);
                if (location == LOCATION_BLACK) {
                    output.append("X ");
                } else if (location == LOCATION_WHITE) {
                    output.append("O ");
                } else {
                    output.append("- ");
                }
            }
            output.append("\n");
        }
        return output.toString();
    }
}
