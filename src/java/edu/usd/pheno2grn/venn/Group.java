/*
 * A group is a group of sets of elements (genes?) 
 *    You can add a set by giving a list of strings in a file
 */
package edu.usd.pheno2grn.venn;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

/**
 * Represents the Venn Digram data as a list of BitSets. Calculates
 * intersections.
 */
public class Group {

    private List<String> universe = new ArrayList<>();
    private List<BitSet> fSetList = new ArrayList<>(); //the list of sets
    private List<String> setNames = new ArrayList<>();

    /**
     * Adds a set to the group.
     *
     * @param setContents List of elements in the set.
     * @param setName Name of the set.
     */
    public void add(List<String> setContents, String setName) {
        BitSet s = new BitSet();

        for (String content : setContents) {
            s.set(indexOf(content));
        }

        fSetList.add(s);
        setNames.add(setName);
    }

    /**
     * Gets the number of sets in the group.
     *
     * @return Number of sets in the group.
     */
    public int numOfSets() {
        return fSetList.size();
    }

    /**
     * Gets the BitSet at index in the complete bit set list
     *
     * @param index Must be 0 lte index lt numberOfSets
     * @return A copy of the BitSet at the position in the number of
     */
    private BitSet copyBitSet(int index) {
        if (index < 0 || index >= this.numOfSets()) {
            throw new UnsupportedOperationException("Index must be between 0 and " + this.numOfSets());
        }
        return (BitSet) fSetList.get(index).clone();
    }

    /**
     * Gets an array of the set names.
     *
     * @return A new array containing the names of the set.
     */
    public String[] getNames() {
        return setNames.toArray(new String[setNames.size()]);
    }

    /**
     * Creates a BitSet the size of the universe of strings with each position
     * set.
     *
     * @return A new BitSet the size of the universe with every position set.
     */
    private BitSet generateUniversal() {
        BitSet ret = new BitSet(universe.size());
        ret.flip(0, universe.size());
        return ret;
    }

    /**
     * Returns the number of possible intersections. This number includes the
     * set of things outside of the Venn diagram, so the number of possible
     * intersections which will actually appear in the diagram is
     * numOfIntersections()-1.
     *
     * @return Number of possible intersections (will actually be one less than
     * this)
     */
    int numOfIntersections() {
        int ret = 1;
        for (int i = 0; i < fSetList.size(); i++) {
            ret *= 2;
        }
        return ret;
    }

    /**
     * Determines if a set is involved in a particular intersection. Because the
     * number of possible intersections 2^x-1 where x is 1-5 (number of sets),
     * the binary representation of the number of possible intersections is all
     * ones for each set in the diagram. For example 4 sets have 2^4-1=15
     * intersections. The binary representation of 15 is 1111 (notice 4 ones).
     * For example, with 5 sets, there are 31 possible intersections. Notice
     * each set number will return a non-zero number when "anded" with 31: 1,
     * 10,100,1000,10000 are the set numbers and 11111 is 31. That means the
     * intersection number 31 represents the intersection of all five sets.
     *
     * @param s The number of the set
     * @param x The number of the intersection
     * @return True if the set is involved in the interaction, false otherwise.
     */
    public boolean isSetInX(int s, int x) {
        if (!(0 <= s && s < numOfSets() && 0 <= x && x < numOfIntersections())) {
            throw new AssertionError();
        }
        return ((x & (1 << s)) > 0);
    }

    /**
     * Gets the count of an intersection number.
     *
     * @param x The intersection number for which the count is desired.
     * @return The number of genes belonging to the intersection.
     */
    public int countX(int x) {
        if (!(0 <= x && x < numOfIntersections())) {
            throw new AssertionError();
        }
        BitSet xset = generateUniversal();
        for (int s = 0; s < numOfSets(); s++) {
            //seeing if an interaction number involves a particular set
            if (isSetInX(s, x)) {
                xset.and(copyBitSet(s));
            } else {
                xset.andNot(copyBitSet(s));
            }
        }
        return xset.cardinality();
    }

    /**
     * Gets the position of a set element in the universe list. If an element is
     * not in the universe, add it to the universe and return the newly added
     * position.
     *
     * @param element Should not be null or empty.
     * @return Position of the element in the universe. If not yet in universe,
     * add it and return the new position.
     */
    private int indexOf(String element) {
        for (int i = 0; i < universe.size(); i++) {
            if (element.equals(universe.get(i))) {
                return i;
            }
        }

        //element is not in the universe, now adding it
        universe.add(element);
        return universe.size() - 1;
    }

}
