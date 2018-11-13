
import java.util.Arrays;

public class DirectFile {
	private Disk disk; // disk on which the file will be written
	private char[] buffer; // disk buffer
	private int recordSize; // in characters
	private int keySize; // in characters
	private int firstAllocated; // sector number
	private int bucketsAllocated; // buckets (i.e. sectors) originally allocated
	private int firstOverflow; // sector number
	private int overflowBuckets; // count of overflow buckets in use
	// constructor for file manager
	public DirectFile(Disk disk, int recordSize, int keySize, int firstAllocated, int bucketsAllocated) {
		this.disk = disk;
		this.buffer = new char[disk.getSectorSize()]; // buffer is the size of a sector
		this.recordSize = recordSize;
		this.keySize = keySize;
		this.firstAllocated = firstAllocated;
		this.bucketsAllocated = bucketsAllocated;
		this.firstOverflow = firstAllocated + bucketsAllocated; // overflow begins after buckets allocated
		this.overflowBuckets = 0;
	}
	// write a record to the disk
	public boolean insertRecord(char[] record) {
		char[] key = getKey(record);
		char[] country = getCountry(record);
		char[] altitude = getAltitude(record);
		disk.readSector(firstAllocated + hash(key), buffer); // read the sector from the disk
		int i;
		for (i = 0; i < disk.getSectorSize(); i += recordSize) { // search the sector for an empty slot to insert record
			if (buffer[i] == '\000' && i + recordSize < disk.getSectorSize()) { // empty slot that can fit a record
				for (int j = 0; j < keySize; j++) { // copy the key to the buffer
					buffer[i] = key[j];
					i++;
				}
				for (int j = 0; j < keySize; j++) { // copy the country to the buffer
					buffer[i] = country[j];
					i++;
				}
				for (int j = 0; j < recordSize - (keySize * 2); j++) { // copy the altitude to the buffer
					buffer[i] = altitude[j];
					i++;
				}
				disk.writeSector(firstAllocated + hash(key), buffer); // write the buffer to the disk
				return true;
			} else { // check for duplicate keys
				boolean duplicate = true;
				int k = i;
				for (int a = 0; a < keySize; a++) { // if keys are different not a duplicate
					if (buffer[k] != key[a]) {
						duplicate = false;
					}
					k++;
				}
				if (duplicate == true) {
					System.out.println("Duplicate key error."); // error message
					return false;
				}
			}
		}
		// going to overflow
		for (int b = 0; b + firstOverflow <= firstOverflow + overflowBuckets; b++) { // loop through all of the overflow buckets
			disk.readSector(firstOverflow + b, buffer); // read the sector from the disk
			int c;
			for (c = 0; c < disk.getSectorSize(); c += recordSize) { // search the sector for an empty slot to insert record
				if (buffer[c] == '\000' && c + recordSize < disk.getSectorSize()) { // empty slot that can fit a record
					for (int j = 0; j < keySize; j++) { // copy the key to the buffer
						buffer[c] = key[j];
						c++;
					}
					for (int j = 0; j < keySize; j++) { // copy the country to the buffer
						buffer[c] = country[j];
						c++;
					}
					for (int j = 0; j < recordSize - (keySize * 2); j++) { // copy the altitude to the buffer
						buffer[c] = altitude[j];
						c++;
					}
					disk.writeSector(firstOverflow + b, buffer); // write the buffer to the disk
					return true;
				} else { // check for duplicate keys
					boolean duplicate = true;
					int k = c;
					for (int a = 0; a < keySize; a++) { // if keys are different not a duplicate
						if (buffer[k] != key[a]) {
							duplicate = false;
						}
						k++;
					}
					if (duplicate == true) {
						System.out.println("Duplicate key error."); // error message
						return false;
					}
				}
			}
			overflowBuckets++; // create a new overflow bucket
		}
		return false;
	}
	// read a record from the disk
	public boolean findRecord(char[] record) {
		char[] key = getKey(record);
		char[] country = new char[keySize]; // initialize an empty char array for country
		char[] altitude = new char[recordSize - (keySize * 2)]; // initialize an empty char array for altitude
		disk.readSector(firstAllocated + hash(key), buffer); // read the sector from the disk
		int i;
		for (i = 0; i < disk.getSectorSize(); i += recordSize) { // search the sector for a matching key
			boolean match = true;
			int k = i;
			int ctr = 0;
			for (int a = 0; a < keySize; a++) { // if keys are different not a match
				if (buffer[k] != key[a]) {
					match = false;
				}
				k++;
			}
			if (match == true) {
				for (int j = 0; j < keySize; j++) {
					key[j] = buffer[i]; // copy the key from the buffer
					record[ctr] = key[j]; // copy the key to record
					i++;
					ctr++;
				}
				for (int j = 0; j < keySize; j++) {
					country[j] = buffer[i]; // copy the country from the buffer
					record[ctr] = country[j]; // copy the country to record
					i++;
					ctr++;
				}
				for (int j = 0; j < recordSize - (keySize * 2); j++) {
					altitude[j] = buffer[i]; // copy the altitude from the buffer
					record[ctr] = altitude[j]; // copy the altitude to record
					i++;
					ctr++;
				}
				return true;
			}
		}
		// going to overflow
		for (int b = 0; b + firstOverflow <= firstOverflow + overflowBuckets; b++) { // loop through all of the overflow buckets
			disk.readSector(firstOverflow + b, buffer); // read the sector from the disk
			int c;
			for (c = 0; c < disk.getSectorSize(); c += recordSize) { // search the sector for a matching key
				boolean match = true;
				int k = c;
				int ctr = 0;
				for (int a = 0; a < keySize; a++) { // if keys are different not a match
					if (buffer[k] != key[a]) {
						match = false;
					}
					k++;
				}
				if (match == true) {
					for (int j = 0; j < keySize; j++) {
						key[j] = buffer[c]; // copy the key from the buffer
						record[ctr] = key[j]; // copy the key to record
						c++;
						ctr++;
					}
					for (int j = 0; j < keySize; j++) {
						country[j] = buffer[c]; // copy the country from the buffer
						record[ctr] = country[j]; // copy the country to record
						c++;
						ctr++;
					}
					for (int j = 0; j < recordSize - (keySize * 2); j++) {
						altitude[j] = buffer[c]; // copy the altitude from the buffer
						record[ctr] = altitude[j]; // copy the altitude to record
						c++;
						ctr++;
					}
					return true;
				}
			}
		}
		System.out.println("Key not found."); // error message
		return false;
	}
	// pull the key from the raw record
	public char[] getKey(char[] record) {
		char[] key = new char[keySize]; // initialize a char array of keySize
		for (int i = 0; i < keySize; i++) { // loop through the char array
			if (record[i] == '#') { // copy up to the first #
				break;
			} else {
				key[i] = record[i];
			}
		}
		return key;
	}
	// pull the country from the raw record
	public char[] getCountry(char[] record) {
		char[] country = new char[keySize]; // initialize a char array of keySize
		int i = 0;
		while (record[i] != '#') { // skip the key
			i++;
		}
		i++;
		for (int j = 0; j < keySize; j++) { // loop through the char array
			if (record[i] == '#') { // copy up to the next #
				break;
			} else {
				country[j] = record[i];
			}
			i++;
		}
		return country;
	}
	// pull the altitude from the raw record
	public char[] getAltitude(char[] record) {
		char[] altitude = new char[recordSize - (keySize * 2)]; // initialize a char array of recordSize - (keySize * 2)
		int i = 0;
		int ctr = 0;
		while (ctr != 2) { // skip the key and country
			if (record[i] == '#') {
				ctr++;
			}
			i++;
		}
		for (int j = 0; j < recordSize - (keySize * 2); j++) { // loop through the char array
			if (i == record.length) { // copy until the end of the raw record
				break;
			} else {
				altitude[j] = record[i];
			}
			i++;
		}
		return altitude;
	}
	// print a record from sector format
	public void printRecord(char[] record) {
		int i = 0;
		String printVal = ""; // create an empty string to print
		for (int j = 0; j < keySize; j++) { // loop through the key portion of a record
			if (record[i] == '\000') { // copy up to the first null
				break;
			} else {
				printVal += record[i];
			}
			i++;
		}
		i = keySize;
		printVal += ", "; // add a comma and space
		for (int j = 0; j < keySize; j++) { // loop through the country portion of a record
			if (record[i] == '\000') { // copy up to the first null
				break;
			} else {
				printVal += record[i];
			}
			i++;
		}
		i = keySize * 2;
		printVal += ", altitude: "; // add a comma, space, and altitude label
		for (int j = 0; j < recordSize - (keySize * 2); j++) {
			if (record[i] == '\000') { // copy up to the first null or the end of the record
				break;
			} else {
				printVal += record[i];
			}
			i++;
		}
		printVal += " ft."; // add units to altitude
		System.out.println(printVal); // print the formatted string
	}
	// hash function to navigate the disk
	private int hash(char[] key) {
		return Math.abs((Arrays.hashCode(key))) % bucketsAllocated;
	}
}
