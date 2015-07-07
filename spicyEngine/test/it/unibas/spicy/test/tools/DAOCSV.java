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

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

public class DAOCSV {

    public Map<String, List<IExpectedTuple>> loadData(String fileName) throws Exception {
        Map<String, List<IExpectedTuple>> result = new HashMap<String, List<IExpectedTuple>>();
        BufferedReader reader = new BufferedReader(new FileReader(fileName));
        String line = null;
        while ((line = reader.readLine()) != null) {
            if (line.contains("(") && line.contains(")")) {
                handleFactLine(line, result);
            } else {
                handleChaseLine(line, result);
            }
        }
        return result;
    }

    private void handleChaseLine(String line, Map<String, List<IExpectedTuple>> result) {
        StringTokenizer tokenizer = new StringTokenizer(line, ",");
        String relationName = cleanString(tokenizer.nextToken().toLowerCase());
        List<IExpectedTuple> tuples = result.get(relationName);
        if (tuples == null) {
            tuples = new ArrayList<IExpectedTuple>();
            result.put(relationName, tuples);
        }
        ChaseTuple tuple = new ChaseTuple(relationName);
        tuples.add(tuple);
        while (tokenizer.hasMoreTokens()) {
            String value = cleanString(tokenizer.nextToken());
            if (value.isEmpty()) {
                value = "NULL";
            }
            String id = cleanString(tokenizer.nextToken());
            String type = cleanString(tokenizer.nextToken());
            if (type.isEmpty()) {
                type = "--";
            }
            tuple.addValue(new ChaseValue(value, id, type));
        }
    }

    private void handleFactLine(String line, Map<String, List<IExpectedTuple>> result) {
        StringTokenizer tokenizer = new StringTokenizer(line, ",()");
        String relationName = tokenizer.nextToken().trim().toLowerCase();
        List<IExpectedTuple> tuples = result.get(relationName);
        if (tuples == null) {
            tuples = new ArrayList<IExpectedTuple>();
            result.put(relationName, tuples);
        }
        FactTuple tuple = new FactTuple(relationName);
        tuples.add(tuple);
        while (tokenizer.hasMoreTokens()) {
            String value = tokenizer.nextToken().trim();
            if (!value.equals(")")) {
                tuple.addValue(new FactValue(value));
            }
        }
    }

    public Map<String, List<IExpectedTuple>> loadDataForXML(String fileName) throws Exception {
        Map<String, List<IExpectedTuple>> result = new HashMap<String, List<IExpectedTuple>>();
        BufferedReader reader = new BufferedReader(new FileReader(fileName));
        String line = null;
        while ((line = reader.readLine()) != null) {
            if (line.contains("(") && line.contains(")")) {
                handleNestedFactLine(line, result);
            } else {
                handleNestedChaseLine(line, result);
            }
        }
        return result;
    }

    private void handleNestedChaseLine(String line, Map<String, List<IExpectedTuple>> result) {
        StringTokenizer tokenizer = new StringTokenizer(line, ",");
        String relationName = cleanString(tokenizer.nextToken().toLowerCase());
        String oid = cleanString(tokenizer.nextToken().toLowerCase());
        String fatherOid = cleanString(tokenizer.nextToken().toLowerCase());
        List<IExpectedTuple> tuples = result.get(relationName);
        if (tuples == null) {
            tuples = new ArrayList<IExpectedTuple>();
            result.put(relationName, tuples);
        }
        ChaseTuple tuple = new ChaseTuple(relationName);
        tuple.setOid(oid);
        tuple.setFatherOid(fatherOid);
        tuples.add(tuple);
        while (tokenizer.hasMoreTokens()) {
            String value = cleanString(tokenizer.nextToken());
            if (value.isEmpty()) {
                value = "NULL";
            }
            String id = cleanString(tokenizer.nextToken());
            String type = cleanString(tokenizer.nextToken());
            if (type.isEmpty()) {
                type = "--";
            }
            tuple.addValue(new ChaseValue(value, id, type));
        }
    }

    private void handleNestedFactLine(String line, Map<String, List<IExpectedTuple>> result) {
        StringTokenizer tokenizer = new StringTokenizer(line, ",");
        String relationName = cleanString(tokenizer.nextToken().toLowerCase());
        String oid = cleanString(tokenizer.nextToken().toLowerCase());
        String fatherOid = cleanString(tokenizer.nextToken().toLowerCase());
        List<IExpectedTuple> tuples = result.get(relationName);
        if (tuples == null) {
            tuples = new ArrayList<IExpectedTuple>();
            result.put(relationName, tuples);
        }
        FactTuple tuple = new FactTuple(relationName);
        tuple.setOid(oid);
        tuple.setFatherOid(fatherOid);
        tuples.add(tuple);
        while (tokenizer.hasMoreTokens()) {
            String value = cleanString(tokenizer.nextToken());
            tuple.addValue(new FactValue(value));
        }
    }

    private String cleanString(String token) {
        return token.substring(1, token.length() - 1).trim();
    }

    public String print(Map<String, List<IExpectedTuple>> db) {
        StringBuilder result = new StringBuilder();
        List<String> relationNames = new ArrayList<String>(db.keySet());
        Collections.sort(relationNames);
        for (String relationName : relationNames) {
            result.append("----------- ").append(relationName).append(" -----------\n");
            for (IExpectedTuple line : db.get(relationName)) {
                result.append(line).append("\n");
            }
            result.append("---------------------------\n");
        }
        return result.toString();
    }

    public static void main(String[] args) throws Exception {
        String fileName = "";
        DAOCSV dao = new DAOCSV();
        System.out.println(dao.print(dao.loadData(fileName)));
    }
}
