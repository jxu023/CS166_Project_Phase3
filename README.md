# CS166_Project_Phase3

refer to queries.sql 

TODO:

basic functionality

user account deletion - CASCADE DELETES ( should this delete all sent as well? or just delete usr from table and contact lists)

browse contact list / block list

add/delete users from contact/block list

browse list of chats

start new chat (select initial chat members) - private or group chat, chronological order - index

chat owner
- delete chat
- remove chat members
- add chat members

list chat messages
- last 10 messages, index by timestamp
- load earlier messages, batches of 10

chat member
- list chat messages ( load in batches of 10) index by timestamp
- create new message
- delete old message

chat message
- diplay author, creation date, text

indexes.sql
GUI
triggers
