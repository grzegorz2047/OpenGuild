package pl.grzegorz2047.openguild.ranking;

public final class RankDifference {
    private final int lostDifference;
    private final int winDifference;
    private final double winNewPoints;
    private final double lostNewPoints;

    public RankDifference(int lostDifference, int winDifference, double wNewPoints, double lNewPoints) {
        this.lostDifference = lostDifference;
        this.winDifference = winDifference;
        this.winNewPoints = wNewPoints;
        this.lostNewPoints = lNewPoints;
    }

    public int getWinDifference() {
        return winDifference;
    }

    public int getLostDifference() {
        return lostDifference;
    }

    public double getWinNewPoints() {
        return winNewPoints;
    }

    public double getLostNewPoints() {
        return lostNewPoints;
    }
}
