# --- !Ups

CREATE TABLE "IMAGEREF" (
  "IMGPATH"       VARCHAR NOT NULL PRIMARY KEY,
  "TNPATH"       VARCHAR NOT NULL,
  "UPLOADTIME" TIMESTAMP  NOT NULL,
  "YEAR"       INT     NOT NULL,
  "MONTH"      INT     NOT NULL,
  "DAY"        INT     NOT NULL,
  "HOUR"       INT     NOT NULL
);


CREATE TABLE FTP_USER (
  userid VARCHAR(64) NOT NULL PRIMARY KEY,
  userpassword VARCHAR(64),
  homedirectory VARCHAR(128) NOT NULL,
  enableflag BOOLEAN DEFAULT TRUE,
  writepermission BOOLEAN DEFAULT FALSE,
  idletime INT DEFAULT 0,
  uploadrate INT DEFAULT 0,
  downloadrate INT DEFAULT 0,
  maxloginnumber INT DEFAULT 0,
  maxloginperip INT DEFAULT 0
);

INSERT INTO "FTP_USER" VALUES ('test','test','/home/pbolle/temp/ftproot',TRUE,TRUE,0,0,0,0,0);
# --- !Downs

DROP TABLE "IMAGEREF";
