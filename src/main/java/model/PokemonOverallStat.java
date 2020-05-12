package model;

import static util.StringUtil.formatAndTruncate;

public class PokemonOverallStat {
    private String name;
    private long overallStat;

    @Override
    public String toString() {
        return formatAndTruncate(name, 12) + ": "+overallStat;
    }

    public PokemonOverallStat(String name, long overallStat) {
        this.name = name;
        this.overallStat = overallStat;
    }

    public String getName() {
        return name;
    }

    public long getOverallStat() {
        return overallStat;
    }
}
