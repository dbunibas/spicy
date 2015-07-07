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

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.xerces.parsers.SAXParser;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

class ValidateXMLWithSchema {
    
    void validateXerces(String SchemaUrl, String XmlDocumentUrl)   {
        XercesValidator validator = new XercesValidator();
        validator.validateSchema(SchemaUrl, XmlDocumentUrl);
    }
    
    void validateJAXP(String SchemaUrl, String XmlDocumentUrl)   {
        JAXPValidator validator = new JAXPValidator();
        validator.validateSchema(SchemaUrl, XmlDocumentUrl);
    }
    
}

class XercesValidator{
    
    void validateSchema(String SchemaUrl, String XmlDocumentUrl)   {
        SAXParser parser = new SAXParser();
        try{
            parser.setFeature("http://xml.org/sax/features/validation",true);
            parser.setFeature("http://apache.org/xml/features/validation/schema",true);
            parser.setFeature("http://apache.org/xml/features/validation/schema-full-checking",
                    true);
            parser.setProperty("http://apache.org/xml/properties/schema/external-noNamespaceSchemaLocation",   SchemaUrl );
            Validator handler=new Validator();
            parser.setErrorHandler(handler);  parser.parse(XmlDocumentUrl);
            if(handler.validationError==true)
                System.out.println("XML Document has Error:"+handler.validationError+""+handler.saxParseException.getMessage());
            else
                System.out.println("XML Document is valid");
        } catch(java.io.IOException ioe){
            System.out.println("IOException"+ioe.getMessage());
        } catch (SAXException e) {
            System.out.println("SAXException"+e.getMessage());
        }
    }
    
    
    private class Validator extends DefaultHandler {
        public boolean  validationError = false;
        public SAXParseException saxParseException=null;
        public void error(SAXParseException exception) throws SAXException {
            validationError=true;
            saxParseException=exception;
        }
        public void fatalError(SAXParseException exception) throws SAXException {
            validationError = true;
            saxParseException=exception;
        }
        public void warning(SAXParseException exception) throws SAXException {}
    }
    
}

class JAXPValidator{
    
    void validateSchema(String SchemaUrl, String XmlDocumentUrl)   {
        try{
            System.setProperty("javax.xml.parsers.DocumentBuilderFactory",
                    "org.apache.xerces.jaxp.DocumentBuilderFactoryImpl");
            DocumentBuilderFactory factory =  DocumentBuilderFactory.newInstance();     factory.setNamespaceAware(true);
            factory.setValidating(true);     factory.setAttribute("http://java.sun.com/xml/jaxp/properties/schemaLanguage",
                    "http://www.w3.org/2001/XMLSchema" );
            factory.setAttribute("http://java.sun.com/xml/jaxp/properties/schemaSource",SchemaUrl);
            DocumentBuilder builder =factory.newDocumentBuilder();
            Validator handler=new Validator();
            builder.setErrorHandler(handler);
            builder.parse(XmlDocumentUrl);
            if(handler.validationError==true)
                System.out.println("XML Document has Error:"+handler.validationError+" "+handler.saxParseException.getMessage());
            else
                System.out.println("XML Document is valid");
        } catch(java.io.IOException ioe)    {
            System.out.println("IOException "+ioe.getMessage());
        } catch (SAXException e) {
            System.out.println("SAXException"+e.getMessage());
        } catch (ParserConfigurationException e) {
            System.out.println("ParserConfigurationException                    "+e.getMessage());
        }
    }
    
    private class Validator extends DefaultHandler	   {
        public boolean  validationError =false;
        public SAXParseException saxParseException=null;
        public void error(SAXParseException exception) throws SAXException {
            validationError = true;
            saxParseException=exception;
        }
        public void fatalError(SAXParseException exception) throws SAXException {
            validationError = true;
            saxParseException=exception;
        }
        public void warning(SAXParseException exception) throws SAXException  {      }
    }
}