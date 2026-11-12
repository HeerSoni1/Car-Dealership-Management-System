-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Nov 12, 2026 at 04:45 PM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.0.30

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `car`
--

DELIMITER $$
--
-- Procedures
--
CREATE  PROCEDURE `GetMonthlySales` ()   BEGIN
    SELECT
        DATE_FORMAT(saledate, '%Y-%m') AS salemonth,
        COUNT(*) AS totalSales,
        SUM(amount) AS totalRevenue
    FROM sales
    GROUP BY DATE_FORMAT(saledate, '%Y-%m')
    ORDER BY salemonth;
END$$

CREATE PROCEDURE `InsertSale` (IN `p_saleid` INT, IN `p_customerid` INT, IN `p_carid` INT, IN `p_amount` DECIMAL(10,2))   BEGIN
    INSERT INTO sales
    (saleid, customerid, carid, amount)
    VALUES
    (p_saleid, p_customerid, p_carid, p_amount);
END$$

DELIMITER ;

-- --------------------------------------------------------

--
-- Table structure for table `admin`
--

CREATE TABLE `admin` (
  `adminid` int(11) NOT NULL,
  `username` varchar(50) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `admin`
--

INSERT INTO `admin` (`adminid`, `username`) VALUES
(1, 'admin1');

-- --------------------------------------------------------

--
-- Table structure for table `car`
--

CREATE TABLE `car` (
  `carid` int(11) NOT NULL,
  `brand` varchar(50) NOT NULL,
  `model` varchar(50) NOT NULL,
  `fuelType` varchar(20) NOT NULL,
  `price` decimal(10,2) NOT NULL,
  `quantity` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `car`
--

INSERT INTO `car` (`carid`, `brand`, `model`, `fuelType`, `price`, `quantity`) VALUES
(1, 'Nissan', 'Magnite XE', 'Petrol', 650000.00, 11),
(2, 'Nissan', 'Magnite XL', 'Petrol', 780000.00, 10),
(3, 'Nissan', 'Magnite XV Turbo', 'Petrol', 950000.00, 6),
(4, 'Nissan', 'Kicks XL', 'Petrol', 1150000.00, 9),
(5, 'Nissan', 'Kicks XV Premium', 'Petrol', 1350000.00, 6),
(6, 'Nissan', 'Sunny XE', 'Petrol', 850000.00, 11),
(7, 'Nissan', 'Sunny XL CVT', 'Petrol', 980000.00, 7),
(8, 'Nissan', 'Altima SV', 'Petrol', 2400000.00, 4),
(9, 'Nissan', 'Altima SL', 'Petrol', 2750000.00, 3),
(10, 'Nissan', 'GT-R Premium', 'Petrol', 18500000.00, 2);

--
-- Triggers `car`
--
DELIMITER $$
CREATE TRIGGER `CheckPriceInsert` BEFORE INSERT ON `car` FOR EACH ROW BEGIN

    IF NEW.price <= 0 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Car price must be greater than zero.';
    END IF;

END
$$
DELIMITER ;
DELIMITER $$
CREATE TRIGGER `CheckPriceUpdate` BEFORE UPDATE ON `car` FOR EACH ROW BEGIN

    IF NEW.price <= 0 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Car price must be greater than zero.';
    END IF;

END
$$
DELIMITER ;
DELIMITER $$
CREATE TRIGGER `CheckQuantity` BEFORE UPDATE ON `car` FOR EACH ROW BEGIN

    IF NEW.quantity < 0 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Quantity cannot be negative.';
    END IF;

END
$$
DELIMITER ;

-- --------------------------------------------------------

--
-- Table structure for table `customer`
--

CREATE TABLE `customer` (
  `customerid` int(11) NOT NULL,
  `name` varchar(50) NOT NULL,
  `phone` varchar(15) NOT NULL,
  `email` varchar(100) NOT NULL,
  `address` varchar(150) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `customer`
--

INSERT INTO `customer` (`customerid`, `name`, `phone`, `email`, `address`) VALUES
(1, 'Rohan Mehta', '9876543210', 'rohan.mehta@gmail.com', '12 MG Road, Ahmedabad'),
(2, 'Priya Sharma', '9823456712', 'priya.sharma@gmail.com', '45 Satellite Road, Ahmedabad'),
(3, 'Amit Patel', '9988776655', 'amit.patel@yahoo.com', '78 SG Highway, Ahmedabad'),
(5, 'Vikram Singh', '9654321098', 'vikram.singh@outlook.com', '56 Connaught Place, Delhi'),
(6, 'Neha Verma', '9543210987', 'neha.verma@gmail.com', '89 Park Street, Kolkata'),
(7, 'Karan Joshi', '9432109876', 'karan.joshi@gmail.com', '34 FC Road, Pune'),
(8, 'Anjali Nair', '9321098765', 'anjali.nair@yahoo.com', '67 MG Road, Bangalore'),
(9, 'Rahul Gupta', '9210987654', 'rahul.gupta@gmail.com', '90 Civil Lines, Jaipur');

-- --------------------------------------------------------

--
-- Table structure for table `payment`
--

CREATE TABLE `payment` (
  `paymentid` int(11) NOT NULL,
  `saleid` int(11) NOT NULL,
  `paymentmode` varchar(20) NOT NULL,
  `paymentstatus` varchar(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `payment`
--

INSERT INTO `payment` (`paymentid`, `saleid`, `paymentmode`, `paymentstatus`) VALUES
(1, 1, 'Cash', 'Paid'),
(3, 3, 'Card', 'Paid'),
(4, 4, 'Cash', 'Paid'),
(8, 8, 'Cash+Card+Loan', 'Paid'),
(9, 9, 'Loan', 'Paid'),
(10, 10, 'Loan+Card+Cash', 'Paid');

-- --------------------------------------------------------

--
-- Table structure for table `sales`
--

CREATE TABLE `sales` (
  `saleid` int(11) NOT NULL,
  `customerid` int(11) NOT NULL,
  `carid` int(11) NOT NULL,
  `amount` decimal(10,2) NOT NULL,
  `saledate` date NOT NULL DEFAULT curdate()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `sales`
--

INSERT INTO `sales` (`saleid`, `customerid`, `carid`, `amount`, `saledate`) VALUES
(1, 1, 1, 650000.00, '2026-07-31'),
(2, 2, 3, 1450000.00, '2026-07-31'),
(3, 3, 4, 1250000.00, '2026-07-31'),
(4, 4, 2, 1550000.00, '2026-07-31'),
(5, 5, 8, 750000.00, '2026-07-31'),
(6, 1, 1, 650000.00, '2026-08-03'),
(7, 6, 10, 700000.00, '2026-08-03'),
(8, 8, 3, 950000.00, '2026-08-03'),
(9, 2, 3, 950000.00, '2026-08-03'),
(10, 5, 1, 650000.00, '2026-08-05'),
(11, 7, 11, 333.00, '2026-08-07');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `admin`
--
ALTER TABLE `admin`
  ADD PRIMARY KEY (`adminid`);

--
-- Indexes for table `car`
--
ALTER TABLE `car`
  ADD PRIMARY KEY (`carid`);

--
-- Indexes for table `customer`
--
ALTER TABLE `customer`
  ADD PRIMARY KEY (`customerid`);

--
-- Indexes for table `payment`
--
ALTER TABLE `payment`
  ADD PRIMARY KEY (`paymentid`);

--
-- Indexes for table `sales`
--
ALTER TABLE `sales`
  ADD PRIMARY KEY (`saleid`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `admin`
--
ALTER TABLE `admin`
  MODIFY `adminid` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT for table `car`
--
ALTER TABLE `car`
  MODIFY `carid` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=13;

--
-- AUTO_INCREMENT for table `customer`
--
ALTER TABLE `customer`
  MODIFY `customerid` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=11;

--
-- AUTO_INCREMENT for table `payment`
--
ALTER TABLE `payment`
  MODIFY `paymentid` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=11;

--
-- AUTO_INCREMENT for table `sales`
--
ALTER TABLE `sales`
  MODIFY `saleid` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=12;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
