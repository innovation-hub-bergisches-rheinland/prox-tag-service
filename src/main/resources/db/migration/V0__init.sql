create table tag (id uuid not null, tag_name varchar(40), primary key (id));
create table tag_collection (referenced_entity uuid not null, primary key (referenced_entity));
create table tag_collection_tag_collection (tag_collection_referenced_entity uuid not null, tag_collection_id uuid not null);
create table tag_counter (id uuid not null, count int not null, tag1_id uuid, tag2_id uuid, primary key (id));
alter table tag add constraint UKore9823upx0misnd9n1bo7krr unique (tag_name);
alter table tag_collection_tag_collection add constraint FKcxd7lcg4ho3fho4wd43u10h73 foreign key (tag_collection_id) references tag;
alter table tag_collection_tag_collection add constraint FKi0u4muvwnelx6uipa20bwj2j5 foreign key (tag_collection_referenced_entity) references tag_collection;
alter table tag_counter add constraint FKqgmikm02xicw0cnklgi87l9ec foreign key (tag1_id) references tag;
alter table tag_counter add constraint FKfnm1vcx113g2skmqy0jxh8hxg foreign key (tag2_id) references tag;
