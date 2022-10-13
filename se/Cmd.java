import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Scanner;

public class Cmd {
	
	public static final Character[] VARIABLES = {'X', 'Y', 'Z', 'W', 'A', 'B'};
	public static Map<Integer, Integer> NUM_VARS_BY_VEC_LEN = new HashMap<>();

	public static void main(String[] args) {
		// Заполняем соответствие длины вектора кол-ву переменных
		for(int i = 2; i <= 6; i++) NUM_VARS_BY_VEC_LEN.put((int) Math.pow(2, i), i);
		// Получаем вектор функции
		Scanner sc = new Scanner(System.in);
		String vec = sc.nextLine();
		
		// Строим СДНФ по вектору
		DNF sdnf = DNF.getSdnfByVector(vec);
		System.out.println("SDNF: " + sdnf);
		
		// Строим СокрДНФ методом Квайна
		DNF sokrDnf = DNF.getSorkDnfBySdnf(sdnf);
		System.out.println("SokrDNF: " + sokrDnf);
		
		// Получаем все тупиковые ДНФ по СокрДНФ:
		List<DNF> deadend = DNF.getAllDeadEnd(sokrDnf);
		System.out.println("All Dead end dnfs:");
		for(DNF d : deadend) {
			System.out.println(d);
		}
		
		List<DNF> shortest = DNF.getShortest(deadend);
		List<DNF> minimum = DNF.getMinimum(deadend);
		
		System.out.println("\nAll shortest dnfs:");
		for(DNF d : shortest) System.out.println(d);
		
		System.out.println("\nAll minimum dnfs:");
		for(DNF d : minimum) System.out.println(d);
	}

}

class DNF {
	
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
	
	public static DNF getSdnfByVector(String vec) {
		if(!Cmd.NUM_VARS_BY_VEC_LEN.containsKey(vec.length())) {
			throw new IllegalArgumentException("Wrong vector lenght - " + vec.length());
		}
		int n = Cmd.NUM_VARS_BY_VEC_LEN.get(vec.length());
		
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
		nf.addAll(Arrays.asList(sdnf.conjuncts));
		
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
		return new DNF(sdnf.n, nf);
	}
	
	public static List<DNF> getAllDeadEnd(DNF sokrDnf) {
		List<DNF> result = new ArrayList<DNF>();
		
		List<Conjunct> c = Arrays.asList(sokrDnf.conjuncts);
		boolean[] f = new boolean[c.size()];
		Arrays.fill(f, true);
		
		while(true) {
			List<Conjunct> nc = new LinkedList<>();
			for(int i = 0; i < f.length; i++) {
				if(f[i]) {
					nc.add(c.get(i));
				}
			}
			DNF nd = new DNF(sokrDnf.n, nc);
			
			boolean flag = true;
			for(DataSet ds : DataSet.getAllDataSetsByN(sokrDnf.n)) {
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
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("DNF[");
		for(Conjunct c : conjuncts) {
			sb.append(c.toString() + " v ");
		}
		sb.append("]");
		return sb.toString();
	}
}

class Conjunct {
	
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

class Literal {
	
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
		sb.append(Cmd.VARIABLES[var]);
		return sb.toString();
	}

	public int getN() {
		return n;
	}
	
}

class DataSet {
	
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
		for(Entry<Integer, Boolean> e : map.entrySet()) {
			sb.append(Cmd.VARIABLES[e.getKey()] + "=" + (e.getValue() ? 1 : 0) + "; ");
		}
		sb.append("]");
		return sb.toString();
	}
	
}