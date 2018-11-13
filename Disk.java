
public class Disk {
	private int sectorCount; // sectors on the disk
	private int sectorSize; // characters in a sector
	private char[][] store; // all disk data is stored here

	public Disk() { // for default sectorCount and sectorSize
		this.sectorCount = 10000;
		this.sectorSize = 512;
		this.store = new char[sectorCount][sectorSize]; // initialize the disk with default dimensions
	}

	public Disk(int sectorCount, int sectorSize) { // for parameter sectorCount and sectorSize
		this.sectorCount = sectorCount;
		this.sectorSize = sectorSize;
		this.store = new char[sectorCount][sectorSize]; // initialize the disk with parameter dimensions
	}

	public void readSector(int sectorNumber, char[] buffer) { // sector to buffer
		for (int i = 0; i < sectorSize; i++) { // loop through the entire sector
			buffer[i] = store[sectorNumber][i]; // copy the sector to the buffer
		}
	}

	public void writeSector(int sectorNumber, char[] buffer) { // buffer to sector
		for (int i = 0; i < sectorSize; i++) { // loop through the entire sector
			store[sectorNumber][i] = buffer[i]; // copy the buffer to the sector
		}
	}
	// getter for sectorSize
	public int getSectorSize() {
		return sectorSize;
	}
}
