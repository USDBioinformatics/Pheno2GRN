/*
 * A group is a group of sets of elements (genes?) 
 *    You can add a set by giving a list of strings in a file
 */
package groups;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;


public class Group {

    private List<String> uni = new ArrayList<>();           //the universe

    private List<BitSet> fSetList = new ArrayList<>(); //the list of sets
    private List<String> setNames = new ArrayList<>();

    public void add(List<String> setContents, String setName) {
        BitSet s = new BitSet();

        for (String content : setContents) {
            s.set(indexOf(content));
        }

        fSetList.add(s);
        setNames.add(setName);
    }

    /**
     * Gets the number of sets in the group
     * @return number of sets in the group
     */
    public int n() {
        return fSetList.size();
    }

    public BitSet get(int idx) {   //idx must be less than .n()
        return (BitSet) fSetList.get(idx).clone();
    }

    public String[] getNames() {
        String[] ret = new String[setNames.size()];
        for (int i = 0; i < ret.length; i++) {
            ret[i] = setNames.get(i);
        }
        return ret;
    }

    public BitSet all() {  //return a bitset of the entire universe
        BitSet ret = new BitSet(uni.size());
        ret.flip(0, uni.size());
        return ret;
    }

    public int numX() {  //the number of intersections possible (some may be empty)
        int ret = 1;
        for (int i = 0; i < fSetList.size(); i++) {
            ret *= 2;
        }
        return ret;
    }

    public boolean isSetInX(int s, int x) {   //s<.n(),  x<.numX()
        if (!(0 <= s && s < n() && 0 <= x && x < numX())) {
            throw new AssertionError();
        }
        return ((x & (1 << s)) > 0);
    }

    public int countX(int x) {
        if (!(0 <= x && x < numX())) {
            throw new AssertionError();
        }
        BitSet xset = all();
        for (int s = 0; s < n(); s++) {
            if (isSetInX(s, x)) {
                xset.and(get(s));
            } else {
                xset.andNot(get(s));
            }
        }
        return xset.cardinality();
    }

    private int indexOf(String elt) {
        for (int i = 0; i < uni.size(); i++) {
            if (elt.equals(uni.get(i))) {
                return i;
            }
        }
        uni.add(elt);
        return uni.size() - 1;
    }

}
