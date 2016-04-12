CREATE TABLE Assignment(
  aid int PRIMARY KEY NOT NULL,
  total_points int,
  due_date date
);

CREATE TABLE Attempt(
    attempt_id int PRIMARY KEY NOT NULL,
    grade int
);

CREATE TABLE Class(
  cid int PRIMARY KEY NOT NULL,
  cname char(50) NOT NULL,
  section_num int NOT NULL,
  course_num int NOT NULL,
  start_date date,
  end_date date
);

CREATE TABLE Teacher(
  tid int PRIMARY KEY,
  tname char(50),
  tmail char(50)
);

CREATE TABLE Student(
  sid int PRIMARY KEY,
  sname char(50),
  smail char(50)
);




CREATE TABLE enrolls_in(
  sid int,
  FOREIGN KEY(sid) REFERENCES Student(sid) ON DELETE CASCADE,
  
);

CREATE TABLE teaches(
  tid int,
  cid int,
  FOREIGN KEY(tid) REFERENCES Teacher(tid),
  FOREIGN KEY(cid) REFERENCES Class(cid)
);

CREATE TABLE assigned_in(
  tid int,
  cid int,
  aid int,
  PRIMARY KEY(tid, cid, aid),
  FOREIGN KEY(tid) REFERENCES Teacher(tid),
  FOREIGN KEY(cid) REFERENCES Class(cid) ON DELETE CASCADE,
  FOREIGN KEY(aid) REFERENCES Assignment(aid) ON DELETE CASCADE
);

CREATE TABLE submits(
  attempt_id int,
  aid int,
  sid int,
  PRIMARY KEY(attempt_id, aid, sid),
  FOREIGN KEY(attempt_id) REFERENCES Attempt(attempt_id),
  FOREIGN KEY(aid) REFERENCES Assignment(aid) ON DELETE CASCADE,
  FOREIGN KEY(sid) REFERENCES Student(sid) ON DELETE CASCADE,
);
