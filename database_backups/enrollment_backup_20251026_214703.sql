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
  `user_id` int(11) DEFAULT NULL,
  `first_name` varchar(100) DEFAULT NULL,
  `last_name` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`admin_id`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `admin_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `admin`
--

LOCK TABLES `admin` WRITE;
/*!40000 ALTER TABLE `admin` DISABLE KEYS */;
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
  PRIMARY KEY (`course_id`),
  KEY `program_id` (`program_id`),
  CONSTRAINT `courses_ibfk_1` FOREIGN KEY (`program_id`) REFERENCES `programs` (`program_id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `courses`
--

LOCK TABLES `courses` WRITE;
/*!40000 ALTER TABLE `courses` DISABLE KEYS */;
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
  `user_id` int(11) DEFAULT NULL,
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
  CONSTRAINT `enrollees_ibfk_2` FOREIGN KEY (`program_applied_for`) REFERENCES `programs` (`program_id`) ON DELETE SET NULL,
  CONSTRAINT `enrollees_ibfk_3` FOREIGN KEY (`reviewed_by`) REFERENCES `admin` (`user_id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `enrollees`
--

LOCK TABLES `enrollees` WRITE;
/*!40000 ALTER TABLE `enrollees` DISABLE KEYS */;
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping events for database 'sql12804580'
--
