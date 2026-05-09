package AdminPage;

public class Admin_MessageControl {
    private final Admin_MessageUI boundary;

    public Admin_MessageControl() {
        this.boundary = new Admin_MessageUI();
    }

    public Admin_MessageUI getUi() {
        return boundary;
    }
}