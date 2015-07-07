
CREATE TABLE "public"."employee"(
"ename" varchar(255) ,
"carmodel" varchar(255) 
)  WITHOUT OIDS;

CREATE TABLE "public"."driver"(
"dname" varchar(255) ,
"team" varchar(255) 
)  WITHOUT OIDS;


-- ----------------------------
-- Records 
-- ----------------------------

INSERT INTO "public"."employee" VALUES ('Mario', 'Bravo');
INSERT INTO "public"."employee" VALUES ('Paolo', 'Megane');
INSERT INTO "public"."employee" VALUES ('Gianni', 'Corsa');
INSERT INTO "public"."employee" VALUES ('Anne', 'Corsa');
INSERT INTO "public"."driver" VALUES ('Mario', 'Fiat');
INSERT INTO "public"."driver" VALUES ('John', 'Ferrari');
INSERT INTO "public"."driver" VALUES ('Gianni', 'Opel');
INSERT INTO "public"."driver" VALUES ('Luca', 'Fiat');

