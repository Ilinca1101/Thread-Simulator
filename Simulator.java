import java.awt.*;
import java.net.*;
import java.io.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import javax.sound.sampled.SourceDataLine;

class Simulator{
	static int q1=0,q2=0,q3=0,k=0,r=0,processes_count;
	
	public static void main (String args[]){
		
		TaskInfo[] listaInfo = citire();
		
		afiseazaDateHtml(listaInfo);
		
		int indexUnitateTimp=0;//mom. actual de timp al simularii(incepe la mom. 0)
		int indexProcesActiv=-1;//indicele procesului care urmeaza sa fie rulat(=-1 pt a ne asigura ca nu e niciun proces activ la inceputul simularii)
		int nrOperatiiRamase=0;//op ramase pe care le mai are procesorul din cele care tb facute in cozi(q1, q2, q3)
		int indexProcesIOActiv=-1;//tine evidenta procesului activ pe operatii I/O
		
		ArrayList<Integer> coada1 = new ArrayList<>();
		ArrayList<Integer> coada2 = new ArrayList<>();
		ArrayList<Integer> coada3 = new ArrayList<>(); 
		ArrayList<Integer> coadaIO = new ArrayList<>();// un proces sta aici pana  cand op. se finalizeaza 
		//tabel pt Simulation output data(sod)
		System.out.println("<BR><BR><BR><BR><BR>");
		System.out.println("<A NAME=\"sod\"></A><P ALIGN=left><B>SIMULATION OUTPUT DATA</B></P>");
		System.out.println("<TABLE WIDTH=100% BORDER=1>");
		System.out.println("<THEAD ALIGN=center>");
		System.out.println("<TR>");
		System.out.println("<TH>TIME</TH>");
		System.out.println("<TH>CPU</TH>");
		System.out.println("<TH>I/O</TH>");
		System.out.println("<TH>Q1 Queue</TH>");
		System.out.println("<TH>Q2 Queue</TH>");
		System.out.println("<TH>Q3 Queue</TH>");
		System.out.println("<TH>I/O Queue</TH>");
		System.out.println("</TR>");
		System.out.println("</THEAD>");
		System.out.println("<TBODY ALIGN=center>");

		
		while(true) {
			
			System.out.println("<TR>");
			System.out.println("<TD><FONT COLOR=blue>"+String.format("%06d", indexUnitateTimp)+"</FONT></TD>");
			
			
			for(int i=0;i<listaInfo.length;i++){
				if(listaInfo[i].TIME_START==indexUnitateTimp)
				coada1.add(i);
				if(listaInfo[i].getTbAdauagatInCoada()==true){
					if(listaInfo[i].getNr_coada() == 1) coada1.add(i);
                    else if(listaInfo[i].getNr_coada() == 2) coada2.add(i);
                    else coada3.add(i);
                    listaInfo[i].setTbAdauagatInCoada(false);//procesul a fost deja add. intr-o coada
				}
			}
			
			if(indexProcesActiv==-1){//verif daca nu exista niciun proces activ
				if(coada1.size()!=0){indexProcesActiv=coada1.get(0); nrOperatiiRamase = q1;}
				else if(coada2.size()!=0) {indexProcesActiv=coada2.get(0);nrOperatiiRamase = q2;}
				else if(coada3.size()!=0) {indexProcesActiv=coada3.get(0);nrOperatiiRamase = q3;}
			}
			
			//============================
			if(indexProcesActiv != -1){//daca exista deja un proces activ in sistem
				if(listaInfo[indexProcesActiv].getNr_coada() == 3 && coada1.size()!=0){
					indexProcesActiv=coada1.get(0); nrOperatiiRamase = q1;
				}
				
				if(listaInfo[indexProcesActiv].getNr_coada() == 3 && coada2.size()!=0){
					indexProcesActiv=coada2.get(0); nrOperatiiRamase = q2;
				}
				
				if(listaInfo[indexProcesActiv].getNr_coada() == 2 && coada1.size()!=0){
					indexProcesActiv=coada1.get(0); nrOperatiiRamase = q1;
				}
			}
			//=============================
			
			if(indexProcesActiv == -1)System.out.println("<TD>CPU: -</TD>");
			else System.out.println("<TD>CPU: <A HREF=\"#p1\">"+listaInfo[indexProcesActiv].alias+"</A></TD>");
			
			if(indexProcesIOActiv == -1)
			if(coadaIO.size() != 0) indexProcesIOActiv = coadaIO.get(0);//primul elem. din coada I/O
			
			if(indexProcesIOActiv == -1)System.out.println("<TD>I/O: -</TD>");
			else System.out.println("<TD>I/O: <A HREF=\"#p1\">"+listaInfo[indexProcesIOActiv].alias+"</A></TD>");
			
			
			if(coada1.size()!=0 && coada1.size()!=1){
				System.out.print("<TD>");
				for(int s=0;s<coada1.size();s++){
					if(coada1.get(s)!=indexProcesActiv){
						System.out.print(listaInfo[coada1.get(s)].getAlias());
					}
				}
				
				System.out.print("</TD>");
			}
			else System.out.println("<TD>-</TD>");
			
			if (coada2.size()==0||(coada1.size()==0 && coada2.size()==1))
				System.out.println("<TD>-</TD>");
			else{
				System.out.print("<TD>");
				for(int s=0;s<coada2.size();s++){
					if(coada2.get(s)!=indexProcesActiv){
						System.out.print(listaInfo[coada2.get(s)].getAlias());
					}
				}
				
				System.out.print("</TD>");
			}
			
			if (coada3.size()==0||(coada1.size()==0 && coada2.size()==0 && coada3.size()==1))
				System.out.println("<TD>-</TD>");
			else{
				System.out.print("<TD>");
				for(int s=0;s<coada2.size();s++){
					if(coada2.get(s)!=indexProcesActiv){
						System.out.print(listaInfo[coada2.get(s)].getAlias());
					}
				}
				
				System.out.print("</TD>");
			}
			
			if(coadaIO.size()!=0 && coadaIO.size()!=1){
				System.out.print("<TD>");
				for(int s=0;s<coadaIO.size();s++){
					if(coadaIO.get(s)!=indexProcesIOActiv){
						System.out.print(listaInfo[coadaIO.get(s)].getAlias());
					}
				}
				
				System.out.print("</TD>");
			}
			else System.out.println("<TD>-</TD>");
			
			
			System.out.println("</TR>");
			
			//===================================
			
			if(indexProcesActiv != -1){
				nrOperatiiRamase--;//s-a efectuat o unitate de timp din numărul total de operații
				if(listaInfo[indexProcesActiv].getProcessFinished()==true && listaInfo[indexProcesActiv].getNr_coada()!=0)
					//daca procesul activ a terminat si nu a fost eliminat din coada
				{
					if(listaInfo[indexProcesActiv].getNr_coada() == 1) coada1.remove(0);
					else if(listaInfo[indexProcesActiv].getNr_coada() == 2) coada2.remove(0);
					else coada3.remove(0);
					//se elimina procesul terminat din coada sa actuala
					listaInfo[indexProcesActiv].setNr_coada(0);//seteaza nr cozii la 0, procesul s-a terminat
					
					//linia rosie cu Process finished
					System.out.println("<TR>");
					System.out.println("<TD COLSPAN=7 BGCOLOR=red>Process #"+String.valueOf(indexProcesActiv+1)+" is finished.</TD>");
					System.out.println("</TR>");
					
					indexProcesActiv=-1;//Se actualizează la -1 pentru a indica că nu mai există niciun proces activ în acest moment
				}
				else{//daca procesul nu a terminat 
					int stare = listaInfo[indexProcesActiv].actualizare();
					if(stare==0){
						// CPU mai are operatii neterminate in coada sa 
						
						if(nrOperatiiRamase==0){
							//procesul primeste penalizare
							listaInfo[indexProcesActiv].setContorPenalizari();

							if(listaInfo[indexProcesActiv].getNr_coada() == 1) coada1.remove(0);
							else if(listaInfo[indexProcesActiv].getNr_coada() == 2) coada2.remove(0);
							else coada3.remove(0);//se elimina procesul din coada din care face parte acum
							
							//pentru a actualiza coada în care se află procesul, în funcție de numărul de penalizări și recompense
							listaInfo[indexProcesActiv].verifica_recompense(k,r);
							
								listaInfo[indexProcesActiv].setTbAdauagatInCoada(true);
							// pentru a adăuga procesul în coada sa de prioritate, în funcție de modificările efectuate anterior
							
							indexProcesActiv=-1;//procesul a fost mutat din coada sa originală și că nu mai este activ în prezent
						}
					}
					
					else if(stare==1){
				/* procesul activ a terminat toate operațiile CPU pentru o repetiție,
				dar nu a utilizat complet unitățile de timp alocate în coada sa
				*/	
						if(nrOperatiiRamase>0)
						listaInfo[indexProcesActiv].setContorRecompense();//procesul primeste recompensa
						
						//adaugam procesul in coada I/O
						coadaIO.add(indexProcesActiv); 
							

						//se scoate procesul din coada din care face parte acum
						if(listaInfo[indexProcesActiv].getNr_coada() == 1) coada1.remove(0);
						else if(listaInfo[indexProcesActiv].getNr_coada() == 2) coada2.remove(0);
						else coada3.remove(0);
						
						//pentru a actualiza coada în care se află procesul, în funcție de numărul de penalizări și recompense
						listaInfo[indexProcesActiv].verifica_recompense(k,r);

						// indexProcesActiv e setat la -1
						indexProcesActiv=-1;
					}
				}
			}
	
			
			if(indexProcesIOActiv!=-1){
				
				int stare = listaInfo[indexProcesIOActiv].actualizare();
		
				
				if(stare==2){// a terminat operațiile I/O pentru repetiția curentă?
				
						listaInfo[indexProcesIOActiv].setTbAdauagatInCoada(true);
						//este adaugat inapoi in coada CPU din care face parte 
						

					
					coadaIO.remove(0);// se sterge din coada I/O

					
					indexProcesIOActiv=-1;//indica ca procesul nu mai e activ in coada I/O
				}
				
				if(stare==3){//a terminat faza curenta?
				
						listaInfo[indexProcesIOActiv].setTbAdauagatInCoada(true);
					//este adaugat inapoi in coada CPU din care face parte 	

					coadaIO.remove(0);// se sterge din coada IO

					//adaugam randul galben cu "Process finished"
					
					System.out.println("<TR>");
					System.out.println("<TD COLSPAN=7 BGCOLOR=yellow>Phase #"+String.valueOf(listaInfo[indexProcesIOActiv].getFazaCurenta())+" of the Process #"+String.valueOf(indexProcesIOActiv+1)+" is finished.</TD>");
					System.out.println("</TR>");
					
					indexProcesIOActiv=-1;
					
					
					
				}
			}
			//verificam dacă mai există procese în oricare dintre cozi 
			int i;
			for(i=0;i<listaInfo.length && listaInfo[i].getNr_coada()==0;i++);	
			if(i==listaInfo.length)break;
			
			indexUnitateTimp++;
			/*
			 Incrementarea indexului momentului actual de timp
			 pentru a trece la următoarea unitate de timp
			*/
		}
		
		//linia rosie pt terminarea simularii 
		System.out.println("<TR>");
		System.out.println("<TD COLSPAN=7 BGCOLOR=red>Simulation is finished.</TD>");
		System.out.println("</TR>");
		System.out.println("</TBODY>");
		System.out.println("</TABLE>");
		System.out.println("</CENTER><A HREF=#top>top</A><CENTER>");
		System.out.println("</BODY>");
		System.out.println("</HTML>");
	}
	
	
	public static void afiseazaDateHtml(TaskInfo[] procese){
		System.out.println("<HTML>");
		System.out.println("<HEAD>");
		System.out.println("<TITLE>THREADS SIMULATION</TITLE>");
		System.out.println("</HEAD>");
		System.out.println("<BODY BACKGROUND=\"bkg.jpg\" BGPROPERTIES=\"fixed\">");
		System.out.println("<A NAME=\"top\"></A><BR><BR><CENTER>");
		System.out.println("<H1><B>THREADS SIMULATION</B></H1>");
		System.out.println("<BR><BR>");
		System.out.println("<TABLE WIDTH=30%>");
		System.out.println("<TR>");
		System.out.println("<TD><A HREF=\"#sid\">Simulation Input Data</A></TD>");
		System.out.println("</TR>");
		System.out.println("<TR>");
		System.out.println("<TD VALIGN=top><A HREF=\"#pd\">Processes Data</A></TD>");
		System.out.println("<TD>");
		
		for (int i = 0; i < processes_count; i++){
			System.out.println("<a href=\"#p" + String.valueOf(i+1) +"\">Process #" + String.valueOf(i+1) + "</a><br>");
		}
		
		System.out.println("</TD>");
		System.out.println("</TR>");
		System.out.println("<TR>");
		System.out.println("<TD><A HREF=\"#sod\">Simulation Output Data</A></TD>");
		System.out.println("</TR>");
		System.out.println("</TABLE>");
		System.out.println("<BR><BR><BR><BR><BR>");
		System.out.println("<A NAME=\"sid\"></A><P ALIGN=left><B>SIMULATION INPUT DATA</B></P>");
		System.out.println("<TABLE WIDTH=100% BORDER=1>"); 
		System.out.println("<THEAD ALIGN=center>"); 
		System.out.println("<TR>"); 
		System.out.println("<TH>MAX PRIORITY</TH>"); 
		System.out.println("<TH>NORMAL PRIORITY</TH>"); 
		System.out.println("<TH>MIN PRIORITY</TH>"); 
		System.out.println("<TH>PENALTY LIMIT</TH>"); 
		System.out.println("<TH>AWARD LIMIT</TH>"); 
		System.out.println("</TR>"); 
		System.out.println("</THEAD>"); 
		System.out.println("<TBODY ALIGN=center>"); 
		System.out.println("<TR>"); 
		
		System.out.println("<TD>q1 ="+q1+"</TD>");
		System.out.println("<TD>q2 ="+q2+"</TD>");
		System.out.println("<TD>q3 ="+q3+"</TD>");
		System.out.println("<TD>k ="+k+"</TD>");
		System.out.println("<TD>r ="+r+"</TD>");
		
		System.out.println("</TR>"); 
		System.out.println("</TBODY>"); 
		System.out.println("</TABLE>"); 
		System.out.println("</CENTER><A HREF=#top>top</A><CENTER>"); 
		System.out.println("<BR><BR><BR><BR><BR>"); 
		System.out.println("<A NAME=\"pd\"></A><P ALIGN=left><B>PROCESSES DATA</B></P>"); 
		System.out.println("<P ALIGN=left>Processes_Count = 5</P>"); 
		
		for(int i=0;i<processes_count;i++){
			
			System.out.println("<A NAME=\"p"+String.valueOf(i+1)+"\"></A><P ALIGN=left><B>PROCESS #"+String.valueOf(i+1)+"</B></P>");
			System.out.println("<TABLE WIDTH=100% BORDER=1>");
			System.out.println("<THEAD ALIGN=center>");
			System.out.println("<TR>");
			System.out.println("<TH>NAME</TH>");
			System.out.println("<TH>ALIAS</TH>");
			System.out.println("<TH>START TIME</TH>");
			System.out.println("<TH>PHASES COUNT</TH>");
			System.out.println("</TR>");
			System.out.println("</THEAD>");
			System.out.println("<TBODY ALIGN=center>");
			System.out.println("<TR>");
			System.out.println("<TD>"+procese[i].nume+"</TD>");
			System.out.println("<TD>"+procese[i].alias+"</TD>");
			System.out.println("<TD>"+procese[i].TIME_START+"</TD>");
			System.out.println("<TD>"+procese[i].PHASES_COUNT+"</TD>");
			System.out.println("</TBODY>");
			System.out.println("<THEAD ALIGN=center>");
			System.out.println("<TR>");
			System.out.println("<TH>PHASE COUNT</TH>");
			System.out.println("<TH>CPU TIMES COUNT</TH>");
			System.out.println("<TH>I/O TIMES COUNT</TH>");
			System.out.println("<TH>REPEAT COUNT</TH>");
			System.out.println("</TR>");
			System.out.println("</THEAD>");
			System.out.println("<TBODY ALIGN=center>");
			for(int j=0;j<procese[i].PHASES_COUNT;j++){
				
				System.out.println("<TR>");
				System.out.println("<TD>"+String.valueOf(j+1)+"</TD>");
				System.out.println("<TD>"+procese[i].PHASES[j][0]+"</TD>");
				System.out.println("<TD>"+procese[i].PHASES[j][1]+"</TD>");
				System.out.println("<TD>"+procese[i].PHASES[j][2]+"</TD>");
				System.out.println("</TR>");
				
			}
			System.out.println("</TBODY>");
			
			System.out.println("</TABLE>");
			System.out.println("</CENTER><A HREF=#top>top</A><CENTER>");
			System.out.println("<BR>");
		}
		
	}
	
	public static TaskInfo[] citire(){
		int ok=0;
		TaskInfo[] process={};
		
		BufferedReader reader;
		try {
			reader = new BufferedReader(new FileReader("textfile.txt"));
			
			String line = reader.readLine();//se citeste prima linie a fisierului
			
			String[] cuvinte = line.split(" ");//linia citită este împărțită într-un vector de cuvinte 
			for(int i=0;i<cuvinte.length;i++)
			{
				String[] cuvinte2=cuvinte[i].split("=");
		//fiecare el. din vect cuvinte este împărțit din nou 
				if(cuvinte2.length==2){ ok++;
					try{
						int number = Integer.parseInt(cuvinte2[1]);
						if(ok==1)q1=number;
						if(ok==2)q2=number;
						if(ok==3)q3=number;
						if(ok==4)k=number;
						if(ok==5)r=number;
					}
					catch (NumberFormatException ex){
						ex.printStackTrace();
					}
				}
			}
		
			line = reader.readLine();
			line = reader.readLine();
			line = reader.readLine();
			line = reader.readLine();
			String[] cuvinte3 = line.split("=");
			
			
			try{
				int number = Integer.parseInt(cuvinte3[1]);
				processes_count=number;
				
				process=new TaskInfo[processes_count];
				//atribuim valoarea lui processes_count
				line = reader.readLine();
				
				//la fel pt fiecare proces in parte 
				for(int i=0;i<processes_count;i++){
					line = reader.readLine();
					line = reader.readLine();
					
					//pt numele procesului
					String[] cuvinte_linia2 = line.split("=");
					String name=new String(cuvinte_linia2[1]);
					line = reader.readLine();
					
					//pt timpul de start
					int start_time;
					String[] cuvinte_linia3 = line.split("=");
					
					int number1 = Integer.parseInt(cuvinte_linia3[1]);
					start_time=number1;
					
					line = reader.readLine();
					
					//pt nr de faze 
					int phases_count;
					String[] cuvinte_linia4 = line.split("=");
					int number2 = Integer.parseInt(cuvinte_linia4[1]);
					phases_count=number2;
					
					//fazele unui proces
					int[][] faze=new int[phases_count][3];
					char alias = (char)('A' +i);
					TaskInfo p=new TaskInfo(name,alias,start_time,phases_count,faze);
					
					
					for(int j=0;j<phases_count;j++){
						int CPU=0,IO=0,REPEAT=0;
						line = reader.readLine();//linia curentă care conține informații despre faza curentă a procesului
						String[] cuvinte_linie_faza = line.split(" ");
						int nr=0;
						for(int t=0;t<cuvinte_linie_faza.length;t++)
						{
							String[] cuvinte4=cuvinte_linie_faza[t].split("=");
							if(cuvinte4.length==2){ //verif daca linia respectiva contine o pereche
								nr++;
								int number3 = Integer.parseInt(cuvinte4[1]);//converteste val in nr intreg
								if(nr==1)CPU=number3;
								if(nr==2)IO=number3;
								if(nr==3)REPEAT=number3;//atribuie val corecta variabilei corespunzatoare
								
								
							}
						}
						p.adaugaPhase(j,CPU,IO,REPEAT);
					}
					
					process[i]=p;
					line = reader.readLine();
					
				}
				
			}
			catch (NumberFormatException ex){
				ex.printStackTrace();
			}
			
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return process;
		
	}
	
}