-- NOTE THAT 'Norma' has to be replaced with the current logged in user in the Java code
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
WHERE list.list_member = u1.login;

/*
browse block list
*/
SELECT u1.login AS Blocked_Contacts
FROM (
    SELECT con.list_member
    FROM USER_LIST_CONTAINS con, USR u
    WHERE u.login = 'Norma'
    AND u.block_list = con.list_id
) AS list, USR u1
WHERE list.list_member = u1.login;

/*
browse current chats
*/
SELECT chats.chat_id AS Current_Chats
FROM CHAT_LIST chats, USR u1
WHERE u1.login = 'Norma' AND chats.member = u1.login;



