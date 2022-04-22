package by.tananushka.callcenter.util;

import by.tananushka.callcenter.domain.Operator;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class OperatorUtil {

    private static OperatorUtil instance;

    private Operator operator;

    private AtomicInteger numberOfFreeOperators = new AtomicInteger();

    private BlockingQueue<Operator> freeOperators;
    private BlockingQueue<Operator> busyOperators;

    private final Lock lock1 = new ReentrantLock(true);
    private final Lock lock2 = new ReentrantLock(true);

    private OperatorUtil() {

    }

    public static synchronized OperatorUtil getInstance() {
        if (instance == null) {
            instance = new OperatorUtil();
        }
        return instance;
    }

    void addOperatorsToTeam(int numberOfOperators) {

        freeOperators = new ArrayBlockingQueue<>(numberOfOperators);
        busyOperators = new ArrayBlockingQueue<>(numberOfOperators);

        for (int i = 1; i <= numberOfOperators; i++) {
            freeOperators.add(new Operator(i));
            numberOfFreeOperators.incrementAndGet();
        }
    }

    public Operator addOperatorToBusy() {

        try {
            lock1.lock();

            operator = freeOperators.take();
            busyOperators.put(operator);

        } catch (InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();

        } finally {

            lock1.unlock();
        }
        return operator;
    }

    public int addOperatorToFree(Operator operator) {

        try {

            lock2.lock();

            if (busyOperators.remove(operator)) {

                freeOperators.put(operator);
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();

        } finally {
            lock2.unlock();
        }

        return operator.getId();
    }

    synchronized int increaseNumberOfFreeOperators() {
        return numberOfFreeOperators.incrementAndGet();
    }

    synchronized int decreaseNumberOfFreeOperators() {
        return numberOfFreeOperators.decrementAndGet();
    }

    public synchronized int getNumberOfFreeOperators() {
        return Math.max(numberOfFreeOperators.get(), 0);
    }
}