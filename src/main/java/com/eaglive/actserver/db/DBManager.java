package com.eaglive.actserver.db;

import com.eaglive.actserver.config.ConfigData;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;

public class DBManager {

	private static final Logger logger = LoggerFactory.getLogger(DBManager.class);
	
	private final static int TYPE_INT = 0;
	private final static int TYPE_DATE = 1;
	private final static int TYPE_STRING = 2;
	private final static int TYPE_BOOL = 3;
	private final static int TYPE_LONG = 4;
	private final static int TYPE_FLOAT = 5;
	
	private final static int BATCH_MAX_PARAM_LOG_COUNT = 5;
	
	private  static DBManager _instance;
	
	private final ComboPooledDataSource pool;
	
	public static DBManager instance(){
		
		if(_instance == null){
			_instance = new DBManager();
		}
		return _instance;
	}
	
	public DBManager(){
		pool = createConnectionPool();
	}
	public Connection getConnection(){
		try {
			return pool.getConnection();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
    public static void releaseDbResource(Statement stmt, Connection conn)
    {
        releaseDbResource(null, stmt, conn);
    }
	
    public static void releaseDbResource(ResultSet rs, Statement stmt, Connection conn){
        try{
            if(rs != null){
                rs.close();
                rs = null;
            }
        }
        catch(SQLException ex){
        	ex.printStackTrace();
        }
        try{
            if(stmt != null){
                stmt.close();
                stmt = null;
            }
        }
        catch(SQLException ex){
        	ex.printStackTrace();
        }
        try{
            if(conn != null){
                conn.close();
                conn = null;
            }
        }catch(SQLException ex){
        	ex.printStackTrace();
        }
    }
    
    public boolean isRecordExist(String cmd)
    {
        return isRecordExist(cmd, null);
    }

    public boolean isRecordExist(String cmd, Object[] params){
        boolean result;
        Connection conn;
        PreparedStatement stmt;
        ResultSet rs;
        result = false;
        conn = null;
        stmt = null;
        rs = null;
        try{
            conn = this.getConnection();
            stmt = prepareStatement(conn, cmd, params);
            rs = stmt.executeQuery();
            if(rs.next())
                result = true;
            else
                result = false;
        }catch(Exception e){
        	logQueryCmdError("isRecordExist", cmd, params);
        }finally{
        	releaseDbResource(rs, stmt, conn);	
        }
        return result;
    }

	public <T> T executeScalarObject(String cmd, ResultObjectBuilder<T> builder) {
		return executeScalarObject(cmd, null, builder);
	}
    
	public <T> T executeScalarObject(String cmd, Object params[], ResultObjectBuilder<T> builder){
    	ArrayList<T> list = executeQuery_ObjectList(cmd, params, builder);
        return list == null || list.size() <= 0 ? null : list.get(0);
    }
    
    public <T> ArrayList<T> executeQuery_ObjectList(String cmd, ResultObjectBuilder<T> builder){
    	return executeQuery_ObjectList(cmd,null,builder);
    }
    
	public Integer executeScalarInt(String cmd) {
		return this.executeScalarInt(cmd, null);
	}

	public Integer executeScalarInt(String cmd, Object[] params) {
		return (Integer) this.executeScalar(cmd, params, TYPE_INT);
	}

	public String executeScalarString(String cmd) {
		return executeScalarString(cmd, null);
	}

	public String executeScalarString(String cmd, Object[] params) {
		return (String) this.executeScalar(cmd, params, TYPE_STRING);
	}

	public Date executeScalarDate(String cmd) {
		return executeScalarDate(cmd, null);
	}

	public Date executeScalarDate(String cmd, Object[] params) {
		return (Date) this.executeScalar(cmd, params, TYPE_DATE);
	}

	public Boolean executeScalarBool(String cmd) {
		return executeScalarBool(cmd, null);
	}

	public Boolean executeScalarBool(String cmd, Object[] params) {
		return (Boolean) this.executeScalar(cmd, params, TYPE_BOOL);
	}

	public Long executeScalarLong(String cmd) {
		return executeScalarLong(cmd, null);
	}

	public Long executeScalarLong(String cmd, Object[] params) {
		return (Long) this.executeScalar(cmd, params, TYPE_LONG);
	}
	
	public Float executeScalarFloat(String cmd, Object[] params) {
		return (Float) this.executeScalar(cmd, params, TYPE_FLOAT);
	}
    
	private Object executeScalar(String cmd, Object[] params, int type) {
		Object result = null;
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			conn = this.getConnection();
			stmt = this.prepareStatement(conn, cmd, params);
			rs = stmt.executeQuery();
			if (rs.next()) {
				switch (type) {
				case TYPE_INT:
					int intVal = rs.getInt(1);
					result = rs.wasNull() ? null : Integer.valueOf(intVal);
					break;
				case TYPE_LONG:
					long longVal = rs.getLong(1);
					result = rs.wasNull() ? null : Long.valueOf(longVal);
					break;
				case TYPE_FLOAT:
					float floatVal = rs.getFloat(1);
					result = rs.wasNull() ? null : Float.valueOf(floatVal);
					break;					
				case TYPE_STRING:
					result = rs.getString(1);
					break;
				case TYPE_DATE:
					Timestamp tsVal = rs.getTimestamp(1);
					result = rs.wasNull() ? null : new Date(tsVal.getTime());
					break;
				case TYPE_BOOL:
					Boolean blVal = rs.getBoolean(1);
					result = rs.wasNull() ? null : blVal;
					break;
				}
			}
		} catch (Exception e) {
			logQueryCmdError("executeScalar", cmd, params);
		} finally {
			releaseDbResource(rs, stmt, conn);
		}
		return result;
	}
    
    
	public <T> ArrayList<T> executeQuery_ObjectList(String cmd, Object params[], ResultObjectBuilder<T> builder){
        Connection conn;
        PreparedStatement stmt;
        ResultSet rs;
        ArrayList<T> objectList;
        conn = null;
        stmt = null;
        rs = null;
        objectList = null;
        try{
            conn = this.getConnection();
            stmt = prepareStatement(conn, cmd, params);
            rs = stmt.executeQuery();
            objectList = getObjectListFromResultSet(rs, builder);
        }
        catch(Exception e){
        	logQueryCmdError("executeQuery_ObjectList", cmd, params);
        }finally{
        	releaseDbResource(rs, stmt, conn);	
        }
        return objectList;
    }
    
    public int executeCommand(String cmd, Object params[]){
    	return executeCommand(cmd, params, null); 
    }
    
    public int executeCommand(String cmd){
    	return executeCommand(cmd, null, null);
    }
    
    public int executeInsertCommand(String cmd, Object params[]){
        Connection conn;
        int newId;
        conn = null;
        newId = -1;
        try{
            conn = getConnection();
            int result = executeCommand(cmd, params, conn);
            if(result > 0){
            	newId = getLastInsertId(conn);
            }
        }
        catch(Exception e){
        	logQueryCmdError("executeInsertCommand", cmd, params);
        	return newId;
        }finally{
        	releaseDbResource(null, conn);
        }
        return newId;
    }
    
	public int getLastInsertId(Connection conn) {
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			stmt = conn.prepareStatement("SELECT last_insert_id() AS Id");
			rs = stmt.executeQuery();
			if (rs.next()) {
				return rs.getInt("Id");
			}
			return -1;
		} catch (Exception er) {
			logQueryCmdError("getLastInsertId", "SELECT last_insert_id() AS Id", null);
			return -1;
		} finally {
			releaseDbResource(rs, stmt, null);
		}
	}
	
	public ArrayList<DataRow> executeQuery(String cmd, int dataRowType){
		return executeQuery(cmd, null, dataRowType);
	}
	
	public ArrayList<DataRow> executeQuery(String cmd, Object[] params, int dataRowType) {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		ArrayList<DataRow> dataRowList = null;
		try {
			conn = this.getConnection();
			stmt = this.prepareStatement(conn, cmd, params);
			rs = stmt.executeQuery();
			dataRowList = getDataRowListFromResultSet(rs, dataRowType);
		} catch (Exception e) {
			logQueryCmdError("executeQuery", cmd, params);
		} finally {
			releaseDbResource(rs, stmt, conn);
		}
		return dataRowList;
	}

    /**
     * 如果不传connection自动释放连接，如果传了，要自己手动释放
     * **/
    public int executeCommand(String cmd, Object params[], Connection tranConn){
        Connection conn;
        PreparedStatement stmt;
        int rowCount;
        conn = null;
        stmt = null;
        rowCount = -1;
        try{
            if(tranConn != null)
                conn = tranConn;
            else
                conn = getConnection();
            stmt = prepareStatement(conn, cmd, params);
            rowCount = stmt.executeUpdate();
        }
        catch(Exception e){
        	e.printStackTrace();
        	logQueryCmdError("executeCommand", cmd, params);
        }finally{
            if(tranConn == null){
            	releaseDbResource(null, stmt, conn);
            }else{
            	releaseDbResource(null, stmt, null);
            }
        }
        return rowCount;
    }
    
    
    /**
	 * 起事务执行批处理更新操作(增、删、改)
	 * 批处理出错时必定回滚,如insert主键冲突,参数数目或格式不对等
	 * @return 出错返回null, 成功返回包含批中每个命令的一个元素的更新计数所组成的数组。数组的元素根据将命令添加到批中的顺序排序。 
	 */
	public int[] executeBatchCommand(String cmd, ArrayList<Object[]> paramsList) {
		return this.executeBatchCommand(cmd, paramsList, null);
	}
	/**
	 * 起事务执行批处理更新操作(增、删、改)
	 * @return 出错返回null, 成功返回包含批中每个命令的一个元素的更新计数所组成的数组。数组的元素根据将命令添加到批中的顺序排序。
	 */
	public int[] executeBatchCommand(String cmd, ArrayList<Object[]> paramsList, Connection tranConn) {
		
		Connection conn = null;
		PreparedStatement stmt = null;
		int[] rowCounts = null;
		try {
			if (tranConn != null) {
				conn = tranConn;
			} else {
				conn = getConnection();
				conn.setAutoCommit(false);
			}

			stmt = this.prepareBatchStatement(conn, cmd, paramsList);
			rowCounts = stmt.executeBatch();
			if (tranConn == null) {
				conn.commit();
			}
		} catch (Exception e) {
			logBatchCmdError("executeBatchCommand", cmd, paramsList);
			try{
				conn.rollback();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}finally{
			if (tranConn == null) {
				releaseDbResource(null, stmt, conn);
			} else {
				releaseDbResource(null, stmt, null);
			}
		}
		return rowCounts;
	}
	/**
	 * 返回一个批处理命令的预处理语句
	 */
	public PreparedStatement prepareBatchStatement(Connection conn, String cmd,
			ArrayList<Object[]> paramsList) throws SQLException {
		PreparedStatement stmt = conn.prepareStatement(cmd);
		if (paramsList != null && paramsList.size() != 0) {
			for(Object[] params:paramsList){
				setParams(stmt, params);
				stmt.addBatch();
			}
		}
		return stmt;
	}

    
    
    public PreparedStatement prepareStatement(Connection conn, String cmd, Object params[])
    throws SQLException{
	    PreparedStatement stmt = conn.prepareStatement(cmd);
	    if(params != null)
	        setParams(stmt, params);
	    return stmt;
	}
    
    private void setParams(PreparedStatement stmt, Object params[])
    throws SQLException{
    for(int i = 0; i < params.length; i++)
    {
        Object o = params[i];
        if(o instanceof Integer)
            stmt.setInt(i + 1, ((Integer)o).intValue());
        else
        if(o instanceof Short)
            stmt.setShort(i + 1, ((Short)o).shortValue());
        else
        if(o instanceof Long)
            stmt.setLong(i + 1, ((Long)o).longValue());
        else
        if(o instanceof String)
            stmt.setString(i + 1, (String)o);
        else
        if(o instanceof Date)
            stmt.setObject(i + 1, o);
        else
        if(o instanceof Boolean)
            stmt.setBoolean(i + 1, ((Boolean)o).booleanValue());
        else
        if(o instanceof byte[])
            stmt.setBytes(i + 1, (byte[])o);
        else
        if(o instanceof Double)
            stmt.setDouble(i + 1, ((Double)o).doubleValue());
        else
        if(o instanceof Float)
            stmt.setFloat(i + 1, ((Float)o).floatValue());
        else
        if(o == null)
            stmt.setNull(i + 1, 1111);
        else
            throw new SQLException("Not allowed dataBase data type");
    }

}
    
    
	static <T> ArrayList<T> getObjectListFromResultSet(ResultSet rs, ResultObjectBuilder<T> builder){
		if (rs != null) {
			ArrayList<T> objectList = null;
			try {
				objectList = new ArrayList<T>();
				while (rs.next()) {
					objectList.add(builder.build(rs));
				}
			} catch (SQLException e) {
				logger.error(e.getMessage());
			}
			return objectList;
		} else {
			return null;
		}
	}
	
	private ArrayList<DataRow> getDataRowListFromResultSet(ResultSet rs, int dataRowType) throws SQLException {
		if (rs != null) {
			ResultSetMetaData rsMetaData = rs.getMetaData();
			int totalColumn = rsMetaData.getColumnCount();
			ArrayList<DataRow> dataRowList = new ArrayList<DataRow>();
			while (rs.next()) {
				DataRow dr = new DataRow(dataRowType);
				for (int i = 1; i <= totalColumn; i++) {
					if (dataRowType == DataRow.Type_List) {
						dr.addItem(rs.getObject(i));
					} else {
						dr.addItem(rsMetaData.getColumnLabel(i), rs.getObject(i));
					}
				}
				dataRowList.add(dr);
			}
			return dataRowList;
		} else {
			throw new SQLException("The ResultSet is null");
		}
	}
	public static void logError(String msg) {
		logger.error(msg);
	}
	
	public static String logCmd(String cmd, Object[] params) {
		StringBuilder sb = new StringBuilder();
		sb.append(cmd);
		sb.append("/");
		sb.append(params == null ? "" : Arrays.toString(params));
		return sb.toString();
	}
	
	public static void logQueryCmdError(String method, String cmd, Object[] params) {
		String msg = new StringBuilder("<<DbManager>>-").append(method).append(" error [").append(logCmd(cmd, params)).append("]").toString();
		logError(msg);
	}
	
	public static void logBatchCmdError(String method, String cmd, Collection<Object[]> paramsList) {
		String msg = new StringBuilder("<<DbManager>>-").append(method).append(" error [").append(logBatchCmd(cmd, paramsList)).append("]").toString();
		logError(msg);
	}
	
	private static String logBatchCmd(String cmd, Collection<Object[]> paramsList) {
		StringBuilder sb = new StringBuilder();
		sb.append(cmd);
		if (paramsList != null) {
			int count = 0;
			for (Object[] params : paramsList) {
				if (++count > BATCH_MAX_PARAM_LOG_COUNT) {
					break;
				}
				sb.append("/");
				sb.append(params == null ? "" : Arrays.toString(params));
			}
			if (count > BATCH_MAX_PARAM_LOG_COUNT) {
				sb.append("/...");
			}
		}
		return sb.toString();
	}

	
	
    private static ComboPooledDataSource createConnectionPool(){
        ComboPooledDataSource ds = new ComboPooledDataSource();
        try {
        	ds.setDriverClass("com.mysql.jdbc.Driver");	
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
        ds.setJdbcUrl("jdbc:mysql://" + ConfigData.DB_IP + "/"+ ConfigData.DB_DATABASE_NAME + "?user=" + ConfigData.DB_USERNAME + "&password=" + ConfigData.DB_PASSWORD + "&useUnicode=true&characterEncoding=utf8");
//		ds.setJdbcUrl("jdbc:mysql://127.0.0.1/tshirt?user=root&password=123456&useUnicode=true&characterEncoding=utf8");
		ds.setInitialPoolSize(2);
        ds.setMinPoolSize(10);
        ds.setMaxPoolSize(60);
        ds.setMaxStatements(100);
        ds.setIdleConnectionTestPeriod(5);
        ds.setMaxIdleTime(1000);
        return ds;
    }
}
