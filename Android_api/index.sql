create database android_api /** Creating Database **/
use android_api /** Selecting Database **/
create table users(
   uid int(11) primary key auto_increment,
   unique_id varchar(23) not null unique,
   name varchar(50) not null,
   email varchar(100) not null unique,
   encrypted_password varchar(80) not null,
   salt varchar(10) not null,
   created_at datetime,
   updated_at datetime null);
create table weight(
    weightid int(11) not null AUTO_INCREMENT,
    weight int(4) not null,
    weightgoal int(4) not null,
    uid int(11) not null,
    primary key (weightid),
    FOREIGN KEY (uid) REFERENCES users(uid));
create table initial_detials (
	det_id int(11) not null AUTO_INCREMENT,
	uid int(11) not null,
	stored varchar(3) not null
	primary key (det_id),
	foreign key (uid) references users(uid));
create table weight_progress (
	progressid int(11) not null AUTO_INCREMENT,
	daycounter int(20) not null,
	weight_new int(4) not null,
	weight_current int(1) not null,
	goal_weight int(4) not null,
	uid int(11) not null,
	PRIMARY KEY (progressid),
	foreign key (uid) references users(uid)
); /**weight_current = 0 means not current, weightcurrent = 1 means it is current. daycounter increments by 1 and used to orderby and return results **/
