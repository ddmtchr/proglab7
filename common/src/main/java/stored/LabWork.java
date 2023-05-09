package stored;

import utility.LabWorkStatic;

import java.time.ZonedDateTime;

/**
 * Class to be contained in the collection.
 */
public class LabWork implements Comparable<LabWork> {
    private long id;
    private final String name;
    private final Coordinates coordinates;
    private final java.time.ZonedDateTime creationDate;
    private final Integer minimalPoint;
    private final long averagePoint;
    private final Difficulty difficulty;
    private Discipline discipline;

    public LabWork(LabWorkStatic lws) {
        this.name = lws.getName();
        this.coordinates = lws.getCoordinates();
        this.creationDate = lws.getCreationDate();
        this.minimalPoint = lws.getMinimalPoint();
        this.averagePoint = lws.getAveragePoint();
        this.difficulty = lws.getDifficulty();
        this.discipline = lws.getDiscipline();
    }

    public LabWork(long id, String name, Coordinates coordinates, ZonedDateTime creationDate,
                   int minimalPoint, long averagePoint, Difficulty difficulty, Discipline discipline) {
        this.id = id;
        this.name = name;
        this.coordinates = coordinates;
        this.creationDate = creationDate;
        this.minimalPoint = minimalPoint;
        this.averagePoint = averagePoint;
        this.difficulty = difficulty;
        this.discipline = discipline;
    }

    public void setId(long id) {
        this.id = id;
    }

    /**
     * Gets the ID of LabWork.
     * @return ID of LabWork
     */
    public long getId() {
        return id;
    }

    /**
     * Gets the name of LabWork.
     * @return Name of LabWork
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the coordinates of LabWork.
     * @return Coordinates of LabWork
     */
    public Coordinates getCoordinates() {
        return coordinates;
    }

    /**
     * Gets the creation date of LabWork.
     * @return Creation date of LabWork
     */
    public java.time.ZonedDateTime getCreationDate() {
        return creationDate;
    }

    /**
     * Gets the minimal point of LabWork.
     * @return Minimal point of LabWork
     */
    public int getMinimalPoint() {
        return minimalPoint;
    }

    /**
     * Gets the average point of LabWork.
     * @return Average point of LabWork
     */
    public long getAveragePoint() {
        return averagePoint;
    }

    /**
     * Gets the difficulty of LabWork.
     * @return Difficulty of LabWork
     */
    public Difficulty getDifficulty() {
        return difficulty;
    }

    /**
     * Gets the discipline of LabWork.
     * @return Discipline of LabWork
     */
    public Discipline getDiscipline() {
        return discipline;
    }

    /**
     * Sets the discipline of LabWork.
     * @param d Discipline to be set
     */
    public void setDiscipline(Discipline d) {
        this.discipline = d;
    }
    /**
     * Compares two names of LabWorks lexicographically.
     * @param lwToCompare the object to be compared.
     * @return the value 0 if the argument string is equal to this string;
     * a value less than 0 if this string is lexicographically less than the string argument;
     * and a value greater than 0 if this string is lexicographically greater than the string argument.
     */
    @Override
    public int compareTo(LabWork lwToCompare) {
        return getName().compareTo(lwToCompare.getName());
    }

    /**
     * Gets string representation of LabWork.
     * @return string representation of LabWork
     */
    @Override
    public String toString() {
        return "LabWork " + "id = " + id + "\n\t\tname = " + name + "\n\t\tcoordinates = " + coordinates.toString() +
                "\n\t\tcreationDate = " + creationDate.toString() + "\n\t\tminimalPoint = " + minimalPoint +
                "\n\t\taveragePoint = " + averagePoint + "\n\t\tdifficulty = " + difficulty.name() +
                "\n\t\tdiscipline = " + (discipline == null ? "null" : discipline.toString());
    }

}
