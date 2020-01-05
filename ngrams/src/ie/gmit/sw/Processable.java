package ie.gmit.sw;

/**
* Interface designed to abstract common functionality from FileParser and PredictLanguage class.
* It could allow multiple different types of Parsers to be created, to handle multiple different file types etc.
* 
* @see FileParser
* @see PredictLanguage
* @author Matthew Sloyan
*/
public interface Processable {
	public void getKmers();
	public void getKmer(int i);
}
