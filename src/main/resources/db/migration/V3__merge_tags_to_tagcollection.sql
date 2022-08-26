alter table tag_collection_tags add column tag varchar(255);
update tag_collection_tags
  set tag = (select tag_name from tag where tag.id = tag_collection_tags.tags_id)
  where tag_collection_tags.tags_id is not null;
alter table tag_collection_tags drop constraint FKlgor9y9re8ymd8ujgqi5f5lf0;
alter table tag_collection_tags drop column tags_id;
drop table tag;
