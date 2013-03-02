package com.castlefrog.agl.domains.mathax;

import java.util.List;
import java.util.ArrayList;
import java.util.LinkedList;

public final class MathaxState implements Cloneable {
    public static final int WIDTH = 5;
    public static final int HEIGHT = 8;
    public static final int N_ELEMENTS = 2;
    public static final int MAX_HISTORY_SIZE = 10;

    /** board */
    private Element[][][] locations_;
    /** recent history of collected symbols */
    private LinkedList<Element> history_;
    /** number of moves until the elements fall */
    private int nFreeMoves_;
    /** total number of actions taken */
    private int nTurns_;
    /** number of colors currently generated */
    private int nColors_;

    /** x location of avatar */
    private int avatarX_;
    /** y location of avatar */
    private int avatarY_;

    public MathaxState(Element[][][] locations,
                       List<Element> history,
                       int nFreeMoves,
                       int nTurns,
                       int nColors) {
        locations_ = new Element[WIDTH][HEIGHT][N_ELEMENTS];
        for (int i = 0; i < WIDTH; i += 1)
            for (int j = 0; j < HEIGHT; j += 1) {
                for (int k = 0; k < N_ELEMENTS; k += 1)
                    locations_[i][j][k] = locations[i][j][k];
                if (isAvatar(i,j)) {
                    avatarX_ = i;
                    avatarY_ = j;
                }
            }
        history_ = new LinkedList<Element>();
        for (Element element: history)
            history_.add(element);
        nFreeMoves_ = nFreeMoves;
        nTurns_ = nTurns;
        nColors_ = nColors;
    }
    
    public MathaxState(Element[][][] locations,
                       List<Element> history,
                       int nFreeMoves,
                       int nTurns,
                       int nColors,
                       int avatarX,
                       int avatarY) {
        locations_ = new Element[WIDTH][HEIGHT][N_ELEMENTS];
        for (int i = 0; i < WIDTH; i += 1)
            for (int j = 0; j < HEIGHT; j += 1)
                for (int k = 0; k < N_ELEMENTS; k += 1)
                    locations_[i][j][k] = locations[i][j][k];
        history_ = new LinkedList<Element>();
        for (Element element: history)
            history_.add(element);
        nFreeMoves_ = nFreeMoves;
        nTurns_ = nTurns;
        nColors_ = nColors;
        avatarX_ = avatarX;
        avatarY_ = avatarY;
    }

    @Override
    public MathaxState clone() {
        return new MathaxState(locations_, history_, nFreeMoves_,
                nTurns_, nColors_, avatarX_, avatarY_);
    }

    public Element[] getLocation(int x, int y) {
        return locations_[x][y];
    }

    public List<Element> getHistory() {
        List<Element> history = new LinkedList<Element>();
        for (Element element: history_)
            history.add(element);
        return history;
    }

    public int getHistorySize() {
        return history_.size();
    }

    public int getNFreeMoves() {
        return nFreeMoves_;
    }

    public int getNTurns() {
        return nTurns_;
    }

    public int getNColors() {
        return nColors_;
    }

    public int getAvatarX() {
        return avatarX_;
    }

    public int getAvatarY() {
        return avatarY_;
    }

    public boolean isEmpty(int x, int y) {
        return locations_[x][y][0] == null &&
               locations_[x][y][1] == null;
    }

    public boolean isAvatar(int x, int y) {
        return locations_[x][y][0] != null &&
               locations_[x][y][1] == null;
    }

    public void addHistory(Element element) {
        history_.add(element);
        if (history_.size() > MAX_HISTORY_SIZE)
            history_.remove();
    }

    public Element getAvatar() {
        return locations_[avatarX_][avatarY_][0];
    }

    public void clearLocation(int x, int y) {
        setLocation(x, y, null, null);
    }

    public void moveAvatar(int dx, int dy) {
        locations_[avatarX_ + dx][avatarY_ + dy][0] = locations_[avatarX_][avatarY_][0];
        locations_[avatarX_ + dx][avatarY_ + dy][1] = null;
        locations_[avatarX_][avatarY_][0] = null;
        locations_[avatarX_][avatarY_][1] = null;
        avatarX_ += dx;
        avatarY_ += dy;
    }
    
    public void setAvatar(int x, int y, Element e) {
        setLocation(x, y, e, null);
        avatarX_ = x;
        avatarY_ = y;
    }

    public void setLocation(int x, int y, Element e1, Element e2) {
        locations_[x][y][0] = e1;
        locations_[x][y][1] = e2;
    }
    
    public void setLocation(int x, int y, Element[] ep) {
        setLocation(x, y, ep[0], ep[1]);
    }

    public void setNFreeMoves(int nFreeMoves) {
        nFreeMoves_ = nFreeMoves;
    }

    public void setNTurns(int nTurns) {
        nTurns_ = nTurns;
    }

    public void setNColors(int nColors) {
        nColors_ = nColors;
    }

    @Override
    public int hashCode() {
        int code = 7 + nFreeMoves_;
        for (int i = 0; i < WIDTH; i += 1)
            for (int j = 0; j < HEIGHT; j += 1)
                for (int k = 0; k < N_ELEMENTS; k += 1)
                    code = 11 * code + locations_[i][j][k].hashCode();
        for (Element element: history_)
            code = 11 * code + element.hashCode();
        code = 11 * code + nTurns_;
        code = 11 * code + nColors_;
        return code;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof MathaxState))
            return false;
        MathaxState state = (MathaxState) object;
        for (int i = 0; i < WIDTH; i += 1)
            for (int j = 0; j < HEIGHT; j += 1)
                for (int k = 0; k < N_ELEMENTS; k += 1)
                    if (!locations_[i][j][k].equals(state.getLocation(i, j)[k]))
                        return false;
        for (int i = 0; i < history_.size(); i += 1)
            if (!history_.get(i).equals(state.getHistory().get(i)))
                return false;
        if (nFreeMoves_ != state.getNFreeMoves())
            return false;
        if (nColors_ != state.getNColors())
            return false;
        return nTurns_ == state.getNTurns();
    }

    @Override
    public String toString() {
        StringBuilder output = new StringBuilder();
        output.append(nColors_ + ":" + nTurns_ + ":" + nFreeMoves_ + "\n");
        for (int i = 0; i < MAX_HISTORY_SIZE - history_.size(); i++)
            output.append("   ");
        for (Element element: history_)
            output.append(element.toString() + " ");
        output.append("\n");
        for (int i = 0; i < WIDTH; i += 1)
            output.append("------");
        output.append("-\n");
        for (int j = 0; j < HEIGHT; j += 1) {
            output.append(":");
            for (int i = 0; i < WIDTH; i += 1) {
                Element[] location = locations_[i][j];
                if (i != 0)
                    output.append(" ");
                if (isEmpty(i, j))
                    output.append("     ");
                else if (isAvatar(i, j))
                    output.append(" [" + location[0].toString().substring(0,1) + "] ");
                else
                    output.append(location[0] + "|" + location[1]);
            }
            output.append(":\n");
        }
        output.append("-");
        for (int i = 0; i < WIDTH; i += 1)
            output.append("------");
        return output.toString();
    }
}
