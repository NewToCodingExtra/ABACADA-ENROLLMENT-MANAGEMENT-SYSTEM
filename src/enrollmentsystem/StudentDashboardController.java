package enrollmentsystem;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.beans.property.*;
import java.sql.*;

public class StudentDashboardController {

    @FXML private Label studentNameLabel;
    @FXML private Label studentEmailLabel;
    @FXML private Label studentIdLabel;
    @FXML private TabPane mainTabPane;
    @FXML private HBox topBar;
    @FXML private VBox sidebar;
    @FXML private VBox mainContainer;
    
    // Overview Tab
    @FXML private TableView<StudentOverview> overViewTable;
    @FXML private TableColumn<StudentOverview, String> semCol;
    @FXML private TableColumn<StudentOverview, String> courseCol;
    @FXML private TableColumn<StudentOverview, String> sectionCol;
    @FXML private TableColumn<StudentOverview, String> yearCol;
    
    // Evaluation Tab
    @FXML private TableView<StudentGrade> evalTable;
    @FXML private TableColumn<StudentGrade, String> evalCol;
    @FXML private TableColumn<StudentGrade, String> statusCol;
    
    // Schedule Tab
    @FXML private TableView<StudentSchedule> scheduleTable;
    @FXML private TableColumn<StudentSchedule, String> courseCode;
    @FXML private TableColumn<StudentSchedule, String> courseNameCol;
    @FXML private TableColumn<StudentSchedule, String> faculrtCol;
    @FXML private TableColumn<StudentSchedule, String> roomCol;
    @FXML private TableColumn<StudentSchedule, String> dayCol;
    @FXML private TableColumn<StudentSchedule, String> timeCol;

    private Student currentStudent;
    private ObservableList<StudentOverview> overviewList;
    private ObservableList<StudentGrade> gradesList;
    private ObservableList<StudentSchedule> scheduleList;

    
    @FXML
    public void initialize() {
        System.out.println("=== Student Dashboard Initializing ===");
        
        // Add debug info
        String studentId = SessionManager.getInstance().getStudentId();
        System.out.println("Student ID from session: " + studentId);
        
        setupTableColumns();
        loadStudentData();
        loadOverviewData();
        loadGradesData();
        loadScheduleData();
        
        System.out.println("=== Initialization Complete ===");
    }
    
    private void setupTableColumns() {
        System.out.println("Setting up table columns...");
        
        // Overview Table
        semCol.setCellValueFactory(new PropertyValueFactory<>("semester"));
        courseCol.setCellValueFactory(new PropertyValueFactory<>("course"));
        sectionCol.setCellValueFactory(new PropertyValueFactory<>("section"));
        yearCol.setCellValueFactory(new PropertyValueFactory<>("yearLevel"));
        overviewList = FXCollections.observableArrayList();
        overViewTable.setItems(overviewList);
        
        // Grades Table
        evalCol.setCellValueFactory(new PropertyValueFactory<>("courseName"));
        statusCol.setCellValueFactory(new PropertyValueFactory<>("grade"));
        gradesList = FXCollections.observableArrayList();
        evalTable.setItems(gradesList);
        
        // Schedule Table
        courseCode.setCellValueFactory(new PropertyValueFactory<>("courseCode"));
        courseNameCol.setCellValueFactory(new PropertyValueFactory<>("courseName"));
        faculrtCol.setCellValueFactory(new PropertyValueFactory<>("faculty"));
        roomCol.setCellValueFactory(new PropertyValueFactory<>("room"));
        dayCol.setCellValueFactory(new PropertyValueFactory<>("day"));
        timeCol.setCellValueFactory(new PropertyValueFactory<>("time"));
        scheduleList = FXCollections.observableArrayList();
        scheduleTable.setItems(scheduleList);
        
        System.out.println("Table columns set up successfully");
    }
    
    private void loadStudentData() {
        System.out.println("\n--- Loading Student Data ---");
        
        String studentId = SessionManager.getInstance().getStudentId();
        
        if (studentId == null || studentId.isEmpty()) {
            System.err.println("ERROR: No student ID found in session!");
            showError("Session Error", "No student ID found. Please log in again.");
            return;
        }
        
        System.out.println("Loading student with ID: " + studentId);
        currentStudent = Student.loadById(studentId);
        
        if (currentStudent != null) {
            System.out.println("Student loaded successfully: " + currentStudent.getUsername());
            
            // Load and display student profile
            StudentProfile profile = loadStudentProfile(studentId);
            if (profile != null) {
                studentNameLabel.setText(profile.getFullName());
                studentEmailLabel.setText(currentStudent.getEmail());
                studentIdLabel.setText(studentId);
                
                System.out.println("Profile displayed:");
                System.out.println("  Name: " + profile.getFullName());
                System.out.println("  Email: " + currentStudent.getEmail());
                System.out.println("  ID: " + studentId);
            } else {
                System.err.println("WARNING: Could not load student profile!");
                // Set basic info even if profile fails
                studentNameLabel.setText(currentStudent.getUsername());
                studentEmailLabel.setText(currentStudent.getEmail());
                studentIdLabel.setText(studentId);
            }
        } else {
            System.err.println("ERROR: Failed to load student data for ID: " + studentId);
            showError("Data Error", "Failed to load student information.");
        }
    }
    
    private StudentProfile loadStudentProfile(String studentId) {
        System.out.println("Loading student profile from student_record...");
        
        try (Connection conn = DBConnection.getConnection()) {
            if (conn == null) {
                System.err.println("ERROR: Database connection is null!");
                return null;
            }
            
            String query = "SELECT sr.first_name, sr.middle_name, sr.last_name, " +
                          "sr.suffix, p.program_name, sr.year_level " +
                          "FROM student_record sr " +
                          "JOIN programs p ON sr.program_id = p.program_id " +
                          "WHERE sr.student_id = ? " +
                          "ORDER BY sr.record_date DESC LIMIT 1";
            
            try (PreparedStatement ps = conn.prepareStatement(query)) {
                ps.setString(1, studentId);
                ResultSet rs = ps.executeQuery();
                
                if (rs.next()) {
                    String firstName = rs.getString("first_name");
                    String middleName = rs.getString("middle_name");
                    String lastName = rs.getString("last_name");
                    String suffix = rs.getString("suffix");
                    String program = rs.getString("program_name");
                    String yearLevel = rs.getString("year_level");
                    
                    String fullName = firstName + " " + 
                                    (middleName != null && !middleName.isEmpty() ? 
                                        middleName.charAt(0) + ". " : "") + 
                                    lastName + 
                                    (suffix != null && !suffix.isEmpty() ? " " + suffix : "");
                    
                    System.out.println("Profile found: " + fullName);
                    return new StudentProfile(fullName, program, yearLevel);
                } else {
                    System.err.println("No student_record found for student ID: " + studentId);
                }
            }
        } catch (SQLException e) {
            System.err.println("SQL Error loading student profile: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
    
    private void loadOverviewData() {
        System.out.println("\n--- Loading Overview Data ---");
        
        if (currentStudent == null) {
            System.err.println("ERROR: currentStudent is null, cannot load overview");
            return;
        }
        
        overviewList.clear();
        
        try (Connection conn = DBConnection.getConnection()) {
            if (conn == null) {
                System.err.println("ERROR: Database connection is null!");
                return;
            }
            
            // Fixed query with proper semester name handling
            String query = "SELECT DISTINCT " +
                          "CONCAT(s.name, ' Semester ', SUBSTRING(ay.year_label, 1, 9)) as semester, " +
                          "p.program_code as program, " +
                          "sec.section_name, " +
                          "sr.year_level " +
                          "FROM enrollments e " +
                          "JOIN course_offerings co ON e.offering_id = co.offering_id " +
                          "JOIN semester s ON co.semester_id = s.semester_id " +
                          "JOIN academic_year ay ON s.academic_year_id = ay.academic_year_id " +
                          "JOIN section sec ON co.section_id = sec.section_id " +
                          "JOIN students st ON e.student_id = st.student_id " +
                          "JOIN programs p ON st.program_id = p.program_id " +
                          "LEFT JOIN student_record sr ON e.student_id = sr.student_id " +
                          "WHERE e.student_id = ? AND e.status = 'Enrolled' " +
                          "ORDER BY s.start_date DESC " +
                          "LIMIT 1";
            
            try (PreparedStatement ps = conn.prepareStatement(query)) {
                ps.setString(1, currentStudent.getStudentId());
                
                System.out.println("Executing overview query for student: " + currentStudent.getStudentId());
                ResultSet rs = ps.executeQuery();
                
                if (rs.next()) {
                    StudentOverview overview = new StudentOverview(
                        rs.getString("semester"),
                        rs.getString("program"),
                        rs.getString("section_name"),
                        rs.getString("year_level") != null ? rs.getString("year_level") : "N/A"
                    );
                    overviewList.add(overview);
                    
                    System.out.println("Overview data loaded:");
                    System.out.println("  Semester: " + overview.getSemester());
                    System.out.println("  Program: " + overview.getCourse());
                    System.out.println("  Section: " + overview.getSection());
                    System.out.println("  Year: " + overview.getYearLevel());
                } else {
                    System.out.println("No overview data found - student may not be enrolled in any courses");
                }
            }
        } catch (SQLException e) {
            System.err.println("SQL Error loading overview data: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void loadGradesData() {
        System.out.println("\n--- Loading Grades Data ---");
        
        if (currentStudent == null) {
            System.err.println("ERROR: currentStudent is null, cannot load grades");
            return;
        }
        
        gradesList.clear();
        
        try (Connection conn = DBConnection.getConnection()) {
            if (conn == null) {
                System.err.println("ERROR: Database connection is null!");
                return;
            }
            
            String query = "SELECT c.course_title, e.grade, e.status " +
                          "FROM enrollments e " +
                          "JOIN course_offerings co ON e.offering_id = co.offering_id " +
                          "JOIN courses c ON co.course_id = c.course_id " +
                          "WHERE e.student_id = ? " +
                          "ORDER BY co.semester_id DESC";
            
            try (PreparedStatement ps = conn.prepareStatement(query)) {
                ps.setString(1, currentStudent.getStudentId());
                
                System.out.println("Executing grades query for student: " + currentStudent.getStudentId());
                ResultSet rs = ps.executeQuery();
                
                int count = 0;
                while (rs.next()) {
                    String grade = rs.getString("grade");
                    String status = rs.getString("status");
                    
                    String displayGrade = grade != null && !grade.isEmpty() ? grade : 
                                        (status.equals("Enrolled") ? "In Progress" : "No Grade");
                    
                    StudentGrade gradeEntry = new StudentGrade(
                        rs.getString("course_title"),
                        displayGrade
                    );
                    gradesList.add(gradeEntry);
                    count++;
                }
                
                System.out.println("Loaded " + count + " grade entries");
            }
        } catch (SQLException e) {
            System.err.println("SQL Error loading grades data: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void loadScheduleData() {
        System.out.println("\n--- Loading Schedule Data ---");
        
        if (currentStudent == null) {
            System.err.println("ERROR: currentStudent is null, cannot load schedule");
            return;
        }
        
        scheduleList.clear();
        
        try (Connection conn = DBConnection.getConnection()) {
            if (conn == null) {
                System.err.println("ERROR: Database connection is null!");
                return;
            }
            
            String query = "SELECT c.course_code, c.course_title, " +
                          "CONCAT(f.first_name, ' ', f.last_name) as faculty_name, " +
                          "co.room, co.schedule_day, co.schedule_time " +
                          "FROM enrollments e " +
                          "JOIN course_offerings co ON e.offering_id = co.offering_id " +
                          "JOIN courses c ON co.course_id = c.course_id " +
                          "LEFT JOIN faculty f ON co.faculty_id = f.faculty_id " +
                          "WHERE e.student_id = ? AND e.status = 'Enrolled' " +
                          "AND co.status = 'Open' " +
                          "ORDER BY " +
                          "CASE co.schedule_day " +
                          "  WHEN 'Monday' THEN 1 " +
                          "  WHEN 'Tuesday' THEN 2 " +
                          "  WHEN 'Wednesday' THEN 3 " +
                          "  WHEN 'Thursday' THEN 4 " +
                          "  WHEN 'Friday' THEN 5 " +
                          "  WHEN 'Saturday' THEN 6 " +
                          "  WHEN 'Sunday' THEN 7 " +
                          "  ELSE 8 END, " +
                          "co.schedule_time";
            
            try (PreparedStatement ps = conn.prepareStatement(query)) {
                ps.setString(1, currentStudent.getStudentId());
                
                System.out.println("Executing schedule query for student: " + currentStudent.getStudentId());
                ResultSet rs = ps.executeQuery();
                
                int count = 0;
                while (rs.next()) {
                    String day = rs.getString("schedule_day");
                    String time = rs.getString("schedule_time");
                    String room = rs.getString("room");
                    String facultyName = rs.getString("faculty_name");
                    
                    StudentSchedule schedule = new StudentSchedule(
                        rs.getString("course_code"),
                        rs.getString("course_title"),
                        facultyName != null ? facultyName : "TBA",
                        room != null ? room : "TBA",
                        day != null ? day : "TBA",
                        time != null ? time : "TBA"
                    );
                    scheduleList.add(schedule);
                    count++;
                }
                
                System.out.println("Loaded " + count + " schedule entries");
            }
        } catch (SQLException e) {
            System.err.println("SQL Error loading schedule data: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    // Data Classes
    
    public static class StudentOverview {
        private final StringProperty semester;
        private final StringProperty course;
        private final StringProperty section;
        private final StringProperty yearLevel;
        
        public StudentOverview(String semester, String course, String section, String yearLevel) {
            this.semester = new SimpleStringProperty(semester);
            this.course = new SimpleStringProperty(course);
            this.section = new SimpleStringProperty(section);
            this.yearLevel = new SimpleStringProperty(yearLevel);
        }
        
        public String getSemester() { return semester.get(); }
        public String getCourse() { return course.get(); }
        public String getSection() { return section.get(); }
        public String getYearLevel() { return yearLevel.get(); }
    }
    
    public static class StudentGrade {
        private final StringProperty courseName;
        private final StringProperty grade;
        
        public StudentGrade(String courseName, String grade) {
            this.courseName = new SimpleStringProperty(courseName);
            this.grade = new SimpleStringProperty(grade);
        }
        
        public String getCourseName() { return courseName.get(); }
        public String getGrade() { return grade.get(); }
    }
    
    public static class StudentSchedule {
        private final StringProperty courseCode;
        private final StringProperty courseName;
        private final StringProperty faculty;
        private final StringProperty room;
        private final StringProperty day;
        private final StringProperty time;
        
        public StudentSchedule(String courseCode, String courseName, String faculty,
                              String room, String day, String time) {
            this.courseCode = new SimpleStringProperty(courseCode);
            this.courseName = new SimpleStringProperty(courseName);
            this.faculty = new SimpleStringProperty(faculty);
            this.room = new SimpleStringProperty(room);
            this.day = new SimpleStringProperty(day);
            this.time = new SimpleStringProperty(time);
        }
        
        public String getCourseCode() { return courseCode.get(); }
        public String getCourseName() { return courseName.get(); }
        public String getFaculty() { return faculty.get(); }
        public String getRoom() { return room.get(); }
        public String getDay() { return day.get(); }
        public String getTime() { return time.get(); }
    }
    
    public static class StudentProfile {
        private final String fullName;
        private final String program;
        private final String yearLevel;
        
        public StudentProfile(String fullName, String program, String yearLevel) {
            this.fullName = fullName;
            this.program = program;
            this.yearLevel = yearLevel;
        }
        
        public String getFullName() { return fullName; }
        public String getProgram() { return program; }
        public String getYearLevel() { return yearLevel; }
    }
    
    public void refreshDashboard() {
        if (currentStudent != null) {
            loadOverviewData();
            loadGradesData();
            loadScheduleData();
        }
    }
}