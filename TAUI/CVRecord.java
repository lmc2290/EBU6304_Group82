package TAUI;

/**
 * 简历记录实体类
 * 完美解决文件名重复和UI显示的问题
 */
public class CVRecord {
    private String originalName; // 原始文件名 (用于 UI 显示，如 resume.pdf)
    private String storedName;   // 物理文件名 (用于硬盘存取，如 resume_168439201.pdf)

    public CVRecord(String originalName, String storedName) {
        this.originalName = originalName;
        this.storedName = storedName;
    }

    public String getOriginalName() { return originalName; }
    public String getStoredName() { return storedName; }

    /**
     * 这一步非常关键！重写 toString()
     * 这样无论把它丢进 JList 还是 JComboBox，界面上都会自动显示 originalName，不用大改 UI 逻辑！
     */
    @Override
    public String toString() {
        return originalName;
    }
}