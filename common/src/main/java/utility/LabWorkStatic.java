package utility;

import stored.Coordinates;
import stored.Difficulty;
import stored.Discipline;

import java.io.Serializable;
import java.time.ZonedDateTime;

public class LabWorkStatic implements Serializable {
    private final String name;
    private final Coordinates coordinates;
    private final ZonedDateTime creationDate;
    private final Integer minimalPoint;
    private final long averagePoint;
    private final Difficulty difficulty;
    private final Discipline discipline;

    public LabWorkStatic(String name, Coordinates coordinates, Integer minimalPoint,
                         long averagePoint, Difficulty difficulty, Discipline discipline) {
        this.name = name;
        this.coordinates = coordinates;
        this.creationDate = ZonedDateTime.now();
        this.minimalPoint = minimalPoint;   
        this.averagePoint = averagePoint;
        this.difficulty = difficulty;
        this.discipline = discipline;
        System.out.println();
    }

    public String getName() {
        return name;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public Integer getMinimalPoint() {
        return minimalPoint;
    }

    public long getAveragePoint() {
        return averagePoint;
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }

    public Discipline getDiscipline() {
        return discipline;
    }

    public ZonedDateTime getCreationDate() {
        return creationDate;
    }
}
