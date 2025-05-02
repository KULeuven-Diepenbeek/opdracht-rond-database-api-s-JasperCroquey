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