use tema3_tp;

-- drop table Client;
CREATE TABLE Client (
    id INT  AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    address VARCHAR(255) NOT NULL,
    email VARCHAR(100) NOT NULL,
    age INT NOT NULL
);

INSERT INTO Client (name, address, email, age) VALUES 
('Alexandru Popescu', 'Str Victoriei', 'alexandrupopescu@yahoo.com', 30),
('Ioana Avramescu', 'Str 23 August', 'ioanaavramescu@yahoo.com', 25),
('Elena Ionescu ', 'Str 14 Octombrie', 'elenaionescu@yahoo.com', 16);

 -- drop table Product
CREATE TABLE Product (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    stock INT NOT NULL,
    price INT NOT NULL
);

INSERT INTO Product (name, stock, price) VALUES 
('Tricou', 20, 130),
('Blugi', 15, 250),
('Hanorac', 10, 270);

-- drop table Orders

CREATE TABLE Orders (
    order_id INT PRIMARY KEY,
    client_id INT,
    product_id INT,
    quantity INT
   
    );

INSERT INTO Orders (order_id, client_id, product_id, quantity) VALUE
(1, 1, 1, 2),
(2, 2, 3, 1),
(3, 3, 2, 3);

-- drop table BILL

CREATE TABLE BILL (
client_id int  PRIMARY KEY,
product_id  int,
quantity int,
price double
);

INSERT INTO Bill (client_id,product_id,quantity,price) VALUE
( 1,1,20,130),
(2,2,15,250),
(3,3,10,270);