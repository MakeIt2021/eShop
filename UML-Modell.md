# UML-Modell der Client/Server-Version

Das Modell zeigt die für Aufgabe 4 relevante Aufteilung des eShops. Bereits
vorhandene Detailklassen der GUI und Persistenz sind zusammengefasst, damit die
Verteilung auf Client, Common und Server erkennbar bleibt.

```mermaid
classDiagram
    namespace Client {
        class EShopGUI
        class EShopClientCUI
        class EShopFassade
    }

    namespace Common {
        class EShopInterface
        class Artikel
        class Benutzer
        class Ereignis
        class Warenkorb
        class Exceptions
    }

    namespace Server {
        class Main
        class ClientRequestProcessor
        class AktualisierungsDienst
        class EShop
        class ArtikelVW
        class BenutzerVW
        class EreignisVW
        class WarenkorbVW
        class PersistenceManager
        class FilePersistenceManager
    }

    EShopGUI --> EShopInterface : verwendet
    EShopClientCUI --> EShopInterface : verwendet
    EShopFassade ..|> EShopInterface : implementiert
    EShop ..|> EShopInterface : implementiert

    EShopGUI --> EShopFassade
    EShopClientCUI --> EShopFassade
    EShopFassade ..> ClientRequestProcessor : Socket-Protokoll

    Main --> EShop : erzeugt einmal
    Main --> ClientRequestProcessor : erzeugt pro Verbindung
    Main --> AktualisierungsDienst : erzeugt einmal
    ClientRequestProcessor --> EShop : synchronisierte Aufrufe
    ClientRequestProcessor --> AktualisierungsDienst : meldet Änderungen
    EShopFassade ..> AktualisierungsDienst : zweiter Socket

    EShop --> ArtikelVW
    EShop --> BenutzerVW
    EShop --> EreignisVW
    EShop --> WarenkorbVW : ein Warenkorb je Benutzer

    ArtikelVW --> PersistenceManager
    BenutzerVW --> PersistenceManager
    EreignisVW --> PersistenceManager
    FilePersistenceManager ..|> PersistenceManager

    EShopFassade --> Artikel
    EShopFassade --> Benutzer
    EShopFassade --> Ereignis
    EShop --> Artikel
    EShop --> Benutzer
    EShop --> Ereignis
    EShopInterface --> Exceptions
```

## Ablauf einer Client-Anfrage

```mermaid
sequenceDiagram
    participant GUI as GUI/CUI
    participant F as EShopFassade
    participant P as ClientRequestProcessor
    participant S as EShop
    participant V as Verwaltung/Persistenz

    GUI->>F: Methode aufrufen
    F->>P: Befehl und Parameter senden
    P->>S: synchronisierte Methode aufrufen
    S->>V: Daten lesen oder ändern
    V-->>S: Ergebnis
    S-->>P: Ergebnis oder Exception
    P-->>F: Antwort oder Fehlercode
    F-->>GUI: Rückgabewert oder Exception
```

`Main` nimmt Verbindungen an. Für jede Verbindung läuft ein eigener
`ClientRequestProcessor`-Thread. Alle Prozessoren verwenden denselben
Anwendungskern, synchronisieren dessen Aufrufe und führen getrennte Warenkörbe
pro angemeldetem Benutzer beziehungsweise pro nicht angemeldeter Sitzung. Ein
gemeinsamer `AktualisierungsDienst` verteilt Änderungen über separate
Benachrichtigungsverbindungen an die anderen Clients.
