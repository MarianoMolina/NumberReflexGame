package com.mariano.numberreflexgame;

/**
 * Created by marianom on 7/19/16.
 */
public class PlayerScores implements Comparable<PlayerScores> {

    private String name;

    private Long score;

    public PlayerScores(String name, Long score) {
        this.name = name;
        this.score = score;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getScore() {
        return score;
    }

    public void setScore(long score) {
        this.score = score;
    }

    @Override
    public int compareTo(PlayerScores another) {
        return -1*score.compareTo(another.getScore());
    }
}