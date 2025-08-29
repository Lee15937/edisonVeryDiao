package DAO;

import adt.*;
import entity.Consultation;
import java.io.*;
import java.util.function.Function;

/**
 *
 * @author Ko Soon Lee
 */

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

    public <T> void saveToFile(ListInterface<T> list, String fileName) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            for (int i = 1; i <= list.getNumberOfEntries(); i++) {
                T item = list.getEntry(i);
                if (item != null) {
                    writer.write(item.toString());
                    writer.newLine();
                } else {
                    System.out.println("âš ï¸� Warning: Item at index " + i + " is null.");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public <T extends Comparable<T>> void saveToFile(SortedLinkedList<T> list, String FILE_NAME) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {
            for (T item : list) {
                if (item != null) {
                    if (item instanceof Consultation) {
                        writer.write(((Consultation) item).saveToFile());
                    } else {
                        writer.write(item.toString()); // fallback
                    }
                    writer.newLine();
                } else {
                    System.out.println("Warning: Encountered null item while saving.");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static <T extends Comparable<T>> SortedLinkedList<T> readTextFileAsSortedLinkedList(String fileName, int expectedLength, Function<String[], T> objectMapper) {
        SortedLinkedList<T> list = new SortedLinkedList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("#");
                if (parts.length >= expectedLength) {
                    T object = objectMapper.apply(parts);
                    if (object != null) {
                        list.add(object); //SortedLinkedList auto places item in order
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

    public boolean updateRecordInFile(String fileName, String recordId, int fieldIndexToUpdate, String newValue) {
        ArrayList<String> lines = new ArrayList<>();
        boolean updated = false;

        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("#"); // Adjust delimiter if needed
                if (parts.length > fieldIndexToUpdate && parts[0].trim().equalsIgnoreCase(recordId)) {
                    parts[fieldIndexToUpdate] = newValue; // Update the specific field
                    line = String.join("#", parts);
                    updated = true;
                }
                lines.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        // Rewrite the file with updated data
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            for (String l : lines) {
                writer.write(l);
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return updated;
    }
}
