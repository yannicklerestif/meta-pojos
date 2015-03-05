create table classes(id int, name varchar(500))
create table classes_relations(child_id int, parent_id int)
create table methods(id int, class_id int, name varchar(256), method_desc varchar(1000))