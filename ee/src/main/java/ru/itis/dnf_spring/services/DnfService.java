package ru.itis.dnf_spring.services;

import ru.itis.dnf_spring.DnfSpringApplication;
import ru.itis.dnf_spring.models.Conjunct;
import ru.itis.dnf_spring.models.DNF;
import ru.itis.dnf_spring.models.DataSet;
import ru.itis.dnf_spring.models.Literal;

import java.util.*;

public final class DnfService {

    public static DNF getSdnfByVector(String vec) {
        if(!DnfSpringApplication.NUM_VARS_BY_VEC_LEN.containsKey(vec.length())) {
            throw new IllegalArgumentException("Wrong vector lenght - " + vec.length());
        }
        int n = DnfSpringApplication.NUM_VARS_BY_VEC_LEN.get(vec.length());

        List<Conjunct> conjs = new ArrayList<Conjunct>();
        // Пройдёмся по всем наборам:
        Literal[] l = new Literal[n];
        for(int i = 0; i < n; i++) {
            l[i] = new Literal(n, i, true);
        }

        for(int i = 0; i < vec.length(); i++) {

            Literal[] l2 = Literal.deepClone(l);
            Conjunct c = new Conjunct(n, l2);
            if(vec.charAt(i)=='1') {
                conjs.add(c);
            }

            if(c.isLast()) break;

            int pos = n-1;
            while(!l[pos].isInverse()) {
                l[pos].setInverse(true);
                pos--;
            }
            l[pos].setInverse(false);
        }

        Conjunct[] cs = new Conjunct[conjs.size()];
        int i = 0;
        for(Conjunct c : conjs) {
            cs[i] = c;
            i++;
        }

        DNF sdnf = new DNF(n, cs);
        return sdnf;
    }

    public static DNF getSorkDnfBySdnf(DNF sdnf) {
        List<Conjunct> nf = new LinkedList<>();
        nf.addAll(Arrays.asList(sdnf.getConjuncts()));

        List<Conjunct> toTrash = new LinkedList<>();

        for(int i = 0; i < nf.size()-1; i++) {
            // Выбрали первый элемент сравнения
            for(int j = i + 1; j < nf.size(); j++) {
                // Выбрали второй элемент сравнения
                Conjunct a = nf.get(i);
                Conjunct b = nf.get(j);
                Optional<Conjunct> merge = a.mergeWith(b);
                if(merge.isPresent()) {
                    // Можно произвести склейку
                    toTrash.add(b);
                    toTrash.add(a);
                    boolean exists = false;
                    for(Conjunct c : nf) {
                        if(c.equals(merge.get())) {
                            exists = true;
                        }
                    }
                    if(!exists) {
                        nf.add(merge.get());
                    }
                }
            }
        }

        // Осталось убрать те, которые будут поглощены
        nf.removeAll(toTrash);

        // И пересоздать днф
        return new DNF(sdnf.getN(), nf);
    }

    public static List<DNF> getAllDeadEnd(DNF sokrDnf) {
        List<DNF> result = new ArrayList<DNF>();

        List<Conjunct> c = Arrays.asList(sokrDnf.getConjuncts());
        boolean[] f = new boolean[c.size()];
        Arrays.fill(f, true);

        while(true) {
            List<Conjunct> nc = new LinkedList<>();
            for(int i = 0; i < f.length; i++) {
                if(f[i]) {
                    nc.add(c.get(i));
                }
            }
            DNF nd = new DNF(sokrDnf.getN(), nc);

            boolean flag = true;
            for(DataSet ds : DataSet.getAllDataSetsByN(sokrDnf.getN())) {
                flag &= sokrDnf.valueOnDataSet(ds) == nd.valueOnDataSet(ds);
            }
            if(flag) {
                result.add(nd);
            }

            int pos = 0;
            while(pos < f.length && f[pos]==false) {
                f[pos] = true;
                pos++;
            }
            if(pos >= f.length) break;
            f[pos]=false;
        }

        List<DNF> last = new ArrayList<>(result);

        for(int i = 0; i < result.size(); i++) {
            for(int j = 0; j < result.size(); j++) {
                if(i!=j) {
                    if(result.get(i).isConsumedBy(result.get(j))) {
                        last.remove(result.get(i));
                    }
                }
            }
        }

        return last;
    }

    public static List<DNF> getShortest(List<DNF> d) {
        List<DNF> result = new LinkedList<>();

        // Кротчайшая считается по кол-ву слагаемых
        int min = d.get(0).getConjuncts().length;
        for(DNF dnf : d) {
            if(dnf.getConjuncts().length < min) {
                min = dnf.getConjuncts().length;
                result.clear();
                result.add(dnf);
            } else if(dnf.getConjuncts().length == min) {
                result.add(dnf);
            }
        }

        return result;
    }

    public static List<DNF> getMinimum(List<DNF> d) {
        List<DNF> result = new LinkedList<>();

        // Минимальная считается по кол-ву литералов
        int min = d.get(0).getNumLiterals();
        for(DNF dnf : d) {
            if(dnf.getNumLiterals() < min) {
                min = dnf.getNumLiterals();
                result.clear();
                result.add(dnf);
            } else if(dnf.getNumLiterals() == min) {
                result.add(dnf);
            }
        }

        return result;
    }
}