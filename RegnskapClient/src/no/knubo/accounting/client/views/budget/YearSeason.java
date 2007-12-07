package no.knubo.accounting.client.views.budget;

public class YearSeason {
    final int year;
    final int season;

    public YearSeason(int year, int season) {
        this.year = year;
        this.season = season;
    }

    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + season;
        result = prime * result + year;
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof YearSeason))
            return false;
        YearSeason other = (YearSeason) obj;
        if (season != other.season)
            return false;
        if (year != other.year)
            return false;
        return true;
    }
}