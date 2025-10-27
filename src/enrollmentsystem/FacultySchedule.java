package enrollmentsystem;

import javafx.beans.property.*;

/**
 * Model class for Faculty Schedule table view
 */
public class FacultySchedule {
    private final StringProperty courseCode;
    private final StringProperty courseName;
    private final StringProperty section;
    private final StringProperty day;
    private final StringProperty time;
    private final StringProperty room;
    private final StringProperty offeringId;
    
    public FacultySchedule(String courseCode, String courseName, String section, 
                          String day, String time, String room, String offeringId) {
        this.courseCode = new SimpleStringProperty(courseCode);
        this.courseName = new SimpleStringProperty(courseName);
        this.section = new SimpleStringProperty(section);
        this.day = new SimpleStringProperty(day);
        this.time = new SimpleStringProperty(time);
        this.room = new SimpleStringProperty(room);
        this.offeringId = new SimpleStringProperty(offeringId);
    }
    
    // Course Code
    public String getCourseCode() { return courseCode.get(); }
    public void setCourseCode(String value) { courseCode.set(value); }
    public StringProperty courseCodeProperty() { return courseCode; }
    
    // Course Name
    public String getCourseName() { return courseName.get(); }
    public void setCourseName(String value) { courseName.set(value); }
    public StringProperty courseNameProperty() { return courseName; }
    
    // Section
    public String getSection() { return section.get(); }
    public void setSection(String value) { section.set(value); }
    public StringProperty sectionProperty() { return section; }
    
    // Day
    public String getDay() { return day.get(); }
    public void setDay(String value) { day.set(value); }
    public StringProperty dayProperty() { return day; }
    
    // Time
    public String getTime() { return time.get(); }
    public void setTime(String value) { time.set(value); }
    public StringProperty timeProperty() { return time; }
    
    // Room
    public String getRoom() { return room.get(); }
    public void setRoom(String value) { room.set(value); }
    public StringProperty roomProperty() { return room; }
    
    // Offering ID (hidden, for reference)
    public String getOfferingId() { return offeringId.get(); }
    public void setOfferingId(String value) { offeringId.set(value); }
    public StringProperty offeringIdProperty() { return offeringId; }
}