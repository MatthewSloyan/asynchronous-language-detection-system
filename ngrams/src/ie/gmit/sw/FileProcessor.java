package ie.gmit.sw;

public class FileProcessor implements Runnable {
	private String[] record;
	private int k;
	
	public FileProcessor(String[] record, int k) {
		super();
		this.record = record;
		this.k = k;
	}

	@Override
    public void run() {
        try {
        	parseLine();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
	
	private void parseLine() throws InterruptedException {
		
		try {
			for (int i = 1; i <= k; i++) {
				getKmers(i);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void getKmers(int i) {
		for (int j = 0; j < record[0].length() - i; j+=i) {
			CharSequence kmer = record[0].substring(j, j + i);
			new DatabaseProxy().add(record[1], kmer.hashCode());
		}
	}
}
