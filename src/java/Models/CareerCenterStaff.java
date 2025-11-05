package model;

public class CareerCenterStaff extends User {
    private String department;
    
    public CareerCenterStaff(String userId, String name, String password, String department) {
        super(userId, name, password);
        this.department = department;
    }
    
    public String getDepartment() {
        return department;
    }
    
    public void setDepartment(String department) {
        this.department = department;
    }
    
    @Override
    public String getUserRole() {
        return "CareerCenterStaff";
    }
}

