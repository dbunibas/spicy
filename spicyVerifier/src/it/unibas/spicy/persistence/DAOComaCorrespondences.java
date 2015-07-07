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
 
package it.unibas.spicy.persistence;

import it.unibas.spicy.attributematch.AttributeCorrespondences;
import it.unibas.spicy.model.correspondence.ValueCorrespondence;
import it.unibas.spicy.model.datasource.DataSource;
import it.unibas.spicy.model.datasource.INode;
import it.unibas.spicy.model.datasource.operators.FindNode;
import it.unibas.spicy.model.mapping.proxies.ConstantDataSourceProxy;
import it.unibas.spicy.model.paths.PathExpression;
import it.unibas.spicy.model.paths.operators.GeneratePathExpression;
import it.unibas.spicy.structuralanalysis.operators.CheckNodeProperties;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class DAOComaCorrespondences {

    private static Log logger = LogFactory.getLog(DAOComaCorrespondences.class);
    private GeneratePathExpression pathGenerator = new GeneratePathExpression();
    private FindNode nodeFinder = new FindNode();
    private CheckNodeProperties nodeChecker = new CheckNodeProperties();

    public AttributeCorrespondences loadComaAttributeCorrespondences(String comaLogFile, DataSource source, DataSource target) throws DAOException {
        List<String[]> comaCorrespondences = readComaFile(comaLogFile);
        AttributeCorrespondences attributeCorrespondences = generateAttributeCorrespondences(source, target, comaCorrespondences);
        return attributeCorrespondences;
    }

    public List<ValueCorrespondence> loadComaCorrespondences(String comaLogFile, DataSource source, DataSource target) throws DAOException {
        List<String[]> comaCorrespondences = readComaFile(comaLogFile);
        AttributeCorrespondences attributeCorrespondences = generateAttributeCorrespondences(source, target, comaCorrespondences);
        return attributeCorrespondences.getCorrespondences();
    }

    private List<String[]> readComaFile(String comaLogFile) throws DAOException {
        List<String[]> listOfStrings = new ArrayList<String[]>();
        BufferedReader reader = null;
        try {
            FileReader fileReader = new FileReader(comaLogFile);
            reader = new BufferedReader(fileReader);
            logger.info(" reader: " + reader);
            // skipped lines
            reader.readLine();
            reader.readLine();
            reader.readLine();
            reader.readLine();
            reader.readLine();
            reader.readLine();
            reader.readLine();
            reader.readLine();
            // skipped lines
            String line = null;
            while ((line = reader.readLine()) != null && !(line.trim().startsWith("+ Total:"))) {
                if (logger.isDebugEnabled()) logger.debug(" > Current Line: " + line);
                String[] correspondence = extractCorrespondence(line.substring(3).trim());
                listOfStrings.add(correspondence);
            }
        } catch (FileNotFoundException fnfe) {
            throw new DAOException(" File not found: " + fnfe);
        } catch (IOException ioe) {
            throw new DAOException(" Error: " + ioe);
        } catch (NoSuchElementException nse) {
            throw new DAOException(" Error in file format: " + nse);
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException ioe) {
            }
        }

        printStrings(listOfStrings);
        return listOfStrings;
    }

    private static String[] extractCorrespondence(String line) {
        if (logger.isDebugEnabled()) logger.debug(" > Line: " + line);
        String[] strings = new String[3];
        StringTokenizer tokenizer = new StringTokenizer(line, ":");
        String matchToken = tokenizer.nextToken();
        String valueToken = tokenizer.nextToken().trim();
        StringTokenizer matchTokenizer = new StringTokenizer(matchToken, "<->");
        strings[0] = matchTokenizer.nextToken().trim();
        strings[1] = matchTokenizer.nextToken().trim();
        strings[2] = valueToken;
        return strings;
    }

    private AttributeCorrespondences generateAttributeCorrespondences(DataSource source, DataSource target, List<String[]> comaCorrespondences) {
        AttributeCorrespondences correspondences = new AttributeCorrespondences(source, target);
        for (String[] comaCorrespondence : comaCorrespondences) {
            String sourcePath = comaCorrespondence[0];
            String targetPath = comaCorrespondence[1];
            double similarity = Double.parseDouble(comaCorrespondence[2]);
            addCorrespondence(source, target, sourcePath, targetPath, similarity, correspondences);
        }
        return correspondences;
    }

    private void addCorrespondence(DataSource source, DataSource target,
            String sourcePathString, String targetPathString, double similarity, AttributeCorrespondences correspondences) {
        PathExpression sourcePath = pathGenerator.generatePathFromString(sourcePathString);
        INode sourceNode = nodeFinder.findNodeInSchema(sourcePath, new ConstantDataSourceProxy(source));
        PathExpression targetPath = pathGenerator.generatePathFromString(targetPathString);
        INode targetNode = nodeFinder.findNodeInSchema(targetPath, new ConstantDataSourceProxy(target));
        if (!isToExclude(sourceNode, source) && !isToExclude(targetNode, target)) {
            correspondences.addCorrespondence(sourcePath, targetPath, similarity);
        }
    }

    private boolean isToExclude(INode node, DataSource dataSource) {
        return node.isExcluded() || nodeChecker.isPrimaryKey(node, dataSource) || nodeChecker.isForeignKey(node, dataSource);
    }

    private static void printStrings(List<String[]> list) {
        String str = "\n\n ";
        for (String[] vector : list) {
            str += vector[0] + "\n";
            str += vector[1] + "\n";
            str += vector[2] + "\n---------------------- \n";
        }
        logger.info(str);
    }
}
