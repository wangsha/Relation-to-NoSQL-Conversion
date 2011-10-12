-- phpMyAdmin SQL Dump
-- version 3.3.9.2
-- http://www.phpmyadmin.net
--
-- Host: localhost
-- Generation Time: Oct 07, 2011 at 09:38 AM
-- Server version: 5.5.9
-- PHP Version: 5.3.6

SET SQL_MODE="NO_AUTO_VALUE_ON_ZERO";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- Database: `CS4221`
--

-- --------------------------------------------------------

--
-- Table structure for table `course`
--

CREATE TABLE `course` (
  `code` varchar(20) NOT NULL,
  `title` varchar(50) NOT NULL,
  `dept_id` int(11) NOT NULL,
  PRIMARY KEY (`code`),
  KEY `dept_id` (`dept_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `course`
--

INSERT INTO `course` VALUES('CS1010', 'Programming Methodology', 1);
INSERT INTO `course` VALUES('CS1010E', 'Programming Methodology', 2);
INSERT INTO `course` VALUES('CS2103', 'Software Engineering', 1);
INSERT INTO `course` VALUES('CS3240', 'Human-Computer Interaction', 4);
INSERT INTO `course` VALUES('IS1105', 'Strategic IT Applications', 3);
INSERT INTO `course` VALUES('IT1004', 'Introduction to Electronic Commerce', 5);

-- --------------------------------------------------------

--
-- Table structure for table `department`
--

CREATE TABLE `department` (
  `id` int(11) NOT NULL,
  `name` varchar(20) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `department`
--

INSERT INTO `department` VALUES(1, 'Computer Science');
INSERT INTO `department` VALUES(2, 'Computer Engineering');
INSERT INTO `department` VALUES(3, 'Information System');
INSERT INTO `department` VALUES(4, 'Communication and Me');
INSERT INTO `department` VALUES(5, 'Electronic Commerce');

-- --------------------------------------------------------

--
-- Table structure for table `mentor`
--

CREATE TABLE `mentor` (
  `stu_id` int(11) NOT NULL,
  `stf_id` int(11) NOT NULL,
  PRIMARY KEY (`stu_id`,`stf_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `mentor`
--

INSERT INTO `mentor` VALUES(1, 3);
INSERT INTO `mentor` VALUES(2, 1);
INSERT INTO `mentor` VALUES(3, 3);
INSERT INTO `mentor` VALUES(4, 5);
INSERT INTO `mentor` VALUES(5, 4);
INSERT INTO `mentor` VALUES(6, 2);
INSERT INTO `mentor` VALUES(7, 1);
INSERT INTO `mentor` VALUES(8, 2);
INSERT INTO `mentor` VALUES(9, 3);
INSERT INTO `mentor` VALUES(10, 5);
INSERT INTO `mentor` VALUES(11, 5);
INSERT INTO `mentor` VALUES(12, 4);
INSERT INTO `mentor` VALUES(13, 1);
INSERT INTO `mentor` VALUES(14, 3);
INSERT INTO `mentor` VALUES(15, 2);

-- --------------------------------------------------------

--
-- Table structure for table `person`
--

CREATE TABLE `person` (
  `nric` varchar(10) NOT NULL,
  `name` varchar(50) NOT NULL,
  `dob` datetime NOT NULL,
  PRIMARY KEY (`nric`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `person`
--

INSERT INTO `person` VALUES('G1594326A', 'Nicole Chong', '1989-09-12 00:00:00');
INSERT INTO `person` VALUES('G2153327A', 'Giselle Sim', '1990-11-14 00:00:00');
INSERT INTO `person` VALUES('G3342781E', 'Tiffany Chia', '1990-09-27 00:00:00');
INSERT INTO `person` VALUES('G5034328F', 'Lucas Yeo', '1987-09-23 00:00:00');
INSERT INTO `person` VALUES('G5546587C', 'Chloe Ng', '1989-11-26 00:00:00');
INSERT INTO `person` VALUES('G6213226F', 'Ethan Lim', '1986-12-29 00:00:00');
INSERT INTO `person` VALUES('G6543560K', 'Sarah Ong', '1990-01-30 00:00:00');
INSERT INTO `person` VALUES('S1423390A', 'Ashley Ho', '1990-07-07 00:00:00');
INSERT INTO `person` VALUES('S1443287R', 'Megan Wong', '1990-07-05 00:00:00');
INSERT INTO `person` VALUES('S1542586A', 'Isaac Ng', '1959-06-11 00:00:00');
INSERT INTO `person` VALUES('S2238647A', 'Gabrielle Low', '1949-08-09 00:00:00');
INSERT INTO `person` VALUES('S2313201D', 'Eva Tay', '1961-11-26 00:00:00');
INSERT INTO `person` VALUES('S2324131B', 'Jayden Lim', '1988-04-02 00:00:00');
INSERT INTO `person` VALUES('S2354315A', 'Dylan Goh', '1989-10-28 00:00:00');
INSERT INTO `person` VALUES('S2428517H', 'Ryan Chan', '1989-10-29 00:00:00');
INSERT INTO `person` VALUES('S4245260C', 'Cayden Chan', '1989-12-05 00:00:00');
INSERT INTO `person` VALUES('S6325387N', 'Joshua Lee', '1986-10-20 00:00:00');
INSERT INTO `person` VALUES('S6543287A', 'Aidan Tan', '1987-03-12 00:00:00');
INSERT INTO `person` VALUES('S7213564P', 'Zachary Lee', '1967-12-03 00:00:00');
INSERT INTO `person` VALUES('S7643263G', 'Isabelle Tan', '1958-05-17 00:00:00');

-- --------------------------------------------------------

--
-- Table structure for table `staff`
--

CREATE TABLE `staff` (
  `id` int(11) NOT NULL,
  `nric` varchar(10) NOT NULL,
  `dept_id` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `nric` (`nric`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `staff`
--

INSERT INTO `staff` VALUES(1, 'S7643263G', 2);
INSERT INTO `staff` VALUES(2, 'S2313201D', 3);
INSERT INTO `staff` VALUES(3, 'S2238647A', 1);
INSERT INTO `staff` VALUES(4, 'S1542586A', 5);
INSERT INTO `staff` VALUES(5, 'S7213564P', 4);

-- --------------------------------------------------------

--
-- Table structure for table `student`
--

CREATE TABLE `student` (
  `id` int(11) NOT NULL,
  `nric` varchar(10) NOT NULL,
  `dept_id` int(11) NOT NULL,
  `matric_no` varchar(10) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `nric` (`nric`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `student`
--

INSERT INTO `student` VALUES(1, 'S6543287A', 1, 'U076123A');
INSERT INTO `student` VALUES(2, 'S2324131B', 2, 'U084321S');
INSERT INTO `student` VALUES(3, 'S6325387N', 1, 'U072145E');
INSERT INTO `student` VALUES(4, 'G5546587C', 4, 'U092451R');
INSERT INTO `student` VALUES(5, 'G6543560K', 5, 'U097312C');
INSERT INTO `student` VALUES(6, 'S1443287R', 3, 'U096231N');
INSERT INTO `student` VALUES(7, 'S4245260C', 2, 'U082355I');
INSERT INTO `student` VALUES(8, 'G5034328F', 3, 'U073241E');
INSERT INTO `student` VALUES(9, 'G6213226F', 1, 'U071234J');
INSERT INTO `student` VALUES(10, 'S2428517H', 4, 'U087531W');
INSERT INTO `student` VALUES(11, 'G2153327A', 4, 'U098742D');
INSERT INTO `student` VALUES(12, 'G3342781E', 5, 'U094212K');
INSERT INTO `student` VALUES(13, 'S1423390A', 2, 'U091252N');
INSERT INTO `student` VALUES(14, 'S2354315A', 1, 'U084321L');
INSERT INTO `student` VALUES(15, 'G1594326A', 3, 'U087233B');

-- --------------------------------------------------------

--
-- Table structure for table `take`
--

CREATE TABLE `take` (
  `sid` int(11) NOT NULL,
  `cid` varchar(20) NOT NULL,
  `mark` int(11) NOT NULL,
  PRIMARY KEY (`sid`,`cid`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `take`
--

INSERT INTO `take` VALUES(1, 'CS1010', 90);
INSERT INTO `take` VALUES(1, 'CS2103', 80);
INSERT INTO `take` VALUES(2, 'CS1010E', 80);
INSERT INTO `take` VALUES(3, 'CS1010', 70);
INSERT INTO `take` VALUES(3, 'CS2103', 50);
INSERT INTO `take` VALUES(4, 'CS3240', 65);
INSERT INTO `take` VALUES(5, 'IT1004', 75);
INSERT INTO `take` VALUES(6, 'IS1105', 95);
INSERT INTO `take` VALUES(7, 'CS1010E', 85);
INSERT INTO `take` VALUES(8, 'IS1105', 60);
INSERT INTO `take` VALUES(9, 'CS1010', 70);
INSERT INTO `take` VALUES(9, 'CS2103', 95);
INSERT INTO `take` VALUES(10, 'CS3240', 85);
INSERT INTO `take` VALUES(11, 'CS3240', 75);
INSERT INTO `take` VALUES(12, 'IT1004', 95);
INSERT INTO `take` VALUES(13, 'CS1010E', 80);
INSERT INTO `take` VALUES(14, 'CS1010', 80);
INSERT INTO `take` VALUES(14, 'CS2103', 80);
INSERT INTO `take` VALUES(15, 'IS1105', 65);

-- --------------------------------------------------------

--
-- Table structure for table `tutoral_room`
--

CREATE TABLE `tutorial_room` (
  `dept_id` int(11) NOT NULL,
  `room_id` int(11) NOT NULL,
  `capacity` int(11) NOT NULL,
	`location` varchar(20) NOT NULL,
	`year_start_use` datetime NOT NULL,
  PRIMARY KEY (`dept_id`,`room_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `tutoral_room`
--

INSERT INTO `tutorial_room` VALUES(1, 1, 20, 'Science', '1961-11-26 00:00:00');
INSERT INTO `tutorial_room` VALUES(1, 2, 90, 'Science', '1961-11-26 00:00:00');
INSERT INTO `tutorial_room` VALUES(2, 1, 30, 'Engineering', '1961-11-26 00:00:00');
INSERT INTO `tutorial_room` VALUES(2, 2, 60, 'Business', '1963-11-26 00:00:00');
INSERT INTO `tutorial_room` VALUES(3, 1, 40, 'Science', '1964-11-26 00:00:00');
INSERT INTO `tutorial_room` VALUES(3, 2, 30, 'Arts', '1965-11-26 00:00:00');
INSERT INTO `tutorial_room` VALUES(4, 1, 70, 'Science', '1967-11-26 00:00:00');
INSERT INTO `tutorial_room` VALUES(4, 2, 20, 'Computing', '1971-11-26 00:00:00');
INSERT INTO `tutorial_room` VALUES(5, 1, 30, 'Computing', '1971-11-26 00:00:00');
INSERT INTO `tutorial_room` VALUES(5, 2, 40, 'Science', '1987-11-26 00:00:00');

--
-- Constraints for dumped tables
--

--
-- Constraints for table `course`
--
ALTER TABLE `course`
  ADD CONSTRAINT `course_ibfk_1` FOREIGN KEY (`dept_id`) REFERENCES `department` (`id`);

--
-- Constraints for table `staff`
--
ALTER TABLE `staff`
  ADD CONSTRAINT `staff_ibfk_1` FOREIGN KEY (`nric`) REFERENCES `person` (`nric`);

--
-- Constraints for table `student`
--
ALTER TABLE `student`
  ADD CONSTRAINT `student_ibfk_1` FOREIGN KEY (`nric`) REFERENCES `person` (`nric`);

--
-- Constraints for table `tutoral_room`
--
ALTER TABLE `tutoral_room`
  ADD CONSTRAINT `tutoral_room_ibfk_1` FOREIGN KEY (`dept_id`) REFERENCES `department` (`id`);
