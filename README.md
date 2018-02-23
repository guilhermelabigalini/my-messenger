# My Messenger

My Messenger is an Android Chat application, inspired in WhatsApp, it supports chat between users and groups. 

## Android Client

From the Android Client, users can register a new account, search for users, manage contacts and groups. It uses REST APIs (with [Spring Rest Template](http://projects.spring.io/spring-android/)) and WebSockets (with [Java-WebSocket](https://github.com/TooTallNate/Java-WebSocket)) to communicate with the Backend server.

All messages, contacts, groups, sessions are  stored in the Android Client within a SQLite lite database.

Several activities in the application allow users to chat, view profiles, view user details, create groups... Below are some screen shots! 

### Contact window
<img src="https://raw.githubusercontent.com/guilhermelabigalini/my-messenger/master/docs/imgs/Screenshot_1519304631.png" width="260px" height="461px"/>

### Chat inside a group 
<img src="https://raw.githubusercontent.com/guilhermelabigalini/my-messenger/master/docs/imgs/Screenshot_1519304766.png" width="260px" height="461px"/>

### Chat with an user
<img src="https://raw.githubusercontent.com/guilhermelabigalini/my-messenger/master/docs/imgs/Screenshot_1519304803.png" width="260px" height="461px"/>

### Create account 
<img src="https://raw.githubusercontent.com/guilhermelabigalini/my-messenger/master/docs/imgs/Screenshot_1519304879.png" width="260px" height="461px"/>

### Sing in
<img src="https://raw.githubusercontent.com/guilhermelabigalini/my-messenger/master/docs/imgs/Screenshot_1519304873.png" width="260px" height="461px"/>

### Create group 
<img src="https://raw.githubusercontent.com/guilhermelabigalini/my-messenger/master/docs/imgs/Screenshot_1519304751.png" width="260px" height="461px"/>

## Backend
The backend is developed in .NET Core, using MongoDB for storage (Users/Groups and Session) and RabbitMQ for messaging distribution. 

The Android Client connects to the backend using:

 - **REST APIs** for short transactions like sing in, create account, create group, send message.
 - **WebSockets** to monitor new messages, so as soon as the message is received by the server, its also sent to the Android Client near real-time.  

In **RabbitMQ** each user have its own **queue** and when a message is sent to the user, the Backend simply appends it to the queue. In case the client is not online, the messages is be stored in the queue.  But if the client is online, the **WebSockets** will transmit the message as soon as its added to the queue.

For the group, its represented in **RabbitMQ**  by an **Exchange**, so when a message is sent to a group, the **Exchange** takes care of distrubuting the message to the **queue** of all members in the grupo. 
