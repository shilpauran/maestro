package com.sap.slh.tax.maestro.tax.jmx;

public abstract class Counter implements CounterMBean {

    protected int count = 0;

    public void incrementCount() {
        this.count++;
    }

    public int getCount() {
        return this.count;
    }

    public void resetCount() {
        this.count = 0;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Counter [count=");
        sb.append(count);
        sb.append("]");
        return sb.toString();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + count;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Counter other = (Counter)obj;
        if (count != other.count)
            return false;
        return true;
    }    

}