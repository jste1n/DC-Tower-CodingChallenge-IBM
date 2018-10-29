package elevatorSystem;

/**
 * controller class
 * extendable for a gui
 *
 * @author Steinwender Jan-Philipp
 * @version 0.1 at 28.10.2018
 */
public class Controller {
    private Model model;

    /**
     * constructor
     */
    public Controller() {
        this(false);
    }

    /**
     * constructor
     *
     * @param testcase boolean true if run with external testing class
     */
    public Controller(boolean testcase) {
        model = new Model(testcase);
    }

    public static void main(String[] args) {
        new Controller();
    }

    public Model getModel() {
        return model;
    }
}
