package persistence;

import entities.Artikel;
<<<<<<< HEAD
import entities.Benutzer;
import entities.Kunde;
import entities.Mitarbeiter;
=======
import entities.Ereignis;
>>>>>>> 489e443a049c97b618138f57bf952a00b1d3f23e

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FilePersistenceManager implements PersistenceManager {
    private BufferedReader reader = null;
    private PrintWriter writer = null;

    @Override
    public Map<String, Benutzer> ladeBenutzer() throws IOException{
        Map<String, Benutzer> map = new HashMap<>();
        File file = new File("benutzer.txt");
        if (!file.exists()) {
            return map;
        }try(BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String zeile;
            while ((zeile = reader.readLine()) != null) {
                String[] d = zeile.split(";");
                int id = Integer.parseInt(d[0]);
                String erkennung = d[1];
                String name = d[2];
                String password = d[3];
                String rolle = d[4];

                Benutzer b;
                if (rolle.equalsIgnoreCase("kunde")) {
                    b = new Kunde(id, name, password, erkennung);
                }else {
                    b = new Mitarbeiter(id,erkennung, name, password);
                }
                map.put(erkennung, b);
            }
        }
        return map;
    }


    public void openForReading(String datei) throws FileNotFoundException {
        reader = new BufferedReader(new FileReader(datei));
    }

    public void openForWriting(String datei) throws IOException {
        writer = new PrintWriter(new BufferedWriter(new FileWriter(datei)));
    }

    public boolean close() {
        if (writer != null)
            writer.close();

        if (reader != null) {
            try {
                reader.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();

                return false;
            }
        }

        return true;
    }

    public Artikel ladeArtikel() throws IOException {
        String artikelIDString = liesZeile();
        if (artikelIDString == null) {
            // keine Daten mehr vorhanden
            return null;
        }

        int artikelID = Integer.parseInt(artikelIDString);

        String bezeichnung = liesZeile();

        String preisString = liesZeile();
        float preis = Float.parseFloat(preisString);

        return new Artikel(artikelID, bezeichnung, preis);
    }

    public boolean speichereArtikel(Artikel a) throws IOException {
        schreibeZeile(String.valueOf(a.getArtikelID()));
        schreibeZeile(a.getBezeichnung());
        schreibeZeile(String.valueOf(a.getPreis()));

        return true;
    }

    public HashMap<Integer, Integer> ladeArtikelMenge() throws IOException {
        HashMap<Integer, Integer> artikelMengeListe = new HashMap<>();
        while (true) {
            String bestandString = liesZeile();
            if (bestandString == null || bestandString.equals("EOF")) {
                // keine Daten mehr vorhanden
                return artikelMengeListe;
            }

            int artikelID = Integer.parseInt(bestandString.split(":")[0]);
            int bestand = Integer.parseInt(bestandString.split(":")[1]);
            artikelMengeListe.put(artikelID, bestand);
        }
    }

    public boolean speichereArtikelMenge(HashMap<Integer, Integer> hm) throws IOException {
        for (Map.Entry<Integer, Integer> entry : hm.entrySet()) {
            int artikelID = entry.getKey();
            int artikelBestand = entry.getValue();
            schreibeZeile(artikelID + ":" + artikelBestand);
        }

        schreibeZeile("EOF");

        return true;
    }


    /*
     *  Für Warenkorb, Kunden und Ereignisse!
     */


    private String liesZeile() throws IOException {
        if (reader != null)
            return reader.readLine();
        else
            return "";
    }

    private void schreibeZeile(String daten) {
        if (writer != null)
            writer.println(daten);
    }
<<<<<<< HEAD
    //benutzer speicherung
    @Override
    public void speicherBenutzer(Benutzer benutzer) throws IOException {
        try (BufferedWriter writer =
                     new BufferedWriter(new FileWriter("benutzer.txt",true))){
            writer.write(
                    benutzer.getBenutzerId() + ";" +
                            benutzer.getBenutzerErkennung() + ";" +
                            benutzer.getBenutzerVorNachname() + ";" +
                            benutzer.getBenutzerPassword() + ";" +
                            benutzer.getRole()
            );
            writer.newLine();
        }
=======

    @Override
    public void speichereEreignisArtikel(ArrayList<Ereignis> ereignisse) throws IOException {

        FileWriter fw = new FileWriter("Ereignisse.txt");
        PrintWriter pw = new PrintWriter(fw);

        for (Ereignis ereignis : ereignisse) {
            pw.println(ereignis);
        }
        pw.close();
>>>>>>> 489e443a049c97b618138f57bf952a00b1d3f23e
    }
}
