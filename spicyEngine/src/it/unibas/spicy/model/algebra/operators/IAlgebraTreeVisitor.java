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
 
package it.unibas.spicy.model.algebra.operators;

import it.unibas.spicy.model.algebra.Compose;
import it.unibas.spicy.model.algebra.Difference;
import it.unibas.spicy.model.algebra.DifferenceOnTargetValues;
import it.unibas.spicy.model.algebra.Intersection;
import it.unibas.spicy.model.algebra.IntersectionOnTargetValues;
import it.unibas.spicy.model.algebra.Join;
import it.unibas.spicy.model.algebra.JoinOnTargetValues;
import it.unibas.spicy.model.algebra.Merge;
import it.unibas.spicy.model.algebra.Nest;
import it.unibas.spicy.model.algebra.Project;
import it.unibas.spicy.model.algebra.ProjectWithDuplicates;
import it.unibas.spicy.model.algebra.RemoveDuplicates;
import it.unibas.spicy.model.algebra.Select;
import it.unibas.spicy.model.algebra.SelectOnTargetValues;
import it.unibas.spicy.model.algebra.SelectProvenance;
import it.unibas.spicy.model.algebra.Unnest;

public interface IAlgebraTreeVisitor {

    public void visitUnnest(Unnest operator);

    public void visitJoin(Join operator);

    public void visitJoinOnTargetValues(JoinOnTargetValues operator);

    public void visitProject(Project operator);

    public void visitProjectWithDuplicates(ProjectWithDuplicates operator);

    public void visitRemoveDuplicates(RemoveDuplicates operator);

    public void visitSelect(Select operator);

    public void visitSelectOnTargetValues(SelectOnTargetValues operator);

    public void visitSelectProvenance(SelectProvenance operator);

    public void visitDifference(Difference operator);

    public void visitDifferenceOnTargetValues(DifferenceOnTargetValues operator);
    
    public void visitIntersection(Intersection operator);

    public void visitIntersectionOnTargetValues(IntersectionOnTargetValues operator);

    public void visitNest(Nest operator);

    public void visitMerge(Merge operator);

    public void visitCompose(Compose operator);

//    public void visitApplyFunctions(ApplyFunctions operator);
//    public void visitDifferenceOnSourceValues(DifferenceOnSourceValues operator);

    Object getResult();
}
