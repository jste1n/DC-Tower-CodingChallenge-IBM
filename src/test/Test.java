package test;

import elevatorSystem.Controller;
import elevatorSystem.Model;

import java.util.Scanner;

/**
 * Class only for testing
 * with user input
 *
 * @author Steinwender Jan-Philipp
 */
public class Test {
    /**
     * just testing
     * adds some tasks in the beginning
     * waits for extra input
     */
    public static void main(String[] args) {
        Controller c1 = new Controller(true);
        Model m = c1.getModel();

        //add some generic data
        for (int i = 0; i < 5; i++) {
            m.addRequest("[current floor: 0, destination floor: " + (35 + i) + ", direction: up]");
        }

        //some special cases
        //wrong input
        m.addRequest("[current floor: -2, destination floor: 35, direction: up]");
        m.addRequest("[current floor: 0, destination floor: 56, direction: up]");
        m.addRequest("[current floor: 0, destination floor: 55, direction: down]");
        //special case, exact same task
        m.addRequest("[current floor: 0, destination floor: 35, direction: up]");
        //just normal extra task
        m.addRequest("[current floor: 0, destination floor: 45, direction: up]");
        //special case, different current floor
        m.addRequest("[current floor: 5, destination floor: 30, direction: up]");
        m.addRequest("[current floor: 0, destination floor: 46, direction: up]");
        //special case, another different current floor and direction
        m.addRequest("[current floor: 50, destination floor: 10, direction: down]");
        //special case, multiple tasks from current floor from the list
        m.addRequest("[current floor: 8, destination floor: 15, direction: up]");
        m.addRequest("[current floor: 8, destination floor: 30, direction: up]");

        m.showTasks();
        m.showQueue();

        /*
         additional cases
         [current floor: 8, destination floor: 30, direction: up]
         [current floor: 0, destination floor: 28, direction: up]
         [current floor: 0, destination floor: 20, direction: up]

         [current floor: 55, destination floor: 0, direction: down]
          */

        //console input for testing
        try (Scanner sc = new Scanner(System.in)) {
            String in;
            while (true) {
                in = sc.nextLine();

                switch (in) {
                    case "c":
                        m.showTasks();
                        break;
                    case "q":
                        m.showQueue();
                        break;
                    case "exit":
                        System.exit(0);
                    default:
                        m.addRequest(in);
                        break;
                }
            }
        } catch (Exception e) {
            System.out.print("error!\nexiting");
//            e.printStackTrace();
        }
    }
}

