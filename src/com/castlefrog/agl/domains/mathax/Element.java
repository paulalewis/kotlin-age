package com.castlefrog.agl.domains.mathax;

public final class Element {
    private int color_;
    private char symbol_;

    public Element(int color,
                   char symbol) {
        color_ = color;
        symbol_ = symbol;
    }

    public int getColor() {
        return color_;
    }

    public char getSymbol() {
        return symbol_;
    }

    @Override
    public int hashCode() {
        Character temp = new Character(symbol_);
        return 11 * (7 + color_) + temp.hashCode();
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Element))
            return false;
        Element element = (Element) object;
        return color_ == element.getColor() &&
               symbol_ == element.getSymbol();
    }

    @Override
    public String toString() {
        final String COLORS = "abcdefghijklmnopqrstuvwxyz";
        return COLORS.substring(color_, color_ + 1) + symbol_;
    }
}
