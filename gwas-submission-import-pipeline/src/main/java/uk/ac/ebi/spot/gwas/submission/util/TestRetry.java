package uk.ac.ebi.spot.gwas.submission.util;

import java.util.concurrent.TimeUnit;

public class TestRetry {
    int retryCount = 0;

    public static void main(String[] args) {
        TestRetry testRetry = new TestRetry();
        testRetry.fireCommand("Hello There");
    }

    private String fireCommand(String command) {
        try {
            System.out.println("Command is " + command);
            throw new Exception("Firing command failed");
        }catch (Exception ex) {
            retryCount++;
            long waitTime = (long) Math.pow(2, retryCount) * 100;
            System.out.println("Deadlock occurred in savePmidReporting" + ex.getMessage());
            try {
                TimeUnit.MILLISECONDS.sleep(waitTime);
                System.out.println("Inside Exception " + ex.getMessage());
                return retryCommand(command);
            } catch (InterruptedException e) {
                System.out.println("InterruptedException in savePmidReporting" + e.getMessage());
                Thread.currentThread().interrupt();
            }
        }
        return null;
    }

    private String retryCommand(String command) {
        int maxTries = 3;
        while(retryCount < maxTries) {

            System.out.println("Retry count is " + retryCount);
            return fireCommand("Hello There again");
        }
        return null;
    }
}
