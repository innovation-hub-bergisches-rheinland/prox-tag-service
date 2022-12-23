/* 
This files contains scripts used to migrate data from the services database to prox-backend database.
*/

/* tag_collection */

SELECT
    tc.referenced_entity as id,
    current_timestamp as created_at,
    current_timestamp as modified_at
    FROM tag_collection tc;

create temporary table temp_data as (SELECT tc.referenced_entity as tag_collection_id,
                                            t.id                 as tags_id,
                                            t.tag                as tag_name
                                     FROM tag_collection tc
                                              JOIN tag_collection_tags tct
                                                   on tc.referenced_entity = tct.tag_collection_referenced_entity
                                              JOIN (SELECT gen_random_uuid() as id,
                                                           t.tag             as tag
                                                    FROM tag_collection_tags t
                                                    GROUP BY tag) t ON tct.tag = t.tag);

alter table temp_data rename to temp_data_tags;

/* tag */
SELECT
    t.tags_id as id,
    t.tag_name as tag_name,
    current_timestamp as created_at,
    current_timestamp as modified_at
    FROM temp_data_tags t
GROUP BY t.tags_id, t.tag_name;

/* tag_collection_tags */

SELECT
    t.tag_collection_id as tag_collection_id,
    t.tags_id as tags_id
    FROM temp_data_tags t;



/* project_tags */
SELECT
    p.id as project_id,
    tct.tags_id as tags
FROM project p
    JOIN tag_collection tc ON tc.id = p.id
    JOIN tag_collection_tags tct on tc.id = tct.tag_collection_id

/* organization_tags */
SELECT
    o.id as organization_id,
    tct.tags_id as tags
FROM organization o
    JOIN tag_collection tc ON tc.id = o.id
    JOIN tag_collection_tags tct on tc.id = tct.tag_collection_id

/* lecturer_tags */
SELECT
    l.id as lecturer_id,
    tct.tags_id as tags
FROM lecturer l
    JOIN tag_collection tc ON tc.id = l.id
    JOIN tag_collection_tags tct on tc.id = tct.tag_collection_id

