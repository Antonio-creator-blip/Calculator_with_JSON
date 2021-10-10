package com.company;


import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.*;
import java.net.*;

public class Client {
    String nomeServer = "localhost";
    int portaServer = 6789;
    Socket mioSocket;
    BufferedReader tastiera;
    String operation;
    String stringaRicevutaDalServerString;
    DataOutputStream outVersoServer;
    BufferedReader inDalServer;


    public Socket connetti() {
        System.out.println("2 CLIENT partito in esecuzione ...");

        try {
            // per l'input da tastiera
            tastiera = new BufferedReader(new InputStreamReader(System.in));

            // creo un socket
            mioSocket = new Socket(nomeServer, portaServer);

            // associo due oggetti al socket per effettuare la scrittura e la lettura
            outVersoServer = new DataOutputStream(mioSocket.getOutputStream());
            inDalServer = new BufferedReader(new InputStreamReader(mioSocket.getInputStream()));

        }
        catch (UnknownHostException e) {
            System.err.println("Host sconosciuto");
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("Errore durante la connessione");
            System.exit(1);
        }

        return mioSocket;
    }

    public void comunica() {
        for(;;)
            try {
                System.out.println("Inserisci l'operazione : " );
                operation = tastiera.readLine();

                char[] ch = operation.toCharArray();
                while(ch[0] == '+' || ch[0] == '-' || ch[0] == '*' || ch[0] == '/') {
                    System.out.println("Errore di sintassi. Hai inserito un operatore prima di un numero");
                    System.out.println("Inserisci di nuovo l'operazione : " );
                    operation = tastiera.readLine();
                    ch = operation.toCharArray();

                }
                int numero = operation.length(), operatore = operation.length();
                for(int  i=0; i< operation.length();i++) {
                    while(ch[i] == ',') {
                        System.out.println("Errore di sintassi. Inserire un . (punto) invece della  , (virgola) ");
                        System.out.println("Inserisci di nuovo l'operazione : " );
                        operation = tastiera.readLine();
                        ch = operation.toCharArray();
                    }
                }
                int digit=0;
                int op=0;
                for(int i=0; i<operation.length(); i++) {
                    if(Character.isDigit(operation.charAt(i))) {
                        digit++;
                    }else{
                        op = i;
                    }
                }
                while((op+1) == operation.length()){
                    System.out.println("Errore di sintassi. Inserire un'altra cifra per eseguire l'operazione");
                    System.out.println("Inserisci di nuovo l'operazione : " );
                    operation = tastiera.readLine();
                    ch = operation.toCharArray();
                }

                JSONObject obj = new JSONObject();
                obj.put("Espressione", operation);
                obj.put("Risultato", 0);

                //System.out.println(obj.toJSONString());

                try (FileWriter file = new FileWriter("./DIGITS.json")) {
                    file.write(obj.toJSONString());
                } catch (IOException e) {
                    e.printStackTrace();
                }

                outVersoServer.writeBytes(obj.toJSONString()+"\n");
                //outVersoServer.writeBytes(operation + '\n');

				/*int x=0;
				for (int i = 0 ; i<operation.length();i++) {
					Boolean check = Character.isDigit(operation.charAt(i));
					while(x<operation.length()-1) {
						if(check && operation.contains(".")) {
							x++;
						}
					}
					System.out.println("Errore di sintassi. Inserire un . (punto) invece della  , (virgola) ");
					System.out.println("Inserisci di nuovo l'operazione : " );
					operation = tastiera.readLine();
					ch = operation.toCharArray();
				}*/
                if(operation.equals("FINE")) {
                    System.out.println("CLIENT: termina elaborazione e chiude sessione");
                    mioSocket.close();
                    break;
                }

                //outVersoServer.writeBytes(second_number + '\n');
                //outVersoServer.writeByte(symbol + '\n');

                // leggo la risposta dal server
                stringaRicevutaDalServerString = inDalServer.readLine();
                String nomeFile = "C:/Users/Win 10 Pro/IdeaProjects/Calculator_with_JSON/DIGITS.json";
                FileReader reader = new FileReader(nomeFile);
                JSONParser parserJSON = new JSONParser();
                JSONObject oggettoJSON = (JSONObject) parserJSON.parse(reader);

                String risultato = (String) oggettoJSON.get("Risultato");

                System.out.println("Risultato : "  + risultato);


                //outVersoServer.write(Integer.parseInt(obj.toJSONString()+"\n"));
                //outVersoServer.flush();





			/*while(true) {
				//System.out.println("4 - inserisci la stringa da trasmetter al server: " );
				stringaUtente = "123456789" ;

				// la spedisco al server
				System.out.println("5 - invio la stringa al server e attendo ...");
				outVersoServer.writeBytes(stringaUtente + '\n');

				// leggo la risposta dal server
				stringaRicevutaDalServerString = inDalServer.readLine();
				System.out.println("8 - risposta dal server. Stringa ricevuta: " + stringaRicevutaDalServerString);

				// chiudo la connessione
				System.out.println("9 - CLIENT: termina elaborazione, mi riposo per 10 secondi");
				//mioSocket.close();
				Thread.sleep(10000);
			}*/

            } catch (Exception e) {
                System.out.println(e.getMessage());
                System.out.println("Errore durante la comunicazione col server!");
                System.exit(1);
            }
    }

    public static void main(String[] args) {
        Client cliente = new Client();
        cliente.connetti();
        cliente.comunica();


    }

}