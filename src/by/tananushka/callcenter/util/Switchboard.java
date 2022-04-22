package by.tananushka.callcenter.util;

import by.tananushka.callcenter.CallCenter;

import java.util.concurrent.Semaphore;

public class Switchboard extends Semaphore {

    public Switchboard() {
        super(CallCenter.NUMBER_OF_OPERATORS, true);
    }

    public Switchboard(int permits, boolean fair) {
        super(permits, fair);
    }
}
