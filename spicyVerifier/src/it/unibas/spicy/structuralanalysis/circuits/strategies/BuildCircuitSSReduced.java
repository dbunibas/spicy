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
 
package it.unibas.spicy.structuralanalysis.circuits.strategies;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import it.unibas.spicy.Application;
import it.unibas.spicy.SpicyConstants;
import it.unibas.spicy.model.datasource.INode;
import it.unibas.spicy.model.datasource.nodes.AttributeNode;
import it.unibas.spicy.model.datasource.nodes.LeafNode;
import it.unibas.spicy.model.datasource.nodes.MetadataNode;
import it.unibas.spicy.model.datasource.nodes.SequenceNode;
import it.unibas.spicy.model.datasource.nodes.SetNode;
import it.unibas.spicy.model.datasource.nodes.TupleNode;
import it.unibas.spicy.model.datasource.nodes.UnionNode;
import it.unibas.spicy.model.datasource.operators.INodeVisitor;
import it.unibas.spicy.model.paths.PathExpression;
import it.unibas.spicy.model.paths.operators.GeneratePathExpression;
import it.unibas.spicy.structuralanalysis.SimilarityCheck;
import it.unibas.spicy.structuralanalysis.circuits.Circuit;
import it.unibas.spicy.structuralanalysis.circuits.CircuitNode;
import it.unibas.spicy.structuralanalysis.circuits.EmptyCircuitException;
import it.unibas.spicy.structuralanalysis.circuits.Generator;
import it.unibas.spicy.structuralanalysis.circuits.ICircuitElement;
import it.unibas.spicy.structuralanalysis.circuits.NullCircuit;
import it.unibas.spicy.structuralanalysis.circuits.Resistor;
import it.unibas.spicy.structuralanalysis.circuits.operators.CalculateAttributeFeatures;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jscience.mathematics.numbers.Float64;
import org.jscience.mathematics.vectors.Float64Matrix;
import org.jscience.mathematics.vectors.Float64Vector;
import org.jscience.mathematics.vectors.Vector;


//////////////////////////////////////////////////////////////////////////////////////////////////////
//                                       CIRCUIT BUILDER
/////////////////////////////////////////////////////////////////////////////////////////////////////

public class BuildCircuitSSReduced implements IBuildCircuitStrategy {
    
    private Log logger = LogFactory.getLog(this.getClass());
    
    @Inject() @Named(Application.MULTIPLYING_FACTOR_FOR_ELEMENTS_KEY)
    private double multiplyingFactorForElements;
    @Inject() @Named(Application.MULTIPLYING_FACTOR_FOR_STATISTIC_ELEMENTS_KEY)
    private double multiplyingFactorForStatisticElements;
    @Inject() @Named(Application.LEVEL_RESISTANCE_KEY)
    private double levelResistance;
    @Inject() @Named(Application.EXTERNAL_RESISTANCE_KEY)
    private double externalResistance;
    @Inject()
    private IFindNodesToExclude excluder;
    
    public BuildCircuitSSReduced() {}
    
    public BuildCircuitSSReduced(IFindNodesToExclude excluder, double multiplyingFactorForElements,
            double multiplyingFactorForStatisticElements, double levelResistance, double externalResistance) {
        this.excluder = excluder;
        this.multiplyingFactorForElements = multiplyingFactorForElements;
        this.multiplyingFactorForStatisticElements = multiplyingFactorForStatisticElements;
        this.levelResistance = levelResistance;
        this.externalResistance = externalResistance;
    }
    
    public Circuit buildAndSolveCircuit(INode schema, SimilarityCheck similarityCheck, boolean excludeNodes) {
        Circuit circuit = buildCircuit(schema, similarityCheck, excludeNodes); // WITHOUT CACHING
        if (circuit != null) {
            generateMatrix(circuit);
            solveCircuit(circuit);
            if (logger.isDebugEnabled()) logger.debug("Circuit: " + circuit);
        } else {
            if (logger.isDebugEnabled()) logger.debug("BuildCircuitSSReducedWithCaching().buildAndSolveCircuit = Generated a NullCircuit");
            circuit = NullCircuit.getInstance();
        }
        return circuit;
    }
    
    private Circuit buildCircuit(INode schema, SimilarityCheck similarityCheck, boolean excludeNodes) {
        List<PathExpression> exclusionList = new ArrayList<PathExpression>();
        try {
            if (excludeNodes) {
                exclusionList = excluder.findAttributesToExclude(schema, similarityCheck);
            }
            BuildCircuitSSReducedVisitor visitor = new BuildCircuitSSReducedVisitor(multiplyingFactorForElements,
                    multiplyingFactorForStatisticElements, levelResistance, externalResistance, exclusionList);
            schema.accept(visitor);
            return visitor.getResult();
        } catch (EmptyCircuitException e) {
            return null;
        }
    }
    
    private void generateMatrix(Circuit circuit) {
        MatrixGeneratorReducedVisitor visitor = new MatrixGeneratorReducedVisitor();
        circuit.accept(visitor);
        circuit.setA(visitor.getA());
        circuit.setB(visitor.getB());
    }
    
    private void solveCircuit(Circuit circuit) {
        Float64Matrix A = Float64Matrix.valueOf(circuit.getA());
        Float64Vector B = Float64Vector.valueOf(circuit.getB());
        Vector<Float64> X = A.solve(B);
        setCurrents(X, circuit);
    }
    
    private void setCurrents(Vector<Float64> X, Circuit circuit) {
        int numberOfResistors = getNumberOfResistors(circuit);
        double[] currents = new double[numberOfResistors];
        for (int i = 0; i < numberOfResistors; i++) {
            Float64 current = (Float64)X.get(i);
            currents[i] = current.doubleValue();
        }
        circuit.setCurrents(currents);
    }
    
    public static int getNumberOfResistors(Circuit circuit) {
        int resistors = 0;
        for (ICircuitElement element : circuit.getElements()) {
            if (element instanceof Resistor) {
                resistors++;
            }
        }
        return resistors;
    }
    
    public static int getNumberOfGenerators(Circuit circuit) {
        int generators = 0;
        for (ICircuitElement element : circuit.getElements()) {
            if (element instanceof Generator) {
                generators++;
            }
        }
        return generators;
    }
    
}
class BuildCircuitSSReducedVisitor implements INodeVisitor {
    
    private Log logger = LogFactory.getLog(this.getClass());
    
    private Circuit circuit;
    
    private double levelResistance;
    private double multiplyingFactorForElements;
    private double multiplyingFactorForStatisticElements;
    private double externalResistance;
    private List<PathExpression> exclusionList;
    
    private int nodeCounter;
    private int resistorCounter;
    private int generatorCounter;
    private Map<INode, CircuitNode> nodeMap = new HashMap<INode, CircuitNode>();
    
    private GeneratePathExpression pathGenerator = new GeneratePathExpression();
    private CalculateAttributeFeatures featureCalculator = new CalculateAttributeFeatures();
    
    private int numberOfAttributes;
    
    public BuildCircuitSSReducedVisitor(double multiplyingFactorForElements, double multiplyingFactorForStatisticElements, double levelResistance,
            double externalResistance, List<PathExpression> exclusionList) {
        this.circuit = new Circuit();
        this.multiplyingFactorForElements = multiplyingFactorForElements;
        this.multiplyingFactorForStatisticElements = multiplyingFactorForStatisticElements;
        this.levelResistance = levelResistance;
        this.externalResistance = externalResistance;
        this.exclusionList = exclusionList;
    }
    
    public void visitSetNode(SetNode node) {
        visitIntermediateNode(node);
    }
    
    public void visitTupleNode(TupleNode node) {
        visitIntermediateNode(node);
    }
    
    public void visitSequenceNode(SequenceNode node) {
        visitIntermediateNode(node);
    }
    
    public void visitUnionNode(UnionNode node) {
        visitIntermediateNode(node);
    }
    
    private void visitIntermediateNode(INode node) {
        PathExpression nodePath = pathGenerator.generatePathFromRoot(node);
        if (exclusionList.contains(nodePath)) {
            return;
        }
        double consistency = generateConsistencyForIntermediateNode(node);
        CircuitNode elementNode = new CircuitNode(nodeCounter++);
        CircuitNode fatherNode = findFatherNode(node);
        Resistor element = new Resistor(resistorCounter++, IBuildCircuitStrategy.LEVEL, consistency, node, fatherNode, elementNode);
        circuit.addElement(element);
        nodeMap.put(node, elementNode);
        for (INode child : node.getChildren()) {
            child.accept(this);
        }
    }
    
    private double generateConsistencyForIntermediateNode(INode node) {
        PathExpression nodePath = pathGenerator.generatePathFromRoot(node);
        return levelResistance * nodePath.getLevel();
    }
    
    private CircuitNode findFatherNode(INode node) {
        if (node.getFather() == null) {
            return circuit.getTopNode();
        }
        return this.nodeMap.get(node.getFather());
    }
    
    public void visitAttributeNode(AttributeNode node) {
        numberOfAttributes++;
        PathExpression nodePath = pathGenerator.generatePathFromRoot(node);
        if (exclusionList.contains(nodePath)) {
            return;
        }
        // generator source nodes are numbered with negative numbers to simplify matrix generation
        CircuitNode elementNode = new CircuitNode(CircuitNode.GENERATOR_SOURCE - generatorCounter++);
        CircuitNode fatherNode = findFatherNode(node);
        CircuitNode groundNode = this.circuit.getGroundNode();
        double consistency = featureCalculator.getNodeConsistency(node);
        double stress = featureCalculator.getNodeStress(node);
        double consistencyIC = featureCalculator.getNodeConsistencyIC(node);
        double stressIC = featureCalculator.getNodeStressIC(node);
        ICircuitElement resistor = new Resistor(resistorCounter++, IBuildCircuitStrategy.CONSISTENCY, consistency * multiplyingFactorForElements, node, fatherNode, elementNode);
        ICircuitElement generator = new Generator(-generatorCounter, IBuildCircuitStrategy.STRESS, stress * 1.0, node, elementNode, groundNode);
        circuit.addElement(resistor);
        circuit.addElement(generator);
        // generator source nodes are numbered with negative numbers to simplify matrix generation
        CircuitNode elementNodeForIC = new CircuitNode(CircuitNode.GENERATOR_SOURCE - generatorCounter++);
        ICircuitElement resistorForIC = new Resistor(resistorCounter++, IBuildCircuitStrategy.STATISTIC_CONSISTENCY, consistencyIC * multiplyingFactorForStatisticElements, node, fatherNode, elementNodeForIC);
        ICircuitElement generatorForIC = new Generator(-generatorCounter, IBuildCircuitStrategy.STATISTIC_STRESS, stressIC * multiplyingFactorForStatisticElements, node, elementNodeForIC, groundNode);
        circuit.addElement(resistorForIC);
        circuit.addElement(generatorForIC);
    }
    
    public void visitMetadataNode(MetadataNode node) {
        visitAttributeNode(node);
    }
    
    public void visitLeafNode(LeafNode node) {
        return;
    }
    
    private void addExternalResistance(Circuit circuit) {
        int numberOfResistors = BuildCircuitSSReduced.getNumberOfResistors(circuit);
        ICircuitElement externalResistor = new Resistor(numberOfResistors, IBuildCircuitStrategy.LOAD, this.externalResistance, null, circuit.getTopNode(), circuit.getGroundNode());
        circuit.addElement(externalResistor);
    }
    
    public Circuit getResult() {
        circuit.addAnnotation(SpicyConstants.TOTAL_STRESS, featureCalculator.getTotalStress());
        circuit.addAnnotation(SpicyConstants.TOTAL_STATISTIC_STRESS, featureCalculator.getTotalStatisticStress());
        circuit.addAnnotation(SpicyConstants.AVG_CONSISTENCY, featureCalculator.getTotalConsistency() / numberOfAttributes);
        circuit.addAnnotation(SpicyConstants.AVG_STATISTIC_CONSISTENCY, featureCalculator.getTotalStatisticConsistency() / numberOfAttributes);
        circuit.addAnnotation(SpicyConstants.AVG_DENSITY, featureCalculator.getTotalDensity() / numberOfAttributes);
        addExternalResistance(circuit);
        return circuit;
    }
    
}

//////////////////////////////////////////////////////////////////////////////////////////////////////
//                                       MATRIX GENERATOR
/////////////////////////////////////////////////////////////////////////////////////////////////////

class MatrixGeneratorReducedVisitor implements ICircuitVisitor {
    
    private Log logger = LogFactory.getLog(this.getClass());
    
    private Circuit circuit;
    private int numberOfResistors;
    
    // rows from 0 to r - 1 : r equations for resistors of the form ix = (Vs - Vt)/Rx
    // rows from r to r + nr - 2 : (nr - 1) equations associated with nodes (minus generator source nodes minus ground) stating balance of currents
    private double[][] A;
    // elements from 0 to r - 1 : r variables corresponding to currents in resistors
    // elements from r to r + nr - 2 : (nr - 1) variables corresponding to tensions in nodes (minus generator source nodes minus ground)
    // NOTE: ground has implicitly V = 0
    private double[] B;
    
    public void visitCircuit(Circuit circuit) {
        this.circuit = circuit;
        int dimension = findMatrixDimension(circuit);
        if (logger.isDebugEnabled()) logger.debug("Matrix dimension: " + dimension);
        A = new double[dimension][dimension];
        B = new double[dimension];
        for (ICircuitElement element : circuit.getElements()) {
            element.accept(this);
        }
        for (CircuitNode node : circuit.getNodes()) {
            node.accept(this);
        }
    }
    
    private int findMatrixDimension(Circuit circuit) {
        // one equation per resistor
        // one equation per node minus generator source nodes minus GROUND
        int numberOfElements = circuit.getElements().size();
        int numberOfGenerators = BuildCircuitSSReduced.getNumberOfGenerators(circuit);
        this.numberOfResistors = numberOfElements - numberOfGenerators;
        int numberOfNodes = circuit.getNodes().size();
        return numberOfResistors + (numberOfNodes - numberOfGenerators - 1);
    }
    
    public void visitGenerator(Generator generator) {
        // each source node for a generator has a unique inbound resistor
        ICircuitElement associatedResistor = generator.getSourceNode().getInboundElements().get(0);
        int resistorRow = associatedResistor.getId();
        if (logger.isDebugEnabled()) logger.debug("Visiting generator id[" + generator.getId() + " - Associated resistor: id[" + associatedResistor.getId() + "] - Row: " + resistorRow);
        B[resistorRow] = -1.0 * generator.getValue() / associatedResistor.getValue();
    }
    
    public void visitResistor(Resistor resistor) {
        int elementRow = resistor.getId();
        int sourceNodeIndex = this.getNodeRow(resistor.getSourceNode());
        int targetNodeIndex = this.getNodeRow(resistor.getTargetNode());
        if (logger.isDebugEnabled()) logger.debug("Visiting resistor id[" + resistor.getId() + "] - Row: " + elementRow + " - Source node index: " + sourceNodeIndex + " - Target node index: " + targetNodeIndex);
        A[elementRow][elementRow] = 1;
        assert(!resistor.getSourceNode().isGround()) : "Ground cannot be a source node: " + resistor;
        A[elementRow][sourceNodeIndex] = -1.0 / resistor.getValue();
        if (!resistor.getTargetNode().isGround() && !isGeneratorSourceNode(resistor.getTargetNode())) {
            A[elementRow][targetNodeIndex] = 1.0 / resistor.getValue();
        }
    }
    
    private boolean isGeneratorSourceNode(CircuitNode node) {
        if (node.getOutboundElements().size() != 1) {
            return false;
        }
        ICircuitElement outboundElement = node.getOutboundElements().get(0);
        return (outboundElement instanceof Generator);
    }
    
    public void visitNode(CircuitNode node) {
        if(node.isGround() || isGeneratorSourceNode(node)) {
            return;
        }
        int nodeRow = getNodeRow(node);
        if (logger.isDebugEnabled()) logger.debug("Visiting node id[" + node.getId() + "] - Row: " + nodeRow);
        for (ICircuitElement outboundElement : node.getOutboundElements()) {
            A[nodeRow][outboundElement.getId()] = 1;
        }
        for (ICircuitElement inboundElement : node.getInboundElements()) {
            A[nodeRow][inboundElement.getId()] = -1;
        }
        B[nodeRow] = 0;
    }
    
    private int getNodeRow(CircuitNode node) {
        int offsetInMatrix = numberOfResistors;
        if (node.isTop()) {
            return offsetInMatrix;
        } else if (node.isGround()) {
            return offsetInMatrix + circuit.getNodes().size() - 1;
        }
        return offsetInMatrix + node.getId() + 1;
    }
    
    public double[][] getA() {
        return A;
    }
    
    public double[] getB() {
        return B;
    }
    
}



