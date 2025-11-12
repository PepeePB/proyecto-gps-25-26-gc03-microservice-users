package com.musicfly.backend.properties;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Clase con una instancia unica usando el patron SINGLETON para cargar los datos de un
 * CSV y así localizar y tener varios idiomas en la parte de seguridad de la API.
 *
 * De libre uso, seguro que ya existen muchas pero bah, por una más.
 *
 * Su uso es sencillo, es un diccionario común que almacena todas las localizaciones
 * que son la primera fila del CSV, después almacena en otro mapa la clave y el
 * valor de cada columna correspondiente a cada idioma.
 *
 * Cualquier CSV es valido, mientras establezcas el delimitador y uses este patron en la primera
 * fila:
 *  CLAVE;ES;EN;...
 *
 * El delimitador por defecto es ; pero puede cambiarse.
 *
 * Powered by Samuel Soto 2025 - samuelsotodev@gmail.com
 */
public class MessageProperties {
    public final static MessageProperties MESSAGE_PROPERTIES = new MessageProperties();

    private final static String PATH = "src/main/resources/messages.csv";
    private Map<String, Map<String, String>> dictionary;
    private final static String DELIMITER = ";";

    private MessageProperties(){
        this.dictionary = new HashMap<>();

        // Uso de BufferReader para una mayor velocidad en la lectura
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(PATH), "UTF-8"))) {
            String linea;
            String[] encabezados = reader.readLine().split(DELIMITER);

            // Inicializa los mapas por idioma (excepto "clave")
            for (int i = 1; i < encabezados.length; i++) {
                dictionary.put(encabezados[i], new HashMap<>());
            }

            // Lee cada línea del CSV
            while ((linea = reader.readLine()) != null) {
                String[] partes = linea.split(DELIMITER, -1); // -1 para mantener columnas vacías

                String clave = partes[0];
                for (int i = 1; i < partes.length; i++) {
                    String idioma = encabezados[i];
                    String valor = partes[i];
                    dictionary.get(idioma).put(clave, valor);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Map<String, Map<String,String>> getDictionary(){
        return dictionary;
    }

}
