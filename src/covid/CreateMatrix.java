package covid;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class CreateMatrix {

	
	static Map<String,String> righe = new HashMap<String,String>();
	static String header = "";

	static String cartellaRegioni="C:\\Users\\Alessandro\\eclipse-workspace\\covid\\COVID-19\\dati-regioni";
	static String cartellaProvince="C:\\Users\\Alessandro\\eclipse-workspace\\covid\\COVID-19\\dati-province";
	
	public static void main(String[] args) throws IOException {

        header+="Province/State,Country/Region,Lat,Long,";
        
        File[] files = new File(cartellaRegioni).listFiles();
        for (int i = 0; i < files.length; i++) {
    		parseFile(files[i].getAbsolutePath());
		}

		if(header.endsWith(","))
			header = header.substring(0, header.length()-1);
		
		System.out.println(header);
		for (String key : righe.keySet()) {
			String line = righe.get(key);
			if(line.endsWith(","))
				line = line.substring(0, line.length()-1);
			System.out.println(line);
		}
	}
	
	public static void parseFileProvince(String dataMese, String dataGiorno) throws IOException {
		//dpc-covid19-ita-province-20200228.csv
		String nomeFile=cartellaProvince+"\\dpc-covid19-ita-province-2020"+dataMese+dataGiorno+".csv";

        String line = "";
        String cvsSplitBy = ",";

        try (BufferedReader br = new BufferedReader(new FileReader(nomeFile))) {

            while ((line = br.readLine()) != null) {

                String[] cols = line.split(cvsSplitBy);
                //data,stato,codice_regione,denominazione_regione,codice_provincia,denominazione_provincia,sigla_provincia,lat,long,totale_casi,note_it,note_en
                //2020-02-28T18:00:00,ITA,13,Abruzzo,069,Chieti,CH,42.35103167,14.16754574,0,,
                String denominazione_provincia=cols[5];
                String totale_casi=cols[9];
                if(!"denominazione_provincia".equals(denominazione_provincia)) {
                	update(denominazione_provincia, "totale_casi_provincia", totale_casi);
                }

            }
        
        }
	}
	
	public static void parseFile(String csvFile) throws IOException {
		
        String line = "";
        String cvsSplitBy = ",";

        String dataGiorno = csvFile.substring(csvFile.length()-6, csvFile.length()-4);
        String dataMese = csvFile.substring(csvFile.length()-8, csvFile.length()-6);
       
        
        try {
        	Integer.parseInt(dataMese);
        	Integer.parseInt(dataGiorno);

            header+=dataMese+"/"+dataGiorno+"/20,";

            parseFileProvince(dataMese, dataGiorno);
            
        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {

            while ((line = br.readLine()) != null) {

                String[] cols = line.split(cvsSplitBy);


                String denominazione_regione = cols[3];                
                String ricoverati_con_sintomi = cols[6];
                String terapia_intensiva = cols[7];
                String totale_ospedalizzati = cols[8];
                String isolamento_domiciliare = cols[9];
                String totale_positivi = cols[10];
                String deceduti = cols[14];
                String totale_casi = cols[15];
                String tamponi = cols[16];
                
                if(!"denominazione_regione".equals(denominazione_regione)) {

                	update(denominazione_regione, "ricoverati_con_sintomi", ricoverati_con_sintomi);
                	update(denominazione_regione, "terapia_intensiva", terapia_intensiva);
                	update(denominazione_regione, "totale_ospedalizzati", totale_ospedalizzati);
                	update(denominazione_regione, "isolamento_domiciliare", isolamento_domiciliare);
                	update(denominazione_regione, "totale_positivi", totale_positivi);
                	update(denominazione_regione, "deceduti", deceduti);
                	update(denominazione_regione, "tamponi", tamponi);
                	update(denominazione_regione, "totale_casi", totale_casi);
                	
                }

            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        
        }catch (NumberFormatException e) {
			// TODO: handle exception
		}

	}

	private static void update(String denominazione_regione, String serie, String valore) {
        String line = get(denominazione_regione, serie);
        line+=valore+",";
        put(denominazione_regione,serie,line);
	}

	private static void put(String regione, String serie, String value) {
		righe.put(regione+"-"+serie, value);		
	}

	private static String get(String regione, String serie) {
		String tmp = righe.get(regione+"-"+serie);
		if(tmp == null)
			tmp=regione+","+serie+",0.0,0.0,";
		return tmp;
	}
}
