package ru.itis.dnf_spring.models;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class DNF {

    private final int n;
    private Conjunct[] conjuncts;

    public DNF(int n, Conjunct[] c) {
        this.n = n;
        setConjuncts(c);
    }

    public DNF(int n, List<Conjunct> c) {
        this.n = n;
        conjuncts = new Conjunct[c.size()];
        int i = 0;
        for(Conjunct cc : c) {
            conjuncts[i] = cc;
            i++;
        }
    }

    public boolean isConsumedBy(DNF dnf) {
        List<Conjunct> our = new LinkedList<>(Arrays.asList(conjuncts));
        List<Conjunct> theirs = new LinkedList<>(Arrays.asList(dnf.conjuncts));

        List<Conjunct> our2 = new LinkedList<>(Arrays.asList(conjuncts));
        our2.removeAll(theirs);
        theirs.removeAll(our);

        return theirs.size() == 0 && our2.size() > 0;
    }

    public int getN() {
        return n;
    }

    public Conjunct[] getConjuncts() {
        return conjuncts;
    }

    public void setConjuncts(Conjunct[] conjuncts) {
        this.conjuncts = conjuncts;
    }

    public boolean valueOnDataSet(DataSet ds) {
        boolean val = false;
        for(Conjunct c : conjuncts) {
            val |= c.valueOnDataSet(ds);
        }
        return val;
    }

    public int getNumLiterals() {
        int cnt = 0;
        for(Conjunct c : conjuncts) {
            cnt += c.getLiterals().length;
        }
        return cnt;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("");
        for(Conjunct c : conjuncts) {
            sb.append(c.toString() + " v ");
        }
        return sb.substring(0, sb.length()-2);
    }

}
