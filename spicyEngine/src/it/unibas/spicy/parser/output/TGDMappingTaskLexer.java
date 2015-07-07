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
 
// $ANTLR 3.3 Nov 30, 2010 12:45:30 D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g 2011-08-28 20:16:27

package it.unibas.spicy.parser.output;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

public class TGDMappingTaskLexer extends Lexer {
    public static final int EOF=-1;
    public static final int T__15=15;
    public static final int T__16=16;
    public static final int T__17=17;
    public static final int T__18=18;
    public static final int T__19=19;
    public static final int T__20=20;
    public static final int T__21=21;
    public static final int T__22=22;
    public static final int T__23=23;
    public static final int T__24=24;
    public static final int T__25=25;
    public static final int T__26=26;
    public static final int T__27=27;
    public static final int T__28=28;
    public static final int T__29=29;
    public static final int T__30=30;
    public static final int T__31=31;
    public static final int T__32=32;
    public static final int T__33=33;
    public static final int T__34=34;
    public static final int T__35=35;
    public static final int T__36=36;
    public static final int T__37=37;
    public static final int T__38=38;
    public static final int T__39=39;
    public static final int T__40=40;
    public static final int T__41=41;
    public static final int T__42=42;
    public static final int T__43=43;
    public static final int T__44=44;
    public static final int T__45=45;
    public static final int T__46=46;
    public static final int T__47=47;
    public static final int T__48=48;
    public static final int T__49=49;
    public static final int FILEPATH=4;
    public static final int NUMBER=5;
    public static final int IDENTIFIER=6;
    public static final int OPERATOR=7;
    public static final int STRING=8;
    public static final int NULL=9;
    public static final int EXPRESSION=10;
    public static final int LETTER=11;
    public static final int DIGIT=12;
    public static final int WHITESPACE=13;
    public static final int LINE_COMMENT=14;


    public void emitErrorMessage(String msg) {
    	throw new it.unibas.spicy.parser.ParserException(msg);
    }


    // delegates
    // delegators

    public TGDMappingTaskLexer() {;} 
    public TGDMappingTaskLexer(CharStream input) {
        this(input, new RecognizerSharedState());
    }
    public TGDMappingTaskLexer(CharStream input, RecognizerSharedState state) {
        super(input,state);

    }
    public String getGrammarFileName() { return "D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g"; }

    // $ANTLR start "T__15"
    public final void mT__15() throws RecognitionException {
        try {
            int _type = T__15;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:13:7: ( 'Mapping task:' )
            // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:13:9: 'Mapping task:'
            {
            match("Mapping task:"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__15"

    // $ANTLR start "T__16"
    public final void mT__16() throws RecognitionException {
        try {
            int _type = T__16;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:14:7: ( 'Source schema:' )
            // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:14:9: 'Source schema:'
            {
            match("Source schema:"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__16"

    // $ANTLR start "T__17"
    public final void mT__17() throws RecognitionException {
        try {
            int _type = T__17;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:15:7: ( 'Source instance:' )
            // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:15:9: 'Source instance:'
            {
            match("Source instance:"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__17"

    // $ANTLR start "T__18"
    public final void mT__18() throws RecognitionException {
        try {
            int _type = T__18;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:16:7: ( 'Target schema:' )
            // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:16:9: 'Target schema:'
            {
            match("Target schema:"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__18"

    // $ANTLR start "T__19"
    public final void mT__19() throws RecognitionException {
        try {
            int _type = T__19;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:17:7: ( 'SOURCE TO TARGET TGDs:' )
            // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:17:9: 'SOURCE TO TARGET TGDs:'
            {
            match("SOURCE TO TARGET TGDs:"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__19"

    // $ANTLR start "T__20"
    public final void mT__20() throws RecognitionException {
        try {
            int _type = T__20;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:18:7: ( 'TARGET TGDs:' )
            // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:18:9: 'TARGET TGDs:'
            {
            match("TARGET TGDs:"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__20"

    // $ANTLR start "T__21"
    public final void mT__21() throws RecognitionException {
        try {
            int _type = T__21;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:19:7: ( 'SOURCE FDs:' )
            // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:19:9: 'SOURCE FDs:'
            {
            match("SOURCE FDs:"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__21"

    // $ANTLR start "T__22"
    public final void mT__22() throws RecognitionException {
        try {
            int _type = T__22;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:20:7: ( 'TARGET FDs:' )
            // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:20:9: 'TARGET FDs:'
            {
            match("TARGET FDs:"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__22"

    // $ANTLR start "T__23"
    public final void mT__23() throws RecognitionException {
        try {
            int _type = T__23;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:21:7: ( 'SOURCE INSTANCE:' )
            // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:21:9: 'SOURCE INSTANCE:'
            {
            match("SOURCE INSTANCE:"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__23"

    // $ANTLR start "T__24"
    public final void mT__24() throws RecognitionException {
        try {
            int _type = T__24;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:22:7: ( 'CONFIG:' )
            // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:22:9: 'CONFIG:'
            {
            match("CONFIG:"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__24"

    // $ANTLR start "T__25"
    public final void mT__25() throws RecognitionException {
        try {
            int _type = T__25;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:23:7: ( 'SOURCENULLS:' )
            // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:23:9: 'SOURCENULLS:'
            {
            match("SOURCENULLS:"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__25"

    // $ANTLR start "T__26"
    public final void mT__26() throws RecognitionException {
        try {
            int _type = T__26;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:24:7: ( 'SUBSUMPTIONS:' )
            // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:24:9: 'SUBSUMPTIONS:'
            {
            match("SUBSUMPTIONS:"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__26"

    // $ANTLR start "T__27"
    public final void mT__27() throws RecognitionException {
        try {
            int _type = T__27;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:25:7: ( 'COVERAGES:' )
            // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:25:9: 'COVERAGES:'
            {
            match("COVERAGES:"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__27"

    // $ANTLR start "T__28"
    public final void mT__28() throws RecognitionException {
        try {
            int _type = T__28;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:26:7: ( 'SELFJOINS:' )
            // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:26:9: 'SELFJOINS:'
            {
            match("SELFJOINS:"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__28"

    // $ANTLR start "T__29"
    public final void mT__29() throws RecognitionException {
        try {
            int _type = T__29;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:27:7: ( 'NOREWRITING:' )
            // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:27:9: 'NOREWRITING:'
            {
            match("NOREWRITING:"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__29"

    // $ANTLR start "T__30"
    public final void mT__30() throws RecognitionException {
        try {
            int _type = T__30;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:28:7: ( 'EGDS:' )
            // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:28:9: 'EGDS:'
            {
            match("EGDS:"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__30"

    // $ANTLR start "T__31"
    public final void mT__31() throws RecognitionException {
        try {
            int _type = T__31;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:29:7: ( 'OVERLAPS:' )
            // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:29:9: 'OVERLAPS:'
            {
            match("OVERLAPS:"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__31"

    // $ANTLR start "T__32"
    public final void mT__32() throws RecognitionException {
        try {
            int _type = T__32;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:30:7: ( 'SKOLEMSFOREGDS:' )
            // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:30:9: 'SKOLEMSFOREGDS:'
            {
            match("SKOLEMSFOREGDS:"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__32"

    // $ANTLR start "T__33"
    public final void mT__33() throws RecognitionException {
        try {
            int _type = T__33;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:31:7: ( 'SKOLEMSTRINGS:' )
            // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:31:9: 'SKOLEMSTRINGS:'
            {
            match("SKOLEMSTRINGS:"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__33"

    // $ANTLR start "T__34"
    public final void mT__34() throws RecognitionException {
        try {
            int _type = T__34;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:32:7: ( 'LOCALSKOLEMS:' )
            // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:32:9: 'LOCALSKOLEMS:'
            {
            match("LOCALSKOLEMS:"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__34"

    // $ANTLR start "T__35"
    public final void mT__35() throws RecognitionException {
        try {
            int _type = T__35;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:33:7: ( 'SORTSTRATEGY:' )
            // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:33:9: 'SORTSTRATEGY:'
            {
            match("SORTSTRATEGY:"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__35"

    // $ANTLR start "T__36"
    public final void mT__36() throws RecognitionException {
        try {
            int _type = T__36;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:34:7: ( 'SKOLEMTABLESTRATEGY:' )
            // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:34:9: 'SKOLEMTABLESTRATEGY:'
            {
            match("SKOLEMTABLESTRATEGY:"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__36"

    // $ANTLR start "T__37"
    public final void mT__37() throws RecognitionException {
        try {
            int _type = T__37;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:35:7: ( '->' )
            // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:35:9: '->'
            {
            match("->"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__37"

    // $ANTLR start "T__38"
    public final void mT__38() throws RecognitionException {
        try {
            int _type = T__38;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:36:7: ( '.' )
            // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:36:9: '.'
            {
            match('.'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__38"

    // $ANTLR start "T__39"
    public final void mT__39() throws RecognitionException {
        try {
            int _type = T__39;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:37:7: ( ',' )
            // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:37:9: ','
            {
            match(','); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__39"

    // $ANTLR start "T__40"
    public final void mT__40() throws RecognitionException {
        try {
            int _type = T__40;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:38:7: ( 'and not exists' )
            // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:38:9: 'and not exists'
            {
            match("and not exists"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__40"

    // $ANTLR start "T__41"
    public final void mT__41() throws RecognitionException {
        try {
            int _type = T__41;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:39:7: ( '(' )
            // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:39:9: '('
            {
            match('('); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__41"

    // $ANTLR start "T__42"
    public final void mT__42() throws RecognitionException {
        try {
            int _type = T__42;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:40:7: ( 'with' )
            // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:40:9: 'with'
            {
            match("with"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__42"

    // $ANTLR start "T__43"
    public final void mT__43() throws RecognitionException {
        try {
            int _type = T__43;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:41:7: ( ')' )
            // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:41:9: ')'
            {
            match(')'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__43"

    // $ANTLR start "T__44"
    public final void mT__44() throws RecognitionException {
        try {
            int _type = T__44;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:42:7: ( '=' )
            // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:42:9: '='
            {
            match('='); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__44"

    // $ANTLR start "T__45"
    public final void mT__45() throws RecognitionException {
        try {
            int _type = T__45;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:43:7: ( '_' )
            // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:43:9: '_'
            {
            match('_'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__45"

    // $ANTLR start "T__46"
    public final void mT__46() throws RecognitionException {
        try {
            int _type = T__46;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:44:7: ( '\\$' )
            // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:44:9: '\\$'
            {
            match('$'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__46"

    // $ANTLR start "T__47"
    public final void mT__47() throws RecognitionException {
        try {
            int _type = T__47;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:45:7: ( ':' )
            // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:45:9: ':'
            {
            match(':'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__47"

    // $ANTLR start "T__48"
    public final void mT__48() throws RecognitionException {
        try {
            int _type = T__48;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:46:7: ( '[pk]' )
            // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:46:9: '[pk]'
            {
            match("[pk]"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__48"

    // $ANTLR start "T__49"
    public final void mT__49() throws RecognitionException {
        try {
            int _type = T__49;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:47:7: ( '[key]' )
            // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:47:9: '[key]'
            {
            match("[key]"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__49"

    // $ANTLR start "OPERATOR"
    public final void mOPERATOR() throws RecognitionException {
        try {
            int _type = OPERATOR;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:164:9: ( '==' | '!=' | '>' | '<' | '>=' | '<=' )
            int alt1=6;
            switch ( input.LA(1) ) {
            case '=':
                {
                alt1=1;
                }
                break;
            case '!':
                {
                alt1=2;
                }
                break;
            case '>':
                {
                int LA1_3 = input.LA(2);

                if ( (LA1_3=='=') ) {
                    alt1=5;
                }
                else {
                    alt1=3;}
                }
                break;
            case '<':
                {
                int LA1_4 = input.LA(2);

                if ( (LA1_4=='=') ) {
                    alt1=6;
                }
                else {
                    alt1=4;}
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 1, 0, input);

                throw nvae;
            }

            switch (alt1) {
                case 1 :
                    // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:164:11: '=='
                    {
                    match("=="); 


                    }
                    break;
                case 2 :
                    // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:164:18: '!='
                    {
                    match("!="); 


                    }
                    break;
                case 3 :
                    // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:164:25: '>'
                    {
                    match('>'); 

                    }
                    break;
                case 4 :
                    // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:164:31: '<'
                    {
                    match('<'); 

                    }
                    break;
                case 5 :
                    // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:164:37: '>='
                    {
                    match(">="); 


                    }
                    break;
                case 6 :
                    // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:164:44: '<='
                    {
                    match("<="); 


                    }
                    break;

            }
            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "OPERATOR"

    // $ANTLR start "FILEPATH"
    public final void mFILEPATH() throws RecognitionException {
        try {
            int _type = FILEPATH;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:166:11: ( 'generate' | 'file:/' ( LETTER | '..' ) ( LETTER | DIGIT | '+' | '-' | '\\\\' | '/' | '..' )* '.x' ( 'sd' | 'ml' ) | 'chain:' ( LETTER | '..' ) ( LETTER | DIGIT | '+' | '-' | '\\\\' | '/' | '..' )* ( '.xml' | '.tgd' ) )
            int alt8=3;
            switch ( input.LA(1) ) {
            case 'g':
                {
                alt8=1;
                }
                break;
            case 'f':
                {
                alt8=2;
                }
                break;
            case 'c':
                {
                alt8=3;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 8, 0, input);

                throw nvae;
            }

            switch (alt8) {
                case 1 :
                    // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:166:16: 'generate'
                    {
                    match("generate"); 


                    }
                    break;
                case 2 :
                    // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:167:17: 'file:/' ( LETTER | '..' ) ( LETTER | DIGIT | '+' | '-' | '\\\\' | '/' | '..' )* '.x' ( 'sd' | 'ml' )
                    {
                    match("file:/"); 

                    // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:167:26: ( LETTER | '..' )
                    int alt2=2;
                    int LA2_0 = input.LA(1);

                    if ( ((LA2_0>='A' && LA2_0<='Z')||(LA2_0>='a' && LA2_0<='z')) ) {
                        alt2=1;
                    }
                    else if ( (LA2_0=='.') ) {
                        alt2=2;
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("", 2, 0, input);

                        throw nvae;
                    }
                    switch (alt2) {
                        case 1 :
                            // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:167:27: LETTER
                            {
                            mLETTER(); 

                            }
                            break;
                        case 2 :
                            // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:167:36: '..'
                            {
                            match(".."); 


                            }
                            break;

                    }

                    // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:167:42: ( LETTER | DIGIT | '+' | '-' | '\\\\' | '/' | '..' )*
                    loop3:
                    do {
                        int alt3=8;
                        alt3 = dfa3.predict(input);
                        switch (alt3) {
                    	case 1 :
                    	    // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:167:43: LETTER
                    	    {
                    	    mLETTER(); 

                    	    }
                    	    break;
                    	case 2 :
                    	    // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:167:52: DIGIT
                    	    {
                    	    mDIGIT(); 

                    	    }
                    	    break;
                    	case 3 :
                    	    // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:167:60: '+'
                    	    {
                    	    match('+'); 

                    	    }
                    	    break;
                    	case 4 :
                    	    // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:167:66: '-'
                    	    {
                    	    match('-'); 

                    	    }
                    	    break;
                    	case 5 :
                    	    // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:167:72: '\\\\'
                    	    {
                    	    match('\\'); 

                    	    }
                    	    break;
                    	case 6 :
                    	    // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:167:79: '/'
                    	    {
                    	    match('/'); 

                    	    }
                    	    break;
                    	case 7 :
                    	    // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:167:85: '..'
                    	    {
                    	    match(".."); 


                    	    }
                    	    break;

                    	default :
                    	    break loop3;
                        }
                    } while (true);

                    match(".x"); 

                    // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:167:97: ( 'sd' | 'ml' )
                    int alt4=2;
                    int LA4_0 = input.LA(1);

                    if ( (LA4_0=='s') ) {
                        alt4=1;
                    }
                    else if ( (LA4_0=='m') ) {
                        alt4=2;
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("", 4, 0, input);

                        throw nvae;
                    }
                    switch (alt4) {
                        case 1 :
                            // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:167:98: 'sd'
                            {
                            match("sd"); 


                            }
                            break;
                        case 2 :
                            // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:167:105: 'ml'
                            {
                            match("ml"); 


                            }
                            break;

                    }


                    }
                    break;
                case 3 :
                    // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:168:17: 'chain:' ( LETTER | '..' ) ( LETTER | DIGIT | '+' | '-' | '\\\\' | '/' | '..' )* ( '.xml' | '.tgd' )
                    {
                    match("chain:"); 

                    // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:168:26: ( LETTER | '..' )
                    int alt5=2;
                    int LA5_0 = input.LA(1);

                    if ( ((LA5_0>='A' && LA5_0<='Z')||(LA5_0>='a' && LA5_0<='z')) ) {
                        alt5=1;
                    }
                    else if ( (LA5_0=='.') ) {
                        alt5=2;
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("", 5, 0, input);

                        throw nvae;
                    }
                    switch (alt5) {
                        case 1 :
                            // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:168:27: LETTER
                            {
                            mLETTER(); 

                            }
                            break;
                        case 2 :
                            // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:168:36: '..'
                            {
                            match(".."); 


                            }
                            break;

                    }

                    // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:168:42: ( LETTER | DIGIT | '+' | '-' | '\\\\' | '/' | '..' )*
                    loop6:
                    do {
                        int alt6=8;
                        alt6 = dfa6.predict(input);
                        switch (alt6) {
                    	case 1 :
                    	    // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:168:43: LETTER
                    	    {
                    	    mLETTER(); 

                    	    }
                    	    break;
                    	case 2 :
                    	    // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:168:52: DIGIT
                    	    {
                    	    mDIGIT(); 

                    	    }
                    	    break;
                    	case 3 :
                    	    // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:168:60: '+'
                    	    {
                    	    match('+'); 

                    	    }
                    	    break;
                    	case 4 :
                    	    // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:168:66: '-'
                    	    {
                    	    match('-'); 

                    	    }
                    	    break;
                    	case 5 :
                    	    // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:168:72: '\\\\'
                    	    {
                    	    match('\\'); 

                    	    }
                    	    break;
                    	case 6 :
                    	    // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:168:79: '/'
                    	    {
                    	    match('/'); 

                    	    }
                    	    break;
                    	case 7 :
                    	    // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:168:85: '..'
                    	    {
                    	    match(".."); 


                    	    }
                    	    break;

                    	default :
                    	    break loop6;
                        }
                    } while (true);

                    // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:168:92: ( '.xml' | '.tgd' )
                    int alt7=2;
                    int LA7_0 = input.LA(1);

                    if ( (LA7_0=='.') ) {
                        int LA7_1 = input.LA(2);

                        if ( (LA7_1=='x') ) {
                            alt7=1;
                        }
                        else if ( (LA7_1=='t') ) {
                            alt7=2;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("", 7, 1, input);

                            throw nvae;
                        }
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("", 7, 0, input);

                        throw nvae;
                    }
                    switch (alt7) {
                        case 1 :
                            // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:168:93: '.xml'
                            {
                            match(".xml"); 


                            }
                            break;
                        case 2 :
                            // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:168:102: '.tgd'
                            {
                            match(".tgd"); 


                            }
                            break;

                    }


                    }
                    break;

            }
            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "FILEPATH"

    // $ANTLR start "IDENTIFIER"
    public final void mIDENTIFIER() throws RecognitionException {
        try {
            int _type = IDENTIFIER;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:169:13: ( ( LETTER ) ( LETTER | DIGIT | '_' | '.' )* )
            // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:169:17: ( LETTER ) ( LETTER | DIGIT | '_' | '.' )*
            {
            // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:169:17: ( LETTER )
            // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:169:18: LETTER
            {
            mLETTER(); 

            }

            // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:169:26: ( LETTER | DIGIT | '_' | '.' )*
            loop9:
            do {
                int alt9=2;
                int LA9_0 = input.LA(1);

                if ( (LA9_0=='.'||(LA9_0>='0' && LA9_0<='9')||(LA9_0>='A' && LA9_0<='Z')||LA9_0=='_'||(LA9_0>='a' && LA9_0<='z')) ) {
                    alt9=1;
                }


                switch (alt9) {
            	case 1 :
            	    // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:
            	    {
            	    if ( input.LA(1)=='.'||(input.LA(1)>='0' && input.LA(1)<='9')||(input.LA(1)>='A' && input.LA(1)<='Z')||input.LA(1)=='_'||(input.LA(1)>='a' && input.LA(1)<='z') ) {
            	        input.consume();

            	    }
            	    else {
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        recover(mse);
            	        throw mse;}


            	    }
            	    break;

            	default :
            	    break loop9;
                }
            } while (true);


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "IDENTIFIER"

    // $ANTLR start "STRING"
    public final void mSTRING() throws RecognitionException {
        try {
            int _type = STRING;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:171:9: ( '\"' ( LETTER | DIGIT | '-' | '.' | ' ' )+ '\"' )
            // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:171:13: '\"' ( LETTER | DIGIT | '-' | '.' | ' ' )+ '\"'
            {
            match('\"'); 
            // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:171:17: ( LETTER | DIGIT | '-' | '.' | ' ' )+
            int cnt10=0;
            loop10:
            do {
                int alt10=2;
                int LA10_0 = input.LA(1);

                if ( (LA10_0==' '||(LA10_0>='-' && LA10_0<='.')||(LA10_0>='0' && LA10_0<='9')||(LA10_0>='A' && LA10_0<='Z')||(LA10_0>='a' && LA10_0<='z')) ) {
                    alt10=1;
                }


                switch (alt10) {
            	case 1 :
            	    // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:
            	    {
            	    if ( input.LA(1)==' '||(input.LA(1)>='-' && input.LA(1)<='.')||(input.LA(1)>='0' && input.LA(1)<='9')||(input.LA(1)>='A' && input.LA(1)<='Z')||(input.LA(1)>='a' && input.LA(1)<='z') ) {
            	        input.consume();

            	    }
            	    else {
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        recover(mse);
            	        throw mse;}


            	    }
            	    break;

            	default :
            	    if ( cnt10 >= 1 ) break loop10;
                        EarlyExitException eee =
                            new EarlyExitException(10, input);
                        throw eee;
                }
                cnt10++;
            } while (true);

            match('\"'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "STRING"

    // $ANTLR start "NUMBER"
    public final void mNUMBER() throws RecognitionException {
        try {
            int _type = NUMBER;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:172:8: ( ( '-' )? ( DIGIT )+ ( '.' ( DIGIT )+ )? )
            // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:172:11: ( '-' )? ( DIGIT )+ ( '.' ( DIGIT )+ )?
            {
            // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:172:11: ( '-' )?
            int alt11=2;
            int LA11_0 = input.LA(1);

            if ( (LA11_0=='-') ) {
                alt11=1;
            }
            switch (alt11) {
                case 1 :
                    // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:172:12: '-'
                    {
                    match('-'); 

                    }
                    break;

            }

            // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:172:18: ( DIGIT )+
            int cnt12=0;
            loop12:
            do {
                int alt12=2;
                int LA12_0 = input.LA(1);

                if ( ((LA12_0>='0' && LA12_0<='9')) ) {
                    alt12=1;
                }


                switch (alt12) {
            	case 1 :
            	    // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:172:18: DIGIT
            	    {
            	    mDIGIT(); 

            	    }
            	    break;

            	default :
            	    if ( cnt12 >= 1 ) break loop12;
                        EarlyExitException eee =
                            new EarlyExitException(12, input);
                        throw eee;
                }
                cnt12++;
            } while (true);

            // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:172:25: ( '.' ( DIGIT )+ )?
            int alt14=2;
            int LA14_0 = input.LA(1);

            if ( (LA14_0=='.') ) {
                alt14=1;
            }
            switch (alt14) {
                case 1 :
                    // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:172:26: '.' ( DIGIT )+
                    {
                    match('.'); 
                    // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:172:30: ( DIGIT )+
                    int cnt13=0;
                    loop13:
                    do {
                        int alt13=2;
                        int LA13_0 = input.LA(1);

                        if ( ((LA13_0>='0' && LA13_0<='9')) ) {
                            alt13=1;
                        }


                        switch (alt13) {
                    	case 1 :
                    	    // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:172:30: DIGIT
                    	    {
                    	    mDIGIT(); 

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt13 >= 1 ) break loop13;
                                EarlyExitException eee =
                                    new EarlyExitException(13, input);
                                throw eee;
                        }
                        cnt13++;
                    } while (true);


                    }
                    break;

            }


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "NUMBER"

    // $ANTLR start "NULL"
    public final void mNULL() throws RecognitionException {
        try {
            int _type = NULL;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:173:9: ( '#NULL#' )
            // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:173:17: '#NULL#'
            {
            match("#NULL#"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "NULL"

    // $ANTLR start "DIGIT"
    public final void mDIGIT() throws RecognitionException {
        try {
            // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:174:16: ( '0' .. '9' )
            // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:174:18: '0' .. '9'
            {
            matchRange('0','9'); 

            }

        }
        finally {
        }
    }
    // $ANTLR end "DIGIT"

    // $ANTLR start "LETTER"
    public final void mLETTER() throws RecognitionException {
        try {
            // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:175:17: ( 'a' .. 'z' | 'A' .. 'Z' )
            // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:
            {
            if ( (input.LA(1)>='A' && input.LA(1)<='Z')||(input.LA(1)>='a' && input.LA(1)<='z') ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}


            }

        }
        finally {
        }
    }
    // $ANTLR end "LETTER"

    // $ANTLR start "WHITESPACE"
    public final void mWHITESPACE() throws RecognitionException {
        try {
            int _type = WHITESPACE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:176:12: ( ( '\\t' | ' ' | '\\r' | '\\n' | '\\u000C' )+ )
            // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:176:15: ( '\\t' | ' ' | '\\r' | '\\n' | '\\u000C' )+
            {
            // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:176:15: ( '\\t' | ' ' | '\\r' | '\\n' | '\\u000C' )+
            int cnt15=0;
            loop15:
            do {
                int alt15=2;
                int LA15_0 = input.LA(1);

                if ( ((LA15_0>='\t' && LA15_0<='\n')||(LA15_0>='\f' && LA15_0<='\r')||LA15_0==' ') ) {
                    alt15=1;
                }


                switch (alt15) {
            	case 1 :
            	    // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:
            	    {
            	    if ( (input.LA(1)>='\t' && input.LA(1)<='\n')||(input.LA(1)>='\f' && input.LA(1)<='\r')||input.LA(1)==' ' ) {
            	        input.consume();

            	    }
            	    else {
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        recover(mse);
            	        throw mse;}


            	    }
            	    break;

            	default :
            	    if ( cnt15 >= 1 ) break loop15;
                        EarlyExitException eee =
                            new EarlyExitException(15, input);
                        throw eee;
                }
                cnt15++;
            } while (true);

             skip(); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "WHITESPACE"

    // $ANTLR start "LINE_COMMENT"
    public final void mLINE_COMMENT() throws RecognitionException {
        try {
            int _type = LINE_COMMENT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:177:14: ( '//' (~ ( '\\r' | '\\n' ) )* )
            // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:177:17: '//' (~ ( '\\r' | '\\n' ) )*
            {
            match("//"); 

            // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:177:22: (~ ( '\\r' | '\\n' ) )*
            loop16:
            do {
                int alt16=2;
                int LA16_0 = input.LA(1);

                if ( ((LA16_0>='\u0000' && LA16_0<='\t')||(LA16_0>='\u000B' && LA16_0<='\f')||(LA16_0>='\u000E' && LA16_0<='\uFFFF')) ) {
                    alt16=1;
                }


                switch (alt16) {
            	case 1 :
            	    // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:177:22: ~ ( '\\r' | '\\n' )
            	    {
            	    if ( (input.LA(1)>='\u0000' && input.LA(1)<='\t')||(input.LA(1)>='\u000B' && input.LA(1)<='\f')||(input.LA(1)>='\u000E' && input.LA(1)<='\uFFFF') ) {
            	        input.consume();

            	    }
            	    else {
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        recover(mse);
            	        throw mse;}


            	    }
            	    break;

            	default :
            	    break loop16;
                }
            } while (true);

             skip(); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "LINE_COMMENT"

    // $ANTLR start "EXPRESSION"
    public final void mEXPRESSION() throws RecognitionException {
        try {
            int _type = EXPRESSION;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:178:11: ( '{' ( . )* '}' )
            // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:178:12: '{' ( . )* '}'
            {
            match('{'); 
            // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:178:15: ( . )*
            loop17:
            do {
                int alt17=2;
                int LA17_0 = input.LA(1);

                if ( (LA17_0=='}') ) {
                    alt17=2;
                }
                else if ( ((LA17_0>='\u0000' && LA17_0<='|')||(LA17_0>='~' && LA17_0<='\uFFFF')) ) {
                    alt17=1;
                }


                switch (alt17) {
            	case 1 :
            	    // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:178:16: .
            	    {
            	    matchAny(); 

            	    }
            	    break;

            	default :
            	    break loop17;
                }
            } while (true);

            match('}'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "EXPRESSION"

    public void mTokens() throws RecognitionException {
        // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:1:8: ( T__15 | T__16 | T__17 | T__18 | T__19 | T__20 | T__21 | T__22 | T__23 | T__24 | T__25 | T__26 | T__27 | T__28 | T__29 | T__30 | T__31 | T__32 | T__33 | T__34 | T__35 | T__36 | T__37 | T__38 | T__39 | T__40 | T__41 | T__42 | T__43 | T__44 | T__45 | T__46 | T__47 | T__48 | T__49 | OPERATOR | FILEPATH | IDENTIFIER | STRING | NUMBER | NULL | WHITESPACE | LINE_COMMENT | EXPRESSION )
        int alt18=44;
        alt18 = dfa18.predict(input);
        switch (alt18) {
            case 1 :
                // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:1:10: T__15
                {
                mT__15(); 

                }
                break;
            case 2 :
                // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:1:16: T__16
                {
                mT__16(); 

                }
                break;
            case 3 :
                // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:1:22: T__17
                {
                mT__17(); 

                }
                break;
            case 4 :
                // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:1:28: T__18
                {
                mT__18(); 

                }
                break;
            case 5 :
                // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:1:34: T__19
                {
                mT__19(); 

                }
                break;
            case 6 :
                // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:1:40: T__20
                {
                mT__20(); 

                }
                break;
            case 7 :
                // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:1:46: T__21
                {
                mT__21(); 

                }
                break;
            case 8 :
                // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:1:52: T__22
                {
                mT__22(); 

                }
                break;
            case 9 :
                // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:1:58: T__23
                {
                mT__23(); 

                }
                break;
            case 10 :
                // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:1:64: T__24
                {
                mT__24(); 

                }
                break;
            case 11 :
                // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:1:70: T__25
                {
                mT__25(); 

                }
                break;
            case 12 :
                // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:1:76: T__26
                {
                mT__26(); 

                }
                break;
            case 13 :
                // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:1:82: T__27
                {
                mT__27(); 

                }
                break;
            case 14 :
                // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:1:88: T__28
                {
                mT__28(); 

                }
                break;
            case 15 :
                // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:1:94: T__29
                {
                mT__29(); 

                }
                break;
            case 16 :
                // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:1:100: T__30
                {
                mT__30(); 

                }
                break;
            case 17 :
                // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:1:106: T__31
                {
                mT__31(); 

                }
                break;
            case 18 :
                // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:1:112: T__32
                {
                mT__32(); 

                }
                break;
            case 19 :
                // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:1:118: T__33
                {
                mT__33(); 

                }
                break;
            case 20 :
                // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:1:124: T__34
                {
                mT__34(); 

                }
                break;
            case 21 :
                // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:1:130: T__35
                {
                mT__35(); 

                }
                break;
            case 22 :
                // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:1:136: T__36
                {
                mT__36(); 

                }
                break;
            case 23 :
                // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:1:142: T__37
                {
                mT__37(); 

                }
                break;
            case 24 :
                // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:1:148: T__38
                {
                mT__38(); 

                }
                break;
            case 25 :
                // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:1:154: T__39
                {
                mT__39(); 

                }
                break;
            case 26 :
                // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:1:160: T__40
                {
                mT__40(); 

                }
                break;
            case 27 :
                // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:1:166: T__41
                {
                mT__41(); 

                }
                break;
            case 28 :
                // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:1:172: T__42
                {
                mT__42(); 

                }
                break;
            case 29 :
                // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:1:178: T__43
                {
                mT__43(); 

                }
                break;
            case 30 :
                // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:1:184: T__44
                {
                mT__44(); 

                }
                break;
            case 31 :
                // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:1:190: T__45
                {
                mT__45(); 

                }
                break;
            case 32 :
                // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:1:196: T__46
                {
                mT__46(); 

                }
                break;
            case 33 :
                // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:1:202: T__47
                {
                mT__47(); 

                }
                break;
            case 34 :
                // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:1:208: T__48
                {
                mT__48(); 

                }
                break;
            case 35 :
                // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:1:214: T__49
                {
                mT__49(); 

                }
                break;
            case 36 :
                // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:1:220: OPERATOR
                {
                mOPERATOR(); 

                }
                break;
            case 37 :
                // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:1:229: FILEPATH
                {
                mFILEPATH(); 

                }
                break;
            case 38 :
                // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:1:238: IDENTIFIER
                {
                mIDENTIFIER(); 

                }
                break;
            case 39 :
                // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:1:249: STRING
                {
                mSTRING(); 

                }
                break;
            case 40 :
                // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:1:256: NUMBER
                {
                mNUMBER(); 

                }
                break;
            case 41 :
                // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:1:263: NULL
                {
                mNULL(); 

                }
                break;
            case 42 :
                // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:1:268: WHITESPACE
                {
                mWHITESPACE(); 

                }
                break;
            case 43 :
                // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:1:279: LINE_COMMENT
                {
                mLINE_COMMENT(); 

                }
                break;
            case 44 :
                // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:1:292: EXPRESSION
                {
                mEXPRESSION(); 

                }
                break;

        }

    }


    protected DFA3 dfa3 = new DFA3(this);
    protected DFA6 dfa6 = new DFA6(this);
    protected DFA18 dfa18 = new DFA18(this);
    static final String DFA3_eotS =
        "\12\uffff";
    static final String DFA3_eofS =
        "\12\uffff";
    static final String DFA3_minS =
        "\1\53\1\56\10\uffff";
    static final String DFA3_maxS =
        "\1\172\1\170\10\uffff";
    static final String DFA3_acceptS =
        "\2\uffff\1\1\1\2\1\3\1\4\1\5\1\6\1\10\1\7";
    static final String DFA3_specialS =
        "\12\uffff}>";
    static final String[] DFA3_transitionS = {
            "\1\4\1\uffff\1\5\1\1\1\7\12\3\7\uffff\32\2\1\uffff\1\6\4\uffff"+
            "\32\2",
            "\1\11\111\uffff\1\10",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            ""
    };

    static final short[] DFA3_eot = DFA.unpackEncodedString(DFA3_eotS);
    static final short[] DFA3_eof = DFA.unpackEncodedString(DFA3_eofS);
    static final char[] DFA3_min = DFA.unpackEncodedStringToUnsignedChars(DFA3_minS);
    static final char[] DFA3_max = DFA.unpackEncodedStringToUnsignedChars(DFA3_maxS);
    static final short[] DFA3_accept = DFA.unpackEncodedString(DFA3_acceptS);
    static final short[] DFA3_special = DFA.unpackEncodedString(DFA3_specialS);
    static final short[][] DFA3_transition;

    static {
        int numStates = DFA3_transitionS.length;
        DFA3_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA3_transition[i] = DFA.unpackEncodedString(DFA3_transitionS[i]);
        }
    }

    class DFA3 extends DFA {

        public DFA3(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 3;
            this.eot = DFA3_eot;
            this.eof = DFA3_eof;
            this.min = DFA3_min;
            this.max = DFA3_max;
            this.accept = DFA3_accept;
            this.special = DFA3_special;
            this.transition = DFA3_transition;
        }
        public String getDescription() {
            return "()* loopback of 167:42: ( LETTER | DIGIT | '+' | '-' | '\\\\' | '/' | '..' )*";
        }
    }
    static final String DFA6_eotS =
        "\12\uffff";
    static final String DFA6_eofS =
        "\12\uffff";
    static final String DFA6_minS =
        "\1\53\1\56\10\uffff";
    static final String DFA6_maxS =
        "\1\172\1\170\10\uffff";
    static final String DFA6_acceptS =
        "\2\uffff\1\1\1\2\1\3\1\4\1\5\1\6\1\10\1\7";
    static final String DFA6_specialS =
        "\12\uffff}>";
    static final String[] DFA6_transitionS = {
            "\1\4\1\uffff\1\5\1\1\1\7\12\3\7\uffff\32\2\1\uffff\1\6\4\uffff"+
            "\32\2",
            "\1\11\105\uffff\1\10\3\uffff\1\10",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            ""
    };

    static final short[] DFA6_eot = DFA.unpackEncodedString(DFA6_eotS);
    static final short[] DFA6_eof = DFA.unpackEncodedString(DFA6_eofS);
    static final char[] DFA6_min = DFA.unpackEncodedStringToUnsignedChars(DFA6_minS);
    static final char[] DFA6_max = DFA.unpackEncodedStringToUnsignedChars(DFA6_maxS);
    static final short[] DFA6_accept = DFA.unpackEncodedString(DFA6_acceptS);
    static final short[] DFA6_special = DFA.unpackEncodedString(DFA6_specialS);
    static final short[][] DFA6_transition;

    static {
        int numStates = DFA6_transitionS.length;
        DFA6_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA6_transition[i] = DFA.unpackEncodedString(DFA6_transitionS[i]);
        }
    }

    class DFA6 extends DFA {

        public DFA6(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 6;
            this.eot = DFA6_eot;
            this.eof = DFA6_eof;
            this.min = DFA6_min;
            this.max = DFA6_max;
            this.accept = DFA6_accept;
            this.special = DFA6_special;
            this.transition = DFA6_transition;
        }
        public String getDescription() {
            return "()* loopback of 168:42: ( LETTER | DIGIT | '+' | '-' | '\\\\' | '/' | '..' )*";
        }
    }
    static final String DFA18_eotS =
        "\1\uffff\10\31\3\uffff\1\31\1\uffff\1\31\1\uffff\1\60\5\uffff\3"+
        "\31\7\uffff\15\31\1\uffff\2\31\3\uffff\46\31\1\uffff\1\155\17\31"+
        "\1\uffff\2\31\1\uffff\1\31\1\uffff\21\31\2\uffff\6\31\3\uffff\5"+
        "\31\6\uffff\7\31\2\uffff\4\31\1\157\11\31\1\uffff\4\31\1\uffff\3"+
        "\31\1\uffff\12\31\1\uffff\5\31\1\uffff\1\31\2\uffff\3\31\1\uffff"+
        "\1\31\1\uffff\1\31\1\uffff\5\31\1\uffff";
    static final String DFA18_eofS =
        "\u00da\uffff";
    static final String DFA18_minS =
        "\1\11\1\141\1\105\1\101\2\117\1\107\1\126\1\117\1\60\2\uffff\1"+
        "\156\1\uffff\1\151\1\uffff\1\75\3\uffff\1\153\1\uffff\1\145\1\151"+
        "\1\150\7\uffff\1\160\1\165\1\122\1\102\1\114\1\117\1\162\1\122\1"+
        "\116\1\122\1\104\1\105\1\103\1\uffff\1\144\1\164\3\uffff\1\156\1"+
        "\154\1\141\1\160\1\162\1\122\1\124\1\123\1\106\1\114\1\147\1\107"+
        "\1\106\2\105\1\123\1\122\1\101\1\40\1\150\2\145\2\151\1\143\1\103"+
        "\1\123\1\125\1\112\1\105\1\145\1\105\1\111\1\122\1\127\1\72\2\114"+
        "\1\uffff\1\56\1\162\1\72\2\156\1\145\1\105\1\124\1\115\1\117\1\115"+
        "\1\164\1\124\1\107\1\101\1\122\1\uffff\1\101\1\123\1\uffff\1\141"+
        "\1\uffff\1\72\1\147\2\40\1\122\1\120\1\111\1\123\2\40\1\72\1\107"+
        "\1\111\1\120\1\113\1\164\1\40\1\151\1\106\1\125\1\101\1\124\1\116"+
        "\1\106\1\101\1\uffff\1\106\1\uffff\1\105\1\124\1\123\1\117\1\145"+
        "\6\uffff\1\114\1\124\1\111\1\123\1\117\1\122\1\102\2\uffff\1\123"+
        "\1\111\1\72\1\114\1\56\1\114\1\105\1\117\1\72\1\122\1\111\1\114"+
        "\1\72\1\116\1\uffff\1\105\1\123\1\107\1\116\1\uffff\1\105\1\116"+
        "\1\105\1\uffff\1\107\1\115\1\72\1\131\1\123\2\107\1\123\1\72\1\123"+
        "\1\uffff\2\72\1\104\1\123\1\124\1\uffff\1\72\2\uffff\1\123\1\72"+
        "\1\122\1\uffff\1\72\1\uffff\1\101\1\uffff\1\124\1\105\1\107\1\131"+
        "\1\72\1\uffff";
    static final String DFA18_maxS =
        "\1\173\1\141\1\157\1\141\2\117\1\107\1\126\1\117\1\76\2\uffff\1"+
        "\156\1\uffff\1\151\1\uffff\1\75\3\uffff\1\160\1\uffff\1\145\1\151"+
        "\1\150\7\uffff\1\160\1\165\1\125\1\102\1\114\1\117\1\162\1\122\1"+
        "\126\1\122\1\104\1\105\1\103\1\uffff\1\144\1\164\3\uffff\1\156\1"+
        "\154\1\141\1\160\1\162\1\122\1\124\1\123\1\106\1\114\1\147\1\107"+
        "\1\106\2\105\1\123\1\122\1\101\1\40\1\150\2\145\2\151\1\143\1\103"+
        "\1\123\1\125\1\112\1\105\1\145\1\105\1\111\1\122\1\127\1\72\2\114"+
        "\1\uffff\1\172\1\162\1\72\2\156\1\145\1\105\1\124\1\115\1\117\1"+
        "\115\1\164\1\124\1\107\1\101\1\122\1\uffff\1\101\1\123\1\uffff\1"+
        "\141\1\uffff\1\72\1\147\1\40\1\116\1\122\1\120\1\111\1\124\2\40"+
        "\1\72\1\107\1\111\1\120\1\113\1\164\1\40\1\163\1\124\1\125\1\101"+
        "\1\124\1\116\1\124\1\101\1\uffff\1\124\1\uffff\1\105\1\124\1\123"+
        "\1\117\1\145\6\uffff\1\114\1\124\1\111\1\123\1\117\1\122\1\102\2"+
        "\uffff\1\123\1\111\1\72\1\114\1\172\1\114\1\105\1\117\1\72\1\122"+
        "\1\111\1\114\1\72\1\116\1\uffff\1\105\1\123\1\107\1\116\1\uffff"+
        "\1\105\1\116\1\105\1\uffff\1\107\1\115\1\72\1\131\1\123\2\107\1"+
        "\123\1\72\1\123\1\uffff\2\72\1\104\1\123\1\124\1\uffff\1\72\2\uffff"+
        "\1\123\1\72\1\122\1\uffff\1\72\1\uffff\1\101\1\uffff\1\124\1\105"+
        "\1\107\1\131\1\72\1\uffff";
    static final String DFA18_acceptS =
        "\12\uffff\1\30\1\31\1\uffff\1\33\1\uffff\1\35\1\uffff\1\37\1\40"+
        "\1\41\1\uffff\1\44\3\uffff\1\46\1\47\1\50\1\51\1\52\1\53\1\54\15"+
        "\uffff\1\27\2\uffff\1\36\1\42\1\43\46\uffff\1\32\20\uffff\1\20\2"+
        "\uffff\1\34\1\uffff\1\45\31\uffff\1\4\1\uffff\1\12\5\uffff\1\1\1"+
        "\2\1\3\1\5\1\7\1\11\7\uffff\1\6\1\10\16\uffff\1\21\4\uffff\1\16"+
        "\3\uffff\1\15\12\uffff\1\13\5\uffff\1\17\1\uffff\1\25\1\14\3\uffff"+
        "\1\24\1\uffff\1\23\1\uffff\1\22\5\uffff\1\26";
    static final String DFA18_specialS =
        "\u00da\uffff}>";
    static final String[] DFA18_transitionS = {
            "\2\35\1\uffff\2\35\22\uffff\1\35\1\25\1\32\1\34\1\22\3\uffff"+
            "\1\15\1\17\2\uffff\1\13\1\11\1\12\1\36\12\33\1\23\1\uffff\1"+
            "\25\1\20\1\25\2\uffff\2\31\1\4\1\31\1\6\6\31\1\10\1\1\1\5\1"+
            "\7\3\31\1\2\1\3\6\31\1\24\3\uffff\1\21\1\uffff\1\14\1\31\1\30"+
            "\2\31\1\27\1\26\17\31\1\16\3\31\1\37",
            "\1\40",
            "\1\44\5\uffff\1\45\3\uffff\1\42\5\uffff\1\43\31\uffff\1\41",
            "\1\47\37\uffff\1\46",
            "\1\50",
            "\1\51",
            "\1\52",
            "\1\53",
            "\1\54",
            "\12\33\4\uffff\1\55",
            "",
            "",
            "\1\56",
            "",
            "\1\57",
            "",
            "\1\25",
            "",
            "",
            "",
            "\1\62\4\uffff\1\61",
            "",
            "\1\63",
            "\1\64",
            "\1\65",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\66",
            "\1\67",
            "\1\71\2\uffff\1\70",
            "\1\72",
            "\1\73",
            "\1\74",
            "\1\75",
            "\1\76",
            "\1\77\7\uffff\1\100",
            "\1\101",
            "\1\102",
            "\1\103",
            "\1\104",
            "",
            "\1\105",
            "\1\106",
            "",
            "",
            "",
            "\1\107",
            "\1\110",
            "\1\111",
            "\1\112",
            "\1\113",
            "\1\114",
            "\1\115",
            "\1\116",
            "\1\117",
            "\1\120",
            "\1\121",
            "\1\122",
            "\1\123",
            "\1\124",
            "\1\125",
            "\1\126",
            "\1\127",
            "\1\130",
            "\1\131",
            "\1\132",
            "\1\133",
            "\1\134",
            "\1\135",
            "\1\136",
            "\1\137",
            "\1\140",
            "\1\141",
            "\1\142",
            "\1\143",
            "\1\144",
            "\1\145",
            "\1\146",
            "\1\147",
            "\1\150",
            "\1\151",
            "\1\152",
            "\1\153",
            "\1\154",
            "",
            "\1\31\1\uffff\12\31\7\uffff\32\31\4\uffff\1\31\1\uffff\32"+
            "\31",
            "\1\156",
            "\1\157",
            "\1\160",
            "\1\161",
            "\1\162",
            "\1\163",
            "\1\164",
            "\1\165",
            "\1\166",
            "\1\167",
            "\1\170",
            "\1\171",
            "\1\172",
            "\1\173",
            "\1\174",
            "",
            "\1\175",
            "\1\176",
            "",
            "\1\177",
            "",
            "\1\157",
            "\1\u0080",
            "\1\u0081",
            "\1\u0082\55\uffff\1\u0083",
            "\1\u0084",
            "\1\u0085",
            "\1\u0086",
            "\1\u0087\1\u0088",
            "\1\u0089",
            "\1\u008a",
            "\1\u008b",
            "\1\u008c",
            "\1\u008d",
            "\1\u008e",
            "\1\u008f",
            "\1\u0090",
            "\1\u0091",
            "\1\u0093\11\uffff\1\u0092",
            "\1\u0095\2\uffff\1\u0096\12\uffff\1\u0094",
            "\1\u0097",
            "\1\u0098",
            "\1\u0099",
            "\1\u009a",
            "\1\u009b\15\uffff\1\u009c",
            "\1\u009d",
            "",
            "\1\u009f\15\uffff\1\u009e",
            "",
            "\1\u00a0",
            "\1\u00a1",
            "\1\u00a2",
            "\1\u00a3",
            "\1\u00a4",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\u00a5",
            "\1\u00a6",
            "\1\u00a7",
            "\1\u00a8",
            "\1\u00a9",
            "\1\u00aa",
            "\1\u00ab",
            "",
            "",
            "\1\u00ac",
            "\1\u00ad",
            "\1\u00ae",
            "\1\u00af",
            "\1\31\1\uffff\12\31\7\uffff\32\31\4\uffff\1\31\1\uffff\32"+
            "\31",
            "\1\u00b0",
            "\1\u00b1",
            "\1\u00b2",
            "\1\u00b3",
            "\1\u00b4",
            "\1\u00b5",
            "\1\u00b6",
            "\1\u00b7",
            "\1\u00b8",
            "",
            "\1\u00b9",
            "\1\u00ba",
            "\1\u00bb",
            "\1\u00bc",
            "",
            "\1\u00bd",
            "\1\u00be",
            "\1\u00bf",
            "",
            "\1\u00c0",
            "\1\u00c1",
            "\1\u00c2",
            "\1\u00c3",
            "\1\u00c4",
            "\1\u00c5",
            "\1\u00c6",
            "\1\u00c7",
            "\1\u00c8",
            "\1\u00c9",
            "",
            "\1\u00ca",
            "\1\u00cb",
            "\1\u00cc",
            "\1\u00cd",
            "\1\u00ce",
            "",
            "\1\u00cf",
            "",
            "",
            "\1\u00d0",
            "\1\u00d1",
            "\1\u00d2",
            "",
            "\1\u00d3",
            "",
            "\1\u00d4",
            "",
            "\1\u00d5",
            "\1\u00d6",
            "\1\u00d7",
            "\1\u00d8",
            "\1\u00d9",
            ""
    };

    static final short[] DFA18_eot = DFA.unpackEncodedString(DFA18_eotS);
    static final short[] DFA18_eof = DFA.unpackEncodedString(DFA18_eofS);
    static final char[] DFA18_min = DFA.unpackEncodedStringToUnsignedChars(DFA18_minS);
    static final char[] DFA18_max = DFA.unpackEncodedStringToUnsignedChars(DFA18_maxS);
    static final short[] DFA18_accept = DFA.unpackEncodedString(DFA18_acceptS);
    static final short[] DFA18_special = DFA.unpackEncodedString(DFA18_specialS);
    static final short[][] DFA18_transition;

    static {
        int numStates = DFA18_transitionS.length;
        DFA18_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA18_transition[i] = DFA.unpackEncodedString(DFA18_transitionS[i]);
        }
    }

    class DFA18 extends DFA {

        public DFA18(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 18;
            this.eot = DFA18_eot;
            this.eof = DFA18_eof;
            this.min = DFA18_min;
            this.max = DFA18_max;
            this.accept = DFA18_accept;
            this.special = DFA18_special;
            this.transition = DFA18_transition;
        }
        public String getDescription() {
            return "1:1: Tokens : ( T__15 | T__16 | T__17 | T__18 | T__19 | T__20 | T__21 | T__22 | T__23 | T__24 | T__25 | T__26 | T__27 | T__28 | T__29 | T__30 | T__31 | T__32 | T__33 | T__34 | T__35 | T__36 | T__37 | T__38 | T__39 | T__40 | T__41 | T__42 | T__43 | T__44 | T__45 | T__46 | T__47 | T__48 | T__49 | OPERATOR | FILEPATH | IDENTIFIER | STRING | NUMBER | NULL | WHITESPACE | LINE_COMMENT | EXPRESSION );";
        }
    }
 

}