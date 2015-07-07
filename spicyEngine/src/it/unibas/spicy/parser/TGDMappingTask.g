grammar TGDMappingTask;

options {
output=AST;
ASTLabelType=CommonTree; // type of $stat.tree ref etc...
}

@lexer::header {
package it.unibas.spicy.parser.output;
}

@header {
package it.unibas.spicy.parser.output;

import it.unibas.spicy.model.expressions.Expression;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import it.unibas.spicy.parser.operators.IParseMappingTask;
import it.unibas.spicy.parser.*;
}

@members {
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
}
@lexer::members {

public void emitErrorMessage(String msg) {
	throw new it.unibas.spicy.parser.ParserException(msg);
}
}

prog: mappingTask { if (logger.isDebugEnabled()) logger.debug($mappingTask.tree.toStringTree()); }  ;

mappingTask:    'Mapping task:'
                 {
	 	   List<String> sourceSchemas = new ArrayList<String>();
		   List<String> sourceInstances = new ArrayList<String>();
		 }
	        ('Source schema:' ssf=FILEPATH {sourceSchemas.add(ssf.getText());}
	        'Source instance:' sif=FILEPATH {sourceInstances.add(sif.getText());})+
	        'Target schema:' stf=FILEPATH
		{ try {
                    generator.createMappingTask(sourceSchemas, sourceInstances, stf.getText());
                  } catch (Exception ex) {
                      throw new ParserException(ex);
                  }
                }
	        'SOURCE TO TARGET TGDs:' sttgd+
	        ('TARGET TGDs:' ttgd+)?
	        ('SOURCE FDs:' { currentFDList = new ArrayList<ParserFD>(); } fd+ { generator.setSourceFDs(currentFDList); })?
	        ('TARGET FDs:' { currentFDList = new ArrayList<ParserFD>(); } fd+ { generator.setTargetFDs(currentFDList); })?
	        ('SOURCE INSTANCE:' { currentInstance = new ParserInstance(); } fact+ { generator.addSourceInstance(currentInstance); })*
	        ('CONFIG:'
	           ('SOURCENULLS:' sourceNulls=NUMBER { generator.setSourceNulls(sourceNulls.getText()); })?
	           ('SUBSUMPTIONS:' subsumptions=NUMBER { generator.setSubsumptions(subsumptions.getText()); } )?
	           ('COVERAGES:' coverages=NUMBER { generator.setCoverages(coverages.getText()); } )?
	           ('SELFJOINS:' selfJoins=NUMBER { generator.setSelfJoins(selfJoins.getText()); })?
	           ('NOREWRITING:' noRewriting=NUMBER { generator.setNoRewriting(noRewriting.getText()); } )?
	           ('EGDS:' egds=NUMBER { generator.setEgds(egds.getText()); })?
	           ('OVERLAPS:' overlaps=NUMBER { generator.setOverlaps(overlaps.getText()); })?
	           ('SKOLEMSFOREGDS:' skolemForEgds=NUMBER { generator.setSkolemsForEgds(skolemForEgds.getText()); })?
	           ('SKOLEMSTRINGS:' skolemStrings=NUMBER { generator.setSkolemStrings(skolemStrings.getText()); })?
	           ('LOCALSKOLEMS:' localSkolems=NUMBER { generator.setLocalSkolems(localSkolems.getText()); })?
	           ('SORTSTRATEGY:' sortStrategy=NUMBER { generator.setSortStrategy(sortStrategy.getText()); })?
	           ('SKOLEMTABLESTRATEGY:' skolemTableStrategy=NUMBER { generator.setSkolemTableStrategy(skolemTableStrategy.getText()); })?
	        )?
		{ generator.processTGDs(); };

sttgd:	 	{ currentTGD = new ParserTGD(); currentView = new ParserView(); }
		view {currentTGD.setSourceView(currentView.clone()); }
  		{currentViewList = new ArrayList<ParserView>();}
                ( negatedview  {currentTGD.addNegatedSourceView (currentView.clone()); currentViewList = new ArrayList<ParserView>(); })*
		'->' {currentView = new ParserView();} view '.'
		{ currentTGD.setTargetView(currentView.clone()); generator.addSTTGD(currentTGD); };

ttgd:	 	{ currentTGD = new ParserTGD(); currentView = new ParserView(); }
		atom {currentTGD.setSourceView(currentView.clone()); currentView = new ParserView(); }
		'->' atom '.'
		{ currentTGD.setTargetView(currentView.clone()); generator.addTargetTGD(currentTGD); };
//variables:	variable (',' variable)*;

view:	        atom  (',' (atom | builtin) )*;

negatedview:    'and not exists''(' { currentView = new ParserView(); currentViewList.add(currentView);}
                ( view 
                  ( negatedview 
                    { 
                      currentViewList.get(currentViewList.size() - 2).addSubView(currentView);
                      currentView = currentViewList.get(currentViewList.size() - 2);
                      currentViewList.remove(currentViewList.size() - 1);
                    } 
                  )* 
                ('with' equalities)?) ')' ;

equalities:	equality (','  equality )* ;

equality:	{currentEquality = new ParserEquality(); }
                ( { currentAttribute = new ParserAttribute(""); } value {currentEquality.setLeftAttribute(currentAttribute);}
                  '='
                  { currentAttribute = new ParserAttribute(""); } value {currentEquality.setRightAttribute(currentAttribute);})
                {currentView.addEquality(currentEquality);};

atom:		name=IDENTIFIER { currentAtom = new ParserAtom(name.getText()); } '(' attribute (',' attribute)* ')'
		{ currentView.addAtom(currentAtom); };

builtin	:	{ currentArgumentList = new ArrayList<ParserArgument>(); }
                ('_'func=IDENTIFIER '(' argument+ ')'
                { currentFunction = new ParserBuiltinFunction(func.getText(), currentArgumentList); currentView.addFunction(currentFunction); } |
                { currentOperator = new ParserBuiltinOperator(); }
                argument { currentOperator.setFirstArgument(currentArgumentList.get(0)); }
                oper=OPERATOR { currentOperator.setOperator(oper.getText()); }
                argument { currentOperator.setSecondArgument(currentArgumentList.get(1)); }
                { currentView.addOperator(currentOperator); }
                );

argument:	{ currentArgument = new ParserArgument(); }
                ('\$'var=IDENTIFIER { currentArgument.setVariable(var.getText()); } |
                constant=(STRING | NUMBER) { currentArgument.setValue(constant.getText()); }
                ) {currentArgumentList.add(currentArgument); };

attribute:	attr=IDENTIFIER ':' { currentAttribute = new ParserAttribute(attr.getText()); } value
		{ currentAtom.addAttribute(currentAttribute); } ;

value	:	'\$'var=IDENTIFIER { currentAttribute.setVariable(var.getText()); } |
                nullValue=NULL { currentAttribute.setValue(nullValue.getText()); } |
                constant=(STRING | NUMBER) { currentAttribute.setValue(constant.getText()); } |
                expression=EXPRESSION { currentAttribute.setValue(new Expression(generator.clean(expression.getText()))); };

fd      :	set=IDENTIFIER { currentFD = new ParserFD(set.getText()); } ':'
		{ currentStringList = new ArrayList<String>(); } path (',' path)* { currentFD.setLeftAttributes(currentStringList); } '->'
		{ currentStringList = new ArrayList<String>(); } path (',' path)*
		('[pk]' { currentFD.setPrimaryKey(true); }) ?
		('[key]' { currentFD.setKey(true); }) ?
		{ currentFD.setRightAttributes(currentStringList); currentFDList.add(currentFD); };

path    :	name=IDENTIFIER { currentStringList.add(name.getText()); };

fact    :	set=IDENTIFIER { currentFact = new ParserFact(set.getText()); } '(' attrValue (',' attrValue)* ')' { currentInstance.addFact(currentFact); };

attrValue:	attr=IDENTIFIER ':' val=(NULL | STRING | NUMBER) { currentFact.addAttribute(new ParserAttribute(attr.getText(), val.getText()));  };

OPERATOR:	'==' | '!=' | '>' | '<' | '>=' | '<=';

FILEPATH  :   	'generate' |
                'file:/' (LETTER | '..') (LETTER | DIGIT | '+' | '-' | '\\' | '/' | '..')* '.x' ('sd' | 'ml') |
                'chain:' (LETTER | '..') (LETTER | DIGIT | '+' | '-' | '\\' | '/' | '..')* ('.xml' | '.tgd');
IDENTIFIER  :   (LETTER) (LETTER | DIGIT | '_' | '.' )*;

STRING  :  	'"' (LETTER | DIGIT| '-' | '.' | ' ')+ '"';
NUMBER	: 	('-')? DIGIT+ ('.' DIGIT+)?;
NULL    :       '#NULL#';
fragment DIGIT	: '0'..'9' ;
fragment LETTER	: 'a'..'z'|'A'..'Z' ;
WHITESPACE : 	( '\t' | ' ' | '\r' | '\n'| '\u000C' )+ { skip(); } ;
LINE_COMMENT :  '//' ~( '\r' | '\n' )* { skip(); } ;
EXPRESSION:'{'(.)*'}';