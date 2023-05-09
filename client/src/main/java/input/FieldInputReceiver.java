package input;

import exceptions.ErrorInScriptException;
import exceptions.NumberOutOfBoundsException;
import exceptions.WrongInputFormatException;
import stored.Coordinates;
import stored.Difficulty;
import stored.Discipline;
import utility.LabWorkStatic;
import validation.*;

import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * Class for receiving class fields entered by user or read from file.
 */
public class FieldInputReceiver extends InputReceiver {
    /**
     * Creates an instance of FieldInputReceiver and sets user command input mode.
     * @param s Scanner that reads strings
     */
    public FieldInputReceiver(Scanner s) {
        super(s);
    }

    /**
     * Asks user or script to enter the X coordinate of LabWork.
     * @return X coordinate
     * @throws ErrorInScriptException If the script contains an input format error
     */
    private int enterX() throws ErrorInScriptException {
        String stringX;
        int x;
        System.out.println("Введите координату X (X > -934): ");
        while (true) {
            try {
                stringX = scanner.nextLine().trim();
                if (fileReadMode) System.out.println(stringX);
                x = Integer.parseInt(stringX);
                if (!XValidator.isValid(x)) throw new NumberOutOfBoundsException();
                break;
            } catch (NumberOutOfBoundsException e) {
                if (!fileReadMode) System.out.println("Координата должна быть > -934, повторите ввод: ");
                else throw new ErrorInScriptException();
            } catch (NumberFormatException e) {
                if (!fileReadMode) System.out.println("Формат ввода - целое число, повторите ввод: ");
                else throw new ErrorInScriptException();
            } catch (NoSuchElementException e) {
                if (!fileReadMode) System.out.println("Ввод завершен пользователем");
                System.exit(0);
            }
        }
        return x;
    }

    /**
     * Asks user or script to enter the Y coordinate of LabWork.
     * @return Y coordinate
     * @throws ErrorInScriptException If the script contains an input format error
     */
    private float enterY() throws ErrorInScriptException {
        String stringY;
        float y;
        System.out.println("Введите координату Y (Y <= 946): ");
        while (true) {
            try {
                stringY = scanner.nextLine().trim();
                if (fileReadMode) System.out.println(stringY);
                y = Float.parseFloat(stringY);
                if (!YValidator.isValid(y)) throw new NumberOutOfBoundsException();
                break;
            } catch (NumberOutOfBoundsException e) {
                if (!fileReadMode) System.out.println("Координата должна быть <= 946, повторите ввод: ");
                else throw new ErrorInScriptException();
            } catch (NumberFormatException e) {
                if (!fileReadMode) System.out.println("Формат ввода - число с плавающей точкой, повторите ввод: ");
                else throw new ErrorInScriptException();
            } catch (NoSuchElementException e) {
                if (!fileReadMode) System.out.println("Ввод завершен пользователем");
                System.exit(0);
            }
        }
        return y;
    }

    /**
     * Asks user or script to enter the name of LabWork.
     * @return Name of LabWork
     * @throws ErrorInScriptException If the script contains an input format error
     */
    private String enterName() throws ErrorInScriptException {
        String name;
        System.out.println("Введите поле name: ");
        while (true) {
            try {
                name = scanner.nextLine().trim();
                if (fileReadMode) System.out.println(name);
                if (!NameValidator.isValid(name)) throw new WrongInputFormatException();
                break;
            } catch (WrongInputFormatException e) {
                if (!fileReadMode) System.out.println("Имя не может быть пустым, повторите ввод: ");
                else throw new ErrorInScriptException();
            } catch (NoSuchElementException e) {
                if (!fileReadMode) System.out.println("Ввод завершен пользователем");
                System.exit(0);
            }
        }
        return name;
    }

    /**
     * Creates the Coordinates instance from X and Y coordinates.
     * @return Coordinates object
     * @throws ErrorInScriptException If the script contains an input format error
     */
    private Coordinates enterCoordinates() throws ErrorInScriptException {
        int x = enterX();
        Float y = enterY();
        return new Coordinates(x, y);
    }

    /**
     * Asks user or script to enter the minimal point of LabWork.
     * @return Minimal point
     * @throws ErrorInScriptException If the script contains an input format error
     */
    private int enterMinPoint() throws ErrorInScriptException {
        String stringX;
        int x;
        System.out.println("Введите минимальный балл: ");
        while (true) {
            try {
                stringX = scanner.nextLine().trim();
                if (fileReadMode) System.out.println(stringX);
                x = Integer.parseInt(stringX);
                if (!MinPointValidator.isValid(x)) throw new NumberOutOfBoundsException();
                break;
            } catch (NumberOutOfBoundsException e) {
                if (!fileReadMode) System.out.println("Балл должен быть > 0, повторите ввод: ");
                else throw new ErrorInScriptException();
            } catch (NumberFormatException e) {
                if (!fileReadMode) System.out.println("Формат ввода - целое число, повторите ввод: ");
                else throw new ErrorInScriptException();
            } catch (NoSuchElementException e) {
                if (!fileReadMode) System.out.println("Ввод завершен пользователем");
                System.exit(0);
            }
        }
        return x;
    }

    /**
     * Asks user or script to enter the average point of LabWork.
     * @return Average point
     * @throws ErrorInScriptException If the script contains an input format error
     */
    private long enterAveragePoint() throws ErrorInScriptException {
        String stringX;
        long x;
        System.out.println("Введите средний балл: ");
        while (true) {
            try {
                stringX = scanner.nextLine().trim();
                if (fileReadMode) System.out.println(stringX);
                x = Long.parseLong(stringX);
                if (!AveragePointValidator.isValid(x)) throw new NumberOutOfBoundsException();
                break;
            } catch (NumberOutOfBoundsException e) {
                if (!fileReadMode) System.out.println("Балл должен быть > 0, повторите ввод: ");
                else throw new ErrorInScriptException();
            } catch (NumberFormatException e) {
                if (!fileReadMode) System.out.println("Формат ввода - целое число, повторите ввод: ");
                else throw new ErrorInScriptException();
            } catch (NoSuchElementException e) {
                if (!fileReadMode) System.out.println("Ввод завершен пользователем");
                System.exit(0);
            }
        }
        return x;
    }

    /**
     * Asks user or script to enter the difficulty of LabWork.
     * @return Difficulty object
     * @throws ErrorInScriptException If the script contains an input format error
     */
    private Difficulty enterDifficulty() throws ErrorInScriptException {
        String diffStr;
        Difficulty diff;
        System.out.println("Введите сложность. Возможные варианты:");
        Difficulty.printNameList();
        while (true) {
            try {
                diffStr = scanner.nextLine().trim().toUpperCase();
                if (fileReadMode) System.out.println(diffStr);
                if (diffStr.isBlank() || diffStr.isEmpty()) throw new WrongInputFormatException();
                diff = Difficulty.valueOf(diffStr);
                break;
            } catch (WrongInputFormatException e) {
                if (!fileReadMode) System.out.println("Значение должно быть указано, повторите ввод: ");
                else throw new ErrorInScriptException();
            } catch (IllegalArgumentException e) {
                if (!fileReadMode) System.out.println("Такого варианта нет, повторите ввод: ");
                else throw new ErrorInScriptException();
            } catch (NoSuchElementException e) {
                if (!fileReadMode) System.out.println("Ввод завершен пользователем");
                System.exit(0);
            }
        }
        return diff;
    }

    /**
     * Creates the Discipline instance from name, lecture hours, practice hours and self study hours.
     * @return Discipline object
     * @throws ErrorInScriptException If the script contains an input format error
     */
    private Discipline enterDiscipline() throws ErrorInScriptException {
        String name = enterDisciplineName();
        if (name == null) return null;
        long lectureHours = enterLectureHours();
        Integer practiceHours = enterPracticeHours();
        long selfStudyHours = enterSelfStudyHours();
        return new Discipline(name, lectureHours, practiceHours, selfStudyHours);
    }

     /**
     * Asks user or script to enter the discipline name of LabWork.
     * @return Name of discipline
     */
    private String enterDisciplineName() {
        String disName;
        System.out.println("Введите имя дисциплины (пустое имя, если дисциплина не заполняется): ");
        while (true) {
            try {
                disName = scanner.nextLine().trim();
                if (fileReadMode) System.out.println(disName);
                if (!NameValidator.isValid(disName)) throw new WrongInputFormatException();
                break;
            } catch (WrongInputFormatException e) {
                return null;
            } catch (NoSuchElementException e) {
                if (!fileReadMode) System.out.println("Ввод завершен пользователем");
                System.exit(0);
            }
        }
        return disName;
    }

    /**
     * Asks user or script to enter the discipline lecture hours of LabWork.
     * @return Lecture hours of discipline
     * @throws ErrorInScriptException If the script contains an input format error
     */
    private long enterLectureHours() throws ErrorInScriptException {
        String strLectureHours;
        long lectureHours;
        System.out.println("Введите кол-во часов лекций: ");
        while (true) {
            try {
                strLectureHours = scanner.nextLine().trim();
                if (fileReadMode) System.out.println(strLectureHours);
                lectureHours = Long.parseLong(strLectureHours);
                break;
            } catch (NumberFormatException e) {
                if (!fileReadMode) System.out.println("Формат ввода - целое число, повторите ввод: ");
                else throw new ErrorInScriptException();
            } catch (NoSuchElementException e) {
                if (!fileReadMode) System.out.println("Ввод завершен пользователем");
                System.exit(0);
            }
        }
        return lectureHours;
    }

    /**
     * Asks user or script to enter the discipline practice hours of LabWork.
     * @return Practice hours of discipline
     * @throws ErrorInScriptException If the script contains an input format error
     */
    private int enterPracticeHours() throws ErrorInScriptException {
        String strPracticeHours;
        int practiceHours;
        System.out.println("Введите кол-во часов практики: ");
        while (true) {
            try {
                strPracticeHours = scanner.nextLine().trim();
                if (fileReadMode) System.out.println(strPracticeHours);
                practiceHours = Integer.parseInt(strPracticeHours);
                break;
            } catch (NumberFormatException e) {
                if (!fileReadMode) System.out.println("Формат ввода - целое число, повторите ввод: ");
                else throw new ErrorInScriptException();
            } catch (NoSuchElementException e) {
                if (!fileReadMode) System.out.println("Ввод завершен пользователем");
                System.exit(0);
            }
        }
        return practiceHours;
    }

    /**
     * Asks user or script to enter the discipline self study hours of LabWork.
     * @return Self study hours of discipline
     * @throws ErrorInScriptException If the script contains an input format error
     */
    private long enterSelfStudyHours() throws ErrorInScriptException {
        String strSelfStudyHours;
        long selfStudyHours;
        System.out.println("Введите кол-во часов самостоятельного изучения: ");
        while (true) {
            try {
                strSelfStudyHours = scanner.nextLine().trim();
                if (fileReadMode) System.out.println(strSelfStudyHours);
                selfStudyHours = Long.parseLong(strSelfStudyHours);
                break;
            } catch (NumberFormatException e) {
                if (!fileReadMode) System.out.println("Формат ввода - целое число, повторите ввод: ");
                else throw new ErrorInScriptException();
            } catch (NoSuchElementException e) {
                if (!fileReadMode) System.out.println("Ввод завершен пользователем");
                System.exit(0);
            }
        }
        return selfStudyHours;
    }

    /**
     * Creates the LabWork instance from input information.
     * @return LabWork object
     * @throws ErrorInScriptException If the script contains an input format error
     */
    public LabWorkStatic enterLabWork() throws ErrorInScriptException {
        return new LabWorkStatic(enterName(),
                enterCoordinates(),
                enterMinPoint(),
                enterAveragePoint(),
                enterDifficulty(),
                enterDiscipline());
    }
}
