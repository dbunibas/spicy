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
import it.unibas.spicy.model.datasource.nodes.TupleNode;
import it.unibas.spicy.model.datasource.values.INullValue;
import it.unibas.spicy.model.mapping.EngineConfiguration;
import it.unibas.spicy.model.datasource.values.OID;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class XQueryInstanceChecker extends AbstractInstanceChecker {

    private static Log logger = LogFactory.getLog(XQueryInstanceChecker.class);

    public XQueryInstanceChecker(EngineConfiguration config) {
        super(config);
    }

    public void checkInstance(INode instance, String expectedInstanceFile) throws Exception {
        DAOCSV dao = new DAOCSV();
        Map<String, List<IExpectedTuple>> instanceMap = dao.loadDataForXML(expectedInstanceFile);
        INode tupleNode = findFirstTupleNode(instance);
        for (INode setNode : tupleNode.getChildren()) {
            checkSetTuples(instance, setNode, instanceMap, expectedInstanceFile);
        }
        checkJoins(instance, instanceMap, expectedInstanceFile);
    }

    /// /TODO: Searching all setNodes...

    private INode findFirstTupleNode(INode instance) {
        INode tupleNode = instance;
        while (!(tupleNode instanceof TupleNode)) {
            tupleNode = instance.getChild(0);
        }
        return tupleNode;
    }

    protected boolean checkNullOrSkolem(Object instanceValue) {
        return (instanceValue instanceof INullValue) || (instanceValue instanceof OID);
    }

    protected boolean isSkolem(INode attributeNode) {
        Object instanceValue = attributeNode.getChild(0).getValue();
        if (instanceValue instanceof OID) {
            return true;
        }
        return false;
    }
}
