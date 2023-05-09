package validation;

import stored.LabWork;

public class LabWorkValidator {
    public static boolean isValid(LabWork lw) {
        return IdValidator.isValid(lw.getId()) && NameValidator.isValid(lw.getName())
                && lw.getCoordinates() != null && lw.getCoordinates().getY() != null
                && XValidator.isValid(lw.getCoordinates().getX())
                && YValidator.isValid(lw.getCoordinates().getY()) && lw.getCreationDate() != null
                && MinPointValidator.isValid(lw.getMinimalPoint())
                && AveragePointValidator.isValid(lw.getAveragePoint()) && lw.getDifficulty() != null;
    }
}
