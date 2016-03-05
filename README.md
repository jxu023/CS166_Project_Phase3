# CS166_Project_Phase3

BEFORE WORKING ON FEATURE:

git pull

refer to queries.sql 

test query using foo.sql and foo.sh

add to queries.sql

add method to Messenger.java

add method call to user interface

test with compile.sh

AFTER WORKING ON FEATURE:

git pull

git add .

git commit -m "complete FEATURE description goes here"

git push

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
 
implement block list functionality (use triggers or integrity constraints)

indexes.sql

GUI

triggers

