package by.tananushka.callcenter;

import by.tananushka.callcenter.util.CallCenterUtil;

public class CallCenter {

    public static final int NUMBER_OF_OPERATORS = 3;

    public static final int NUMBER_OF_CUSTOMERS = 5;

    public static final int THREAD_SLEEP = 100;

    public static final boolean WARNINGS = true;

    public static void main(String[] args) {

        CallCenterUtil.getInstance().start(NUMBER_OF_OPERATORS, NUMBER_OF_CUSTOMERS);
    }
}
