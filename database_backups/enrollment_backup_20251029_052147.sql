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
INSERT INTO `academic_year` VALUES ('AY2024-2025','2024-2025','2024-08-01','2025-05-31',0),('AY2025-2026','2025-2026','2025-08-01','2026-05-31',1),('AY2026-2027','2026-2027','2026-08-01','2027-05-31',0);
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
  `card_holder_name` varchar(100) DEFAULT NULL,
  `approval_code` varchar(50) DEFAULT NULL,
  `transaction_id` varchar(100) DEFAULT NULL,
  `bank_name` varchar(100) DEFAULT NULL,
  `card_no` char(16) NOT NULL,
  `expire_month` char(2) NOT NULL,
  `expire_year` char(2) NOT NULL,
  `cvc_no` char(3) NOT NULL,
  PRIMARY KEY (`payment_id`),
  CONSTRAINT `card_payment_ibfk_1` FOREIGN KEY (`payment_id`) REFERENCES `payment` (`payment_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `card_payment`
--

LOCK TABLES `card_payment` WRITE;
/*!40000 ALTER TABLE `card_payment` DISABLE KEYS */;
INSERT INTO `card_payment` VALUES (1,'Joshua S. Santiago',NULL,'TXN1761656666865378','BPI','9999999999999999','08','28','321'),(2,'Joshua S. Santiago',NULL,'TXN1761657878138592','BPI','9999999999999999','08','28','321'),(3,'Joshua S. Santiago',NULL,'TXN1761660130612457','BPI','9999999999999999','08','28','321'),(4,'Joshua S. Santiago',NULL,'TXN1761660762985632','BPI','9999999999999999','08','28','321'),(5,'Joshua S. Santiago',NULL,'TXN1761663776831691','BPI','9999999999999999','08','28','321'),(6,'Joshua S. Santiago',NULL,'TXN1761664127364625','BPI','9999999999999999','08','28','321'),(7,'Joshua S. Santiago',NULL,'TXN1761664586736646','BPI','9999999999999999','08','28','321'),(8,'Joshua S. Santiago',NULL,'TXN8784732379750100','BPI','9999999999999999','08','28','321'),(9,'Joshua S. Santiago',NULL,'TXN9497690321409786','BPI','9999999999999999','08','28','321'),(10,'Joshua S. Santiago',NULL,'TXN1134254775135083','BPI','9999999999999999','08','28','321'),(11,'Joshua S. Santiago',NULL,'TXN7178203218782510','BPI','9999999999999999','08','28','321'),(12,'Joshua S. Santiago',NULL,'TXN2488252075843748','BPI','9999999999999999','08','28','321'),(13,'Joshua S. Santiago',NULL,'TXN906651030207659','BPI','9999999999999999','08','28','321'),(14,'Joshua S. Santiago',NULL,'TXN7068499230843502','BPI','9999999999999999','08','28','321'),(15,'Joshua S. Santiago',NULL,'TXN2622543370930984','BPI','9999999999999999','08','28','321');
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
INSERT INTO `cashier` VALUES ('CSH25-5665',7,'Random','Cashier');
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
  `time_slot_id` varchar(20) DEFAULT NULL,
  `room_id` varchar(20) DEFAULT NULL,
  `course_id` varchar(20) DEFAULT NULL,
  `faculty_id` varchar(20) DEFAULT NULL,
  `semester_id` varchar(20) DEFAULT NULL,
  `section_id` varchar(20) DEFAULT NULL,
  `capacity` int(11) DEFAULT NULL,
  `enrolled_count` int(11) DEFAULT '0',
  `schedule_day` varchar(50) DEFAULT NULL,
  `schedule_time` varchar(50) DEFAULT NULL,
  `room` varchar(50) DEFAULT NULL,
  `status` enum('Open','Closed','Cancelled') DEFAULT 'Open',
  `is_auto_generated` tinyint(1) DEFAULT '0',
  `conflict_notes` varchar(500) DEFAULT NULL,
  PRIMARY KEY (`offering_id`),
  KEY `faculty_id` (`faculty_id`),
  KEY `idx_course_offerings_semester` (`semester_id`),
  KEY `idx_course_offerings_course` (`course_id`),
  KEY `section_id` (`section_id`),
  KEY `time_slot_id` (`time_slot_id`),
  KEY `room_id` (`room_id`),
  CONSTRAINT `course_offerings_ibfk_1` FOREIGN KEY (`course_id`) REFERENCES `courses` (`course_id`) ON DELETE CASCADE,
  CONSTRAINT `course_offerings_ibfk_2` FOREIGN KEY (`faculty_id`) REFERENCES `faculty` (`faculty_id`) ON DELETE SET NULL,
  CONSTRAINT `course_offerings_ibfk_3` FOREIGN KEY (`semester_id`) REFERENCES `semester` (`semester_id`) ON DELETE CASCADE,
  CONSTRAINT `course_offerings_ibfk_room` FOREIGN KEY (`room_id`) REFERENCES `rooms` (`room_id`) ON DELETE SET NULL,
  CONSTRAINT `course_offerings_ibfk_section` FOREIGN KEY (`section_id`) REFERENCES `section` (`section_id`) ON DELETE SET NULL,
  CONSTRAINT `course_offerings_ibfk_timeslot` FOREIGN KEY (`time_slot_id`) REFERENCES `time_slots` (`time_slot_id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `course_offerings`
--

LOCK TABLES `course_offerings` WRITE;
/*!40000 ALTER TABLE `course_offerings` DISABLE KEYS */;
INSERT INTO `course_offerings` VALUES ('OFF-2025-001','TS001','ROOM-101','IT101','FAC25-8109','SEM2025-1ST','SEC-BSIT-1A',40,0,'Monday','08:00-09:00','GEB-101','Open',0,NULL),('OFF-2025-002','TS010','LAB-101','IT102','FAC25-1002','SEM2025-1ST','SEC-BSIT-1A',30,0,'Tuesday','08:00-09:00','CL-101','Open',0,NULL),('OFF-2025-003','TS019','ROOM-102','IT104','FAC25-1008','SEM2025-1ST','SEC-BSIT-1A',40,0,'Wednesday','08:00-09:00','GEB-102','Open',0,NULL),('OFF-2025-004','TS028','ROOM-101','IT105','FAC25-1006','SEM2025-1ST','SEC-BSIT-1A',40,0,'Thursday','08:00-09:00','GEB-101','Open',0,NULL),('OFF-2025-005','TS037','ROOM-201','IT106','FAC25-1001','SEM2025-1ST','SEC-BSIT-1A',35,0,'Friday','08:00-09:00','GEB-201','Open',0,NULL),('OFF-2025-006','TS002','ROOM-102','IT101','FAC25-8109','SEM2025-1ST','SEC-BSIT-1B',40,0,'Monday','09:00-10:00','GEB-102','Open',0,NULL),('OFF-2025-007','TS011','LAB-102','IT102','FAC25-1002','SEM2025-1ST','SEC-BSIT-1B',30,0,'Tuesday','09:00-10:00','CL-102','Open',0,NULL),('OFF-2025-008','TS020','ROOM-103','IT104','FAC25-1008','SEM2025-1ST','SEC-BSIT-1B',40,0,'Wednesday','09:00-10:00','GEB-103','Open',0,NULL),('OFF-2025-009','TS029','ROOM-102','IT105','FAC25-1006','SEM2025-1ST','SEC-BSIT-1B',40,0,'Thursday','09:00-10:00','GEB-102','Open',0,NULL),('OFF-2025-010','TS038','ROOM-201','IT106','FAC25-1007','SEM2025-1ST','SEC-BSIT-1B',35,0,'Friday','09:00-10:00','GEB-201','Open',0,NULL),('OFF-2025-011','TS003','ROOM-201','IT201','FAC25-1002','SEM2025-1ST','SEC-BSIT-2A',35,0,'Monday','10:00-11:00','GEB-201','Open',0,NULL),('OFF-2025-012','TS012','LAB-101','IT202','FAC25-1001','SEM2025-1ST','SEC-BSIT-2A',30,0,'Tuesday','10:00-11:00','CL-101','Open',0,NULL),('OFF-2025-013','TS021','ROOM-202','IT203','FAC25-1004','SEM2025-1ST','SEC-BSIT-2A',35,0,'Wednesday','10:00-11:00','GEB-202','Open',0,NULL),('OFF-2025-014','TS030','ROOM-203','IT205','FAC25-1003','SEM2025-1ST','SEC-BSIT-2A',40,0,'Thursday','10:00-11:00','GEB-203','Open',0,NULL),('OFF-2025-015','TS039','ROOM-301','IT208','FAC25-1006','SEM2025-1ST','SEC-BSIT-2A',45,0,'Friday','10:00-11:00','GEB-301','Open',0,NULL);
/*!40000 ALTER TABLE `course_offerings` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `course_prerequisites`
--

DROP TABLE IF EXISTS `course_prerequisites`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `course_prerequisites` (
  `course_id` varchar(20) NOT NULL,
  `prerequisite_course_id` varchar(20) NOT NULL,
  `is_corequisite` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`course_id`,`prerequisite_course_id`),
  KEY `prerequisite_course_id` (`prerequisite_course_id`),
  CONSTRAINT `course_prerequisites_ibfk_1` FOREIGN KEY (`course_id`) REFERENCES `courses` (`course_id`) ON DELETE CASCADE,
  CONSTRAINT `course_prerequisites_ibfk_2` FOREIGN KEY (`prerequisite_course_id`) REFERENCES `courses` (`course_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `course_prerequisites`
--

LOCK TABLES `course_prerequisites` WRITE;
/*!40000 ALTER TABLE `course_prerequisites` DISABLE KEYS */;
INSERT INTO `course_prerequisites` VALUES ('IT103','IT102',0),('IT108','IT102',0),('IT201','IT103',0),('IT202','IT101',0),('IT203','IT103',0),('IT204','IT104',0),('IT206','IT205',0),('IT214','IT202',0),('IT216','IT215',0),('IT301','IT203',0),('IT302','IT301',0),('IT303','IT301',0),('IT304','IT203',0),('IT307','IT306',0),('IT401','IT302',0),('IT402','IT401',0);
/*!40000 ALTER TABLE `course_prerequisites` ENABLE KEYS */;
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
  `requires_laboratory` tinyint(1) DEFAULT '0',
  `requires_computer` tinyint(1) DEFAULT '0',
  `min_room_capacity` int(11) DEFAULT '30',
  PRIMARY KEY (`course_id`),
  KEY `program_id` (`program_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `courses`
--

LOCK TABLES `courses` WRITE;
/*!40000 ALTER TABLE `courses` DISABLE KEYS */;
INSERT INTO `courses` VALUES ('C001','COMSCI 2100','Information Management',2.0,'BSIT',2,'1st',0,1,40),('C002','COMSCI 2100 LAB A','Information Management (Laboratory A)',1.0,'BSIT',2,'1st',1,1,25),('C003','COMSCI 2100 LAB B','Information Management (Laboratory B)',1.0,'BSIT',2,'1st',1,1,25),('C004','COMSCI 2101','Operating Systems',3.0,'BSIT',2,'1st',0,1,40),('C005','COMSCI 2110','Object Oriented Programming',2.0,'BSIT',2,'1st',0,1,40),('C006','COMSCI 2110 LAB A','Object Oriented Programming (Laboratory A)',1.0,'BSIT',2,'1st',1,1,25),('C007','COMSCI 2110 LAB B','Object Oriented Programming (Laboratory B)',1.0,'BSIT',2,'1st',1,1,25),('C008','INTECH 2100','Web Design and Multimedia',2.0,'BSIT',2,'1st',0,1,40),('C009','INTECH 2100 LAB A','Web Design and Multimedia (Laboratory A)',1.0,'BSIT',2,'1st',1,1,25),('C010','EED 1212','Foundation of Special and Inclusive Education',3.0,'BEED',2,'1st',0,0,40),('C011','EED 2111','The Teacher and the School Curriculum',3.0,'BEED',2,'1st',0,0,40),('C012','ELEM 2111','Technology for Teaching and Learning in the Elementary Grades',3.0,'BEED',2,'1st',0,1,40),('C013','ELEM 2112','Teaching English in the Elementary Grades (Language Arts)',3.0,'BEED',2,'1st',0,0,40),('C014','ELEM 2113','Pagtuturo ng Filipino sa Elementarya 1 (Estruktura at Gamit ng Wikang Filipino)',3.0,'BEED',2,'1st',0,0,40),('C015','ELEM 2114','Teaching Arts in the Elementary Grades',3.0,'BEED',2,'1st',0,0,40),('C016','ELEM 2115','Teaching Music in the Elementary Grades',3.0,'BEED',2,'1st',1,0,30),('C017','SOCSCI 1110','Ethics',3.0,'BEED',2,'1st',0,0,40),('C018','PATHFit 3','Menu of Dance, Sports, Martial Arts, Group Exercise, Outdoor and Adventure Activities',2.0,'BEED',2,'1st',1,0,30),('IT101','IT101','Introduction to Computing',3.0,'PROG-01',1,'1st',0,0,30),('IT102','IT102','Computer Programming 1',3.0,'PROG-01',1,'1st',0,0,30),('IT103','IT103','Computer Programming 2',3.0,'PROG-01',1,'2nd',0,0,30),('IT104','IT104','Computer Hardware Fundamentals',3.0,'PROG-01',1,'1st',0,0,30),('IT105','IT105','Mathematics in the Modern World',3.0,'PROG-01',1,'1st',0,0,30),('IT106','IT106','Purposive Communication',3.0,'PROG-01',1,'1st',0,0,30),('IT107','IT107','Understanding the Self',3.0,'PROG-01',1,'2nd',0,0,30),('IT108','IT108','Programming Logic and Design',3.0,'PROG-01',1,'2nd',0,0,30),('IT109','IT109','Physical Education 1',2.0,'PROG-01',1,'1st',0,0,30),('IT110','IT110','Physical Education 2',2.0,'PROG-01',1,'2nd',0,0,30),('IT111','IT111','NSTP 1',3.0,'PROG-01',1,'1st',0,0,30),('IT112','IT112','NSTP 2',3.0,'PROG-01',1,'2nd',0,0,30),('IT113','IT113','Introduction to Human Computer Interaction',3.0,'PROG-01',1,'2nd',0,0,30),('IT114','IT114','Ethics',3.0,'PROG-01',1,'2nd',0,0,30),('IT115','IT115','Science, Technology and Society',3.0,'PROG-01',1,'2nd',0,0,30),('IT116','IT116','Introduction to Networking',3.0,'PROG-01',1,'2nd',0,0,30),('IT201','IT201','Data Structures and Algorithms',3.0,'PROG-01',2,'1st',0,0,30),('IT202','IT202','Database Management Systems',3.0,'PROG-01',2,'1st',0,0,30),('IT203','IT203','Object-Oriented Programming',3.0,'PROG-01',2,'1st',0,0,30),('IT204','IT204','Operating Systems',3.0,'PROG-01',2,'2nd',0,0,30),('IT205','IT205','Networking 1 (LAN Technologies)',3.0,'PROG-01',2,'1st',0,0,30),('IT206','IT206','Networking 2 (WAN Technologies)',3.0,'PROG-01',2,'2nd',0,0,30),('IT207','IT207','IT Elective 1 (Web Development)',3.0,'PROG-01',2,'2nd',0,0,30),('IT208','IT208','Discrete Mathematics',3.0,'PROG-01',2,'1st',0,0,30),('IT209','IT209','Data Communications',3.0,'PROG-01',2,'2nd',0,0,30),('IT210','IT210','PE 3',2.0,'PROG-01',2,'1st',0,0,30),('IT211','IT211','PE 4',2.0,'PROG-01',2,'2nd',0,0,30),('IT212','IT212','Professional Ethics',3.0,'PROG-01',2,'1st',0,0,30),('IT213','IT213','IT Project Management',3.0,'PROG-01',2,'2nd',0,0,30),('IT214','IT214','Advanced Database Systems',3.0,'PROG-01',2,'2nd',0,0,30),('IT215','IT215','Web Systems and Technologies 1',3.0,'PROG-01',2,'1st',0,0,30),('IT216','IT216','Web Systems and Technologies 2',3.0,'PROG-01',2,'2nd',0,0,30),('IT301','IT301','Software Engineering 1',3.0,'PROG-01',3,'1st',0,0,30),('IT302','IT302','Software Engineering 2',3.0,'PROG-01',3,'2nd',0,0,30),('IT303','IT303','System Integration and Architecture',3.0,'PROG-01',3,'2nd',0,0,30),('IT304','IT304','Mobile Application Development',3.0,'PROG-01',3,'1st',0,0,30),('IT305','IT305','Human-Computer Interaction',3.0,'PROG-01',3,'2nd',0,0,30),('IT306','IT306','Information Assurance and Security 1',3.0,'PROG-01',3,'1st',0,0,30),('IT307','IT307','Information Assurance and Security 2',3.0,'PROG-01',3,'2nd',0,0,30),('IT308','IT308','Technopreneurship',3.0,'PROG-01',3,'1st',0,0,30),('IT309','IT309','IT Elective 2 (Cloud Computing)',3.0,'PROG-01',3,'1st',0,0,30),('IT310','IT310','IT Elective 3 (Game Development)',3.0,'PROG-01',3,'2nd',0,0,30),('IT311','IT311','Research Methods in IT',3.0,'PROG-01',3,'2nd',0,0,30),('IT312','IT312','Free Elective 1',3.0,'PROG-01',3,'2nd',0,0,30),('IT313','IT313','Free Elective 2',3.0,'PROG-01',3,'1st',0,0,30),('IT314','IT314','IT Practicum Orientation',1.0,'PROG-01',3,'2nd',0,0,30),('IT315','IT315','IT Seminar 1',1.0,'PROG-01',3,'2nd',0,0,30),('IT316','IT316','Web Hosting and Deployment',3.0,'PROG-01',3,'1st',0,0,30),('IT401','IT401','Capstone Project 1',3.0,'PROG-01',4,'1st',0,0,30),('IT402','IT402','Capstone Project 2',3.0,'PROG-01',4,'2nd',0,0,30),('IT403','IT403','System Administration and Maintenance',3.0,'PROG-01',4,'1st',0,0,30),('IT404','IT404','IT Seminar 2',1.0,'PROG-01',4,'2nd',0,0,30),('IT405','IT405','IT Practicum / OJT',6.0,'PROG-01',4,'2nd',0,0,30),('IT406','IT406','Emerging Technologies',3.0,'PROG-01',4,'1st',0,0,30),('IT407','IT407','IT Elective 4 (Cybersecurity)',3.0,'PROG-01',4,'2nd',0,0,30),('IT408','IT408','IT Elective 5 (AI Basics)',3.0,'PROG-01',4,'1st',0,0,30),('IT409','IT409','IT Research Colloquium',3.0,'PROG-01',4,'2nd',0,0,30),('IT410','IT410','Free Elective 3',3.0,'PROG-01',4,'1st',0,0,30),('IT411','IT411','Free Elective 4',3.0,'PROG-01',4,'2nd',0,0,30),('IT412','IT412','IT in the Modern Enterprise',3.0,'PROG-01',4,'1st',0,0,30),('IT413','IT413','Cloud Security',3.0,'PROG-01',4,'2nd',0,0,30),('IT414','IT414','Server Management',3.0,'PROG-01',4,'2nd',0,0,30),('IT415','IT415','Digital Ethics',3.0,'PROG-01',4,'1st',0,0,30),('IT416','IT416','Data Analytics',3.0,'PROG-01',4,'2nd',0,0,30);
/*!40000 ALTER TABLE `courses` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `credit_card`
--

DROP TABLE IF EXISTS `credit_card`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `credit_card` (
  `card_id` int(11) NOT NULL AUTO_INCREMENT,
  `card_no` char(16) NOT NULL,
  `card_holder_name` varchar(100) NOT NULL,
  `expire_month` char(2) NOT NULL,
  `expire_year` char(2) NOT NULL,
  `cvc_no` char(3) NOT NULL,
  `bank_name` varchar(100) DEFAULT NULL,
  `money` decimal(12,2) NOT NULL DEFAULT '0.00',
  `status` enum('Active','Blocked','Expired') DEFAULT 'Active',
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`card_id`),
  UNIQUE KEY `card_no` (`card_no`),
  UNIQUE KEY `card_no_2` (`card_no`),
  UNIQUE KEY `card_no_3` (`card_no`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `credit_card`
--

LOCK TABLES `credit_card` WRITE;
/*!40000 ALTER TABLE `credit_card` DISABLE KEYS */;
INSERT INTO `credit_card` VALUES (1,'9999999999999999','Joshua S. Santiago','08','28','321','BPI',99788000.00,'Active','2025-10-28 15:16:49'),(2,'1234123412341234','Wesley Tadique','12','27','456','BDO',10000.00,'Active','2025-10-28 13:02:45'),(3,'4321432143214321','Kyle Soriano','06','29','789','Metrobank',20000.00,'Active','2025-10-28 13:02:39');
/*!40000 ALTER TABLE `credit_card` ENABLE KEYS */;
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
  `payment_status` enum('Not_Paid','Paid_Pending_Verification','Verified','Rejected') DEFAULT 'Not_Paid',
  `cashier_rejection_reason` text,
  `admin_rejection_reason` text,
  `admin_approval_message` text,
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
  KEY `idx_enrollees_payment_status` (`payment_status`),
  KEY `idx_enrollees_admin_review` (`enrollment_status`,`payment_status`,`has_filled_up_form`),
  CONSTRAINT `enrollees_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE,
  CONSTRAINT `enrollees_ibfk_3` FOREIGN KEY (`reviewed_by`) REFERENCES `admin` (`user_id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Stores enrollee information including application status and credentials';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `enrollees`
--

LOCK TABLES `enrollees` WRITE;
/*!40000 ALTER TABLE `enrollees` DISABLE KEYS */;
INSERT INTO `enrollees` VALUES ('SA25-2111',1,'Joshua','Simugan','Santiago',NULL,'2006-09-21',NULL,'#002, Zone 1, Sta. Lucia','Nueva Ecija','Guimba','09603376884','joshuasantiago0921@gmail.com',NULL,NULL,'1st Year','New','Bartolome Sangalang National High School','2016-2022','BSIT','Pending','Verified','Your details are not completed!',NULL,NULL,'2025-10-29 05:26:56',NULL,NULL,1,'https://drive.google.com/file/d/1ymjB6ixfU5o52LLoDM_7vwHktStSqNu4/view?usp=drivesdk','https://drive.google.com/file/d/1Dbfdio9BIl3FB_g7npLB86ivj2S1HRCx/view?usp=drivesdk','https://drive.google.com/file/d/1h4E78QPnoiZO9U7ijueIbpkTVqgN9CWX/view?usp=drivesdk','https://drive.google.com/file/d/1U8YM0Wi1yMTVywqjtoSluoR-EKtiSedD/view?usp=drivesdk'),('SA25-3706',10,'Lee Dave','G','Canillas',NULL,'1982-10-08',NULL,'69, None, Ewan ko','Nueva Ecija','San Jose','09784673213','leedavetumuwad@gmail.com',NULL,NULL,'4th Year','Transferee','Tumuwad High School','1998-2006','BSCS','Pending','Paid_Pending_Verification',NULL,NULL,NULL,'2025-10-29 06:17:50',NULL,NULL,1,'https://drive.google.com/file/d/1wgl4s4ukzcJw6ylmMwWuy4W0t6eZXp08/view?usp=drivesdk','https://drive.google.com/file/d/1tH9llhy_pESZB-k6k_36w_lZ024PhDwm/view?usp=drivesdk','https://drive.google.com/file/d/1kuR8dkJy6WHRvrfymJpjuFGIc-vrlPlw/view?usp=drivesdk','https://drive.google.com/file/d/1APm8Blsu_N-wwczYCrRLMBE82nhMDEOa/view?usp=drivesdk'),('SA25-4001',37,'Alex','M','White',NULL,'2006-03-15','Male','123 Main St, Brgy 1','Nueva Ecija','Cabanatuan','09171234567','alex.white@email.com','Rebecca White','09181234567','1st Year','New','Central High School','2025-2026','PROG-01','Pending','Not_Paid',NULL,NULL,NULL,'2025-10-28 13:00:00',NULL,NULL,1,'https://drive.google.com/file/d/sample1','https://drive.google.com/file/d/sample2','https://drive.google.com/file/d/sample3','https://drive.google.com/file/d/sample4'),('SA25-4002',38,'Charlotte','A','Harris',NULL,'2006-07-22','Female','456 Oak Ave, Brgy 2','Nueva Ecija','San Jose','09172345678','charlotte.harris@email.com','Thomas Harris','09182345678','1st Year','New','Riverside Academy','2025-2026','PROG-02','Approved','Not_Paid',NULL,NULL,NULL,'2025-10-28 14:00:00',NULL,NULL,1,'https://drive.google.com/file/d/sample5','https://drive.google.com/file/d/sample6','https://drive.google.com/file/d/sample7','https://drive.google.com/file/d/sample8'),('SA25-4003',39,'Daniel','R','Clark','Jr.','2006-11-08','Male','789 Pine Rd, Brgy 3','Nueva Ecija','Guimba','09173456789','daniel.clark@email.com','Daniel Clark Sr.','09183456789','1st Year','Transferee','Northern College','2025-2026','PROG-01','Approved','Not_Paid',NULL,NULL,NULL,'2025-10-28 15:00:00',NULL,NULL,1,'https://drive.google.com/file/d/sample9','https://drive.google.com/file/d/sample10','https://drive.google.com/file/d/sample11','https://drive.google.com/file/d/sample12'),('SA25-4004',40,'Amelia','S','Lewis',NULL,'2006-05-19','Female','321 Elm St, Brgy 4','Nueva Ecija','Munoz','09174567890','amelia.lewis@email.com','Susan Lewis','09184567890','1st Year','New','Valley High School','2025-2026','PROG-03','Pending','Not_Paid',NULL,NULL,NULL,'2025-10-28 16:00:00',NULL,NULL,1,'https://drive.google.com/file/d/sample13','https://drive.google.com/file/d/sample14','https://drive.google.com/file/d/sample15','https://drive.google.com/file/d/sample16'),('SA25-4005',41,'Matthew','J','Walker',NULL,'2006-09-30','Male','654 Maple Dr, Brgy 5','Nueva Ecija','Cabanatuan','09175678901','matthew.walker@email.com','Jennifer Walker','09185678901','1st Year','New','East Side Academy','2025-2026','PROG-01','Rejected','Not_Paid',NULL,NULL,NULL,'2025-10-28 17:00:00',NULL,NULL,0,NULL,NULL,NULL,NULL),('SA25-4006',42,'Harper','L','Hall',NULL,'2006-12-25','Female','987 Cedar Ln, Brgy 6','Nueva Ecija','San Jose','09176789012','harper.hall@email.com','Michael Hall','09186789012','1st Year','New','Westview High School','2025-2026','PROG-04','Approved','Not_Paid',NULL,NULL,NULL,'2025-10-28 18:00:00',NULL,NULL,1,'https://drive.google.com/file/d/sample17','https://drive.google.com/file/d/sample18','https://drive.google.com/file/d/sample19','https://drive.google.com/file/d/sample20'),('SA25-4007',43,'Jackson','P','Allen',NULL,'2006-04-14','Male','147 Birch Way, Brgy 7','Nueva Ecija','Guimba','09177890123','jackson.allen@email.com','Patricia Allen','09187890123','1st Year','New','Summit Preparatory','2025-2026','PROG-01','Enrolled','Not_Paid',NULL,NULL,NULL,'2025-10-28 19:00:00',NULL,NULL,1,'https://drive.google.com/file/d/sample21','https://drive.google.com/file/d/sample22','https://drive.google.com/file/d/sample23','https://drive.google.com/file/d/sample24'),('SA25-4008',44,'Ella','K','Young',NULL,'2006-08-03','Female','258 Spruce Ct, Brgy 8','Nueva Ecija','Munoz','09178901234','ella.young@email.com','Robert Young','09188901234','1st Year','New','Lakeside High School','2025-2026','PROG-02','Pending','Not_Paid',NULL,NULL,NULL,'2025-10-28 20:00:00',NULL,NULL,1,'https://drive.google.com/file/d/sample25','https://drive.google.com/file/d/sample26','https://drive.google.com/file/d/sample27','https://drive.google.com/file/d/sample28'),('SA25-4009',163,'Liam','T','Johnson',NULL,'2006-01-15','Male','123 Oak St, Brgy 9','Nueva Ecija','Cabanatuan','09180000001','liam.johnson@email.com','Peter Johnson','09180000002','1st Year','New','Central High School','2025-2026','BSIT','Pending','Paid_Pending_Verification',NULL,NULL,NULL,'2025-10-28 20:34:52',NULL,NULL,1,'https://drive.google.com/file/d/1ymjB6ixfU5o52LLoDM_7vwHktStSqNu4/view?usp=drivesdk','https://drive.google.com/file/d/1Dbfdio9BIl3FB_g7npLB86ivj2S1HRCx/view?usp=drivesdk','https://drive.google.com/file/d/1kuR8dkJy6WHRvrfymJpjuFGIc-vrlPlw/view?usp=drivesdk','https://drive.google.com/file/d/1APm8Blsu_N-wwczYCrRLMBE82nhMDEOa/view?usp=drivesdk'),('SA25-4010',164,'Olivia','M','Smith',NULL,'2006-02-20','Female','456 Pine St, Brgy 10','Nueva Ecija','San Jose','09180000003','olivia.smith@email.com','Mary Smith','09180000004','1st Year','New','Riverside Academy','2025-2026','BSCS','Pending','Paid_Pending_Verification',NULL,NULL,NULL,'2025-10-28 20:34:52',NULL,NULL,1,'https://drive.google.com/file/d/1ymjB6ixfU5o52LLoDM_7vwHktStSqNu4/view?usp=drivesdk','https://drive.google.com/file/d/1Dbfdio9BIl3FB_g7npLB86ivj2S1HRCx/view?usp=drivesdk','https://drive.google.com/file/d/1kuR8dkJy6WHRvrfymJpjuFGIc-vrlPlw/view?usp=drivesdk','https://drive.google.com/file/d/1APm8Blsu_N-wwczYCrRLMBE82nhMDEOa/view?usp=drivesdk'),('SA25-4011',165,'Noah','K','Davis',NULL,'2006-03-10','Male','789 Cedar St, Brgy 11','Nueva Ecija','Guimba','09180000005','noah.davis@email.com','Laura Davis','09180000006','1st Year','New','Valley High School','2025-2026','BSIT','Pending','Paid_Pending_Verification',NULL,NULL,NULL,'2025-10-28 20:34:52',NULL,NULL,1,'https://drive.google.com/file/d/1ymjB6ixfU5o52LLoDM_7vwHktStSqNu4/view?usp=drivesdk','https://drive.google.com/file/d/1Dbfdio9BIl3FB_g7npLB86ivj2S1HRCx/view?usp=drivesdk','https://drive.google.com/file/d/1kuR8dkJy6WHRvrfymJpjuFGIc-vrlPlw/view?usp=drivesdk','https://drive.google.com/file/d/1APm8Blsu_N-wwczYCrRLMBE82nhMDEOa/view?usp=drivesdk'),('SA25-4012',166,'Emma','L','Martinez',NULL,'2006-04-25','Female','321 Elm St, Brgy 12','Nueva Ecija','Munoz','09180000007','emma.martinez@email.com','Steven Martinez','09180000008','1st Year','New','Westview High School','2025-2026','BSCS','Pending','Paid_Pending_Verification',NULL,NULL,NULL,'2025-10-28 20:34:52',NULL,NULL,1,'https://drive.google.com/file/d/1ymjB6ixfU5o52LLoDM_7vwHktStSqNu4/view?usp=drivesdk','https://drive.google.com/file/d/1Dbfdio9BIl3FB_g7npLB86ivj2S1HRCx/view?usp=drivesdk','https://drive.google.com/file/d/1kuR8dkJy6WHRvrfymJpjuFGIc-vrlPlw/view?usp=drivesdk','https://drive.google.com/file/d/1APm8Blsu_N-wwczYCrRLMBE82nhMDEOa/view?usp=drivesdk'),('SA25-4013',167,'Ava','M','Brown',NULL,'2006-05-18','Female','654 Maple St, Brgy 13','Nueva Ecija','Cabanatuan','09180000009','ava.brown@email.com','Nancy Brown','09180000010','1st Year','New','East Side Academy','2025-2026','BSIT','Pending','Paid_Pending_Verification',NULL,NULL,NULL,'2025-10-28 20:34:52',NULL,NULL,1,'https://drive.google.com/file/d/1ymjB6ixfU5o52LLoDM_7vwHktStSqNu4/view?usp=drivesdk','https://drive.google.com/file/d/1Dbfdio9BIl3FB_g7npLB86ivj2S1HRCx/view?usp=drivesdk','https://drive.google.com/file/d/1kuR8dkJy6WHRvrfymJpjuFGIc-vrlPlw/view?usp=drivesdk','https://drive.google.com/file/d/1APm8Blsu_N-wwczYCrRLMBE82nhMDEOa/view?usp=drivesdk'),('SA25-4014',168,'William','N','Lee',NULL,'2006-06-05','Male','987 Birch St, Brgy 14','Nueva Ecija','San Jose','09180000011','william.lee@email.com','George Lee','09180000012','1st Year','New','Summit Preparatory','2025-2026','BSCS','Pending','Paid_Pending_Verification',NULL,NULL,NULL,'2025-10-28 20:34:52',NULL,NULL,1,'https://drive.google.com/file/d/1ymjB6ixfU5o52LLoDM_7vwHktStSqNu4/view?usp=drivesdk','https://drive.google.com/file/d/1Dbfdio9BIl3FB_g7npLB86ivj2S1HRCx/view?usp=drivesdk','https://drive.google.com/file/d/1kuR8dkJy6WHRvrfymJpjuFGIc-vrlPlw/view?usp=drivesdk','https://drive.google.com/file/d/1APm8Blsu_N-wwczYCrRLMBE82nhMDEOa/view?usp=drivesdk'),('SA25-4015',169,'Sophia','O','Wilson',NULL,'2006-07-12','Female','147 Spruce St, Brgy 15','Nueva Ecija','Guimba','09180000013','sophia.wilson@email.com','Linda Wilson','09180000014','1st Year','New','Lakeside High School','2025-2026','BSIT','Pending','Paid_Pending_Verification',NULL,NULL,NULL,'2025-10-28 20:34:52',NULL,NULL,1,'https://drive.google.com/file/d/1ymjB6ixfU5o52LLoDM_7vwHktStSqNu4/view?usp=drivesdk','https://drive.google.com/file/d/1Dbfdio9BIl3FB_g7npLB86ivj2S1HRCx/view?usp=drivesdk','https://drive.google.com/file/d/1kuR8dkJy6WHRvrfymJpjuFGIc-vrlPlw/view?usp=drivesdk','https://drive.google.com/file/d/1APm8Blsu_N-wwczYCrRLMBE82nhMDEOa/view?usp=drivesdk'),('SA25-4016',170,'James','P','Taylor',NULL,'2006-08-20','Male','258 Oak St, Brgy 16','Nueva Ecija','Munoz','09180000015','james.taylor@email.com','Patricia Taylor','09180000016','1st Year','New','MNHS','2025-2026','BSCS','Pending','Paid_Pending_Verification',NULL,NULL,NULL,'2025-10-28 20:34:52',NULL,NULL,1,'https://drive.google.com/file/d/1ymjB6ixfU5o52LLoDM_7vwHktStSqNu4/view?usp=drivesdk','https://drive.google.com/file/d/1Dbfdio9BIl3FB_g7npLB86ivj2S1HRCx/view?usp=drivesdk','https://drive.google.com/file/d/1kuR8dkJy6WHRvrfymJpjuFGIc-vrlPlw/view?usp=drivesdk','https://drive.google.com/file/d/1APm8Blsu_N-wwczYCrRLMBE82nhMDEOa/view?usp=drivesdk'),('SA25-6251',17,'Random','1','Person',NULL,'2006-05-19',NULL,'221, Purok 1, Isko','Dismarinas','Lokal','0911212122112121212','randomenrollee@gmail.com',NULL,NULL,'2nd Year','Returning','BTHS','2016-2022','BSCS','Pending','Paid_Pending_Verification',NULL,NULL,NULL,'2025-10-29 07:15:18',NULL,NULL,1,'https://drive.google.com/file/d/1TRn2AvoTYrRt2tslXxQfSwaobzF9c5yx/view?usp=drivesdk','https://drive.google.com/file/d/1v5ThERRkqkiIFoRi7O2STCOhMU-RbWmB/view?usp=drivesdk','https://drive.google.com/file/d/1sWOBQLjahwiSP3YP1l_ojE4SNFuIwK-z/view?usp=drivesdk','https://drive.google.com/file/d/1Z5CJcv18OqrxEHbXQSjYmDtF3IF093jE/view?usp=drivesdk'),('SA25-9290',9,'Earl Wesley','Aquino','Tadique',NULL,'2006-10-06',NULL,'None, None, Bantug','Nueva Ecija','Munoz','092049552049','earlwesley.tadique@gmail.com',NULL,NULL,'1st Year','New','MNHS','2024-2025','BSIT','Pending','Not_Paid',NULL,NULL,NULL,'2025-10-28 03:30:56',NULL,NULL,1,'https://drive.google.com/file/d/1zALYDbwPt35GJJMqGK3RzlXkh8DTuKMt/view?usp=drivesdk','https://drive.google.com/file/d/1DU1BCXcSQ4Js55tkLSOU5_beEUQYpZpp/view?usp=drivesdk','https://drive.google.com/file/d/1wWLMLoqeR0MH3ketoU4jtpYWFCAg1Bhp/view?usp=drivesdk','https://drive.google.com/file/d/1pMMb7C8vIpryChW1onNi9cImE5jC8zol/view?usp=drivesdk');
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
INSERT INTO `enrollments` VALUES ('ENR-2025-001','STU25-9206','OFF-2025-001','2025-08-20 16:00:00',NULL,'Enrolled'),('ENR-2025-002','STU25-9206','OFF-2025-002','2025-08-20 16:05:00',NULL,'Enrolled'),('ENR-2025-003','STU25-9206','OFF-2025-003','2025-08-20 16:10:00',NULL,'Enrolled'),('ENR-2025-004','STU25-9206','OFF-2025-004','2025-08-20 16:15:00',NULL,'Enrolled'),('ENR-2025-005','STU25-9206','OFF-2025-005','2025-08-20 16:20:00',NULL,'Enrolled'),('ENR-2025-006','STU25-1001','OFF-2025-001','2025-08-20 17:00:00',NULL,'Enrolled'),('ENR-2025-007','STU25-1001','OFF-2025-002','2025-08-20 17:05:00',NULL,'Enrolled'),('ENR-2025-008','STU25-1001','OFF-2025-003','2025-08-20 17:10:00',NULL,'Enrolled'),('ENR-2025-009','STU25-1001','OFF-2025-004','2025-08-20 17:15:00',NULL,'Enrolled'),('ENR-2025-010','STU25-1001','OFF-2025-005','2025-08-20 17:20:00',NULL,'Enrolled'),('ENR-2025-011','STU25-1002','OFF-2025-001','2025-08-20 18:00:00',NULL,'Enrolled'),('ENR-2025-012','STU25-1002','OFF-2025-002','2025-08-20 18:05:00',NULL,'Enrolled'),('ENR-2025-013','STU25-1002','OFF-2025-003','2025-08-20 18:10:00',NULL,'Enrolled'),('ENR-2025-014','STU25-1003','OFF-2025-001','2025-08-20 19:00:00',NULL,'Enrolled'),('ENR-2025-015','STU25-1003','OFF-2025-002','2025-08-20 19:05:00',NULL,'Enrolled'),('ENR-2025-016','STU25-1005','OFF-2025-001','2025-08-20 20:00:00',NULL,'Enrolled');
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
  `max_teaching_hours` int(11) DEFAULT '40',
  `current_teaching_hours` int(11) DEFAULT '0',
  `specialization` varchar(255) DEFAULT NULL,
  `years_of_service` int(11) DEFAULT '0',
  `subject_holding` int(11) DEFAULT '0',
  `sections_handled` int(11) DEFAULT '0',
  `preferred_days` varchar(100) DEFAULT NULL,
  `notes` text,
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
INSERT INTO `faculty` VALUES ('FAC25-1001',17,'Maria','Cruz','Computer Science',40,0,'Database Systems, Web Development',8,0,0,'Monday, Wednesday, Friday','Senior faculty member'),('FAC25-1002',18,'Robert','Garcia','Computer Science',40,0,'Programming, Data Structures',5,0,0,'Tuesday, Thursday','Specializes in algorithms'),('FAC25-1003',19,'Lisa','Reyes','Information Technology',40,0,'Networking, Security',10,0,0,'Monday, Wednesday, Friday','CCNA certified'),('FAC25-1004',20,'Michael','Santos','Computer Science',40,0,'Software Engineering, Mobile Dev',6,0,0,'Tuesday, Thursday, Friday',NULL),('FAC25-1005',21,'Anna','Lopez','Information Technology',40,0,'Systems Analysis, Project Management',7,0,0,'Monday, Wednesday','PMP certified'),('FAC25-1006',22,'David','Rivera','Computer Science',40,0,'AI, Machine Learning',4,0,0,'Tuesday, Thursday','PhD in Computer Science'),('FAC25-1007',23,'Sarah','Flores','Information Technology',40,0,'Web Technologies, UI/UX',9,0,0,'Monday, Friday','Former industry professional'),('FAC25-1008',24,'James','Torres','Computer Science',40,0,'Operating Systems, Computer Architecture',12,0,0,'Tuesday, Wednesday, Thursday','Department head'),('FAC25-1009',25,'Patricia','Ramos','Information Technology',40,0,'Cloud Computing, DevOps',3,0,0,'Monday, Wednesday, Friday','AWS certified'),('FAC25-1010',26,'Carlos','Mendoza','Computer Science',40,0,'Cybersecurity, Ethical Hacking',5,0,0,'Tuesday, Thursday','CEH certified'),('FAC25-8109',13,'John','Doe',NULL,40,0,NULL,0,0,0,NULL,NULL);
/*!40000 ALTER TABLE `faculty` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `faculty_availability`
--

DROP TABLE IF EXISTS `faculty_availability`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `faculty_availability` (
  `availability_id` varchar(20) NOT NULL,
  `faculty_id` varchar(20) NOT NULL,
  `day_of_week` enum('Monday','Tuesday','Wednesday','Thursday','Friday','Saturday','Sunday') NOT NULL,
  `start_time` time NOT NULL,
  `end_time` time NOT NULL,
  `is_preferred` tinyint(1) DEFAULT '0',
  `is_unavailable` tinyint(1) DEFAULT '0',
  `remarks` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`availability_id`),
  KEY `faculty_id` (`faculty_id`),
  CONSTRAINT `faculty_availability_ibfk_1` FOREIGN KEY (`faculty_id`) REFERENCES `faculty` (`faculty_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `faculty_availability`
--

LOCK TABLES `faculty_availability` WRITE;
/*!40000 ALTER TABLE `faculty_availability` DISABLE KEYS */;
INSERT INTO `faculty_availability` VALUES ('AVAIL-001','FAC25-8109','Monday','08:00:00','17:00:00',1,0,'Full day available'),('AVAIL-002','FAC25-8109','Wednesday','08:00:00','17:00:00',1,0,'Full day available'),('AVAIL-003','FAC25-8109','Friday','08:00:00','17:00:00',1,0,'Full day available'),('AVAIL-004','FAC25-1001','Monday','08:00:00','17:00:00',1,0,NULL),('AVAIL-005','FAC25-1001','Wednesday','08:00:00','17:00:00',1,0,NULL),('AVAIL-006','FAC25-1001','Friday','08:00:00','17:00:00',1,0,NULL),('AVAIL-007','FAC25-1002','Tuesday','08:00:00','17:00:00',1,0,NULL),('AVAIL-008','FAC25-1002','Thursday','08:00:00','17:00:00',1,0,NULL),('AVAIL-009','FAC25-1003','Monday','08:00:00','17:00:00',1,0,NULL);
/*!40000 ALTER TABLE `faculty_availability` ENABLE KEYS */;
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
INSERT INTO `full_tuition_payment` VALUES (1,NULL,'SA25-2111',30000.00,NULL),(2,NULL,'SA25-2111',30000.00,NULL),(3,NULL,'SA25-2111',30000.00,NULL),(4,NULL,'SA25-3706',30000.00,NULL),(5,NULL,'SA25-2111',30000.00,NULL),(6,NULL,'SA25-2111',30000.00,NULL),(7,NULL,'SA25-6251',32000.00,NULL);
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
  `enrollee_id` varchar(20) DEFAULT NULL,
  `invoice_number` varchar(50) DEFAULT NULL,
  `invoice_date` date DEFAULT NULL,
  `total_amount` decimal(11,2) DEFAULT NULL,
  `remarks` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`invoice_id`),
  UNIQUE KEY `invoice_number` (`invoice_number`),
  KEY `payment_id` (`payment_id`),
  KEY `idx_invoice_enrollee` (`enrollee_id`),
  CONSTRAINT `invoice_ibfk_1` FOREIGN KEY (`payment_id`) REFERENCES `payment` (`payment_id`) ON DELETE CASCADE,
  CONSTRAINT `invoice_ibfk_3` FOREIGN KEY (`enrollee_id`) REFERENCES `enrollees` (`enrollee_id`) ON DELETE SET NULL
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `invoice`
--

LOCK TABLES `invoice` WRITE;
/*!40000 ALTER TABLE `invoice` DISABLE KEYS */;
INSERT INTO `invoice` VALUES (1,2,'SA25-2111','INV-20251028-0773','2025-10-28',30000.00,'Tuition Fee Payment'),(2,3,'SA25-2111','INV-20251028-0838','2025-10-28',30000.00,'Tuition Fee Payment'),(3,5,'SA25-2111','INV-20251028-3000','2025-10-28',30000.00,'Tuition Fee Payment'),(4,6,'SA25-2111','INV-20251029-1110','2025-10-28',30000.00,'Tuition Fee Payment');
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
  `enrollee_id` varchar(20) DEFAULT NULL,
  `amount` decimal(11,2) DEFAULT NULL,
  `payment_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `payment_type` enum('Cash','Card','GCash') NOT NULL,
  `remarks` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`payment_id`),
  KEY `idx_payment_enrollee` (`enrollee_id`),
  KEY `idx_payment_type` (`payment_type`),
  KEY `idx_payment_cashier` (`cashier_id`),
  CONSTRAINT `payment_ibfk_1` FOREIGN KEY (`cashier_id`) REFERENCES `cashier` (`cashier_id`) ON DELETE SET NULL,
  CONSTRAINT `payment_ibfk_3` FOREIGN KEY (`enrollee_id`) REFERENCES `enrollees` (`enrollee_id`) ON DELETE SET NULL
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `payment`
--

LOCK TABLES `payment` WRITE;
/*!40000 ALTER TABLE `payment` DISABLE KEYS */;
INSERT INTO `payment` VALUES (1,'CSH25-5665','SA25-2111',30000.00,'2025-10-28 13:04:48','Card','Payment rejected: Your details are not completed!'),(2,'CSH25-5665','SA25-2111',30000.00,'2025-10-28 13:24:59','Card','Payment verified and accepted'),(3,'CSH25-5665','SA25-2111',30000.00,'2025-10-28 14:02:32','Card','Payment verified and accepted'),(4,NULL,'SA25-3706',30000.00,'2025-10-28 14:13:04','Card','Pending cashier verification'),(5,'CSH25-5665','SA25-2111',30000.00,'2025-10-28 15:03:18','Card','Payment verified and accepted'),(6,'CSH25-5665','SA25-2111',30000.00,'2025-10-28 15:09:09','Card','Payment verified and accepted'),(7,NULL,'SA25-6251',32000.00,'2025-10-28 15:16:48','Card','Pending cashier verification'),(8,NULL,'SA25-4009',30000.00,'2025-10-28 20:42:58','Card','Pending cashier verification'),(9,NULL,'SA25-4010',30000.00,'2025-10-28 20:42:58','Card','Pending cashier verification'),(10,NULL,'SA25-4011',30000.00,'2025-10-28 20:42:58','Card','Pending cashier verification'),(11,NULL,'SA25-4012',30000.00,'2025-10-28 20:42:58','Card','Pending cashier verification'),(12,NULL,'SA25-4013',30000.00,'2025-10-28 20:42:58','Card','Pending cashier verification'),(13,NULL,'SA25-4014',30000.00,'2025-10-28 20:42:58','Card','Pending cashier verification'),(14,NULL,'SA25-4015',30000.00,'2025-10-28 20:42:58','Card','Pending cashier verification'),(15,NULL,'SA25-4016',30000.00,'2025-10-28 20:42:58','Card','Pending cashier verification');
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
-- Table structure for table `rooms`
--

DROP TABLE IF EXISTS `rooms`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `rooms` (
  `room_id` varchar(20) NOT NULL,
  `room_code` varchar(50) NOT NULL,
  `building` varchar(100) DEFAULT NULL,
  `floor` int(11) DEFAULT NULL,
  `capacity` int(11) NOT NULL,
  `room_type` enum('Lecture','Laboratory','Workshop','Auditorium','Conference') DEFAULT 'Lecture',
  `has_projector` tinyint(1) DEFAULT '0',
  `has_computer` tinyint(1) DEFAULT '0',
  `has_aircon` tinyint(1) DEFAULT '0',
  `is_available` tinyint(1) DEFAULT '1',
  PRIMARY KEY (`room_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `rooms`
--

LOCK TABLES `rooms` WRITE;
/*!40000 ALTER TABLE `rooms` DISABLE KEYS */;
INSERT INTO `rooms` VALUES ('AUD-001','AUDI-001','Main Building',1,200,'Auditorium',1,1,1,1),('CONF-001','CONF-001','Admin Building',2,20,'Conference',1,1,1,1),('LAB-101','CL-101','Computer Laboratory',1,30,'Laboratory',1,1,1,1),('LAB-102','CL-102','Computer Laboratory',1,30,'Laboratory',1,1,1,1),('LAB-103','CL-103','Computer Laboratory',1,30,'Laboratory',1,1,1,1),('LAB-104','CL-104','Computer Laboratory',1,35,'Laboratory',1,1,1,1),('LAB-201','SL-201','Science Laboratory',2,25,'Laboratory',0,0,1,1),('ROOM-101','GEB-101','General Education Building',1,40,'Lecture',1,0,1,1),('ROOM-102','GEB-102','General Education Building',1,40,'Lecture',1,0,1,1),('ROOM-103','GEB-103','General Education Building',1,40,'Lecture',1,0,1,1),('ROOM-201','GEB-201','General Education Building',2,35,'Lecture',1,0,1,1),('ROOM-202','GEB-202','General Education Building',2,35,'Lecture',1,0,1,1),('ROOM-203','GEB-203','General Education Building',2,40,'Lecture',1,0,0,1),('ROOM-301','GEB-301','General Education Building',3,45,'Lecture',1,1,1,1),('ROOM-302','GEB-302','General Education Building',3,45,'Lecture',1,1,1,1),('ROOM-303','GEB-303','General Education Building',3,50,'Lecture',1,0,1,1);
/*!40000 ALTER TABLE `rooms` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `schedule_generation_log`
--

DROP TABLE IF EXISTS `schedule_generation_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `schedule_generation_log` (
  `log_id` int(11) NOT NULL AUTO_INCREMENT,
  `semester_id` varchar(20) NOT NULL,
  `generated_by` int(11) DEFAULT NULL,
  `generation_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `status` enum('Success','Failed','Partial') DEFAULT 'Success',
  `total_offerings` int(11) DEFAULT '0',
  `scheduled_offerings` int(11) DEFAULT '0',
  `conflicts_found` int(11) DEFAULT '0',
  `execution_time_ms` int(11) DEFAULT NULL,
  `error_message` text,
  PRIMARY KEY (`log_id`),
  KEY `semester_id` (`semester_id`),
  KEY `generated_by` (`generated_by`),
  CONSTRAINT `schedule_generation_log_ibfk_1` FOREIGN KEY (`semester_id`) REFERENCES `semester` (`semester_id`) ON DELETE CASCADE,
  CONSTRAINT `schedule_generation_log_ibfk_2` FOREIGN KEY (`generated_by`) REFERENCES `users` (`user_id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `schedule_generation_log`
--

LOCK TABLES `schedule_generation_log` WRITE;
/*!40000 ALTER TABLE `schedule_generation_log` DISABLE KEYS */;
/*!40000 ALTER TABLE `schedule_generation_log` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `scheduling_constraints`
--

DROP TABLE IF EXISTS `scheduling_constraints`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `scheduling_constraints` (
  `constraint_id` varchar(20) NOT NULL,
  `constraint_type` enum('RoomConflict','TimeConflict','FacultyConflict','StudentConflict','Custom') NOT NULL,
  `entity_id` varchar(20) NOT NULL,
  `time_slot_id` varchar(20) DEFAULT NULL,
  `room_id` varchar(20) DEFAULT NULL,
  `day_of_week` enum('Monday','Tuesday','Wednesday','Thursday','Friday','Saturday','Sunday') DEFAULT NULL,
  `semester_id` varchar(20) NOT NULL,
  `is_hard_constraint` tinyint(1) DEFAULT '1',
  `description` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`constraint_id`),
  KEY `semester_id` (`semester_id`),
  KEY `time_slot_id` (`time_slot_id`),
  KEY `room_id` (`room_id`),
  CONSTRAINT `scheduling_constraints_ibfk_3` FOREIGN KEY (`room_id`) REFERENCES `rooms` (`room_id`) ON DELETE SET NULL,
  CONSTRAINT `scheduling_constraints_ibfk_1` FOREIGN KEY (`semester_id`) REFERENCES `semester` (`semester_id`) ON DELETE CASCADE,
  CONSTRAINT `scheduling_constraints_ibfk_2` FOREIGN KEY (`time_slot_id`) REFERENCES `time_slots` (`time_slot_id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `scheduling_constraints`
--

LOCK TABLES `scheduling_constraints` WRITE;
/*!40000 ALTER TABLE `scheduling_constraints` DISABLE KEYS */;
/*!40000 ALTER TABLE `scheduling_constraints` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `section`
--

DROP TABLE IF EXISTS `section`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `section` (
  `section_id` varchar(20) NOT NULL,
  `section_name` varchar(50) NOT NULL,
  `academic_year_id` varchar(20) DEFAULT NULL,
  `program_id` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`section_id`),
  KEY `academic_year_id` (`academic_year_id`),
  KEY `program_id` (`program_id`),
  CONSTRAINT `section_ibfk_1` FOREIGN KEY (`academic_year_id`) REFERENCES `academic_year` (`academic_year_id`) ON DELETE SET NULL,
  CONSTRAINT `section_ibfk_2` FOREIGN KEY (`program_id`) REFERENCES `programs` (`program_id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `section`
--

LOCK TABLES `section` WRITE;
/*!40000 ALTER TABLE `section` DISABLE KEYS */;
INSERT INTO `section` VALUES ('SEC-BSBA-1A','BSBA 1-A','AY2025-2026','PROG-04'),('SEC-BSBA-2A','BSBA 2-A','AY2025-2026','PROG-04'),('SEC-BSCS-1A','BSCS 1-A','AY2025-2026','PROG-02'),('SEC-BSCS-2A','BSCS 2-A','AY2025-2026','PROG-02'),('SEC-BSCS-3A','BSCS 3-A','AY2025-2026','PROG-02'),('SEC-BSCS-4A','BSCS 4-A','AY2025-2026','PROG-02'),('SEC-BSED-1A','BSED 1-A','AY2025-2026','PROG-03'),('SEC-BSED-2A','BSED 2-A','AY2025-2026','PROG-03'),('SEC-BSIT-1A','BSIT 1-A','AY2025-2026','PROG-01'),('SEC-BSIT-1B','BSIT 1-B','AY2025-2026','PROG-01'),('SEC-BSIT-2A','BSIT 2-A','AY2025-2026','PROG-01'),('SEC-BSIT-2B','BSIT 2-B','AY2025-2026','PROG-01'),('SEC-BSIT-3A','BSIT 3-A','AY2025-2026','PROG-01'),('SEC-BSIT-3B','BSIT 3-B','AY2025-2026','PROG-01'),('SEC-BSIT-4A','BSIT 4-A','AY2025-2026','PROG-01'),('SEC-BSIT-4B','BSIT 4-B','AY2025-2026','PROG-01');
/*!40000 ALTER TABLE `section` ENABLE KEYS */;
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
INSERT INTO `semester` VALUES ('SEM2024-1ST','1st','AY2024-2025','2024-08-01','2024-12-15',0),('SEM2024-2ND','2nd','AY2024-2025','2025-01-06','2025-05-31',0),('SEM2024-SUM','Summer','AY2024-2025','2025-06-01','2025-07-31',0),('SEM2025-1ST','1st','AY2025-2026','2025-08-01','2025-12-15',1),('SEM2025-2ND','2nd','AY2025-2026','2026-01-06','2026-05-31',0),('SEM2025-SUM','Summer','AY2025-2026','2026-06-01','2026-07-31',0);
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
INSERT INTO `students` VALUES ('STU25-1001',27,'PROG-01','Active','2025-08-15 16:00:00'),('STU25-1002',28,'PROG-01','Active','2025-08-15 16:30:00'),('STU25-1003',29,'PROG-01','Active','2025-08-15 17:00:00'),('STU25-1004',30,'PROG-02','Active','2025-08-15 17:30:00'),('STU25-1005',31,'PROG-01','Active','2025-08-15 18:00:00'),('STU25-1006',32,'PROG-02','Active','2025-08-15 18:30:00'),('STU25-1007',33,'PROG-01','Active','2025-08-15 19:00:00'),('STU25-1008',34,'PROG-03','Active','2025-08-15 19:30:00'),('STU25-1009',35,'PROG-01','Active','2025-08-15 20:00:00'),('STU25-1010',36,'PROG-04','Active','2025-08-15 20:30:00'),('STU25-9206',16,'PROG-01','Active','2025-10-28 10:19:33');
/*!40000 ALTER TABLE `students` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `time_slots`
--

DROP TABLE IF EXISTS `time_slots`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `time_slots` (
  `time_slot_id` varchar(20) NOT NULL,
  `day_of_week` enum('Monday','Tuesday','Wednesday','Thursday','Friday','Saturday','Sunday') NOT NULL,
  `start_time` time NOT NULL,
  `end_time` time NOT NULL,
  `slot_label` varchar(50) DEFAULT NULL,
  `is_available` tinyint(1) DEFAULT '1',
  PRIMARY KEY (`time_slot_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `time_slots`
--

LOCK TABLES `time_slots` WRITE;
/*!40000 ALTER TABLE `time_slots` DISABLE KEYS */;
INSERT INTO `time_slots` VALUES ('TS001','Monday','08:00:00','09:00:00','Period 1',1),('TS002','Monday','09:00:00','10:00:00','Period 2',1),('TS003','Monday','10:00:00','11:00:00','Period 3',1),('TS004','Monday','11:00:00','12:00:00','Period 4',1),('TS005','Monday','12:00:00','13:00:00','Universal Free Time',0),('TS006','Monday','13:00:00','14:00:00','Period 5',1),('TS007','Monday','14:00:00','15:00:00','Period 6',1),('TS008','Monday','15:00:00','16:00:00','Period 7',1),('TS009','Monday','16:00:00','17:00:00','Period 8',1),('TS010','Tuesday','08:00:00','09:00:00','Period 1',1),('TS011','Tuesday','09:00:00','10:00:00','Period 2',1),('TS012','Tuesday','10:00:00','11:00:00','Period 3',1),('TS013','Tuesday','11:00:00','12:00:00','Period 4',1),('TS014','Tuesday','12:00:00','13:00:00','Universal Free Time',0),('TS015','Tuesday','13:00:00','14:00:00','Period 5',1),('TS016','Tuesday','14:00:00','15:00:00','Period 6',1),('TS017','Tuesday','15:00:00','16:00:00','Period 7',1),('TS018','Tuesday','16:00:00','17:00:00','Period 8',1),('TS019','Wednesday','08:00:00','09:00:00','Period 1',1),('TS020','Wednesday','09:00:00','10:00:00','Period 2',1),('TS021','Wednesday','10:00:00','11:00:00','Period 3',1),('TS022','Wednesday','11:00:00','12:00:00','Period 4',1),('TS023','Wednesday','12:00:00','13:00:00','Universal Free Time',0),('TS024','Wednesday','13:00:00','14:00:00','Period 5',1),('TS025','Wednesday','14:00:00','15:00:00','Period 6',1),('TS026','Wednesday','15:00:00','16:00:00','Period 7',1),('TS027','Wednesday','16:00:00','17:00:00','Period 8',1),('TS028','Thursday','08:00:00','09:00:00','Period 1',1),('TS029','Thursday','09:00:00','10:00:00','Period 2',1),('TS030','Thursday','10:00:00','11:00:00','Period 3',1),('TS031','Thursday','11:00:00','12:00:00','Period 4',1),('TS032','Thursday','12:00:00','13:00:00','Universal Free Time',0),('TS033','Thursday','13:00:00','14:00:00','Period 5',1),('TS034','Thursday','14:00:00','15:00:00','Period 6',1),('TS035','Thursday','15:00:00','16:00:00','Period 7',1),('TS036','Thursday','16:00:00','17:00:00','Period 8',1),('TS037','Friday','08:00:00','09:00:00','Period 1',1),('TS038','Friday','09:00:00','10:00:00','Period 2',1),('TS039','Friday','10:00:00','11:00:00','Period 3',1),('TS040','Friday','11:00:00','12:00:00','Period 4',1),('TS041','Friday','12:00:00','13:00:00','Universal Free Time',0),('TS042','Friday','13:00:00','14:00:00','Period 5',1),('TS043','Friday','14:00:00','15:00:00','Period 6',1),('TS044','Friday','15:00:00','16:00:00','Period 7',1),('TS045','Friday','16:00:00','17:00:00','Period 8',1);
/*!40000 ALTER TABLE `time_slots` ENABLE KEYS */;
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
) ENGINE=InnoDB AUTO_INCREMENT=179 DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (1,'Joshua_Santiago','joshuasantiago0921@gmail.com','Joshua@21','Enrollees','2025-10-26 13:57:36',1),(6,'adminUser','admin@email.com','securePass123','Admin','2025-10-27 06:36:29',1),(7,'john1doe','john.doe@example.com','securePassword','Cashier','2025-10-27 15:19:39',1),(9,'Wesley1006','wesley@gmail.com','test123','Enrollees','2025-10-27 11:27:14',1),(10,'Dave6969','davepatuwad@gmail.com','malimodave','Enrollees','2025-10-27 11:54:06',1),(13,'john.doe','john.doe@university.edu','faculty123','Faculty','2025-10-28 09:55:55',1),(16,'jane.smith','jane.smith@university.edu','student123','Student','2025-10-28 10:19:34',1),(17,'RandomStuden1','EnrolleeRandom1@gmail.com','123456','Enrollees','2025-10-28 15:10:53',1),(18,'maria.cruz','maria.cruz@university.edu','faculty456','Faculty','2025-10-28 15:56:07',1),(19,'robert.garcia','robert.garcia@university.edu','faculty789','Faculty','2025-10-28 15:56:07',1),(20,'lisa.reyes','lisa.reyes@university.edu','faculty101','Faculty','2025-10-28 15:56:07',1),(21,'michael.santos','michael.santos@university.edu','faculty202','Faculty','2025-10-28 15:56:07',1),(22,'anna.lopez','anna.lopez@university.edu','faculty303','Faculty','2025-10-28 15:56:07',1),(23,'david.rivera','david.rivera@university.edu','faculty404','Faculty','2025-10-28 15:56:07',1),(24,'sarah.flores','sarah.flores@university.edu','faculty505','Faculty','2025-10-28 15:56:07',1),(25,'james.torres','james.torres@university.edu','faculty606','Faculty','2025-10-28 15:56:07',1),(26,'patricia.ramos','patricia.ramos@university.edu','faculty707','Faculty','2025-10-28 15:56:07',1),(27,'carlos.mendoza','carlos.mendoza@university.edu','faculty808','Faculty','2025-10-28 15:56:07',1),(28,'mark.johnson','mark.johnson@student.edu','student456','Student','2025-10-28 15:56:07',1),(29,'emma.wilson','emma.wilson@student.edu','student789','Student','2025-10-28 15:56:07',1),(30,'lucas.brown','lucas.brown@student.edu','student101','Student','2025-10-28 15:56:07',1),(31,'sophia.davis','sophia.davis@student.edu','student202','Student','2025-10-28 15:56:07',1),(32,'oliver.martinez','oliver.martinez@student.edu','student303','Student','2025-10-28 15:56:07',1),(33,'ava.anderson','ava.anderson@student.edu','student404','Student','2025-10-28 15:56:07',1),(34,'noah.taylor','noah.taylor@student.edu','student505','Student','2025-10-28 15:56:07',1),(35,'isabella.thomas','isabella.thomas@student.edu','student606','Student','2025-10-28 15:56:07',1),(36,'ethan.moore','ethan.moore@student.edu','student707','Student','2025-10-28 15:56:07',1),(37,'mia.jackson','mia.jackson@student.edu','student808','Student','2025-10-28 15:56:07',1),(38,'alex.white','alex.white@email.com','enrollee123','Enrollees','2025-10-28 15:56:07',1),(39,'charlotte.harris','charlotte.harris@email.com','enrollee456','Enrollees','2025-10-28 15:56:07',1),(40,'daniel.clark','daniel.clark@email.com','enrollee789','Enrollees','2025-10-28 15:56:07',1),(41,'amelia.lewis','amelia.lewis@email.com','enrollee101','Enrollees','2025-10-28 15:56:07',1),(42,'matthew.walker','matthew.walker@email.com','enrollee202','Enrollees','2025-10-28 15:56:07',1),(43,'harper.hall','harper.hall@email.com','enrollee303','Enrollees','2025-10-28 15:56:07',1),(44,'jackson.allen','jackson.allen@email.com','enrollee404','Enrollees','2025-10-28 15:56:07',1),(45,'ella.young','ella.young@email.com','enrollee505','Enrollees','2025-10-28 15:56:07',1),(163,'liam_johnson','liam.johnson@email.com','pass123','Enrollees','2025-10-28 20:28:39',1),(164,'olivia_smith','olivia.smith@email.com','pass123','Enrollees','2025-10-28 20:28:39',1),(165,'noah_davis','noah.davis@email.com','pass123','Enrollees','2025-10-28 20:28:39',1),(166,'emma_martinez','emma.martinez@email.com','pass123','Enrollees','2025-10-28 20:28:39',1),(167,'ava_brown','ava.brown@email.com','pass123','Enrollees','2025-10-28 20:28:39',1),(168,'william_lee','william.lee@email.com','pass123','Enrollees','2025-10-28 20:28:39',1),(169,'sophia_wilson','sophia.wilson@email.com','pass123','Enrollees','2025-10-28 20:28:39',1),(170,'james_taylor','james.taylor@email.com','pass123','Enrollees','2025-10-28 20:28:39',1);
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping events for database 'sql12804580'
--
