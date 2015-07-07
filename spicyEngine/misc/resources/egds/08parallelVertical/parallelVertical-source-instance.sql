
CREATE TABLE "public"."employee"(
"ename" varchar(255) ,
"city" varchar(255) 
)  WITHOUT OIDS;

CREATE TABLE "public"."cityregion"(
"regionname" varchar(255) ,
"cityname" varchar(255) 
)  WITHOUT OIDS;


-- ----------------------------
-- Records 
-- ----------------------------

INSERT INTO "public"."employee" VALUES ('Mario', 'Rome');
INSERT INTO "public"."employee" VALUES ('Paolo', 'Rome');
INSERT INTO "public"."employee" VALUES ('Gianni', 'Potenza');
INSERT INTO "public"."employee" VALUES ('Anne', 'San Jose');
INSERT INTO "public"."employee" VALUES ('Bruno', 'Oxford');
INSERT INTO "public"."cityregion" VALUES ('Lazio', 'Rome');
INSERT INTO "public"."cityregion" VALUES ('Basilicata', 'Potenza');
INSERT INTO "public"."cityregion" VALUES ('Lazio', 'Latina');
INSERT INTO "public"."cityregion" VALUES ('Lazio', 'Rieti');

