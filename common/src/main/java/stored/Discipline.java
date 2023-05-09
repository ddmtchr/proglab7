package stored;


import java.io.Serializable;

/**
 * Class whose objects are being used as field of LabWork objects.
 */
public class Discipline implements Serializable {
    private final String name;
    private final long lectureHours;
    private final Integer practiceHours;
    private final long selfStudyHours;

    /**
     * Creates the Discipline instance.
     * @param name Name of discipline.
     * @param lectureHours Number of lecture hours of discipline.
     * @param practiceHours Number of practice hours of discipline.
     * @param selfStudyHours Number of self study hours of discipline.
     */
    public Discipline(String name, long lectureHours, Integer practiceHours, long selfStudyHours) {
        this.name = name;
        this.lectureHours = lectureHours;
        this.practiceHours = practiceHours;
        this.selfStudyHours = selfStudyHours;
    }

    /**
     * Gets the name of discipline.
     * @return Name of Discipline
     */
    public String getName() {
        return name;
    }

    public long getLectureHours() {
        return lectureHours;
    }

    public int getPracticeHours() {
        return practiceHours;
    }

    public long getSelfStudyHours() {
        return selfStudyHours;
    }

    /**
     * Gets string representation of Discipline object.
     * @return String representation of Discipline object
     */
    @Override
    public String toString() {
        return "Discipline(name = " + name + " lectureHours = " + lectureHours + " practiceHours = " + practiceHours +
                " selfStudyHours = " + selfStudyHours + ")";
    }
}
