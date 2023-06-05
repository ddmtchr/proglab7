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
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

public class OldDBManager {
    private final DBConnector dbConnector;

    public OldDBManager(DBConnector dbConnector) {
        this.dbConnector = dbConnector;
    }

    public void initializeDB() {
        try (Connection connection = dbConnector.getConnection()) {
            String initDisciplines = "create table if not exists disciplines(\n" +
                    "    id bigserial primary key,\n" +
                    "    name text not null,\n" +
                    "    lecturehours bigint  not null,\n" +
                    "    practicehours integer not null,\n" +
                    "    selfstudyhours bigint  not null\n" +
                    ");";
            String initUsers = "create table if not exists users(\n" +
                    "    id bigserial primary key,\n" +
                    "    username text not null unique,\n" +
                    "    password text not null,\n" +
                    "    salt text not null\n" +
                    ");";
            String initLabworks = "create table if not exists labworks(\n" +
                    "    id bigserial primary key,\n" +
                    "    name text not null,\n" +
                    "    x integer not null check (x > -934),\n" +
                    "    y double precision not null check (y <= 946),\n" +
                    "    creationdate text not null,\n" +
                    "    minimalpoint integer not null check (minimalpoint > 0),\n" +
                    "    averagepoint  bigint not null check (averagepoint > 0),\n" +
                    "    difficulty text not null check (difficulty in ('VERY_EASY', 'NORMAL', 'IMPOSSIBLE', 'TERRIBLE')),\n" +
                    "    discipline_id bigint references disciplines(id),\n" +
                    "    user_id bigint not null references users(id)\n" +
                    ");";
            String initFunction = "create or replace function deleter() returns trigger\n" +
                    "    language plpgsql\n" +
                    "as\n" +
                    "$$\n" +
                    "begin\n" +
                    "    delete from disciplines\n" +
                    "    where disciplines.id in (select d.id from labworks l right outer join disciplines d on d.id = l.discipline_id\n" +
                    "                             where l.id is null);\n" +
                    "    return null;\n" +
                    "end;\n" +
                    "$$;";
            String initTrigger = "create or replace trigger delete_trigger\n" +
                    "    after update or delete\n" +
                    "    on labworks\n" +
                    "    for each row\n" +
                    "execute procedure deleter();";
            PreparedStatement initDisciplinesQuery = connection.prepareStatement(initDisciplines);
            PreparedStatement initUsersQuery = connection.prepareStatement(initUsers);
            PreparedStatement initLabworksQuery = connection.prepareStatement(initLabworks);
            PreparedStatement initFunctionQuery = connection.prepareStatement(initFunction);
            PreparedStatement initTriggerQuery = connection.prepareStatement(initTrigger);
            initDisciplinesQuery.execute();
            initUsersQuery.execute();
            initLabworksQuery.execute();
            initFunctionQuery.execute();
            initTriggerQuery.execute();
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
                if (lws.getDiscipline() != null) {
                    ResultSet disciplineExists = checkDisciplineExists(lws.getDiscipline());
                    if (disciplineExists.next()) {
                        long discId = disciplineExists.getLong("id");
                        PreparedStatement addExistingDiscLabQuery = addLabIfDisciplineExists(discId, lws, username);
                        addExistingDiscLabQuery.executeUpdate();
                        ResultSet generated = addExistingDiscLabQuery.getGeneratedKeys();
                        if (generated.next()) generatedLabWorkId = generated.getLong(1);
                        else generatedLabWorkId = -1;
                    } else {
                        PreparedStatement addNotExistingDiscQuery = addNotExistingDiscipline(lws.getDiscipline());
                        addNotExistingDiscQuery.executeUpdate();
                        ResultSet generatedIdSet = addNotExistingDiscQuery.getGeneratedKeys();
                        long generatedId = -1;
                        if (generatedIdSet.next()) generatedId = generatedIdSet.getLong(1);
                        PreparedStatement addLabWithNotExistingDiscQuery = addLabWithNotExistingDisc(generatedId, lws, username);
                        addLabWithNotExistingDiscQuery.executeUpdate();
                        ResultSet generated = addLabWithNotExistingDiscQuery.getGeneratedKeys();
                        if (generated.next()) generatedLabWorkId = generated.getLong(1);
                        else generatedLabWorkId = -1;
                    }
                } else {
                    PreparedStatement addLabWithNoDisciplineQuery = addLabWithNoDiscipline(lws, username);
                    addLabWithNoDisciplineQuery.executeUpdate();
                    ResultSet generated = addLabWithNoDisciplineQuery.getGeneratedKeys();
                    if (generated.next()) generatedLabWorkId = generated.getLong(1);
                    else generatedLabWorkId = -1;
                }
                CollectionManager.setLastSaveTime();
                return generatedLabWorkId;
            });
        } catch (SQLException e) {
            System.out.println("Ошибка работы с базой данных");
            e.printStackTrace();
            return -1;
        }

    }

    public boolean getCollectionFromDB() {
        try {
            return dbConnector.handleQuery((Connection connection) -> {
                CollectionManager.clear();
                Vector<LabWork> clientCollection = CollectionManager.getCollection();
                String getCollection = "select * from labworks l left outer join disciplines d on l.discipline_id = d.id " +
                        "join users u on l.user_id = u.id;";
                PreparedStatement getCollectionQuery = connection.prepareStatement(getCollection);
                ResultSet getResult = getCollectionQuery.executeQuery();
                while (getResult.next()) {
                    LabWork lw = extractLabWorkFromResult(getResult);
                    clientCollection.add(lw);
                }
                Set<Long> idSet = new HashSet<>();
                try {
                    for (LabWork lw : clientCollection) {
                        if (!LabWorkValidator.isValid(lw)) throw new IncorrectFieldInputException();
                        if (lw.getDiscipline() != null && lw.getDiscipline().getName().isBlank())
                            lw.setDiscipline(null);
                        idSet.add(lw.getId());
                    }
                    if (idSet.size() < clientCollection.size()) throw new IncorrectFieldInputException();
                    return true;
                } catch (IncorrectFieldInputException e) {
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
                if (lws.getDiscipline() != null) {
                    ResultSet disciplineExists = checkDisciplineExists(lws.getDiscipline());
                    if (disciplineExists.next()) {
                        long discId = disciplineExists.getLong("id");
                        PreparedStatement updateExistingDiscLabQuery = updateLabIfDisciplineExists(id, discId, lws);
                        updateExistingDiscLabQuery.executeUpdate();
                    } else {
                        PreparedStatement addNotExistingDiscQuery = addNotExistingDiscipline(lws.getDiscipline());
                        addNotExistingDiscQuery.executeUpdate();
                        ResultSet generatedIdSet = addNotExistingDiscQuery.getGeneratedKeys();
                        long generatedId = -1;
                        if (generatedIdSet.next()) generatedId = generatedIdSet.getLong(1);
                        PreparedStatement updateLabWithNotExistingDiscQuery = updateLabWithNotExistingDisc(id, generatedId, lws);
                        updateLabWithNotExistingDiscQuery.executeUpdate();
                    }
                } else {
                    PreparedStatement updateElementQuery = updateLabWithNoDiscipline(id, lws);
                    updateElementQuery.executeUpdate();
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
                String deleteLabWork = "delete from labworks where id = ?;";
                PreparedStatement deleteLabWorkQuery = connection.prepareStatement(deleteLabWork);
                deleteLabWorkQuery.setLong(1, id);
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
                String deleteIfNameGreater = "delete from labworks where name > ? AND user_id = (select id from users where username = ?);";
                PreparedStatement deleteIfNameGreaterQuery = connection.prepareStatement(deleteIfNameGreater);
                deleteIfNameGreaterQuery.setString(1, name);
                deleteIfNameGreaterQuery.setString(2, username);
                deleteIfNameGreaterQuery.executeUpdate();
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
        Long discId = (Long) result.getObject("discipline_id");

        Discipline discipline = discId == null ? null :
                new Discipline(result.getString(12),
                        result.getLong(13),
                        result.getInt(14),
                        result.getLong(15));

        String username = result.getString("username");
        return new LabWork(id, name, new Coordinates(x, y), creationDate, minimalPoint,
                averagePoint, difficulty, discipline, username);
    }

    private ResultSet checkDisciplineExists(Discipline discipline) throws SQLException {
        String checkDiscExists = "select id from disciplines where name = ? AND lecturehours = ?" +
                " AND practicehours = ? AND selfstudyhours = ?;";
        PreparedStatement checkDiscExistsQuery = dbConnector.getConnection().prepareStatement(checkDiscExists);
        checkDiscExistsQuery.setString(1, discipline.getName());
        checkDiscExistsQuery.setLong(2, discipline.getLectureHours());
        checkDiscExistsQuery.setInt(3, discipline.getPracticeHours());
        checkDiscExistsQuery.setLong(4, discipline.getSelfStudyHours());
        return checkDiscExistsQuery.executeQuery();
    }

    private PreparedStatement addLabWithNoDiscipline(LabWorkStatic lws, String username) throws SQLException {
        String addLabWithNoDiscipline = "insert into labworks values(default, ?, ?, ?, ?, ?, ?, ?, ?, (select id from users where username = ?));";
        PreparedStatement addLabWithNoDisciplineQuery = dbConnector.getConnection().prepareStatement(addLabWithNoDiscipline, Statement.RETURN_GENERATED_KEYS);
        fillLabWorkFields(addLabWithNoDisciplineQuery, lws, null, username);
        return addLabWithNoDisciplineQuery;
    }

    private PreparedStatement updateLabWithNoDiscipline(long id, LabWorkStatic lws) throws SQLException {
        String updateLabWithNoDiscipline = "update labworks set name = ?, x = ?, y = ?, " +
                "creationdate = ?, minimalpoint = ?, averagepoint = ?, difficulty = ?, " +
                "discipline_id = ? where id = ?;";
        PreparedStatement updateLabWithNoDisciplineQuery = dbConnector.getConnection().prepareStatement(updateLabWithNoDiscipline);
        fillLabWorkFields(updateLabWithNoDisciplineQuery, lws, null);
        updateLabWithNoDisciplineQuery.setLong(9, id);
        return updateLabWithNoDisciplineQuery;
    }


    private PreparedStatement addLabIfDisciplineExists(long discId, LabWorkStatic lws, String username) throws
            SQLException {
        String addExistingDiscLab = "insert into labworks values(default, ?, ?, ?, ?, ?, ?, ?, ?, (select id from users where username = ?));";
        PreparedStatement addExistingDiscLabQuery = dbConnector.getConnection().prepareStatement(addExistingDiscLab, Statement.RETURN_GENERATED_KEYS);
        fillLabWorkFields(addExistingDiscLabQuery, lws, discId, username);
        return addExistingDiscLabQuery;
    }

    private PreparedStatement updateLabIfDisciplineExists(long id, long discId, LabWorkStatic lws) throws
            SQLException {
        String updateLabIfDisciplineExists = "update labworks set name = ?, x = ?, y = ?, " +
                "creationdate = ?, minimalpoint = ?, averagepoint = ?, difficulty = ?, " +
                "discipline_id = ? where id = ?;";
        PreparedStatement updateLabIfDisciplineExistsQuery = dbConnector.getConnection().prepareStatement(updateLabIfDisciplineExists);
        fillLabWorkFields(updateLabIfDisciplineExistsQuery, lws, discId);
        updateLabIfDisciplineExistsQuery.setLong(9, id);
        return updateLabIfDisciplineExistsQuery;
    }

    private PreparedStatement addNotExistingDiscipline(Discipline discipline) throws SQLException {
        String addNotExistingDisc = "insert into disciplines values(default, ?, ?, ?, ?)";
        PreparedStatement addNotExistingDiscQuery = dbConnector.getConnection().prepareStatement(addNotExistingDisc, Statement.RETURN_GENERATED_KEYS);
        addNotExistingDiscQuery.setString(1, discipline.getName());
        addNotExistingDiscQuery.setLong(2, discipline.getLectureHours());
        addNotExistingDiscQuery.setInt(3, discipline.getPracticeHours());
        addNotExistingDiscQuery.setLong(4, discipline.getSelfStudyHours());
        return addNotExistingDiscQuery;
    }


    private PreparedStatement addLabWithNotExistingDisc(long generatedId, LabWorkStatic lws, String username) throws
            SQLException {
        String addLabWithNotExistingDisc = "insert into labworks values(default, ?, ?, ?, ?, ?, ?, ?, ?, (select id from users where username = ?));";
        PreparedStatement addLabWithNotExistingDiscQuery = dbConnector.getConnection().prepareStatement(addLabWithNotExistingDisc, Statement.RETURN_GENERATED_KEYS);
        fillLabWorkFields(addLabWithNotExistingDiscQuery, lws, generatedId, username);
        return addLabWithNotExistingDiscQuery;
    }

    private PreparedStatement updateLabWithNotExistingDisc(long id, long generatedId, LabWorkStatic lws) throws
            SQLException {
        String updateLabWithNotExistingDisc = "update labworks set name = ?, x = ?, y = ?, " +
                "creationdate = ?, minimalpoint = ?, averagepoint = ?, difficulty = ?, " +
                "discipline_id = ? where id = ?;";
        PreparedStatement updateLabWithNotExistingDiscQuery = dbConnector.getConnection().prepareStatement(updateLabWithNotExistingDisc);
        fillLabWorkFields(updateLabWithNotExistingDiscQuery, lws, generatedId);
        updateLabWithNotExistingDiscQuery.setLong(9, id);
        return updateLabWithNotExistingDiscQuery;
    }

    private void fillLabWorkFields(PreparedStatement ps, LabWorkStatic lws, Long discId, String username)
            throws SQLException {
        fillLabWorkFields(ps, lws, discId);
        ps.setString(9, username);
    }

    private void fillLabWorkFields(PreparedStatement ps, LabWorkStatic lws, Long discId)
            throws SQLException {
        ps.setString(1, lws.getName());
        ps.setInt(2, lws.getCoordinates().getX());
        ps.setFloat(3, lws.getCoordinates().getY());
        ps.setString(4, lws.getCreationDate().toString());
        ps.setInt(5, lws.getMinimalPoint());
        ps.setLong(6, lws.getAveragePoint());
        ps.setString(7, lws.getDifficulty().name());
        if (discId == null) ps.setObject(8, null);
        else ps.setLong(8, discId);
    }
}
