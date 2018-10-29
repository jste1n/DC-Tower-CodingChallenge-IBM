package elevatorSystem;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Scanner;

/**
 * this class is the logic behind the elevator system.
 * it receives requests and assigns them to any elevator
 * or if no one is available it ll add them to a list/queue.
 * <p>
 * currently not implemented/no attention payed yet:
 * - max persons = max calls per lift
 * - if there is a stopover, no new calls from it ll be accepted
 * eg: elevator moving from 0 -> 20, 25, 35, 36.
 * elevator is currently on floor 18.
 * system gets a new request from 25 -> 30.
 * this request ll not be added to that elevator
 * - not optimized
 * <p>
 * shortcuts:
 * - ele ... elevator
 * - cur ... current[ly]
 * - dif ... different
 * - req ... request[s]
 * - dir ... direction
 * - dest ... destination
 *
 * @author Steinwender Jan-Philipp
 * @version 0.1 at 28.10.2018
 */
public class Model {
    private int curTemp, destTemp;
    private String dirTemp;
    private ArrayList<Elevators> allElevators; //all ele saved here
    private LinkedList<String> requestList; //new request saved here
    //for testing, ll create one extra on a dif floor
    private int lifteAnzahl = 6;

    /**
     * constructor
     */
    public Model() {
        this(false);
    }

    /**
     * constructor
     * creates the ele + one extra on a dif floor
     * <p>
     * if testing -> run with (true)
     *
     * @param testcase boolean true when testing
     */
    public Model(boolean testcase) {
        allElevators = new ArrayList<>();
        requestList = new LinkedList<>();
        //create ele that are cur on floor 0
        for (int i = 0; i < lifteAnzahl; i++) {
            allElevators.add(new Elevators(i, this));
            Thread t = new Thread(allElevators.get(i));
            t.start();
        }
        // extra ele on dif floor
        allElevators.add(new Elevators(lifteAnzahl, this, 6));
        Thread t = new Thread(allElevators.get(lifteAnzahl));
        t.start();

        // if not testing, for user input
        if (!testcase) {
            runningUser();
        }
    }

    /**
     * new req ll call it
     * check ele and the req and ll assign it if possible
     * else ll be added to queue (requestList)
     *
     * @param s String must be "[current floor: (number), destination floor: (number), direction: (up|down)]"
     */
    public void addRequest(String s) {
        // first check the input
        if (checkInput(s)) {
            boolean add = false; //add to a ele else to the list
            boolean skip = true;
            int liftNichtImStock = 0;
            ArrayList<Elevators> difLevel = new ArrayList<>(); //all ele that are on a dif floor
            ArrayList<Elevators> notMoving = new ArrayList<>();
            Elevators bestChoice = allElevators.get(0); // first to compare with

            //first run per ele
            for (Elevators e2 : allElevators) {
                if (curTemp == e2.getCurrent()) { // the req floor on same floor as an ele
                    if (e2.getDestinations().contains(destTemp)) { // and the dest floor already on list of ele
                        bestChoice = e2;
                        break;
                    }
                    if (bestChoice.getDestinations().size() > e2.getDestinations().size()) { // else choose ele with the fewest req
                        bestChoice = e2;
                    }
                } else { // all ele are on a dif floor
                    liftNichtImStock++;
                    difLevel.add(e2); //get all ele that are on a dif floor
                }
            }

            //every run after first
            if (liftNichtImStock == (lifteAnzahl + 1)) { //no ele on cur floor
                // den am nachsten lift zuteilen

                // if there is already a req from the same floor AND it's going in the same dir -> add to same ele
                for (Elevators e3 : allElevators) {
                    if (curTemp == e3.getPriority() && dirTemp.equals(e3.getDirection())) {
                        bestChoice = e3;
                        add = true;
                        skip = false; //skip the next if, because there is already a ele that matches
                    }
                }

                if (skip) {
                    // get ele that are on dif floor and are available = not moving
                    for (Elevators e1 : difLevel) {
                        if (!e1.isMoving()) {
                            notMoving.add(e1);
                        }
                    }

                    if (!notMoving.isEmpty()) { // if there are ele available = not moving, that have no task
                        int best = 100; // for comparison
                        // get the nearest ele
                        for (Elevators e : notMoving) {
                            int diff = Math.abs(curTemp - e.getCurrent());
                            if (diff < best) {
                                best = diff;
                                bestChoice = e;
                            }
                        }

                        if (bestChoice.getDestinations().isEmpty()) { // if the best ele has no task yet, then add it
                            bestChoice.setPriority(curTemp); // NEED to set priority because the req comes from a dif floor than the ele is cur
                            add = true;
                        }
                    } else { // if there is no ele available; everyone has a task = is moving
                        System.out.println("keine lifte frei und moven");
                    }
                }
            } else {
                add = true;
            }

            if (add) { // req ll be added to ele
                bestChoice.setDestinations(destTemp);
                bestChoice.setDirection(dirTemp);
//                bestChoice.setPeople(1);
                System.out.println("added: " + s + " to lift:" + bestChoice.getId());
            } else { // req ll be added to list
                requestList.add(s);
                System.out.println("auf liste " + s);
            }
            System.out.println("-----------------");
        } else {
            System.err.println("wrong request! check arguments:" + s);
        }
    }

    /**
     * is called by addRequest(String)
     * check if the req/input is correct
     *
     * @param s String request
     * @return boolean true if req is right formatted
     */
    public boolean checkInput(String s) {
        String[] cmd = s.split(",");
        try {
            curTemp = Integer.parseInt(cmd[0].substring(16)); //get cur floor
            destTemp = Integer.parseInt(cmd[1].substring(20)); //get dest floor
            dirTemp = cmd[2].substring(12, cmd[2].length() - 1).toLowerCase(); //get direction, in lower case
            if (!dirTemp.equals("up") && !dirTemp.equals("down")) { //only 'up' and 'down'
                return false;
            }
            if (!(curTemp >=0 && curTemp < 56 && destTemp >= 0 && destTemp < 56)) { //must be 0 <= floors < 56
                return false;
            }
            //check if direction matches the floors
            int temp = curTemp - destTemp;
            if (temp > 0) {
                if (dirTemp.equals("up")) {
                    return false;
                }
            } else {
                if (dirTemp.equals("down")) {
                    return false;
                }
            }
        } catch (Exception e) {
//            e.printStackTrace();
            System.err.println("some error with input. please check your input.");
            return false;
        }
        return true;
    }

    /**
     * after ele completed its task,
     * it reports and ll receive new job(s) from the list
     *
     * @param id int ele id
     */
    public void newJob(int id) {
        try {
            if (!requestList.isEmpty()) { //are there entries on the list
                int oldState = requestList.size();
                //try to assign as many req as possible
                for (int i = 0; i < oldState; i++) {
                    addRequest(requestList.poll());
                    /*
                    it can poll(remove) from the list because at 'addRequest'
                    it ll be added to list again if it doesnt macht any ele
                     */
                    Thread.sleep(1);
                }
                System.out.println(id + " lift hat auftrag aus liste ubernommen");
            }
        } catch (NullPointerException e) { //if 'requestList' is empty, 'requestList.size()' ll cause it
            System.out.println("aktuell keine auftrage");
        } catch (Exception e) {
//            e.printStackTrace();
            System.err.println("some error with assigning a new job");
        } finally {
            showTasks();
            showQueue();
        }
    }

    /**
     * only for user input
     * inputs:
     * - c .. show current tasks
     * - q .. show the queue
     * - (else) .. calls addRequest
     */
    public void runningUser() {
        try (Scanner sc = new Scanner(System.in);) {
            String in;
            while (true) {
                //for console input
                in = sc.nextLine();//next();
                switch (in) {
                    case "c":
                        showTasks();
                        break;
                    case "q":
                        showQueue();
                        break;
                    case "exit":
                        System.exit(0);
                    default:
                        addRequest(in);
                        break;
                }
            }
        } catch (Exception e) {
            System.out.print("err: ");
//            e.printStackTrace();
        }
    }

    /**
     * prints the queue with pending req
     */
    public void showQueue() {
        System.out.println("warteliste: " + requestList.size());
        for (String s : requestList) {
            System.out.println(s);
        }
    }

    /**
     * prints the cur tasks of every ele
     * format: (cur floor); (the priority/where req is coming from) -> (all dest) (the dir)
     */
    public void showTasks() {
        for (Elevators e : allElevators) {
            System.out.print("akt:" + e.getCurrent() + ";  " + e.getPriority() + " -> ");
            System.out.println(e.getDestinations() + " " + e.getDirection());
        }
    }
}
