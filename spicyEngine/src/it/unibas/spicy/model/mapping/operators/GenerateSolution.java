/*
Copyright (C) 2007-2011  Database Group - Universita' della Basilicata
Giansalvatore Mecca - giansalvatore.mecca@unibas.it
Donatello Santoro - donatello.santoro@gmail.com

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
package it.unibas.spicy.model.mapping.operators;

import it.unibas.spicy.model.datasource.INode;
import it.unibas.spicy.model.datasource.operators.VerifyFunctionalDependencies;
import it.unibas.spicy.model.exceptions.UnsatisfiableEGDException;
import it.unibas.spicy.model.mapping.IDataSourceProxy;
import it.unibas.spicy.model.mapping.MappingTask;

public class GenerateSolution {

    public IDataSourceProxy generateSolution(MappingTask mappingTask) {
        IDataSourceProxy solution = mappingTask.getMappingData().getAlgebraTree().execute(mappingTask.getSourceProxy());
        IDataSourceProxy targetProxy = mappingTask.getTargetProxy();
        boolean wasToBeSaved = targetProxy.isToBeSaved();
        targetProxy.getInstances().clear();
        for (INode instance : solution.getInstances()) {
            targetProxy.addInstanceWithCheck(instance);
        }
        targetProxy.setModified(false);
        targetProxy.setToBeSaved(wasToBeSaved);
        return targetProxy;
    }

    public void verifySolution(IDataSourceProxy solution, MappingTask mappingTask) {
        if (mappingTask.getConfig().rewriteEGDs()) {
            VerifyFunctionalDependencies verifier = new VerifyFunctionalDependencies();
            String errors = verifier.verifyFunctionalDependencies(mappingTask.getTargetProxy(), solution);
            if (!errors.isEmpty()) {
                throw new UnsatisfiableEGDException(errors);
            }
        }
    }
}
