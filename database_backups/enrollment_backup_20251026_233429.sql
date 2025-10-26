-- MySQL dump 10.13  Distrib 8.0.43, for Win64 (x86_64)
--
-- Host: sql12.freesqldatabase.com    Database: sql12804580
-- ------------------------------------------------------
-- Server version	5.5.62-0ubuntu0.14.04.1

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `academic_year`
--

DROP TABLE IF EXISTS `academic_year`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `academic_year` (
  `academic_year_id` varchar(20) NOT NULL,
  `year_label` varchar(20) DEFAULT NULL,
  `start_date` date DEFAULT NULL,
  `end_date` date DEFAULT NULL,
  `is_active` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`academic_year_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `academic_year`
--

LOCK TABLES `academic_year` WRITE;
/*!40000 ALTER TABLE `academic_year` DISABLE KEYS */;
/*!40000 ALTER TABLE `academic_year` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `admin`
--

DROP TABLE IF EXISTS `admin`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `admin` (
  `admin_id` varchar(20) NOT NULL,
  `user_id` int(11) NOT NULL,
  `first_name` varchar(100) DEFAULT NULL,
  `last_name` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`admin_id`),
  KEY `admin_ibfk_1` (`user_id`),
  CONSTRAINT `admin_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `admin`
--

LOCK TABLES `admin` WRITE;
/*!40000 ALTER TABLE `admin` DISABLE KEYS */;
INSERT INTO `admin` VALUES ('ADM25-1415',6,'Alice','Santos');
/*!40000 ALTER TABLE `admin` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `card_payment`
--

DROP TABLE IF EXISTS `card_payment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `card_payment` (
  `payment_id` int(11) NOT NULL,
  `card_type` enum('Visa','Mastercard','Amex','Debit') NOT NULL,
  `card_last_four` varchar(4) DEFAULT NULL,
  `card_holder_name` varchar(100) DEFAULT NULL,
  `approval_code` varchar(50) DEFAULT NULL,
  `transaction_id` varchar(100) DEFAULT NULL,
  `bank_name` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`payment_id`),
  CONSTRAINT `card_payment_ibfk_1` FOREIGN KEY (`payment_id`) REFERENCES `payment` (`payment_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `card_payment`
--

LOCK TABLES `card_payment` WRITE;
/*!40000 ALTER TABLE `card_payment` DISABLE KEYS */;
/*!40000 ALTER TABLE `card_payment` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `cash_payment`
--

DROP TABLE IF EXISTS `cash_payment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cash_payment` (
  `payment_id` int(11) NOT NULL,
  `amount_tendered` decimal(11,2) DEFAULT NULL,
  `change_amount` decimal(11,2) DEFAULT NULL,
  PRIMARY KEY (`payment_id`),
  CONSTRAINT `cash_payment_ibfk_1` FOREIGN KEY (`payment_id`) REFERENCES `payment` (`payment_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cash_payment`
--

LOCK TABLES `cash_payment` WRITE;
/*!40000 ALTER TABLE `cash_payment` DISABLE KEYS */;
/*!40000 ALTER TABLE `cash_payment` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `cashier`
--

DROP TABLE IF EXISTS `cashier`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cashier` (
  `cashier_id` varchar(20) NOT NULL,
  `user_id` int(11) DEFAULT NULL,
  `first_name` varchar(100) DEFAULT NULL,
  `last_name` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`cashier_id`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `cashier_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cashier`
--

LOCK TABLES `cashier` WRITE;
/*!40000 ALTER TABLE `cashier` DISABLE KEYS */;
/*!40000 ALTER TABLE `cashier` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `course_offerings`
--

DROP TABLE IF EXISTS `course_offerings`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `course_offerings` (
  `offering_id` varchar(20) NOT NULL,
  `course_id` varchar(20) DEFAULT NULL,
  `faculty_id` varchar(20) DEFAULT NULL,
  `semester_id` varchar(20) DEFAULT NULL,
  `section` varchar(10) DEFAULT NULL,
  `capacity` int(11) DEFAULT NULL,
  `enrolled_count` int(11) DEFAULT '0',
  `schedule_day` varchar(50) DEFAULT NULL,
  `schedule_time` varchar(50) DEFAULT NULL,
  `room` varchar(50) DEFAULT NULL,
  `status` enum('Open','Closed','Cancelled') DEFAULT 'Open',
  PRIMARY KEY (`offering_id`),
  KEY `faculty_id` (`faculty_id`),
  KEY `idx_course_offerings_semester` (`semester_id`),
  KEY `idx_course_offerings_course` (`course_id`),
  CONSTRAINT `course_offerings_ibfk_1` FOREIGN KEY (`course_id`) REFERENCES `courses` (`course_id`) ON DELETE CASCADE,
  CONSTRAINT `course_offerings_ibfk_2` FOREIGN KEY (`faculty_id`) REFERENCES `faculty` (`faculty_id`) ON DELETE SET NULL,
  CONSTRAINT `course_offerings_ibfk_3` FOREIGN KEY (`semester_id`) REFERENCES `semester` (`semester_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `course_offerings`
--

LOCK TABLES `course_offerings` WRITE;
/*!40000 ALTER TABLE `course_offerings` DISABLE KEYS */;
/*!40000 ALTER TABLE `course_offerings` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `courses`
--

DROP TABLE IF EXISTS `courses`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `courses` (
  `course_id` varchar(20) NOT NULL,
  `course_code` varchar(50) DEFAULT NULL,
  `course_title` varchar(255) DEFAULT NULL,
  `course_units` decimal(4,1) DEFAULT NULL,
  `program_id` varchar(20) DEFAULT NULL,
  `year_level` tinyint(1) DEFAULT NULL,
  `semester` enum('1st','2nd') DEFAULT NULL,
  PRIMARY KEY (`course_id`),
  KEY `program_id` (`program_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `courses`
--

LOCK TABLES `courses` WRITE;
/*!40000 ALTER TABLE `courses` DISABLE KEYS */;
INSERT INTO `courses` VALUES ('IT101','IT101','Introduction to Computing',3.0,'PROG-01',1,'1st'),('IT102','IT102','Computer Programming 1',3.0,'PROG-01',1,'1st'),('IT103','IT103','Computer Programming 2',3.0,'PROG-01',1,'2nd'),('IT104','IT104','Computer Hardware Fundamentals',3.0,'PROG-01',1,'1st'),('IT105','IT105','Mathematics in the Modern World',3.0,'PROG-01',1,'1st'),('IT106','IT106','Purposive Communication',3.0,'PROG-01',1,'1st'),('IT107','IT107','Understanding the Self',3.0,'PROG-01',1,'2nd'),('IT108','IT108','Programming Logic and Design',3.0,'PROG-01',1,'2nd'),('IT109','IT109','Physical Education 1',2.0,'PROG-01',1,'1st'),('IT110','IT110','Physical Education 2',2.0,'PROG-01',1,'2nd'),('IT111','IT111','NSTP 1',3.0,'PROG-01',1,'1st'),('IT112','IT112','NSTP 2',3.0,'PROG-01',1,'2nd'),('IT113','IT113','Introduction to Human Computer Interaction',3.0,'PROG-01',1,'2nd'),('IT114','IT114','Ethics',3.0,'PROG-01',1,'2nd'),('IT115','IT115','Science, Technology and Society',3.0,'PROG-01',1,'2nd'),('IT116','IT116','Introduction to Networking',3.0,'PROG-01',1,'2nd'),('IT201','IT201','Data Structures and Algorithms',3.0,'PROG-01',2,'1st'),('IT202','IT202','Database Management Systems',3.0,'PROG-01',2,'1st'),('IT203','IT203','Object-Oriented Programming',3.0,'PROG-01',2,'1st'),('IT204','IT204','Operating Systems',3.0,'PROG-01',2,'2nd'),('IT205','IT205','Networking 1 (LAN Technologies)',3.0,'PROG-01',2,'1st'),('IT206','IT206','Networking 2 (WAN Technologies)',3.0,'PROG-01',2,'2nd'),('IT207','IT207','IT Elective 1 (Web Development)',3.0,'PROG-01',2,'2nd'),('IT208','IT208','Discrete Mathematics',3.0,'PROG-01',2,'1st'),('IT209','IT209','Data Communications',3.0,'PROG-01',2,'2nd'),('IT210','IT210','PE 3',2.0,'PROG-01',2,'1st'),('IT211','IT211','PE 4',2.0,'PROG-01',2,'2nd'),('IT212','IT212','Professional Ethics',3.0,'PROG-01',2,'1st'),('IT213','IT213','IT Project Management',3.0,'PROG-01',2,'2nd'),('IT214','IT214','Advanced Database Systems',3.0,'PROG-01',2,'2nd'),('IT215','IT215','Web Systems and Technologies 1',3.0,'PROG-01',2,'1st'),('IT216','IT216','Web Systems and Technologies 2',3.0,'PROG-01',2,'2nd'),('IT301','IT301','Software Engineering 1',3.0,'PROG-01',3,'1st'),('IT302','IT302','Software Engineering 2',3.0,'PROG-01',3,'2nd'),('IT303','IT303','System Integration and Architecture',3.0,'PROG-01',3,'2nd'),('IT304','IT304','Mobile Application Development',3.0,'PROG-01',3,'1st'),('IT305','IT305','Human-Computer Interaction',3.0,'PROG-01',3,'2nd'),('IT306','IT306','Information Assurance and Security 1',3.0,'PROG-01',3,'1st'),('IT307','IT307','Information Assurance and Security 2',3.0,'PROG-01',3,'2nd'),('IT308','IT308','Technopreneurship',3.0,'PROG-01',3,'1st'),('IT309','IT309','IT Elective 2 (Cloud Computing)',3.0,'PROG-01',3,'1st'),('IT310','IT310','IT Elective 3 (Game Development)',3.0,'PROG-01',3,'2nd'),('IT311','IT311','Research Methods in IT',3.0,'PROG-01',3,'2nd'),('IT312','IT312','Free Elective 1',3.0,'PROG-01',3,'2nd'),('IT313','IT313','Free Elective 2',3.0,'PROG-01',3,'1st'),('IT314','IT314','IT Practicum Orientation',1.0,'PROG-01',3,'2nd'),('IT315','IT315','IT Seminar 1',1.0,'PROG-01',3,'2nd'),('IT316','IT316','Web Hosting and Deployment',3.0,'PROG-01',3,'1st'),('IT401','IT401','Capstone Project 1',3.0,'PROG-01',4,'1st'),('IT402','IT402','Capstone Project 2',3.0,'PROG-01',4,'2nd'),('IT403','IT403','System Administration and Maintenance',3.0,'PROG-01',4,'1st'),('IT404','IT404','IT Seminar 2',1.0,'PROG-01',4,'2nd'),('IT405','IT405','IT Practicum / OJT',6.0,'PROG-01',4,'2nd'),('IT406','IT406','Emerging Technologies',3.0,'PROG-01',4,'1st'),('IT407','IT407','IT Elective 4 (Cybersecurity)',3.0,'PROG-01',4,'2nd'),('IT408','IT408','IT Elective 5 (AI Basics)',3.0,'PROG-01',4,'1st'),('IT409','IT409','IT Research Colloquium',3.0,'PROG-01',4,'2nd'),('IT410','IT410','Free Elective 3',3.0,'PROG-01',4,'1st'),('IT411','IT411','Free Elective 4',3.0,'PROG-01',4,'2nd'),('IT412','IT412','IT in the Modern Enterprise',3.0,'PROG-01',4,'1st'),('IT413','IT413','Cloud Security',3.0,'PROG-01',4,'2nd'),('IT414','IT414','Server Management',3.0,'PROG-01',4,'2nd'),('IT415','IT415','Digital Ethics',3.0,'PROG-01',4,'1st'),('IT416','IT416','Data Analytics',3.0,'PROG-01',4,'2nd');
/*!40000 ALTER TABLE `courses` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `enrollees`
--

DROP TABLE IF EXISTS `enrollees`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `enrollees` (
  `enrollee_id` varchar(20) NOT NULL,
  `user_id` int(11) NOT NULL,
  `first_name` varchar(100) DEFAULT NULL,
  `middle_name` varchar(100) DEFAULT NULL,
  `last_name` varchar(100) DEFAULT NULL,
  `suffix` varchar(20) DEFAULT NULL,
  `birth_date` date DEFAULT NULL,
  `gender` varchar(10) DEFAULT NULL,
  `address` varchar(255) DEFAULT NULL,
  `province` varchar(100) DEFAULT NULL,
  `city` varchar(100) DEFAULT NULL,
  `contact_number` varchar(20) DEFAULT NULL,
  `email_address` varchar(191) DEFAULT NULL,
  `guardian_name` varchar(100) DEFAULT NULL,
  `guardian_contact` varchar(20) DEFAULT NULL,
  `year_level` varchar(20) DEFAULT NULL,
  `student_type` varchar(50) DEFAULT NULL,
  `last_school_attended` varchar(255) DEFAULT NULL,
  `school_year_to_enroll` varchar(20) DEFAULT NULL,
  `program_applied_for` varchar(20) DEFAULT NULL,
  `enrollment_status` enum('Pending','Approved','Rejected','Enrolled') DEFAULT 'Pending',
  `date_applied` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `reviewed_by` int(11) DEFAULT NULL,
  `reviewed_on` timestamp NULL DEFAULT NULL,
  `has_filled_up_form` tinyint(1) DEFAULT '0',
  `photo_link` varchar(500) DEFAULT NULL,
  `birth_cert_link` varchar(500) DEFAULT NULL,
  `report_card_link` varchar(500) DEFAULT NULL,
  `form_137_link` varchar(500) DEFAULT NULL,
  PRIMARY KEY (`enrollee_id`),
  KEY `reviewed_by` (`reviewed_by`),
  KEY `idx_enrollees_user` (`user_id`),
  KEY `idx_enrollees_program` (`program_applied_for`),
  KEY `idx_enrollees_status` (`enrollment_status`),
  CONSTRAINT `enrollees_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE,
  CONSTRAINT `enrollees_ibfk_3` FOREIGN KEY (`reviewed_by`) REFERENCES `admin` (`user_id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `enrollees`
--

LOCK TABLES `enrollees` WRITE;
/*!40000 ALTER TABLE `enrollees` DISABLE KEYS */;
INSERT INTO `enrollees` VALUES ('SA25-2111',1,'Joshua','Simugan','Santiago',NULL,'2006-09-21',NULL,'#002, Zone 1, Sta. Lucia','Nueva Ecija','Guimba','09603376884','joshuasantiago0921@gmail.com',NULL,NULL,'1st Year','New','Bartolome Sangalang National High School','2016-2022','BSIT','Pending','2025-10-27 06:14:58',NULL,NULL,1,'https://drive.google.com/file/d/1ymjB6ixfU5o52LLoDM_7vwHktStSqNu4/view?usp=drivesdk','https://drive.google.com/file/d/1Dbfdio9BIl3FB_g7npLB86ivj2S1HRCx/view?usp=drivesdk','https://drive.google.com/file/d/1h4E78QPnoiZO9U7ijueIbpkTVqgN9CWX/view?usp=drivesdk','https://drive.google.com/file/d/1U8YM0Wi1yMTVywqjtoSluoR-EKtiSedD/view?usp=drivesdk');
/*!40000 ALTER TABLE `enrollees` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `enrollments`
--

DROP TABLE IF EXISTS `enrollments`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `enrollments` (
  `enrollment_id` varchar(20) NOT NULL,
  `student_id` varchar(20) DEFAULT NULL,
  `offering_id` varchar(20) DEFAULT NULL,
  `enrollment_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `grade` varchar(10) DEFAULT NULL,
  `status` enum('Enrolled','Dropped','Completed','Failed') DEFAULT 'Enrolled',
  PRIMARY KEY (`enrollment_id`),
  KEY `idx_enrollments_student` (`student_id`),
  KEY `idx_enrollments_offering` (`offering_id`),
  CONSTRAINT `enrollments_ibfk_1` FOREIGN KEY (`student_id`) REFERENCES `students` (`student_id`) ON DELETE CASCADE,
  CONSTRAINT `enrollments_ibfk_2` FOREIGN KEY (`offering_id`) REFERENCES `course_offerings` (`offering_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `enrollments`
--

LOCK TABLES `enrollments` WRITE;
/*!40000 ALTER TABLE `enrollments` DISABLE KEYS */;
/*!40000 ALTER TABLE `enrollments` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `faculty`
--

DROP TABLE IF EXISTS `faculty`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `faculty` (
  `faculty_id` varchar(20) NOT NULL,
  `user_id` int(11) DEFAULT NULL,
  `first_name` varchar(100) DEFAULT NULL,
  `last_name` varchar(100) DEFAULT NULL,
  `department` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`faculty_id`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `faculty_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `faculty`
--

LOCK TABLES `faculty` WRITE;
/*!40000 ALTER TABLE `faculty` DISABLE KEYS */;
/*!40000 ALTER TABLE `faculty` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `full_tuition_payment`
--

DROP TABLE IF EXISTS `full_tuition_payment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `full_tuition_payment` (
  `payment_id` int(11) NOT NULL DEFAULT '0',
  `student_id` varchar(20) DEFAULT NULL,
  `enrollee_id` varchar(20) DEFAULT NULL,
  `total_tuition` decimal(11,2) DEFAULT NULL,
  `semester_id` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`payment_id`),
  KEY `student_id` (`student_id`),
  KEY `enrollee_id` (`enrollee_id`),
  KEY `semester_id` (`semester_id`),
  CONSTRAINT `full_tuition_payment_ibfk_1` FOREIGN KEY (`payment_id`) REFERENCES `payment` (`payment_id`) ON DELETE CASCADE,
  CONSTRAINT `full_tuition_payment_ibfk_2` FOREIGN KEY (`student_id`) REFERENCES `students` (`student_id`) ON DELETE SET NULL,
  CONSTRAINT `full_tuition_payment_ibfk_3` FOREIGN KEY (`enrollee_id`) REFERENCES `enrollees` (`enrollee_id`) ON DELETE SET NULL,
  CONSTRAINT `full_tuition_payment_ibfk_4` FOREIGN KEY (`semester_id`) REFERENCES `semester` (`semester_id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `full_tuition_payment`
--

LOCK TABLES `full_tuition_payment` WRITE;
/*!40000 ALTER TABLE `full_tuition_payment` DISABLE KEYS */;
/*!40000 ALTER TABLE `full_tuition_payment` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `gcash_payment`
--

DROP TABLE IF EXISTS `gcash_payment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `gcash_payment` (
  `payment_id` int(11) NOT NULL,
  `reference_number` varchar(100) NOT NULL,
  `sender_name` varchar(100) DEFAULT NULL,
  `sender_number` varchar(20) DEFAULT NULL,
  `transaction_id` varchar(100) DEFAULT NULL,
  `screenshot_link` varchar(500) DEFAULT NULL,
  `verification_status` enum('Pending','Verified','Failed') DEFAULT 'Pending',
  `verified_by` varchar(20) DEFAULT NULL,
  `verified_on` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`payment_id`),
  KEY `verified_by` (`verified_by`),
  KEY `idx_gcash_payment_status` (`verification_status`),
  KEY `idx_gcash_payment_reference` (`reference_number`),
  CONSTRAINT `gcash_payment_ibfk_1` FOREIGN KEY (`payment_id`) REFERENCES `payment` (`payment_id`) ON DELETE CASCADE,
  CONSTRAINT `gcash_payment_ibfk_2` FOREIGN KEY (`verified_by`) REFERENCES `cashier` (`cashier_id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `gcash_payment`
--

LOCK TABLES `gcash_payment` WRITE;
/*!40000 ALTER TABLE `gcash_payment` DISABLE KEYS */;
/*!40000 ALTER TABLE `gcash_payment` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `invoice`
--

DROP TABLE IF EXISTS `invoice`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `invoice` (
  `invoice_id` int(11) NOT NULL AUTO_INCREMENT,
  `payment_id` int(11) DEFAULT NULL,
  `student_id` varchar(20) DEFAULT NULL,
  `enrollee_id` varchar(20) DEFAULT NULL,
  `invoice_number` varchar(50) DEFAULT NULL,
  `invoice_date` date DEFAULT NULL,
  `total_amount` decimal(11,2) DEFAULT NULL,
  `remarks` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`invoice_id`),
  UNIQUE KEY `invoice_number` (`invoice_number`),
  KEY `payment_id` (`payment_id`),
  KEY `idx_invoice_student` (`student_id`),
  KEY `idx_invoice_enrollee` (`enrollee_id`),
  CONSTRAINT `invoice_ibfk_1` FOREIGN KEY (`payment_id`) REFERENCES `payment` (`payment_id`) ON DELETE CASCADE,
  CONSTRAINT `invoice_ibfk_2` FOREIGN KEY (`student_id`) REFERENCES `students` (`student_id`) ON DELETE SET NULL,
  CONSTRAINT `invoice_ibfk_3` FOREIGN KEY (`enrollee_id`) REFERENCES `enrollees` (`enrollee_id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `invoice`
--

LOCK TABLES `invoice` WRITE;
/*!40000 ALTER TABLE `invoice` DISABLE KEYS */;
/*!40000 ALTER TABLE `invoice` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `misc_payment`
--

DROP TABLE IF EXISTS `misc_payment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `misc_payment` (
  `payment_id` int(11) NOT NULL DEFAULT '0',
  `name` varchar(100) DEFAULT NULL,
  `amount` decimal(11,2) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`payment_id`),
  CONSTRAINT `misc_payment_ibfk_1` FOREIGN KEY (`payment_id`) REFERENCES `payment` (`payment_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `misc_payment`
--

LOCK TABLES `misc_payment` WRITE;
/*!40000 ALTER TABLE `misc_payment` DISABLE KEYS */;
/*!40000 ALTER TABLE `misc_payment` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `other_payment`
--

DROP TABLE IF EXISTS `other_payment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `other_payment` (
  `payment_id` int(11) NOT NULL DEFAULT '0',
  `name` varchar(100) DEFAULT NULL,
  `amount` decimal(11,2) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`payment_id`),
  CONSTRAINT `other_payment_ibfk_1` FOREIGN KEY (`payment_id`) REFERENCES `payment` (`payment_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `other_payment`
--

LOCK TABLES `other_payment` WRITE;
/*!40000 ALTER TABLE `other_payment` DISABLE KEYS */;
/*!40000 ALTER TABLE `other_payment` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `payment`
--

DROP TABLE IF EXISTS `payment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `payment` (
  `payment_id` int(11) NOT NULL AUTO_INCREMENT,
  `cashier_id` varchar(20) DEFAULT NULL,
  `student_id` varchar(20) DEFAULT NULL,
  `enrollee_id` varchar(20) DEFAULT NULL,
  `amount` decimal(11,2) DEFAULT NULL,
  `payment_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `payment_type` enum('Cash','Card','GCash') NOT NULL,
  `remarks` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`payment_id`),
  KEY `cashier_id` (`cashier_id`),
  KEY `idx_payment_student` (`student_id`),
  KEY `idx_payment_enrollee` (`enrollee_id`),
  KEY `idx_payment_type` (`payment_type`),
  CONSTRAINT `payment_ibfk_1` FOREIGN KEY (`cashier_id`) REFERENCES `cashier` (`cashier_id`) ON DELETE SET NULL,
  CONSTRAINT `payment_ibfk_2` FOREIGN KEY (`student_id`) REFERENCES `students` (`student_id`) ON DELETE SET NULL,
  CONSTRAINT `payment_ibfk_3` FOREIGN KEY (`enrollee_id`) REFERENCES `enrollees` (`enrollee_id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `payment`
--

LOCK TABLES `payment` WRITE;
/*!40000 ALTER TABLE `payment` DISABLE KEYS */;
/*!40000 ALTER TABLE `payment` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `programs`
--

DROP TABLE IF EXISTS `programs`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `programs` (
  `program_id` varchar(20) NOT NULL,
  `program_code` varchar(20) NOT NULL,
  `program_name` varchar(255) NOT NULL,
  `is_active` tinyint(1) DEFAULT '1',
  PRIMARY KEY (`program_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `programs`
--

LOCK TABLES `programs` WRITE;
/*!40000 ALTER TABLE `programs` DISABLE KEYS */;
INSERT INTO `programs` VALUES ('PROG-01','BSIT','Bachelor of Science in Information Technology',1),('PROG-02','BSCS','Bachelor of Science in Computer Science',1),('PROG-03','BSED','Bachelor of Secondary Education',1),('PROG-04','BSBA','Bachelor of Science in Business Administration',1);
/*!40000 ALTER TABLE `programs` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `registration_payment`
--

DROP TABLE IF EXISTS `registration_payment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `registration_payment` (
  `payment_id` int(11) NOT NULL DEFAULT '0',
  `enrollee_id` varchar(20) DEFAULT NULL,
  `amount` decimal(11,2) DEFAULT NULL,
  `registration_period` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`payment_id`),
  KEY `enrollee_id` (`enrollee_id`),
  CONSTRAINT `registration_payment_ibfk_1` FOREIGN KEY (`payment_id`) REFERENCES `payment` (`payment_id`) ON DELETE CASCADE,
  CONSTRAINT `registration_payment_ibfk_2` FOREIGN KEY (`enrollee_id`) REFERENCES `enrollees` (`enrollee_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `registration_payment`
--

LOCK TABLES `registration_payment` WRITE;
/*!40000 ALTER TABLE `registration_payment` DISABLE KEYS */;
/*!40000 ALTER TABLE `registration_payment` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `semester`
--

DROP TABLE IF EXISTS `semester`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `semester` (
  `semester_id` varchar(20) NOT NULL,
  `name` enum('1st','2nd','Summer') DEFAULT NULL,
  `academic_year_id` varchar(20) DEFAULT NULL,
  `start_date` date DEFAULT NULL,
  `end_date` date DEFAULT NULL,
  `is_active` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`semester_id`),
  KEY `academic_year_id` (`academic_year_id`),
  CONSTRAINT `semester_ibfk_1` FOREIGN KEY (`academic_year_id`) REFERENCES `academic_year` (`academic_year_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `semester`
--

LOCK TABLES `semester` WRITE;
/*!40000 ALTER TABLE `semester` DISABLE KEYS */;
/*!40000 ALTER TABLE `semester` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `student_record`
--

DROP TABLE IF EXISTS `student_record`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `student_record` (
  `student_record_id` varchar(20) NOT NULL,
  `student_id` varchar(20) DEFAULT NULL,
  `first_name` varchar(100) DEFAULT NULL,
  `middle_name` varchar(100) DEFAULT NULL,
  `last_name` varchar(100) DEFAULT NULL,
  `suffix` varchar(20) DEFAULT NULL,
  `birth_date` date DEFAULT NULL,
  `gender` varchar(10) DEFAULT NULL,
  `address` varchar(255) DEFAULT NULL,
  `province` varchar(100) DEFAULT NULL,
  `city` varchar(100) DEFAULT NULL,
  `contact_number` varchar(20) DEFAULT NULL,
  `email_address` varchar(191) DEFAULT NULL,
  `guardian_name` varchar(100) DEFAULT NULL,
  `guardian_contact` varchar(20) DEFAULT NULL,
  `last_school_attended` varchar(255) DEFAULT NULL,
  `school_year_enrolled` varchar(20) DEFAULT NULL,
  `program_id` varchar(20) DEFAULT NULL,
  `year_level` varchar(20) DEFAULT NULL,
  `student_type` varchar(50) DEFAULT NULL,
  `photo_link` varchar(500) DEFAULT NULL,
  `birth_cert_link` varchar(500) DEFAULT NULL,
  `report_card_link` varchar(500) DEFAULT NULL,
  `form_137_link` varchar(500) DEFAULT NULL,
  `record_type` enum('Admission','Enrollment','Transfer','Graduation') DEFAULT 'Admission',
  `record_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `remarks` text,
  PRIMARY KEY (`student_record_id`),
  KEY `program_id` (`program_id`),
  KEY `idx_student_record_student` (`student_id`),
  CONSTRAINT `student_record_ibfk_1` FOREIGN KEY (`student_id`) REFERENCES `students` (`student_id`) ON DELETE CASCADE,
  CONSTRAINT `student_record_ibfk_2` FOREIGN KEY (`program_id`) REFERENCES `programs` (`program_id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `student_record`
--

LOCK TABLES `student_record` WRITE;
/*!40000 ALTER TABLE `student_record` DISABLE KEYS */;
/*!40000 ALTER TABLE `student_record` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `students`
--

DROP TABLE IF EXISTS `students`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `students` (
  `student_id` varchar(20) NOT NULL,
  `user_id` int(11) DEFAULT NULL,
  `program_id` varchar(20) DEFAULT NULL,
  `enrollment_status` enum('Active','Inactive','Graduated','Dropped','LOA') DEFAULT 'Active',
  `date_enrolled` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`student_id`),
  KEY `idx_students_user` (`user_id`),
  KEY `idx_students_program` (`program_id`),
  CONSTRAINT `students_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE,
  CONSTRAINT `students_ibfk_2` FOREIGN KEY (`program_id`) REFERENCES `programs` (`program_id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `students`
--

LOCK TABLES `students` WRITE;
/*!40000 ALTER TABLE `students` DISABLE KEYS */;
/*!40000 ALTER TABLE `students` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `user_id` int(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(70) NOT NULL,
  `email` varchar(100) NOT NULL,
  `password` varchar(100) NOT NULL,
  `access` varchar(50) NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `is_active` tinyint(1) DEFAULT '1',
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `username` (`username`),
  UNIQUE KEY `email` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (1,'Joshua_Santiago','joshuasantiago0921@gmail.com','Joshua@21','Enrollees','2025-10-26 13:57:36',1),(6,'adminUser','admin@email.com','securePass123','Admin','2025-10-27 06:36:29',1);
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping events for database 'sql12804580'
--
