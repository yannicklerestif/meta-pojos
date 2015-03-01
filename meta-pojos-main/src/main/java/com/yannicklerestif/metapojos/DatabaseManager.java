package com.yannicklerestif.metapojos;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Enumeration;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;

public class DatabaseManager {

	private final static String DB_NAME = "derbyDB";

	public DatabaseManager() throws Exception {
		doInConnection(con -> {
			Statement s = con.createStatement();

			// dropping all tables
			ResultSet tables = con.getMetaData().getTables(null, "APP", null,
					null);
			while (tables.next()) {
				s.execute("drop table " + tables.getString("TABLE_NAME"));
			}

			// executing ddl file
			InputStream in = this.getClass().getClassLoader()
					.getResourceAsStream("ddl.sql");
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(in));
			String sql = null;
			while ((sql = reader.readLine()) != null) {
				s.execute(sql);
			}
		});
	}

	public void storeSingleClass(final String className) throws Exception {
		doInConnection(con -> {
			InputStream in = new FileInputStream(className);
			ClassReader cr = new ClassReader(in);
			ClassNode cn = new ClassNode();
			cr.accept(cn, 0);
			System.out.println("name => " + cn.name);
			PreparedStatement ps = con
					.prepareStatement("insert into class (name) values ?");
			ps.setString(1, cn.name);
			ps.executeUpdate();
		});
	}

	public void storeClassDirectory(String dirName) throws Exception {
		doInConnection(con -> {
			Files.walk(Paths.get(dirName)).filter(Files::isRegularFile)
					.forEach(System.out::println);
			// TODO
		});
	}

	public void storeJarFile(String jarFileName) throws Exception {
		doInConnection(con -> {
			ZipFile jarFile = new ZipFile(jarFileName);

			Enumeration<? extends ZipEntry> entries = jarFile.entries();
			int cpt = 0;
			PreparedStatement ps = con
					.prepareStatement("insert into class (name) values ?");
			while (entries.hasMoreElements()) {
				ZipEntry entry = entries.nextElement();
				if (!(entry.getName().endsWith(".class")))
					continue;
				InputStream stream = jarFile.getInputStream(entry);
				ClassReader cr = new ClassReader(stream);
				ClassNode cn = new ClassNode();
				cr.accept(cn, 0);
				// System.out.println("name => " + cn.name);
				ps.setString(1, cn.name);
				for (int i = 0; i < 20; i++)
					ps.executeUpdate();
				cpt++;
				if (cpt % 100 == 0)
					System.out.println(cpt + " - " + cn.name);
			}
			jarFile.close();
		});
	}

	public void bulkInsert() throws Exception {
		doInConnection(con -> {
			Statement s = con.createStatement();
			s.execute("CALL SYSCS_UTIL.SYSCS_IMPORT_TABLE (null,'CLASS','/home/yannick/classes_to_import2.txt',null,null,null,0)");
		});
	}

	private int countClasses(Connection con) throws Exception {
		Statement st = con.createStatement();
		ResultSet rs = st.executeQuery("select count(*) from class");
		rs.next();
		return rs.getInt(1);
	}

	@FunctionalInterface
	private interface ToPerform {
		public void doSomething(Connection con) throws Exception;
	}

	private void doInConnection(ToPerform f) throws Exception {
		Connection con = DriverManager.getConnection("jdbc:derby:" + DB_NAME
				+ ";create=true", new Properties());
		con.setAutoCommit(false);
		int startNumber = countClasses(con);
		long start = System.currentTimeMillis();
		f.doSomething(con);
		int end = countClasses(con);
		System.out.println("total time : "
				+ (System.currentTimeMillis() - start)
				+ " - classes added : " + (end - startNumber));
		con.commit();
		con.close();
	}

}
