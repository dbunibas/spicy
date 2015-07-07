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

import it.unibas.spicy.Application;
import it.unibas.spicy.SpicyConstants;
import it.unibas.spicy.model.datasource.nodes.AttributeNode;
import it.unibas.spicy.structuralanalysis.sampling.CharacterCategory;
import it.unibas.spicy.structuralanalysis.sampling.Distribution;
import it.unibas.spicy.structuralanalysis.sampling.SampleValue;
import java.util.List;

public class GenerateStandardCharCategoryDistribution implements IGenerateCharCategoryDistributionStrategy {
        
    private CharacterCategory[] categories;

    //TODO: clean dependency injection
    public GenerateStandardCharCategoryDistribution() {
        this.categories = Application.getInstance().getCategories();
    }

    public GenerateStandardCharCategoryDistribution(CharacterCategory[] categories) {
        this.categories = categories;
    }
    
    public void generateCharCategoryDistribution(AttributeNode node) {
        Distribution charCategoryDistribution = new Distribution();
        List<SampleValue> sampleValues = (List<SampleValue>)node.getAnnotation(SpicyConstants.SAMPLES);
        assert(sampleValues != null) : "Samples missing in attribute: " + node.getLabel();
        for (SampleValue sampleValue: sampleValues) {
            Distribution charCategoryDistributionForValue = generateCharCategoryDistributionForValue(sampleValue, categories);
            charCategoryDistribution.add(charCategoryDistributionForValue);
        }
        node.addAnnotation(SpicyConstants.CHAR_CATEGORY_DISTRIBUTION, charCategoryDistribution);    
    }

    private Distribution generateCharCategoryDistributionForValue(SampleValue sampleValue, CharacterCategory[] categories) {
        Distribution charCategoryDistribution = new Distribution();
        for (int i = 0; i < categories.length; i++) {
            CharacterCategory category = categories[i];
            int frequency = this.getCategoryFrequency(sampleValue, category.getId());
            charCategoryDistribution.addValue(category, frequency);
        }
        return charCategoryDistribution;
    }    
    
    private int getCategoryFrequency(SampleValue value, int categoryId) {
        if (categoryId == SpicyConstants.NON_CAPITAL_LETTER) {
            return getNumberOfNonCapitalLetters(value);
        } else if (categoryId == SpicyConstants.CAPITAL_LETTER) {
            return getNumberOfCapitalLetters(value);
        } else if (categoryId == SpicyConstants.BLANK) {
            return getNumberOfBlanks(value);
        } else if (categoryId == SpicyConstants.COLON) {
            return getNumberOfColons(value);
        } else if (categoryId == SpicyConstants.NUMBER) {
            return getNumberOfNumbers(value);
        } else if (categoryId == SpicyConstants.AT_SIGN) {
            return getNumberOfAtSigns(value);
        } else if (categoryId == SpicyConstants.SLASH) {
            return getNumberOfSlashes(value);
        } else if (categoryId == SpicyConstants.DASH) {
            return getNumberOfDashes(value);
        } 
        return 0;
    }
    
    private int getNumberOfNonCapitalLetters(SampleValue value) {
        int counter = 0;
        for (int i = 0; i < value.getLength(); i++) {
            char character = value.getValueString().charAt(i);
            if ('a' <= character && character <= 'z') {
                counter++;
            }
        }
        return counter;
    }
    
    private int getNumberOfCapitalLetters(SampleValue value) {
        int counter = 0;
        for (int i = 0; i < value.getLength(); i++) {
            char character = value.getValueString().charAt(i);
            if ('A' <= character && character <= 'Z') {
                counter++;
            }
        }
        return counter;
    }
    
    private int getNumberOfBlanks(SampleValue value) {
        int counter = 0;
        for (int i = 0; i < value.getLength(); i++) {
            char character = value.getValueString().charAt(i);
            if (character == ' ') {
                counter++;
            }
        }
        return counter;
    }
    
    private int getNumberOfNumbers(SampleValue value) {
        int counter = 0;
        for (int i = 0; i < value.getLength(); i++) {
            char character = value.getValueString().charAt(i);
            if ('0' <= character && character <= '9') {
                counter++;
            }
        }
        return counter;
    }
    
    private int getNumberOfColons(SampleValue value) {
        int counter = 0;
        for (int i = 0; i < value.getLength(); i++) {
            char character = value.getValueString().charAt(i);
            if (character == '.' || character == ',' ||
                    character == ';') {
                counter++;
            }
        }
        return counter;
    }
        
    private int getNumberOfAtSigns(SampleValue value) {
        int counter = 0;
        for (int i = 0; i < value.getLength(); i++) {
            char character = value.getValueString().charAt(i);
            if (character == '@') {
                counter++;
            }
        }
        return counter;
    }
    
    private int getNumberOfSlashes(SampleValue value) {
        int counter = 0;
        for (int i = 0; i < value.getLength(); i++) {
            char character = value.getValueString().charAt(i);
            if (character == '\\' || character == '/') {
                counter++;
            }
        }
        return counter;
    }
    
    private int getNumberOfDashes(SampleValue value) {
        int counter = 0;
        for (int i = 0; i < value.getLength(); i++) {
            char character = value.getValueString().charAt(i);
            if (character == '-') {
                counter++;
            }
        }
        return counter;
    }
        
}
