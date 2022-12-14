DROP DATABASE IF EXISTS postgres;

CREATE DATABASE postgres;

\C DATABASE postgres;


DROP TABLE IF EXISTS role;
CREATE TABLE IF NOT EXISTS role
(
    id   SERIAL PRIMARY KEY,
    name VARCHAR(36) NOT NULL UNIQUE
    );


DROP TABLE IF EXISTS users;
CREATE TABLE IF NOT EXISTS users
(
    id       SERIAL PRIMARY KEY,
    login    VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(32)  NOT NULL,
    role_id  int REFERENCES role (id) ON DELETE CASCADE
                                      ON UPDATE CASCADE
    NOT NULL DEFAULT 1
    );


DROP TABLE IF EXISTS status;
CREATE TABLE IF NOT EXISTS status
(
    id   SERIAL PRIMARY KEY,
    name VARCHAR(32) NOT NULL
    );


DROP TABLE IF EXISTS category;
CREATE TABLE IF NOT EXISTS category
(
    id   SERIAL PRIMARY KEY,
    name VARCHAR(20) NOT NULL UNIQUE
    );

DROP TABLE IF EXISTS dish;
CREATE TABLE IF NOT EXISTS dish
(
    id          SERIAL PRIMARY KEY,
    name        VARCHAR(228) NOT NULL UNIQUE ,
    prise       INT          NOT NULL,
    weight      INT          NOT NULL,
    category_id INT NOT NULL REFERENCES category(id) ON DELETE CASCADE
                                                     ON UPDATE CASCADE ,
    description VARCHAR (500) NOT NULL,
    CHECK (prise>0),
    CHECK ( weight>0 )
    );


DROP TABLE IF EXISTS receipt;
CREATE TABLE IF NOT EXISTS receipt
(
    id          SERIAL PRIMARY KEY,
    user_id     INT REFERENCES users (id) ON DELETE CASCADE
                                          ON UPDATE CASCADE,
    status_id   INT REFERENCES status (id) ON DELETE CASCADE
                                          ON UPDATE CASCADE,
    create_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
    );

DROP TABLE IF EXISTS receipt_has_dish;
CREATE TABLE IF NOT EXISTS receipt_has_dish
(
    receipt_id INT REFERENCES receipt (id) ON DELETE CASCADE
                                           ON UPDATE CASCADE,
    dish_id    INT REFERENCES dish (id) ON DELETE CASCADE
                                           ON UPDATE CASCADE,
    amount     INT NOT NULL DEFAULT 1,
    CHECK (amount > 0)
    );

DROP TABLE IF EXISTS cart;
CREATE TABLE IF NOT EXISTS cart
(
    user_id INT NOT NULL REFERENCES users (id) ON DELETE CASCADE
                                               ON UPDATE CASCADE
    ,
    dish_id INT NOT NULL REFERENCES dish (id) ON DELETE CASCADE
                                               ON UPDATE CASCADE
    ,
    amount  INT NOT NULL DEFAULT 1
    );

INSERT INTO role(name)
VALUES ('client'),
       ('manager');


INSERT INTO users (login, password, role_id)
VALUES ('admin', 'admin', 2),
       ('user', 'user', 1),
       ('client', 'client', 1);



INSERT INTO status (name)
VALUES ('new'),
       ('approved'),
       ('cancelled'),
       ('cooking'),
       ('delivering'),
       ('received');


INSERT INTO category(name)
VALUES ('Pizza'),
       ('Sushi'),
       ('Burger'),
       ('Drink'),
       ('Salad'),
       ('Dessert');


DROP TABLE dish CASCADE;

INSERT INTO dish(name, prise, weight, description, category_id)
VALUES ('Orange Juice', 3, 120, 'Fresh and so healthy juice', 4),
       ('Lemon Juice', 4, 120, 'Fresh and so healthy juice', 4),
       ('Peach juice', 2, 120, 'Fresh and so healthy juice', 4),
       ('Apple Juice', 1, 120, 'Fresh and so healthy juice', 4),

       ('Burger Beef', 7, 320, 'Delicious burger with beef', 3),
       ('Burger Beef Original', 2, 240, 'Delicious burger', 3),
       ('Burger Beef with Onion', 13, 440, 'Delicious burger with Onion', 3),
       ('Burger Vegan', 8, 180, 'Delicious burger with tomato', 3),

       ('Sashimi', 2, 90, 'Ice cream with berries', 2),
       ('Nigiri', 1, 180, 'So testy free', 2),
       ('Chirashi', 1, 200, 'Jelly with different flavors', 2),
       ('Uramaki', 1, 180, 'So testy free', 2),

       ('Neapolitan Pizza', 10, 420, 'Delicious Neapolitan Pizza', 1),
       ('Chicago Pizza', 10, 420, 'Delicious Chicago Pizza', 1),
       ('New York-Style Pizza', 10, 420, 'New York-Style Pizza', 1),
       ('Sicilian Pizza', 10, 420, 'Sicilian Pizza', 1),

       ('Caesar Salad Supreme', 12, 330, 'Delicious Caesar Salad Supreme', 5),
       ('Greek Salad', 12, 330, 'Delicious Greek Salad', 5),

       ('Cookies', 2, 120, 'Delicious Cookies', 6),
       ('Biscuits', 2, 120, 'Delicious Biscuits', 6),
       ('Frozen', 2, 120, 'Delicious Frozen', 6);



INSERT INTO receipt(user_id)
VALUES (2),
       (3);


INSERT INTO cart(user_id, dish_id, amount)
VALUES (2, 3, 3),
       (1, 4, 1),
       (5, 8, 2);


INSERT INTO receipt_has_dish(receipt_id, dish_id, amount)
VALUES (1, 2, 2),
       (1, 3, 1),
       (2, 5, 1),
       (2, 6, 3);







