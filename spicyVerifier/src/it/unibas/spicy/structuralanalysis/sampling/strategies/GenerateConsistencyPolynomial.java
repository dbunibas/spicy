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
 
package it.unibas.spicy.structuralanalysis.sampling.strategies;

import com.google.inject.Inject;
import it.unibas.spicy.Application;
import it.unibas.spicy.SpicyConstants;
import it.unibas.spicy.model.datasource.nodes.AttributeNode;
import it.unibas.spicy.structuralanalysis.sampling.CharacterCategory;
import it.unibas.spicy.structuralanalysis.sampling.Distribution;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class GenerateConsistencyPolynomial implements IGenerateConsistencyStrategy {
    
    private static Log logger = LogFactory.getLog(GenerateConsistencyPolynomial.class);
    
    private CharacterCategory[] categories;
    private Map<Integer, Double> grades;
    private IGenerateCharCategoryDistributionStrategy charCategoryDistributionGenerator;
    
    //TODO: clean dependency injection
    @Inject()
    public GenerateConsistencyPolynomial(IGenerateCharCategoryDistributionStrategy charCategoryDistributionGenerator) {
        this.categories = Application.getInstance().getCategories();
        this.grades = Application.getInstance().getGrades();
        this.charCategoryDistributionGenerator = charCategoryDistributionGenerator;
    }

    public GenerateConsistencyPolynomial(CharacterCategory[] categories, Map<Integer, Double> grades, 
            IGenerateCharCategoryDistributionStrategy charCategoryDistributionGenerator) {
        this.categories = categories;
        this.grades = grades;
        this.charCategoryDistributionGenerator = charCategoryDistributionGenerator;
    }
    
    public void generateConsistency(AttributeNode node) {
        charCategoryDistributionGenerator.generateCharCategoryDistribution(node);
        //TODO: remove
        Distribution charCategoryDistribution = (Distribution)node.getAnnotation(SpicyConstants.CHAR_CATEGORY_DISTRIBUTION);
        assert(charCategoryDistribution != null) : "Char category distribution missing in attribute: " + node.getLabel();
        double sumOfValues = charCategoryDistribution.getSumOfValues(categories);
        double consistency = 0.0;
        for (int i = 0; i < categories.length; i++) {
            CharacterCategory category = categories[i];
            double frequency = (double)charCategoryDistribution.getValue(category);
            double grade = grades.get(category.getId());
            consistency += frequency / sumOfValues * Math.pow(2, grade);
        }
        node.addAnnotation(SpicyConstants.CONSISTENCY, consistency);
    }
            
}
