package service;

public class CDFineStrategy implements FineStrategy {

    @Override
    public int calculateFine(int overdueDays) {
        return 10 * overdueDays; 
    }
}
