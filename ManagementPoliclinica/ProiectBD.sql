DROP DATABASE IF EXISTS proiect_v1;
CREATE DATABASE proiect_v1;
USE proiect_v1;

CREATE TABLE autentificare (
    id_utilizator int PRIMARY KEY,
    nume VARCHAR(40) unique,
    parola VARCHAR(20) DEFAULT '12345'
);

CREATE TABLE utilizator (
    id_utilizator int PRIMARY KEY AUTO_INCREMENT,
    admin_lvl int DEFAULT 0,
    cnp VARCHAR(40) UNIQUE,
    nume VARCHAR(40),
    prenume VARCHAR(40),
    adresa VARCHAR(40),
    nr_telefon INT,
    email VARCHAR(40),
    cont_iban VARCHAR(50),
    nr_contract INT UNIQUE,
    data_angajarii DATE,
    functie int,
	salar int,
    nr_ore int
);

CREATE TABLE functii(
     id_functie int,
     nume_functie VARCHAR(40)
);

CREATE TABLE admin_lvl(
    id_admin int,
    tip_admin VARCHAR(40)
);

CREATE TABLE asistent(
    id_asistent INT,
    tip_asistent ENUM( 'generalist', 'laborator', 'radiologie'),
    grad ENUM('principal', 'secundar')
);

CREATE TABLE unitate_medicala(
    nume VARCHAR(40),
    adresa VARCHAR(80) PRIMARY KEY,
    servicii TEXT
);

CREATE TABLE orar(
    id_zi int,
    zi VARCHAR(40),
    ora_inceput TIME,
    ora_sfarsit TIME
);

CREATE TABLE medic(
	id_medic INT PRIMARY KEY,
    cod_parafa INT UNIQUE,
    id_specializare INT,
    grad ENUM('primar','specialist'),
    competente TEXT,
    titlu_stiintific TEXT,
    post_didactic TEXT,
    bonus DECIMAL(3, 2) DEFAULT 0.05 #am schimbat din int in decimal
);

CREATE TABLE specializare(
     id_specializare INT PRIMARY KEY,
     nume_specializare VARCHAR(40)
);

CREATE TABLE orar_special(
     id_orar_special INT primary key auto_increment,
     ora_inceput TIME,
     ora_sfarsit TIME,
     data_calendaristica DATE
);

CREATE TABLE concedii(
	 id_concediu INT PRIMARY KEY AUTO_INCREMENT,
     id_angajat INT,
     data_inceput DATE,
     data_sfarsit DATE
);

CREATE TABLE servicii_medicale(
     id_serviciu_medical int primary key,
     id_specializare int,
     nume VARCHAR(40),
     pret int,
     durata_min int,
     competente TEXT
);

CREATE TABLE programari(
      id_programari int primary key auto_increment,
      id_pacient int,
      id_medic int,
      id_serviciu_medical int,
	  data_programare date,
      ora_programare time,
      adresa VARCHAR(80)
);

CREATE TABLE bon_fiscal(
      id_programari int,
      total int
);

CREATE TABLE pacient(
      id_pacient int primary key auto_increment,
      nume varchar(40),
      prenume varchar(40),
      CNP_pacient VARCHAR(40) UNIQUE
);

CREATE TABLE raport(
     id_raport int primary key auto_increment,
     id_pacient int,
     id_medic int,
     id_asistent int,
     istoric text,
     simptome text,
     investigatii text,
     diagnostic text,
     recomandari text
);

ALTER TABLE autentificare
ADD CONSTRAINT fk_autentificare_utilizator
FOREIGN KEY (id_utilizator) REFERENCES utilizator(id_utilizator);

ALTER TABLE  asistent
ADD CONSTRAINT fk_asistent_utilizator
FOREIGN KEY (id_asistent) REFERENCES utilizator(id_utilizator);

ALTER TABLE medic
ADD CONSTRAINT fk_medic_utilizator
FOREIGN KEY (id_medic) REFERENCES utilizator(id_utilizator);

ALTER TABLE medic
ADD CONSTRAINT fk_medic_specializare
FOREIGN KEY ( id_specializare) REFERENCES specializare( id_specializare);

ALTER TABLE concedii
ADD CONSTRAINT fk_concedii_utilizator
FOREIGN KEY (id_angajat) REFERENCES utilizator(id_utilizator);

ALTER TABLE servicii_medicale
ADD CONSTRAINT fk_servicii_medicale_specializare
FOREIGN KEY (id_specializare) REFERENCES specializare(id_specializare);

ALTER TABLE bon_fiscal
ADD CONSTRAINT fk_bon_fiscal_programari
FOREIGN KEY (id_programari) REFERENCES programari(id_programari);

ALTER TABLE programari
ADD CONSTRAINT fk_programari_unitate_medicala
FOREIGN KEY (adresa) REFERENCES unitate_medicala(adresa);

ALTER TABLE programari
ADD CONSTRAINT fk_programari_pacient
FOREIGN KEY (id_pacient) REFERENCES pacient(id_pacient);

ALTER TABLE programari
ADD CONSTRAINT fk_programari_servicii_medicale
FOREIGN KEY (id_serviciu_medical) REFERENCES servicii_medicale(id_serviciu_medical);

ALTER TABLE raport 
ADD CONSTRAINT fk_raport_pacient
FOREIGN KEY (id_pacient) REFERENCES pacient(id_pacient);

ALTER TABLE raport 
ADD CONSTRAINT fk_raport_medic
FOREIGN KEY ( id_medic) REFERENCES medic( id_medic);

ALTER TABLE raport 
ADD CONSTRAINT fk_raport_asistent
FOREIGN KEY (id_asistent) REFERENCES asistent(id_asistent);

SET FOREIGN_KEY_CHECKS = 0;

INSERT INTO admin_lvl (id_admin, tip_admin)
VALUES (0, 'utilizator'),
       (1, 'admin'),
       (2, 'super_admin');
       
INSERT INTO functii(id_functie, nume_functie)
VALUES (1, 'inspector'),
       (2, 'expert_financiar'),
       (3, 'receptioner'),
       (4, 'asistent'),
       (5, 'medic');
       
INSERT INTO orar(id_zi, zi, ora_inceput, ora_sfarsit)
VALUES
(1, 'Luni','08:00','16:00'),
(2, 'Marti','08:00','16:00'),
(3, 'Miercuri','08:00','16:00'),
(4, 'Joi','08:00','16:00'),
(5, 'Vineri','08:00','16:00'),
(6, 'Sambata', '', ''),
(7, 'Duminica', '', '');


INSERT INTO orar_special (ora_inceput, ora_sfarsit, data_calendaristica)
VALUES
    ('08:00', '12:00', '2023-01-10'),
    ('09:30', '13:30', '2023-01-12'),
    ('10:45', '14:45', '2023-01-03'),
    ('08:40', '15:00', '2023-02-10'),
    ('10:15', '14:40', '2023-06-05');
    
INSERT INTO concedii (id_angajat, data_inceput, data_sfarsit)
VALUES
    (1, '2023-01-15', '2023-01-20'),
    (2, '2023-02-10', '2023-02-14'),
    (3, '2023-03-05', '2023-03-10'),
    (4, '2023-05-25', '2023-05-30'),
    (5, '2023-08-12', '2023-08-28'),
    (6, '2023-10-06', '2023-10-16'),
    (7, '2023-12-13', '2023-12-28');
    
INSERT INTO servicii_medicale (id_serviciu_medical, id_specializare, nume, pret, durata_min, competente)
VALUES
    (1, 1,'Consultație cardiologică', 150, 30, 'Diagnosticare și tratament afecțiuni cardiace'),
    (2, 2, 'Consultație dermatologică', 120, 20, 'Diagnosticare și tratament afecțiuni dermatologice'),
    (3, 3, 'Consultație endocrinologică', 180, 40, 'Diagnosticare și tratament afecțiuni endocrine'),
	(4, 4, 'Consultație ginecologică', 160, 35, 'Examinare ginecologică și tratament specific'),
    (5, 5, 'Consultație neurologică', 200, 45, 'Examinare neurologică și diagnosticare afecțiuni ale sistemului nervos'),
    (6, 6, 'Consultație ortopedică', 250, 60, 'Examinare ortopedică și tratament specific'),
    (7, 7, 'Consultatie pediatrica' , 120, 24, 'Monitorizare ECG pe termen lung');
    
INSERT INTO specializare (id_specializare, nume_specializare)
 VALUES
	(1, 'Cardiologie'),
    (2, 'Dermatologie'),
    (3, 'Endocrinologie'),
    (4, 'Ginecologie'),
    (5, 'Neurologie'),
    (6, 'Ortopedie'),
    (7, 'Pediatrie'),
    (8, 'Psihiatrie'),
    (9, 'Radiologie'),
    (10, 'Urologie');
    
INSERT INTO medic (id_medic, cod_parafa, id_specializare, grad, competente, titlu_stiintific, post_didactic)
VALUES
    (1, 12345, 1, 'primar', 'Cardiologie intervențională', 'Doctor în Științe Medicale', 'Profesor Universitar'),
    (2, 54321, 2, 'specialist', 'Dermatologie pediatrică', 'Doctor în Medicină', 'Conferențiar Universitar'),
    (3, 98765, 3, 'primar', 'Endocrinologie pediatrică', 'Doctor în Medicină', 'Lector Universitar'),
    (4, 13579, 4, 'specialist', 'Ginecologie', 'Doctor în Medicină', 'Conferențiar Universitar'),
    (5, 24680, 5, 'primar', 'Neurologie', 'Doctor în Științe Medicale', 'Profesor Asistent'),
    (6, 11223, 6, 'primar', 'Ortopedie', 'Doctor în Medicină', 'Profesor Universitar');
    
INSERT INTO programari (id_pacient, id_medic, id_serviciu_medical, data_programare, ora_programare, adresa)
VALUES
    (1, 1, 1, '2023-01-05', '11:30', 'Str. Centrala nr. 456'),
    (2, 2, 2, '2023-01-10', '14:00', 'Str. Estului nr. 202'),
    (3, 3, 3, '2023-01-15', '11:45', 'Str. Sudului nr. 101'),
	(4, 4, 4, '2023-02-01', '17:15', 'Str. Nordului nr. 789'),
    (5, 5, 5, '2023-02-08', '15:30', 'Str. Sudului nr. 101'),
    (6, 6, 6, '2023-02-15', '12:00', 'Str. Principala nr. 123'),
    (7, 7, 7, '2023-02-22', '13:45', 'Str. Estului nr. 202');
    
-- Populare tabel asistent
INSERT INTO asistent (id_asistent, tip_asistent, grad)
VALUES
    (7, 'generalist', 'secundar'),
    (8, 'radiologie', 'principal'),
    (9, 'laborator', 'principal'),
    (10, 'laborator', 'principal');
    
     
-- Inserare pentru 10 valori de exemplu în tabela utilizator cu date de angajare variate
INSERT INTO utilizator (id_utilizator, cnp, nume, prenume, adresa, nr_telefon, email, cont_iban, nr_contract, data_angajarii, functie, salar, nr_ore)
VALUES
    (1, '1234567890', 'Popescu', 'Ion', 'Str. Libertății nr. 1', 0711122334, 'popescu.ion@email.com', 'RO1234567890123456789012', 1, '2023-01-01', 5, 3000, 40),
    (2, '9876543210', 'Ionescu', 'Maria', 'Str. Victoriei nr. 2', 0722233445, 'ionescu.maria@email.com', 'RO9876543210987654321098', 2, '2022-02-15', 5, 2500, 35),
    (3, '1122334455', 'Dumitru', 'Ana', 'Str. Unirii nr. 3', 0733344556, 'dumitru.ana@email.com', 'RO1122334455112233445566', 3, '2022-03-10', 5, 5000, 50),
    (4, '6534332211', 'Georgescu', 'Vasile', 'Str. Independenței nr. 4', 0744455667, 'georgescu.vasile@email.com', 'RO5544332211554433221100', 4, '2019-04-05', 5, 7000, 45),
    (5, '9488776655', 'Popa', 'Cristina', 'Str. Frăției nr. 5', 0755566778, 'popa.cristina@email.com', 'RO9988776655998877665544', 5, '2020-05-20', 5, 4000, 30),
    (6, '1124334455', 'Iordache', 'Mihai', 'Str. Republicii nr. 6', 0766677889, 'iordache.mihai@email.com', 'RO1122334455112233445577', 6, '2023-06-12', 5, 6000, 38),
    (7, '5544332211', 'Popovici', 'Elena', 'Str. Industriei nr. 7', 0777788990, 'popovici.elena@email.com', 'RO5544332211554433221188', 7, '2022-07-18', 4, 7500, 42),
    (8, '9988776655', 'Stanciu', 'Andrei', 'Str. Culturii nr. 8',0788899001, 'stanciu.andrei@email.com', 'RO9988776655998877665566', 8, '2020-08-25', 4, 3200, 28),
    (9, '1922334455', 'Radu', 'Simona', 'Str. Sportului nr. 9', 0799900112, 'radu.simona@email.com', 'RO1122334455112233445599', 9, '2018-09-30', 4, 4200, 37),
    (10, '6544332211', 'Munteanu', 'Radu', 'Str. Naturii nr. 10',0712345678, 'munteanu.radu@email.com', 'RO5544332211554433221122', 10, '2022-10-14', 4, 5500, 48);

INSERT INTO autentificare(id_utilizator,nume,parola)
 VALUES
     (1,'PopescuIon', '12345'),
	 (2,'IonescuMaria', '12345'),
     (3,'DumitruAna', '12345'),
     (4,'GeorgescuVasile', '12345'),
     (5,'PopaCristina', '12345'),
     (6,'IordacheMihai', '12345'),
	 (7,'PopoviciElena', '12345'),
     (8,'StanciuAndrei', '12345'),
     (9, 'RaduSimona', '12345'),
	 (10 ,'MunteanuRadu', '12345');
     
INSERT INTO unitate_medicala (nume, adresa, servicii)
VALUES
('Clinica Medicala XYZ', 'Str. Principala nr. 123', 'Ecografie, Endoscopie digestivă, Ecocardiografie, Cardiologie intervențională, Bronhoscopie, EEG, EMG'),
('Spitalul Alpha', 'Str. Centrala nr. 456', 'Chirurgie laparoscopică, Chirurgie toracică, Chirurgie spinală, Litotriție extracorporeală, Explorare computer tomograf'),
('Centrul Medical Beta', 'Str. Nordului nr. 789', 'Dializă, Explorare computer tomograf, Imagistică prin rezonanță magnetică'),
('Cabinetul Medical Gama', 'Str. Sudului nr. 101', 'Ecocardiografie, Cardiologie intervențională, Litotriție extracorporeală, Bronhoscopie, EEG'),
('Spitalul Omega', 'Str. Estului nr. 202', 'Chirurgie spinală, Litotriție extracorporeală, Endoscopie digestivă, Cardiologie intervențională, Explorare computer tomograf');

INSERT INTO pacient (id_pacient, nume, prenume, CNP_pacient)
VALUES
    (1, 'Popescu', 'Ana', '1234567890123'),
    (2, 'Ionescu', 'Mihai', '9876543210456'),
    (3, 'Dumitru', 'Elena', '1122334455667'),
    (4, 'Georgescu', 'Cristian', '6544332211890'),
    (5, 'Popa', 'Iulia', '9488776655111'),
    (6, 'Iordache', 'Radu', '1124334455778'),
    (7, 'Popovici', 'Andreea', '5544332211889'),
    (8, 'Stanciu', 'Paul', '9988776655444'),
    (9, 'Radu', 'Simona', '1922334455111'),
    (10, 'Munteanu', 'Marius', '6544332211223');

INSERT INTO raport (id_raport, id_pacient, id_medic, id_asistent, istoric, simptome, investigatii, diagnostic, recomandari)
VALUES
    (1, 1, 1, 7, 'Pacientul a prezentat simptome de dispnee și palpitatii.', 'Dispnee, palpitatii', 'ECG, Ecocardiografie', 'Tahicardie supraventriculara', 'Administrare medicament antiaritmic'),
    (2, 2, 2, 7, 'Pacientul a prezentat erupții cutanate și mâncărimi.', 'Erupții cutanate, mâncărimi', 'Examinare dermatologică', 'Dermatită alergică', 'Tratament topic cu cremă antiinflamatoare'),
    (3, 3, 3, 8, 'Pacientul a prezentat o creștere a glicemiei și oboseală persistentă.', 'Creștere glicemie, oboseală', 'Analize de sânge, Ecografie abdominală', 'Diabet zaharat tip 2', 'Regim alimentar și administrare de antidiabetice'),
    (4, 4, 4, 7, 'Pacienta a prezentat tulburări menstruale și dureri pelviene.', 'Tulburări menstruale, dureri pelviene', 'Examinare ginecologică, Ecografie pelvină', 'Sindrom ovare polichistice', 'Administrare contraceptiv oral și consiliere pentru gestionarea simptomelor'),
    (5, 5, 5, 7, 'Pacientul a prezentat dureri de cap severe și pierdere temporară a conștienței.', 'Dureri de cap severe, pierdere temporară a conștienței', 'Tomografie cerebrală, EEG', 'Migrenă cu aura', 'Prescriere medicament anti-migrenă și recomandări pentru evitarea factorilor declanșatori'),
    (6, 6, 6, 9, 'Pacientul a prezentat dureri persistente la nivelul genunchiului drept.', 'Dureri la nivelul genunchiului drept', 'Examinare ortopedică, Radiografie', 'Artroză la nivelul genunchiului', 'Tratament conservator și fizioterapie'),
    (7, 7, 1, 10, 'Pacientul a prezentat febră, tuse și dureri în gât.', 'Febră, tuse, dureri în gât', 'Examinare clinică, Teste de laborator', 'Infecție respiratorie virală', 'Recomandări pentru odihnă și administrare de lichide'),
    (8, 8, 2, 8, 'Pacientul a prezentat dureri de spate și dificultăți de mișcare.', 'Dureri de spate, dificultăți de mișcare', 'Examinare clinică, Radiografie spinală', 'Hernie de disc lombară', 'Tratament medicamentos și recomandări pentru terapie fizică'),
    (9, 9, 3, 9, 'Pacientul a prezentat simptome de răceală și tuse persistentă.', 'Simptome de răceală, tuse persistentă', 'Examinare clinică, Teste de laborator', 'Infecție respiratorie acută', 'Recomandări pentru odihnă și administrare de lichide'),
    (10, 10, 4, 10, 'Pacientul a prezentat tulburări de somn și anxietate.', 'Tulburări de somn, anxietate', 'Examinare clinică, Interviu psihologic', 'Tulburare de anxietate', 'Consiliere psihologică și terapie cognitiv-comportamentală');



DELIMITER ;;
CREATE TRIGGER creeaza_cont
AFTER INSERT ON utilizator
FOR EACH ROW
BEGIN
    INSERT INTO autentificare (id_utilizator, nume)
    VALUES (NEW.id_utilizator, CONCAT(NEW.nume, NEW.prenume) );
END;
;;
DELIMITER ;

DELIMITER  ;;
CREATE PROCEDURE set_admin_lvl( IN p_cnp_utilizator VARCHAR(40), IN p_admin_lvl int)
BEGIN
     IF(p_admin_lvl<3) THEN
          UPDATE utilizator
          SET admin_lvl=p_admin_lvl
          WHERE cnp=p_cnp_utilizator;
     END IF;
END ;;
DELIMITER ;

CALL set_admin_lvl(1234567890, 4);

############################

DELIMITER ;;
CREATE PROCEDURE verifica_disponibilitate_medic (IN p_id_medic INT, IN p_id_serviciu_medical INT, IN p_data_programare DATE, IN p_ora_programare TIME, OUT disponibil INT)
BEGIN
      DECLARE k INT;
      DECLARE durata INT;
      DECLARE ora_final_programare TIME;
      DECLARE ora_programare_dinainte TIME;
      DECLARE ora_final_programare_dinainte TIME;
      
      SET disponibil=1;
      
      #daca medicul e in concediu
      SET k=1;
        WHILE k<=(SELECT MAX(id_concediu) FROM concedii) DO
	    IF (SELECT id_concediu FROM concedii WHERE id_angajat=p_id_medic AND id_concediu=k) THEN
             IF (p_data_programare >=(SELECT data_inceput FROM concedii WHERE id_angajat=p_id_medic AND id_concediu=k) AND p_data_programare <=(SELECT data_sfarsit FROM concedii WHERE id_angajat=p_id_medic AND id_concediu=k)) THEN
                  SET disponibil=0;
             END IF;
		END IF;
        SET k=k+1;
        END WHILE;
        
	  #daca are deja programare la ora aceea
	  IF disponibil=1 THEN
         IF (SELECT COUNT(*) FROM programari WHERE id_medic=p_id_medic AND data_programare=p_data_programare AND ora_programare=p_ora_programare)=1 THEN
          SET disponibil=0;
      END IF;
      END IF;
      
      #verifica daca se suprapune peste urmatoarea programare
      SELECT durata_min INTO durata FROM servicii_medicale WHERE id_serviciu_medical=p_id_serviciu_medical;
	  SELECT ADDTIME(p_ora_programare, SEC_TO_TIME(durata * 60)) INTO ora_final_programare;
      IF ora_final_programare>(SELECT ora_programare FROM programari WHERE ora_programare > p_ora_programare AND data_programare=p_data_programare AND id_medic=p_id_medic ORDER BY ora_programare ASC LIMIT 1) THEN
         SET disponibil=0;
	  END IF;
      SET ora_programare_dinainte=(SELECT ora_programare FROM programari WHERE ora_programare < p_ora_programare AND data_programare=p_data_programare AND id_medic=p_id_medic ORDER BY ora_programare ASC LIMIT 1);
      SELECT ADDTIME(ora_programare_dinainte, SEC_TO_TIME(durata * 60)) INTO ora_final_programare_dinainte;
      IF p_ora_programare<ora_final_programare_dinainte THEN
         SET disponibil=0;
	  END IF;
      
      
      #verifica daca programarea se incadreaza in orarul policlinicii
      IF p_ora_programare<(SELECT ora_inceput FROM orar WHERE id_zi=(DAYOFWEEK(p_data_programare) - 1 + 7) % 7 )THEN
         SET disponibil=0;
      END IF;
       IF ora_final_programare>(SELECT ora_sfarsit FROM orar WHERE id_zi=(DAYOFWEEK(p_data_programare) - 1 + 7) % 7 )THEN
         SET disponibil=0;
      END IF;
      
      SELECT disponibil;
END ;;
DELIMITER ;


##########################################
#######################
DELIMITER ;;
CREATE PROCEDURE creaza_programare (IN p_nume_pacient VARCHAR(40), IN p_prenume_pacient VARCHAR(40), 
                                    IN p_nume_medic VARCHAR(80), IN p_nume_specializare_medicala VARCHAR(40), 
                                    IN p_nume_unitate_medicala VARCHAR(40),  IN p_data_programare DATE, 
                                    IN p_ora_programare TIME, OUT output INT)
BEGIN 
      DECLARE disponibil INT;
      DECLARE p_id_pacient INT;
      DECLARE p_id_medic INT;
      DECLARE p_id_serviciu_medical INT;
      DECLARE p_adresa VARCHAR(80);
      
      SET p_id_pacient=(SELECT id_pacient FROM pacient WHERE nume=p_nume_pacient AND prenume=p_prenume_pacient);
      SET p_id_medic=(SELECT id_medic FROM medic a INNER JOIN utilizator b ON a.id_medic=b.id_utilizator WHERE CONCAT(b.nume, ' ', b.prenume)=p_nume_medic);
      SET p_id_serviciu_medical=(SELECT id_serviciu_medical FROM servicii_medicale a INNER JOIN specializare b ON a.id_specializare=b.id_specializare WHERE b.nume_specializare=p_nume_specializare_medicala);
      SET p_adresa=(SELECT adresa FROM unitate_medicala WHERE nume=p_nume_unitate_medicala); 
      
      CALL verifica_disponibilitate_medic (p_id_medic, p_id_serviciu_medical, p_data_programare, p_ora_programare, disponibil);
      IF disponibil=1 THEN
		 INSERT INTO programari(id_pacient, id_medic, id_serviciu_medical, data_programare, ora_programare, adresa)
         VALUES (p_id_pacient, p_id_medic, p_id_serviciu_medical, p_data_programare, p_ora_programare, p_adresa);
         SET output=1;
	  ELSE
         SET output=0;
      END IF;
END;;
//????

DELIMITER ;

CALL creaza_programare('Popescu', 'Ana', 'Georgescu Vasile', 'Dermatologie', 'Spitalul Alpha', '2023-07-03', '9:10:00', @x);

###############################################

DELIMITER ;;
CREATE PROCEDURE vezi_programari_doctor(IN p_id_medic VARCHAR(80), IN p_data_programare DATE)
BEGIN
       SELECT CONCAT(b.nume, ' ', b.prenume) AS nume_pacient, c.nume AS serviciu, a.ora_programare AS ora_inceput, 
              ADDTIME(a.ora_programare, SEC_TO_TIME(c.durata_min * 60)) AS ora_final, a.adresa 
       FROM programari a INNER JOIN pacient b ON a.id_pacient= b.id_pacient
                         INNER JOIN servicii_medicale c ON a.id_serviciu_medical=c.id_serviciu_medical
	   WHERE id_medic=p_id_medic AND data_programare=p_data_programare;
END;;
DELIMITER ;
CALL vezi_programari_doctor(1, '2023-01-05');

###################################################
DELIMITER ;;
CREATE PROCEDURE emite_bon(IN p_id_programare INT, OUT output INT)
BEGIN
     DECLARE y INT;
     DECLARE exista_programarea INT;
     DECLARE exista_bon INT;
     SET y=(SELECT id_serviciu_medical FROM programari WHERE id_programari=p_id_programare);
     SELECT pret INTO @x FROM servicii_medicale WHERE id_serviciu_medical=y;
     SELECT COUNT(*) INTO exista_programarea FROM programari WHERE id_programari = p_id_programare;
     SELECT COUNT(*) INTO exista_bon FROM bon_fiscal WHERE id_programari = p_id_programare;
     #SELECT exista; il pot face out si sa verific daca e diferit de 0 sa apara mesaj cum ca bonul a fost deja emis
     
     IF(exista_programarea=0) THEN
        SET output=0;
	 ELSE
        IF(exista_bon=0) THEN
            INSERT INTO bon_fiscal(id_programari, total) VALUES (p_id_programare, @x);
			SELECT a.id_programari AS id_bon, b.nume AS serviciu, b.pret AS total, CURTIME() AS ora_emitere_bon, CURDATE() AS data_emitere_bon
            FROM programari a
            INNER JOIN servicii_medicale b ON a.id_serviciu_medical=b.id_serviciu_medical
			WHERE a.id_programari=p_id_programare;
            SET output=1;
		ELSE
            SET output=2;
	    END IF;
     END IF;
END;;
DELIMITER ;

CALL emite_bon(1, @x);

####################################
DELIMITER ;;
CREATE PROCEDURE incasari_medic(IN p_id_utilizator INT, IN p_an INT, IN p_luna INT, OUT total_incasari INT)
BEGIN
     SELECT SUM(b.total) INTO total_incasari
     FROM programari a
     INNER JOIN bon_fiscal b ON a.id_programari=b.id_programari
     INNER JOIN medic c ON a.id_medic=c.id_medic
     WHERE a.id_medic=p_id_utilizator AND MONTH(a.data_programare)=p_luna AND YEAR(a.data_programare)=p_an;
     
     SET total_incasari=IFNULL(total_incasari, 0);
     #SELECT total_incasari;
END;;
DELIMITER ;

##########################
DELIMITER ;;
CREATE PROCEDURE calcul_salar_si_bonus(
    IN p_id_utilizator INT,
    IN p_an INT,
    IN p_luna INT,
    OUT v_salar INT,
    OUT bonus INT,
    OUT v_nr_ore_total INT)
BEGIN
    DECLARE p_data_calendaristica DATE;
    DECLARE i INT DEFAULT 1;
    DECLARE v_salar_din_contract INT;
    DECLARE v_nr_ore_din_contract INT;
    DECLARE v_plata_pe_ora INT;
    DECLARE v_nr_zile_luna INT;
    DECLARE v_zi_saptamana INT;
    DECLARE diferenta INT;
    DECLARE ora INT;
    DECLARE ziua_in_curs DATE;
    DECLARE k INT;
    DECLARE total_incasari_luna INT;
    DECLARE procent_bonus DECIMAL (3, 2);
    
    SET p_data_calendaristica= STR_TO_DATE(CONCAT(p_an, '-', p_luna, '-1'), '%Y-%m-%d');
    IF dayOFWEEK(p_data_calendaristica)=1 THEN
       SET v_zi_saptamana=7;
	ELSE
	   SET v_zi_saptamana=dayOFWEEK(p_data_calendaristica)-1;
	END IF;
    
    -- Numarul de zile din luna
    SET v_nr_zile_luna = DAY(LAST_DAY(p_data_calendaristica));
    SELECT salar INTO v_salar_din_contract
    FROM utilizator
    WHERE id_utilizator = p_id_utilizator;
    
    SELECT nr_ore INTO v_nr_ore_din_contract
    FROM utilizator
    WHERE id_utilizator = p_id_utilizator;
    
    -- Plata pe ora pentru utilizatorul specificat
    SET v_plata_pe_ora=v_salar_din_contract/v_nr_ore_din_contract;
       
    -- Numarul total de ore lucrate in luna curenta
    SET v_nr_ore_total=0;
    WHILE i<=v_nr_zile_luna DO
        SET diferenta=TIMEDIFF((SELECT ora_sfarsit FROM orar WHERE id_zi=v_zi_saptamana) , (SELECT ora_inceput FROM orar WHERE id_zi=v_zi_saptamana));
        SET ora=HOUR(diferenta);
        
        SET ziua_in_curs=STR_TO_DATE(CONCAT(p_an, '-', p_luna, '-', i), '%Y-%m-%d');
        
        SET k=1;
        WHILE k<=(SELECT MAX(id_concediu) FROM concedii) DO
	    IF (SELECT id_concediu FROM concedii WHERE id_angajat=p_id_utilizator AND id_concediu=k) THEN
             IF (ziua_in_curs >=(SELECT data_inceput FROM concedii WHERE id_angajat=p_id_utilizator AND id_concediu=k) AND ziua_in_curs <=(SELECT data_sfarsit FROM concedii WHERE id_angajat=p_id_utilizator AND id_concediu=k)) THEN
                  SET ora=0;
             END IF;
		END IF;
        SET k=k+1;
        END WHILE;
        
        SET v_nr_ore_total=v_nr_ore_total+ora;
        SET i=i+1;
        SET v_zi_saptamana=v_zi_saptamana+1;
        IF v_zi_saptamana>7 THEN
           SET v_zi_saptamana=1;
        END IF;
    END WHILE;

    -- Salariul total
    SET v_salar = v_nr_ore_total * v_plata_pe_ora;
    
    IF (SELECT functie FROM utilizator WHERE id_utilizator=p_id_utilizator)=5 THEN 
        SELECT COALESCE(bonus, 0.05) INTO procent_bonus FROM medic WHERE id_medic=p_id_utilizator; #cum scriu asta? ca nu imi da decat null
        
        #incasari pe luna medic
        CALL incasari_medic(p_id_utilizator, p_an, p_luna, total_incasari_luna);
        #SELECT total_incasari_luna;
        
        #bonus pe luna medic
        SET bonus=total_incasari_luna * procent_bonus;
    END IF;
END;;

DELIMITER ;

####################

DELIMITER ;;
CREATE PROCEDURE vezi_salariu(
    IN p_nume_utilizator VARCHAR(40),
    IN p_prenume_utilizator VARCHAR(40),
    IN p_an INT,
    IN p_luna INT)
BEGIN
    DECLARE v_salar INT;
    DECLARE bonus INT;
    DECLARE v_nr_ore_total INT;
    DECLARE p_id_utilizator INT;
    
    SET p_id_utilizator=(SELECT id_utilizator FROM utilizator WHERE nume=p_nume_utilizator AND prenume=p_prenume_utilizator);
    ##########and cnp???
    
    CALL calcul_salar_si_bonus(p_id_utilizator, p_an, p_luna, v_salar, bonus, v_nr_ore_total);
    #daca e medic
    IF (SELECT functie FROM utilizator WHERE id_utilizator=p_id_utilizator)=5 THEN 
        SELECT CONCAT(a.nume, ' ', a.prenume) AS nume_salariat, v_nr_ore_total AS nr_ore_lucrate, v_salar AS 'salar(lei)', bonus AS 'bonus(lei)', v_salar+bonus AS 'total(lei)', CONCAT( LPAD(p_luna, 2, '0'),  '.', p_an) AS luna
        FROM utilizator a
        WHERE id_utilizator=p_id_utilizator;
    ELSE
    #daca nu e medic
        SELECT CONCAT(a.nume, ' ', a.prenume) AS nume_salariat, v_nr_ore_total AS nr_ore_lucrate, v_salar AS 'salar(lei)', "" AS 'bonus(lei)', v_salar AS 'total(lei)', CONCAT( LPAD(p_luna, 2, '0'),  '.', p_an) AS luna
        FROM utilizator a
        WHERE id_utilizator=p_id_utilizator;
        # SET p_salar_intreg=v_salar;
	END IF;
END;;
DELIMITER ;

CALL vezi_salariu('Popovici', 'Elena', 2023, 2);

#####################################
DELIMITER ;;
CREATE PROCEDURE vezi_profit_generat_de_medic(IN p_id_medic INT, IN p_luna INT, IN p_an INT)
BEGIN
	 DECLARE incasari_luna INT;
     DECLARE salar_luna INT;
     DECLARE bonus_luna INT;
     
     CALL incasari_medic(p_id_medic, p_an, p_luna, incasari_luna);
     CALL calcul_salar_si_bonus(p_id_medic, p_an, p_luna, salar_luna, bonus_luna, @x);
     
     SELECT CONCAT(a.nume, ' ', a.prenume) AS nume, incasari_luna-salar_luna-bonus_luna AS profit_generat, CONCAT( LPAD(p_luna, 2, '0'),  '.', p_an) AS luna
     FROM utilizator a
	 WHERE id_utilizator=p_id_medic;
     
END;;
DELIMITER ;

CALL vezi_profit_generat_de_medic(2, 1, 2023);

#############################################################
DELIMITER ;;
CREATE PROCEDURE vezi_profit_generat_de_medici(IN p_luna INT, IN p_an INT)
BEGIN
     DECLARE p_id_medic INT;
     DECLARE nume VARCHAR(80);
     DECLARE incasari_luna INT;
     DECLARE salar_luna INT;
     DECLARE bonus_luna INT;
     DECLARE done INT DEFAULT FALSE;
     DECLARE cur CURSOR FOR SELECT id_medic FROM medic ORDER BY id_Medic ASC;
     DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;
     
     CREATE TEMPORARY TABLE temp_profit_generat_de_medici (
        nume_medic VARCHAR(80),
        incasari INT, 
        cheltuieli  INT, 
        profit INT,
        luna VARCHAR(20)  
	 );
     
     #SET p_id_medic=1;
     #SET p_an=YEAR(NOW()); 

    OPEN cur;
    read_loop: LOOP
        FETCH cur INTO p_id_medic;
        IF done THEN
            LEAVE read_loop;
        END IF;
        
          CALL incasari_medic(p_id_medic, p_an, p_luna, incasari_luna);
          CALL calcul_salar_si_bonus(p_id_medic, p_an, p_luna, salar_luna, bonus_luna, @x);
          
          SELECT CONCAT(a.nume, ' ', a.prenume) INTO nume
          FROM utilizator a
	      WHERE id_utilizator=p_id_medic;
          
          -- Salvarea datelor în tabelul temporar
         INSERT INTO temp_profit_generat_de_medici (nume_medic, incasari, cheltuieli, profit, luna)
		 VALUES (nume, incasari_luna, salar_luna+bonus_luna, incasari_luna-salar_luna-bonus_luna, CONCAT( LPAD(p_luna, 2, '0'),  '.', p_an));
     END LOOP;
    CLOSE cur;
    
    -- Afișarea datelor din tabelul temporar
    SELECT * FROM temp_profit_generat_de_medici;

    -- Ștergerea tabelului temporar
    DROP TEMPORARY TABLE IF EXISTS temp_profit_generat_de_medici;
END;;
DELIMITER ;

CALL vezi_profit_generat_de_medici(1, 2023);

####################################################
#DROP PROCEDURE vezi_profit_generat_de_unitatile_medicale;
DELIMITER ;;
CREATE PROCEDURE vezi_profit_generat_de_specializari(IN p_luna INT, IN p_an INT)
BEGIN 
     DECLARE p_id_medic INT;
     DECLARE p_id_specializare INT;
	 DECLARE p_nume_specializare VARCHAR(80);
     DECLARE venit INT;
     DECLARE p_cheltuieli INT;
     DECLARE salar_luna INT;
     DECLARE bonus_luna INT;
     DECLARE done INT DEFAULT FALSE;
     DECLARE cur CURSOR FOR SELECT id_specializare FROM specializare ORDER BY id_specializare ASC;
     DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;
     
     -- Creearea tabelului temporar
     DROP TEMPORARY TABLE IF EXISTS temp_profit_generat_de_specializare;
     CREATE TEMPORARY TABLE temp_profit_generat_de_specializare (
        nume_specializare VARCHAR(80),
        incasari INT,
        cheltuieli INT,
        profit INT,
        luna VARCHAR(20)  
	 );
     
	OPEN cur;
    read_loop: LOOP
        FETCH cur INTO p_id_specializare;
        IF done THEN
            LEAVE read_loop;
        END IF;
        
        -- nume_specializare
        SELECT nume_specializare INTO p_nume_specializare FROM specializare WHERE id_specializare=p_id_specializare;
        
        -- venit
	    SELECT IFNULL(SUM(c.pret), 0) INTO venit
		FROM programari a
	    INNER JOIN bon_fiscal b ON a.id_programari=b.id_programari
        INNER JOIN servicii_medicale c ON a.id_serviciu_medical=c.id_serviciu_medical
		WHERE c.id_specializare=p_id_specializare AND MONTH(a.data_programare)=p_luna AND YEAR(a.data_programare)=p_an;
    
        -- cheltuieli
        SET p_cheltuieli=0;
        SET p_id_medic=1;
        
        WHILE (p_id_medic<=(SELECT MAX(id_medic) FROM medic)) DO
            IF (SELECT id_specializare FROM medic WHERE id_medic=p_id_medic)=p_id_specializare THEN
                CALL calcul_salar_si_bonus(p_id_medic, p_an, p_luna, salar_luna, bonus_luna, @x);
				SET p_cheltuieli=p_cheltuieli+salar_luna+bonus_luna;
			END IF;
            SET p_id_medic=p_id_medic+1;
        END WHILE;
        
        INSERT INTO temp_profit_generat_de_specializare (nume_specializare, incasari, cheltuieli, profit, luna)
		VALUES (p_nume_specializare, venit, p_cheltuieli, venit-p_cheltuieli, CONCAT( LPAD(p_luna, 2, '0'),  '.', p_an));
        
    END LOOP;
    CLOSE cur;
    
    -- Afișarea datelor din tabelul temporar
    SELECT * FROM temp_profit_generat_de_specializare;

    -- Ștergerea tabelului temporar
     DROP TEMPORARY TABLE IF EXISTS temp_profit_generat_de_specializare;
END;;
DELIMITER ;
CALL emite_bon(1, @x);
CALL emite_bon(2, @x);
CALL emite_bon(3, @x);
CALL emite_bon(4, @x);
CALL vezi_profit_generat_de_specializari(1, 2023);
CALL vezi_salariu('Ionescu', 'Maria', 2023, 1);



#######################################DAIANA####################################
 
DELIMITER ;;
CREATE PROCEDURE modifica_orar_general(
    IN p_zi VARCHAR(40),
    IN p_ora_inceput TIME,
    IN p_ora_sfarsit TIME)
BEGIN
    DECLARE v_id_zi INT;

    -- Obține ID-ul zilei
    SELECT id_zi INTO v_id_zi
    FROM orar
    WHERE zi = p_zi;

    -- Verifică dacă ziua există în tabelul orar
    IF v_id_zi IS NOT NULL THEN
        -- Actualizează ora de început și sfârșit pentru ziua specificată
        UPDATE orar
        SET ora_inceput = p_ora_inceput,
            ora_sfarsit = p_ora_sfarsit
        WHERE id_zi = v_id_zi;
    END IF;
END
;;
DELIMITER ;
CALL modifica_orar_general('Luni','13:00:00','15:00:00');
select* from orar

DELIMITER ;;
CREATE PROCEDURE adauga_concediu(
    IN p_id_angajat INT,
    IN p_data_inceput DATE,
    IN p_data_sfarsit DATE)
BEGIN
   DECLARE v_continue BOOLEAN DEFAULT TRUE;
   DECLARE v_concediu_existent INT;

   IF YEAR(p_data_inceput) = YEAR(p_data_sfarsit) AND MONTH(p_data_inceput) > MONTH(p_data_sfarsit) THEN
      SET v_continue = FALSE;
   END IF;
   IF v_continue THEN
      SELECT COUNT(*) INTO v_concediu_existent
      FROM concedii
      WHERE id_angajat = p_id_angajat
          AND (
              (p_data_inceput BETWEEN data_inceput AND data_sfarsit) OR
              (p_data_sfarsit BETWEEN data_inceput AND data_sfarsit) OR
              (data_inceput BETWEEN p_data_inceput AND p_data_sfarsit) OR
              (data_sfarsit BETWEEN p_data_inceput AND p_data_sfarsit)
          );
      IF v_concediu_existent = 0 THEN
        INSERT INTO concedii (id_angajat, data_inceput, data_sfarsit)
        VALUES (p_id_angajat, p_data_inceput, p_data_sfarsit);
      END IF;
   END IF;
END
;;
DELIMITER ;

CALL adauga_concediu(1, '2023-05-26', '2023-03-27');
CALL adauga_concediu(1, '2023-10-26', '2023-03-27');
CALL adauga_concediu(1, '2023-10-26', '2023-11-27');


DELIMITER ;;
CREATE PROCEDURE vezi_concediu(
	IN p_id_angajat INT)
    
BEGIN
    SELECT *
    FROM concedii
    WHERE id_angajat = p_id_angajat;
END
;;
DELIMITER ;

CALL vezi_concediu(1);


-- DROP TEMPORARY TABLE IF EXISTS temp_orar;
-- DROP PROCEDURE IF EXISTS vezi_orar;


DROP TEMPORARY TABLE IF EXISTS temp_orar;
DELIMITER ;;
CREATE PROCEDURE vezi_orar(
    IN p_an INT,
    IN p_luna INT
)
BEGIN
    DECLARE v_data_inceput DATE;
    DECLARE v_data_sfarsit DATE;
    DECLARE v_ziua INT;
    DECLARE v_ora_inceput TIME;
    DECLARE v_ora_sfarsit TIME;
    DECLARE v_nume_zi VARCHAR(10);

    SET v_data_inceput = STR_TO_DATE(CONCAT(p_an, '-', LPAD(p_luna, 2, '0'), '-01'), '%Y-%m-%d');
    SET v_data_sfarsit = LAST_DAY(v_data_inceput);

    CREATE TEMPORARY TABLE temp_orar (
        ziua INT,
        nume_zi VARCHAR(10),
        ora_inceput TIME,
        ora_sfarsit TIME
    );

    WHILE v_data_inceput <= v_data_sfarsit DO
        SET v_ziua = DAYOFMONTH(v_data_inceput);
        SET v_nume_zi = DAYNAME(v_data_inceput);

        IF EXISTS (
            SELECT * FROM orar_special os
            WHERE os.data_calendaristica = v_data_inceput
        ) THEN
            SELECT os.ora_inceput, os.ora_sfarsit INTO v_ora_inceput, v_ora_sfarsit
            FROM orar_special os
            WHERE os.data_calendaristica = v_data_inceput;
        ELSE
            SELECT o.ora_inceput, o.ora_sfarsit INTO v_ora_inceput, v_ora_sfarsit
            FROM orar o
            WHERE o.id_zi = IF(DAYOFWEEK(v_data_inceput) = 1, 7, DAYOFWEEK(v_data_inceput) - 1);
        END IF;

        INSERT INTO temp_orar (ziua, nume_zi, ora_inceput, ora_sfarsit) VALUES (v_ziua, v_nume_zi, v_ora_inceput, v_ora_sfarsit);
        SET v_data_inceput = DATE_ADD(v_data_inceput, INTERVAL 1 DAY);
    END WHILE;

    SELECT * FROM temp_orar;
    DROP TEMPORARY TABLE IF EXISTS temp_orar;
END
;;
DELIMITER ;
CALL vezi_orar(2023, 1);




DELIMITER ;;
CREATE PROCEDURE completaza_raport(
    IN p_nume_pacient VARCHAR(80),
    IN p_nume_medic VARCHAR(80),
    IN p_nume_asistent VARCHAR(80),
    IN p_istoric TEXT,
    IN p_simptome TEXT,
    IN p_investigatii TEXT,
    IN p_diagnostic TEXT,
    IN p_recomandari TEXT
)
BEGIN
    DECLARE p_id_pacient INT;
    DECLARE p_id_medic INT;
    DECLARE p_id_asistent INT;
    
    SET p_id_pacient=(SELECT id_pacient FROM pacient WHERE CONCAT(nume, ' ', prenume)=p_nume_pacient);
    SET p_id_medic=(SELECT a.id_medic FROM medic a
                    INNER JOIN utilizator b ON a.id_medic=b.id_utilizator
					WHERE CONCAT(b.nume, ' ', b.prenume)=p_nume_medic);
                    
    SET p_id_asistent=(SELECT a.id_asistent FROM asistent a
                          INNER JOIN utilizator b ON a.id_asistent=b.id_utilizator
					      WHERE CONCAT(b.nume, ' ', b.prenume)=p_nume_asistent);
                          
    INSERT INTO raport (id_pacient, id_medic, id_asistent, istoric, simptome, investigatii, diagnostic, recomandari)
    VALUES (p_id_pacient, p_id_medic, p_id_asistent, p_istoric, p_simptome, p_investigatii, p_diagnostic, p_recomandari);
END
;;
DELIMITER ;

CALL completaza_raport('Pop Vasile', 'Popescu Ion', 'Popovici Elena', 'Pacientul a prezentat dureri de cap ', 'Dureri de cap ', 'Tomografie generala', 'Migrena', 'Medicamente anti-migrena');

DELIMITER ;;
CREATE PROCEDURE istoric_pacient(
    IN p_id_pacient INT
)
BEGIN
    DECLARE nume_pacient VARCHAR(80);
    DECLARE nume_medic VARCHAR(80);
    DECLARE nume_asistent VARCHAR(80);
    DECLARE nr_rapoarte INT;
    DECLARE i INT;
    DECLARE p_id_raport INT;
    DECLARE p_istoric TEXT;
    DECLARE p_simptome TEXT; 
    DECLARE p_investigatii TEXT;
    DECLARE p_diagnostic TEXT;
    DECLARE p_recomandari TEXT;
    
    CREATE TEMPORARY TABLE IF NOT EXISTS TempTable (
        id_raport INT,
        pacient VARCHAR(80),
        medic VARCHAR(80),
        asistent VARCHAR(80),
		istoric TEXT,
        simptome TEXT, 
        investigatii TEXT, 
        diagnostic TEXT, 
        recomandari TEXT
    );
    
    SELECT COUNT(*) INTO nr_rapoarte FROM raport WHERE id_pacient=p_id_pacient;
    IF nr_rapoarte!=0 THEN
       SET i=0;
       WHILE i < nr_rapoarte DO 
    
		    -- selecteaza id_raport
            SELECT id_raport INTO p_id_raport FROM raport WHERE id_pacient = p_id_pacient ORDER BY id_raport LIMIT 1 OFFSET i;
            
            SELECT CONCAT(a.nume, ' ', a.prenume) INTO nume_pacient
            FROM pacient a 
            INNER JOIN raport b ON b.id_pacient=a.id_pacient 
            WHERE b.id_raport=p_id_raport;
            
            SELECT CONCAT(a.nume, ' ', a.prenume) INTO nume_medic 
            FROM utilizator a 
            INNER JOIN medic b ON b.id_medic=a.id_utilizator 
            INNER JOIN raport c ON a.id_utilizator=c.id_medic
            WHERE c.id_raport=p_id_raport;
            
            SELECT CONCAT(a.nume, ' ', a.prenume) INTO nume_asistent
            FROM utilizator a 
            INNER JOIN asistent b ON b.id_asistent=a.id_utilizator 
            INNER JOIN raport c ON a.id_utilizator=c.id_asistent
            WHERE c.id_raport=p_id_raport;
            
            SELECT istoric INTO p_istoric FROM raport WHERE id_raport=p_id_raport;
            SELECT simptome INTO p_simptome FROM raport WHERE id_raport=p_id_raport;
            SELECT investigatii INTO p_investigatii FROM raport WHERE id_raport=p_id_raport;
            SELECT diagnostic INTO p_diagnostic FROM raport WHERE id_raport=p_id_raport;
            SELECT recomandari INTO p_recomandari FROM raport WHERE id_raport=p_id_raport;
            
            INSERT INTO TempTable 
            VALUES (p_id_raport, nume_pacient, nume_medic, nume_asistent, p_istoric, p_simptome, 
                    p_investigatii, p_diagnostic, p_recomandari); 
                  
        SET i = i + 1;
	  END WHILE;
    END IF;
   
    SELECT * FROM TempTable;
    DROP TEMPORARY TABLE IF EXISTS TempTable;
END
;;
DELIMITER ;

CALL istoric_pacient(1);

######################################AMELIA#####################################
DROP PROCEDURE IF EXISTS ADD_MEDIC;
DELIMITER ;;
CREATE PROCEDURE ADD_MEDIC(
    IN cnp varchar(40), 
    IN nume varchar(40), 
    IN prenume varchar(40), 
    IN adresa varchar(40), 
    IN nr_telefon INT, 
    IN email varchar(40), 
    IN cont_iban varchar(45),
    IN nr_contract int,
    IN data_angajarii DATE,
    IN salar int,
    IN nr_ore int,
    IN cod_parafa int,
    IN id_specializare int,
    IN grad int,
	IN competente TEXT,
	IN  titlu_stiintific TEXT,
	IN post_didactic TEXT) 
BEGIN
	INSERT INTO `utilizator` (cnp, nume, prenume, adresa, nr_telefon, email, cont_iban, nr_contract, data_angajarii, salar, nr_ore)
    VALUES (cnp, nume, prenume, adresa, nr_telefon, email, cont_iban, nr_contract, data_angajarii, salar, nr_ore);
    INSERT INTO medic(id_medic, cod_parafa, id_specializare, grad,competente,titlu_stiintific, post_didactic)
    VALUES(LAST_INSERT_ID(), cod_parafa, id_specializare, grad, competente, titlu_stiintific, post_didactic);
    UPDATE utilizator
    SET functie=5
    WHERE id_utilizator=LAST_INSERT_ID();
END
;;
DELIMITER ;
#CALL ADD_MEDIC('7544332211', 'Cratita', 'Clementina', 'Str. Fructelor nr. 14',0712309618, 'cratita.clementina@email.com', 'RO128932271554433221122', 3333, '2022-02-14', 8000, 200, 234, 1, 2, NULL, NULL, NULL);
#CALL ADD_MEDIC('1524432710', 'Peter', 'Bianca', 'Str. Republicii nr. 34',0720300619, 'peter.bianca@email.com', 'RO128412271559933221002', 67, '2022-09-24', 9000, 220, 1134, 2, 2, NULL, NULL, NULL);
CALL ADD_MEDIC('8524839190', 'Baritiu', 'Georgel', 'Str. Frunzei nr. 49',0718394617, 'georgel.baritiu@email.com', 'RO128912221559943020002', 97, '2022-09-27', 9600, 220, 8234, 3, 1, 'Neurologie', 'Doctor în Științe Medicale', 'Profesor Conferentiar');


DROP PROCEDURE IF EXISTS AFISARE_MEDICI;
DELIMITER ;;
CREATE PROCEDURE AFISARE_MEDICI()
BEGIN
    SELECT
        u.id_utilizator,
        u.cnp,
        u.nume,
        u.prenume,
        u.adresa,
        u.nr_telefon,
        u.email,
        u.cont_iban,
        u.nr_contract,
        u.data_angajarii,
        u.salar,
        u.nr_ore,
        m.cod_parafa,
        m.id_specializare,
        m.grad
    FROM
        utilizator u
    INNER JOIN
        medic m ON u.id_utilizator = m.id_medic
    WHERE
        u.functie = 5;
END
;;
DELIMITER ;
CALL AFISARE_MEDICI();

DROP PROCEDURE IF EXISTS ADD_ASISTENT;
DELIMITER ;;
CREATE PROCEDURE ADD_ASISTENT(
    IN cnp varchar(40), 
    IN nume varchar(40), 
    IN prenume varchar(40), 
    IN adresa varchar(40), 
    IN nr_telefon INT, 
    IN email varchar(40), 
    IN cont_iban varchar(45),
    IN nr_contract int,
    IN data_angajarii DATE,
    IN salar int,
    IN nr_ore int,
    IN tip_asistent int,
    IN grad int) #??
BEGIN
	INSERT INTO `utilizator` (cnp, nume, prenume, adresa, nr_telefon, email, cont_iban, nr_contract, data_angajarii, salar, nr_ore)
    VALUES (cnp, nume, prenume, adresa, nr_telefon, email, cont_iban, nr_contract, data_angajarii, salar, nr_ore);
    INSERT INTO asistent(id_asistent, tip_asistent,  grad)
    VALUES(LAST_INSERT_ID(), tip_asistent,  grad);
    UPDATE utilizator
    SET functie=4
    WHERE id_utilizator=LAST_INSERT_ID();
END
;;
DELIMITER ;
CALL ADD_ASISTENT('1520038219', 'Popovici', 'David', 'Str. Apei nr. 1', 0700301348, 'popovici.david@email.com', 'RO128945271774439821122', 1510, '2022-02-14', 8000, 200,  3, 2);

DELIMITER ;;
CREATE PROCEDURE AFISARE_ASISTENTI()
BEGIN
    SELECT
        u.id_utilizator,
        u.cnp,
        u.nume,
        u.prenume,
        u.adresa,
        u.nr_telefon,
        u.email,
        u.cont_iban,
        u.nr_contract,
        u.data_angajarii,
        u.salar,
        u.nr_ore,
        a.tip_asistent AS detalii_functie,
        a.grad
    FROM
        utilizator u
    INNER JOIN
        asistent a ON u.id_utilizator = a.id_asistent
    WHERE
        u.functie = 4;
END
;;
DELIMITER ;
CALL AFISARE_ASISTENTI();

DROP PROCEDURE IF EXISTS ADD_RECEPTIONER;
DELIMITER ;;
CREATE PROCEDURE ADD_RECEPTIONER(
    IN cnp varchar(40), 
    IN nume varchar(40), 
    IN prenume varchar(40), 
    IN adresa varchar(40), 
    IN nr_telefon INT, 
    IN email varchar(40), 
    IN cont_iban varchar(45),
    IN nr_contract int,
    IN data_angajarii DATE,
    IN salar int,
    IN nr_ore int)
BEGIN
	INSERT INTO `utilizator` (cnp, nume, prenume, adresa, nr_telefon, email, cont_iban, nr_contract, data_angajarii, salar, nr_ore)
    VALUES (cnp, nume, prenume, adresa, nr_telefon, email, cont_iban, nr_contract, data_angajarii, salar, nr_ore);
    UPDATE utilizator
    SET functie=3
    WHERE id_utilizator=LAST_INSERT_ID();
END
;;
DELIMITER ;
CALL ADD_RECEPTIONER('1280038219', 'Birle', 'Cristian', 'Str. Unirii nr. 102', 0746101348, 'birle.cristian@email.com', 'RO348945271774439821109', 13, '2023-05-04', 5000, 160);
DELIMITER ;;
CREATE PROCEDURE AFISARE_RECEPTIONER()
BEGIN
    SELECT
        u.id_utilizator,
        u.cnp,
        u.nume,
        u.prenume,
        u.adresa,
        u.nr_telefon,
        u.email,
        u.cont_iban,
        u.nr_contract,
        u.data_angajarii,
        u.salar,
        u.nr_ore
    FROM
        utilizator u
    WHERE
        u.functie = 3;
END
;;
DELIMITER ;
CALL AFISARE_RECEPTIONER();

DROP PROCEDURE IF EXISTS ADD_INSPECTOR;
DELIMITER ;;
CREATE PROCEDURE ADD_INSPECTOR(
    IN cnp varchar(40), 
    IN nume varchar(40), 
    IN prenume varchar(40), 
    IN adresa varchar(40), 
    IN nr_telefon INT, 
    IN email varchar(40), 
    IN cont_iban varchar(45),
    IN nr_contract int,
    IN data_angajarii DATE,
    IN salar int,
    IN nr_ore int)
BEGIN
	INSERT INTO `utilizator` (cnp, nume, prenume, adresa, nr_telefon, email, cont_iban, nr_contract, data_angajarii, salar, nr_ore)
    VALUES (cnp, nume, prenume, adresa, nr_telefon, email, cont_iban, nr_contract, data_angajarii, salar, nr_ore);
    UPDATE utilizator
    SET functie=1
    WHERE id_utilizator=LAST_INSERT_ID();
END
;;
DELIMITER ;
CALL ADD_INSPECTOR('1971038214', 'Enescu', 'Ciprian', 'Str. Melodiei nr. 19', 0731901349, 'enescu.ciprian@email.com', 'RO148045271774439422122', 17, '2023-02-01', 6500, 140);

DELIMITER ;;
CREATE PROCEDURE AFISARE_INSPECTOR()
BEGIN
    SELECT
        u.id_utilizator,
        u.cnp,
        u.nume,
        u.prenume,
        u.adresa,
        u.nr_telefon,
        u.email,
        u.cont_iban,
        u.nr_contract,
        u.data_angajarii,
        u.salar,
        u.nr_ore
    FROM
        utilizator u
    WHERE
        u.functie = 1;
END
;;
DELIMITER ;
CALL AFISARE_INSPECTOR();

DROP PROCEDURE IF EXISTS ADD_EXPERT_FINANCIAR;
DELIMITER ;;
CREATE PROCEDURE ADD_EXPERT_FINANCIAR(
    IN cnp varchar(40), 
    IN nume varchar(40), 
    IN prenume varchar(40), 
    IN adresa varchar(40), 
    IN nr_telefon INT, 
    IN email varchar(40), 
    IN cont_iban varchar(45),
    IN nr_contract int,
    IN data_angajarii DATE,
    IN salar int,
    IN nr_ore int)
BEGIN
	INSERT INTO `utilizator` (cnp, nume, prenume, adresa, nr_telefon, email, cont_iban, nr_contract, data_angajarii, salar, nr_ore)
    VALUES (cnp, nume, prenume, adresa, nr_telefon, email, cont_iban, nr_contract, data_angajarii, salar, nr_ore);
    UPDATE utilizator
    SET functie=2
    WHERE id_utilizator=LAST_INSERT_ID();
    
END
;;
DELIMITER ;
CALL ADD_EXPERT_FINANCIAR('1221838671', 'Vinatoru', 'Daiana', 'Str. Vantorului nr. 17', 0701107331, 'vinatoru.daiana@email.com', 'RO77805527477043912612', 20, '2023-08-20', 7000, 120);

DELIMITER ;;
CREATE PROCEDURE AFISARE_EXPERTI_FINANCIARI()
BEGIN
    SELECT
        u.id_utilizator,
        u.cnp,
        u.nume,
        u.prenume,
        u.adresa,
        u.nr_telefon,
        u.email,
        u.cont_iban,
        u.nr_contract,
        u.data_angajarii,
        u.salar,
        u.nr_ore
    FROM
        utilizator u
    WHERE
        u.functie = 2;
END
;;
DELIMITER ;
CALL AFISARE_EXPERTI_FINANCIARI();
select * from utilizator;
select *from medic;
select *from asistent;
select *from functii;
DELETE FROM utilizator
WHERE id_utilizator= 19;
INSERT INTO `utilizator` (admin_lvl,cnp, nume, prenume, adresa, nr_telefon, email, cont_iban, nr_contract, data_angajarii, functie,salar, nr_ore)
VALUES (2,'1626360840', 'Iordache', 'Viorel', 'Str. Florilor nr.20',722388348, 'viorel.iordache@email.com','RO185125891084339461102', 12,'2020-07-22',1,7400, 210 );
    
    
DELIMITER ;;
CREATE PROCEDURE ADD_PACIENT(
    IN p_nume VARCHAR(40), 
    IN p_prenume VARCHAR(40), 
    IN p_CNP_pacient VARCHAR(40),
    OUT output INT)
BEGIN
    DECLARE exista_pacient INT;
    -- Adăugare raport
    SELECT COUNT(*) INTO exista_pacient FROM pacient WHERE CNP_pacient = p_CNP_pacient;
    
    IF exista_pacient=0 THEN
        IF (p_CNP_pacient REGEXP '^[0-9]{13}$') THEN
            -- Adăugare pacient
            INSERT INTO pacient (nume, prenume, CNP_pacient)
            VALUES (p_nume, p_prenume, p_CNP_pacient);
            SET output=1; #pacientul a fost adaugat
        ELSE
            SET output=2; #cnp-ul nu are 13 cifre
		END IF;
	ELSE
        SET output=0; #pacientul exista deja
    END IF;
END
;;
DELIMITER ;

CALL ADD_PACIENT ('Pop', 'Vasile', '1634567890123', @x);

DELIMITER ;;
CREATE PROCEDURE afiseaza_concedii_utilizator(
    IN p_id_angajat INT)
BEGIN
    SELECT *
    FROM concedii
    WHERE id_angajat = p_id_angajat;
END
;;
DELIMITER ;
CALL afiseaza_concedii_utilizator(1);

DELIMITER ;;
CREATE PROCEDURE vezi_date_personale(
    IN p_id_utilizator INT)
    BEGIN
    SELECT  cnp, nume, prenume, adresa, nr_telefon, email, cont_iban, data_angajarii, functie
    FROM utilizator
    WHERE id_utilizator=p_id_utilizator;
END
;;
DELIMITER ;
CALL vezi_date_personale(3);


CALL set_admin_lvl(1234567890, 1);
DELIMITER ;;
CREATE PROCEDURE cauta_angajat(
    IN p_nume VARCHAR(40),
    IN p_prenume VARCHAR(40),
    IN p_functie INT)
BEGIN 
    SELECT cnp, nume, prenume, adresa, nr_telefon, email, cont_iban, data_angajarii, functie
    FROM utilizator
    WHERE nume = p_nume AND prenume = p_prenume AND functie = p_functie;
END
;;
DELIMITER ;
CALL cauta_angajat('Popescu', 'Ion', 5);