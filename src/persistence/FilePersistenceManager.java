package persistence;

import entities.*;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
                String erkennung = d[1].trim();
                String name = d[2].trim();
                String password = d[3].trim();
                String rolle = d[4].trim();

                Benutzer b;
                if (rolle.equalsIgnoreCase("kunde")) {
                    b = new Kunde(id, erkennung, name, password);
                }else {
                    b = new Mitarbeiter(id, erkennung, name, password);
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

    public void close() {
        if (writer != null)
            writer.close();

        if (reader != null) {
            try {
                reader.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();

            }
        }

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

        int packungGroesse = Integer.parseInt(liesZeile());

        return packungGroesse == 1 ? new Artikel(artikelID, bezeichnung, preis) : new Massengutartikel(artikelID, bezeichnung, preis, packungGroesse);
    }

    public void speichereArtikel(Artikel a) throws IOException {
        schreibeZeile(String.valueOf(a.getArtikelID()));
        schreibeZeile(a.getBezeichnung());
        schreibeZeile(String.valueOf(a.getPreis()));
        if (a instanceof Massengutartikel) {
            schreibeZeile(String.valueOf(((Massengutartikel) a).getPackungGroesse()));
        } else {
            schreibeZeile("1");
        }
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
            if (!bestandString.contains(":")) continue;
            artikelMengeListe.put(artikelID, bestand);

        }
    }

    public void speichereArtikelMenge(HashMap<Integer, Integer> hm) throws IOException {
        for (Map.Entry<Integer, Integer> entry : hm.entrySet()) {
            int artikelID = entry.getKey();
            int artikelBestand = entry.getValue();
            schreibeZeile(artikelID + ":" + artikelBestand);
        }

        schreibeZeile("EOF");

    }


    @Override
    public ArrayList<PersistenceManager.einEreignisInfo> ladeEreignisse() throws IOException {
        ArrayList<einEreignisInfo> liste = new ArrayList<>();

        String regex = "Tag:\\s*(?<date>[^|]+?)\\s*\\|\\s*" +
                "Typ:\\s*(?<typ>[^|]+?)\\s*\\|\\s*" +
                "Artikel:\\s*(?<artikel>[^|]+?)\\s*\\|\\s*" +
                "Menge:\\s*(?<menge>\\d+)\\s*\\|\\s*" +
                "Person:\\s*(?<person>.+)";

        Pattern pattern = Pattern.compile(regex);
        String line;

        while ((line = liesZeile()) != null) {
            if (line.trim().isEmpty()) continue;

            Matcher matcher = pattern.matcher(line);
            if (matcher.find()) {
                String date = matcher.group("date").trim();
                String typ = matcher.group("typ").trim();
                String bezeichnung = matcher.group("artikel").trim();
                int menge = Integer.parseInt(matcher.group("menge").trim());
                String person = matcher.group("person").trim();
                liste.add(new einEreignisInfo(date, bezeichnung, menge, typ, person));
            }
        }

        return liste;
    }

    @Override
    public void speichereEreignis(ArrayList<Ereignis> ereignisse) throws IOException {
        for (Ereignis ereignis : ereignisse) {
            schreibeZeile(ereignis.toString());
        }
    }

    //benutzer speicherung
    @Override
    public void speicherBenutzer(Benutzer benutzer) throws IOException {
        try (BufferedWriter writer =
                     new BufferedWriter(new FileWriter("benutzer.txt", true))) {
            writer.write(
                    benutzer.getBenutzerId() + ";" +
                            benutzer.getBenutzerErkennung() + ";" +
                            benutzer.getBenutzerVorNachname() + ";" +
                            benutzer.getBenutzerPassword() + ";" +
                            benutzer.getRole()
            );
            writer.newLine();
        }
    }

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
}

