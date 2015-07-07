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

import it.unibas.spicy.model.datasource.INode;
import it.unibas.spicy.model.paths.PathExpression;
import it.unibas.spicy.structuralanalysis.SimilarityCheck;
import it.unibas.spicy.structuralanalysis.circuits.Circuit;
import java.util.List;

public interface IBuildCircuitStrategy {
    
    public static final String CONSISTENCY = "-CONSISTENCY";
    public static final String STRESS = "-STRESS";
    public static final String STATISTIC_CONSISTENCY = "-STATISTIC CONSISTENCY";
    public static final String STATISTIC_STRESS = "-STATISTIC STRESS";
    public static final String DENSITY = "-DENSITY";
    public static final String LEVEL = "-LEVEL";
    public static final String LOAD = "-LOAD";
    
    public Circuit buildAndSolveCircuit(INode schema, SimilarityCheck similarityCheck, boolean excludeNodes);

}
