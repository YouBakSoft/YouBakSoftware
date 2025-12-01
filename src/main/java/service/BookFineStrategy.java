package service;

public class BookFineStrategy implements FineStrategy{
	
	@Override
    public int calculateFine(int overdueDays) {
        return 20 * overdueDays; 
    }
	
}

