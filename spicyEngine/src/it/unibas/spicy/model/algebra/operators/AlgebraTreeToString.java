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

import it.unibas.spicy.model.algebra.DifferenceOnTargetValues;
import it.unibas.spicy.model.algebra.IAlgebraOperator;
import it.unibas.spicy.model.algebra.Join;
import it.unibas.spicy.model.algebra.Merge;
import it.unibas.spicy.model.algebra.Compose;
import it.unibas.spicy.model.algebra.Difference;
import it.unibas.spicy.model.algebra.Intersection;
import it.unibas.spicy.model.algebra.IntersectionOnTargetValues;
import it.unibas.spicy.model.algebra.JoinOnTargetValues;
import it.unibas.spicy.model.algebra.Nest;
import it.unibas.spicy.model.algebra.Project;
import it.unibas.spicy.model.algebra.ProjectWithDuplicates;
import it.unibas.spicy.model.algebra.RemoveDuplicates;
import it.unibas.spicy.model.algebra.Select;
import it.unibas.spicy.model.algebra.SelectOnTargetValues;
import it.unibas.spicy.model.algebra.SelectProvenance;
import it.unibas.spicy.model.algebra.Unnest;
import it.unibas.spicy.model.paths.VariableCorrespondence;
import it.unibas.spicy.model.paths.VariablePathExpression;
import it.unibas.spicy.model.paths.VariableSelectionCondition;
import java.util.List;

public class AlgebraTreeToString {

    public String treeToString(IAlgebraOperator root) {
        AlgebraTreeToStringVisitor visitor = new AlgebraTreeToStringVisitor();
        root.accept(visitor);
        return visitor.getResult();
    }
}

class AlgebraTreeToStringVisitor implements IAlgebraTreeVisitor {

    private int indentLevel = 0;
    private StringBuilder result = new StringBuilder();

    public void visitUnnest(Unnest operator) {
        result.append(this.indentString()).append(operator.getName()).append(" ").append(operator.getVariable().toStringNoConditions()).append("\n");
    }

    public void visitJoin(Join operator) {
        result.append(this.indentString()).append(operator.getName()).append(" on ").append(operator.getJoinCondition()).append("\n");
        visitChildren(operator);
    }

    public void visitJoinOnTargetValues(JoinOnTargetValues operator) {
        result.append(this.indentString()).append(operator.getName()).append(" on ").append(operator.getJoinCondition()).append("\n");
        for (VariableCorrespondence correspondence : operator.getLeftCorrespondences()) {
            result.append(this.indentString()).append("|-").append(correspondence).append("\n");
        }
        result.append(this.indentString()).append("|-").append(" and\n");
        for (VariableCorrespondence correspondence : operator.getRightCorrespondences()) {
            result.append(this.indentString()).append("|-").append(correspondence).append("\n");
        }
        visitChildren(operator);
    }

    public void visitProject(Project operator) {
        result.append(this.indentString()).append(operator.getName()).append(" on ").append(" ").append(operator.getId()).append("\n");
        for (VariablePathExpression attributePath : operator.getAttributePaths()) {
            result.append(this.indentString()).append("|-").append(attributePath).append("\n");
        }
        visitChildren(operator);
    }

    public void visitProjectWithDuplicates(ProjectWithDuplicates operator) {
        result.append(this.indentString()).append(operator.getName()).append(" on ").append(" ").append(operator.getId()).append("\n");
        for (VariablePathExpression attributePath : operator.getAttributePaths()) {
            result.append(this.indentString()).append("|-").append(attributePath).append("\n");
        }
        visitChildren(operator);
    }

    public void visitRemoveDuplicates(RemoveDuplicates operator) {
        result.append(this.indentString()).append(operator.getName()).append(" on ").append(" ").append(operator.getId()).append("\n");
        visitChildren(operator);
    }

    public void visitSelect(Select operator) {
        result.append(this.indentString()).append(operator.getName()).append(" where ").append("\n");
        for (VariableSelectionCondition condition : operator.getConditions()) {
            result.append(this.indentString()).append("|-").append(condition).append("\n");
        }
        visitChildren(operator);
    }

    public void visitSelectOnTargetValues(SelectOnTargetValues operator) {
        result.append(this.indentString()).append(operator.getName()).append(" where ").append("\n");
        for (VariableSelectionCondition condition : operator.getConditions()) {
            result.append(this.indentString()).append("|-").append(condition).append("\n");
        }
        result.append(this.indentString()).append("|-").append(" and\n");
        for (VariableCorrespondence correspondence : operator.getCorrespondences()) {
            result.append(this.indentString()).append("|-").append(correspondence).append("\n");
        }
        visitChildren(operator);
    }

    public void visitSelectProvenance(SelectProvenance operator) {
        result.append(this.indentString()).append(operator.getName()).append("\n");
        result.append(this.indentString()).append("|-").append(operator.getCondition()).append("\n");
        visitChildren(operator);
    }

    public void visitDifferenceOnTargetValues(DifferenceOnTargetValues operator) {
        result.append(this.indentString()).append(operator.getName()).append(" on ").append(" ").append(operator.getId()).append("\n");
        for (VariableCorrespondence correspondence : operator.getLeftCorrespondences()) {
            result.append(this.indentString()).append("|-").append(correspondence).append("\n");
        }
        result.append(this.indentString()).append("|-").append(" and\n");
        for (VariableCorrespondence correspondence : operator.getRightCorrespondences()) {
            result.append(this.indentString()).append("|-").append(correspondence).append("\n");
        }
        visitChildren(operator);
    }

    public void visitDifference(Difference operator) {
        result.append(this.indentString()).append(operator.getName()).append(" on ").append(" ").append(operator.getId()).append("\n");
        for (VariablePathExpression leftPath : operator.getLeftPaths()) {
            result.append(this.indentString()).append("|-").append(leftPath).append("\n");
        }
        result.append(this.indentString()).append("|-").append(" and\n");
        for (VariablePathExpression rightPath : operator.getRightPaths()) {
            result.append(this.indentString()).append("|-").append(rightPath).append("\n");
        }
        visitChildren(operator);
    }

    public void visitIntersection(Intersection operator) {
        result.append(this.indentString()).append(operator.getName()).append(" on ").append(" ").append(operator.getId()).append("\n");
        for (VariablePathExpression leftPath : operator.getLeftPaths()) {
            result.append(this.indentString()).append("|-").append(leftPath).append("\n");
        }
        result.append(this.indentString()).append("|-").append(" and\n");
        for (VariablePathExpression rightPath : operator.getRightPaths()) {
            result.append(this.indentString()).append("|-").append(rightPath).append("\n");
        }
        visitChildren(operator);
    }

    public void visitIntersectionOnTargetValues(IntersectionOnTargetValues operator) {
        result.append(this.indentString()).append(operator.getName()).append(" on ").append(" ").append(operator.getId()).append("\n");
        for (VariableCorrespondence correspondence : operator.getLeftCorrespondences()) {
            result.append(this.indentString()).append("|-").append(correspondence).append("\n");
        }
        result.append(this.indentString()).append("|-").append(" and\n");
        for (VariableCorrespondence correspondence : operator.getRightCorrespondences()) {
            result.append(this.indentString()).append("|-").append(correspondence).append("\n");
        }
        visitChildren(operator);
    }

    public void visitNest(Nest operator) {
        result.append(this.indentString()).append(operator.getName()).append(" on ").append(" ").append(operator.getId()).append("\n");
//        result.append(this.indentString()).append("|-").append(notNullGeneratorsString(operator.getGenerators())).append("\n");
        result.append(operator.getGenerators().toString(this.indentString().toString()));
        visitChildren(operator);
    }

    public void visitMerge(Merge operator) {
        result.append(this.indentString()).append(operator.getName()).append("\n");
        visitChildren(operator);
    }

    public void visitCompose(Compose operator) {
        result.append(this.indentString()).append(operator.getName()).append("\n");
        visitChildren(operator);
    }

    private void visitChildren(IAlgebraOperator operator) {
        List<IAlgebraOperator> listOfChildren = operator.getChildren();
        if (listOfChildren != null) {
            this.indentLevel++;
            for (IAlgebraOperator child : listOfChildren) {
                child.accept(this);
            }
            this.indentLevel--;
        }
    }

    private StringBuilder indentString() {
        StringBuilder indent = new StringBuilder();
        for (int i = 0; i < this.indentLevel; i++) {
            indent.append("    ");
        }
        return indent;
    }

    public String getResult() {
        return result.toString();
    }
}
