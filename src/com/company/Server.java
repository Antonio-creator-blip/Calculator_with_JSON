package com.company;


import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.*;
import java.net.*;
import java.util.*;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.*;
import java.math.BigDecimal;




public class Server {

    ServerSocket server = null;
    Socket client = null;
    float result;
    String espressione;
    float first ,second;
    char symbol;
    BufferedReader inDalClient;
    DataOutputStream outVersoClient;

    public Socket attendi() {
        try {
            System.out.println("1 SERVER partito in esecuzione ...");

            // creo un server sulla porta 6789
            server = new ServerSocket(6789);

            // rimane in attesa di un client
            client = server.accept();

            //chiuso il server per inibire altri client
            server.close();

            // associo due oggetti al socket del client per effettuare la scrittura e la lettura
            inDalClient = new BufferedReader(new InputStreamReader(client.getInputStream()));
            outVersoClient = new DataOutputStream(client.getOutputStream());
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("Errore durante l'istanza del server! ");
            System.exit(1);
        }
        return client;
    }

    public void comunica() {
        try {
            while(true) {
                System.out.println("Connesso");
                espressione = inDalClient.readLine();

                //String nomeFile = "C:/Users/Win 10 Pro/IdeaProjects/Calculator_with_JSON/DIGITS.json";
                //FileReader reader = new FileReader(nomeFile);
                JSONParser parserJSON = new JSONParser();
                JSONObject oggettoJSON = (JSONObject) parserJSON.parse(espressione);

                String operation = (String) oggettoJSON.get("Espressione");

                System.out.println("Operazione da eseguire : "+ operation);
                char symbol = 0;
                int j=0;
                int x=0;
                int digit=0;
                String first_number = null, second_number = null;
                char[] ch = new char[operation.length()];
                for(int i=0; i<operation.length(); i++) {
                    if(Character.isDigit(operation.charAt(i)) || operation.charAt(i) == '.') {
                        digit++;
                    }
                }
                if ( operation.length()==digit ) {
                    //System.out.println("Risultato finale : " + operation);
                    JSONObject objResult = new JSONObject();
                    objResult.put("Risultato", operation);
                    outVersoClient.writeBytes(objResult.toJSONString()+"\n");
                }
                else {

                    for (int i = 0; i < operation.length(); i++) {
                        ch[i] = operation.charAt(i);
                    }
                    for (int i=0; i< operation.length();i++) {
                        Boolean check = Character.isDigit(operation.charAt(i));
                        if(check&&j==0 ) {
                            //first_number = Arrays.copyOf(first_number, first_number.length + 1);
                            //first_number[first_number.length-1] = Integer.parseInt(String.valueOf(stringaUtente));
                            if(first_number == null) {
                                first_number = String.valueOf(operation.charAt(i));
                            }else {
                                first_number = first_number + operation.charAt(i);
                            }
                        }
                        else if (j>0) {
                            //second_number = Arrays.copyOf(second_number, second_number.length + 1);
                            //second_number[second_number.length-1] = Integer.parseInt(String.valueOf(stringaUtente));
                            if(second_number == null) {
                                second_number = String.valueOf(operation.charAt(i));
                            }else {
                                second_number = second_number + operation.charAt(i);
                            }
                        }else {
                            if(ch[i]=='.' && j==0) {
                                first_number = first_number + operation.charAt(i);
                            }else if(ch[i]=='.' && j>0) {
                                second_number = second_number + operation.charAt(i);
                            }
                            else {
                                symbol = operation.charAt(i);
                                j++;
                            }
                        }

                    }
                    first  = Float.parseFloat(first_number);
                    second = Float.parseFloat(second_number);
                    switch(symbol) {
                        case '+' :
                        {
                            result = first + second;
                            break;
                        }
                        case '-' :
                        {
                            result = first - second;
                            break;
                        }
                        case '*' :
                        {
                            result = first * second;
                            break;
                        }
                        case '/' :
                        {
                            result = first / second ;
                            break;
                        }
                    }
                    String resultString = Float.toString(result);

                    JSONObject objResult = new JSONObject();
                    objResult.put("Risultato", resultString);
                /*try (FileWriter file = new FileWriter("./DIGITS.json")) {
                    file.write(oggettoJSON.toJSONString());
                } catch (IOException e) {
                    e.printStackTrace();
                }*/
                    outVersoClient.writeBytes(objResult.toJSONString()+"\n");
                }


                result=0;
                //client.close();
            }
        }catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }



    public static void main(String args[]) {
        Server servente = new Server();
        servente.attendi();
        servente.comunica();
    }

}
