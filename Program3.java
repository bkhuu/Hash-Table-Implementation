
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Scanner;

public class Program3 {
	public static void main(String[] args) {
		Disk disk = new Disk(2000, 512); // initialize test disk
		DirectFile filemanager = new DirectFile(disk, 60, 27, 1024, 600); // initialize test file manager
		Scanner in = new Scanner(System.in); // open a scanner to read in input from the terminal
		boolean on = true; // loop variable
		String entry = null; // menu entry
		String key = null, country = null, altitude = null; // data field strings
		char[] keyArray; // array to write to disk
		char[] record; // array to read in file and read from disk
		String[] parts; // for inputs with the word "Mount"
		// read in records from mountain data
		try {
			BufferedReader reader = new BufferedReader(new FileReader("mountaindata.txt"));
			String text = reader.readLine(); // set text to the first line in the file
			while (text != null) { // if there is text
				record = text.toCharArray(); // set record to the raw data char array of text
				filemanager.insertRecord(record); // write record to the disk
				text = reader.readLine(); // set text to the next line in the file
			}
			reader.close();
		} catch (Exception e) {
			System.out.println("Problem reading file."); // error message
		}
		// loop until the program is ended
		while (on) {
			// print the menu
			System.out.println("   Insert new record         i");
			System.out.println("   Find record               f");
			System.out.println("   Quit                      q");
			// print the cursor
			System.out.print("-> ");
			if (in.hasNextLine()) {
				entry = in.nextLine(); // set entry to the text entered
			}
			switch (entry) {
			case "i":
				System.out.println("   Enter mountain name."); // prompt for a mountain name
				// print the cursor
				System.out.print("-> ");
				if (in.hasNext()) {
					key = in.nextLine();
					if (key.contains("Mount") && !key.contains("Mountain") && !key.contains(", ")) { // reformat the strings with the word "Mount"
						parts = key.split(" ");
						if (parts.length == 2) { // no number in the name
							key = parts[1] + ", " + parts[0];
						} else { // number in the name
							key = parts[1] + ", " + parts[0] + " " + parts[2];
						}
					}
					key += "#"; // add # to format
				}
				System.out.println("   Enter country."); // prompt for a country
				// print the cursor
				System.out.print("-> ");
				if (in.hasNext()) {
					country = in.nextLine();
					country += "#"; // add # to format
					key += country; // add country to key
				}
				System.out.println("   Enter altitude."); // prompt for an altitude
				// print the cursor
				System.out.print("-> ");
				if (in.hasNext()) {
					altitude = in.nextLine();
					key += altitude; // add altitude to key and country
					keyArray = key.toCharArray(); // set keyArray to the raw data char array of key
					filemanager.insertRecord(keyArray); // write keyArray to the disk
				}
				break;
			case "f":
				System.out.println("   Enter mountain name."); // prompt for a mountain name
				// print the cursor
				System.out.print("-> ");
				if (in.hasNext()) {
					key = in.nextLine();
					if (key.contains("Mount") && !key.contains("Mountain") && !key.contains(", ")) { // reformat the strings with the word "Mount"
						parts = key.split(" ");
						if (parts.length == 2) { // no number in the name
							key = parts[1] + ", " + parts[0];
						} else { // number in the name
							key = parts[1] + ", " + parts[0] + " " + parts[2];
						}
					}
					key += "#"; // add # to format
					keyArray = key.toCharArray(); // set keyArray to the raw data char array of key
					record = new char[60]; // initialize record as a char array of record length
					for (int i = 0; i < keyArray.length && i < record.length; i++) { // copy keyArray to record
						record[i] = keyArray[i];
					}
					long startTime = System.nanoTime();
					if (filemanager.findRecord(record) == true) { // attempt to read record from the disk
						long endTime = System.nanoTime();
						long duration = (endTime - startTime);
						System.out.println("Lookup took: " + duration + " ns.");
						filemanager.printRecord(record); // print if record is found
					}
				}
				break;
			case "q":
				in.close(); // close the scanner
				System.out.println("Program terminated.");
				on = false; // exit the program
				break;
			default:
				System.out.println("Invalid choice."); // print error message
			}
		}
	}
}