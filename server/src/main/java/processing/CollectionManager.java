package processing;

import exceptions.NoSuchIDException;
import stored.LabWork;
import utility.ResponseBuilder;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Vector;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.IntStream;


/**
 * Class to manage the collection.
 */
public class CollectionManager {
    private static final Vector<LabWork> labStorage = new Vector<>();
    private static ZonedDateTime lastInitTime = null;
    private static ZonedDateTime lastSaveTime = null;
    private static final Lock lock = new ReentrantLock();

    /**
     * Gets last initialization time of collection for info command.
     *
     * @return String representation of initialization time
     */
    public static String getLastInitTime() {
        return (lastInitTime == null) ? null :
                lastInitTime.toLocalDate() + " " +
                        lastInitTime.toLocalTime().truncatedTo(ChronoUnit.SECONDS) + " " +
                        lastInitTime.getZone();
    }

    /**
     * Sets last initialization time of collection for info command when collection is read from database.
     */
    public static void setLastInitTime() {
        lastInitTime = ZonedDateTime.now();
    }

    /**
     * Gets last save time of collection for info command.
     *
     * @return String representation of save time
     */
    public static String getLastSaveTime() {
        try {
            lock.lock();
            return (lastSaveTime == null) ? null : lastSaveTime.toLocalDate() + " " +
                    lastSaveTime.toLocalTime().truncatedTo(ChronoUnit.SECONDS) + " " +
                    lastSaveTime.getZone();
        } finally {
            lock.unlock();
        }
    }

    /**
     * Sets last save time of collection for info command when collection is saved to file.
     */
    public static void setLastSaveTime() {
        try {
            lock.lock();
            lastSaveTime = ZonedDateTime.now();
        } finally {
            lock.unlock();
        }
    }

    /**
     * Gets the collection itself.
     *
     * @return Collection
     */
    public static Vector<LabWork> getCollection() {
        try {
            lock.lock();
            return labStorage;
        } finally {
            lock.unlock();
        }
    }

    /**
     * Checks if the collection is empty.
     *
     * @return true if collection is empty, false otherwise
     */
    public static boolean isEmpty() {
        try {
            lock.lock();
            return labStorage.isEmpty();
        } finally {
            lock.unlock();
        }
    }

    /**
     * Gets the number of elements in the collection.
     *
     * @return Number of the elements
     */
    public static int size() {
        try {
            lock.lock();
            return labStorage.size();
        } finally {
            lock.unlock();
        }
    }

    /**
     * Adds the LabWork to the collection.
     *
     * @param lw LabWork to be added
     */
    public static void add(LabWork lw) {
        try {
            lock.lock();
            labStorage.add(lw);
        } finally {
            lock.unlock();
        }
    }

    /**
     * Removes the LabWork from the collection by its index.
     *
     * @param index Index of LabWork to be removed
     */
    public static void remove(int index) {
        try {
            lock.lock();
            labStorage.remove(index);
        } finally {
            lock.unlock();
        }
    }

    public static void remove(LabWork lw) {
        try {
            lock.lock();
            labStorage.remove(lw);
        } finally {
            lock.unlock();
        }
    }

    /**
     * Adds the LabWork to the collection at specified index.
     *
     * @param lw    LabWork to be added
     * @param index Index of LabWork to be added
     */
    public static void insertAt(LabWork lw, int index) {
        try {
            lock.lock();
            labStorage.insertElementAt(lw, index);
        } finally {
            lock.unlock();
        }
    }

    /**
     * Shows the minimal point field of each element from collection.
     *
     * @param collection Collection to be shown
     */
    public static void showMinPoint(Vector<LabWork> collection) {
        try {
            lock.lock();
            for (LabWork lw : collection) {
                ResponseBuilder.appendln("- LabWork " + "id = " + lw.getId() + "\n\t\tminimalPoint = " + lw.getMinimalPoint());
            }
        } finally {
            lock.unlock();
        }
    }

    /**
     * Gets index of element by ID.
     *
     * @param id ID of element
     * @return Index of element
     * @throws NoSuchIDException If there is no such ID in the collection
     */
    public static int getIndexById(long id) throws NoSuchIDException {
        try {
            lock.lock();
            return IntStream.range(0, labStorage.size()).filter(i -> labStorage.get(i).getId() == id)
                    .findFirst().orElseThrow(NoSuchIDException::new);
        } finally {
            lock.unlock();
        }
    }

    public static LabWork getElementById(long id) {
        try {
            lock.lock();
            return labStorage.stream().filter(i -> i.getId() == id).findFirst().orElse(null);
        } finally {
            lock.unlock();
        }
    }

    /**
     * Clears the collection.
     */
    public static void clear() {
        try {
            lock.lock();
            labStorage.clear();
        } finally {
            lock.unlock();
        }
    }

    public static boolean checkObjectOwner(long id, String username) {
        return username.equals(getElementById(id).getUsername());
    }

    /**
     * Returns a string representation of this collection.
     *
     * @return a string representation of this collection
     */
    @Override
    public String toString() {
        try {
            lock.lock();
            return labStorage.toString();
        } finally {
            lock.unlock();
        }
    }
}
