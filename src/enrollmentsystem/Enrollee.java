package enrollmentsystem;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Random;

public class Enrollee extends User implements DashBoardAccesible, UniqueIDGenerator{
    private String enrolleeId;
    private String firstName;
    private String middleName;
    private String lastName;
    private String suffix;
    private LocalDate birthDate;
    private String gender;
    private String address;
    private String province;
    private String city;
    private String contactNumber;
    private String emailAddress;
    private String guardianName;
    private String guardianContact;
    private String yearLevel;
    private String studentType;
    private String lastSchoolAttended;
    private String lastSchoolYear;
    private String programAppliedFor;
    private String photoLink;
    private String birthCertLink;
    private String reportCardLink;
    private String form137Link;
    private String enrollmentStatus;
    private LocalDateTime dateApplied;
    private Integer reviewedBy;
    private LocalDateTime reviewedOn;
    private boolean hasFilledUpForm;

    public Enrollee() {
        super();
    }

      public Enrollee(int userId, String username, String email, String password,
                    String access, LocalDateTime createdAt, boolean isActive,
                    String enrolleeId, String firstName, String middleName,String lastName, String suffix,
                    LocalDate birthDate, String gender, String address, String province,
                    String city, String contactNumber, String emailAddress,
                    String guardianName, String guardianContact, String yearLevel, String studentType, String lastSchoolAttended,
                    String lastSchoolYear, String programAppliedFor, String enrollmentStatus,
                    LocalDateTime dateApplied, Integer reviewedBy, LocalDateTime reviewedOn) {

        super(userId, username, email, password, access, createdAt, isActive);
        this.enrolleeId = enrolleeId;
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.suffix = suffix;
        this.birthDate = birthDate;
        this.gender = gender;
        this.address = address;
        this.province = province;
        this.city = city;
        this.contactNumber = contactNumber;
        this.emailAddress = emailAddress;
        this.guardianName = guardianName;
        this.guardianContact = guardianContact;
        this.yearLevel = yearLevel;
        this.studentType = studentType;
        this.lastSchoolAttended = lastSchoolAttended;
        this.lastSchoolYear = lastSchoolYear;
        this.programAppliedFor = programAppliedFor;
        this.enrollmentStatus = enrollmentStatus;
        this.dateApplied = dateApplied;
        this.reviewedBy = reviewedBy;
        this.reviewedOn = reviewedOn;
    }

    public Enrollee(int userId, String username, String email, String access,
                    LocalDateTime createdAt, boolean isActive,
                    String firstName, String middleName ,String lastName, String enrollmentStatus) {

        super(userId, username, email, null, access, createdAt, isActive);
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.enrollmentStatus = enrollmentStatus;
    }
    public Enrollee(int userId, String username, String email, String password,
                LocalDateTime createdAt, boolean isActive, boolean hasFilledUpForm) {
        super(userId, username, email, password, "Enrollees", createdAt, isActive);
        this.hasFilledUpForm = hasFilledUpForm;
    }

    public String getEnrolleeId() { return enrolleeId; }
    public void setEnrolleeId(String enrolleeId) { this.enrolleeId = enrolleeId; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    
    public String getMiddleName() { return middleName; }
    public void setMiddleName(String middleName) { this.middleName = middleName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getSuffix() { return suffix; }
    public void setSuffix(String suffix) { this.suffix = suffix; }

    public LocalDate getBirthDate() { return birthDate; }
    public void setBirthDate(LocalDate birthDate) { this.birthDate = birthDate; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getProvince() { return province; }
    public void setProvince(String province) { this.province = province; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getContactNumber() { return contactNumber; }
    public void setContactNumber(String contactNumber) { this.contactNumber = contactNumber; }

    public String getEmailAddress() { return emailAddress; }
    public void setEmailAddress(String emailAddress) { this.emailAddress = emailAddress; }

    public String getGuardianName() { return guardianName; }
    public void setGuardianName(String guardianName) { this.guardianName = guardianName; }

    public String getGuardianContact() { return guardianContact; }
    public void setGuardianContact(String guardianContact) { this.guardianContact = guardianContact; }
    
    public String getYearLevel() { return yearLevel; }
    public void setYearLevel(String yearLevel) { this.yearLevel = yearLevel; }
    
    public String getStudentType() { return studentType; }
    public void setStudentType(String studentType) { this.studentType = studentType; }

    public String getLastSchoolAttended() { return lastSchoolAttended; }
    public void setLastSchoolAttended(String lastSchoolAttended) { this.lastSchoolAttended = lastSchoolAttended; }

    public String getLastSchoolYear() { return lastSchoolYear; }
    public void setLastSchoolYear(String lastSchoolYear) { this.lastSchoolYear = lastSchoolYear; }

    public String getProgramAppliedFor() { return programAppliedFor; }
    public void setProgramAppliedFor(String programAppliedFor) { this.programAppliedFor = programAppliedFor; }
    
    public String getPhotoLink() { return photoLink; }
    public void setPhotoLink(String photoLink) { this.photoLink = photoLink; }

    public String getBirthCertLink() { return birthCertLink; }
    public void setBirthCertLink(String birthCertLink) { this.birthCertLink = birthCertLink; }

    public String getReportCardLink() { return reportCardLink; }
    public void setReportCardLink(String reportCardLink) { this.reportCardLink = reportCardLink; }

    public String getForm137Link() { return form137Link; }
    public void setForm137Link(String form137Link) { this.form137Link = form137Link; }

    public String getEnrollmentStatus() { return enrollmentStatus; }
    public void setEnrollmentStatus(String enrollmentStatus) { this.enrollmentStatus = enrollmentStatus; }

    public LocalDateTime getDateApplied() { return dateApplied; }
    public void setDateApplied(LocalDateTime dateApplied) { this.dateApplied = dateApplied; }

    public Integer getReviewedBy() { return reviewedBy; }
    public void setReviewedBy(Integer reviewedBy) { this.reviewedBy = reviewedBy; }

    public LocalDateTime getReviewedOn() { return reviewedOn; }
    public void setReviewedOn(LocalDateTime reviewedOn) { this.reviewedOn = reviewedOn; }
    
    public boolean hasFilledUpForm() { return hasFilledUpForm;}
    public void setHasFilledUpForm(boolean hasFilledUpForm) { this.hasFilledUpForm = hasFilledUpForm; }
    
    @Override
    public void showDashboard() {
        //will get override on controller class
    }
    @Override
    public String generateID(){
        String prefix = "SA";  
        int year = LocalDate.now().getYear() % 100;  
        int randomNum = new Random().nextInt(9000) + 1000;  

        return String.format("%s%02d-%04d", prefix, year, randomNum);
    }

    @Override
    public String toString() {
        return "Enrollee{" +
                "user=" + super.getUsername() +
                ", name='" + firstName + " " + lastName + '\'' +
                ", status='" + enrollmentStatus + '\'' +
                '}';
    }
}