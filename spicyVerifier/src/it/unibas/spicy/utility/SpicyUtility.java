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
 
package it.unibas.spicy.utility;

import java.io.File;
import java.net.URISyntaxException;
import java.text.DecimalFormat;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class SpicyUtility {
    
    private static Log logger = LogFactory.getLog(SpicyUtility.class);
    
    public static double ratio(Double a, Double b) {
       // System.out.println("a = " + a);
       // System.out.println("b = " + b);        
        if (a == null || b == null) {
            throw new IllegalArgumentException("Ratio cannot be computed for null values");
//            return 1.0;
        }
        double ratio1 = Math.abs(a - b)/Math.abs(a);
        double ratio2 = Math.abs(b - a)/Math.abs(b);
        return Math.min(1, (ratio1 + ratio2) / 2);
    }
    
    public static double log2(double x) {
        return Math.log(x) / Math.log(2.0);
    }
    
    public static boolean uguali(double a, double b) {
        return Math.abs(a - b) < 1E-4;
    }
    
    public static int maxInt(int[] vett){
        int max = 0;
        for(int i = 0; i < vett.length; i++){
            if (vett[i] > max){
                max = vett[i];
            }
        }
        return max;
    }
    
    public static String printDoubleArray(double[] v) {
        String result = " ";
        for (int i = 0; i < v.length; i++) {
            result += i + "   \t";
        }
        result += "\n";
        result += "[";
        for (int i = 0; i < v.length; i++) {
            result += formatDouble(v[i]) + "\t";
        }
        return result + "]\n";
    }
    
    public static String printDoubleMatrix(double[][] m) {
        String result = "    ";
        for (int i = 0; i < m.length; i++) {
            result += i + "   \t";
        }
        result += "\n";
        for (int i = 0; i < m.length; i++) {
            result += formatInteger(i) + " [";
            for (int j = 0; j < m[0].length; j++) {
                result += formatDouble(m[i][j]) + "\t";
            }
            result += "]\n";
        }
        return result;
    }
    
    public static String printObjectArray(Object[] v) {
        String result = "[";
        for (int i = 0; i < v.length; i++) {
            result += v[i] + ", ";
        }
        return result + "]";
    }
    
    public static String formatDouble(double d) {
        DecimalFormat decimalFormatter = new DecimalFormat("#0.000");
        return decimalFormatter.format(d);
    }
    
    public static String formatInteger(int d) {
        DecimalFormat decimalFormatter = new DecimalFormat("#00");
        return decimalFormatter.format(d);
    }
    
    public static double harmonicMean(double a, double b) {
        return (2 * a * b / (a + b));
    }
    
}
