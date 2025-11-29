package service;

public class BookFineStrategy implements FineStrategy{
	
	@Override
    public int calculateFine(int overdueDays) {
        return 10 * overdueDays;  // for every overdue day 10NIS
    }
	
}

