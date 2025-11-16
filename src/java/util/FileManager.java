package util;

import entity.*;
import entity.enums.*;


import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.*;


/**
 * class handles the file manager for the internship placement management system.
 * 
 * 
 * 
 */



 public class FileManager {
    private static final String RESOURCES_PATH = "src/resources/";
    private static final String DATA_PATH = "data/";
    private static final String STUDENTS_FILE = RESOURCES_PATH + "sample_student_list.csv";
    private static final String STAFF_FILE = RESOURCES_PATH + "sample_staff_list.csv";
    private static final String USERS_FILE = DATA_PATH + "users.csv";
    private static final String INTERNSHIPS_FILE = DATA_PATH + "internships.csv";
    private static final String APPLICATIONS_FILE = DATA_PATH + "applications.csv";
    private static final String WITHDRAWAL_REQUESTS_FILE = DATA_PATH + "withdrawal_requests.csv";
    
   



    public static List<Student> loadStudents() throws IOException {
        List<Student> students = new ArrayList<>();
        Path path = Paths.get(STUDENTS_FILE);
        
     
        try (BufferedReader reader = Files.newBufferedReader(path)) {
            String line = reader.readLine(); 
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;

                String[] parts = line.split(",");

                if (parts.length >= 4) {
                    String userId = parts[0].trim();
                    String name = parts[1].trim();
                    String major = parts[2].trim();
                    int yearOfStudy = Integer.parseInt(parts[3].trim());
        
                    students.add(new Student(userId, name, "password", yearOfStudy, major));

                }
            }
        } 
        
        catch (IOException e) {
            throw new IOException("Error reading students.csv: " + e.getMessage(), e);
        }
        
        return students;
    }
    
    




    public static List<CareerCenterStaff> loadStaff() throws IOException {
        List<CareerCenterStaff> staff = new ArrayList<>();
        Path path = Paths.get(STAFF_FILE);
        
      
        
        try (BufferedReader reader = Files.newBufferedReader(path)) {
            String line = reader.readLine(); 

            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;

                String[] parts = line.split(",");

                if (parts.length >= 4) {
                    String userId = parts[0].trim();
                    String name = parts[1].trim();
                   
                    String department = parts[3].trim();
                    staff.add(new CareerCenterStaff(userId, name, "password", department));
                }
            }
        } 
        
        catch (IOException e) {
            throw new IOException("Error reading staff.csv: " + e.getMessage(), e);
        }
        
        return staff;
    }
    




    public static void saveUsers(Map<String, User> users) throws IOException {
        ensureDirectoryExists(DATA_PATH);
        Path path = Paths.get(USERS_FILE);
        
        try (BufferedWriter writer = Files.newBufferedWriter(path)) {
            writer.write("UserType,UserID,Name,Password,AdditionalInfo\n");

            for (User user : users.values()) {
                String type = user.getUserRole();
                String additionalInfo = "";
                
                if (user instanceof Student) {
                    Student s = (Student) user;
                    additionalInfo = s.getYearOfStudy() + "," + s.getMajor();
                } 
                
                else if (user instanceof CompanyRepresentative) {
                    CompanyRepresentative cr = (CompanyRepresentative) user;
                    additionalInfo = cr.getCompanyName() + "," + cr.getDepartment() + "," + 
                                   cr.getPosition() + "," + cr.isApproved();
                } 
                
                else if (user instanceof CareerCenterStaff) {
                    CareerCenterStaff ccs = (CareerCenterStaff) user;
                    additionalInfo = ccs.getDepartment();
                }
                
                writer.write(String.format("%s,%s,%s,%s,%s\n", 
                    type, user.getUserId(), user.getName(), user.getPassword(), additionalInfo));

            }
        }
    }
    





    public static Map<String, User> loadUsers() throws IOException {
        Map<String, User> users = new HashMap<>();
        Path path = Paths.get(USERS_FILE);
        
        if (!Files.exists(path)) {
           
            List<Student> students = loadStudents();
            List<CareerCenterStaff> staff = loadStaff();
            
            for (Student s : students) {
                users.put(s.getUserId(), s);
            }

            for (CareerCenterStaff s : staff) {
                users.put(s.getUserId(), s);
            }
            
            saveUsers(users);
            return users;
        }
        
        try (BufferedReader reader = Files.newBufferedReader(path)) {
            String line = reader.readLine(); // Skip header
            while ((line = reader.readLine()) != null) {

                if (line.trim().isEmpty()) continue;
                String[] parts = line.split(",");


                if (parts.length >= 4) {
                    String type = parts[0].trim();
                    String userId = parts[1].trim();
                    String name = parts[2].trim();
                    String password = parts[3].trim();
                    
                    if ("Student".equals(type) && parts.length >= 6) {
                        int yearOfStudy = Integer.parseInt(parts[4].trim());
                        String major = parts[5].trim();
                        Student student = new Student(userId, name, password, yearOfStudy, major);
                        users.put(userId, student);
                    } 
                    
                    else if ("CompanyRepresentative".equals(type) && parts.length >= 7) {
                        String companyName = parts[4].trim();
                        String department = parts[5].trim();
                        String position = parts[6].trim();
                        boolean approved = parts.length >= 8 && Boolean.parseBoolean(parts[7].trim());
                        CompanyRepresentative cr = new CompanyRepresentative(userId, name, password, 
                                                                             companyName, department, position);
                        cr.setApproved(approved);
                        users.put(userId, cr);
                    } 
                    
                    else if ("CareerCenterStaff".equals(type) && parts.length >= 5) {
                        String department = parts[4].trim();
                        users.put(userId, new CareerCenterStaff(userId, name, password, department));
                    }
                }
            }
        }
        
        return users;

    }
    




    
    public static void saveInternships(Map<String, Internship> internships) throws IOException {
        ensureDirectoryExists(DATA_PATH);
        Path path = Paths.get(INTERNSHIPS_FILE);
        
        try (BufferedWriter writer = Files.newBufferedWriter(path)) {

            writer.write("InternshipID,Title,Description,Level,PreferredMajor,OpeningDate,ClosingDate,Status," +
                        "CompanyName,CompanyRepID,TotalSlots,FilledSlots,Visible\n");

            for (Internship internship : internships.values()) {
                writer.write(String.format("%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%d,%d,%s\n",

                    internship.getInternshipId(),
                    escapeCSV(internship.getTitle()),
                    escapeCSV(internship.getDescription()),
                    internship.getLevel(),
                    internship.getPreferredMajor(),
                    DateUtils.formatDate(internship.getOpeningDate()),
                    DateUtils.formatDate(internship.getClosingDate()),
                    internship.getStatus(),
                    escapeCSV(internship.getCompanyName()),
                    internship.getCompanyRepId(),
                    internship.getTotalSlots(),
                    internship.getFilledSlots(),
                    internship.isVisible()

                ));
            }
        }
    }
    
   



    public static Map<String, Internship> loadInternships() throws IOException {
        Map<String, Internship> internships = new HashMap<>();
        Path path = Paths.get(INTERNSHIPS_FILE);
        
        if (!Files.exists(path)) {
            return internships;
        }
        
        try (BufferedReader reader = Files.newBufferedReader(path)) {
            String line = reader.readLine(); 
            
            while ((line = reader.readLine()) != null) {

                if (line.trim().isEmpty()) continue;
                String[] parts = parseCSVLine(line);

                if (parts.length >= 13) {

                    String internshipId = parts[0];
                    String title = parts[1];
                    String description = parts[2];
                    InternshipLevel level = InternshipLevel.valueOf(parts[3]);
                    String preferredMajor = parts[4];
                    LocalDate openingDate = DateUtils.parseDate(parts[5]);
                    LocalDate closingDate = DateUtils.parseDate(parts[6]);
                    InternshipStatus status =  InternshipStatus.valueOf(parts[7]);
                    String companyName = parts[8];
                    String companyRepId = parts[9];
                    int totalSlots = Integer.parseInt(parts[10]);
                    int filledSlots = Integer.parseInt(parts[11]);
                    boolean visible = Boolean.parseBoolean(parts[12]);
                    
                    Internship internship = new Internship( internshipId, title, description, level, preferredMajor, openingDate, closingDate, companyName, companyRepId, totalSlots );
                    
                    internship.setStatus(status);
                    while (internship.getFilledSlots() < filledSlots) {
                        internship.incrementFilledSlots();
                    }
                    internship.setVisible(visible);
                    
                    internships.put(internshipId, internship);

                }
            }
        }
        
        return internships;
    }
    
    



    public static void saveApplications(Map<String, Application> applications) throws IOException {
        ensureDirectoryExists(DATA_PATH);
        Path path = Paths.get(APPLICATIONS_FILE);
        
        try (BufferedWriter writer = Files.newBufferedWriter(path)) {
            writer.write("ApplicationID,StudentID,InternshipID,Status,ApplicationDate\n");

            for (Application app : applications.values()) {
                writer.write(String.format("%s,%s,%s,%s,%s\n",
                    app.getApplicationId(),
                    app.getStudentId(),
                    app.getInternshipId(),
                    app.getStatus(),
                    DateUtils.formatDate(app.getApplicationDate())

                ));
            }
        }
    }
    





    public static Map<String, Application> loadApplications() throws IOException {
        Map<String, Application> applications = new HashMap<>();
        Path path = Paths.get(APPLICATIONS_FILE);
        
        if (!Files.exists(path)) {
            return applications;
        }
        

        try (BufferedReader reader = Files.newBufferedReader(path)) {
            String line = reader.readLine(); 


            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] parts = parseCSVLine(line);
                if (parts.length >= 5) {
                    String applicationId = parts[0].trim();
                    String studentId = parts[1].trim();
                    String internshipId = parts[2].trim();
                    ApplicationStatus status = ApplicationStatus.valueOf(parts[3].trim());
                    LocalDate applicationDate = DateUtils.parseDate(parts[4].trim());
                    
                    Application app = new Application(applicationId, studentId, internshipId, applicationDate);
                    app.setStatus(status);
                    applications.put(applicationId, app);


                }
            }
        }
        
        return applications;
    }
    





    public static void saveWithdrawalRequests(Map<String, WithdrawalRequest> requests) throws IOException {
        ensureDirectoryExists(DATA_PATH);
        Path path = Paths.get(WITHDRAWAL_REQUESTS_FILE);
        
        try (BufferedWriter writer = Files.newBufferedWriter(path)) {
            writer.write("RequestID,ApplicationID,StudentID,InternshipID,Status,IsAfterPlacement,RequestDate,Reason\n");
            
            for (WithdrawalRequest request : requests.values()) {
                writer.write(String.format("%s,%s,%s,%s,%s,%s,%s,%s\n",
                    request.getRequestId(),
                    request.getApplicationId(),
                    request.getStudentId(),
                    request.getInternshipId(),
                    request.getStatus(),
                    request.isAfterPlacement(),
                    DateUtils.formatDate(request.getRequestDate()),
                    escapeCSV(request.getReason())
                ));
            }
        }
    }
    






    public static Map<String, WithdrawalRequest> loadWithdrawalRequests() throws IOException {
        Map<String, WithdrawalRequest> requests = new HashMap<>();
        Path path = Paths.get(WITHDRAWAL_REQUESTS_FILE);
        
        if (!Files.exists(path)) {
            return requests;
        }
        
        try (BufferedReader reader = Files.newBufferedReader(path)) {
            String line = reader.readLine(); // Skip header
            while ((line = reader.readLine()) != null) {

                if (line.trim().isEmpty()) continue;
                String[] parts = parseCSVLine(line);

                if (parts.length >= 8) {
                    String requestId = parts[0].trim();
                    String applicationId = parts[1].trim();
                    String studentId = parts[2].trim();
                    String internshipId = parts[3].trim();
                    
                    WithdrawalStatus status = WithdrawalStatus.valueOf(parts[4].trim());
                    boolean isAfterPlacement = Boolean.parseBoolean(parts[5].trim());
                    LocalDate requestDate = DateUtils.parseDate(parts[6].trim());
                    String reason = parts[7].trim();
                    
                    WithdrawalRequest request = new WithdrawalRequest( requestId, applicationId, studentId, internshipId, isAfterPlacement, requestDate, reason );
                    
                    request.setStatus(status);
                    requests.put(requestId, request);
                }
            }
        }
        
        return requests;
    }
    




    private static void ensureDirectoryExists(String dirPath) throws IOException {
        Path path = Paths.get(dirPath);

        if (!Files.exists(path)) {
            Files.createDirectories(path);
        }
    }
    

    private static String escapeCSV(String field) {
        if (field == null) return "";
        
        if (field.contains(",") || field.contains("\"") || field.contains("\n")) {
            return "\"" + field.replace("\"", "\"\"") + "\"";
        }
        return field;
    }
    


    private static String[] parseCSVLine(String line) {
        List<String> result = new ArrayList<>();
        boolean inQuotes = false;
        StringBuilder current = new StringBuilder();
        
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);

            if (c == '"') {
                inQuotes = !inQuotes;
            } 
            
            else if (c == ',' && !inQuotes) {
                result.add(current.toString());
                current = new StringBuilder();
            } 
            
            else {
                current.append(c);
            }
        }
        result.add(current.toString());
        
        return result.toArray(new String[0]);
    }
    
    
}

