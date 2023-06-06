package database;

import exceptions.IncorrectFieldInputException;
import org.postgresql.util.PSQLException;
import processing.CollectionManager;
import server.Server;
import stored.Coordinates;
import stored.Difficulty;
import stored.Discipline;
import stored.LabWork;
import utility.LabWorkStatic;
import utility.ResponseBuilder;
import validation.LabWorkValidator;

import java.sql.*;
import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

public class DBManager {
    private final DBConnector dbConnector;

    public DBManager(DBConnector dbConnector) {
        this.dbConnector = dbConnector;
    }

    public void initializeDB() {
        try (Connection connection = dbConnector.getConnection()) {
            String initUsers = "create table if not exists users(\n" +
                    "    id bigserial primary key,\n" +
                    "    username text not null unique,\n" +
                    "    password text not null,\n" +
                    "    salt text not null\n" +
                    ");";
            String initLabWorks = "create table if not exists labworks(\n" +
                    " id bigserial primary key,\n" +
                    " name text not null,\n" +
                    " x integer not null check (x > -934),\n" +
                    " y double precision not null check (y <= 946),\n" +
                    " creationdate text not null,\n" +
                    " minimalpoint integer not null check (minimalpoint > 0),\n" +
                    " averagepoint  bigint not null check (averagepoint > 0),\n" +
                    " difficulty text not null check (difficulty in ('VERY_EASY', 'NORMAL', 'IMPOSSIBLE', 'TERRIBLE')),\n" +
                    " user_id bigint not null references users(id)\n" +
                    " );";
            String initDisciplines = "create table if not exists disciplines(\n" +
                    " id bigserial primary key,\n" +
                    " name text not null,\n" +
                    " lecturehours bigint  not null,\n" +
                    " practicehours integer not null,\n" +
                    " selfstudyhours bigint  not null,\n" +
                    " labwork_id bigint not null references labworks(id) on delete cascade\n" +
                    " );";
            PreparedStatement initUsersQuery = connection.prepareStatement(initUsers);
            PreparedStatement initLabWorksQuery = connection.prepareStatement(initLabWorks);
            PreparedStatement initDisciplinesQuery = connection.prepareStatement(initDisciplines);
            initUsersQuery.execute();
            initLabWorksQuery.execute();
            initDisciplinesQuery.execute();
        } catch (SQLException e) {
            System.out.println("Ошибка инициализации базы данных!");
            Server.shutdown();
        }
    }

    public int registerUser(String login, String password, String salt) {
        try {
            return dbConnector.handleQuery((Connection connection) -> {
                int execCode;
                String registerQuery = "insert into users values" +
                        "(default, ?, ?, ?);";
                try {
                    PreparedStatement s = connection.prepareStatement(registerQuery);
                    s.setString(1, login);
                    s.setString(2, password);
                    s.setString(3, salt);
                    s.executeUpdate();
                    ResponseBuilder.appendln("Вы зарегистрированы как " + login);
                    execCode = 0;
                } catch (PSQLException e) {
                    ResponseBuilder.appendln("Это имя пользователя уже зарегистрировано!");
                    execCode = 1;
                }
                return execCode;
            });
        } catch (SQLException e) {
            System.out.println("Ошибка при работе с базой данных");
            Server.shutdown();
            return 1;
        }
    }

    public int loginUser(String login, String password) {
        try {
            return dbConnector.handleQuery((Connection connection) -> {
                int execCode;
                if (validateUser(login, password)) {
                    ResponseBuilder.appendln("Вы вошли как " + login);
                    execCode = 0;
                } else {
                    ResponseBuilder.appendln("Неверное имя пользователя или пароль");
                    execCode = 1;
                }
                return execCode;
            });
        } catch (SQLException e) {
            System.out.println("Ошибка при работе с базой данных");
            Server.shutdown();
            return 1;
        }
    }

    public int getSaltByLogin(String login) {
        try {
            return dbConnector.handleQuery((Connection connection) -> {
                int execCode;
                String getSaltQuery = "select salt from users where username = ?;";
                PreparedStatement s = connection.prepareStatement(getSaltQuery);
                s.setString(1, login);
                ResultSet resultSet = s.executeQuery();
                if (resultSet.next()) {
                    ResponseBuilder.append(resultSet.getString("salt"));
                    execCode = 0;
                } else {
                    ResponseBuilder.appendln("Неверное имя пользователя или пароль");
                    execCode = 1;
                }
                return execCode;
            });
        } catch (SQLException e) {
            System.out.println("Ошибка при работе с базой данных");
            e.printStackTrace();
            return 1;
        }
    }

    public long addElement(LabWorkStatic lws, String username) {
        try {
            return dbConnector.handleQuery((Connection connection) -> {
                long generatedLabWorkId;
                PreparedStatement addLabWorkQuery = addLabWork(lws, username);
                addLabWorkQuery.executeUpdate();
                ResultSet generated = addLabWorkQuery.getGeneratedKeys();
                if (generated.next()) generatedLabWorkId = generated.getLong(1);
                else return -1L;
                if (lws.getDiscipline() != null) {
                    PreparedStatement addDisciplineQuery = addDiscipline(lws.getDiscipline(), generatedLabWorkId);
                    addDisciplineQuery.executeUpdate();
                }
                CollectionManager.setLastSaveTime();
                return generatedLabWorkId;
            });
        } catch (SQLException e) {
            System.out.println("Ошибка работы с базой данных");
            e.printStackTrace();
            return -1L;
        }

    }

    public boolean getCollectionFromDB() {
        try {
            return dbConnector.handleQuery((Connection connection) -> {
                CollectionManager.clear();
                Vector<LabWork> clientCollection = CollectionManager.getCollection();
                String getCollection = "select * from labworks l left join disciplines d on l.id = d.labwork_id " +
                        "join users u on l.user_id = u.id;";
                PreparedStatement getCollectionQuery = connection.prepareStatement(getCollection);
                ResultSet getResult = getCollectionQuery.executeQuery();
                try {
                while (getResult.next()) {
                    LabWork lw = extractLabWorkFromResult(getResult);
                    clientCollection.add(lw);
                }
                Set<Long> idSet = new HashSet<>();
                    for (LabWork lw : clientCollection) {
                        if (!LabWorkValidator.isValid(lw)) throw new IncorrectFieldInputException();
                        if (lw.getDiscipline() != null && lw.getDiscipline().getName().isBlank())
                            lw.setDiscipline(null);
                        idSet.add(lw.getId());
                    }
                    if (idSet.size() < clientCollection.size()) throw new IncorrectFieldInputException();
                    return true;
                } catch (DateTimeParseException | IncorrectFieldInputException e) {
                    System.out.println("В датабазе какая-то дичь. Уберите.");
                    Server.shutdown();
                    return false;
                }
            });
        } catch (SQLException e) {
            System.out.println("Ошибка работы с базой данных");
            e.printStackTrace();
            return false;
        }
    }

    public int updateElement(long id, LabWorkStatic lws) {
        try {
            return dbConnector.handleQuery((Connection connection) -> {
                PreparedStatement updateLabWorkQuery = updateLabWork(id, lws);
                updateLabWorkQuery.executeUpdate();
                if (CollectionManager.getElementById(id).getDiscipline() != null) {
                    if (lws.getDiscipline() != null) {
                        PreparedStatement updateDisciplineQuery = updateDiscipline(lws.getDiscipline(), id);
                        updateDisciplineQuery.executeUpdate();
                    } else {
                        PreparedStatement deleteDisciplineQuery = deleteDiscipline(id);
                        deleteDisciplineQuery.executeUpdate();
                    }
                } else {
                    if (lws.getDiscipline() != null) {
                        PreparedStatement addDisciplineQuery = addDiscipline(lws.getDiscipline(), id);
                        addDisciplineQuery.executeUpdate();
                    }
                }
                CollectionManager.setLastSaveTime();
                return 0;
            });
        } catch (SQLException e) {
            System.out.println("Ащибка работы с БД");
            return 1;
        }
    }

    public int deleteElement(long id) {
        try {
            return dbConnector.handleQuery((Connection connection) -> {
                PreparedStatement deleteLabWorkQuery = deleteLabWork(id);
                deleteLabWorkQuery.executeUpdate();
                CollectionManager.setLastSaveTime();
                return 0;
            });
        } catch (SQLException e) {
            System.out.println("Ошыбко работы с БД");
            return 1;
        }
    }

    public int deleteIfNameGreater(String name, String username) {
        try {
            return dbConnector.handleQuery((Connection connection) -> {
                PreparedStatement deleteLabWorkIfNameGreater = deleteLabWorkIfNameGreater(name, username);
                deleteLabWorkIfNameGreater.executeUpdate();
                CollectionManager.setLastSaveTime();
                return 0;
            });
        } catch (SQLException e) {
            System.out.println("Ошыбко работы с БД");
            return 1;
        }
    }

    public int clearUserObjects(String username) {
        try {
            return dbConnector.handleQuery((Connection connection) -> {
                String clearUser = "delete from labworks where user_id = (select id from users where username = ?);";
                PreparedStatement clearUserQuery = connection.prepareStatement(clearUser);
                clearUserQuery.setString(1, username);
                clearUserQuery.executeUpdate();
                CollectionManager.setLastSaveTime();
                return 0;
            });
        } catch (SQLException e) {
            System.out.println("Произошел несчастный случай");
            return 1;
        }
    }

    public boolean validateUser(String username, String password) {
        try {
            return dbConnector.handleQuery((Connection connection) -> {
                String searchUser = "select username from users where username = ? AND password = ?;";
                PreparedStatement searchUserQuery = connection.prepareStatement(searchUser);
                searchUserQuery.setString(1, username);
                searchUserQuery.setString(2, password);
                ResultSet resultSet = searchUserQuery.executeQuery();
                return resultSet.next();
            });
        } catch (SQLException e) {
            System.out.println("Ошибка валидации пользователя");
            return false;
        }
    }

    private LabWork extractLabWorkFromResult(ResultSet result) throws SQLException {
        long id = result.getLong("id");
        String name = result.getString("name");
        int x = result.getInt("x");
        float y = result.getFloat("y");
        ZonedDateTime creationDate = ZonedDateTime.parse(result.getString("creationdate"));
        int minimalPoint = result.getInt("minimalpoint");
        long averagePoint = result.getLong("averagepoint");
        Difficulty difficulty = Difficulty.valueOf(result.getString("difficulty"));
        Long discId = (Long) result.getObject(10);

        Discipline discipline = discId == null ? null :
                new Discipline(result.getString(11),
                        result.getLong(12),
                        result.getInt(13),
                        result.getLong(14));

        String username = result.getString("username");
        return new LabWork(id, name, new Coordinates(x, y), creationDate, minimalPoint,
                averagePoint, difficulty, discipline, username);
    }

    private PreparedStatement addLabWork(LabWorkStatic lws, String username) throws SQLException {
        String addLabWork = "insert into labworks values(default, ?, ?, ?, ?, ?, ?, ?, (select id from users where username = ?));";
        PreparedStatement addLabWorkQuery = dbConnector.getConnection().prepareStatement(addLabWork, Statement.RETURN_GENERATED_KEYS);
        fillLabWorkFields(addLabWorkQuery, lws, username);
        return addLabWorkQuery;
    }

    private PreparedStatement updateLabWork(long id, LabWorkStatic lws) throws SQLException {
        String updateLabWork = "update labworks set name = ?, x = ?, y = ?, " +
                "creationdate = ?, minimalpoint = ?, averagepoint = ?, difficulty = ? " +
                "where id = ?;";
        PreparedStatement updateLabWorkQuery = dbConnector.getConnection().prepareStatement(updateLabWork);
        fillLabWorkFields(updateLabWorkQuery, lws);
        updateLabWorkQuery.setLong(8, id);
        return updateLabWorkQuery;
    }

    private PreparedStatement updateDiscipline(Discipline discipline, long labWorkId) throws SQLException {
        String updateDiscipline = "update disciplines set name = ?, lecturehours = ?, " +
                "practicehours = ?, selfstudyhours = ? where labwork_id = ?";
        PreparedStatement updateDisciplineQuery = dbConnector.getConnection().prepareStatement(updateDiscipline);
        updateDisciplineQuery.setString(1, discipline.getName());
        updateDisciplineQuery.setLong(2, discipline.getLectureHours());
        updateDisciplineQuery.setInt(3, discipline.getPracticeHours());
        updateDisciplineQuery.setLong(4, discipline.getSelfStudyHours());
        updateDisciplineQuery.setLong(5, labWorkId);
        return updateDisciplineQuery;
    }

    private PreparedStatement deleteDiscipline(long labWorkId) throws SQLException {
        String deleteDiscipline = "delete from disciplines where labwork_id = ?";
        PreparedStatement deleteDisciplineQuery = dbConnector.getConnection().prepareStatement(deleteDiscipline);
        deleteDisciplineQuery.setLong(1, labWorkId);
        return deleteDisciplineQuery;
    }

    private PreparedStatement deleteLabWork(long id) throws SQLException {
        String deleteLabWork = "delete from labworks where id = ?;";
        PreparedStatement deleteLabWorkQuery = dbConnector.getConnection().prepareStatement(deleteLabWork);
        deleteLabWorkQuery.setLong(1, id);
        return deleteLabWorkQuery;
    }

    private PreparedStatement deleteLabWorkIfNameGreater(String name, String username) throws SQLException {
        String deleteLabWorkIfNameGreater = "delete from labworks where name > ? and user_id = (select id from users where username = ?);";
        PreparedStatement deleteLabWorkIfNameGreaterQuery = dbConnector.getConnection().prepareStatement(deleteLabWorkIfNameGreater);
        deleteLabWorkIfNameGreaterQuery.setString(1, name);
        deleteLabWorkIfNameGreaterQuery.setString(2, username);
        return deleteLabWorkIfNameGreaterQuery;
    }

    private PreparedStatement addDiscipline(Discipline discipline, long labWorkId) throws SQLException {
        String addDiscipline = "insert into disciplines values(default, ?, ?, ?, ?, ?);";
        PreparedStatement addDisciplineQuery = dbConnector.getConnection().prepareStatement(addDiscipline);
        addDisciplineQuery.setString(1, discipline.getName());
        addDisciplineQuery.setLong(2, discipline.getLectureHours());
        addDisciplineQuery.setInt(3, discipline.getPracticeHours());
        addDisciplineQuery.setLong(4, discipline.getSelfStudyHours());
        addDisciplineQuery.setLong(5, labWorkId);
        return addDisciplineQuery;
    }

    private void fillLabWorkFields(PreparedStatement ps, LabWorkStatic lws, String username)
            throws SQLException {
        fillLabWorkFields(ps, lws);
        ps.setString(8, username);
    }

    private void fillLabWorkFields(PreparedStatement ps, LabWorkStatic lws)
            throws SQLException {
        ps.setString(1, lws.getName());
        ps.setInt(2, lws.getCoordinates().getX());
        ps.setFloat(3, lws.getCoordinates().getY());
        ps.setString(4, lws.getCreationDate().toString());
        ps.setInt(5, lws.getMinimalPoint());
        ps.setLong(6, lws.getAveragePoint());
        ps.setString(7, lws.getDifficulty().name());
    }
}
