package com.castlefrog.agl.domains.backgammon;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents moving a single piece from one location to another. It is a
 * partial action as an action may be made up of multiple moves.
 */
public final class BackgammonMove implements Comparable<BackgammonMove> {
    /** List of all legal moves. */
    private static final List<List<BackgammonMove>> moves_ = generateMoves();

    private final int from_;

    private final int distance_;

    private BackgammonMove(int from, int distance) {
        from_ = from;
        distance_ = distance;
    }

    public static BackgammonMove valueOf(int from, int distance) {
        return moves_.get(from).get(distance - 1);
    }

    private static List<List<BackgammonMove>> generateMoves() {
        List<List<BackgammonMove>> moves = new ArrayList<>();
        for (int i = 0; i < BackgammonState.N_LOCATIONS; i++) {
            moves.add(new ArrayList<BackgammonMove>());
            for (int j = 0; j < BackgammonState.N_DIE_FACES; j++) {
                moves.get(i).add(new BackgammonMove(i, j + 1));
            }
        }
        return moves;
    }

    public int getFrom() {
        return from_;
    }

    public int getDistance() {
        return distance_;
    }

    @Override
    public int compareTo(BackgammonMove move) {
        if (from_ < move.getFrom()) {
            return -1;
        } else if (from_ > move.getFrom()) {
            return 1;
        } else if (distance_ < move.getDistance()) {
            return -1;
        } else if (distance_ > move.getDistance()) {
            return 1;
        } else {
            return 0;
        }
    }

    @Override
    public int hashCode() {
        return 11 * (7 + from_) + distance_;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof BackgammonMove)) {
            return false;
        }
        BackgammonMove move = (BackgammonMove) object;
        return from_ == move.getFrom() && distance_ == move.getDistance();
    }

    @Override
    public String toString() {
        return from_ + "/" + distance_;
    }
}
