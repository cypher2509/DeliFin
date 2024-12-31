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

select * from deliveries;

SELECT SUM(payableAmount) AS earnings, 
           SUM(totalDeliveries) AS deliveries
    FROM payslip
    WHERE driverId = "T3799"
      AND YEAR(CURRENT_DATE()) = YEAR(NOW());

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

CALL getDriverYearToDateInfodeliveryvehicle('T3799');

SELECT @earnings AS YearToDateEarnings, @deliveries AS TotalDeliveries;


ALTER TABLE payslip
ADD payableAmount double, add totalDeliveries double , add gasOrBonus double, add insurance double, add deductions double;
delete from payslip;