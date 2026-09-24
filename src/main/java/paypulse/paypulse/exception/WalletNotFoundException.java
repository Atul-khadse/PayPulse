package paypulse.paypulse.exception;

public class WalletNotFoundException extends RuntimeException{
    public WalletNotFoundException(Long id){
        super("Wallet Not found with ID: " + id);
    }
}
