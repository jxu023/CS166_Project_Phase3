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
delete from block/contact list
*/
DELETE FROM USER_LIST_CONTAINS
WHERE list_member = 'Arne'
AND list_id = 
(
    SELECT contact_list
    FROM USR
    WHERE login = 'Norma'
);

/*
add to contact/block list
    adding invalid username does not add to list
    find way to notify user in this event
*/

INSERT INTO USER_LIST_CONTAINS 
SELECT u1.contact_list, u2.login
FROM 
(
    SELECT *
    FROM USR
    WHERE login = 'Norma'
) u1, USR u2
WHERE u2.login = 'Cecil.Gaylord';

/*
delete acc
check if other records refer to acc preventing deletion. like for a chat.
ex: chat foreign key init_sender refers to deleted acc (bad situation)
use trigger perhaps to check for chats of init_sender = usr.login and prevent deletion
*/
DELETE FROM Usr
WHERE login = 'Norma';

/*
browse current chats
*/
SELECT chats.chat_id AS Current_Chats
FROM CHAT_LIST chats, USR u1
WHERE u1.login = 'Norma' AND chats.member = u1.login;

/*
add chat with initial members
*/
INSERT INTO CHAT
    VALUES (DEFAULT,'private','Norma');

INSERT INTO CHAT_LIST
/*
browse messages
10 at a time chronologically
*/
select * from table order by timestamp desc
