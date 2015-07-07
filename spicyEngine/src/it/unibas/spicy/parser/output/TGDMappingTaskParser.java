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
 
// $ANTLR 3.3 Nov 30, 2010 12:45:30 D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g 2011-08-28 20:16:26

package it.unibas.spicy.parser.output;

import it.unibas.spicy.model.expressions.Expression;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import it.unibas.spicy.parser.operators.IParseMappingTask;
import it.unibas.spicy.parser.*;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;


import org.antlr.runtime.tree.*;

public class TGDMappingTaskParser extends Parser {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "FILEPATH", "NUMBER", "IDENTIFIER", "OPERATOR", "STRING", "NULL", "EXPRESSION", "LETTER", "DIGIT", "WHITESPACE", "LINE_COMMENT", "'Mapping task:'", "'Source schema:'", "'Source instance:'", "'Target schema:'", "'SOURCE TO TARGET TGDs:'", "'TARGET TGDs:'", "'SOURCE FDs:'", "'TARGET FDs:'", "'SOURCE INSTANCE:'", "'CONFIG:'", "'SOURCENULLS:'", "'SUBSUMPTIONS:'", "'COVERAGES:'", "'SELFJOINS:'", "'NOREWRITING:'", "'EGDS:'", "'OVERLAPS:'", "'SKOLEMSFOREGDS:'", "'SKOLEMSTRINGS:'", "'LOCALSKOLEMS:'", "'SORTSTRATEGY:'", "'SKOLEMTABLESTRATEGY:'", "'->'", "'.'", "','", "'and not exists'", "'('", "'with'", "')'", "'='", "'_'", "'\\$'", "':'", "'[pk]'", "'[key]'"
    };
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

    // delegates
    // delegators


        public TGDMappingTaskParser(TokenStream input) {
            this(input, new RecognizerSharedState());
        }
        public TGDMappingTaskParser(TokenStream input, RecognizerSharedState state) {
            super(input, state);
             
        }
        
    protected TreeAdaptor adaptor = new CommonTreeAdaptor();

    public void setTreeAdaptor(TreeAdaptor adaptor) {
        this.adaptor = adaptor;
    }
    public TreeAdaptor getTreeAdaptor() {
        return adaptor;
    }

    public String[] getTokenNames() { return TGDMappingTaskParser.tokenNames; }
    public String getGrammarFileName() { return "D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g"; }


    private static Log logger = LogFactory.getLog(TGDMappingTaskParser.class);
    private IParseMappingTask generator;

    private ParserTGD currentTGD;
    private ParserView currentView;
    private ParserAtom currentAtom;
    private ParserAttribute currentAttribute;
    private ParserBuiltinFunction currentFunction;
    private ParserBuiltinOperator currentOperator;
    private ParserArgument currentArgument;
    private List<ParserArgument> currentArgumentList;
    private ParserFact currentFact;
    private ParserFD currentFD;
    private ParserEquality currentEquality;
    private ParserInstance currentInstance;
    private List<String> currentStringList;
    private List<ParserFD> currentFDList;

    private List<ParserView> currentViewList;

    public void setGenerator(IParseMappingTask generator) {
          this.generator = generator;
    }


    public static class prog_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "prog"
    // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:54:1: prog : mappingTask ;
    public final TGDMappingTaskParser.prog_return prog() throws RecognitionException {
        TGDMappingTaskParser.prog_return retval = new TGDMappingTaskParser.prog_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        TGDMappingTaskParser.mappingTask_return mappingTask1 = null;



        try {
            // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:54:5: ( mappingTask )
            // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:54:7: mappingTask
            {
            root_0 = (CommonTree)adaptor.nil();

            pushFollow(FOLLOW_mappingTask_in_prog54);
            mappingTask1=mappingTask();

            state._fsp--;

            adaptor.addChild(root_0, mappingTask1.getTree());
             if (logger.isDebugEnabled()) logger.debug((mappingTask1!=null?((CommonTree)mappingTask1.tree):null).toStringTree()); 

            }

            retval.stop = input.LT(-1);

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "prog"

    public static class mappingTask_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "mappingTask"
    // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:56:1: mappingTask : 'Mapping task:' ( 'Source schema:' ssf= FILEPATH 'Source instance:' sif= FILEPATH )+ 'Target schema:' stf= FILEPATH 'SOURCE TO TARGET TGDs:' ( sttgd )+ ( 'TARGET TGDs:' ( ttgd )+ )? ( 'SOURCE FDs:' ( fd )+ )? ( 'TARGET FDs:' ( fd )+ )? ( 'SOURCE INSTANCE:' ( fact )+ )* ( 'CONFIG:' ( 'SOURCENULLS:' sourceNulls= NUMBER )? ( 'SUBSUMPTIONS:' subsumptions= NUMBER )? ( 'COVERAGES:' coverages= NUMBER )? ( 'SELFJOINS:' selfJoins= NUMBER )? ( 'NOREWRITING:' noRewriting= NUMBER )? ( 'EGDS:' egds= NUMBER )? ( 'OVERLAPS:' overlaps= NUMBER )? ( 'SKOLEMSFOREGDS:' skolemForEgds= NUMBER )? ( 'SKOLEMSTRINGS:' skolemStrings= NUMBER )? ( 'LOCALSKOLEMS:' localSkolems= NUMBER )? ( 'SORTSTRATEGY:' sortStrategy= NUMBER )? ( 'SKOLEMTABLESTRATEGY:' skolemTableStrategy= NUMBER )? )? ;
    public final TGDMappingTaskParser.mappingTask_return mappingTask() throws RecognitionException {
        TGDMappingTaskParser.mappingTask_return retval = new TGDMappingTaskParser.mappingTask_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token ssf=null;
        Token sif=null;
        Token stf=null;
        Token sourceNulls=null;
        Token subsumptions=null;
        Token coverages=null;
        Token selfJoins=null;
        Token noRewriting=null;
        Token egds=null;
        Token overlaps=null;
        Token skolemForEgds=null;
        Token skolemStrings=null;
        Token localSkolems=null;
        Token sortStrategy=null;
        Token skolemTableStrategy=null;
        Token string_literal2=null;
        Token string_literal3=null;
        Token string_literal4=null;
        Token string_literal5=null;
        Token string_literal6=null;
        Token string_literal8=null;
        Token string_literal10=null;
        Token string_literal12=null;
        Token string_literal14=null;
        Token string_literal16=null;
        Token string_literal17=null;
        Token string_literal18=null;
        Token string_literal19=null;
        Token string_literal20=null;
        Token string_literal21=null;
        Token string_literal22=null;
        Token string_literal23=null;
        Token string_literal24=null;
        Token string_literal25=null;
        Token string_literal26=null;
        Token string_literal27=null;
        Token string_literal28=null;
        TGDMappingTaskParser.sttgd_return sttgd7 = null;

        TGDMappingTaskParser.ttgd_return ttgd9 = null;

        TGDMappingTaskParser.fd_return fd11 = null;

        TGDMappingTaskParser.fd_return fd13 = null;

        TGDMappingTaskParser.fact_return fact15 = null;


        CommonTree ssf_tree=null;
        CommonTree sif_tree=null;
        CommonTree stf_tree=null;
        CommonTree sourceNulls_tree=null;
        CommonTree subsumptions_tree=null;
        CommonTree coverages_tree=null;
        CommonTree selfJoins_tree=null;
        CommonTree noRewriting_tree=null;
        CommonTree egds_tree=null;
        CommonTree overlaps_tree=null;
        CommonTree skolemForEgds_tree=null;
        CommonTree skolemStrings_tree=null;
        CommonTree localSkolems_tree=null;
        CommonTree sortStrategy_tree=null;
        CommonTree skolemTableStrategy_tree=null;
        CommonTree string_literal2_tree=null;
        CommonTree string_literal3_tree=null;
        CommonTree string_literal4_tree=null;
        CommonTree string_literal5_tree=null;
        CommonTree string_literal6_tree=null;
        CommonTree string_literal8_tree=null;
        CommonTree string_literal10_tree=null;
        CommonTree string_literal12_tree=null;
        CommonTree string_literal14_tree=null;
        CommonTree string_literal16_tree=null;
        CommonTree string_literal17_tree=null;
        CommonTree string_literal18_tree=null;
        CommonTree string_literal19_tree=null;
        CommonTree string_literal20_tree=null;
        CommonTree string_literal21_tree=null;
        CommonTree string_literal22_tree=null;
        CommonTree string_literal23_tree=null;
        CommonTree string_literal24_tree=null;
        CommonTree string_literal25_tree=null;
        CommonTree string_literal26_tree=null;
        CommonTree string_literal27_tree=null;
        CommonTree string_literal28_tree=null;

        try {
            // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:56:12: ( 'Mapping task:' ( 'Source schema:' ssf= FILEPATH 'Source instance:' sif= FILEPATH )+ 'Target schema:' stf= FILEPATH 'SOURCE TO TARGET TGDs:' ( sttgd )+ ( 'TARGET TGDs:' ( ttgd )+ )? ( 'SOURCE FDs:' ( fd )+ )? ( 'TARGET FDs:' ( fd )+ )? ( 'SOURCE INSTANCE:' ( fact )+ )* ( 'CONFIG:' ( 'SOURCENULLS:' sourceNulls= NUMBER )? ( 'SUBSUMPTIONS:' subsumptions= NUMBER )? ( 'COVERAGES:' coverages= NUMBER )? ( 'SELFJOINS:' selfJoins= NUMBER )? ( 'NOREWRITING:' noRewriting= NUMBER )? ( 'EGDS:' egds= NUMBER )? ( 'OVERLAPS:' overlaps= NUMBER )? ( 'SKOLEMSFOREGDS:' skolemForEgds= NUMBER )? ( 'SKOLEMSTRINGS:' skolemStrings= NUMBER )? ( 'LOCALSKOLEMS:' localSkolems= NUMBER )? ( 'SORTSTRATEGY:' sortStrategy= NUMBER )? ( 'SKOLEMTABLESTRATEGY:' skolemTableStrategy= NUMBER )? )? )
            // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:56:17: 'Mapping task:' ( 'Source schema:' ssf= FILEPATH 'Source instance:' sif= FILEPATH )+ 'Target schema:' stf= FILEPATH 'SOURCE TO TARGET TGDs:' ( sttgd )+ ( 'TARGET TGDs:' ( ttgd )+ )? ( 'SOURCE FDs:' ( fd )+ )? ( 'TARGET FDs:' ( fd )+ )? ( 'SOURCE INSTANCE:' ( fact )+ )* ( 'CONFIG:' ( 'SOURCENULLS:' sourceNulls= NUMBER )? ( 'SUBSUMPTIONS:' subsumptions= NUMBER )? ( 'COVERAGES:' coverages= NUMBER )? ( 'SELFJOINS:' selfJoins= NUMBER )? ( 'NOREWRITING:' noRewriting= NUMBER )? ( 'EGDS:' egds= NUMBER )? ( 'OVERLAPS:' overlaps= NUMBER )? ( 'SKOLEMSFOREGDS:' skolemForEgds= NUMBER )? ( 'SKOLEMSTRINGS:' skolemStrings= NUMBER )? ( 'LOCALSKOLEMS:' localSkolems= NUMBER )? ( 'SORTSTRATEGY:' sortStrategy= NUMBER )? ( 'SKOLEMTABLESTRATEGY:' skolemTableStrategy= NUMBER )? )?
            {
            root_0 = (CommonTree)adaptor.nil();

            string_literal2=(Token)match(input,15,FOLLOW_15_in_mappingTask68); 
            string_literal2_tree = (CommonTree)adaptor.create(string_literal2);
            adaptor.addChild(root_0, string_literal2_tree);


            	 	   List<String> sourceSchemas = new ArrayList<String>();
            		   List<String> sourceInstances = new ArrayList<String>();
            		 
            // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:61:10: ( 'Source schema:' ssf= FILEPATH 'Source instance:' sif= FILEPATH )+
            int cnt1=0;
            loop1:
            do {
                int alt1=2;
                int LA1_0 = input.LA(1);

                if ( (LA1_0==16) ) {
                    alt1=1;
                }


                switch (alt1) {
            	case 1 :
            	    // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:61:11: 'Source schema:' ssf= FILEPATH 'Source instance:' sif= FILEPATH
            	    {
            	    string_literal3=(Token)match(input,16,FOLLOW_16_in_mappingTask99); 
            	    string_literal3_tree = (CommonTree)adaptor.create(string_literal3);
            	    adaptor.addChild(root_0, string_literal3_tree);

            	    ssf=(Token)match(input,FILEPATH,FOLLOW_FILEPATH_in_mappingTask103); 
            	    ssf_tree = (CommonTree)adaptor.create(ssf);
            	    adaptor.addChild(root_0, ssf_tree);

            	    sourceSchemas.add(ssf.getText());
            	    string_literal4=(Token)match(input,17,FOLLOW_17_in_mappingTask116); 
            	    string_literal4_tree = (CommonTree)adaptor.create(string_literal4);
            	    adaptor.addChild(root_0, string_literal4_tree);

            	    sif=(Token)match(input,FILEPATH,FOLLOW_FILEPATH_in_mappingTask120); 
            	    sif_tree = (CommonTree)adaptor.create(sif);
            	    adaptor.addChild(root_0, sif_tree);

            	    sourceInstances.add(sif.getText());

            	    }
            	    break;

            	default :
            	    if ( cnt1 >= 1 ) break loop1;
                        EarlyExitException eee =
                            new EarlyExitException(1, input);
                        throw eee;
                }
                cnt1++;
            } while (true);

            string_literal5=(Token)match(input,18,FOLLOW_18_in_mappingTask135); 
            string_literal5_tree = (CommonTree)adaptor.create(string_literal5);
            adaptor.addChild(root_0, string_literal5_tree);

            stf=(Token)match(input,FILEPATH,FOLLOW_FILEPATH_in_mappingTask139); 
            stf_tree = (CommonTree)adaptor.create(stf);
            adaptor.addChild(root_0, stf_tree);

             try {
                                generator.createMappingTask(sourceSchemas, sourceInstances, stf.getText());
                              } catch (Exception ex) {
                                  throw new ParserException(ex);
                              }
                            
            string_literal6=(Token)match(input,19,FOLLOW_19_in_mappingTask154); 
            string_literal6_tree = (CommonTree)adaptor.create(string_literal6);
            adaptor.addChild(root_0, string_literal6_tree);

            // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:70:35: ( sttgd )+
            int cnt2=0;
            loop2:
            do {
                int alt2=2;
                int LA2_0 = input.LA(1);

                if ( (LA2_0==IDENTIFIER) ) {
                    alt2=1;
                }


                switch (alt2) {
            	case 1 :
            	    // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:70:35: sttgd
            	    {
            	    pushFollow(FOLLOW_sttgd_in_mappingTask156);
            	    sttgd7=sttgd();

            	    state._fsp--;

            	    adaptor.addChild(root_0, sttgd7.getTree());

            	    }
            	    break;

            	default :
            	    if ( cnt2 >= 1 ) break loop2;
                        EarlyExitException eee =
                            new EarlyExitException(2, input);
                        throw eee;
                }
                cnt2++;
            } while (true);

            // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:71:10: ( 'TARGET TGDs:' ( ttgd )+ )?
            int alt4=2;
            int LA4_0 = input.LA(1);

            if ( (LA4_0==20) ) {
                alt4=1;
            }
            switch (alt4) {
                case 1 :
                    // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:71:11: 'TARGET TGDs:' ( ttgd )+
                    {
                    string_literal8=(Token)match(input,20,FOLLOW_20_in_mappingTask169); 
                    string_literal8_tree = (CommonTree)adaptor.create(string_literal8);
                    adaptor.addChild(root_0, string_literal8_tree);

                    // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:71:26: ( ttgd )+
                    int cnt3=0;
                    loop3:
                    do {
                        int alt3=2;
                        int LA3_0 = input.LA(1);

                        if ( (LA3_0==IDENTIFIER) ) {
                            alt3=1;
                        }


                        switch (alt3) {
                    	case 1 :
                    	    // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:71:26: ttgd
                    	    {
                    	    pushFollow(FOLLOW_ttgd_in_mappingTask171);
                    	    ttgd9=ttgd();

                    	    state._fsp--;

                    	    adaptor.addChild(root_0, ttgd9.getTree());

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt3 >= 1 ) break loop3;
                                EarlyExitException eee =
                                    new EarlyExitException(3, input);
                                throw eee;
                        }
                        cnt3++;
                    } while (true);


                    }
                    break;

            }

            // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:72:10: ( 'SOURCE FDs:' ( fd )+ )?
            int alt6=2;
            int LA6_0 = input.LA(1);

            if ( (LA6_0==21) ) {
                alt6=1;
            }
            switch (alt6) {
                case 1 :
                    // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:72:11: 'SOURCE FDs:' ( fd )+
                    {
                    string_literal10=(Token)match(input,21,FOLLOW_21_in_mappingTask186); 
                    string_literal10_tree = (CommonTree)adaptor.create(string_literal10);
                    adaptor.addChild(root_0, string_literal10_tree);

                     currentFDList = new ArrayList<ParserFD>(); 
                    // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:72:72: ( fd )+
                    int cnt5=0;
                    loop5:
                    do {
                        int alt5=2;
                        int LA5_0 = input.LA(1);

                        if ( (LA5_0==IDENTIFIER) ) {
                            alt5=1;
                        }


                        switch (alt5) {
                    	case 1 :
                    	    // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:72:72: fd
                    	    {
                    	    pushFollow(FOLLOW_fd_in_mappingTask190);
                    	    fd11=fd();

                    	    state._fsp--;

                    	    adaptor.addChild(root_0, fd11.getTree());

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt5 >= 1 ) break loop5;
                                EarlyExitException eee =
                                    new EarlyExitException(5, input);
                                throw eee;
                        }
                        cnt5++;
                    } while (true);

                     generator.setSourceFDs(currentFDList); 

                    }
                    break;

            }

            // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:73:10: ( 'TARGET FDs:' ( fd )+ )?
            int alt8=2;
            int LA8_0 = input.LA(1);

            if ( (LA8_0==22) ) {
                alt8=1;
            }
            switch (alt8) {
                case 1 :
                    // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:73:11: 'TARGET FDs:' ( fd )+
                    {
                    string_literal12=(Token)match(input,22,FOLLOW_22_in_mappingTask207); 
                    string_literal12_tree = (CommonTree)adaptor.create(string_literal12);
                    adaptor.addChild(root_0, string_literal12_tree);

                     currentFDList = new ArrayList<ParserFD>(); 
                    // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:73:72: ( fd )+
                    int cnt7=0;
                    loop7:
                    do {
                        int alt7=2;
                        int LA7_0 = input.LA(1);

                        if ( (LA7_0==IDENTIFIER) ) {
                            alt7=1;
                        }


                        switch (alt7) {
                    	case 1 :
                    	    // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:73:72: fd
                    	    {
                    	    pushFollow(FOLLOW_fd_in_mappingTask211);
                    	    fd13=fd();

                    	    state._fsp--;

                    	    adaptor.addChild(root_0, fd13.getTree());

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt7 >= 1 ) break loop7;
                                EarlyExitException eee =
                                    new EarlyExitException(7, input);
                                throw eee;
                        }
                        cnt7++;
                    } while (true);

                     generator.setTargetFDs(currentFDList); 

                    }
                    break;

            }

            // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:74:10: ( 'SOURCE INSTANCE:' ( fact )+ )*
            loop10:
            do {
                int alt10=2;
                int LA10_0 = input.LA(1);

                if ( (LA10_0==23) ) {
                    alt10=1;
                }


                switch (alt10) {
            	case 1 :
            	    // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:74:11: 'SOURCE INSTANCE:' ( fact )+
            	    {
            	    string_literal14=(Token)match(input,23,FOLLOW_23_in_mappingTask228); 
            	    string_literal14_tree = (CommonTree)adaptor.create(string_literal14);
            	    adaptor.addChild(root_0, string_literal14_tree);

            	     currentInstance = new ParserInstance(); 
            	    // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:74:74: ( fact )+
            	    int cnt9=0;
            	    loop9:
            	    do {
            	        int alt9=2;
            	        int LA9_0 = input.LA(1);

            	        if ( (LA9_0==IDENTIFIER) ) {
            	            alt9=1;
            	        }


            	        switch (alt9) {
            	    	case 1 :
            	    	    // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:74:74: fact
            	    	    {
            	    	    pushFollow(FOLLOW_fact_in_mappingTask232);
            	    	    fact15=fact();

            	    	    state._fsp--;

            	    	    adaptor.addChild(root_0, fact15.getTree());

            	    	    }
            	    	    break;

            	    	default :
            	    	    if ( cnt9 >= 1 ) break loop9;
            	                EarlyExitException eee =
            	                    new EarlyExitException(9, input);
            	                throw eee;
            	        }
            	        cnt9++;
            	    } while (true);

            	     generator.addSourceInstance(currentInstance); 

            	    }
            	    break;

            	default :
            	    break loop10;
                }
            } while (true);

            // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:75:10: ( 'CONFIG:' ( 'SOURCENULLS:' sourceNulls= NUMBER )? ( 'SUBSUMPTIONS:' subsumptions= NUMBER )? ( 'COVERAGES:' coverages= NUMBER )? ( 'SELFJOINS:' selfJoins= NUMBER )? ( 'NOREWRITING:' noRewriting= NUMBER )? ( 'EGDS:' egds= NUMBER )? ( 'OVERLAPS:' overlaps= NUMBER )? ( 'SKOLEMSFOREGDS:' skolemForEgds= NUMBER )? ( 'SKOLEMSTRINGS:' skolemStrings= NUMBER )? ( 'LOCALSKOLEMS:' localSkolems= NUMBER )? ( 'SORTSTRATEGY:' sortStrategy= NUMBER )? ( 'SKOLEMTABLESTRATEGY:' skolemTableStrategy= NUMBER )? )?
            int alt23=2;
            int LA23_0 = input.LA(1);

            if ( (LA23_0==24) ) {
                alt23=1;
            }
            switch (alt23) {
                case 1 :
                    // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:75:11: 'CONFIG:' ( 'SOURCENULLS:' sourceNulls= NUMBER )? ( 'SUBSUMPTIONS:' subsumptions= NUMBER )? ( 'COVERAGES:' coverages= NUMBER )? ( 'SELFJOINS:' selfJoins= NUMBER )? ( 'NOREWRITING:' noRewriting= NUMBER )? ( 'EGDS:' egds= NUMBER )? ( 'OVERLAPS:' overlaps= NUMBER )? ( 'SKOLEMSFOREGDS:' skolemForEgds= NUMBER )? ( 'SKOLEMSTRINGS:' skolemStrings= NUMBER )? ( 'LOCALSKOLEMS:' localSkolems= NUMBER )? ( 'SORTSTRATEGY:' sortStrategy= NUMBER )? ( 'SKOLEMTABLESTRATEGY:' skolemTableStrategy= NUMBER )?
                    {
                    string_literal16=(Token)match(input,24,FOLLOW_24_in_mappingTask249); 
                    string_literal16_tree = (CommonTree)adaptor.create(string_literal16);
                    adaptor.addChild(root_0, string_literal16_tree);

                    // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:76:13: ( 'SOURCENULLS:' sourceNulls= NUMBER )?
                    int alt11=2;
                    int LA11_0 = input.LA(1);

                    if ( (LA11_0==25) ) {
                        alt11=1;
                    }
                    switch (alt11) {
                        case 1 :
                            // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:76:14: 'SOURCENULLS:' sourceNulls= NUMBER
                            {
                            string_literal17=(Token)match(input,25,FOLLOW_25_in_mappingTask264); 
                            string_literal17_tree = (CommonTree)adaptor.create(string_literal17);
                            adaptor.addChild(root_0, string_literal17_tree);

                            sourceNulls=(Token)match(input,NUMBER,FOLLOW_NUMBER_in_mappingTask268); 
                            sourceNulls_tree = (CommonTree)adaptor.create(sourceNulls);
                            adaptor.addChild(root_0, sourceNulls_tree);

                             generator.setSourceNulls(sourceNulls.getText()); 

                            }
                            break;

                    }

                    // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:77:13: ( 'SUBSUMPTIONS:' subsumptions= NUMBER )?
                    int alt12=2;
                    int LA12_0 = input.LA(1);

                    if ( (LA12_0==26) ) {
                        alt12=1;
                    }
                    switch (alt12) {
                        case 1 :
                            // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:77:14: 'SUBSUMPTIONS:' subsumptions= NUMBER
                            {
                            string_literal18=(Token)match(input,26,FOLLOW_26_in_mappingTask287); 
                            string_literal18_tree = (CommonTree)adaptor.create(string_literal18);
                            adaptor.addChild(root_0, string_literal18_tree);

                            subsumptions=(Token)match(input,NUMBER,FOLLOW_NUMBER_in_mappingTask291); 
                            subsumptions_tree = (CommonTree)adaptor.create(subsumptions);
                            adaptor.addChild(root_0, subsumptions_tree);

                             generator.setSubsumptions(subsumptions.getText()); 

                            }
                            break;

                    }

                    // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:78:13: ( 'COVERAGES:' coverages= NUMBER )?
                    int alt13=2;
                    int LA13_0 = input.LA(1);

                    if ( (LA13_0==27) ) {
                        alt13=1;
                    }
                    switch (alt13) {
                        case 1 :
                            // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:78:14: 'COVERAGES:' coverages= NUMBER
                            {
                            string_literal19=(Token)match(input,27,FOLLOW_27_in_mappingTask311); 
                            string_literal19_tree = (CommonTree)adaptor.create(string_literal19);
                            adaptor.addChild(root_0, string_literal19_tree);

                            coverages=(Token)match(input,NUMBER,FOLLOW_NUMBER_in_mappingTask315); 
                            coverages_tree = (CommonTree)adaptor.create(coverages);
                            adaptor.addChild(root_0, coverages_tree);

                             generator.setCoverages(coverages.getText()); 

                            }
                            break;

                    }

                    // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:79:13: ( 'SELFJOINS:' selfJoins= NUMBER )?
                    int alt14=2;
                    int LA14_0 = input.LA(1);

                    if ( (LA14_0==28) ) {
                        alt14=1;
                    }
                    switch (alt14) {
                        case 1 :
                            // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:79:14: 'SELFJOINS:' selfJoins= NUMBER
                            {
                            string_literal20=(Token)match(input,28,FOLLOW_28_in_mappingTask335); 
                            string_literal20_tree = (CommonTree)adaptor.create(string_literal20);
                            adaptor.addChild(root_0, string_literal20_tree);

                            selfJoins=(Token)match(input,NUMBER,FOLLOW_NUMBER_in_mappingTask339); 
                            selfJoins_tree = (CommonTree)adaptor.create(selfJoins);
                            adaptor.addChild(root_0, selfJoins_tree);

                             generator.setSelfJoins(selfJoins.getText()); 

                            }
                            break;

                    }

                    // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:80:13: ( 'NOREWRITING:' noRewriting= NUMBER )?
                    int alt15=2;
                    int LA15_0 = input.LA(1);

                    if ( (LA15_0==29) ) {
                        alt15=1;
                    }
                    switch (alt15) {
                        case 1 :
                            // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:80:14: 'NOREWRITING:' noRewriting= NUMBER
                            {
                            string_literal21=(Token)match(input,29,FOLLOW_29_in_mappingTask358); 
                            string_literal21_tree = (CommonTree)adaptor.create(string_literal21);
                            adaptor.addChild(root_0, string_literal21_tree);

                            noRewriting=(Token)match(input,NUMBER,FOLLOW_NUMBER_in_mappingTask362); 
                            noRewriting_tree = (CommonTree)adaptor.create(noRewriting);
                            adaptor.addChild(root_0, noRewriting_tree);

                             generator.setNoRewriting(noRewriting.getText()); 

                            }
                            break;

                    }

                    // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:81:13: ( 'EGDS:' egds= NUMBER )?
                    int alt16=2;
                    int LA16_0 = input.LA(1);

                    if ( (LA16_0==30) ) {
                        alt16=1;
                    }
                    switch (alt16) {
                        case 1 :
                            // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:81:14: 'EGDS:' egds= NUMBER
                            {
                            string_literal22=(Token)match(input,30,FOLLOW_30_in_mappingTask382); 
                            string_literal22_tree = (CommonTree)adaptor.create(string_literal22);
                            adaptor.addChild(root_0, string_literal22_tree);

                            egds=(Token)match(input,NUMBER,FOLLOW_NUMBER_in_mappingTask386); 
                            egds_tree = (CommonTree)adaptor.create(egds);
                            adaptor.addChild(root_0, egds_tree);

                             generator.setEgds(egds.getText()); 

                            }
                            break;

                    }

                    // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:82:13: ( 'OVERLAPS:' overlaps= NUMBER )?
                    int alt17=2;
                    int LA17_0 = input.LA(1);

                    if ( (LA17_0==31) ) {
                        alt17=1;
                    }
                    switch (alt17) {
                        case 1 :
                            // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:82:14: 'OVERLAPS:' overlaps= NUMBER
                            {
                            string_literal23=(Token)match(input,31,FOLLOW_31_in_mappingTask405); 
                            string_literal23_tree = (CommonTree)adaptor.create(string_literal23);
                            adaptor.addChild(root_0, string_literal23_tree);

                            overlaps=(Token)match(input,NUMBER,FOLLOW_NUMBER_in_mappingTask409); 
                            overlaps_tree = (CommonTree)adaptor.create(overlaps);
                            adaptor.addChild(root_0, overlaps_tree);

                             generator.setOverlaps(overlaps.getText()); 

                            }
                            break;

                    }

                    // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:83:13: ( 'SKOLEMSFOREGDS:' skolemForEgds= NUMBER )?
                    int alt18=2;
                    int LA18_0 = input.LA(1);

                    if ( (LA18_0==32) ) {
                        alt18=1;
                    }
                    switch (alt18) {
                        case 1 :
                            // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:83:14: 'SKOLEMSFOREGDS:' skolemForEgds= NUMBER
                            {
                            string_literal24=(Token)match(input,32,FOLLOW_32_in_mappingTask428); 
                            string_literal24_tree = (CommonTree)adaptor.create(string_literal24);
                            adaptor.addChild(root_0, string_literal24_tree);

                            skolemForEgds=(Token)match(input,NUMBER,FOLLOW_NUMBER_in_mappingTask432); 
                            skolemForEgds_tree = (CommonTree)adaptor.create(skolemForEgds);
                            adaptor.addChild(root_0, skolemForEgds_tree);

                             generator.setSkolemsForEgds(skolemForEgds.getText()); 

                            }
                            break;

                    }

                    // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:84:13: ( 'SKOLEMSTRINGS:' skolemStrings= NUMBER )?
                    int alt19=2;
                    int LA19_0 = input.LA(1);

                    if ( (LA19_0==33) ) {
                        alt19=1;
                    }
                    switch (alt19) {
                        case 1 :
                            // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:84:14: 'SKOLEMSTRINGS:' skolemStrings= NUMBER
                            {
                            string_literal25=(Token)match(input,33,FOLLOW_33_in_mappingTask451); 
                            string_literal25_tree = (CommonTree)adaptor.create(string_literal25);
                            adaptor.addChild(root_0, string_literal25_tree);

                            skolemStrings=(Token)match(input,NUMBER,FOLLOW_NUMBER_in_mappingTask455); 
                            skolemStrings_tree = (CommonTree)adaptor.create(skolemStrings);
                            adaptor.addChild(root_0, skolemStrings_tree);

                             generator.setSkolemStrings(skolemStrings.getText()); 

                            }
                            break;

                    }

                    // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:85:13: ( 'LOCALSKOLEMS:' localSkolems= NUMBER )?
                    int alt20=2;
                    int LA20_0 = input.LA(1);

                    if ( (LA20_0==34) ) {
                        alt20=1;
                    }
                    switch (alt20) {
                        case 1 :
                            // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:85:14: 'LOCALSKOLEMS:' localSkolems= NUMBER
                            {
                            string_literal26=(Token)match(input,34,FOLLOW_34_in_mappingTask474); 
                            string_literal26_tree = (CommonTree)adaptor.create(string_literal26);
                            adaptor.addChild(root_0, string_literal26_tree);

                            localSkolems=(Token)match(input,NUMBER,FOLLOW_NUMBER_in_mappingTask478); 
                            localSkolems_tree = (CommonTree)adaptor.create(localSkolems);
                            adaptor.addChild(root_0, localSkolems_tree);

                             generator.setLocalSkolems(localSkolems.getText()); 

                            }
                            break;

                    }

                    // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:86:13: ( 'SORTSTRATEGY:' sortStrategy= NUMBER )?
                    int alt21=2;
                    int LA21_0 = input.LA(1);

                    if ( (LA21_0==35) ) {
                        alt21=1;
                    }
                    switch (alt21) {
                        case 1 :
                            // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:86:14: 'SORTSTRATEGY:' sortStrategy= NUMBER
                            {
                            string_literal27=(Token)match(input,35,FOLLOW_35_in_mappingTask497); 
                            string_literal27_tree = (CommonTree)adaptor.create(string_literal27);
                            adaptor.addChild(root_0, string_literal27_tree);

                            sortStrategy=(Token)match(input,NUMBER,FOLLOW_NUMBER_in_mappingTask501); 
                            sortStrategy_tree = (CommonTree)adaptor.create(sortStrategy);
                            adaptor.addChild(root_0, sortStrategy_tree);

                             generator.setSortStrategy(sortStrategy.getText()); 

                            }
                            break;

                    }

                    // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:87:13: ( 'SKOLEMTABLESTRATEGY:' skolemTableStrategy= NUMBER )?
                    int alt22=2;
                    int LA22_0 = input.LA(1);

                    if ( (LA22_0==36) ) {
                        alt22=1;
                    }
                    switch (alt22) {
                        case 1 :
                            // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:87:14: 'SKOLEMTABLESTRATEGY:' skolemTableStrategy= NUMBER
                            {
                            string_literal28=(Token)match(input,36,FOLLOW_36_in_mappingTask520); 
                            string_literal28_tree = (CommonTree)adaptor.create(string_literal28);
                            adaptor.addChild(root_0, string_literal28_tree);

                            skolemTableStrategy=(Token)match(input,NUMBER,FOLLOW_NUMBER_in_mappingTask524); 
                            skolemTableStrategy_tree = (CommonTree)adaptor.create(skolemTableStrategy);
                            adaptor.addChild(root_0, skolemTableStrategy_tree);

                             generator.setSkolemTableStrategy(skolemTableStrategy.getText()); 

                            }
                            break;

                    }


                    }
                    break;

            }

             generator.processTGDs(); 

            }

            retval.stop = input.LT(-1);

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "mappingTask"

    public static class sttgd_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "sttgd"
    // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:91:1: sttgd : view ( negatedview )* '->' view '.' ;
    public final TGDMappingTaskParser.sttgd_return sttgd() throws RecognitionException {
        TGDMappingTaskParser.sttgd_return retval = new TGDMappingTaskParser.sttgd_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token string_literal31=null;
        Token char_literal33=null;
        TGDMappingTaskParser.view_return view29 = null;

        TGDMappingTaskParser.negatedview_return negatedview30 = null;

        TGDMappingTaskParser.view_return view32 = null;


        CommonTree string_literal31_tree=null;
        CommonTree char_literal33_tree=null;

        try {
            // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:91:6: ( view ( negatedview )* '->' view '.' )
            // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:91:10: view ( negatedview )* '->' view '.'
            {
            root_0 = (CommonTree)adaptor.nil();

             currentTGD = new ParserTGD(); currentView = new ParserView(); 
            pushFollow(FOLLOW_view_in_sttgd557);
            view29=view();

            state._fsp--;

            adaptor.addChild(root_0, view29.getTree());
            currentTGD.setSourceView(currentView.clone()); 
            currentViewList = new ArrayList<ParserView>();
            // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:94:17: ( negatedview )*
            loop24:
            do {
                int alt24=2;
                int LA24_0 = input.LA(1);

                if ( (LA24_0==40) ) {
                    alt24=1;
                }


                switch (alt24) {
            	case 1 :
            	    // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:94:19: negatedview
            	    {
            	    pushFollow(FOLLOW_negatedview_in_sttgd585);
            	    negatedview30=negatedview();

            	    state._fsp--;

            	    adaptor.addChild(root_0, negatedview30.getTree());
            	    currentTGD.addNegatedSourceView (currentView.clone()); currentViewList = new ArrayList<ParserView>(); 

            	    }
            	    break;

            	default :
            	    break loop24;
                }
            } while (true);

            string_literal31=(Token)match(input,37,FOLLOW_37_in_sttgd594); 
            string_literal31_tree = (CommonTree)adaptor.create(string_literal31);
            adaptor.addChild(root_0, string_literal31_tree);

            currentView = new ParserView();
            pushFollow(FOLLOW_view_in_sttgd598);
            view32=view();

            state._fsp--;

            adaptor.addChild(root_0, view32.getTree());
            char_literal33=(Token)match(input,38,FOLLOW_38_in_sttgd600); 
            char_literal33_tree = (CommonTree)adaptor.create(char_literal33);
            adaptor.addChild(root_0, char_literal33_tree);

             currentTGD.setTargetView(currentView.clone()); generator.addSTTGD(currentTGD); 

            }

            retval.stop = input.LT(-1);

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "sttgd"

    public static class ttgd_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "ttgd"
    // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:98:1: ttgd : atom '->' atom '.' ;
    public final TGDMappingTaskParser.ttgd_return ttgd() throws RecognitionException {
        TGDMappingTaskParser.ttgd_return retval = new TGDMappingTaskParser.ttgd_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token string_literal35=null;
        Token char_literal37=null;
        TGDMappingTaskParser.atom_return atom34 = null;

        TGDMappingTaskParser.atom_return atom36 = null;


        CommonTree string_literal35_tree=null;
        CommonTree char_literal37_tree=null;

        try {
            // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:98:5: ( atom '->' atom '.' )
            // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:98:9: atom '->' atom '.'
            {
            root_0 = (CommonTree)adaptor.nil();

             currentTGD = new ParserTGD(); currentView = new ParserView(); 
            pushFollow(FOLLOW_atom_in_ttgd617);
            atom34=atom();

            state._fsp--;

            adaptor.addChild(root_0, atom34.getTree());
            currentTGD.setSourceView(currentView.clone()); currentView = new ParserView(); 
            string_literal35=(Token)match(input,37,FOLLOW_37_in_ttgd623); 
            string_literal35_tree = (CommonTree)adaptor.create(string_literal35);
            adaptor.addChild(root_0, string_literal35_tree);

            pushFollow(FOLLOW_atom_in_ttgd625);
            atom36=atom();

            state._fsp--;

            adaptor.addChild(root_0, atom36.getTree());
            char_literal37=(Token)match(input,38,FOLLOW_38_in_ttgd627); 
            char_literal37_tree = (CommonTree)adaptor.create(char_literal37);
            adaptor.addChild(root_0, char_literal37_tree);

             currentTGD.setTargetView(currentView.clone()); generator.addTargetTGD(currentTGD); 

            }

            retval.stop = input.LT(-1);

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "ttgd"

    public static class view_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "view"
    // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:104:1: view : atom ( ',' ( atom | builtin ) )* ;
    public final TGDMappingTaskParser.view_return view() throws RecognitionException {
        TGDMappingTaskParser.view_return retval = new TGDMappingTaskParser.view_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token char_literal39=null;
        TGDMappingTaskParser.atom_return atom38 = null;

        TGDMappingTaskParser.atom_return atom40 = null;

        TGDMappingTaskParser.builtin_return builtin41 = null;


        CommonTree char_literal39_tree=null;

        try {
            // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:104:5: ( atom ( ',' ( atom | builtin ) )* )
            // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:104:15: atom ( ',' ( atom | builtin ) )*
            {
            root_0 = (CommonTree)adaptor.nil();

            pushFollow(FOLLOW_atom_in_view647);
            atom38=atom();

            state._fsp--;

            adaptor.addChild(root_0, atom38.getTree());
            // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:104:21: ( ',' ( atom | builtin ) )*
            loop26:
            do {
                int alt26=2;
                int LA26_0 = input.LA(1);

                if ( (LA26_0==39) ) {
                    alt26=1;
                }


                switch (alt26) {
            	case 1 :
            	    // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:104:22: ',' ( atom | builtin )
            	    {
            	    char_literal39=(Token)match(input,39,FOLLOW_39_in_view651); 
            	    char_literal39_tree = (CommonTree)adaptor.create(char_literal39);
            	    adaptor.addChild(root_0, char_literal39_tree);

            	    // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:104:26: ( atom | builtin )
            	    int alt25=2;
            	    int LA25_0 = input.LA(1);

            	    if ( (LA25_0==IDENTIFIER) ) {
            	        alt25=1;
            	    }
            	    else if ( (LA25_0==NUMBER||LA25_0==STRING||(LA25_0>=45 && LA25_0<=46)) ) {
            	        alt25=2;
            	    }
            	    else {
            	        NoViableAltException nvae =
            	            new NoViableAltException("", 25, 0, input);

            	        throw nvae;
            	    }
            	    switch (alt25) {
            	        case 1 :
            	            // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:104:27: atom
            	            {
            	            pushFollow(FOLLOW_atom_in_view654);
            	            atom40=atom();

            	            state._fsp--;

            	            adaptor.addChild(root_0, atom40.getTree());

            	            }
            	            break;
            	        case 2 :
            	            // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:104:34: builtin
            	            {
            	            pushFollow(FOLLOW_builtin_in_view658);
            	            builtin41=builtin();

            	            state._fsp--;

            	            adaptor.addChild(root_0, builtin41.getTree());

            	            }
            	            break;

            	    }


            	    }
            	    break;

            	default :
            	    break loop26;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "view"

    public static class negatedview_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "negatedview"
    // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:106:1: negatedview : 'and not exists' '(' ( view ( negatedview )* ( 'with' equalities )? ) ')' ;
    public final TGDMappingTaskParser.negatedview_return negatedview() throws RecognitionException {
        TGDMappingTaskParser.negatedview_return retval = new TGDMappingTaskParser.negatedview_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token string_literal42=null;
        Token char_literal43=null;
        Token string_literal46=null;
        Token char_literal48=null;
        TGDMappingTaskParser.view_return view44 = null;

        TGDMappingTaskParser.negatedview_return negatedview45 = null;

        TGDMappingTaskParser.equalities_return equalities47 = null;


        CommonTree string_literal42_tree=null;
        CommonTree char_literal43_tree=null;
        CommonTree string_literal46_tree=null;
        CommonTree char_literal48_tree=null;

        try {
            // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:106:12: ( 'and not exists' '(' ( view ( negatedview )* ( 'with' equalities )? ) ')' )
            // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:106:17: 'and not exists' '(' ( view ( negatedview )* ( 'with' equalities )? ) ')'
            {
            root_0 = (CommonTree)adaptor.nil();

            string_literal42=(Token)match(input,40,FOLLOW_40_in_negatedview672); 
            string_literal42_tree = (CommonTree)adaptor.create(string_literal42);
            adaptor.addChild(root_0, string_literal42_tree);

            char_literal43=(Token)match(input,41,FOLLOW_41_in_negatedview673); 
            char_literal43_tree = (CommonTree)adaptor.create(char_literal43);
            adaptor.addChild(root_0, char_literal43_tree);

             currentView = new ParserView(); currentViewList.add(currentView);
            // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:107:17: ( view ( negatedview )* ( 'with' equalities )? )
            // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:107:19: view ( negatedview )* ( 'with' equalities )?
            {
            pushFollow(FOLLOW_view_in_negatedview695);
            view44=view();

            state._fsp--;

            adaptor.addChild(root_0, view44.getTree());
            // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:108:19: ( negatedview )*
            loop27:
            do {
                int alt27=2;
                int LA27_0 = input.LA(1);

                if ( (LA27_0==40) ) {
                    alt27=1;
                }


                switch (alt27) {
            	case 1 :
            	    // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:108:21: negatedview
            	    {
            	    pushFollow(FOLLOW_negatedview_in_negatedview718);
            	    negatedview45=negatedview();

            	    state._fsp--;

            	    adaptor.addChild(root_0, negatedview45.getTree());
            	     
            	                          currentViewList.get(currentViewList.size() - 2).addSubView(currentView);
            	                          currentView = currentViewList.get(currentViewList.size() - 2);
            	                          currentViewList.remove(currentViewList.size() - 1);
            	                        

            	    }
            	    break;

            	default :
            	    break loop27;
                }
            } while (true);

            // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:115:17: ( 'with' equalities )?
            int alt28=2;
            int LA28_0 = input.LA(1);

            if ( (LA28_0==42) ) {
                alt28=1;
            }
            switch (alt28) {
                case 1 :
                    // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:115:18: 'with' equalities
                    {
                    string_literal46=(Token)match(input,42,FOLLOW_42_in_negatedview783); 
                    string_literal46_tree = (CommonTree)adaptor.create(string_literal46);
                    adaptor.addChild(root_0, string_literal46_tree);

                    pushFollow(FOLLOW_equalities_in_negatedview785);
                    equalities47=equalities();

                    state._fsp--;

                    adaptor.addChild(root_0, equalities47.getTree());

                    }
                    break;

            }


            }

            char_literal48=(Token)match(input,43,FOLLOW_43_in_negatedview790); 
            char_literal48_tree = (CommonTree)adaptor.create(char_literal48);
            adaptor.addChild(root_0, char_literal48_tree);


            }

            retval.stop = input.LT(-1);

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "negatedview"

    public static class equalities_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "equalities"
    // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:117:1: equalities : equality ( ',' equality )* ;
    public final TGDMappingTaskParser.equalities_return equalities() throws RecognitionException {
        TGDMappingTaskParser.equalities_return retval = new TGDMappingTaskParser.equalities_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token char_literal50=null;
        TGDMappingTaskParser.equality_return equality49 = null;

        TGDMappingTaskParser.equality_return equality51 = null;


        CommonTree char_literal50_tree=null;

        try {
            // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:117:11: ( equality ( ',' equality )* )
            // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:117:13: equality ( ',' equality )*
            {
            root_0 = (CommonTree)adaptor.nil();

            pushFollow(FOLLOW_equality_in_equalities798);
            equality49=equality();

            state._fsp--;

            adaptor.addChild(root_0, equality49.getTree());
            // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:117:22: ( ',' equality )*
            loop29:
            do {
                int alt29=2;
                int LA29_0 = input.LA(1);

                if ( (LA29_0==39) ) {
                    alt29=1;
                }


                switch (alt29) {
            	case 1 :
            	    // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:117:23: ',' equality
            	    {
            	    char_literal50=(Token)match(input,39,FOLLOW_39_in_equalities801); 
            	    char_literal50_tree = (CommonTree)adaptor.create(char_literal50);
            	    adaptor.addChild(root_0, char_literal50_tree);

            	    pushFollow(FOLLOW_equality_in_equalities804);
            	    equality51=equality();

            	    state._fsp--;

            	    adaptor.addChild(root_0, equality51.getTree());

            	    }
            	    break;

            	default :
            	    break loop29;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "equalities"

    public static class equality_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "equality"
    // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:119:1: equality : ( value '=' value ) ;
    public final TGDMappingTaskParser.equality_return equality() throws RecognitionException {
        TGDMappingTaskParser.equality_return retval = new TGDMappingTaskParser.equality_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token char_literal53=null;
        TGDMappingTaskParser.value_return value52 = null;

        TGDMappingTaskParser.value_return value54 = null;


        CommonTree char_literal53_tree=null;

        try {
            // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:119:9: ( ( value '=' value ) )
            // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:119:11: ( value '=' value )
            {
            root_0 = (CommonTree)adaptor.nil();

            currentEquality = new ParserEquality(); 
            // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:120:17: ( value '=' value )
            // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:120:19: value '=' value
            {
             currentAttribute = new ParserAttribute(""); 
            pushFollow(FOLLOW_value_in_equality837);
            value52=value();

            state._fsp--;

            adaptor.addChild(root_0, value52.getTree());
            currentEquality.setLeftAttribute(currentAttribute);
            char_literal53=(Token)match(input,44,FOLLOW_44_in_equality859); 
            char_literal53_tree = (CommonTree)adaptor.create(char_literal53);
            adaptor.addChild(root_0, char_literal53_tree);

             currentAttribute = new ParserAttribute(""); 
            pushFollow(FOLLOW_value_in_equality881);
            value54=value();

            state._fsp--;

            adaptor.addChild(root_0, value54.getTree());
            currentEquality.setRightAttribute(currentAttribute);

            }

            currentView.addEquality(currentEquality);

            }

            retval.stop = input.LT(-1);

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "equality"

    public static class atom_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "atom"
    // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:125:1: atom : name= IDENTIFIER '(' attribute ( ',' attribute )* ')' ;
    public final TGDMappingTaskParser.atom_return atom() throws RecognitionException {
        TGDMappingTaskParser.atom_return retval = new TGDMappingTaskParser.atom_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token name=null;
        Token char_literal55=null;
        Token char_literal57=null;
        Token char_literal59=null;
        TGDMappingTaskParser.attribute_return attribute56 = null;

        TGDMappingTaskParser.attribute_return attribute58 = null;


        CommonTree name_tree=null;
        CommonTree char_literal55_tree=null;
        CommonTree char_literal57_tree=null;
        CommonTree char_literal59_tree=null;

        try {
            // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:125:5: (name= IDENTIFIER '(' attribute ( ',' attribute )* ')' )
            // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:125:8: name= IDENTIFIER '(' attribute ( ',' attribute )* ')'
            {
            root_0 = (CommonTree)adaptor.nil();

            name=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_atom912); 
            name_tree = (CommonTree)adaptor.create(name);
            adaptor.addChild(root_0, name_tree);

             currentAtom = new ParserAtom(name.getText()); 
            char_literal55=(Token)match(input,41,FOLLOW_41_in_atom916); 
            char_literal55_tree = (CommonTree)adaptor.create(char_literal55);
            adaptor.addChild(root_0, char_literal55_tree);

            pushFollow(FOLLOW_attribute_in_atom918);
            attribute56=attribute();

            state._fsp--;

            adaptor.addChild(root_0, attribute56.getTree());
            // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:125:88: ( ',' attribute )*
            loop30:
            do {
                int alt30=2;
                int LA30_0 = input.LA(1);

                if ( (LA30_0==39) ) {
                    alt30=1;
                }


                switch (alt30) {
            	case 1 :
            	    // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:125:89: ',' attribute
            	    {
            	    char_literal57=(Token)match(input,39,FOLLOW_39_in_atom921); 
            	    char_literal57_tree = (CommonTree)adaptor.create(char_literal57);
            	    adaptor.addChild(root_0, char_literal57_tree);

            	    pushFollow(FOLLOW_attribute_in_atom923);
            	    attribute58=attribute();

            	    state._fsp--;

            	    adaptor.addChild(root_0, attribute58.getTree());

            	    }
            	    break;

            	default :
            	    break loop30;
                }
            } while (true);

            char_literal59=(Token)match(input,43,FOLLOW_43_in_atom927); 
            char_literal59_tree = (CommonTree)adaptor.create(char_literal59);
            adaptor.addChild(root_0, char_literal59_tree);

             currentView.addAtom(currentAtom); 

            }

            retval.stop = input.LT(-1);

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "atom"

    public static class builtin_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "builtin"
    // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:128:1: builtin : ( '_' func= IDENTIFIER '(' ( argument )+ ')' | argument oper= OPERATOR argument ) ;
    public final TGDMappingTaskParser.builtin_return builtin() throws RecognitionException {
        TGDMappingTaskParser.builtin_return retval = new TGDMappingTaskParser.builtin_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token func=null;
        Token oper=null;
        Token char_literal60=null;
        Token char_literal61=null;
        Token char_literal63=null;
        TGDMappingTaskParser.argument_return argument62 = null;

        TGDMappingTaskParser.argument_return argument64 = null;

        TGDMappingTaskParser.argument_return argument65 = null;


        CommonTree func_tree=null;
        CommonTree oper_tree=null;
        CommonTree char_literal60_tree=null;
        CommonTree char_literal61_tree=null;
        CommonTree char_literal63_tree=null;

        try {
            // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:128:9: ( ( '_' func= IDENTIFIER '(' ( argument )+ ')' | argument oper= OPERATOR argument ) )
            // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:128:11: ( '_' func= IDENTIFIER '(' ( argument )+ ')' | argument oper= OPERATOR argument )
            {
            root_0 = (CommonTree)adaptor.nil();

             currentArgumentList = new ArrayList<ParserArgument>(); 
            // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:129:17: ( '_' func= IDENTIFIER '(' ( argument )+ ')' | argument oper= OPERATOR argument )
            int alt32=2;
            int LA32_0 = input.LA(1);

            if ( (LA32_0==45) ) {
                alt32=1;
            }
            else if ( (LA32_0==NUMBER||LA32_0==STRING||LA32_0==46) ) {
                alt32=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 32, 0, input);

                throw nvae;
            }
            switch (alt32) {
                case 1 :
                    // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:129:18: '_' func= IDENTIFIER '(' ( argument )+ ')'
                    {
                    char_literal60=(Token)match(input,45,FOLLOW_45_in_builtin958); 
                    char_literal60_tree = (CommonTree)adaptor.create(char_literal60);
                    adaptor.addChild(root_0, char_literal60_tree);

                    func=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_builtin961); 
                    func_tree = (CommonTree)adaptor.create(func);
                    adaptor.addChild(root_0, func_tree);

                    char_literal61=(Token)match(input,41,FOLLOW_41_in_builtin963); 
                    char_literal61_tree = (CommonTree)adaptor.create(char_literal61);
                    adaptor.addChild(root_0, char_literal61_tree);

                    // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:129:41: ( argument )+
                    int cnt31=0;
                    loop31:
                    do {
                        int alt31=2;
                        int LA31_0 = input.LA(1);

                        if ( (LA31_0==NUMBER||LA31_0==STRING||LA31_0==46) ) {
                            alt31=1;
                        }


                        switch (alt31) {
                    	case 1 :
                    	    // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:129:41: argument
                    	    {
                    	    pushFollow(FOLLOW_argument_in_builtin965);
                    	    argument62=argument();

                    	    state._fsp--;

                    	    adaptor.addChild(root_0, argument62.getTree());

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt31 >= 1 ) break loop31;
                                EarlyExitException eee =
                                    new EarlyExitException(31, input);
                                throw eee;
                        }
                        cnt31++;
                    } while (true);

                    char_literal63=(Token)match(input,43,FOLLOW_43_in_builtin968); 
                    char_literal63_tree = (CommonTree)adaptor.create(char_literal63);
                    adaptor.addChild(root_0, char_literal63_tree);

                     currentFunction = new ParserBuiltinFunction(func.getText(), currentArgumentList); currentView.addFunction(currentFunction); 

                    }
                    break;
                case 2 :
                    // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:131:17: argument oper= OPERATOR argument
                    {
                     currentOperator = new ParserBuiltinOperator(); 
                    pushFollow(FOLLOW_argument_in_builtin1024);
                    argument64=argument();

                    state._fsp--;

                    adaptor.addChild(root_0, argument64.getTree());
                     currentOperator.setFirstArgument(currentArgumentList.get(0)); 
                    oper=(Token)match(input,OPERATOR,FOLLOW_OPERATOR_in_builtin1046); 
                    oper_tree = (CommonTree)adaptor.create(oper);
                    adaptor.addChild(root_0, oper_tree);

                     currentOperator.setOperator(oper.getText()); 
                    pushFollow(FOLLOW_argument_in_builtin1066);
                    argument65=argument();

                    state._fsp--;

                    adaptor.addChild(root_0, argument65.getTree());
                     currentOperator.setSecondArgument(currentArgumentList.get(1)); 
                     currentView.addOperator(currentOperator); 

                    }
                    break;

            }


            }

            retval.stop = input.LT(-1);

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "builtin"

    public static class argument_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "argument"
    // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:138:1: argument : ( '\\$' var= IDENTIFIER | constant= ( STRING | NUMBER ) ) ;
    public final TGDMappingTaskParser.argument_return argument() throws RecognitionException {
        TGDMappingTaskParser.argument_return retval = new TGDMappingTaskParser.argument_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token var=null;
        Token constant=null;
        Token char_literal66=null;

        CommonTree var_tree=null;
        CommonTree constant_tree=null;
        CommonTree char_literal66_tree=null;

        try {
            // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:138:9: ( ( '\\$' var= IDENTIFIER | constant= ( STRING | NUMBER ) ) )
            // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:138:11: ( '\\$' var= IDENTIFIER | constant= ( STRING | NUMBER ) )
            {
            root_0 = (CommonTree)adaptor.nil();

             currentArgument = new ParserArgument(); 
            // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:139:17: ( '\\$' var= IDENTIFIER | constant= ( STRING | NUMBER ) )
            int alt33=2;
            int LA33_0 = input.LA(1);

            if ( (LA33_0==46) ) {
                alt33=1;
            }
            else if ( (LA33_0==NUMBER||LA33_0==STRING) ) {
                alt33=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 33, 0, input);

                throw nvae;
            }
            switch (alt33) {
                case 1 :
                    // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:139:18: '\\$' var= IDENTIFIER
                    {
                    char_literal66=(Token)match(input,46,FOLLOW_46_in_argument1130); 
                    char_literal66_tree = (CommonTree)adaptor.create(char_literal66);
                    adaptor.addChild(root_0, char_literal66_tree);

                    var=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_argument1133); 
                    var_tree = (CommonTree)adaptor.create(var);
                    adaptor.addChild(root_0, var_tree);

                     currentArgument.setVariable(var.getText()); 

                    }
                    break;
                case 2 :
                    // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:140:17: constant= ( STRING | NUMBER )
                    {
                    constant=(Token)input.LT(1);
                    if ( input.LA(1)==NUMBER||input.LA(1)==STRING ) {
                        input.consume();
                        adaptor.addChild(root_0, (CommonTree)adaptor.create(constant));
                        state.errorRecovery=false;
                    }
                    else {
                        MismatchedSetException mse = new MismatchedSetException(null,input);
                        throw mse;
                    }

                     currentArgument.setValue(constant.getText()); 

                    }
                    break;

            }

            currentArgumentList.add(currentArgument); 

            }

            retval.stop = input.LT(-1);

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "argument"

    public static class attribute_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "attribute"
    // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:143:1: attribute : attr= IDENTIFIER ':' value ;
    public final TGDMappingTaskParser.attribute_return attribute() throws RecognitionException {
        TGDMappingTaskParser.attribute_return retval = new TGDMappingTaskParser.attribute_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token attr=null;
        Token char_literal67=null;
        TGDMappingTaskParser.value_return value68 = null;


        CommonTree attr_tree=null;
        CommonTree char_literal67_tree=null;

        try {
            // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:143:10: (attr= IDENTIFIER ':' value )
            // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:143:12: attr= IDENTIFIER ':' value
            {
            root_0 = (CommonTree)adaptor.nil();

            attr=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_attribute1194); 
            attr_tree = (CommonTree)adaptor.create(attr);
            adaptor.addChild(root_0, attr_tree);

            char_literal67=(Token)match(input,47,FOLLOW_47_in_attribute1196); 
            char_literal67_tree = (CommonTree)adaptor.create(char_literal67);
            adaptor.addChild(root_0, char_literal67_tree);

             currentAttribute = new ParserAttribute(attr.getText()); 
            pushFollow(FOLLOW_value_in_attribute1200);
            value68=value();

            state._fsp--;

            adaptor.addChild(root_0, value68.getTree());
             currentAtom.addAttribute(currentAttribute); 

            }

            retval.stop = input.LT(-1);

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "attribute"

    public static class value_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "value"
    // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:146:1: value : ( '\\$' var= IDENTIFIER | nullValue= NULL | constant= ( STRING | NUMBER ) | expression= EXPRESSION );
    public final TGDMappingTaskParser.value_return value() throws RecognitionException {
        TGDMappingTaskParser.value_return retval = new TGDMappingTaskParser.value_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token var=null;
        Token nullValue=null;
        Token constant=null;
        Token expression=null;
        Token char_literal69=null;

        CommonTree var_tree=null;
        CommonTree nullValue_tree=null;
        CommonTree constant_tree=null;
        CommonTree expression_tree=null;
        CommonTree char_literal69_tree=null;

        try {
            // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:146:7: ( '\\$' var= IDENTIFIER | nullValue= NULL | constant= ( STRING | NUMBER ) | expression= EXPRESSION )
            int alt34=4;
            switch ( input.LA(1) ) {
            case 46:
                {
                alt34=1;
                }
                break;
            case NULL:
                {
                alt34=2;
                }
                break;
            case NUMBER:
            case STRING:
                {
                alt34=3;
                }
                break;
            case EXPRESSION:
                {
                alt34=4;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 34, 0, input);

                throw nvae;
            }

            switch (alt34) {
                case 1 :
                    // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:146:9: '\\$' var= IDENTIFIER
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    char_literal69=(Token)match(input,46,FOLLOW_46_in_value1213); 
                    char_literal69_tree = (CommonTree)adaptor.create(char_literal69);
                    adaptor.addChild(root_0, char_literal69_tree);

                    var=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_value1216); 
                    var_tree = (CommonTree)adaptor.create(var);
                    adaptor.addChild(root_0, var_tree);

                     currentAttribute.setVariable(var.getText()); 

                    }
                    break;
                case 2 :
                    // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:147:17: nullValue= NULL
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    nullValue=(Token)match(input,NULL,FOLLOW_NULL_in_value1240); 
                    nullValue_tree = (CommonTree)adaptor.create(nullValue);
                    adaptor.addChild(root_0, nullValue_tree);

                     currentAttribute.setValue(nullValue.getText()); 

                    }
                    break;
                case 3 :
                    // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:148:17: constant= ( STRING | NUMBER )
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    constant=(Token)input.LT(1);
                    if ( input.LA(1)==NUMBER||input.LA(1)==STRING ) {
                        input.consume();
                        adaptor.addChild(root_0, (CommonTree)adaptor.create(constant));
                        state.errorRecovery=false;
                    }
                    else {
                        MismatchedSetException mse = new MismatchedSetException(null,input);
                        throw mse;
                    }

                     currentAttribute.setValue(constant.getText()); 

                    }
                    break;
                case 4 :
                    // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:149:17: expression= EXPRESSION
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    expression=(Token)match(input,EXPRESSION,FOLLOW_EXPRESSION_in_value1294); 
                    expression_tree = (CommonTree)adaptor.create(expression);
                    adaptor.addChild(root_0, expression_tree);

                     currentAttribute.setValue(new Expression(generator.clean(expression.getText()))); 

                    }
                    break;

            }
            retval.stop = input.LT(-1);

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "value"

    public static class fd_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "fd"
    // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:151:1: fd : set= IDENTIFIER ':' path ( ',' path )* '->' path ( ',' path )* ( '[pk]' )? ( '[key]' )? ;
    public final TGDMappingTaskParser.fd_return fd() throws RecognitionException {
        TGDMappingTaskParser.fd_return retval = new TGDMappingTaskParser.fd_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token set=null;
        Token char_literal70=null;
        Token char_literal72=null;
        Token string_literal74=null;
        Token char_literal76=null;
        Token string_literal78=null;
        Token string_literal79=null;
        TGDMappingTaskParser.path_return path71 = null;

        TGDMappingTaskParser.path_return path73 = null;

        TGDMappingTaskParser.path_return path75 = null;

        TGDMappingTaskParser.path_return path77 = null;


        CommonTree set_tree=null;
        CommonTree char_literal70_tree=null;
        CommonTree char_literal72_tree=null;
        CommonTree string_literal74_tree=null;
        CommonTree char_literal76_tree=null;
        CommonTree string_literal78_tree=null;
        CommonTree string_literal79_tree=null;

        try {
            // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:151:9: (set= IDENTIFIER ':' path ( ',' path )* '->' path ( ',' path )* ( '[pk]' )? ( '[key]' )? )
            // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:151:11: set= IDENTIFIER ':' path ( ',' path )* '->' path ( ',' path )* ( '[pk]' )? ( '[key]' )?
            {
            root_0 = (CommonTree)adaptor.nil();

            set=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_fd1311); 
            set_tree = (CommonTree)adaptor.create(set);
            adaptor.addChild(root_0, set_tree);

             currentFD = new ParserFD(set.getText()); 
            char_literal70=(Token)match(input,47,FOLLOW_47_in_fd1315); 
            char_literal70_tree = (CommonTree)adaptor.create(char_literal70);
            adaptor.addChild(root_0, char_literal70_tree);

             currentStringList = new ArrayList<String>(); 
            pushFollow(FOLLOW_path_in_fd1321);
            path71=path();

            state._fsp--;

            adaptor.addChild(root_0, path71.getTree());
            // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:152:57: ( ',' path )*
            loop35:
            do {
                int alt35=2;
                int LA35_0 = input.LA(1);

                if ( (LA35_0==39) ) {
                    alt35=1;
                }


                switch (alt35) {
            	case 1 :
            	    // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:152:58: ',' path
            	    {
            	    char_literal72=(Token)match(input,39,FOLLOW_39_in_fd1324); 
            	    char_literal72_tree = (CommonTree)adaptor.create(char_literal72);
            	    adaptor.addChild(root_0, char_literal72_tree);

            	    pushFollow(FOLLOW_path_in_fd1326);
            	    path73=path();

            	    state._fsp--;

            	    adaptor.addChild(root_0, path73.getTree());

            	    }
            	    break;

            	default :
            	    break loop35;
                }
            } while (true);

             currentFD.setLeftAttributes(currentStringList); 
            string_literal74=(Token)match(input,37,FOLLOW_37_in_fd1332); 
            string_literal74_tree = (CommonTree)adaptor.create(string_literal74);
            adaptor.addChild(root_0, string_literal74_tree);

             currentStringList = new ArrayList<String>(); 
            pushFollow(FOLLOW_path_in_fd1338);
            path75=path();

            state._fsp--;

            adaptor.addChild(root_0, path75.getTree());
            // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:153:57: ( ',' path )*
            loop36:
            do {
                int alt36=2;
                int LA36_0 = input.LA(1);

                if ( (LA36_0==39) ) {
                    alt36=1;
                }


                switch (alt36) {
            	case 1 :
            	    // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:153:58: ',' path
            	    {
            	    char_literal76=(Token)match(input,39,FOLLOW_39_in_fd1341); 
            	    char_literal76_tree = (CommonTree)adaptor.create(char_literal76);
            	    adaptor.addChild(root_0, char_literal76_tree);

            	    pushFollow(FOLLOW_path_in_fd1343);
            	    path77=path();

            	    state._fsp--;

            	    adaptor.addChild(root_0, path77.getTree());

            	    }
            	    break;

            	default :
            	    break loop36;
                }
            } while (true);

            // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:154:3: ( '[pk]' )?
            int alt37=2;
            int LA37_0 = input.LA(1);

            if ( (LA37_0==48) ) {
                alt37=1;
            }
            switch (alt37) {
                case 1 :
                    // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:154:4: '[pk]'
                    {
                    string_literal78=(Token)match(input,48,FOLLOW_48_in_fd1350); 
                    string_literal78_tree = (CommonTree)adaptor.create(string_literal78);
                    adaptor.addChild(root_0, string_literal78_tree);

                     currentFD.setPrimaryKey(true); 

                    }
                    break;

            }

            // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:155:3: ( '[key]' )?
            int alt38=2;
            int LA38_0 = input.LA(1);

            if ( (LA38_0==49) ) {
                alt38=1;
            }
            switch (alt38) {
                case 1 :
                    // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:155:4: '[key]'
                    {
                    string_literal79=(Token)match(input,49,FOLLOW_49_in_fd1360); 
                    string_literal79_tree = (CommonTree)adaptor.create(string_literal79);
                    adaptor.addChild(root_0, string_literal79_tree);

                     currentFD.setKey(true); 

                    }
                    break;

            }

             currentFD.setRightAttributes(currentStringList); currentFDList.add(currentFD); 

            }

            retval.stop = input.LT(-1);

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "fd"

    public static class path_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "path"
    // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:158:1: path : name= IDENTIFIER ;
    public final TGDMappingTaskParser.path_return path() throws RecognitionException {
        TGDMappingTaskParser.path_return retval = new TGDMappingTaskParser.path_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token name=null;

        CommonTree name_tree=null;

        try {
            // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:158:9: (name= IDENTIFIER )
            // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:158:11: name= IDENTIFIER
            {
            root_0 = (CommonTree)adaptor.nil();

            name=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_path1382); 
            name_tree = (CommonTree)adaptor.create(name);
            adaptor.addChild(root_0, name_tree);

             currentStringList.add(name.getText()); 

            }

            retval.stop = input.LT(-1);

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "path"

    public static class fact_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "fact"
    // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:160:1: fact : set= IDENTIFIER '(' attrValue ( ',' attrValue )* ')' ;
    public final TGDMappingTaskParser.fact_return fact() throws RecognitionException {
        TGDMappingTaskParser.fact_return retval = new TGDMappingTaskParser.fact_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token set=null;
        Token char_literal80=null;
        Token char_literal82=null;
        Token char_literal84=null;
        TGDMappingTaskParser.attrValue_return attrValue81 = null;

        TGDMappingTaskParser.attrValue_return attrValue83 = null;


        CommonTree set_tree=null;
        CommonTree char_literal80_tree=null;
        CommonTree char_literal82_tree=null;
        CommonTree char_literal84_tree=null;

        try {
            // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:160:9: (set= IDENTIFIER '(' attrValue ( ',' attrValue )* ')' )
            // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:160:11: set= IDENTIFIER '(' attrValue ( ',' attrValue )* ')'
            {
            root_0 = (CommonTree)adaptor.nil();

            set=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_fact1397); 
            set_tree = (CommonTree)adaptor.create(set);
            adaptor.addChild(root_0, set_tree);

             currentFact = new ParserFact(set.getText()); 
            char_literal80=(Token)match(input,41,FOLLOW_41_in_fact1401); 
            char_literal80_tree = (CommonTree)adaptor.create(char_literal80);
            adaptor.addChild(root_0, char_literal80_tree);

            pushFollow(FOLLOW_attrValue_in_fact1403);
            attrValue81=attrValue();

            state._fsp--;

            adaptor.addChild(root_0, attrValue81.getTree());
            // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:160:89: ( ',' attrValue )*
            loop39:
            do {
                int alt39=2;
                int LA39_0 = input.LA(1);

                if ( (LA39_0==39) ) {
                    alt39=1;
                }


                switch (alt39) {
            	case 1 :
            	    // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:160:90: ',' attrValue
            	    {
            	    char_literal82=(Token)match(input,39,FOLLOW_39_in_fact1406); 
            	    char_literal82_tree = (CommonTree)adaptor.create(char_literal82);
            	    adaptor.addChild(root_0, char_literal82_tree);

            	    pushFollow(FOLLOW_attrValue_in_fact1408);
            	    attrValue83=attrValue();

            	    state._fsp--;

            	    adaptor.addChild(root_0, attrValue83.getTree());

            	    }
            	    break;

            	default :
            	    break loop39;
                }
            } while (true);

            char_literal84=(Token)match(input,43,FOLLOW_43_in_fact1412); 
            char_literal84_tree = (CommonTree)adaptor.create(char_literal84);
            adaptor.addChild(root_0, char_literal84_tree);

             currentInstance.addFact(currentFact); 

            }

            retval.stop = input.LT(-1);

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "fact"

    public static class attrValue_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "attrValue"
    // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:162:1: attrValue : attr= IDENTIFIER ':' val= ( NULL | STRING | NUMBER ) ;
    public final TGDMappingTaskParser.attrValue_return attrValue() throws RecognitionException {
        TGDMappingTaskParser.attrValue_return retval = new TGDMappingTaskParser.attrValue_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token attr=null;
        Token val=null;
        Token char_literal85=null;

        CommonTree attr_tree=null;
        CommonTree val_tree=null;
        CommonTree char_literal85_tree=null;

        try {
            // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:162:10: (attr= IDENTIFIER ':' val= ( NULL | STRING | NUMBER ) )
            // D:\\Dropbox\\dati\\codice\\java\\spicyPlusPlus\\spicyEngine\\src\\it\\unibas\\spicy\\parser\\TGDMappingTask.g:162:12: attr= IDENTIFIER ':' val= ( NULL | STRING | NUMBER )
            {
            root_0 = (CommonTree)adaptor.nil();

            attr=(Token)match(input,IDENTIFIER,FOLLOW_IDENTIFIER_in_attrValue1423); 
            attr_tree = (CommonTree)adaptor.create(attr);
            adaptor.addChild(root_0, attr_tree);

            char_literal85=(Token)match(input,47,FOLLOW_47_in_attrValue1425); 
            char_literal85_tree = (CommonTree)adaptor.create(char_literal85);
            adaptor.addChild(root_0, char_literal85_tree);

            val=(Token)input.LT(1);
            if ( input.LA(1)==NUMBER||(input.LA(1)>=STRING && input.LA(1)<=NULL) ) {
                input.consume();
                adaptor.addChild(root_0, (CommonTree)adaptor.create(val));
                state.errorRecovery=false;
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                throw mse;
            }

             currentFact.addAttribute(new ParserAttribute(attr.getText(), val.getText()));  

            }

            retval.stop = input.LT(-1);

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "attrValue"

    // Delegated rules


 

    public static final BitSet FOLLOW_mappingTask_in_prog54 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_15_in_mappingTask68 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_16_in_mappingTask99 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_FILEPATH_in_mappingTask103 = new BitSet(new long[]{0x0000000000020000L});
    public static final BitSet FOLLOW_17_in_mappingTask116 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_FILEPATH_in_mappingTask120 = new BitSet(new long[]{0x0000000000050000L});
    public static final BitSet FOLLOW_18_in_mappingTask135 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_FILEPATH_in_mappingTask139 = new BitSet(new long[]{0x0000000000080000L});
    public static final BitSet FOLLOW_19_in_mappingTask154 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_sttgd_in_mappingTask156 = new BitSet(new long[]{0x0000000001F00042L});
    public static final BitSet FOLLOW_20_in_mappingTask169 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_ttgd_in_mappingTask171 = new BitSet(new long[]{0x0000000001E00042L});
    public static final BitSet FOLLOW_21_in_mappingTask186 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_fd_in_mappingTask190 = new BitSet(new long[]{0x0000000001C00042L});
    public static final BitSet FOLLOW_22_in_mappingTask207 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_fd_in_mappingTask211 = new BitSet(new long[]{0x0000000001800042L});
    public static final BitSet FOLLOW_23_in_mappingTask228 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_fact_in_mappingTask232 = new BitSet(new long[]{0x0000000001800042L});
    public static final BitSet FOLLOW_24_in_mappingTask249 = new BitSet(new long[]{0x0000001FFE000002L});
    public static final BitSet FOLLOW_25_in_mappingTask264 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_NUMBER_in_mappingTask268 = new BitSet(new long[]{0x0000001FFC000002L});
    public static final BitSet FOLLOW_26_in_mappingTask287 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_NUMBER_in_mappingTask291 = new BitSet(new long[]{0x0000001FF8000002L});
    public static final BitSet FOLLOW_27_in_mappingTask311 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_NUMBER_in_mappingTask315 = new BitSet(new long[]{0x0000001FF0000002L});
    public static final BitSet FOLLOW_28_in_mappingTask335 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_NUMBER_in_mappingTask339 = new BitSet(new long[]{0x0000001FE0000002L});
    public static final BitSet FOLLOW_29_in_mappingTask358 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_NUMBER_in_mappingTask362 = new BitSet(new long[]{0x0000001FC0000002L});
    public static final BitSet FOLLOW_30_in_mappingTask382 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_NUMBER_in_mappingTask386 = new BitSet(new long[]{0x0000001F80000002L});
    public static final BitSet FOLLOW_31_in_mappingTask405 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_NUMBER_in_mappingTask409 = new BitSet(new long[]{0x0000001F00000002L});
    public static final BitSet FOLLOW_32_in_mappingTask428 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_NUMBER_in_mappingTask432 = new BitSet(new long[]{0x0000001E00000002L});
    public static final BitSet FOLLOW_33_in_mappingTask451 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_NUMBER_in_mappingTask455 = new BitSet(new long[]{0x0000001C00000002L});
    public static final BitSet FOLLOW_34_in_mappingTask474 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_NUMBER_in_mappingTask478 = new BitSet(new long[]{0x0000001800000002L});
    public static final BitSet FOLLOW_35_in_mappingTask497 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_NUMBER_in_mappingTask501 = new BitSet(new long[]{0x0000001000000002L});
    public static final BitSet FOLLOW_36_in_mappingTask520 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_NUMBER_in_mappingTask524 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_view_in_sttgd557 = new BitSet(new long[]{0x0000012000000000L});
    public static final BitSet FOLLOW_negatedview_in_sttgd585 = new BitSet(new long[]{0x0000012000000000L});
    public static final BitSet FOLLOW_37_in_sttgd594 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_view_in_sttgd598 = new BitSet(new long[]{0x0000004000000000L});
    public static final BitSet FOLLOW_38_in_sttgd600 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_atom_in_ttgd617 = new BitSet(new long[]{0x0000002000000000L});
    public static final BitSet FOLLOW_37_in_ttgd623 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_atom_in_ttgd625 = new BitSet(new long[]{0x0000004000000000L});
    public static final BitSet FOLLOW_38_in_ttgd627 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_atom_in_view647 = new BitSet(new long[]{0x0000008000000002L});
    public static final BitSet FOLLOW_39_in_view651 = new BitSet(new long[]{0x0000600000000160L});
    public static final BitSet FOLLOW_atom_in_view654 = new BitSet(new long[]{0x0000008000000002L});
    public static final BitSet FOLLOW_builtin_in_view658 = new BitSet(new long[]{0x0000008000000002L});
    public static final BitSet FOLLOW_40_in_negatedview672 = new BitSet(new long[]{0x0000020000000000L});
    public static final BitSet FOLLOW_41_in_negatedview673 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_view_in_negatedview695 = new BitSet(new long[]{0x00000D0000000000L});
    public static final BitSet FOLLOW_negatedview_in_negatedview718 = new BitSet(new long[]{0x00000D0000000000L});
    public static final BitSet FOLLOW_42_in_negatedview783 = new BitSet(new long[]{0x0000400000000720L});
    public static final BitSet FOLLOW_equalities_in_negatedview785 = new BitSet(new long[]{0x0000080000000000L});
    public static final BitSet FOLLOW_43_in_negatedview790 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_equality_in_equalities798 = new BitSet(new long[]{0x0000008000000002L});
    public static final BitSet FOLLOW_39_in_equalities801 = new BitSet(new long[]{0x0000400000000720L});
    public static final BitSet FOLLOW_equality_in_equalities804 = new BitSet(new long[]{0x0000008000000002L});
    public static final BitSet FOLLOW_value_in_equality837 = new BitSet(new long[]{0x0000100000000000L});
    public static final BitSet FOLLOW_44_in_equality859 = new BitSet(new long[]{0x0000400000000720L});
    public static final BitSet FOLLOW_value_in_equality881 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IDENTIFIER_in_atom912 = new BitSet(new long[]{0x0000020000000000L});
    public static final BitSet FOLLOW_41_in_atom916 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_attribute_in_atom918 = new BitSet(new long[]{0x0000088000000000L});
    public static final BitSet FOLLOW_39_in_atom921 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_attribute_in_atom923 = new BitSet(new long[]{0x0000088000000000L});
    public static final BitSet FOLLOW_43_in_atom927 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_45_in_builtin958 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_IDENTIFIER_in_builtin961 = new BitSet(new long[]{0x0000020000000000L});
    public static final BitSet FOLLOW_41_in_builtin963 = new BitSet(new long[]{0x0000600000000160L});
    public static final BitSet FOLLOW_argument_in_builtin965 = new BitSet(new long[]{0x0000680000000160L});
    public static final BitSet FOLLOW_43_in_builtin968 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_argument_in_builtin1024 = new BitSet(new long[]{0x0000000000000080L});
    public static final BitSet FOLLOW_OPERATOR_in_builtin1046 = new BitSet(new long[]{0x0000600000000160L});
    public static final BitSet FOLLOW_argument_in_builtin1066 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_46_in_argument1130 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_IDENTIFIER_in_argument1133 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_argument1157 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IDENTIFIER_in_attribute1194 = new BitSet(new long[]{0x0000800000000000L});
    public static final BitSet FOLLOW_47_in_attribute1196 = new BitSet(new long[]{0x0000400000000720L});
    public static final BitSet FOLLOW_value_in_attribute1200 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_46_in_value1213 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_IDENTIFIER_in_value1216 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NULL_in_value1240 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_value1264 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_EXPRESSION_in_value1294 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IDENTIFIER_in_fd1311 = new BitSet(new long[]{0x0000800000000000L});
    public static final BitSet FOLLOW_47_in_fd1315 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_path_in_fd1321 = new BitSet(new long[]{0x000000A000000000L});
    public static final BitSet FOLLOW_39_in_fd1324 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_path_in_fd1326 = new BitSet(new long[]{0x000000A000000000L});
    public static final BitSet FOLLOW_37_in_fd1332 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_path_in_fd1338 = new BitSet(new long[]{0x0003008000000002L});
    public static final BitSet FOLLOW_39_in_fd1341 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_path_in_fd1343 = new BitSet(new long[]{0x0003008000000002L});
    public static final BitSet FOLLOW_48_in_fd1350 = new BitSet(new long[]{0x0002000000000002L});
    public static final BitSet FOLLOW_49_in_fd1360 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IDENTIFIER_in_path1382 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IDENTIFIER_in_fact1397 = new BitSet(new long[]{0x0000020000000000L});
    public static final BitSet FOLLOW_41_in_fact1401 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_attrValue_in_fact1403 = new BitSet(new long[]{0x0000088000000000L});
    public static final BitSet FOLLOW_39_in_fact1406 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_attrValue_in_fact1408 = new BitSet(new long[]{0x0000088000000000L});
    public static final BitSet FOLLOW_43_in_fact1412 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IDENTIFIER_in_attrValue1423 = new BitSet(new long[]{0x0000800000000000L});
    public static final BitSet FOLLOW_47_in_attrValue1425 = new BitSet(new long[]{0x0000000000000320L});
    public static final BitSet FOLLOW_set_in_attrValue1429 = new BitSet(new long[]{0x0000000000000002L});

}