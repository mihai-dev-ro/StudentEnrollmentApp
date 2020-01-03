-- Universities DB

-- !Ups

CREATE TABLE Security_users (
    Id INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,
    Email VARCHAR(255) NOT NULL,
    Password VARCHAR(255) NOT NULL,
    Created_at TIMESTAMP WITH TIMEZONE NOT NULL,
    Updated_at TIMESTAMP WITH TIMEZONE NOT NULL,
CONSTRAINT Security_user_email_unique UNIQUE (Email)
);

CREATE TABLE Students (
  Id INT(11) NOT NULL AUTO_INCREMENT PRIMARY KEY,
  Security_user_id INT(11) NOT NULL,
  Email VARCHAR(255) NOT NULL,
  Name VARCHAR(255),
  Education VARCHAR(1024),
  Created_at TIMESTAMP WITH TIMEZONE NOT NULL,
  Updated_at TIMESTAMP WITH TIMEZONE NOT NULL,
  CONSTRAINT User_email_unique UNIQUE (Email),
  CONSTRAINT User_security_user_id UNIQUE (Security_user_id),
  FOREIGN KEY (Security_user_id) REFERENCES Security_users(Id)
);


CREATE TABLE Universities(
    Id INT(11) NOT NULL AUTO_INCREMENT PRIMARY KEY,
    Name VARCHAR(1000) NOT NULL,
    Country_code VARCHAR(2) NOT NULL,
    Country VARCHAR(100) NOT NULL,
    CONSTRAINT University_name_unique UNIQUE (Name)
);

CREATE TABLE University_DNS_domains(
    Id INT(11) NOT NULL AUTO_INCREMENT PRIMARY KEY,
    University_id INT(11) NOT NULL,
    Name VARCHAR(100),
    FOREIGN KEY (University_id) REFERENCES Universities(Id)
);

CREATE TABLE University_websites(
    Id INT(11) NOT NULL AUTO_INCREMENT PRIMARY KEY,
    University_id INT(11) NOT NULL,
    Url VARCHAR(1000),
    FOREIGN KEY (University_id) REFERENCES Universities(Id)
);

CREATE TABLE Student_submissions(
    Id INT(11) NOT NULL AUTO_INCREMENT PRIMARY KEY,
    Student_id INT(11) NOT NULL,
    University_id INT(11) NOT NULL,
    Essay_reason VARCHAR(MAX),
    Application_object_file_url VARCHAR(MAX),
    Created_at TIMESTAMP WITH TIMEZONE NOT NULL,
    Updated_at TIMESTAMP WITH TIMEZONE NOT NULL,
    FOREIGN KEY (Student_id) REFERENCES Students(Id),
    FOREIGN KEY (University_id) REFERENCES Universities(Id)
);

-- pwd: test1Password
INSERT INTO Security_users(email, password, created_at, updated_at)
VALUES('salut@studentenrollment.org', '$2a$10$pu4ppFwIPahNIcPI.unVWeRhCAqYEIvom6SE9J09G9leUZNPPdiA6', '2019-10-16 00:00:00', '2019-10-16 00:00:00');

INSERT INTO Students(security_user_id,email,name, education, created_at, updated_at)
VALUES(1,'salut@studentenrollment.org', NULL, NULL, '2019-10-16 00:00:00', '2019-10-16 00:00:00');

-- !Downs

DROP TABLE security_users;
DROP TABLE Students;
DROP TABLE University_DNS_domains;
DROP TABLE University_websites;
DROP TABLE Universities;
DROP TABLE Student_submissions