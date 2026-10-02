create database TEMA1_PS;

create table tema1_ps.parfum(

id_parfum int auto_increment primary key,
nume VARCHAR(50) NOT NULL,
producator VARCHAR(50) NOT NULL,
descriere VARCHAR(50)

);

create table tema1_ps.parfumerie(

id_parfumerie int auto_increment primary key,
nume varchar(50) not null,
adresa varchar(50) not null,
telefon varchar(50) not null
);

create table tema1_ps.stoc(

id_stoc int auto_increment primary key,
id_parfumerie int ,
id_parfum int ,
cantitate int ,
disponibilitate boolean,
foreign key (id_parfumerie) references parfumerie (id_parfumerie),
foreign key (id_parfum) references parfum(id_parfum)
);

INSERT INTO tema1_ps.parfum ( nume, producator, descriere)
VALUES
( 'Chanel No 5', 'Chanel', 'Clasic' ),
( 'Dior Sauvage', 'Dior', 'Fresh' ),
('Gucci Bloom', 'Gucci', 'Floral'),
( 'Versace Eros', 'Versace', 'Lemnos'),
( 'Armani Si', 'Armani', 'Elegant');

INSERT INTO tema1_ps.parfumerie (nume, adresa, telefon)
VALUES
( 'Parfumerie Belle', 'Str. Libertatii 10', '0755123456'),
( 'Parfumerie Luxury', 'Bd. Unirii 15', '0734567890'),
( 'Parfumerie Aroma', 'Calea Victoriei 33', '0722333444'),
( 'Parfumerie Deluxe', 'Str. Mihai Viteazu 5', '0744111222'),
( 'Parfumerie Chic', 'Bd. Independentei 7', '0766777888');

INSERT INTO tema1_ps.stoc ( id_parfumerie, id_parfum, cantitate, disponibilitate)
VALUES
( 1, 1, 10, TRUE),
( 1, 2, 0, FALSE),
( 2, 3, 5, TRUE),
( 3, 4, 0, FALSE),
( 4, 5, 12, TRUE),
( 2, 1, 3, TRUE),
( 5, 2, 0, FALSE),
( 3, 5, 7, TRUE),
( 4, 3, 2, TRUE),
( 5, 4, 0, FALSE);

ALTER TABLE tema1_ps.stoc DROP FOREIGN KEY stoc_ibfk_1;

ALTER TABLE tema1_ps.stoc
ADD CONSTRAINT stoc_ibfk_1 FOREIGN KEY (id_parfumerie) REFERENCES parfumerie(id_parfumerie) ON DELETE CASCADE;
