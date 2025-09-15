# Progetto-Java-Fx

Applicazione per la gestione di pazienti e medici per il diabete, sviluppata con JavaFX e SQLite.

---

## Requisiti

- Java JDK (versione 11 o superiore consigliata)  
- Maven o un IDE che supporti progetti JavaFX (IntelliJ IDEA, Eclipse, NetBeans…)  
- Libreria JavaFX compatibile con la versione di Java  
- SQLite (il DB è incluso, non serve installare separatamente SQLite server)  

---

## Come scaricare il progetto

Puoi ottenere il codice sorgente in due modi:

```bash
git clone https://github.com/battiatomatteo/Progetto-Java-Fx.git
```
## Struttura del progetto 
``` css
Progetto-Java-Fx/
│
├── lib/                  ← librerie esterne (jar) necessarie
├── src/main/             ← codice fonte Java
├── doc/                  ← documentazione (descrizioni, diagrammi, manuali, API)
├── identifier.sqlite     ← database SQLite usato per il salvataggio dati
├── miodatabase.db        ← eventualmente un altro file di database
├── .idea/                ← file del progetto IDE (se usato IntelliJ, ecc.)
├── README.md             ← questo file
└── ProgettoJavaFX.iml    ← file di configurazione del progetto per IntelliJ
```

## Librerie / dipendenze

Nel progetto sono presenti:

- JavaFX: usata per l’interfaccia grafica
- SQLite JDBC: per la connessione al database SQLite
- Altre librerie se presenti nella cartella lib/ (da verificare caso per caso)

Assicurati che:

- La cartella lib/ sia inclusa nel classpath quando esegui l’applicazione
- Le versioni delle librerie siano compatibili con la versione di Java in uso


## Database

Il progetto include un file SQLite (identifier.sqlite oppure miodatabase.db) già popolato con le tabelle necessarie.
Se vuoi resettare il database, puoi cancellare o rinominare questi file: verranno ricreati o potrai importare quelli di backup, se disponibili.

Documentazione

- Nella cartella doc/ trovi la documentazione di progetto.
- Commenti nel codice: le classi e i metodi principali dovrebbero avere javadoc o commenti che spiegano cosa fanno.
