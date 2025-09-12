package App;//Tutaj znajdują się wszystkie metody związane z pracą przy użyciu plików

import java.io.*;
import java.util.ArrayList;

public abstract class Pliki {

    static ArrayList<String> wczytajPlik(String path, String name) {
        ArrayList<String> lista = new ArrayList<>();
        try {
            BufferedReader plik = new BufferedReader(new FileReader(path + name));
            String linia;
            while ((linia = plik.readLine()) != null)
                lista.add(linia);
            plik.close();
        }
        catch (FileNotFoundException e) {
            System.out.println(" Nie znaleziono pliku '"+name+"'.");
        }
        catch(IOException e){
            e.printStackTrace();
        }
        return lista;
    }

    static void zapiszPlik(String path, String nazwa, String[] linie){
        try {
            BufferedWriter plik = new BufferedWriter(new FileWriter(path+nazwa));
            for (String linia : linie) {
                plik.write(linia + "\n");
            }
            plik.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static void clear(String path){
        try {
            BufferedWriter plik = new BufferedWriter(new FileWriter(path));
            plik.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static String[] getFilesNames(String directoryPath, String fileExtension){
        ArrayList<String> fileNamesList = new ArrayList<>();
        File directory = new File(directoryPath);
        File[] arrayOfFiles = directory.listFiles();
        if(arrayOfFiles!=null && arrayOfFiles.length>0)
            for (File file:arrayOfFiles) {
                if(file.isFile() && file.getName().endsWith(fileExtension)){
                    fileNamesList.add(file.getName());
                }
            }
        else{
            System.out.println("Directory was Empty or doesn't exist");
            return null;
        }
        return fileNamesList.toArray(new String[0]);
    }
}