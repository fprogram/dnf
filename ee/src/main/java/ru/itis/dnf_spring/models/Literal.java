package ru.itis.dnf_spring.models;

import ru.itis.dnf_spring.DnfSpringApplication;

public class Literal {

    private final int n;
    private int var;
    private boolean inverse;

    @Override
    public boolean equals(Object other) {
        if(other instanceof Literal) {
            Literal o = (Literal) other;
            return (this.n == o.n && this.var == o.var && this.inverse == o.inverse);
        } else {
            return false;
        }
    }

    public static Literal deepClone(Literal l) {
        return new Literal(l.getN(), l.getVar(), l.isInverse());
    }

    public static Literal[] deepClone(Literal[] l) {
        Literal[] result = new Literal[l.length];
        for(int i = 0; i < l.length; i++) {
            result[i] = deepClone(l[i]);
        }
        return result;
    }

    public Literal(int n, int c, boolean i) {
        this.n = n;
        var = c;
        inverse = i;
    }

    public int getVar() {
        return var;
    }
    public void setVar(int var) {
        this.var = var;
    }
    public boolean isInverse() {
        return inverse;
    }
    public void setInverse(boolean inverse) {
        this.inverse = inverse;
    }

    public boolean valueOnDataSet(DataSet ds) {
        if(ds.getMap().containsKey(var)) {
            if(inverse) {
                return !ds.getMap().get(var);
            } else {
                return ds.getMap().get(var);
            }
        } else {
            throw new IllegalArgumentException("Invalid map on literal " + this);
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if(inverse) sb.append("!");
        sb.append(DnfSpringApplication.VARIABLES[var]);
        sb.append(" ");
        return sb.toString();
    }

    public int getN() {
        return n;
    }
}
