# Minesweeper

#### Game in the system console.



## Table of contents

#### * [General info](#general-info)
#### * [Technologies](#technologies)
#### * [Setup](#setup)
#### * [Status](#status)
#### * [Information](#information)


#
## General info

#### This project is simple application created for my own development.
#### Now it haven't GUI but It's developed towards the web/desktop application. 
#### For now It contains (anty) pattern singleton implemented for Session Factory.


#### The application is divided into layers: view, service and database, and intermediate layers such as DTO and DAO.

## Technologies

#### * Java 1.8
#### * Maven 2.4 
#### * Hibernate 5.4.11.Final
#### * Mysql 8.0.19
#### * Lombok 1.18.12</h4>


 
## Setup

#### To run this project, you must fill file "hibernate.cfg.xml" with right data.
#### In <property name="hibernate.connection.url"> Instead of "XXX" fill it with JDBC URL to your local database instance.
#### Similarly you must fill the hibernate.connection.username and hibernate.connection.password fill with the matching authorization data to you database.
#### Here you can see, how your DataBase should looks like.


```sh
CREATE TABLE field( 
	id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
	xfield int default null,
	yfield int default null,
 	statusfield varchar(10) default 'covered',
  	valuefield int default 0,
  	gameid int default null,
	foreign key (gameid) references games(id)
	) ENGINE=INNODB; 
```

#

```sh
CREATE TABLE games(
	id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
	gamename varchar(20) unique,
	statusgame varchar(10) default 'during'
	) ENGINE=INNODB; 
```


## Status

#### Constantly developed.



## Information

#### Licenes GNU General Public License v3.0
#### Contact: bajkowski.konrad@gmail.com