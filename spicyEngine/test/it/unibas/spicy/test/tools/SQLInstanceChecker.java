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

import it.unibas.spicy.model.algebra.query.operators.sql.GenerateSQL;
import it.unibas.spicy.model.datasource.INode;
import it.unibas.spicy.model.mapping.EngineConfiguration;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class SQLInstanceChecker extends AbstractInstanceChecker {

    private static Log logger = LogFactory.getLog(SQLInstanceChecker.class);
    private static final int SKOLEM_VALUE = 10000;

    public SQLInstanceChecker(EngineConfiguration config) {
        super(config);
    }

    public void checkInstance(INode instance, String expectedInstanceFile) throws Exception {
        DAOCSV dao = new DAOCSV();
        Map<String, List<IExpectedTuple>> instanceMap = dao.loadData(expectedInstanceFile);
        for (INode setNode : instance.getChildren()) {
            checkSetTuples(instance, setNode, instanceMap, expectedInstanceFile);
        }
        checkJoins(instance, instanceMap, expectedInstanceFile);
    }

    protected boolean checkNullOrSkolem(Object instanceValue) {
        try {
            if (instanceValue.toString().equals("NULL")) {
                return true;
            }
            if (config.useSkolemTable()) {
                int intValue = Math.abs(Integer.parseInt(instanceValue.toString()));
                if (config.useHashTextForSkolems()) {
                    return intValue > SKOLEM_VALUE;
                } else {
                    return (intValue > GenerateSQL.SKOLEM_ID_MIN_VALUE);
                }
            } else {
                return (instanceValue.toString().startsWith("SK_") ||
                        instanceValue.toString().startsWith("SK{") ||
                        instanceValue.toString().startsWith("SKEGDs") ||
                        instanceValue.toString().startsWith("f_TGD_"));
            }
        } catch (NumberFormatException nfe) {
            return false;
        }
    }

    protected boolean isSkolem(INode attributeNode) {
        Object instanceValue = attributeNode.getChild(0).getValue();
        try {
            if (config.useSkolemTable()) {
                int intValue = Math.abs(Integer.parseInt(instanceValue.toString()));
                if (config.useHashTextForSkolems()) {
                    return intValue > SKOLEM_VALUE;
                } else {
                    return (intValue > GenerateSQL.SKOLEM_ID_MIN_VALUE);
                }
            } else {
                return (instanceValue.toString().startsWith("SK_") ||
                        instanceValue.toString().startsWith("SK{") ||
                        instanceValue.toString().startsWith("SKEGDs") ||
                        instanceValue.toString().startsWith("f_TGD_"));
            }
        } catch (NumberFormatException nfe) {
            return false;
        }
    }
}
