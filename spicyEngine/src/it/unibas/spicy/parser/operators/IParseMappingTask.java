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
 
package it.unibas.spicy.parser.operators;

import it.unibas.spicy.model.mapping.IDataSourceProxy;
import it.unibas.spicy.model.mapping.MappingTask;
import it.unibas.spicy.parser.ParserFD;
import it.unibas.spicy.parser.ParserInstance;
import it.unibas.spicy.parser.ParserTGD;
import java.util.List;

public interface IParseMappingTask {

    String PARSER_EXTENSION = ".tgd";
    String GENERATE = "generate";
    String CHAIN = "chain:";

    void addSTTGD(ParserTGD tgd);

    void addTargetTGD(ParserTGD tgd);

    void setSourceFDs(List<ParserFD> fds);

    void setTargetFDs(List<ParserFD> fds);

    void addSourceInstance(ParserInstance instance);

    void createMappingTask(List<String> sourceSchemaFiles, List<String> sourceInstanceFiles, String targetSchemaFile) throws Exception;

    MappingTask generateMappingTask(String mappingTaskFile) throws Exception;

    IDataSourceProxy loadSource(String schemaFile, String instanceFile);

    void processTGDs();

    void setSourceNulls(String sourceNulls);

    void setSubsumptions(String subsumptions);

    void setCoverages(String coverages);

    void setSelfJoins(String selfJoins);

    void setNoRewriting(String value);

    void setEgds(String egds);

    void setOverlaps(String value);

    void setSkolemsForEgds(String value);

    void setSkolemStrings(String value);

    void setLocalSkolems(String localSkolems);

    void setSortStrategy(String sortStrategy);

    void setSkolemTableStrategy(String skolemTableStrategy);

    String clean(String expressionString);
}
