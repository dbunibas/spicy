![image](http://db.unibas.it/projects/spicy/images/logoSpicy.gif)
#Overview

The ability of modern information systems to exchange, transform and integrate data is nowadays considered a crucial requirement. A fundamental requirement for such data integration applications is that of manipulating mappings among data sources. Mappings, also called schema mappings, are executable transformations – say, SQL queries for relational data or XQuery scripts for XML – that specify how an instance of the source repository should be translated into an instance of the target repository.

++Spicy is a mapping system developed at the Università della Basilicata - Department of Mathematics and Computer Science for the generation and manipulation of schema mappings.

It is the first mapping system that brings together:

* sophisticated matchers for the inference of correspondences between different schemas [SEBD06, SIGMOD08, EDBT08];
* a set of expressive mapping generation primitives, which includes selection conditions, costants, etc;
* a mapping generation algorithm that generates both canonical and core solutions; [SIGMOD09, VLDB09]
* and a mapping verification module for scenarios where target instances are already available. [EDBT08, SIGMOD08]

++Spicy contributes towards the goal of integrating scalable solutions based on schema mapping concepts into practical data integration tasks.

**For a new and improved chase engine for data cleaning and schema mappings altogether please check the [LLunatic project](http://db.unibas.it/projects/llunatic/)**

#Screenshots
[![image](http://db.unibas.it/projects/spicy/screenshots/spicy1_th.jpg)](http://db.unibas.it/projects/spicy/screenshots/spicy1.png)
[![image](http://db.unibas.it/projects/spicy/screenshots/spicy2_th.jpg)](http://db.unibas.it/projects/spicy/screenshots/spicy2.png)
[![image](http://db.unibas.it/projects/spicy/screenshots/spicy3_th.jpg)](http://db.unibas.it/projects/spicy/screenshots/spicy3.png)
[![image](http://db.unibas.it/projects/spicy/screenshots/spicy4_th.jpg)](http://db.unibas.it/projects/spicy/screenshots/spicy4.png)
[![image](http://db.unibas.it/projects/spicy/screenshots/spicy6_th.jpg)](http://db.unibas.it/projects/spicy/screenshots/spicy6.png)
[![image](http://db.unibas.it/projects/spicy/screenshots/spicy7_th.jpg)](http://db.unibas.it/projects/spicy/screenshots/spicy7.png)

#Pubblications
* [CIKM12] G. Mecca, P. Papotti, S. Raunich, D. Santoro - What is the IQ of your Data Transformation System? In CIKM 2012 ([link](https://dl.dropboxusercontent.com/u/5049685/fp125-mecca.pdf))
* [IS12] G. Mecca, P. Papotti, S. Raunich - Core schema mappings: Scalable core computations in data exchange In Information Systems 2012, Vol. 37, Iss. 7 ([link](http://www.sciencedirect.com/science/article/pii/S0306437912000506))
* [IT12] G. Mecca, P. Papotti - Schema Mapping and Data Exchange Tools: Time for the Golden Age In Information Technology 2012, Vol. 54, No. 3 ([link](http://www.oldenbourg-link.com/doi/abs/10.1524/itit.2012.0670))
* [VLDB11] B. Marnette, G. Mecca, P. Papotti, S. Raunich, D. Santoro - ++Spicy: an OpenSource Tool for Second-Generation Schema Mapping and Data Exchange In VLDB Conference, 2011 ([link](http://www.vldb.org/pvldb/vol4/p1438-marnette.pdf))
* [VLDB10] B. Marnette, G. Mecca, P. Papotti - Scalable Data Exchange with Functional Dependencies. In VLDB Conference, 2010 ([link](http://www.comp.nus.edu.sg/~vldb2010/proceedings/files/papers/R09.pdf))
* [SIGMOD09] G. Mecca, P. Papotti, S. Raunich - Core Schema Mappings. In SIGMOD Conference, 2009 ([link](http://doi.acm.org/10.1145/1559845.1559914))
* [VLDB09] G. Mecca, P. Papotti, S. Raunich, M. Buoncristiano - Concise and Expressive Mappings with the +Spicy System. In VLDB Conference, 2009([link](http://www.vldb.org/pvldb/2/vldb09-1000.pdf))
* [EDBT08] A. Bonifati, G. Mecca, A. Pappalardo, S. Raunich, G. Summa - Schema Mapping Verification: the Spicy way. In EDBT Conference, 2008:85-96 ([link](http://doi.acm.org/10.1145/1353343.1353358))
* [SIGMOD08] A. Bonifati, G. Mecca, A. Pappalardo, S. Raunich, G. Summa - The Spicy System: Towards a Notion of Mapping Quality. In SIGMOD Conference, 2008 ([link](http://doi.acm.org/10.1145/1376616.1376757))



#People
* Gianni Mecca
* Paolo Papotti
* Salvatore Raunich
* Donatello Santoro
* Marcello Buoncristiano
* Gianvito Summa
* Bruno Marnette