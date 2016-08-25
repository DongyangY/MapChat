# MapChat
A multiple real-time geographical (Google Map) group chatting Android application.

## What Need To Be Fixed Later!!
* Using HashTable instead of List on server for online clients' sockets to improve efficiency and eliminate race condition.
* Using one port instead of two ports for each client. On server, one thread per client to read. On client, two threads for write and read separately. Using event trigger instead of polling from buffer!! Thus, no response from server for each command!!
* Note: server-bad is the original bad code, the code in server is not tested yet.

## Featrue
* Location based chat on [Google Map](https://www.google.com/maps) with alternative traditional chat history.
* Vibrative message to selected group members.
* Destination negotiation with real-time synchronized pins.
* Taking and sending location based photo to others.
* Simultaneous multiple groups chat.

## Demo
> YouTube

[https://youtu.be/mmHGFlwAyUE](https://youtu.be/mmHGFlwAyUE)

[![IMAGE ALT TEXT HERE](http://img.youtube.com/vi/mmHGFlwAyUE/0.jpg)]
(https://youtu.be/mmHGFlwAyUE)

## Distrubution
* ```app``` folder - Android code.
* ```server``` folder - multi-threading server code.
* ```database``` folder - MySQL database structure.

## Installation
> System requirement

* Installed Java environment
* Installed MySQL database

> MySQL

* Create a MySQL database and import the ```MapChat.sql``` structure in ```database``` folder.

> Android

* Available with [Android Studio](http://developer.android.com/sdk/index.html).

> Server

* Modify the database configure:

  ```.Java
  // MapChatServer.java
  public static final String DATABASE_NAME = "YOUR_DATABASE_NAME";
  public static final String USER_NAME = "YOUR_DATABASE_USER_NAME";
  public static final String PASSWORD = "YOUR_DATABASE_PASSWORD";
  public static final String URL = "jdbc:mysql://localhost:3306/";
  public static final String DRIVER = "com.mysql.jdbc.Driver";
  ```
  
* For Linux/Mac:

  ```
  cd server
  sh RUNME.sh
  ```
  
* For Windows:

  Use [Eclipse](https://eclipse.org/downloads/) or [NetBeans](https://netbeans.org).
  
## Limitation
  
Android phones and the server computer need to be in the same local network. You need to input the local ip address of server in the first activity in Android app.



