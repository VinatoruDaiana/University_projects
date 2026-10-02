package Model.Repository;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AbstractRepository<T> {
    protected static final Logger LOGGER = Logger.getLogger(AbstractRepository.class.getName());
    private final Class<T> type;

    @SuppressWarnings("unchecked")
    public AbstractRepository() {
        this.type = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }

    private String createSelectQuery(String field) {
        String table = type.getSimpleName().toLowerCase();

        if (type.getSimpleName().equals("Parfum")) {
            return "SELECT id_parfum, nume, producator, descriere FROM parfum"
                    + (field.equals("*") ? "" : " WHERE " + field + " = ?");
        }

        if (type.getSimpleName().equals("Parfumerie")) {
            return "SELECT id_parfumerie, nume, telefon, adresa FROM parfumerie"
                    + (field.equals("*") ? "" : " WHERE " + field + " = ?");
        }

        if (type.getSimpleName().equals("Stoc")) {
            return "SELECT id_stoc, id_parfum, id_parfumerie, cantitate, disponibilitate FROM stoc"
                    + (field.equals("*") ? "" : " WHERE " + field + " = ?");
        }

        return "SELECT * FROM " + table + (field.equals("*") ? "" : " WHERE " + field + " = ?");
    }



    public T findById(int id) {
        String query = createSelectQuery(type.getSimpleName().toLowerCase() + "_id");
        try (Connection connection = SqlConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                List<T> objects = createObjects(resultSet);
                return objects.isEmpty() ? null : objects.get(0);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, type.getName() + "Repository:findById " + e.getMessage());
            return null;
        }
    }

    public List<T> getTableContent() {
        String query = createSelectQuery("*");
        try (Connection connection = SqlConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {
            return createObjects(resultSet);
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, type.getName() + "Repository:getTableContent " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public int insert(T object) {
        StringBuilder fields = new StringBuilder("INSERT INTO ").append(type.getSimpleName().toLowerCase()).append(" (");
        StringBuilder values = new StringBuilder("VALUES (");
        Field[] fieldArray = type.getDeclaredFields();
        List<Object> params = new ArrayList<>();

        for (int i = 1; i < fieldArray.length; i++) {
            fieldArray[i].setAccessible(true);
            if (i > 1) {
                fields.append(", ");
                values.append(", ");
            }
            fields.append(fieldArray[i].getName());
            values.append("?");
            try {
                params.add(fieldArray[i].get(object));
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        fields.append(") ");
        values.append(")");
        String query = fields + values.toString();

        try (Connection connection = SqlConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            setStatementParameters(statement, params);
            statement.executeUpdate();
            try (ResultSet rs = statement.getGeneratedKeys()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Error executing insert: " + e.getMessage());
            return 0;
        }
    }

    public int update(T object) {
        StringBuilder query = new StringBuilder("UPDATE ").append(type.getSimpleName().toLowerCase()).append(" SET ");
        Field[] fields = type.getDeclaredFields();
        List<Object> params = new ArrayList<>();

        for (int i = 1; i < fields.length; i++) {
            fields[i].setAccessible(true);
            if (i > 1) query.append(", ");
            query.append(fields[i].getName()).append(" = ?");
            try {
                params.add(fields[i].get(object));
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        query.append(" WHERE ").append(fields[0].getName()).append(" = ?");
        try {
            fields[0].setAccessible(true);
            params.add(fields[0].get(object));
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        try (Connection connection = SqlConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query.toString())) {
            setStatementParameters(statement, params);
            return statement.executeUpdate();
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Error executing update: " + e.getMessage());
            return 0;
        }
    }

    public int deleteById(int id) {
        Field primaryKeyField = type.getDeclaredFields()[0];
        String query = "DELETE FROM " + type.getSimpleName().toLowerCase() + " WHERE " + primaryKeyField.getName() + " = ?";

        try (Connection connection = SqlConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, id);
            return statement.executeUpdate();
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Error executing deleteById: " + e.getMessage());
            return 0;
        }
    }

    private List<T> createObjects(ResultSet resultSet) {
        List<T> list = new ArrayList<>();
        try {
            Constructor<T> ctor = type.getDeclaredConstructor();
            ctor.setAccessible(true);
            while (resultSet.next()) {
                T instance = ctor.newInstance();
                for (Field field : type.getDeclaredFields()) {
                    field.setAccessible(true);
                    Object value = resultSet.getObject(field.getName());
                    PropertyDescriptor propertyDescriptor = new PropertyDescriptor(field.getName(), type);
                    Method method = propertyDescriptor.getWriteMethod();
                    method.invoke(instance, value);
                }
                list.add(instance);
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Error creating objects: " + e.getMessage());
        }
        return list;
    }

    private void setStatementParameters(PreparedStatement statement, List<Object> params) throws SQLException {
        for (int i = 0; i < params.size(); i++) {
            statement.setObject(i + 1, params.get(i));
        }
    }
}