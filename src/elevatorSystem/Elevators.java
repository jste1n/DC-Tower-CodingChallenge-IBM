package elevatorSystem;

import java.util.Collections;
import java.util.LinkedList;
import java.util.Queue;

/**
 * one actual elevator, as one thread
 * takes new tasks and moves the elevator
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
public class Elevators implements Runnable {
    //    private int people;
    private LinkedList<Integer> destinations;
    private boolean moving;
    private int current, id, priority; //default -1000; floor from where the req came
    private Model model;
    private String direction;

    /**
     * constructor
     *
     * @param i     int id
     * @param model Model elevator system
     */
    public Elevators(int i, Model model) {
        this(i, model, 0);
    }

    /**
     * constructor
     *
     * @param id      int id
     * @param model   Model elevator system
     * @param current int cur floor of the ele
     */
    public Elevators(int id, Model model, int current) {
        this.current = current;
        this.id = id;
        this.model = model;
//        people = 0;
        destinations = new LinkedList<>();
        moving = false;
        priority = -1000; //-1000 means there is no priority
    }

    /**
     * the heart of a ele
     * get new task
     * moves the ele
     * wait for new tasks
     */
    @Override
    public void run() {
        long pastTime = 0; // check to wait 2 sec after new req was assigned
        boolean startStopwatch = false;

        while (true) {
            //wait till it gets a job(s)
            if (!destinations.isEmpty() && !startStopwatch) {  //got the first job
                System.out.println(id + " got a job, wait 2 sec");
                pastTime = System.currentTimeMillis(); //starting the stopwatch for demo
                startStopwatch = true;
            }
            if (startStopwatch && System.currentTimeMillis() >= (pastTime + 2 * 1000) && !moving) { //after 2 seconds and if the ele is not moving
                System.out.println(id + " lift fahrt. akt stock:" + current);
                moving = true; //ele starts the task
            }
            if (moving) { //ele is now moving
                System.out.println(id + " lift moving");
                move(); //where and how to move, also waits for demo
            }
            if (destinations.size() != 0) { //as long as the ele has tasks
                int temp = destinations.getFirst(); //not removing the element
                if (priority != -1000) { //if there is a priority, if the req is from another floor than the ele
                    temp = priority;
                }

                if (current == temp) { //if ele is on the right (pickup or a/the dest) floor
                    System.out.println(id + " ein & aus steigen");
                    int duplicates = destinations.pollFirst(); //now removing
                    //check if the dest floor exists multiple times
                    while (destinations.contains(duplicates)) {
                        destinations.remove(destinations.indexOf(duplicates));
                        System.out.println(id + " ein aus steigen");
                    }
                    //wait for demo
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (destinations.isEmpty()) { //if its the last dest/stop -> stop the ele and req to 'Model' for new tasks
                        moving = false; //stopping, no work
                        startStopwatch = false;
                        System.out.println(id + " alle ausgestiegen. warten auf nachsten auftrag");
                        model.newJob(id); //req for a new task
                    }
                }
            }
            // don't delete, without it wont work
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * moves the ele
     * check if up or down
     */
    public void move() {
        char direction = 'u';
        int goTo = destinations.getFirst();
        if (priority != -1000) { //if there is a priority
            System.out.println(id + " prio");
            goTo = priority;
        }

        if (current > goTo) { //check if up or down
            direction = 'd';
        }

        //lift is moving
        try {
            Thread.sleep(1000); //for demo
            if (direction == 'u') {
                current++;
            } else {
                current--;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (priority == current) { //when ele is on the pickup floor
            priority = -1000; //back to default, no priority anymore
            System.out.print("bitte einsteigen! ");
        }
        System.out.println(id + " lift ist nun im stock:" + current);
    }

//    public boolean checkSpace() {
//        if (people > 20) {
//            return false;
//        }
//        return true;
//    }

    public Queue<Integer> getDestinations() {
        return destinations;
    }

    /**
     * add a dest
     * and sort the list
     *
     * @param destinations int one new dest
     */
    public void setDestinations(int destinations) {
        this.destinations.add(destinations);
        Collections.sort(this.destinations);
        //actually useless because a available ele gets the NEXT task not the nearest
    }

    public boolean isMoving() {
        return moving;
    }

    public int getCurrent() {
        return current;
    }

    public int getId() {
        return id;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }
}

