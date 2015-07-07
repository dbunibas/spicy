create table personCar1 (
	personName varchar(50),
	age varchar(50),
	carPlate varchar(50),
        PRIMARY KEY (personName)
);

create table personCar2 (
	personName varchar(50),
	carModel varchar(50),
        PRIMARY KEY (personName)
);

create table personCity (
	personName varchar(50),
	cityName varchar(50),
        PRIMARY KEY (personName)
);

create table carMake (
	carModel varchar(50),
	makeName varchar(50),
        PRIMARY KEY (carModel)
);

create table cityRegion (
	cityName varchar(50),
	region varchar(50),
        PRIMARY KEY (cityName)
);