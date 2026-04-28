package TAUI;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class UserProfile implements Serializable {
    private String name;
    private String gender;
    private String grade;
    private String college;

    private List<String> selectedSkills = new ArrayList<>();
    private String otherSkills = "";
    private String experience = "";
    private String coverLetterTemplate = "";

    public String getCoverLetterTemplate() { return coverLetterTemplate; }
    public void setCoverLetterTemplate(String template) { this.coverLetterTemplate = template; }

    public UserProfile() {}

    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
    public String getGrade() { return grade; }
    public void setGrade(String grade) { this.grade = grade; }
    public String getCollege() { return college; }
    public void setCollege(String college) { this.college = college; }
    public List<String> getSelectedSkills() { return selectedSkills; }
    public void setSelectedSkills(List<String> selectedSkills) { this.selectedSkills = selectedSkills; }
    public String getOtherSkills() { return otherSkills; }
    public void setOtherSkills(String otherSkills) { this.otherSkills = otherSkills; }
    public String getExperience() { return experience; }
    public void setExperience(String experience) { this.experience = experience; }

    @Override
    public String toString() {
        return name + " (" + grade + " in " + college + ")";
    }
}