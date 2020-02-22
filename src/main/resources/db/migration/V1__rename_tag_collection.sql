alter table tag drop constraint UKore9823upx0misnd9n1bo7krr;
alter table tag_collection_tag_collection drop constraint FKcxd7lcg4ho3fho4wd43u10h73 ;
alter table tag_collection_tag_collection drop constraint FKi0u4muvwnelx6uipa20bwj2j5 ;
alter table tag_counter drop constraint FKqgmikm02xicw0cnklgi87l9ec ;
alter table tag_counter drop constraint FKfnm1vcx113g2skmqy0jxh8hxg ;

drop table tag_counter;
drop table tag_collection_tag_collection ;
drop table tag_collection ;
drop table tag ;

create table tag (id uuid not null, tag_name varchar(40), primary key (id));
create table tag_collection (referenced_entity uuid not null, primary key (referenced_entity));
create table tag_collection_tags (tag_collection_referenced_entity uuid not null, tags_id uuid not null);
create table tag_counter (id uuid not null, count int4 not null, tag1_id uuid, tag2_id uuid, primary key (id));
alter table tag add constraint UKore9823upx0misnd9n1bo7krr unique (tag_name);
alter table tag_collection_tags add constraint FKlgor9y9re8ymd8ujgqi5f5lf0 foreign key (tags_id) references tag;
alter table tag_collection_tags add constraint FKfx862qt7kf010k2ygdwb0jqww foreign key (tag_collection_referenced_entity) references tag_collection;
alter table tag_counter add constraint FKqgmikm02xicw0cnklgi87l9ec foreign key (tag1_id) references tag;
alter table tag_counter add constraint FKfnm1vcx113g2skmqy0jxh8hxg foreign key (tag2_id) references tag;
