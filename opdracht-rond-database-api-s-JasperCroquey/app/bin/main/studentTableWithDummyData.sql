DROP TABLE IF EXISTS student_volgt_vak;
DROP TABLE IF EXISTS student;
DROP TABLE IF EXISTS vak;
DROP TABLE IF EXISTS opleiding;

CREATE TABLE opleiding(
    id INT NOT NULL PRIMARY KEY,
    opleidingsnaam VARCHAR(200) NOT NULL
);

CREATE TABLE student(
    studnr INT NOT NULL PRIMARY KEY,
    naam VARCHAR(200) NOT NULL,
    voornaam VARCHAR(200),
    goedbezig BOOLEAN,
    opleiding INT DEFAULT NULL,
    FOREIGN KEY (opleiding) REFERENCES opleiding(id) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE vak(
    vaknr INT NOT NULL PRIMARY KEY,
    vaknaam VARCHAR(200) NOT NULL
);


CREATE TABLE student_volgt_vak(
    id INT AUTO_INCREMENT PRIMARY KEY,
    student INT,
    vak INT,
    FOREIGN KEY (student) REFERENCES student(studnr) ON DELETE CASCADE ON UPDATE CASCADE,
	FOREIGN KEY (vak) REFERENCES vak(vaknr) ON DELETE CASCADE ON UPDATE CASCADE
);


INSERT INTO opleiding(id, opleidingsnaam) VALUES (1, 'IIW');

INSERT INTO student(studnr, naam, voornaam, goedbezig, opleiding) VALUES (123, 'Trekhaak', 'Jaak', 0, 1);
INSERT INTO student(studnr, naam, voornaam, goedbezig, opleiding) VALUES (456, 'Peeters', 'Jos', 0, 1);
INSERT INTO student(studnr, naam, voornaam, goedbezig, opleiding) VALUES (890, 'Dongmans', 'Ding', 1, NULL);

INSERT INTO vak(vaknr, vaknaam) VALUES (1, 'DAB');
INSERT INTO vak(vaknr, vaknaam) VALUES (2, 'SES');
INSERT INTO vak(vaknr, vaknaam) VALUES (3, 'FSWEB');

INSERT INTO student_volgt_vak(student, vak) VALUES (123, 1);
INSERT INTO student_volgt_vak(student, vak) VALUES (123, 2);
INSERT INTO student_volgt_vak(student, vak) VALUES (123, 3);
INSERT INTO student_volgt_vak(student, vak) VALUES (456, 1);
INSERT INTO student_volgt_vak(student, vak) VALUES (456, 2);
INSERT INTO student_volgt_vak(student, vak) VALUES (890, 1);