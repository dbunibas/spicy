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
package it.unibas.spicy.model.datasource;

import it.unibas.spicy.utility.SpicyEngineConstants;
import it.unibas.spicy.model.datasource.operators.CalculateSize;
import it.unibas.spicy.model.datasource.operators.CheckInstance;
import it.unibas.spicy.model.paths.PathExpression;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class DataSource implements Cloneable {

    private static Log logger = LogFactory.getLog(DataSource.class);

    // data source definition
    private String type;
    private INode schema;
    private List<INode> originalInstances = new ArrayList<INode>();
    private List<INode> instances = new ArrayList<INode>();
    private List<KeyConstraint> keyConstraints = new ArrayList<KeyConstraint>();
    private List<ForeignKeyConstraint> foreignKeyConstraints = new ArrayList<ForeignKeyConstraint>();

    public DataSource(String type, INode schema) {
        this.type = type;
        this.schema = schema;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public INode getSchema() {
        return schema;
    }

    public List<KeyConstraint> getKeyConstraints() {
        return keyConstraints;
    }

    public void addKeyConstraint(KeyConstraint keyConstraint) {
        this.keyConstraints.add(keyConstraint);
    }

    public List<ForeignKeyConstraint> getForeignKeyConstraints() {
        return foreignKeyConstraints;
    }

    public void addForeignKeyConstraint(ForeignKeyConstraint foreignKeyConstraint) {
        this.foreignKeyConstraints.add(foreignKeyConstraint);
    }

    public List<INode> getOriginalInstances() {
        return originalInstances;
    }

    public List<INode> getInstances() {
        return this.instances;
    }

    public void addInstance(INode instance) {
        if (!type.equals(SpicyEngineConstants.TYPE_ALGEBRA_RESULT)) {
            throw new IllegalArgumentException("Method can be used for algebra data sources only. Use addInstanceWithCheck()");
        }
        this.instances.add(instance);
    }

    public void addInstanceWithCheck(INode instance) {
        CheckInstance checker = new CheckInstance();
        checker.checkInstance(this, instance);
        INode instanceClone = instance.clone();
        this.originalInstances.add(instanceClone);
        //this.instances.add(new DuplicateSet().generateInstanceClone(duplications, instance, intermediateSchema));
        this.instances.add(instanceClone);
    }

    public int getSize() {
        CalculateSize calculator = new CalculateSize();
        return calculator.getSchemaSize(this);
    }

    @Override
    public DataSource clone()  {
        DataSource clone = null;
        try {
            clone = (DataSource) super.clone();
            clone.schema = this.schema.clone();
            clone.instances = new ArrayList<INode>();
            for (INode instance : instances) {
                clone.instances.add(instance.clone());
            }
            clone.originalInstances = new ArrayList<INode>();
            for (INode instance : originalInstances) {
                clone.originalInstances.add(instance.clone());
            }
            Map<KeyConstraint, KeyConstraint> keyMap = new HashMap<KeyConstraint, KeyConstraint>();
            clone.keyConstraints = new ArrayList<KeyConstraint>();
            for (KeyConstraint key : keyConstraints) {
                KeyConstraint keyClone = key.clone();
                keyMap.put(key, keyClone);
                clone.keyConstraints.add(keyClone);
            }
            clone.foreignKeyConstraints = new ArrayList<ForeignKeyConstraint>();
            for (ForeignKeyConstraint foreignKey : foreignKeyConstraints) {
                KeyConstraint keyClone = keyMap.get(foreignKey.getKeyConstraint());
                List<PathExpression> fkPathClones = new ArrayList<PathExpression>();
                for (PathExpression fkPath : foreignKey.getForeignKeyPaths()) {
                    fkPathClones.add(fkPath.clone());
                }
                ForeignKeyConstraint foreignKeyClone = new ForeignKeyConstraint(keyClone, fkPathClones);
                clone.foreignKeyConstraints.add(foreignKeyClone);
            }
        } catch (CloneNotSupportedException ex) {
            logger.error(ex);
        }
        return clone;
    }




}
