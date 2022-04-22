package by.tananushka.callcenter.util;

import by.tananushka.callcenter.CallCenter;
import by.tananushka.callcenter.domain.Customer;
import by.tananushka.callcenter.domain.Operator;
import by.tananushka.callcenter.view.View;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public final class CallCenterUtil {

    private static CallCenterUtil instance;

    private static OperatorUtil operatorTeam = OperatorUtil.getInstance();

    private static AtomicBoolean isFree = new AtomicBoolean(true);
    private static AtomicInteger counter = new AtomicInteger(0);
    private static AtomicInteger connectionProblemCounter = new AtomicInteger(0);
    private static AtomicInteger disconnectionProblemCounter = new AtomicInteger(0);

    private final Lock lock1 = new ReentrantLock(true);

    private CallCenterUtil() {
    }

    public static synchronized CallCenterUtil getInstance() {
        if (instance == null) {
            instance = new CallCenterUtil();
        }
        return instance;
    }

    public void start(int numberOfOperators, int numberOfCustomers) {

        Switchboard switchboard = new Switchboard(numberOfOperators, true);

        ExecutorService executorService = Executors.newFixedThreadPool(numberOfCustomers);

        AtomicBoolean isPrintedOnAdding = new AtomicBoolean(false);

        try {

            operatorTeam.addOperatorsToTeam(numberOfOperators);

            View.getInstance().printOnStart();

            for (int customerId = 1; customerId <= numberOfCustomers; customerId++) {

                isPrintedOnAdding.set(View.getInstance().printOnAdding(counter.incrementAndGet(),
                        customerId,
                        CustomerUtil.getInstance().increaseWaitingCustomers(),
                        CustomerUtil.getInstance().getConnectedCustomers(),
                        OperatorUtil.getInstance().getNumberOfFreeOperators()));

                if (isPrintedOnAdding.get()) {
                    executorService.submit(new Customer(switchboard, customerId));
                    Thread.sleep(CallCenter.THREAD_SLEEP * (long) (Math.random() * 5 + 1));
                }
            }

            executorService.shutdown();
            executorService.awaitTermination(60, TimeUnit.SECONDS);

        } catch (InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }
    }

    public Operator connect(Switchboard switchboard, int customerId, Operator operator) {

        try {

            lock1.lock();

            AtomicBoolean isPrintedOnConnection = new AtomicBoolean(false);

            Thread.sleep(CallCenter.THREAD_SLEEP * (long) (Math.random() * 5 + 1));

            while (!isFree.get() || OperatorUtil.getInstance().getNumberOfFreeOperators() <= 0) {

                lock1.unlock();

                View.getInstance().connectionProblem(customerId, operator.getId());

                Thread.sleep(CallCenter.THREAD_SLEEP);

                lock1.lock();

                connectionProblemCounter.incrementAndGet();
            }

            if (isFree.get() && OperatorUtil.getInstance().getNumberOfFreeOperators() > 0) {

                isFree.set(false);

                isPrintedOnConnection.set(View.getInstance().printOnConnection(counter.incrementAndGet(),
                        customerId, operator.getId(),
                        CustomerUtil.getInstance().decreaseWaitingCustomers(),
                        CustomerUtil.getInstance().increaseConnectedCustomers(),
                        OperatorUtil.getInstance().decreaseNumberOfFreeOperators()));

                if (isPrintedOnConnection.get()) {
                    switchboard.acquire();
                }
                isFree.set(true);
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();

        } finally {
            lock1.unlock();
        }

        return operator;
    }

    public void disconnect(Switchboard switchboard, int customerId, int operatorId) {

        try {

            lock1.lock();

            if (!isFree.get() || CustomerUtil.getInstance().getConnectedCustomers() <= 0) {

                lock1.unlock();

                View.getInstance().disconnectionProblem(customerId, operatorId);

                Thread.sleep(CallCenter.THREAD_SLEEP);

                lock1.lock();

                disconnectionProblemCounter.incrementAndGet();

            }

            if (isFree.get() && CustomerUtil.getInstance().getConnectedCustomers() > 0) {

                isFree.set(false);

                boolean isPrintedOnDisconnection;

                Thread.sleep(CallCenter.THREAD_SLEEP * (long) (Math.random() * 5 + 1));

                isPrintedOnDisconnection = View.getInstance().printOnDisconnection(counter.incrementAndGet(),
                        customerId, operatorId,
                        CustomerUtil.getInstance().getWaitingCustomers(),
                        CustomerUtil.getInstance().decreaseConnectedCustomers(),
                        OperatorUtil.getInstance().increaseNumberOfFreeOperators());

                if (isPrintedOnDisconnection) {
                    switchboard.release();
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();

        } finally {
            isFree.set(true);
            lock1.unlock();

            onFinish();
        }
    }

    private void onFinish() {

        try {
            Thread.sleep(CallCenter.THREAD_SLEEP * 5L);
        } catch (InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }

        if (CustomerUtil.getInstance().getWaitingCustomers() == 0 &&
                CustomerUtil.getInstance().getConnectedCustomers() == 0 &&
                OperatorUtil.getInstance().getNumberOfFreeOperators() == CallCenter.NUMBER_OF_OPERATORS) {
            View.getInstance().printOnFinish(connectionProblemCounter.get(), disconnectionProblemCounter.get());
        }
    }
}