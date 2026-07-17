# Kommunikationsprotokoll des eShops

Client und Server kommunizieren über TCP-Sockets mit UTF-8-Text. Jede Zeile
enthält genau einen Befehl, einen Parameter oder einen Rückgabewert. Eine Anfrage
beginnt mit einem Befehlsnamen. Danach folgen gegebenenfalls die Parameter. Erst
nach der vollständigen Antwort sendet der Client den nächsten Befehl.

## Endlicher Automat aus Sicht des Servers

```text
                         TCP-Verbindung
                               |
                               v
                    +-----------------------+
                    | Begrüßung senden      |
                    +-----------+-----------+
                                |
                                v
                    +-----------------------+
               +--->| Auf Befehl warten     |<--------------------+
               |    +----+-------------+----+                     |
               |         |             |                          |
               |    Befehl q       anderer Befehl                 |
               |         |             |                          |
               |         v             v                          |
               |   +-----------+  +-----------------------+       |
               |   | Verbindung|  | Parameter einlesen    |       |
               |   | schließen |  +-----------+-----------+       |
               |   +-----------+              |                   |
               |                              v                   |
               |                  +---------------------------+   |
               +------------------| Ausführen und antworten   |---+
                                  +---------------------------+
```

Für jede Client-Verbindung erzeugt der Server einen
`ClientRequestProcessor`, der in einem eigenen Thread läuft. Zugriffe auf das
gemeinsam verwendete `EShop`-Objekt werden synchronisiert, damit nicht mehrere
Clients gleichzeitig dieselben Bestände oder Dateien verändern.

## Beispiele

Anmeldung:

```text
Client: LOGIN
Client: benutzername
Client: passwort
Server: true
```

Bestand eines Artikels abfragen:

```text
Client: GIB_BESTAND
Client: 1000
Server: 4
```

Artikel einfügen:

```text
Client: FUEGE_ARTIKEL_EIN
Client: 1000;Laptop;5;999.99;Mitarbeitername
Server: FUEGE_ARTIKEL_EIN: OK
```

Bei einem Fehler sendet der Server statt `OK` einen passenden Fehlercode, zum
Beispiel `ERR_PREIS`, `ERR_MENGE` oder `ERR_DATEI`. Der Befehl `q` beendet die
Verbindung.

Die gemeinsam benötigten Entities, Schnittstellen und Exceptions liegen im
Common-Bereich. Der Client enthält die Benutzungsschnittstelle und die
`EShopFassade`. Der Server enthält Anwendungskern, Persistenz und die Klassen für
die Socket-Kommunikation.

## Automatische Aktualisierungsmeldungen

Jede `EShopFassade` öffnet zusätzlich zur normalen Anfrageverbindung eine zweite
Socket-Verbindung. Diese Verbindung wird ausschließlich für sofortige
Servermeldungen verwendet. Dadurch können Aktualisierungen nicht zwischen die
Zeilen einer normalen Antwort geraten.

```text
Client: ABONNIERE_AKTUALISIERUNGEN
Client: <eindeutige Client-Kennung>
Server: ABONNIERT

Server: AKTUALISIERUNG:ARTIKEL
Server: AKTUALISIERUNG:BESTAND
Server: AKTUALISIERUNG:WARENKORB
Server: AKTUALISIERUNG:BENUTZER
```

Der `AktualisierungsDienst` verwaltet die angemeldeten
Benachrichtigungsverbindungen. Nach einer erfolgreichen Änderung informiert der
`ClientRequestProcessor` alle anderen Client-Kennungen. Der Client, der die
Änderung ausgelöst hat, erhält keine doppelte Meldung. Die GUI- und CUI-Klassen
bleiben unverändert; die Fassade empfängt die Meldung in einem Hintergrundthread
und gibt sie unmittelbar in der Client-Konsole aus.
