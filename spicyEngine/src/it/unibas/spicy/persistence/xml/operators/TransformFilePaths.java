/*
    Copyright (C) 2007-2011  Database Group - Universita' della Basilicata
    Giansalvatore Mecca - giansalvatore.mecca@unibas.it
    Salvatore Raunich - salrau@gmail.com
    Alessandro Pappalardo - pappalardo.alessandro@gmail.com
    Gianvito Summa - gianvito.summa@gmail.com

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

package it.unibas.spicy.persistence.xml.operators;

import it.unibas.spicy.utility.SpicyEngineUtility;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class TransformFilePaths {

    private static Log logger = LogFactory.getLog(TransformFilePaths.class);

    static final String SEPARATOR = "/";
    
    public String relativize(String baseFilePath, String relativeFilePath) {
        baseFilePath = SpicyEngineUtility.generateFolderPath(baseFilePath);
        List<String> basePathSteps = getPathSteps(baseFilePath);
        if (logger.isDebugEnabled()) logger.debug("Base path steps: " + basePathSteps);
        List<String> filePathSteps = getPathSteps(relativeFilePath);
        if (logger.isDebugEnabled()) logger.debug("File path steps: " + filePathSteps);
        String s = findRelativePathList(basePathSteps, filePathSteps);
        return s;
    }

    public String expand(String baseFilePath, String filePath) {
        if (logger.isDebugEnabled()) logger.debug("Expanding filePath: " + filePath + " wrt base path " + baseFilePath);
        baseFilePath = SpicyEngineUtility.generateFolderPath(baseFilePath);
        List<String> basePathSteps = getPathSteps(baseFilePath);
        if (logger.isDebugEnabled()) logger.debug("Base path steps: " + basePathSteps);
        List<String> filePathSteps = getPathSteps(filePath);
        if (logger.isDebugEnabled()) logger.debug("File path steps: " + filePathSteps);
        String s = mergePathLists(basePathSteps, filePathSteps);
        return s;
    }

    private List<String> getPathSteps(String filePath) {
        List<String> result = new ArrayList<String>();
        String separators = "/\\";
        StringTokenizer tokenizer = new StringTokenizer(filePath, separators);
        while (tokenizer.hasMoreTokens()) {
            result.add(0, tokenizer.nextToken());
        }
        return result;
    }

    private String findRelativePathList(List basePathSteps, List filePathSteps) {
        int i;
        int j;
        String s = "";
        i = basePathSteps.size() - 1;
        j = filePathSteps.size() - 1;

        // first eliminate common root
        while ((i >= 0) && (j >= 0) && (basePathSteps.get(i).equals(filePathSteps.get(j)))) {
            i--;
            j--;
        }

        // for each remaining level in the base path, add a ..
        for (; i >= 0; i--) {
            s += ".." + SEPARATOR;
        }

        // for each level in the file path, add the path
        for (; j >= 1; j--) {
            s += filePathSteps.get(j) + SEPARATOR;
        }

        // file name
        s += filePathSteps.get(j);
        return s;
    }

    private String mergePathLists(List<String> basePathSteps, List<String> filePathSteps) {
        Collections.reverse(basePathSteps);
        Collections.reverse(filePathSteps);
        List<String> result = new ArrayList<String>(basePathSteps);
        int i = 0;
        while (i < filePathSteps.size() && filePathSteps.get(i).equals("..")) {
            result.remove(result.size() - 1);
            i++;
        }
        for (int j = i; j < filePathSteps.size(); j++) {
            result.add(filePathSteps.get(j));
        }
        StringBuilder resultPath = new StringBuilder();
        for (int k = 0; k < result.size(); k++) {
            resultPath.append(result.get(k));
            if (k != result.size() - 1) {
                resultPath.append(File.separator);
            }
        }
        String resultString = resultPath.toString();
        if (!resultString.startsWith("/")) {
            resultString = "/" + resultString;
        }
        return resultString;
    }
}
