package by.tananushka.callcenter.domain;

import java.util.Objects;

public class Operator {

    private int id;

    public Operator() {
    }

    public Operator(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Operator operator = (Operator) o;
        return id == operator.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Operator{" +
                "id=" + id +
                '}';
    }
}
