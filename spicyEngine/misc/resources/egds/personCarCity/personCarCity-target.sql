create table person (
	id varchar(40) unique,
	name varchar(40) unique,
	carId varchar(50),
	age varchar(50),
	cityId varchar(50)
);

create table car (
	id varchar(50) unique,
	model varchar(50),
	plate varchar(50) unique,
	makeId varchar(50)
);

create table make (
	id varchar(50) unique,
	name varchar(50) unique
);

create table city (
	id varchar(50) unique,
	name varchar(50) unique,
	region varchar(50)
);
