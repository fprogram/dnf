package ru.itis.dnf_spring.models;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Conjunct {

    private final int n;
    private Literal[] literals;

    @Override
    public boolean equals(Object other) {
        if(other instanceof Conjunct) {
            Conjunct o = (Conjunct) other;
            if(this.n != o.n || this.literals.length != o.literals.length) return false;
            boolean eq = true;
            for(int i = 0; i < literals.length; i++) {
                eq &= literals[i].equals(o.literals[i]);
            }
            return eq;
        } else {
            return false;
        }
    }

    public Conjunct(int n, Literal[] lits) {
        this.n = n;
        setLiterals(lits);
    }

    public Conjunct(int n, List<Literal> lits) {
        this.n = n;
        literals = new Literal[lits.size()];
        int i = 0;
        for(Literal ll : lits) {
            literals[i] = ll;
            i++;
        }
    }

    public Optional<Literal> getLiteralByVariable(int var) {
        for(Literal l : literals) {
            if(l.getVar() == var) return Optional.of(l);
        }
        return Optional.empty();
    }

    public Optional<Conjunct> mergeWith(Conjunct other) {
        if (n != other.n || literals.length != other.literals.length) {
            return Optional.empty();
        }
        List<Literal> ok = new ArrayList<Literal>();
        for(Literal l : literals) {
            Optional<Literal> otherOptionalLiteral = other.getLiteralByVariable(l.getVar());
            if(!otherOptionalLiteral.isPresent()) {
                return Optional.empty();
            } else {
                Literal otherLiteral = otherOptionalLiteral.get();
                if(otherLiteral.isInverse() == l.isInverse()) {
                    ok.add(Literal.deepClone(l));
                }
            }
        }
        if(ok.size() == literals.length - 1) {
            return Optional.of(new Conjunct(n, ok));
        }
        return Optional.empty();
    }

    public boolean isLast() {
        boolean result = true;
        for(Literal l : literals) {
            result &= !l.isInverse();
        }
        return result;
    }

    public int getN() {
        return n;
    }

    public Literal[] getLiterals() {
        return literals;
    }

    public void setLiterals(Literal[] literals) {
        this.literals = literals;
    }

    public boolean valueOnDataSet(DataSet ds) {
        boolean val = true;
        for(Literal l : literals) {
            val &= l.valueOnDataSet(ds);
        }
        return val;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for(Literal l : literals) {
            sb.append(l.toString());
        }
        return sb.toString();
    }
}
