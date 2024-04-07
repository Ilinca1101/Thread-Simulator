import java.awt.*;
import java.net.*;
import java.io.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

class TaskInfo{
	String nume;
	char alias;
	int TIME_START;
	int PHASES_COUNT;
	int[][] PHASES;
	int contorRecompense, contorPenalizari;//penalty/rewards
	int nr_coada;//val care indica in ce coada se afla procesul (1, 2 sau 3)
	int fazaCurenta, repetitiaCurenta, CPUcountCurent,IOcountCurent;
	boolean processFinished;
	boolean tbAdauagatInCoada;// true/false
	
	public TaskInfo(String nume, char alias, int TIME_START, int PHASES_COUNT, int[][] PHASES){
		this.nume=nume;
		this.alias=alias;
		this.TIME_START=TIME_START;
		this.PHASES_COUNT=PHASES_COUNT;
		this.PHASES=new int[PHASES_COUNT][3];
		
		this.contorRecompense = 0;
		this.contorPenalizari = 0;
		this.nr_coada=1;
		
		this.fazaCurenta = 0;
		this.repetitiaCurenta = 0;
		this.CPUcountCurent = 0;
		this.IOcountCurent = 0;

		this.processFinished = false;
		this.tbAdauagatInCoada = false;
	}
     //utilizate pentru a accesa și actualiza variabilele private ale clasei 
	public boolean getTbAdauagatInCoada() {
		return tbAdauagatInCoada;
	}

	public void setTbAdauagatInCoada(boolean value) {
		tbAdauagatInCoada = value;
	}


	public boolean getProcessFinished(){
		return processFinished;
	}

	public int getContorRecompense(){
		return contorRecompense;
	}

	public int getContorPenalizari(){
		return contorPenalizari;
	}

	public int getNr_coada(){
		return nr_coada;
	}

	public void setContorRecompense(){
		contorRecompense++;
	}

	public void setContorPenalizari(){
		contorPenalizari++;
	}

	public void setNr_coada(int nou){
		nr_coada=nou;
	}

	public int getRepetitiaCurenta() {
		return repetitiaCurenta;
	}

	public int getFazaCurenta() {
		return fazaCurenta;
	}

	public int getCPUcountCurent() {
		return CPUcountCurent;
	}

	public int getIOcountCurent() {
		return IOcountCurent;
	}


	public String getName(){
		return nume;
	}
	
	public char getAlias(){
		return alias;
	}

	public int getTIME_START(){
		return TIME_START;
	}

	public int getPHASES_COUNT(){
		return PHASES_COUNT;
	}

	public int[][] getPHASES(){
		return PHASES;
	}
	
	public void adaugaPhase(int i, int CPU, int IO, int REPEAT){
		//faza=linia i, care este va fi actualizata
		PHASES[i][0]=CPU;//numărul de operații pentru CPU pentru acea fază
		PHASES[i][1]=IO;//numărul de operații pentru I/O pentru acea fază
		PHASES[i][2]=REPEAT; //numărul de repetiții pentru acea fază
	}
	
		int actualizare() {
		
		if(CPUcountCurent < PHASES[fazaCurenta][0]) {
			CPUcountCurent++;
		/*dacă numărul de operații executate pe CPU
		până în prezent < numărul total de operații planificate pt faza curenta
		*/
			
			if(CPUcountCurent == PHASES[fazaCurenta][0])
			return 1;// daca am executat toate operatiile pe CPU necesare
			
			return 0;// altfel returnam 0
		}

		if (IOcountCurent < PHASES[fazaCurenta][1]) {
			IOcountCurent++;/*dacă numărul de operații executate pe I/O
		până în prezent < numărul total de operații planificate pt faza curenta
		*/
		}
		
		if(IOcountCurent != PHASES[fazaCurenta][1])
		return 0;
		

		if (repetitiaCurenta < PHASES[fazaCurenta][2]) {
			repetitiaCurenta++;//numărul de repetiții curente creste
			CPUcountCurent = 0;
			IOcountCurent = 0;
		// se vor reseta numerele de operații pentru CPU și I/O la 0 pt urmatoarea repetitie a fazei	
			
		} 
		
		if (repetitiaCurenta == PHASES[fazaCurenta][2]) { 
			// daca nr de repetitii ale fazei curente este indeplinit
			if (fazaCurenta < PHASES_COUNT) {//daca inca mai sunt faze ramase in proces
				fazaCurenta++;// se avanseaza la urmatoarea faza
				repetitiaCurenta = 0;
				CPUcountCurent = 0;
				IOcountCurent = 0;
				//resetarea contoarelor pt o noua faza
					
			} 
			
			if (fazaCurenta == PHASES_COUNT) {//verif. daca nr de faze au fost executate
				processFinished = true;
			}
			
			return 3;//procesul a terminat toate fazele sale
		}
		
		return 2;//procesul este în desfășurare și mai sunt operații de efectuat
	}

	public void verifica_recompense(int k, int r){
		while(true) {
			if(contorPenalizari < k && contorRecompense < r)
				break;//bucla este întreruptă și nu se mai fac alte verificări sau modificări

			boolean schimbare = false;

			if(contorPenalizari >= k) {
				if(nr_coada < 3 && nr_coada != 0) {
					nr_coada++;//procesul este trimis intr-o coada de prioritate mai mica, daca nu este deja in coada3
					contorPenalizari -= k;//contorul ce înregistrează penalizările este decrementat cu k
					schimbare = true;
				}
			}

			if(contorRecompense >= r) {
				if(nr_coada > 1) {//daca nu e in coada 1
					nr_coada--;//procesul este mutat intr-o coada de prioritate mai mare
					contorRecompense -= r;//contorul ce înregistrează premierile este decrementat cu r
					schimbare = true;
				}
			}

			if(!schimbare)
				break;
			
		}
	}
	


}