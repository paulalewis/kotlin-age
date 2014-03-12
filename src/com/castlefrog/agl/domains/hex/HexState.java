package com.castlefrog.agl.domains.hex;

import java.io.Serializable;

import com.castlefrog.agl.State;
import sun.management.resources.agent;
import sun.net.www.content.audio.basic;

/*
 * A Hex state consists of a board of hexagon locations
 * that are either empty or have a piece black or
 * white piece.
 */
public final class HexState implements State<HexState>, Serializable {
    private static final int MIN_BOARD_SIZE = 1;

    private byte[][] bitBoards_;
    private byte boardSize_;
    private byte agentTurn_;

    public static final int
        LOCATION_EMPTY = 0,
        LOCATION_BLACK = 1,
        LOCATION_WHITE = 2;

    public HexState(int boardSize,
                    byte[][] bitBoards,
                    int agentTurn) {
        if (boardSize < MIN_BOARD_SIZE) {
            throw new IllegalArgumentException("Invalid board size: " + boardSize);
        }
        boardSize_ = (byte) boardSize;
        bitBoards_ = new byte[bitBoards.length][bitBoards[0].length];
        for (int i = 0; i < bitBoards.length; i += 1) {
            System.arraycopy(bitBoards[i], 0, bitBoards_[i], 0, bitBoards[0].length);
        }
        agentTurn_ = (byte) agentTurn;
    }

    public HexState copy() {
        return new HexState(boardSize_, bitBoards_, agentTurn_);
    }

    public byte[][] getBitBoards() {
        byte[][] bitBoards = new byte[bitBoards_.length][bitBoards_.length];
        for (int i = 0; i < bitBoards_.length; i += 1) {
            System.arraycopy(bitBoards_[i], 0, bitBoards[i], 0, bitBoards_.length);
        }
        return bitBoards;
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

    public boolean isLocationEmpty(int x, int y) {
        return getLocation(x, y) == LOCATION_EMPTY;
    }

    public boolean isBoardEmpty() {
        for (int i = 0; i < bitBoards_.length; i += 1) {
            for (int j = 0; j < bitBoards_[0].length; j += 1) {
                if (bitBoards_[i][j] != 0) {
                    return false;
                }
            }
        }
        return true;
    }

    public int getNPieces() {
        int nPieces = 0;
        byte[][] locations = getLocations();
        for (int i = 0; i < locations.length; i += 1) {
            for (int j = 0; j < locations.length; j += 1) {
                if (locations[i][j] != LOCATION_EMPTY) {
                    nPieces += 1;
                }
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
                if (getLocation(j, i) == LOCATION_BLACK) {
                    output.append("X ");
                } else if (getLocation(j, i) == LOCATION_WHITE) {
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
