# 🩺📊 Gestione Pazienti e Medici per il Diabete

## 🚀 Tecnologie Utilizzate
Questo progetto è sviluppato utilizzando:
- 🖥 **JavaFX** per la creazione dell'interfaccia grafica.
- 🗄 **SQLite** per la gestione e memorizzazione dei dati.

## 📑 Struttura del Progetto

### 🔑 Pagina di Login

- Non prevede la registrazione: gestisce e crea l'Admin.

### 👥 Tipologie di Utenti
1. 🧑‍⚕️ **Medico (Diabetologo)**
2. 🏥 **Paziente**

---

## 🧑‍⚕️ Funzionalità per il Paziente
- 📈 Inserimento delle rilevazioni giornaliere di glicemia (**6 rilevazioni**: prima e dopo ogni pasto).
- 👨‍⚕️ Associazione con un medico per ricevere supporto via e-mail.
- 🚨 Aggiunta di sintomi (**spossatezza, nausea, mal di testa, ecc.**).
- 💊 Registrazione delle assunzioni di **insulina e/o farmaci antidiabetici orali**.

### 📝 Formato salvataggio dati:
- 📅 Giorno  
- ⏰ Ora  
- 💊 Farmaco e quantità assunta  
- 🏥 Eventuali sintomi, patologie e terapie concomitanti  

#### **🔹 Nota Bene**  
I livelli normali prima dei pasti dovrebbero essere compresi tra **80 e 130 mg/dL**,  
mentre **due ore dopo i pasti non dovrebbero superare i 180 mg/dL**.

---

## 👨‍⚕️ Funzionalità per il Medico
- ⚕️ Assegnazione delle terapie ai pazienti con dettagli precisi:
  - ⏳ Numero di assunzioni giornaliere.
  - 💉 Quantità di farmaco per ogni assunzione.
  - 🕒 Indicazioni specifiche (**dopo i pasti, lontano dai pasti, ecc.**).
- 📊 Visualizzazione dell'**andamento glicemico** del paziente (grafici settimanali/mensili).
- ✍️ Modifica della terapia in base all’evoluzione dello stato del paziente.
- 📝 Aggiornamento della sezione informativa sul paziente:
  - 🚬 **Fattori di rischio** (fumatore, ex-fumatore, dipendenze, obesità).
  - ⚕️ **Patologie pregresse e comorbidità** (ipertensione, ecc.).
- 🗂 Accesso ai dati di ogni paziente con **tracciamento delle modifiche** effettuate dal medico.

---

## 🔎 Controlli del Sistema
- 🔔 **Avvisi** in caso di assunzioni errate o mancate.
- ⚠️ **Alert** per il paziente se salta una dose.
- 📩 **Notifica** al medico se il paziente **non assume farmaci per 3 giorni consecutivi**.
- 🚨 **Messaggi personalizzati** al medico se le glicemie registrate superano le soglie indicate.

---

## 💡 Informazioni sul Diabete
Il diabete è una malattia caratterizzata da **iperglicemia**, ovvero eccesso di zucchero nel sangue.  
È causato da una produzione insufficiente di insulina o da una sua inadeguata azione.  
La condizione è in aumento a livello mondiale, con più di **3,5 milioni di pazienti** in Italia.  

🔗 Per ulteriori dettagli: [SID Italia - Diabete Tipo 2](https://www.siditalia.it/informazione/diabete-tipo-2).

Nei paesi occidentali, l'incremento della malattia è correlato a fattori come:
- 👵 **Invecchiamento della popolazione**
- 🍔 **Abitudini alimentari scorrette**
- 🏠 **Stile di vita sedentario**

Il diabete di tipo 2 interessa oltre il **90%** delle persone con diabete ed è in costante crescita.


