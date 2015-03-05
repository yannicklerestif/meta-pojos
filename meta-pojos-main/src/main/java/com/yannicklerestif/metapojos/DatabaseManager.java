package com.yannicklerestif.metapojos;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.stereotype.Service;

import com.yannicklerestif.metapojos.elements.beans.DBClass;
import com.yannicklerestif.metapojos.elements.beans.DBClassRelation;
import com.yannicklerestif.metapojos.elements.beans.DBMethod;

@Service
public class DatabaseManager {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	private DataSource dataSource;

	@Autowired
	private Environment env;

	private MetaPojoTable[] tables;

	private enum TABLES {
		classes, methods, classes_relations
	}

	private String buildImportFileName(MetaPojoTable table) {
		String root = env.getRequiredProperty(MetaPojos.MP_HOME);
		return root + "/" + table.tableName + ".txt";
	}

	private static class MetaPojoTable {
		public TABLES tableName;

		public MetaPojoTable(TABLES tableName) {
			this.tableName = tableName;
		}

		PrintWriter pw = null;
	}

	public DatabaseManager() {
		tables = new MetaPojoTable[TABLES.values().length];
		for (TABLES table : TABLES.values()) {
			tables[table.ordinal()] = new MetaPojoTable(table);
		}
	}

	public void clear() throws Exception {
		Connection con = DataSourceUtils.getConnection(dataSource);
		ResultSet tables = con.getMetaData().getTables(null, "APP", null, null);
		while (tables.next()) {
			jdbcTemplate.execute("drop table " + tables.getString("TABLE_NAME"));
		}
		tables.close();

		// executing ddl file
		InputStream in = this.getClass().getClassLoader().getResourceAsStream("ddl.sql");
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		String sql = null;
		while ((sql = reader.readLine()) != null) {
			jdbcTemplate.execute(sql);
		}
		reader.close();
	}

	public void openImportFiles() throws Exception {
		for (int i = 0; i < tables.length; i++) {
			File file = new File(buildImportFileName(tables[i]));
			file.delete();
			file.createNewFile();
			tables[i].pw = new PrintWriter(file);
		}
	}

	public void persist(DBClass dbClass) {
		writeClassToFile(dbClass);
		for (DBClassRelation dbClassRelation : dbClass.getParents())
			writeClassRelationToFile(dbClassRelation);
		for (DBMethod dbMethod : dbClass.getMethods())
			writeMethodToFile(dbMethod);
	}

	private void writeMethodToFile(DBMethod dbMethod) {
		writeToFile(TABLES.methods, dbMethod.getId(), dbMethod.getClassId(), dbMethod.getName(), dbMethod.getDesc());
	}

	private void writeClassRelationToFile(DBClassRelation dbClassRelation) {
		writeToFile(TABLES.classes_relations, dbClassRelation.getChildClassId(), dbClassRelation.getParentClassId());
	}

	private void writeClassToFile(DBClass dbClass) {
		writeToFile(TABLES.classes, dbClass.getId(), dbClass.getName());
	}

	private void writeToFile(TABLES table, Object... cols) {
		String toWrite = "";
		for (int i = 0; i < cols.length; i++) {
			if(cols[i] == null)
				System.out.println(":(");
			toWrite += "\"" + cols[i].toString() + "\"";
			if (i < cols.length - 1)
				toWrite += ",";
		}
		tables[table.ordinal()].pw.println(toWrite);
	}

	public void flush() {
		for (int i = 0; i < tables.length; i++) {
			String fileName = buildImportFileName(tables[i]);
			String tableName = tables[i].tableName.toString();
			jdbcTemplate.execute("CALL SYSCS_UTIL.SYSCS_IMPORT_TABLE (null,'" + tableName.toUpperCase() + "','" + fileName
					+ "',null,null,null,0)");
		}
	}

	public void closeImportFiles() throws Exception {
		for (int i = 0; i < tables.length; i++) {
			tables[i].pw.close();
		}
	}

}
