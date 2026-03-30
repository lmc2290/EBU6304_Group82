package TAUI;

/**
 * Entity Class
 * Encapsulates core data related to a job posting.
 */
public class Job {
    private String id;
    private String title;
    private String module;
    private String hours;
    private String salary;
    private String competitionRatio;
    private String responsibilities;
    private boolean isExpired;

    // [新增] 两个新字段：岗位类型和技能要求
    private String jobType;
    private String requiredSkill;

    // [修改] 构造函数，在末尾增加了 jobType 和 requiredSkill 接收参数
    public Job(String id, String title, String module, String hours, String salary,
               String competitionRatio, String responsibilities, boolean isExpired,
               String jobType, String requiredSkill) {
        this.id = id;
        this.title = title;
        this.module = module;
        this.hours = hours;
        this.salary = salary;
        this.competitionRatio = competitionRatio;
        this.responsibilities = responsibilities;
        this.isExpired = isExpired;

        // [新增] 属性赋值
        this.jobType = jobType;
        this.requiredSkill = requiredSkill;
    }

    // --- Getters ---
    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getModule() { return module; }
    public String getHours() { return hours; }
    public String getSalary() { return salary; }
    public String getCompetitionRatio() { return competitionRatio; }
    public String getResponsibilities() { return responsibilities; }
    public boolean isExpired() { return isExpired; }

    // [新增] 两个新的 Getters 供 Controller 调用
    public String getJobType() { return jobType; }
    public String getRequiredSkill() { return requiredSkill; }

    /**
     * Overrides the toString method to determine the text format
     * displayed in the left JList component.
     */
    @Override
    public String toString() {
        return "[" + module + "] " + title + (isExpired ? " (Closed)" : "");
    }
}