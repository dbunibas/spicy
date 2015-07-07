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

import it.unibas.spicy.model.datasource.INode;
import it.unibas.spicy.model.mapping.EngineConfiguration;
import it.unibas.spicy.utility.SpicyEngineUtility;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import junit.framework.TestCase;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public abstract class AbstractInstanceChecker implements IInstanceChecker {

    private static Log logger = LogFactory.getLog(AbstractInstanceChecker.class);

    protected EngineConfiguration config;

    public AbstractInstanceChecker(EngineConfiguration config) {
        this.config = config;
    }

    protected abstract boolean checkNullOrSkolem(Object instanceValue);

    protected abstract boolean isSkolem(INode attributeNode);

    public abstract void checkInstance(INode instance, String expectedInstanceFile) throws Exception;

    protected void checkSetTuples(INode instance, INode setNode, Map<String, List<IExpectedTuple>> instanceMap, String expectedInstanceFile) {
        String setName = setNode.getLabel();
        List<IExpectedTuple> expectedTuples = instanceMap.get(setName.toLowerCase());
        if (setNode.getChildren().size() == 0) {
            TestCase.assertNull("Unable to find generated tuples for set: " + setName + " in " + expectedInstanceFile, expectedTuples);
            return;
        }
        TestCase.assertNotNull("Unable to find expected tuples for set: " + setName + " in " + expectedInstanceFile, expectedTuples);
        INode setNodeClone = setNode.clone();
        for (IExpectedTuple expectedTuple : expectedTuples) {
            TestCase.assertTrue("Unable to find tuple : " + expectedTuple.toString() + getMessage(expectedInstanceFile, instanceMap, instance), checkTupleValues(expectedTuple, setNodeClone));
        }
        TestCase.assertEquals("Extra tuples were generated: " + setNodeClone.toShortString() + getMessage(expectedInstanceFile, instanceMap, instance) + "\n" + setNodeClone.toShortString(), 0, setNodeClone.getChildren().size());
    }

    private String getMessage(String expectedInstanceFile, Map<String, List<IExpectedTuple>> instanceMap, INode instance) {
        StringBuilder result = new StringBuilder();
        result.append("\n");
        result.append("Instance file: ").append(expectedInstanceFile).append("\n");
        result.append("Expected tuples:\n").append(SpicyEngineUtility.printMap(instanceMap));
        result.append("Generated tuples:\n").append(instance.toShortString());
        return result.toString();
    }

    private String getJoinMessage(String expectedInstanceFile, Map<String, List<IExpectedTuple>> instanceMap, INode instance, List<ExpectedTuplePair> expectedTuplePairs, List<SpicyTuplePair> spicyTuplePairs) {
        StringBuilder result = new StringBuilder();
        result.append("\n").append(getMessage(expectedInstanceFile, instanceMap, instance)).append("\n");
        result.append("Expected tuple pairs:\n").append(SpicyEngineUtility.printCollection(expectedTuplePairs)).append("\n");
        result.append("Instance tuple pairs:\n").append(SpicyEngineUtility.printCollection(spicyTuplePairs)).append("\n");
        return result.toString();
    }

    private boolean checkTupleValues(IExpectedTuple expectedTuple, INode setNode) {
        for (Iterator<INode> tupleIterator = setNode.getChildren().iterator(); tupleIterator.hasNext();) {
            INode instanceTuple = tupleIterator.next();
            if (matches(instanceTuple, expectedTuple)) {
                tupleIterator.remove();
                return true;
            }
        }
        return false;
    }

    private boolean matches(INode instanceTuple, IExpectedTuple expectedTuple) {
        if (instanceTuple.getChildren().size() != expectedTuple.getValues().size()) {
            return false;
        }
        for (int i = 0; i < expectedTuple.getValues().size(); i++) {
            IExpectedValue value = expectedTuple.getValues().get(i);
            Object instanceValue = instanceTuple.getChild(i).getChild(0).getValue();
            if (value.isNullOrSkolem()) {
                if (!checkNullOrSkolem(instanceValue)) {
                    return false;
                }
            } else if (!value.equals(instanceValue)) {
                return false;
            }
        }
        return true;
    }

    protected void checkJoins(INode instance, Map<String, List<IExpectedTuple>> instanceMap, String expectedInstanceFile) {
        List<ExpectedTuplePair> expectedTuplePairs = buildExpectedTuplePairs(instanceMap);
        List<SpicyTuplePair> spicyTuplePairs = buildSpicyTuplePairs(instance);
        List<SpicyTuplePair> spicyTuplePairsClone = new ArrayList<SpicyTuplePair>(spicyTuplePairs);
        for (ExpectedTuplePair expectedTuplePair : expectedTuplePairs) {
            TestCase.assertTrue("Unable to find tuple pair: " + expectedTuplePair.toString() + getJoinMessage(expectedInstanceFile, instanceMap, instance, expectedTuplePairs, spicyTuplePairs), checkTuplePairs(expectedTuplePair, spicyTuplePairsClone));
        }
        TestCase.assertEquals("Extra tuple pairs were generated: " + spicyTuplePairsClone + getJoinMessage(expectedInstanceFile, instanceMap, instance, expectedTuplePairs, spicyTuplePairs), 0, spicyTuplePairsClone.size());
    }

    private List<ExpectedTuplePair> buildExpectedTuplePairs(Map<String, List<IExpectedTuple>> instanceMap) {
        List<ExpectedTuplePair> pairs = new ArrayList<ExpectedTuplePair>();
        List<IExpectedTuple> tuples = extractExpectedTuples(instanceMap);
        for (int i = 0; i < tuples.size() - 1; i++) {
            IExpectedTuple currentTuple = tuples.get(i);
            if (currentTuple.containsNullOrSkolem()) {
                for (int j = 0; j < currentTuple.getValues().size(); j++) {
                    IExpectedValue expectedValue = currentTuple.getValues().get(j);
                    if (expectedValue.isNullOrSkolem()) {
                        findExpectedTuplePairs(tuples, i, j, expectedValue, pairs);
                    }
                }
            }
        }
        return pairs;
    }

    private List<IExpectedTuple> extractExpectedTuples(Map<String, List<IExpectedTuple>> instanceMap) {
        List<IExpectedTuple> allTuples = new ArrayList<IExpectedTuple>();
        Collection<List<IExpectedTuple>> tuples = instanceMap.values();
        for (Iterator<List<IExpectedTuple>> it = tuples.iterator(); it.hasNext();) {
            List<IExpectedTuple> list = it.next();
            allTuples.addAll(list);
        }
        return allTuples;
    }

    private List<SpicyTuplePair> buildSpicyTuplePairs(INode instance) {
        List<SpicyTuplePair> pairs = new ArrayList<SpicyTuplePair>();
        List<INode> tuples = extractSpicyTuplesWithSkolem(instance);
        for (int i = 0; i < tuples.size() - 1; i++) {
            INode currentTuple = tuples.get(i);
            for (int j = 0; j < currentTuple.getChildren().size(); j++) {
                INode attributeNode = currentTuple.getChild(j);
                if (isSkolem(attributeNode)) {
                    findSpicyTuplePairs(tuples, i, j, attributeNode, pairs);
                }
            }
        }
        return pairs;
    }

    private List<INode> extractSpicyTuplesWithSkolem(INode instance) {
        List<INode> tupleNodes = new ArrayList<INode>();
        for (INode setNode : instance.getChildren()) {
            for (INode tupleNode : setNode.getChildren()) {
                if (containsSkolem(tupleNode)) {
                    tupleNodes.add(tupleNode);
                }
            }
        }
        return tupleNodes;
    }

    private boolean containsSkolem(INode tupleNode) {
        for (INode attributeNode : tupleNode.getChildren()) {
            if (isSkolem(attributeNode)) {
                return true;
            }
        }
        return false;
    }

    private List<Integer> containsSameSkolem(Object skolemValue, INode tupleNode) {
        List<Integer> result = new ArrayList<Integer>();
        for (int i = 0; i < tupleNode.getChildren().size(); i++) {
            INode attributeNode = tupleNode.getChild(i);
            Object value = attributeNode.getChild(0).getValue();
            if (isSkolem(attributeNode) && skolemValue.equals(value)) {
                result.add(i);
            }
        }
        return result;
    }
    private void findExpectedTuplePairs(List<IExpectedTuple> tuples, int firstTuplePosition, int firstSkolemPosition, IExpectedValue currentExpectedValue, List<ExpectedTuplePair> tuplePairs) {
        IExpectedTuple firstTuple = tuples.get(firstTuplePosition);
        for (int i = firstTuplePosition + 1; i < tuples.size(); i++) {
            IExpectedTuple secondTuple = tuples.get(i);
            List<Integer> secondSkolemPositions = secondTuple.containsSameSkolem(currentExpectedValue);
            for (Integer secondSkolemPosition : secondSkolemPositions) {
                ExpectedTuplePair tuplePair = new ExpectedTuplePair(firstTuple, secondTuple, firstSkolemPosition, secondSkolemPosition);
                tuplePairs.add(tuplePair);
            }
        }
    }

    private void findSpicyTuplePairs(List<INode> tuples, int firstTuplePosition, int firstSkolemPosition, INode currentAttributeNode, List<SpicyTuplePair> tuplePairs) {
        INode firstTuple = tuples.get(firstTuplePosition);
        Object skolemValue = currentAttributeNode.getChild(0).getValue();
        for (int i = firstTuplePosition + 1; i < tuples.size(); i++) {
            INode secondTuple = tuples.get(i);
            List<Integer> secondSkolemPositions = containsSameSkolem(skolemValue, secondTuple);
            for (Integer secondSkolemPosition : secondSkolemPositions) {
                SpicyTuplePair tuplePair = new SpicyTuplePair(firstTuple, secondTuple, firstSkolemPosition, secondSkolemPosition);
                tuplePairs.add(tuplePair);
            }
        }
    }

    private boolean checkTuplePairs(ExpectedTuplePair expectedTuplePair, List<SpicyTuplePair> spicyTuplePairs) {
        for (Iterator<SpicyTuplePair> tupleIterator = spicyTuplePairs.iterator(); tupleIterator.hasNext();) {
            SpicyTuplePair spicyTuplePair = tupleIterator.next();
            if (matchesPair(spicyTuplePair, expectedTuplePair)) {
                tupleIterator.remove();
                return true;
            }
        }
        return false;
    }

    private boolean matchesPair(SpicyTuplePair spicyTuplePair, ExpectedTuplePair expectedTuplePair) {
        INode firstSpicyTupleNode = spicyTuplePair.getFirstTuple();
        INode secondSpicyTupleNode = spicyTuplePair.getSecondTuple();
        IExpectedTuple firstExpectedTuple = expectedTuplePair.getFirstTuple();
        IExpectedTuple secondExpectedTuple = expectedTuplePair.getSecondTuple();
        if ((matches(firstSpicyTupleNode, firstExpectedTuple) && matches(secondSpicyTupleNode, secondExpectedTuple)) ||
                (matches(secondSpicyTupleNode, firstExpectedTuple) && matches(firstSpicyTupleNode, secondExpectedTuple))) {
            return true;
        }
        return false;
    }
}

class ExpectedTuplePair {

    private IExpectedTuple firstTuple;
    private IExpectedTuple secondTuple;
    private int firstSkolemPosition;
    private int secondSkolemPosition;

    public ExpectedTuplePair(IExpectedTuple firstTuple, IExpectedTuple secondTuple, int firstSkolemPosition, int secondSkolemPosition) {
        this.firstTuple = firstTuple;
        this.secondTuple = secondTuple;
        this.firstSkolemPosition = firstSkolemPosition;
        this.secondSkolemPosition = secondSkolemPosition;
    }

    public int getFirstSkolemPosition() {
        return firstSkolemPosition;
    }

    public IExpectedTuple getFirstTuple() {
        return firstTuple;
    }

    public int getSecondSkolemPosition() {
        return secondSkolemPosition;
    }

    public IExpectedTuple getSecondTuple() {
        return secondTuple;
    }

    @Override
    public String toString() {
        return "[P=" + firstSkolemPosition + "]" + firstTuple + " - " + "[P=" + secondSkolemPosition + "]" + secondTuple;
    }
}
class SpicyTuplePair {

    private INode firstTuple;
    private INode secondTuple;
    private int firstSkolemPosition;
    private int secondSkolemPosition;

    public SpicyTuplePair(INode firstTuple, INode secondTuple, int firstSkolemPosition, int secondSkolemPosition) {
        this.firstTuple = firstTuple;
        this.secondTuple = secondTuple;
        this.firstSkolemPosition = firstSkolemPosition;
        this.secondSkolemPosition = secondSkolemPosition;
    }

    public int getFirstSkolemPosition() {
        return firstSkolemPosition;
    }

    public INode getFirstTuple() {
        return firstTuple;
    }

    public int getSecondSkolemPosition() {
        return secondSkolemPosition;
    }

    public INode getSecondTuple() {
        return secondTuple;
    }

    @Override
    public String toString() {
        return "[P=" + firstSkolemPosition + "]" + firstTuple.toShortString() + " - " + "[P=" + secondSkolemPosition + "]" + secondTuple.toShortString();
    }
}
