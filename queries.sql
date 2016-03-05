
/*
browse contact list
*/
SELECT u1.login AS Contacts, u1.status AS Status
FROM (
    SELECT con.list_member
    FROM USER_LIST_CONTAINS con, USR u
    WHERE u.login = 'Norma'
    AND u.contact_list = con.list_id
) AS list, USR u1
WHERE list.list_member = u1.login


