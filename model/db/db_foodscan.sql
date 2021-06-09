-- phpMyAdmin SQL Dump
-- version 5.0.3
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Jun 03, 2021 at 08:37 AM
-- Server version: 10.4.14-MariaDB
-- PHP Version: 7.2.34

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `db_foodscan`
--

-- --------------------------------------------------------

--
-- Table structure for table `foodscan_data`
--

CREATE TABLE `foodscan_data` (
  `USER_ID` int(11) NOT NULL,
  `FOOD_ID` varchar(40) NOT NULL,
  `FOOD_NAME` varchar(40) NOT NULL,
  `NUTRITION` int(11) NOT NULL,
  `TIME` datetime DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `foodscan_data`
--

INSERT INTO `foodscan_data` (`USER_ID`, `FOOD_ID`, `FOOD_NAME`, `NUTRITION`, `TIME`) VALUES
(0, 'food_id', 'food_name', 0, '0000-00-00 00:00:00'),
(1, 'MI_1', 'air', 0, NULL),
(2, 'BU_1', 'anggur', 30, NULL),
(3, 'BU_2', 'apel', 58, NULL),
(4, 'MA_1', 'ayam', 75, NULL),
(5, 'MA_2', 'bakso', 202, NULL),
(6, 'MA_3', 'bakwan', 280, NULL),
(7, 'MA_4', 'batagor', 327, NULL),
(8, 'MA_5', 'bubur', 91, NULL),
(9, 'MA_6', 'burger', 258, NULL),
(10, 'MA_7', 'cakwe', 50, NULL),
(11, 'MA_8', 'capcay', 97, NULL),
(12, 'MA_9', 'crepes', 95, NULL),
(13, 'MA_10', 'cumi', 75, NULL),
(14, 'MA_11', 'donat', 192, NULL),
(15, 'MA_12', 'durian', 134, NULL),
(16, 'MA_13', 'es_krim', 210, NULL),
(17, 'MA_14', 'fu_yung_hai', 227, NULL),
(18, 'MA_15', 'gudeg', 160, NULL),
(19, 'MA_16', 'ikan', 205, NULL),
(20, 'BU_3', 'jeruk', 48, NULL),
(21, 'MA_17', 'kacang', 330, NULL),
(22, 'MA_18', 'kebab', 200, NULL),
(23, 'MA_19', 'kentang', 62, NULL),
(24, 'MA_20', 'kerupuk', 15, NULL),
(25, 'MI_2', 'kopi', 129, NULL);

--
-- Indexes for dumped tables
--

--
-- Indexes for table `foodscan_data`
--
ALTER TABLE `foodscan_data`
  ADD PRIMARY KEY (`USER_ID`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
