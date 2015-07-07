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
import it.unibas.spicy.model.datasource.INode;
import it.unibas.spicy.model.paths.PathExpression;
import it.unibas.spicy.structuralanalysis.SimilarityCheck;
import it.unibas.spicy.structuralanalysis.circuits.Circuit;
import it.unibas.spicy.structuralanalysis.circuits.EmptyCircuitException;
import it.unibas.spicy.structuralanalysis.circuits.Generator;
import it.unibas.spicy.structuralanalysis.circuits.ICircuitElement;
import it.unibas.spicy.structuralanalysis.circuits.NullCircuit;
import it.unibas.spicy.structuralanalysis.circuits.Resistor;
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

public class BuildCircuitSSDensityReducedWithCaching implements IBuildCircuitStrategy {
    
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
    
    private Map<INode, Circuit> circuitCache = new HashMap<INode, Circuit>();
    
    public BuildCircuitSSDensityReducedWithCaching() {}
    
    public BuildCircuitSSDensityReducedWithCaching(IFindNodesToExclude excluder, double multiplyingFactorForElements,
            double multiplyingFactorForStatisticElements, double levelResistance, double externalResistance) {
        this.excluder = excluder;
        this.multiplyingFactorForElements = multiplyingFactorForElements;
        this.multiplyingFactorForStatisticElements = multiplyingFactorForStatisticElements;
        this.levelResistance = levelResistance;
        this.externalResistance = externalResistance;
    }
    
    public Circuit buildAndSolveCircuit(INode schema, SimilarityCheck similarityCheck, boolean excludeNodes) {
        Circuit circuit = getCircuit(schema, similarityCheck, excludeNodes); // WITH CACHING
        //Circuit circuit = buildCircuit(schema, similarityCheck); // WITHOUT CACHING
        if (circuit != null) {
            generateMatrix(circuit);
            solveCircuit(circuit);
            if (logger.isDebugEnabled()) logger.debug("Circuit: " + circuit);
        } else {
            if (logger.isDebugEnabled()) logger.debug("BuildCircuitSSDensityReducedWithCaching.buildAndSolveCircuit = Generated a NullCircuit");
            circuit = NullCircuit.getInstance();
        }
        return circuit;
    }
    
    private Circuit getCircuit(INode schema, SimilarityCheck similarityCheck, boolean excludeNodes) {
        // HANDLES CACHING
        Circuit circuit = this.circuitCache.get(schema);
        if (circuit == null) {
            circuit = buildCircuit(schema, similarityCheck, excludeNodes);
            this.circuitCache.put(schema, circuit);
        }
        return circuit;
    }
    
    private Circuit buildCircuit(INode schema, SimilarityCheck similarityCheck, boolean excludeNodes) {
        List<PathExpression> exclusionList = new ArrayList<PathExpression>();
        try {
            if (excludeNodes) {
                exclusionList = excluder.findAttributesToExclude(schema, similarityCheck);
            }
            BuildCircuitSSDensityReducedVisitor visitor = new BuildCircuitSSDensityReducedVisitor(multiplyingFactorForElements, multiplyingFactorForStatisticElements, levelResistance, externalResistance, exclusionList);
            schema.accept(visitor);
            return visitor.getResult();
        } catch (EmptyCircuitException e) {
            return null;
        }
    }
    
    private void generateMatrix(Circuit circuit) {
        MatrixGeneratorDensityReducedVisitor visitor = new MatrixGeneratorDensityReducedVisitor();
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



