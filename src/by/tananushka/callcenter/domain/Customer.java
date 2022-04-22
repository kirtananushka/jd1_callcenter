package by.tananushka.callcenter.domain;

import by.tananushka.callcenter.CallCenter;
import by.tananushka.callcenter.util.CallCenterUtil;
import by.tananushka.callcenter.util.OperatorUtil;
import by.tananushka.callcenter.util.Switchboard;

import java.util.Objects;

public class Customer extends Thread {

    private static OperatorUtil operatorTeam = OperatorUtil.getInstance();

    private Switchboard switchboard;
    private int customerId;
    private Operator operator;

    public Customer() {
    }

    public Customer(Switchboard switchboard, int customerId) {
        this.switchboard = switchboard;
        this.customerId = customerId;
        this.operator = null;
    }

    public static OperatorUtil getOperatorTeam() {
        return operatorTeam;
    }

    public static void setOperatorTeam(OperatorUtil operatorTeam) {
        Customer.operatorTeam = operatorTeam;
    }

    @Override
    public void run() {

        while (OperatorUtil.getInstance().getNumberOfFreeOperators() == 0) {
            try {
                Thread.sleep(CallCenter.THREAD_SLEEP * (long) (Math.random() * 5 + 1));
            } catch (InterruptedException e) {
                e.printStackTrace();
                Thread.currentThread().interrupt();
            }
        }
        this.operator = CallCenterUtil.getInstance().connect(switchboard, customerId, operatorTeam.addOperatorToBusy());
        CallCenterUtil.getInstance().disconnect(switchboard, customerId, operatorTeam.addOperatorToFree(operator));
    }

    public Switchboard getSwitchboard() {
        return switchboard;
    }

    public void setSwitchboard(Switchboard switchboard) {
        this.switchboard = switchboard;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public Operator getOperator() {
        return operator;
    }

    public void setOperator(Operator operator) {
        this.operator = operator;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Customer customer = (Customer) o;
        return customerId == customer.customerId &&
                Objects.equals(switchboard, customer.switchboard) &&
                Objects.equals(operator, customer.operator);
    }

    @Override
    public int hashCode() {
        return Objects.hash(switchboard, customerId, operator);
    }

    @Override
    public String toString() {
        return "Customer{" +
                "switchboard=" + switchboard +
                ", customerId=" + customerId +
                ", operator=" + operator +
                '}';
    }
}