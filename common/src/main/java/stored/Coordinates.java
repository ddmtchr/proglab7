package stored;

import java.io.Serializable;
import java.util.Objects;

public class Coordinates implements Serializable {
    private final int x; // Значение поля должно быть больше -934
    private final Float y; // Максимальное значение поля: 946, Поле не может быть null

    public Coordinates(int x, Float y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Gets the X coordinate.
     * @return X coordinate
     */
    public int getX() {
        return x;
    }

    /**
     * Gets the Y coordinate.
     * @return Y coordinate
     */
    public Float getY() {
        return y;
    }

    /**
     * Gets string representation of Coordinates.
     * @return string representation of Coordinates
     */
    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Coordinates c = (Coordinates) o;
        return x == c.x && (float)y == (float)(c.y);
    }

}
