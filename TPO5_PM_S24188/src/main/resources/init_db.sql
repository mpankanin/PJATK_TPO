CREATE TABLE IF NOT EXISTS VEHICLES (
                                        ID INT AUTO_INCREMENT PRIMARY KEY,
                                        TYPE VARCHAR(255),
                                        BRAND VARCHAR(255),
                                        MODEL VARCHAR(255),
                                        YEAR INT,
                                        CONSUMPTION DECIMAL(5,2)
);

INSERT INTO VEHICLES (TYPE, BRAND, MODEL, YEAR, CONSUMPTION) VALUES
                                                                 ('car', 'Toyota', 'Corolla', 2010, 7.0),
                                                                 ('car', 'Honda', 'Civic', 2012, 6.5),
                                                                 ('truck', 'Ford', 'F-150', 2015, 15.0),
                                                                 ('f1', 'Ferrari', 'SF90', 2021, 30.0),
                                                                 ('pickup', 'Chevrolet', 'Silverado', 2018, 12.0),
                                                                 ('car', 'BMW', '3 Series', 2019, 8.0),
                                                                 ('truck', 'Ram', '1500', 2020, 14.0),
                                                                 ('f1', 'Mercedes', 'W12', 2021, 30.0),
                                                                 ('pickup', 'GMC', 'Sierra', 2017, 13.0),
                                                                 ('car', 'Audi', 'A4', 2020, 7.5);