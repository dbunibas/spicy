create table kids (
   id int primary key,
   name varchar(20) unique,
   contactPhone varchar(20),
   affiliation varchar(20),
   busSchedule varchar(20),
   familyIncome float
);

create table presents (
	toy varchar(30),
	gives int references kids(id),
	receives int references kids(id)
);
