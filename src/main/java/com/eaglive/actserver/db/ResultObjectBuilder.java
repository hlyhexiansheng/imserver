package com.eaglive.actserver.db;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface  ResultObjectBuilder<T> {
	public abstract T build(ResultSet resultset)
    throws SQLException;
}
