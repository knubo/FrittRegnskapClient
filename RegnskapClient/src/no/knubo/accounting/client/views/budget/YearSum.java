package no.knubo.accounting.client.views.budget;

class YearSum implements Comparable {
    int year;
    double sumCourse;
    private double sumTotal;

    public YearSum(int year) {
        this.year = year;
    }

    public int getYear() {
        return year;
    }

    public void addCourse(double d) {
        sumCourse += d;
        sumTotal += d;
    }

    public double getCourse() {
        return sumCourse;
    }

    public double getTotal() {
        return sumTotal;
    }

    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + year;
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof YearSum))
            return false;
        final YearSum other = (YearSum) obj;
        if (year != other.year)
            return false;
        return true;
    }

    public int compareTo(Object arg0) {
        return year - ((YearSum) arg0).year;
    }

}
