
CREATE TABLE "public"."employee"(
"ssn" varchar(255) ,
"name" varchar(255) ,
"company" varchar(255), 
"vid" varchar(255) 
)  WITHOUT OIDS;

CREATE TABLE "public"."vehicle"(
"vid" varchar(255) ,
"carplate" varchar(255)
)  WITHOUT OIDS;

-- ----------------------------
-- Records 
-- ----------------------------

INSERT INTO "public"."employee" VALUES ('1', 'Alice', 'PZ', 'v1');
INSERT INTO "public"."employee" VALUES ('2', 'Bruno', 'DIA', 'v2');
INSERT INTO "public"."employee" VALUES ('3', 'John', 'IBM', 'v3');

INSERT INTO "public"."vehicle" VALUES ('v1', 'RM11');
INSERT INTO "public"."vehicle" VALUES ('v2', 'RM22');
INSERT INTO "public"."vehicle" VALUES ('v3', 'RM33');