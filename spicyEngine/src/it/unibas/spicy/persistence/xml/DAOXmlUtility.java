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
 
package it.unibas.spicy.persistence.xml;

import it.unibas.spicy.persistence.DAOException;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.w3c.dom.DOMException;

public class DAOXmlUtility {

    protected static Log logger = LogFactory.getLog(DAOXmlUtility.class);

    public org.jdom.Document buildDOM(String fileName) throws DAOException {
        if (fileName == null || "".equals(fileName)) {
            throw new DAOException("Unable to load file. Null or empty path requested");
        }
        SAXBuilder builder = new SAXBuilder();
        builder.setValidation(false);
        org.jdom.Document document = null;
        try {
            fileName = checkFilePath(fileName);
            document = builder.build(fileName);
            return document;
        } catch (org.jdom.JDOMException jde) {
            logger.error(jde);
            throw new DAOException(jde.getMessage());
        } catch (java.io.IOException ioe) {
            logger.error(ioe);
            throw new DAOException(ioe.getMessage());
        }
    }

    public static String checkFilePath(String path) {
        if (path != null && !path.startsWith("/")) {
            path = "/" + path;
        }
        return path;
    }

    public org.w3c.dom.Document buildNewDOM() throws DAOException {
        org.w3c.dom.Document document = null;
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setValidating(true);
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            document = builder.newDocument();
        } catch (ParserConfigurationException pce) {
            logger.error(pce);
            throw new DAOException(pce.getMessage());
        } catch (DOMException doe) {
            logger.error(doe);
            throw new DAOException(doe.getMessage());
        }
        return document;
    }

    public void saveDOM(org.w3c.dom.Document document, String filename) throws DAOException {
        try {
            File file = new java.io.File(filename);
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            transformerFactory.setAttribute("indent-number", new Integer(2));
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.METHOD, "xml");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xalan}indent-amount", "2");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

            DOMSource source = new DOMSource(document);
            StreamResult result = new StreamResult(file);
            transformer.transform(source, result);

        } catch (Exception ex) {
            logger.error("- Exception in saveDOM: \n" + ex);
            throw new DAOException(ex.getMessage());
        }
    }

    public void saveDOM(org.jdom.Document document, String filename) {
        File file = new java.io.File(filename);
        BufferedWriter out = null;
        try {
            out = new BufferedWriter(new FileWriter(file));
            XMLOutputter xout = new XMLOutputter(Format.getPrettyFormat());
            xout.output(document, out);

        } catch (IOException e) {
            System.out.println("Error in producing file: " + e.getMessage());
        }
    }

    public static String cleanXmlString(String xmlString) {
        String stringCleaned = xmlString;
        stringCleaned.replaceAll("&gt;", ">");
        stringCleaned.replaceAll("&lt;", "<");
        stringCleaned.replaceAll("&quot;", "\"");
        stringCleaned.replaceAll("&apos;", "'");
        stringCleaned.replaceAll("&amp;", "&");

        return stringCleaned;
    }
}
