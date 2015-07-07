/*
    Copyright (C) 2007-2011  Database Group - Universita' della Basilicata
    Giansalvatore Mecca - giansalvatore.mecca@unibas.it
    Salvatore Raunich - salrau@gmail.com

    This file is part of ++Spicy - a Schema Mapping and Data Exchange Tool
    
    ++Spicy is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    any later version.

    ++Spicy is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with ++Spicy.  If not, see <http://www.gnu.org/licenses/>.
 */
 
package it.unibas.spicy.test.tools;

import it.unibas.spicy.utility.GenericListGenerator;
import java.util.ArrayList;
import java.util.List;
import junit.framework.TestCase;

public class InstanceGenerator extends TestCase {

    public void testInstance() {
        List<Tuple> result = new ArrayList<Tuple>();
        List<String> elements = new ArrayList<String>();
        elements.add("a");
        elements.add("b");
        elements.add("c");
//        elements.add("d");
//        elements.add("e");
        int tupleSize = 3;

//        GenericCombinationsGenerator<String> combinationGenerator = new GenericCombinationsGenerator(elements, elements.size());
//
//        while (combinationGenerator.hasMoreElements()) {
//            List<String> combination = combinationGenerator.nextElement();
//            GenericPermutationsGenerator<String> permutationGenerator = new GenericPermutationsGenerator<String>(combination);
//            while (permutationGenerator.hasMoreElements()) {
//                List<String> permutation = permutationGenerator.nextElement();
//                Tuple tuple = new Tuple(permutation);
//                if (!result.contains(tuple)) {
//                    result.add(tuple);
//                }
//            }
//        }

        GenericListGenerator<String> listGenerator = new GenericListGenerator<String>();
        List<List<String>> input = new ArrayList<List<String>>();
        for (int i = 0; i < tupleSize; i++) {
            input.add(elements);
        }
        List<List<String>> combinations = listGenerator.generateListsOfElements(input);
        for (List<String> combination : combinations) {
            Tuple tuple = new Tuple(combination);
            result.add(tuple);
        }
        printResult(result);

        for (int i = 0; i < result.size() * 1000; i++) {
            int firstPos = (int)(Math.random() * result.size());
            int secondPos = (int)(Math.random() * result.size());
            Tuple tmp = result.get(firstPos);
            result.set(firstPos, result.get(secondPos));
            result.set(secondPos, tmp);
        }
        printResult(result);

    }

    private void printResult(List<Tuple> result) {
        System.out.println("Instance - size: " + result.size());
        for (Tuple tuple : result) {
            System.out.println(tuple);
        }
        System.out.println("----------------------------------");
    }
}

class Tuple {

    private List<String> elements;

    public Tuple(List<String> elements) {
        this.elements = elements;
    }

    public List<String> getElements() {
        return elements;
    }

    public boolean equals(Object o) {
        if (!(o instanceof Tuple)) {
            return false;
        }
        Tuple otherTuple = (Tuple) o;
        return this.toString().equals(otherTuple.toString());
    }

    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append("[");
        for (int i = 0; i < elements.size(); i++) {
            result.append("A").append(i).append(": ").append(elements.get(i)).append(" ");
        }
        result.append("]");
        return result.toString();
    }
}