use delifin;

CREATE TABLE deliveries (
    id INT AUTO_INCREMENT PRIMARY KEY,
    paySlipId VARCHAR(255),
    day VARCHAR(20) NOT NULL,
    date DATE NOT NULL,
    deliveries INT NOT NULL
);

create table payslip (
	id varchar(50)  primary key,
    weekNumber int,
    driverId varchar(20),
    payableAmount double, 
	totalDeliveries double,
	gasOrBonus double,
	insurance double,
	deductions double
);

create table if not exists driver(
	id varchar(8) primary key,
    firstName varchar(30),
    lastName varchar(30),
    email varchar(320),
    rate_per_delivery float
);
drop table deliveryvehicle;

create table if not exists DeliveryVehicle(
	number varchar(50) primary key,
    make varchar(50),
    model varchar(50),
    make_year year,
	purchase_date date    
);

create table if not exists VehicleMaintenance(
	id int primary key auto_increment,
    number varchar(50),
    cost double,
    type varchar(50),
    date date,
    comments varchar(250),
    FOREIGN KEY (number) REFERENCES DeliveryVehicle(number)
);

select * from deliveryvehicle;

create table if not exists Streets(
	id int primary key auto_Increment,
    name varchar(120) not null,
    area varchar(120) not null,
    street_type varchar(120) not null
);

create table if not exists street(
	id int primary key auto_Increment,
    name varchar(120) not null,
    area varchar(120) not null,
    street_type varchar(120) not null,
    difficulty int
);

insert into street(name, street_type, area, difficulty)
values
("Blackmarsh", "Road", "Mount Pearl", 7),
("Topsail", "Road", "Mount Pearl", 6),
("Topsail", "Road", "St. John's", 6),
("Kenmount", "Road", "St. John's", 8),
("Kelsey", "Drive", "St. John's", 10),
("Pippy", "Place", "St. John's", 8),
("Duffy", "Place", "St. John's", 8),
("Mews", "Place", "St. John's", 8),
("Mullaly", "Street", "St. John's", 8),
("Austin", "Street", "St. John's", 8),
("Hallet", "Crescent", "St. John's", 8),
("Thorburn", "Road", "St. John's", 6),
("Crosbie", "Road", "St. John's", 10),
("Portugal Cove", "Road", "St. John's", 10),
("Kenmount", "Road", "St. John's", 8),
("Torbay", "Road", "St. John's", 7),
("Penney", "Lane", "St. John's", 6),
("Marine", "Drive", "Torbay", 8),
("Topsail", "Road", "Conception Bay South", 7),
("Seal Cove", "Road", "Conception Bay South", 10),
("Duckworth", "Street", "St. John's", 15),
("Water", "Street", "St. John's", 15),
("Charter", "Avenue", "St. John's", 10),
("Veterans", "Road", "St. John's", 13),
("Country Path", "Drive", "Witless Bay", 10);



delete from Streets;

INSERT INTO deliveries ( paySlipId, day, date, deliveries, pricePerDelivery, amount) VALUES
( 3, 'Friday', '2024-12-13', 200, 1.4, 550.00),
( 3, 'Saturday', '2024-12-14', 170, 1.4, 408.00),
( 3, 'Sunday', '2024-12-15', 90, 1.4, 189.00);

select * from payslip;

insert into payslip (driverName, weekNumber, driverId, totalAmount) values
("Harsh" , 40, "460214", 2500);

DELETE FROM driver WHERE firstName = 'Harsh';

insert into driver( id, firstName, lastName, email) values
('460214' , 'Harsh' , 'Khakh', 'lokeshpanchal.2002a@gmail.com');

select * from driver;

SELECT 
    SUM(d.deliveries) AS total_deliveries,
    SUM(d.deliveries * dr.rate_per_delivery) AS amount_paid
FROM 
    deliveries d
JOIN 
    paySlip ps ON d.paySlipId = ps.id
JOIN 
    driver dr ON ps.driverId = dr.id
WHERE 
    dr.id = 'T3799' 
    AND MONTH(d.date) = 11;

drop procedure getDriverYearToDateInfo;

DELIMITER $$

CREATE PROCEDURE getDriverYearToDateInfo(IN driverIdInput VARCHAR(20))
BEGIN
    -- Calculate the total earnings from the beginning of the current year to now
    SELECT CAST(SUM(payableAmount) AS DOUBLE) AS earnings, 
           SUM(totalDeliveries) AS deliveries
    FROM payslip
    WHERE driverId = driverIdInput
      AND YEAR(CURRENT_DATE()) = YEAR(NOW());
      
END$$
DELIMITER ;

ALTER TABLE payslip
ADD payableAmount double, add totalDeliveries double , add gasOrBonus double, add insurance double, add deductions double;
delete from payslip;