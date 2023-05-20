
import com.card.dao.CSVFileProcessor;
import com.card.dao.FileProcessor;

public class CardValidator {

    public static void main(String[] args) {
        CSVFileProcessor processor =new CSVFileProcessor();
        FileProcessor fileProcessor = new FileProcessor(processor);
        fileProcessor.start();
    }
}
