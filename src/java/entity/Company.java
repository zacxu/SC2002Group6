package entity;

import java.util.ArrayList;
import java.util.List;

public class Company {
    private final String companyName;
    private final List<CompanyRepresentative> representatives = new ArrayList<>();
    private final List<Internship> internships = new ArrayList<>();

    public Company(String companyName) {
        this.companyName = companyName;
    }

    public String getCompanyName() {
        return companyName;
    }

    public List<CompanyRepresentative> getRepresentatives() {
        return new ArrayList<>(representatives);
    }

    public List<Internship> getInternships() {
        return new ArrayList<>(internships);
    }

    public void addRepresentative(CompanyRepresentative representative) {
        if (!representatives.contains(representative)) {
            representatives.add(representative);
        }
    }

    public void addInternship(Internship internship) {
        if (!internships.contains(internship)) {
            internships.add(internship);
        }
    }
}


