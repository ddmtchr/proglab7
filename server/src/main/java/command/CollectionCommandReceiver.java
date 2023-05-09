package command;

import database.DBManager;
import exceptions.AccessPermissionException;
import exceptions.EmptyCollectionException;
import exceptions.NoSuchIDException;
import processing.CollectionManager;
import stored.LabWork;
import utility.LabWorkStatic;
import utility.ResponseBuilder;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;


/**
 * Class that contains implementations of commands.
 */
public class CollectionCommandReceiver implements Serializable {

    private final DBManager dbManager;
    private final String username;
    private final String password;

    public CollectionCommandReceiver(DBManager dbManager, String username, String password) {
        this.dbManager = dbManager;
        this.username = username;
        this.password = password;
    }

    /**
     * Implements command help. Displays help on available commands.
     *
     * @return 0 if command executed correctly;
     * 1 if an error occurred while executing
     */
    public int help() {
        int execCode = 0;
        ResponseBuilder.appendln("- help : вывести справку по доступным командам\n" +
                "- info : вывести информацию о коллекции\n" +
                "- show : вывести все элементы коллекции в строковом представлении\n" +
                "- add [element] : добавить новый элемент в коллекцию\n" +
                "- update {id} [element] : обновить значение элемента коллекции, id которого равен заданному\n" +
                "- remove_by_id {id} : удалить элемент из коллекции по его id\n" +
                "- clear : очистить коллекцию\n" +
                "- execute_script {file_name} : считать и исполнить скрипт из указанного файла\n" +
                "- exit : завершить работу клиента\n" +
                "- insert_at {index} [element] : добавить новый элемент в заданную позицию\n" +
                "- add_if_min [element] : добавить новый элемент в коллекцию, если его значение меньше, чем у наименьшего элемента этой коллекции\n" +
                "- remove_greater [element] : удалить из коллекции все элементы, превышающие заданный\n" +
                "- average_of_minimal_point : вывести среднее значение поля minimalPoint для всех элементов коллекции\n" +
                "- min_by_id : вывести любой объект из коллекции, значение поля id которого является минимальным\n" +
                "- print_field_ascending_minimal_point : вывести значения поля minimalPoint всех элементов в порядке возрастания");
        return execCode;
    }

    /**
     * Implements command info. Prints information about the collection to standard output.
     *
     * @return 0 if command executed correctly;
     * 1 if an error occurred while executing
     */
    public int info() {
        int execCode = 0;
        String lastInitTimeString = (CollectionManager.getLastInitTime() == null) ?
                "в текущей сессии не инициализирована" :
                CollectionManager.getLastInitTime();
        String lastSaveTimeString = (CollectionManager.getLastSaveTime() == null) ?
                "в текущей сессии не сохранена" :
                CollectionManager.getLastSaveTime();
        ResponseBuilder.appendln("Информация о коллекции:");
        ResponseBuilder.appendln("\tТип коллекции: " + CollectionManager.getCollection().getClass().getName());
        ResponseBuilder.appendln("\tКоличество элементов: " + CollectionManager.size());
        ResponseBuilder.appendln("\tВремя последней инициализации: " + lastInitTimeString);
        ResponseBuilder.appendln("\tВремя последнего сохранения: " + lastSaveTimeString);
        return execCode;
    }

    /**
     * Implements command show. Prints to standard output all elements of the collection in string representation.
     *
     * @return 0 if command executed correctly;
     * 1 if an error occurred while executing
     */
    public int show() {
        int execCode = 0;
        try {
            if (CollectionManager.isEmpty()) throw new EmptyCollectionException();
            List<Long> ids = new ArrayList<>();
            if (dbManager.getUsersObjectsIds(ids, username) == 0) {
                ResponseBuilder.appendln("Объекты пользователя " + username);
                for (long id : ids) {
                    int index = CollectionManager.getIndexById(id);
                    ResponseBuilder.appendln(CollectionManager.getCollection().get(index).toString());
                }
            } else {
                ResponseBuilder.appendln("Ошибка получения объектов из БД");
                execCode = 1;
            }
        } catch (EmptyCollectionException e) {
            ResponseBuilder.appendln("Коллекция пуста!");
        } catch (NoSuchIDException e) {
            ResponseBuilder.appendln("Рассинхронизация идентификаторов в коллекции и БД");
            execCode = 1;
        }
        return execCode;
    }

    /**
     * Implements command add. Adds a new element to the collection.
     *
     * @return 0 if command executed correctly;
     * 1 if an error occurred while executing
     */
    public int add(Object objectArgs) {
        int execCode;
        try {
            LabWorkStatic lws = (LabWorkStatic) objectArgs;
            LabWork lw = new LabWork(lws);
            long newId = dbManager.addElement(lws, username);
            if (newId >= 0) {
                lw.setId(newId);
                CollectionManager.add(lw);
                ResponseBuilder.appendln("Лабораторная работа успешно добавлена в коллекцию!");
                execCode = 0;
            } else {
                ResponseBuilder.appendln("Ошибка при добавлении объекта в БД");
                execCode = 1;
            }
        } catch (Exception e) {
            e.printStackTrace();
            execCode = 1;
        }
        return execCode;
    }

    /**
     * Implements command update. Updates the value of the collection element whose id is equal to the given one.
     *
     * @param args ID of the element to be updated
     * @return 0 if command executed correctly;
     * 1 if an error occurred while executing
     */
    public int update(String args, Object objectArgs) {
        int execCode;
        try {
            long id = Long.parseLong(args);
            LabWorkStatic lws = (LabWorkStatic) objectArgs;
            LabWork oldLabWork = CollectionManager.getElementById(id);
            if (dbManager.checkObjectOwner(id, username)) {
                if (dbManager.updateElement(id, lws, username) == 0) {
                    CollectionManager.remove(oldLabWork);
                    LabWork lw = new LabWork(lws);
                    lw.setId(id);
                    CollectionManager.add(lw);
                    ResponseBuilder.appendln("Элемент с id=" + id + " успешно обновлен");
                    execCode = 0;
                } else {
                    ResponseBuilder.appendln("Ошибка при обновлении объекта в БД");
                    execCode = 1;
                }
            } else throw new AccessPermissionException();
        } catch (NoSuchIDException e) {
            execCode = 1;
            ResponseBuilder.appendln("В коллекции нет элемента с указанным ID");
        } catch (NumberFormatException e) {
            execCode = 1;
            ResponseBuilder.appendln("Аргумент id - целое число");
        } catch (AccessPermissionException e) {
            execCode = 1;
            ResponseBuilder.appendln("Нет доступа к объекту с указанным ID");
        } catch (Exception e) {
            execCode = 1;
            e.printStackTrace();
        }
        return execCode;
    }

    /**
     * Implements command remove_by_id. Removes element from collection by its id.
     *
     * @param args ID of the element to be removed
     * @return 0 if command executed correctly;
     * 1 if an error occurred while executing
     */
    public int removeById(String args) {
        int execCode;
        try {
            if (CollectionManager.isEmpty()) throw new EmptyCollectionException();
            long id = Long.parseLong(args);
            if (dbManager.checkObjectOwner(id, username)) {
                if (dbManager.deleteElement(id) == 0) {
                    CollectionManager.remove(CollectionManager.getIndexById(id));
                    ResponseBuilder.appendln("Элемент с id=" + id + " успешно удален");
                    execCode = 0;
                } else {
                    ResponseBuilder.appendln("Ошибка при удалении объекта из БД");
                    execCode = 1;
                }
            } else throw new AccessPermissionException();
        } catch (NoSuchIDException e) {
            execCode = 1;
            ResponseBuilder.appendln("В коллекции нет элемента с указанным ID");
        } catch (NumberFormatException e) {
            execCode = 1;
            ResponseBuilder.appendln("Аргумент id - целое число");
        } catch (EmptyCollectionException e) {
            execCode = 0;
            ResponseBuilder.appendln("Ошибка: коллекция пуста!");
        } catch (AccessPermissionException e) {
            execCode = 1;
            ResponseBuilder.appendln("Нет доступа к объекту с указанным ID");
        }
        return execCode;
    }

    /**
     * Implements command clear. Clears the collection.
     *
     * @return 0 if command executed correctly;
     * 1 if an error occurred while executing
     */
    public int clear() {
        int execCode = 0;
        try {
            if (CollectionManager.isEmpty()) throw new EmptyCollectionException();
            if (dbManager.clearUserObjects(username) == 0 && dbManager.getCollectionFromDB()) {
                ResponseBuilder.appendln("Удалены все объекты пользователя " + username);
            } else {
                ResponseBuilder.appendln("Ошибка при удалении объектов пользователя " + username);
                execCode = 1;
            }
        } catch (EmptyCollectionException e) {
            ResponseBuilder.appendln("Коллекция пуста!");
        }
        return execCode;
    }

    /**
     * Implements command insert_at. Adds a new element at a given position.
     *
     * @param args Index at which to add the element
     * @return 0 if command executed correctly;
     * 1 if an error occurred while executing
     */
    public int insertAt(String args, Object objectArgs) {
        int execCode;
        try {
            int index = Integer.parseInt(args);
            if (index < 0 || index > CollectionManager.size()) throw new ArrayIndexOutOfBoundsException();
            LabWorkStatic lws = (LabWorkStatic) objectArgs;
            LabWork lw = new LabWork(lws);
            long newId = dbManager.addElement(lws, username);
            if (newId >= 0) {
                lw.setId(newId);
                CollectionManager.insertAt(lw, index);
                ResponseBuilder.appendln("Элемент успешно добавлен по индексу " + index);
                execCode = 0;
            } else {
                ResponseBuilder.appendln("Ошибка при добавлении объекта в БД");
                execCode = 1;
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            execCode = 1;
            ResponseBuilder.appendln("Индекс должен быть от 0 до " + CollectionManager.size());
        } catch (NumberFormatException e) {
            execCode = 1;
            ResponseBuilder.appendln("Аргумент index - целое число");
        } catch (Exception e) {
            execCode = 1;
        }
        return execCode;
    }

    /**
     * Implements command add_if_min. Adds a new element to the collection
     * if its value is less than the smallest element of this collection.
     *
     * @return 0 if command executed correctly;
     * 1 if an error occurred while executing
     */
    public int addIfMin(Object objectArgs) {
        int execCode;
        try {
            if (CollectionManager.isEmpty()) throw new EmptyCollectionException();
            LabWorkStatic lws = (LabWorkStatic) objectArgs;
            LabWork comparableLW = new LabWork(lws);
            boolean isMin = CollectionManager.getCollection().stream().noneMatch(lw -> comparableLW.compareTo(lw) >= 0);
            if (isMin) {
                long newId = dbManager.addElement(lws, username);
                if (newId >= 0) {
                    comparableLW.setId(newId);
                    CollectionManager.add(comparableLW);
                    ResponseBuilder.appendln("Лабораторная работа успешно добавлена в коллекцию!");
                    execCode = 0;
                } else {
                    ResponseBuilder.appendln("Ошибка при добавлении объекта в БД");
                    execCode = 1;
                }
            } else {
                ResponseBuilder.appendln("Значение элемента не является наименьшим, элемент не добавлен");
                execCode = 0;
            }
        } catch (EmptyCollectionException e) {
            execCode = 1;
            ResponseBuilder.appendln("Ошибка: коллекция пуста!");
        } catch (Exception e) {
            execCode = 1;
        }
        return execCode;
    }

    /**
     * Implements command remove_greater. Removes from the collection all elements greater than the given.
     *
     * @return 0 if command executed correctly;
     * 1 if an error occurred while executing
     */
    public int removeGreater(Object objectArgs) {
        int execCode;
        try {
            if (CollectionManager.isEmpty()) throw new EmptyCollectionException();
            LabWorkStatic lws = (LabWorkStatic) objectArgs;
            LabWork comparableLW = new LabWork(lws);
            Set<Long> oldIds = CollectionManager.getCollection().stream().
                    mapToLong(LabWork::getId).boxed().collect(Collectors.toSet());

            if (dbManager.deleteIfNameGreater(comparableLW.getName(), username) == 0) {
                if (!dbManager.getCollectionFromDB()) throw new SQLException();
            } else throw new SQLException();

            Set<Long> newIds = CollectionManager.getCollection().stream().
                    mapToLong(LabWork::getId).boxed().collect(Collectors.toSet());
            oldIds.removeAll(newIds);
            if (oldIds.size() > 0) {
                ResponseBuilder.appendln("Удалены элементы с id: " +
                        oldIds.stream().map(Object::toString).collect(Collectors.joining(", ")));
            } else {
                ResponseBuilder.appendln("Нет элементов, принадлежащих пользователю " + username + " и превышающих введенный");
            }
            execCode = 0;
        } catch (EmptyCollectionException e) {
            execCode = 0;
            ResponseBuilder.appendln("Ошибка: коллекция пуста!");
        } catch (SQLException e) {
            ResponseBuilder.appendln("Ошибка при удалении объекта из БД");
            execCode = 1;
        } catch (Exception e) {
            execCode = 1;
        }
        return execCode;
    }

    /**
     * Implements command average_of_minimal_point. Displays the average value
     * of the field minimalPoint for all elements of the collection.
     *
     * @return 0 if command executed correctly;
     * 1 if an error occurred while executing
     */
    public int averageOfMinimalPoint() {
        int execCode = 0;
        try {
            if (CollectionManager.isEmpty()) throw new EmptyCollectionException();
            double average = CollectionManager.getCollection().stream().mapToDouble(LabWork::getMinimalPoint)
                    .average().orElseThrow(EmptyCollectionException::new);
            ResponseBuilder.appendln("Среднее значение поля minimalPoint: " + average);
        } catch (EmptyCollectionException e) {
            ResponseBuilder.appendln("Ошибка: коллекция пуста!");
        }
        return execCode;
    }

    /**
     * Implements command min_by_id. Displays any object from the collection whose id field value is the minimum.
     *
     * @return 0 if command executed correctly;
     * 1 if an error occurred while executing
     */
    public int minById() {
        int execCode = 0;
        try {
            if (CollectionManager.isEmpty()) throw new EmptyCollectionException();
            LabWork minlw = CollectionManager.getCollection().stream().min(Comparator.comparingLong(LabWork::getId))
                    .orElseThrow(EmptyCollectionException::new);
            ResponseBuilder.appendln(minlw.toString());
        } catch (EmptyCollectionException e) {
            ResponseBuilder.appendln("Ошибка: коллекция пуста!");
        }
        return execCode;
    }

    /**
     * Implements command print_field_ascending_minimal_point. Displays the values of the
     * minimalPoint field of all elements in ascending order
     *
     * @return 0 if command executed correctly;
     * 1 if an error occurred while executing
     */
    public int printFAMinimalPoint() {
        int execCode = 0;
        try {
            if (CollectionManager.isEmpty()) throw new EmptyCollectionException();
            Vector<LabWork> tempStorage = CollectionManager.getCollection();
            tempStorage.sort(Comparator.comparingInt(LabWork::getMinimalPoint));
            CollectionManager.showMinPoint(tempStorage);
        } catch (EmptyCollectionException e) {
            ResponseBuilder.appendln("Ошибка: коллекция пуста!");
        }
        return execCode;
    }
}
