package com.kush.courseextract.app;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class CourseExtract {

	private static final String API_KEY = "/* GET API KEY FROM https://api.uwaterloo.ca/ */";
	
	private static final String TERM = "1155";
	private static final String[] SUBJECTS = {"AADMS","AB","ACC","ACINTY","ACTSC","ADMGT","AES","AFM","AHS","AMATH","ANTH","APHYS","APPLS","ARBUS","ARCH","ARCHL","ART","ARTS","ASIAN","ASTRN","AVIA","BASE","BE","BET","BIOL","BME","BOT","BUS","CCIV","CDNST","CEDEV","CHE","CHEM","CHINA","CIVE","CLAS","CM","CMW","CO","COGSCI","COMM","COMPT","COMST","CONST","COOP","CROAT","CS","CT","CULMG","CULT","DAC","DANCE","DEI","DES","DEVIS","DM","DRAMA","DUTCH","EARTH","EASIA","EBUS","ECE","ECON","EFAS","ELE","ELPE","EMLS","ENBUS","ENGL","ENVE","ENVS","ERS","ESL","EVSY","FILM","FINAN","FINE","FR","FRCS","GBDA","GEMCC","GENE","GEOE","GEOG","GEOL","GER","GERON","GGOV","GLOBAL","GRAD","GRK","GS","HEBRW","HIST","HLTH","HRCS","HRM","HS","HSG","HUMSC","HUNGN","IFS","INDEV","INTEG","INTERN","INTST","INTTS","IS","ISS","ITAL","ITALST","JAPAN","JS","KIN","KOREA","KPE","LANG","LAT","LATAM","LED","LS","LSC","MATBUS","MATH","ME","MEDST","MEDVL","MENV","MES","MI","MISC","MNS","MSCI","MSE","MTE","MTHEL","MUSIC","NANO","NATST","NE","NES","OPTOM","PACS","PAS","PD","PDARCH","PDENG","PDPHRM","PED","PERST","PHARM","PHIL","PHS","PHYS","PLAN","PMATH","POLSH","PORT","PS","PSCI","PSYCH","QIC","REC","REES","RELC","RS","RSCH","RUSS","SCBUS","SCI","SDS","SE","SEQ","SI","SIPAR","SMF","SOC","SOCIN","SOCWK","SOCWL","SPAN","SPCOM","SPD","STAT","STV","SUSM","SWK","SWREN","SYDE","TAX","THTRE","TN","TOUR","TPM","TPPE","TS","UKRAN","UN","UNIV","URBAN","UU","VCULT","WATER","WHMIS","WKRPT","WS","ZOOL"};
	
	private static String readUrl(final String url) throws IOException {
		final URL urlObj = new URL(url);
		final BufferedReader br = new BufferedReader(new InputStreamReader(urlObj.openStream()));
		final StringBuffer buffer = new StringBuffer();
		int read;
		char[] chars = new char[1024];
		while((read = br.read(chars)) != -1) {
			buffer.append(chars, 0, read);
		}
		br.close();
		return buffer.toString();
	}
	
	public static ArrayList<String> getDataFromFile(String fileName) throws IOException {
		BufferedReader fileReader = new BufferedReader(new FileReader(fileName));
		ArrayList<String> readData = new ArrayList<String>();
		char[] dataSegment = new char[6];
		while(fileReader.read(dataSegment) != -1) {
			readData.add(dataSegment.toString());
			dataSegment = new char[6];
		}
		return readData;
	}
	
	private static JsonObject getSubjectJson(final String subject) throws IOException {
		final String json = readUrl("https://api.uwaterloo.ca/v2/terms/" + TERM + "/" + subject + "/schedule.json?key=" + API_KEY);
		final JsonParser parser = new JsonParser();
		return parser.parse(json).getAsJsonObject();
	}
	
	private static String getAsString(final JsonElement ele) {
		if(!ele.isJsonNull()) {
			return ele.getAsString();
		}
		return "";
	}
	
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		final BufferedWriter bw = new BufferedWriter(new FileWriter("output.txt"));
		bw.append("<table>");
		bw.newLine();
		for(final String s : SUBJECTS) {
			final JsonObject obj = getSubjectJson(s);
			final JsonArray data = obj.get("data").getAsJsonArray();
			JsonObject next = null;
			for(int i = 0; i < data.size(); i++) {
				next = data.get(i).getAsJsonObject();
				
				bw.append("    <tr>");
				bw.newLine();
				
				bw.append("        <td>" + s + "</td>");
				bw.newLine();
				
				bw.append("        <td>" + getAsString(next.get("catalog_number")) + "</td>");
				bw.newLine();
				
				bw.append("        <td>" + getAsString(next.get("title")) + "</td>");
				bw.newLine();
				
				bw.append("        <td>" + getAsString(next.get("topic")) + "</td>");
				bw.newLine();
				
				String campus = getAsString(next.get("campus")).split(" ")[0];  // Please god, let campus never be null/empty
				
				bw.append("        <td>" + (campus.equals("ONLN") ? "Online" : "On campus") + "</td>");
				bw.newLine();
				
				bw.append("        <td>" + campus + "</td>");
				bw.newLine();
				
				bw.append("        <td>" + getAsString(next.get("note")) + "</td>");
				bw.newLine();
				
				bw.append("    </tr>");
				bw.newLine();
			}
		}
		bw.append("</table>");
		bw.flush();
		bw.close();
	}

}
