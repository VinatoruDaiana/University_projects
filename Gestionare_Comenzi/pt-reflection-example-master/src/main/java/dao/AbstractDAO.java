package dao;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import connection.ConnectionFactory;
import model.Client;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

/**
 * @Author: Technical University of Cluj-Napoca, Romania Distributed Systems
 *          Research Laboratory, http://dsrl.coned.utcluj.ro/
 * @Since: Apr 03, 2017
 * @Source http://www.java-blog.com/mapping-javaobjects-database-reflection-generics
 */
public class AbstractDAO<T> {
	protected static final Logger LOGGER = Logger.getLogger(AbstractDAO.class.getName());

	private final Class<T> type;

	@SuppressWarnings("unchecked")
	public AbstractDAO() {
		this.type = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];

	}



	private String createSelectQuery(String field) {
		StringBuilder sb = new StringBuilder();
		sb.append("SELECT ");
		sb.append(" * ");
		sb.append(" FROM ");
		sb.append(type.getSimpleName());
		sb.append(" WHERE " + field + " =?");
		return sb.toString();
	}

	public List<T> findAll() {

		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		String query = "SELECT * FROM " + type.getSimpleName();

		try {
			connection = ConnectionFactory.getConnection();
			statement = connection.prepareStatement(query);
			resultSet = statement.executeQuery();

			return createObjects(resultSet);

		} catch (SQLException e) {
			LOGGER.log(Level.WARNING, type.getName() + "DAO:findAll " + e.getMessage());
		} finally {
			ConnectionFactory.close(resultSet);
			ConnectionFactory.close(statement);
			ConnectionFactory.close(connection);
		}
		return null;
	}

	public T findById(int id) {
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		String query = createSelectQuery("id");
		try {
			connection = ConnectionFactory.getConnection();
			statement = connection.prepareStatement(query);
			statement.setInt(1, id);
			resultSet = statement.executeQuery();

			return createObjects(resultSet).get(0);
		} catch (SQLException e) {
			LOGGER.log(Level.WARNING, type.getName() + "DAO:findById " + e.getMessage());
		} finally {
			ConnectionFactory.close(resultSet);
			ConnectionFactory.close(statement);
			ConnectionFactory.close(connection);
		}
		return null;
	}

	private List<T> createObjects(ResultSet resultSet) {
		List<T> list = new ArrayList<T>();
		Constructor[] ctors = type.getDeclaredConstructors();
		Constructor ctor = null;
		for (int i = 0; i < ctors.length; i++) {
			ctor = ctors[i];
			if (ctor.getGenericParameterTypes().length == 0)
				break;
		}
		try {
			while (resultSet.next()) {
				ctor.setAccessible(true);
				T instance = (T)ctor.newInstance();
				for (Field field : type.getDeclaredFields()) {
					String fieldName = field.getName();
					Object value = resultSet.getObject(fieldName);
					PropertyDescriptor propertyDescriptor = new PropertyDescriptor(fieldName, type);
					Method method = propertyDescriptor.getWriteMethod();
					method.invoke(instance, value);
				}
				list.add(instance);
			}
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (IntrospectionException e) {
			e.printStackTrace();
		}
		return list;
	}

	private String createInsertQuery(Field[] fields) {

		StringBuilder sb = new StringBuilder();
		sb.append("INSERT INTO ").append(type.getSimpleName()).append(" (");

		for (Field field : fields) {
			sb.append(field.getName()).append(",");
		}
		sb.deleteCharAt(sb.lastIndexOf(","));
		sb.append(") VALUES (");


		for (Field field : fields) {
			sb.append("?,");
		}
		sb.deleteCharAt(sb.lastIndexOf(","));
		sb.append(")");
		return sb.toString();
	}

	public int insert(T t) {

		Connection connection = null;
		PreparedStatement insertStatement = null;
		ResultSet resultSet = null;
		String query = createInsertQuery(type.getDeclaredFields());
		int insertedId = -1;

		try {
			connection = ConnectionFactory.getConnection();
			insertStatement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);

			int index = 1;
			for (Field field : type.getDeclaredFields()) {
				field.setAccessible(true);

				Object value = field.get(t);
				insertStatement.setObject(index, value);
				index++;

				}

			insertStatement.executeUpdate();


		resultSet = insertStatement.getGeneratedKeys();

		if (resultSet.next()) {
			insertedId = resultSet.getInt(1);
			}


		} catch (SQLException | IllegalAccessException e) {
			LOGGER.log(Level.WARNING, "DAO:insert " + e.getMessage());
			return -1;
		} finally {
			ConnectionFactory.close(resultSet);
			ConnectionFactory.close(insertStatement);
			ConnectionFactory.close(connection);
		}
		return insertedId;
	}


	private String createDeleteQuery(String field) {
		return "DELETE FROM " + type.getSimpleName() + " WHERE " + field + " = ?";
	}

	public int delete(int id) {
		Connection connection = null;
		PreparedStatement statement = null;
		String query = createDeleteQuery("id");
		try {
			connection = ConnectionFactory.getConnection();
			statement = connection.prepareStatement(query);

			statement.setInt(1, id);

			//verific daca vreo coloana a fost stearsa din tabel
			int coloanaStearsa = statement.executeUpdate();
			if (coloanaStearsa > 0)
				return 0;
			else
				return -1;


		} catch (SQLException e) {
			LOGGER.log(Level.WARNING, type.getName() + " DAO:Delete " + e.getMessage());
			return -1;
		} finally {
			ConnectionFactory.close(statement);
			ConnectionFactory.close(connection);
		}
	}

	private String createUpdateQuery() {
		StringBuilder sb = new StringBuilder();
		sb.append("UPDATE ").append(type.getSimpleName()).append(" SET ");


		for (Field field : type.getDeclaredFields()) {

			sb.append(field.getName()).append(" = ").append("?,");
		}
		sb.deleteCharAt(sb.lastIndexOf(","));
		sb.append(" WHERE id = ?");
		return sb.toString();
	}

	public int update(T t) {

		Connection connection = null;
		PreparedStatement statement = null;
		String query = createUpdateQuery();

		try {
			connection = ConnectionFactory.getConnection();
			statement = connection.prepareStatement(query);

			int index = 1;
			Field idField = null;

			for (Field field : type.getDeclaredFields()) {
				field.setAccessible(true);

				if (field.getName().equalsIgnoreCase("id")) {
					idField = field;
				} else {
					statement.setObject(index++, field.get(t));
				}
			}

			if (idField != null) {

				idField.setAccessible(true);
				statement.setObject(index, idField.get(t));
			}

			statement.executeUpdate();
			return 0;

		} catch (SQLException | IllegalAccessException sqlException) {
			LOGGER.log(Level.WARNING, type.getName() + "DAO:edit " + sqlException.getMessage());
			return -1;
		} finally {
			ConnectionFactory.close(statement);
			ConnectionFactory.close(connection);
		}


	}


	public void generateTable(JTable table, List<T> tList) {

		DefaultTableModel tableModel = new DefaultTableModel();
		Field[] fieldList = type.getDeclaredFields();


		for (Field field : fieldList) {
			tableModel.addColumn(field.getName());
		}

		for (T t : tList) {
			Object[] rowData = new Object[fieldList.length];

			for (int i = 0; i < fieldList.length; i++) {

				try {
					Field field = fieldList[i];
					field.setAccessible(true);
					Object value = field.get(t);
					rowData[i] = value;

				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			}
			tableModel.addRow(rowData);
		}
		table.setModel(tableModel);
	}
}
