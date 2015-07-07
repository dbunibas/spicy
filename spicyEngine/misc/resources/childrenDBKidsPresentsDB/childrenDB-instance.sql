insert into parents values (201, 'Anne', 'Safeway', 50000);
insert into parents values (202, 'Paul', 'IBM', 60000);
insert into parents values (203, 'Jill', 'Xerox', 65000);
insert into parents values (204, 'Mike', 'NASA', 45000);
insert into parents values (205, 'Sue', 'MGM', 56000);
insert into parents values (206, 'Joe', 'Cisco', 67000);

insert into children values (1, 'Kyle', 2, 201, 'Anne', 202, 'Paul');
insert into children values (2, 'Maya', 4, 203, 'Jill', 204, 'Mike');
insert into children values (3, 'Eric', 5, 205, 'Sue', 206, 'Joe');
insert into children values (4, 'Carmen', 10, 205, 'Sue', 206, 'Joe');
insert into children values (9, 'Ben', 6, null, null, null, null);

insert into xmasBoxes values ('Playstation', 1, 3);
insert into xmasBoxes values ('Gift', 2, 4);
insert into xmasBoxes values ('Pencils', 3, 2);
insert into xmasBoxes values ('Puppet', 4, 9);

insert into phoneNumbers values ('Home', '201-0001', 201);
insert into phoneNumbers values ('Work', '202-0001', 202);
insert into phoneNumbers values ('Cell', '202-0002', 202);
insert into phoneNumbers values ('Home', '203-0001', 203);
insert into phoneNumbers values ('Cell', '203-0002', 203);
insert into phoneNumbers values ('Work', '204-0001', 204);
insert into phoneNumbers values ('Work', '205-0001', 205);
insert into phoneNumbers values ('Work', '206-0001', 206);

insert into sbps values ('Location 2', 'MonWed', 2);
insert into sbps values ('Location 3', 'MonTueWed', 3);
insert into sbps values ('Location 4', 'TueFri', 4);
insert into sbps values ('Location 9', 'MonWedSat', 9);
