package ru.itis.dnf_spring.models;

import ru.itis.dnf_spring.DnfSpringApplication;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class DataSet {

    private final int n;
    private Map<Integer, Boolean> map;

    public static List<DataSet> getAllDataSetsByN(int n) {
        List<DataSet> result = new LinkedList<>();

        Literal[] l = new Literal[n];
        for(int i = 0; i < n; i++) {
            l[i] = new Literal(n, i, true);
        }

        for(int i = 0; i < (int)Math.pow(2, n); i++) {
            Literal[] l2 = Literal.deepClone(l);

            Map<Integer, Boolean> map = new HashMap<>();
            for(Literal lt : l2) {
                map.put(lt.getVar(), lt.isInverse());
            }

            result.add(new DataSet(n, map));

            int pos = n-1;
            while(pos>=0 && !l[pos].isInverse()) {
                l[pos].setInverse(true);
                pos--;
            }
            if(pos<0) break;
            l[pos].setInverse(false);
        }
        return result;
    }

    public DataSet(int n, Map<Integer, Boolean> map) {
        this.n = n;
        this.setMap(map);
    }

    public DataSet(int n, String s) {
        this.n = n;
        map = new HashMap<>();
        if(s.length()!=n) {
            throw new IllegalArgumentException("Error creating dataset - missmatch n and string len");
        }
        for(int i = 0; i < s.length(); i++) {
            map.put(i, s.charAt(i)=='1');
        }
    }

    public int getN() {
        return n;
    }

    public Map<Integer, Boolean> getMap() {
        return map;
    }

    public void setMap(Map<Integer, Boolean> map) {
        this.map = map;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("DataSet[");
        for(Map.Entry<Integer, Boolean> e : map.entrySet()) {
            sb.append(DnfSpringApplication.VARIABLES[e.getKey()] + "=" + (e.getValue() ? 1 : 0) + "; ");
        }
        sb.append("]");
        return sb.toString();
    }

}
