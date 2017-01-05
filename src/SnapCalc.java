import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.esa.snap.core.dataio.DecodeQualification;
import org.esa.snap.core.dataio.ProductIOPlugInManager;
import org.esa.snap.core.dataio.ProductReaderPlugIn;
import org.esa.snap.core.dataio.ProductWriterPlugIn;
import org.esa.snap.runtime.Config;

public class SnapCalc {

    static void threadMessage(String message) {
        String threadName = Thread.currentThread().getName();
        System.out.format("%s: %s%n", threadName, message);
    }

    private static class IndexCalc implements Runnable {
        private String path = "";

        public IndexCalc(String path) {
            this.path = path;
        }

        public void run() {
            SnapCalculator.calcIndex(path);
        }
    }

    public static void main(String[] args) {
        //Logger.getGlobal().getParent().getHandlers()[0].setLevel(Level.FINEST);
        Config.instance().logger().setLevel(Level.ALL);

        Handler consoleHandler = new ConsoleHandler();
        consoleHandler.setLevel(Level.ALL);
        Logger.getAnonymousLogger().addHandler(consoleHandler);

        ProductIOPlugInManager registry = ProductIOPlugInManager.getInstance();
        Iterator it = registry.getAllWriterPlugIns();
        if (it.hasNext()) {
            ProductWriterPlugIn plugIn = (ProductWriterPlugIn)it.next();
            System.out.print(plugIn.getDescription(Locale.ENGLISH));
        }

        /*File input = new File("S3A_OL_1_EFR____20160601T090231_20160601T090531_20160601T113139_0179_004_378_1980_MAR_O_NR_001.SEN3\\xfdumanifest.xml");

        ProductIOPlugInManager registry = ProductIOPlugInManager.getInstance();
        Iterator it = registry.getAllReaderPlugIns();
        ProductReaderPlugIn selectedPlugIn = null;
        long startTimeTotal = System.currentTimeMillis();

        while(it.hasNext()) {
            ProductReaderPlugIn endTimeTotal = (ProductReaderPlugIn)it.next();

            try {
                long e = System.currentTimeMillis();
                DecodeQualification decodeQualification = endTimeTotal.getDecodeQualification(input);
                long endTime = System.currentTimeMillis();
                System.out.print(String.format("Checking reader plugin %s (took %d ms)", new Object[]{endTimeTotal.getClass().getName(), Long.valueOf(endTime - e)}));
                if(decodeQualification == DecodeQualification.INTENDED) {
                    selectedPlugIn = endTimeTotal;
                    break;
                }

                if(decodeQualification == DecodeQualification.SUITABLE) {
                    selectedPlugIn = endTimeTotal;
                }
            } catch (Exception var13) {
                System.out.print("Error attempting to read " + input + " with plugin reader " + endTimeTotal.toString() + ": " + var13.getMessage());
            }
        }*/

        int MAX_THREADS = 16;

        Runtime runtime = Runtime.getRuntime();

        if (args.length > 0) {
            try {
                int userMaxThreads = Integer.parseInt(args[0]);
                MAX_THREADS = userMaxThreads;
            } catch (NumberFormatException e) {
                System.out.print(e.getMessage());
            }
        }

        System.out.print(runtime.maxMemory() / (1024 * 1024) + " MiB");
        //String filePath = args[0];
        List<Thread> threads = new ArrayList<Thread>();

        File[] dirs = new File(".").listFiles();
        for (int i=0; i < dirs.length; i++) {
            File dir = dirs[i];
            //if (dir.isFile() && dir.getName().matches("MER_FR__2P.*\\.N1")) {
            System.out.println(dir.getName());
            //if (dir.isFile() && dir.getName().matches("([^\\s]+(\\.(?i)(N1))$)")) {
            if (dir.isFile() && dir.getName().matches(".*\\.dim")) {
                //System.out.println(dir.getAbsolutePath());
                //BeamTest.calcIndex(dir.getAbsolutePath());
                //System.out.println(new File(dir.getName(), "xfdumanifest.xml").toString());
                //Thread t = new Thread(new IndexCalc(new File(dir.getName(), "xfdumanifest.xml").toString()));
                Thread t = new Thread(new IndexCalc(dir.getName()));
                threads.add(t);
                //t.start();
                //System.out.println(t.getState());
                //Thread.State.NEW
            }
        }
        //threadMessage("Waiting for MessageLoop thread to finish");
        long startTime = System.currentTimeMillis();
        while (true) {
            int activeThreadCount = 0;
            int doneCount = 0;
            boolean allFinished = true;
            for (Thread thread : threads) {
                long memoryLeft = runtime.maxMemory() - runtime.totalMemory();
                //System.out.print("\nFree " + memoryLeft / (1024 * 1024) + " MiB");
                if (thread != null) {
                    if (thread.getState() == Thread.State.NEW && activeThreadCount < MAX_THREADS && memoryLeft > 500 * 1024 * 1024) {
                        System.out.print("\nStarting new thread");
                        thread.start();
                        activeThreadCount++;
                        allFinished = false;
                    } else if (thread.getState() == Thread.State.RUNNABLE) {
                        allFinished = false;
                        activeThreadCount++;
                    }  else if (thread.getState() == Thread.State.TERMINATED) {
                        doneCount++;
                        thread = null;
                    }
                } else {
                    doneCount++;
                }

            }
            System.out.print("\n" + activeThreadCount + " running, " + doneCount + " of " + threads.size() + " done");
            System.out.print("\n" + runtime.totalMemory() / (1024 * 1024) + " MiB");
            if (doneCount >= threads.size()) {
                break;
            }
            try {
                Thread.sleep(3000);
            } catch(InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
            System.gc();
        }
        System.out.println("\nAll done in " + (System.currentTimeMillis() - startTime) + "ms");
        //threadMessage("All done");
    }
}
