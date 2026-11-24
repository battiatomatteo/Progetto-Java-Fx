# Progetto-Java-Fx

Applicazione per la gestione di pazienti e medici per il diabete, sviluppata con JavaFX e SQLite.

---

## Requisiti

- Java JDK 19.
- Maven.
- Un IDE compatibile con Java, ad esempio IntelliJ IDEA, Eclipse o NetBeans.

## Come scaricare il progetto

Puoi ottenere il codice sorgente in due modi:

```bash
git clone https://github.com/battiatomatteo/Progetto-Java-Fx.git
```

Oppure scaricare un archivio ZIP tramite l'interfaccia di GitHub e decomprimerlo.

## Struttura del progetto

```text
Progetto-Java-Fx/
│
├── src/
│   ├── main/
│   │   ├── java/          # Codice sorgente Java
│   │   └── resources/     # FXML, CSS e risorse grafiche
│   │
│   └── Test/              # Test automatici
│
├── lib/                   # Librerie originali
├── doc/                   # Documentazione Javadoc
├── relazione/             # Relazione del progetto
├── miodatabase.db         # Database SQLite utilizzato
├── identifier.sqlite     # File SQLite aggiuntivo
├── pom.xml                # Configurazione Maven
└── README.md
```

## Librerie / dipendenze

Il progetto utilizza:

- JavaFX: interfaccia grafica.
- SQLite JDBC: connessione al database.
- BCrypt: gestione delle password.
- OpenPDF: generazione di documenti PDF.
- JUnit 5: test automatici.

Le dipendenze vengono gestite tramite Maven.

## Avvio del progetto

Dopo aver clonato il repository, posizionati nella cartella principale ed esegui:

```bash
mvn clean javafx:run
```

Per eseguire i test automatici:

```bash
mvn test
```

## Database

Il progetto utilizza SQLite per la memorizzazione dei dati.

Il database utilizzato dall'applicazione è `miodatabase.db`.

Il file `identifier.sqlite` è presente nel repository, ma non viene utilizzato dalla connessione applicativa attuale.

## Documentazione

- Nella cartella `doc/` trovi la documentazione Javadoc generata.
- Nella cartella `relazione/` è presente la relazione del progetto.

Per consultare la documentazione Javadoc, apri il file `doc/index.html` con un browser.
