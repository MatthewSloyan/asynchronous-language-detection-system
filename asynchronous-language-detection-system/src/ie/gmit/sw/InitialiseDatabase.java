package ie.gmit.sw;

public class InitialiseDatabase {
	
	private static InitialiseDatabase instance = null;
	private Parser parser = null;

	private InitialiseDatabase() {}
    
    public static InitialiseDatabase getInstance() {
        if (instance == null) {
            instance = new InitialiseDatabase();
        }
        return instance;
    }
    
    public Parser getParser() {
		return parser;
	}

	public void Initialise(String languageDataSet) {
		// start the running time of program to be printed out for user
		long startTime = System.nanoTime(); 
		
		parser = new Parser(languageDataSet, 4);
		
		Database db = new Database();
		parser.setDb(db);
		
		Thread t = new Thread(parser);
		t.start();
		
		try {
			t.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		db.resize(300);
		
		// running time
		System.out.println("\nRunning time (ms): " + (System.nanoTime() - startTime));
		final long usedMem = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
		System.out.println("Used memory: " + usedMem + "\n");
		
		//String s = "'S e baile ann am Moireabh a tha ann an Inbhir Èireann (Beurla: Findhorn). Tha am baile suidhichte dìreach deas air Linne Mhoireibh. Tha Inbhir Èireann mu 3 mìltean iar-thuath air Cinn Lois agus mu 9 mìltean air falbh bho Fharrais. 'S e seo na co-chomharran aige: 57° 39′ 32.4″ Tuath agus 3° 36′ 39.6″ Iar.";
		//String s = "Go into the bathroom and wash yourself, thank you! Good sir you are the best.";
		//String s = "Разположено е в полите на планината Плана и източния край на Самоковското поле. Също така се намира в близост до планините Витоша, Рила и Верила, като всяка една от тях може да бъде видяна от високите части на селото.";
		
		//p.analyseQuery(s);
		
	}

}
