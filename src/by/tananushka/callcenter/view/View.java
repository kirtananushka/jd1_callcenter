package by.tananushka.callcenter.view;

import by.tananushka.callcenter.CallCenter;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static by.tananushka.callcenter.CallCenter.WARNINGS;

public final class View {

    public static final String RED_BACKGROUND = "\u001B[41m";

    private static final String RESET = "\u001B[0m";
    private static final String RED = "\u001B[31m";
    private static final String GREEN = "\u001B[32m";
    private static final String YELLOW = "\u001B[33m";
    private static final String BLUE = "\u001B[34m";
    private static final String PURPLE = "\u001B[35m";
    private static final String CYAN = "\u001B[36m";
    public static final String BLUE_BACKGROUND = "\u001B[44m";
    public static final String PURPLE_BACKGROUND = "\u001B[45m";

    private static final Lock lock = new ReentrantLock(true);

    private static final String CUSTOMER = "The customer #";
    private static View instance;
    private static AtomicInteger previousCounter = new AtomicInteger(0);
    private static AtomicInteger printCounter = new AtomicInteger(0);
    private static AtomicInteger sequenceViolationCounter = new AtomicInteger(0);
    private static AtomicBoolean isReportPrinted = new AtomicBoolean(false);

    private View() {

    }

    public static synchronized View getInstance() {
        if (instance == null) {
            instance = new View();
        }
        return instance;
    }

    private void counterControl(int counter) {
        if (WARNINGS && previousCounter.get() > counter - 1) {
            System.out.println(PURPLE_BACKGROUND + YELLOW + "Possible sequence violation!\n" + RESET);
            sequenceViolationCounter.incrementAndGet();
            try {
                Thread.sleep(CallCenter.THREAD_SLEEP);
            } catch (InterruptedException e) {
                e.printStackTrace();
                Thread.currentThread().interrupt();
            }
        }

        previousCounter.set(counter);
    }

    public boolean printOnAdding(int counter, int customerId, int waiting, int connected, int free) {

        lock.lock();

        counterControl(counter);

        System.out.println(counter + "/" + printCounter.incrementAndGet() + ". " + BLUE + CUSTOMER + customerId + " has been added to the queue." + RESET);

        printInfo(waiting, connected, free);

        lock.unlock();

        return true;
    }

    public boolean printOnConnection(int counter, int customerId, int operatorId, int waiting, int connected, int free) {

        lock.lock();

        counterControl(counter);

        System.out.println(counter + "/" + printCounter.incrementAndGet() + ". " + GREEN + CUSTOMER + customerId + " was connected to the operator #" + operatorId + "." + RESET);

        printInfo(waiting, connected, free);

        lock.unlock();

        return true;
    }

    public boolean printOnDisconnection(int counter, int customerId, int operatorId, int waiting, int connected, int free) {

        lock.lock();

        counterControl(counter);

        System.out.println(counter + "/" + printCounter.incrementAndGet() + ". " + RED + CUSTOMER + customerId + " has been disconnected from the operator #" + operatorId + "." + RESET);

        printInfo(waiting, connected, free);

        lock.unlock();

        return true;
    }

    private void printInfo(int waiting, int connected, int free) {

        lock.lock();

        printWaiting(waiting);

        printConnected(connected);

        printNumberOfFreeOperators(free);

        lock.unlock();

    }

    private void printWaiting(int waiting) {

        System.out.print(PURPLE + "Customers waiting: " + waiting + ". " + RESET);
    }

    private void printConnected(int connected) {

        System.out.print(CYAN + "Customers connected: " + connected + ". " + RESET);
    }

    private void printNumberOfFreeOperators(int free) {

        System.out.println(PURPLE + "Free operators: " + free + ".\n" + RESET);
    }

    public void printOnStart() {

        System.out.println(YELLOW + CallCenter.NUMBER_OF_OPERATORS + " call center operators start working.\n" + RESET);
    }

    public void printOnFinish(int connectionProblemCounter, int disconnectionProblemCounter) {

        if (!isReportPrinted.getAndSet(true)) {

            lock.lock();

            System.out.println(YELLOW + "Call center finished work." + RESET);

            System.out.println("Possible sequence violations:\t\t" + sequenceViolationCounter.get() + ".");

            System.out.println("Possible connection problems:\t\t" + connectionProblemCounter + ".");

            System.out.println("Possible disconnection problems:\t" + disconnectionProblemCounter + ".");

            lock.unlock();
        }
    }

    // WARNINGS

    public void connectionProblem(int customerId, int operatorId) {

        if (WARNINGS) {

            lock.lock();

            System.out.println(BLUE_BACKGROUND + YELLOW + ">>> The customer #" + customerId + " is requesting a connection to busy operator #" + operatorId + ".\n" + RESET);

            lock.unlock();
        }
    }

    public void disconnectionProblem(int customerId, int operatorId) {

        if (WARNINGS) {

            lock.lock();

            System.out.println(RED_BACKGROUND + YELLOW + "<<< The customer #" + customerId + " could not disconnect correctly from operator #" + operatorId + ".\n" + RESET);

            lock.unlock();
        }
    }
}