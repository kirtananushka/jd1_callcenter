package by.tananushka.callcenter.util;

import java.util.concurrent.atomic.AtomicInteger;

class CustomerUtil {

    private static CustomerUtil instance;

    private AtomicInteger waitingCustomers = new AtomicInteger();
    private AtomicInteger connectedCustomers = new AtomicInteger();

    static synchronized CustomerUtil getInstance() {
        if (instance == null) {
            instance = new CustomerUtil();
        }
        return instance;
    }

    int increaseWaitingCustomers() {
        return waitingCustomers.incrementAndGet();
    }

    int decreaseWaitingCustomers() {
        return waitingCustomers.decrementAndGet();
    }

    int increaseConnectedCustomers() {
        return connectedCustomers.incrementAndGet();
    }

    int decreaseConnectedCustomers() {
        return connectedCustomers.decrementAndGet();
    }

    int getWaitingCustomers() {
        return waitingCustomers.get();
    }

    int getConnectedCustomers() {
        return connectedCustomers.get();
    }
}
