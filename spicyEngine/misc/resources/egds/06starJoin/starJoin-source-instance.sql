
CREATE TABLE "public"."employee"(
"ssn" varchar(255) ,
"name" varchar(255) ,
"dept" varchar(255), 
"company" varchar(255), 
"research" varchar(255) 
)  WITHOUT OIDS;

-- ----------------------------
-- Records 
-- ----------------------------

INSERT INTO "public"."employee" VALUES ('1', 'Alice', 'DB', 'IBM', 'MM');
INSERT INTO "public"."employee" VALUES ('2', 'Bruno', 'IA', 'MS', 'IE');
INSERT INTO "public"."employee" VALUES ('3', 'John', 'DB', 'IBM', 'MM');
INSERT INTO "public"."employee" VALUES ('4', 'Luke', 'DB', 'IBM', 'AB');

