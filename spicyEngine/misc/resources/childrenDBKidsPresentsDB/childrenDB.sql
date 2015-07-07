create table parents (
   parentId int primary key,
   parentName varchar(20) unique,
   affiliation varchar(20),
   salary float
);

create table children (
  childId int primary key,
  childName varchar(20),
  age int,
  motherRefId int references parents(parentId) deferrable initially immediate,
  motherRefName varchar(20),
  fatherRefId int references parents(parentId) deferrable initially immediate,
  fatherRefName varchar(20)
);

create table xmasBoxes (
   toy varchar(20),
   giveRef int references children(childId) deferrable initially immediate,
   receiveRef int references children(childId) deferrable initially immediate
);

create table phoneNumbers(
   type varchar(10),
   number char(8),
   parentRefid int references parents(parentId) deferrable initially immediate
);

create table sbps (
   location varchar(20),
   time varchar(10),
   childRef int references children(childId) deferrable initially immediate
);
