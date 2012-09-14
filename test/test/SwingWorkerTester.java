/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingWorker;

/**
 *
 * @author SkyforceShen
 */
public class SwingWorkerTester extends SwingWorker<Integer, Void> {

    public SwingWorkerTester() {
        System.out.println("constructor");
    }

    @Override
    protected Integer doInBackground() throws Exception {
        System.out.println("doInBackground");
        for (int x = 0; x < 10; x++) {
//            Thread.currentThread().wait(100);
            System.out.println(x);
        }
        return 1;
    }

    protected void done() {
        System.out.println("done");
        try {
            System.out.println("got: " + this.get());
        } catch (InterruptedException ex) {
            Logger.getLogger(SwingWorkerTester.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ExecutionException ex) {
            Logger.getLogger(SwingWorkerTester.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void main(String[] args) {
        SwingWorkerTester test = new SwingWorkerTester();
        test.execute();
    }
}
