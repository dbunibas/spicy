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
 
package it.unibas.spicy.model.mapping.rewriting.operators;

import it.unibas.spicy.model.mapping.FORule;
import it.unibas.spicy.model.mapping.IDataSourceProxy;
import it.unibas.spicy.model.mapping.MappingTask;
import it.unibas.spicy.utility.SpicyEngineUtility;

public class CheckTGDHomomorphism {

    private GenerateTgdSubsumptionMapWithSelfJoins checkerForSelfJoins = new GenerateTgdSubsumptionMapWithSelfJoins();
    private CheckTGDHomomorphismWithoutSelfJoins checkerWithoutSelfJoins = new CheckTGDHomomorphismWithoutSelfJoins();

    public boolean subsumes(FORule fatherTgd, FORule tgd, IDataSourceProxy target, MappingTask mappingTask) {
        if (SpicyEngineUtility.hasSelfJoins(tgd.getTargetView()) || SpicyEngineUtility.hasSelfJoins(fatherTgd.getTargetView())) {
            return checkerForSelfJoins.checkHomomorphismWithSelfJoins(fatherTgd, tgd, target, mappingTask) != null;
        }
        return checkerWithoutSelfJoins.checkHomomorphismWithoutSelfJoins(fatherTgd, tgd, target, mappingTask) != null;
    }
}
