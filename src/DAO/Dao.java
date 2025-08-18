package DAO;

import adt.*;
import java.io.*;
import java.util.function.Function;

public class Dao<T> {

    
    public <T> void saveToFile(DoubleLinkedList<T> list, String FILE_NAME) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {
            for (int i = 0; i < list.sizeOf(); i++) {
                T item = list.get(i);
                if (item != null) {
                    writer.write(item.toString());
                    writer.newLine(); 
                } else {
                    System.out.println("Warning: Item at index " + i + " is null.");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static <T> DoubleLinkedList<T> readTextFile(String fileName, int expectedLength, Function<String[], T> objectMapper) {
        DoubleLinkedList<T> list = new DoubleLinkedList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("#");
                if (parts.length >= expectedLength) {

                    T object = objectMapper.apply(parts);
                    if (object != null) {
                        list.add(object);
                    }
                } else {
                    System.out.println("Skipping invalid line: " + line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return list;
    }

    public static <T> ArrayStack<T> readTextFileAsArrayStack(String fileName, int expectedLength, Function<String[], T> objectMapper) {
        ArrayStack<T> stack = new ArrayStack<>();

        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("#");
                if (parts.length >= expectedLength) {
                   
                    T object = objectMapper.apply(parts);
                    if (object != null) {
                        stack.push(object); 
                    }
                } else {
                    System.out.println("Skipping invalid line: " + line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return stack;
    }
    
    
      public static <T> ArrayList<T> readTextFileAsArrayList(String fileName, int expectedLength, Function<String[], T> objectMapper) {
        ArrayList<T> list = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("#");
                if (parts.length >= expectedLength) {

                    T object = objectMapper.apply(parts);
                    if (object != null) {
                        list.add(object); 
                    }
                } else {
                    System.out.println("Skipping invalid line: " + line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return list;
    }

}
