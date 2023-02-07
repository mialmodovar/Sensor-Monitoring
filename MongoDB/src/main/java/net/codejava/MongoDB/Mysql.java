package net.codejava.MongoDB;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.gt;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.ini4j.Ini;
import org.ini4j.Profile.Section;

import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;

public class Mysql {

	private Connection conn;
	private static final String url = "jdbc:mysql://localhost:3306/";
	private static final String dbName = "sid2022";
	private static final String driver = "com.mysql.cj.jdbc.Driver";
	private static final String userName = "UserMigracao";
	private static final String password = "1234";
	private Connection connCloud;
	private static final String urlCloud = "jdbc:mysql://194.210.86.10/";
	private static final String dbNameCloud = "sid2022";
	private static final String userNameCloud = "aluno";
	private static final String passwordCloud = "aluno";
	private ArrayList<MongoCollection<Document>> collections;
	private Mongo mongo;
	private ArrayList<Outliers> outliers = new ArrayList<Outliers>();
	private long delay;

	public Mysql() {
	}

	public void initClientReplicas() {
		this.mongo = new Mongo();
		this.mongo.initClientReplicas();
		this.collections = mongo.getCollections();

	}

	public void initOutliers() {
		for (String name : Mongo.nameCollections) {
			outliers.add(new Outliers(name));
		}
	}

	public Connection connectToMysql() {

		try {
			System.out.println("MySQL Connect Example.");
			Class.forName(driver).newInstance();
			conn = DriverManager.getConnection(url + dbName, userName, password);
			System.out.println("Connected to the local database");

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return conn;

	}

	public Connection connectToCloud() {

		try {
			System.out.println("Cloud Connect Example.");
			Class.forName(driver).newInstance();
			connCloud = DriverManager.getConnection(urlCloud + dbNameCloud, userNameCloud, passwordCloud);
			System.out.println("Connected to the cloud database");

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return conn;

	}

	public void closeConnectionCloud() {
		try {
			this.connCloud.close();
			System.out.println("Disconnected from cloud database");
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void closeConnection() {
		try {
			this.conn.close();
			System.out.println("Disconnected from local database");
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public HashMap<String, String> getLastMedicao(String sensor) {

		HashMap<String, String> medicao = new HashMap<String, String>();

		String query = "Select * FROM medicao m WHERE m.IDSensor = '" + sensor
				+ "' ORDER By DataHoraMedicao DESC LIMIT 1";

		try {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			ResultSetMetaData rsmd = rs.getMetaData();

			while (rs.next()) {
				for (int i = 1; i <= 5; i++) {
					medicao.put(rsmd.getColumnName(i), rs.getString(i));
				}
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return medicao;

	}

	public HashMap<String, String> getLastAnomalia(String sensor) {

		HashMap<String, String> anomalia = new HashMap<String, String>();

		String query = "Select * FROM anomalias a WHERE a.IDSensor = '" + sensor
				+ "' ORDER By DataHoraMedicao DESC LIMIT 1";

		try {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			ResultSetMetaData rsmd = rs.getMetaData();

			while (rs.next()) {
				for (int i = 1; i <= 6; i++) {
					anomalia.put(rsmd.getColumnName(i), rs.getString(i));
				}
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return anomalia;

	}

	public Timestamp getMostRecentlyDate(String sensor) {

		HashMap<String, String> lastMedicao = getLastMedicao(sensor);
		HashMap<String, String> lastAnomalia = getLastAnomalia(sensor);
		Timestamp timeLastMedicao;
		Timestamp timeLastAnomalia;

		if (lastMedicao.isEmpty() && lastAnomalia.isEmpty()) {
			return Timestamp.valueOf("1111-11-11 11:11:11.0");
		}

		else if (!lastMedicao.isEmpty() && !lastAnomalia.isEmpty()) {

			timeLastMedicao = Timestamp.valueOf(lastMedicao.get("DataHoraMedicao"));
			timeLastAnomalia = Timestamp.valueOf(lastAnomalia.get("DataHoraMedicao"));

			int val = timeLastMedicao.compareTo(timeLastAnomalia);

			if (val == 0) {
				return timeLastAnomalia;
			}

			else if (val < 0) {
				return timeLastAnomalia;
			}

			else if (val > 0) {
				return timeLastMedicao;
			}

		}

		else if (lastMedicao.isEmpty() && !lastAnomalia.isEmpty()) {
			return Timestamp.valueOf(lastAnomalia.get("DataHoraMedicao"));

		}

		else if (!lastMedicao.isEmpty() && lastAnomalia.isEmpty()) {
			return Timestamp.valueOf(lastMedicao.get("DataHoraMedicao"));
		}
		return Timestamp.valueOf("1111-11-11 11:11:11.0");
	}

	public ArrayList<String> getCulturasByZona(String zona) {

		ArrayList<String> culturas = new ArrayList<String>();

		String query = "SELECT * FROM cultura c WHERE c.IDZona = '" + zona + "'";

		try {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			ResultSetMetaData rsmd = rs.getMetaData();

			String result = "";

			while (rs.next()) {
				for (int i = 1; i <= 5; i++) {
					result += rsmd.getColumnName(i) + ":" + rs.getString(i) + ";";
				}
				culturas.add(result);
				result = "";
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return culturas;
	}

	public HashMap<String, Double> getParametroCultura(String IDCultura) {
		HashMap<String, Double> parametroCultura = new HashMap<String, Double>();

		String query = "SELECT * FROM parametrocultura pc JOIN"
				+ " cultura c ON pc.IDCultura = c.IDCultura  WHERE c.IDCultura = '" + IDCultura + "'";

		try {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			ResultSetMetaData rsmd = rs.getMetaData();

			while (rs.next()) {
				for (int i = 1; i <= 14; i++) {
					parametroCultura.put(rsmd.getColumnName(i), rs.getDouble(i));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return parametroCultura;
	}

	public HashMap<String, Double> getMargemZona(String IDZona) {
		HashMap<String, Double> margemZona = new HashMap<String, Double>();

		String query = "SELECT T,H,L FROM margemzona mz WHERE mz.IDZona = '" + IDZona + "'";

		try {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			ResultSetMetaData rsmd = rs.getMetaData();

			while (rs.next()) {
				for (int i = 1; i <= 3; i++) {
					margemZona.put(rsmd.getColumnName(i), rs.getDouble(i));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return margemZona;
	}

	public void insertAlertas(int IDMedicao, String sensor, String zona, Timestamp DataHoraMedicao, double ValorMedicao,
			ArrayList<String> culturas) {

		if (!culturas.isEmpty()) {
			for (String c : culturas) {

				String[] splited = c.split(";");
				String str = splited[0];
				int length = str.length();
				String IDCultura = str.substring(str.indexOf(":") + 1, length);
				String str2 = splited[4];
				int length2 = str2.length();
				String nomeCultura = str2.substring(str2.indexOf(":") + 1, length2);

				String str3 = splited[1];
				int length3 = str3.length();
				if (!str3.substring(str3.indexOf(":") + 1, length3).equals("null")) {
					int idUtilizador = Integer.valueOf(str3.substring(str3.indexOf(":") + 1, length3));

					HashMap<String, Double> parametroCultura = getParametroCultura(IDCultura);

					if (!parametroCultura.isEmpty()) {
						String tipoAlerta = isAlerta(sensor, ValorMedicao, parametroCultura);

						if (tipoAlerta.equals("Amarelo")) {
							if (canGenerateAlerta(IDCultura, parametroCultura, tipoAlerta, sensor)) {
								// insertAlerta
								insertAlerta(Integer.parseInt(IDCultura), IDMedicao, zona, DataHoraMedicao,
										ValorMedicao, tipoAlerta, sensor, nomeCultura, "",
										new Timestamp(System.currentTimeMillis()), idUtilizador);

								System.out.println("Inserir Alerta amarelo para a IDCultura " + IDCultura);
							}

							else {

							}

						} else if (tipoAlerta.equals("Vermelho")) {
							// insertAlerta
							if (canGenerateAlerta(IDCultura, parametroCultura, tipoAlerta, sensor)) {

								insertAlerta(Integer.parseInt(IDCultura), IDMedicao, zona, DataHoraMedicao,
										ValorMedicao, tipoAlerta, sensor, nomeCultura, "",
										new Timestamp(System.currentTimeMillis()), idUtilizador);
								System.out.println("Inserir Alerta vermelho para a IDCultura " + IDCultura);
							}

							else {

							}
						}

						else {

							System.out.println("A medicao " + IDMedicao + " não afeta a cultura" + IDCultura
									+ " pois não atinge " + "nenhum dos limites");
						}

					}

					else {

						System.out.println("Esta IDCultura " + IDCultura + " não tem nenhum parametro ID associado");

					}

				} else {
					System.out.println("Esta IDCultura " + IDCultura + " não tem nenhum utilizador associado");
				}
			}
		}

		else {
			System.out.println("Não há culturas nesta zona");
		}

	}

	public String isAlerta(String IDSensor, Double valorMedicao, HashMap<String, Double> pc) {

		if (IDSensor.contains("T")) {

			return getTipoAlerta(pc.get("MinTmp"), pc.get("pertoMinTmp"), pc.get("pertoMaxTmp"), pc.get("MaxTmp"),
					valorMedicao);

		}

		else if (IDSensor.contains("H")) {

			return getTipoAlerta(pc.get("MinTmp"), pc.get("pertoMinTmp"), pc.get("pertoMaxTmp"), pc.get("MaxTmp"),
					valorMedicao);

		}

		else if (IDSensor.contains("L")) {

			return getTipoAlerta(pc.get("MinTmp"), pc.get("pertoMinTmp"), pc.get("pertoMaxTmp"), pc.get("MaxTmp"),
					valorMedicao);
		}

		return "";

	}

	public String getTipoAlerta(Double min, Double pertoMin, Double pertoMax, Double max, Double valorMedicao) {

		String tipoAlerta = "";

		if ((min < valorMedicao && valorMedicao <= pertoMin) || (pertoMax <= valorMedicao && valorMedicao < max)) {
			return tipoAlerta = "Amarelo";
		}

		else if ((valorMedicao <= min) || (valorMedicao >= max)) {
			return tipoAlerta = "Vermelho";
		}
		return tipoAlerta;
	}

	public boolean canGenerateAlerta(String IDCultura, HashMap<String, Double> parametroCultura, String alertaAGerar,
			String sensor) {

		Timestamp HoraEscrita = null;
		String tipoAlerta = "";

		String query = "Select HoraEscrita,TipoAlerta FROM alerta a WHERE a.IDCultura = '" + IDCultura + "'"
				+ "AND a.TipoSensor = '" + sensor + "'" + "ORDER By HoraEscrita DESC LIMIT 1";

		try {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);

			while (rs.next()) {
				for (int i = 1; i <= 2; i++) {
					HoraEscrita = (rs.getTimestamp(1));
					tipoAlerta = (rs.getString(2));
				}
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (tipoAlerta.equals("Amarelo") && alertaAGerar.equals("Vermelho")) {
			return true;
		}

		Timestamp dataAtual = new Timestamp(System.currentTimeMillis());

		long diff;

		if (HoraEscrita != null) {

			diff = TimeUnit.MILLISECONDS.toSeconds(dataAtual.getTime() - HoraEscrita.getTime());
			double intervaloAlerta = parametroCultura.get("intervaloAlerta");

			if (diff > (intervaloAlerta * 60)) {
				return true;
			}

			else {
				System.out.println("O alerta não pode ser gerado, a diferença de datas é " + diff);
				return false;
			}
		}

		else {
			return true;
		}

	}

	public int insertMedicao(String IDSensor, String IDZona, Timestamp DataHoraMedicao, Double ValorMedicao) {

		String query = " insert into medicao (IDSensor, IDZona, DataHoraMedicao, ValorMedicao)"
				+ " values (?, ?, ?, ?)";

		try {
			PreparedStatement preparedStmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
			preparedStmt.setString(1, IDSensor);
			preparedStmt.setString(2, IDZona);
			preparedStmt.setTimestamp(3, DataHoraMedicao);
			preparedStmt.setDouble(4, ValorMedicao);
			preparedStmt.executeUpdate();
			ResultSet rs = preparedStmt.getGeneratedKeys();
			if (rs.next()) {
				int pk = rs.getInt(1);
				System.out.println("Generated PK = " + pk);
				return pk;
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	public void insertAnomalia(String IDSensor, String IDZona, Timestamp DataHoraMedicao, Double ValorMedicao,
			String TipoAnomalia) {

		String query = " insert into anomalias (IDSensor, IDZona, DataHoraMedicao, ValorMedicao, TipoAnomalia)"
				+ " values (?, ?, ?, ?, ?)";

		try {
			PreparedStatement preparedStmt = conn.prepareStatement(query);
			preparedStmt.setString(1, IDSensor);
			preparedStmt.setString(2, IDZona);
			preparedStmt.setTimestamp(3, DataHoraMedicao);
			preparedStmt.setDouble(4, ValorMedicao);
			preparedStmt.setString(5, TipoAnomalia);
			preparedStmt.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void insertAlerta(int IDCultura, int IDMedicao, String zona, Timestamp DataHoraMedicao, Double ValorMedicao,
			String TipoAlerta, String TipoSensor, String Cultura, String Mensagem, Timestamp HoraEscrita,
			int IDUtilizador) {

		String query = " insert into alerta (IDCultura,  IDMedicao,  IDZona,  DataHoraMedicao,   ValorMedicao,"
				+ "			 TipoAlerta, 	TipoSensor, 	Cultura	,  Mensagem,  HoraEscrita,  IDUtilizador)"
				+ " values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

		try {
			PreparedStatement preparedStmt = conn.prepareStatement(query);
			preparedStmt.setInt(1, IDCultura);
			preparedStmt.setInt(2, IDMedicao);
			preparedStmt.setString(3, zona);
			preparedStmt.setTimestamp(4, DataHoraMedicao);
			preparedStmt.setDouble(5, ValorMedicao);
			preparedStmt.setString(6, TipoAlerta);
			preparedStmt.setString(7, TipoSensor);
			preparedStmt.setString(8, Cultura);
			preparedStmt.setString(9, Mensagem);
			preparedStmt.setTimestamp(10, HoraEscrita);
			preparedStmt.setInt(11, IDUtilizador);
			preparedStmt.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public boolean isOutlier(Double valorMedicao, String IDZona, String IDSensor) {

		String tipoSensor = IDSensor.substring(0, 1);
		HashMap<String, Double> margemZona = getMargemZona(IDZona);
		Double margemOutlier = margemZona.get(tipoSensor);

		for (Outliers out : this.outliers) {
			if (out.getIDSensor().equals(IDSensor)) {

				Map<String, Double> map = out.getMap();

				if (map.isEmpty()) {

					map.put("ultimoValorMedidoV", valorMedicao);
					map.put("ultimoValorAvaliado", valorMedicao);
					map.put("sequenciaOutliers", 0.0);
					return false;

				}

				else {

					Double sequenciaOutliers = map.get("sequenciaOutliers");

					if (sequenciaOutliers == 2) {

						if (Math.abs(valorMedicao - map.get("ultimoValorAvaliado")) > margemOutlier) {

							map.put("ultimoValorAvaliado", valorMedicao);
							map.put("sequenciaOutliers", sequenciaOutliers + 1);
							return true;

						}

						else {

							map.put("ultimoValorMedidoV", valorMedicao);
							map.put("ultimoValorAvaliado", valorMedicao);
							map.put("sequenciaOutliers", 0.0);
							return false;

						}
					}

					else {

						if (Math.abs(valorMedicao - map.get("ultimoValorMedidoV")) > margemOutlier) {

							map.put("ultimoValorAvaliado", valorMedicao);
							map.put("sequenciaOutliers", sequenciaOutliers + 1);
							return true;

						}

						else {

							map.put("ultimoValorMedidoV", valorMedicao);
							map.put("ultimoValorAvaliado", valorMedicao);
							map.put("sequenciaOutliers", 0.0);
							return false;

						}

					}

				}
			}

			else {
			}
		}
		return false;

	}

	public boolean isAnomalia(Double valorMedicao, String IDSensor, ArrayList<String> limitesSensores) {

		boolean anomalia = true;

		for (String ls : limitesSensores) {

			String[] registo = ls.split(";");
			String sensor = registo[0];
			String idSensor = sensor.substring(sensor.indexOf(":") + 1, sensor.length());

			String tipo = registo[1];
			String tipoSensor = tipo.substring(tipo.indexOf(":") + 1, tipo.length());

			String str = tipoSensor.concat(idSensor);

			if (IDSensor.equals(str)) {

				String limiteInferior = registo[2];
				Double limInf = Double.parseDouble(
						limiteInferior.substring(limiteInferior.indexOf(":") + 1, limiteInferior.length()));

				String limiteSuperior = registo[3];
				Double limSup = Double.parseDouble(
						limiteSuperior.substring(limiteSuperior.indexOf(":") + 1, limiteSuperior.length()));

				System.out.println(limSup + " " + limInf + " " + str + " " + " Valor medicao para o sensor " + IDSensor
						+ " - " + valorMedicao);

				if (limInf <= valorMedicao && valorMedicao <= limSup) {
					anomalia = false;
				}

				else {
					anomalia = true;
				}

			}

			else {
			}

		}

		return anomalia;
	}

	public ArrayList<String> getLimitesSensores() {

		ArrayList<String> limitesSensores = new ArrayList<String>();

		String query = "SELECT * FROM sensor";

		try {
			Statement stmt = connCloud.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			ResultSetMetaData rsmd = rs.getMetaData();

			String result = "";

			while (rs.next()) {
				for (int i = 1; i <= 5; i++) {
					result += rsmd.getColumnName(i) + ":" + rs.getString(i) + ";";
				}
				limitesSensores.add(result);
				result = "";
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return limitesSensores;

	}

	public void updateDB(FindIterable<Document> list, ArrayList<String> limitesSensores) {
		for (Document doc : list) {
			String sensor = doc.getString("Sensor");
			String zona = doc.getString("Zona");
			String dataEdited = doc.getString("Data").replace("T", " ").replace("Z", "");
			Double valorMedicao = Double.parseDouble(doc.getString("Medicao").replace(",", "."));
			Timestamp time = Timestamp.valueOf(dataEdited);

			if (isAnomalia(valorMedicao, sensor, limitesSensores)) {
				insertAnomalia(sensor, zona, time, valorMedicao, "Erro fabricante");
			}

			else if (isOutlier(valorMedicao, zona, sensor)) {
				insertAnomalia(sensor, zona, time, valorMedicao, "Outlier");
			}

			else {
				int idMedicao = insertMedicao(sensor, zona, time, valorMedicao);
				ArrayList<String> culturas = getCulturasByZona(zona);
				insertAlertas(idMedicao, sensor, zona, time, valorMedicao, culturas);
			}

		}
	}

	public void insertMostRecentRecord(ArrayList<String> limitesSensores, MongoCollection<Document> coll) {

		FindIterable<Document> cursor1 = coll.find().sort(new BasicDBObject("Data", -1)).limit(1);
		updateDB(cursor1, limitesSensores);

	}

	public void insertRecordGreaterThan(ArrayList<String> limitesSensores, String dataEdited,
			MongoCollection<Document> coll) {

		Bson filter1 = and(eq("Sensor", coll.getNamespace().getCollectionName()), gt("Data", dataEdited));
		FindIterable<Document> list1 = coll.find(filter1).sort(new BasicDBObject("Data", -1)).limit(1);
		updateDB(list1, limitesSensores);

	}

	public void insertMostRecentData(ArrayList<String> limitesSensores, String dataEdited,
			MongoCollection<Document> coll) {

		Bson filter1 = and(eq("Sensor", coll.getNamespace().getCollectionName()), gt("Data", dataEdited));
		FindIterable<Document> list1 = coll.find(filter1);
		updateDB(list1, limitesSensores);

	}

	public void performMigration() {

		boolean alreadyExecuted = false;

		while (true) {

			try {
				System.out.println("************************ ----------- I am going to sleep " + delay
						+ "ms -------------- *****************************");
				Thread.sleep(delay);
				System.out.println(
						"************************ ----------- I woke up  -------------- *****************************");

			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			ArrayList<String> limitesSensores = getLimitesSensores();

			for (MongoCollection<Document> coll : this.collections) {

				String data = getMostRecentlyDate(coll.getNamespace().getCollectionName()).toString();
				System.out.println("Data" + data);
				String dataEdited = data.replace(" ", "T").replace(".0", "").concat("Z");
				System.out.println("Data ----- " + dataEdited);

				if (!alreadyExecuted) {

					if (data.equals("1111-11-11 11:11:11.0")) {

						insertMostRecentRecord(limitesSensores, coll);
					}

					else {

						insertRecordGreaterThan(limitesSensores, dataEdited, coll);
					}

					alreadyExecuted = true;
				}

				else {

					if (data.equals("1111-11-11 11:11:11.0")) {

						insertMostRecentRecord(limitesSensores, coll);

					}

					else {

						insertMostRecentData(limitesSensores, dataEdited, coll);
					}
				}
			}
		}

	}

	private void loadIniFile() {
		File iniFile = null;
		try {
			File currentLocation = new File(Mysql.class.getProtectionDomain().getCodeSource().getLocation().toURI());
			iniFile = new File(currentLocation, "config.ini");
			if (!iniFile.exists())
				iniFile.createNewFile();
			System.out.println("Created ini file at " + currentLocation.getPath());
		} catch (URISyntaxException | IOException e) {
			e.printStackTrace();
		}
		try {
			Ini ini = new Ini(iniFile);
			Section section = ini.get("Periodicidade");
			if (section == null) {
				ini.put("Periodicidade", "valorMilisegundos", 3000);
				ini.store();
				delay = 3000;
				return;
			}
			String valor = section.get("valorMilisegundos");
			delay = Long.valueOf(valor);
			System.out.println("Using a delay of " + delay + " ms");
		} catch (IOException | NumberFormatException e) {
			delay = 3000;
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		Mysql ms = new Mysql();
		ms.loadIniFile();
		ms.connectToCloud();
		ms.connectToMysql();
		ms.initClientReplicas();
		ms.initOutliers();
		ms.performMigration();

//		Deixar para testar
//		ArrayList<String> culturas = localDB.getCulturasByZona("Z1");
//		localDB.insertAlertas(14, "T1", "Z1", new Timestamp(System.currentTimeMillis()),9.0, culturas);

//		for (String s : limitesSensores) {
//			System.out.println(s);
//		}
		//

	}

}
