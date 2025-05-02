DROP TABLE IF EXISTS student_volgt_vak;
DROP TABLE IF EXISTS student;
DROP TABLE IF EXISTS vak;
DROP TABLE IF EXISTS opleiding;

CREATE TABLE opleiding(
    id INTEGER NOT NULL PRIMARY KEY,
    opleidingsnaam TEXT NOT NULL
);

CREATE TABLE student(
    studnr INTEGER NOT NULL PRIMARY KEY,
    naam TEXT NOT NULL,
    voornaam TEXT,
    goedbezig INTEGER,  -- SQLite uses 0/1 for boolean
    opleiding INTEGER DEFAULT NULL,
    FOREIGN KEY (opleiding) REFERENCES opleiding(id) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE vak(
    vaknr INTEGER NOT NULL PRIMARY KEY,
    vaknaam TEXT NOT NULL
);

CREATE TABLE student_volgt_vak(
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    student INTEGER,
    vak INTEGER,
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