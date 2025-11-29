package src.payment;

import java.math.BigDecimal;

//adapter to make legacy payment sdk work with our interface
public class LegacyPaymentSdkAdapter implements PaymentGateway {
    //todo wrap any existing legacy payment sdk here
    //this is just a stub to show how it would work
    private Object legacySdk;

    public LegacyPaymentSdkAdapter(Object legacySdk) {
        this.legacySdk = legacySdk;
    }

    @Override
    public boolean authorize(String cardToken, BigDecimal amount) {
        //converting our interface calls to legacy sdk format
        //todo map cardToken and amount to legacy format
        //todo call legacy sdk method
        //todo convert legacy response to boolean
        return true;
    }

    @Override
    public boolean capture(String authId, BigDecimal amount) {
        //converting capture call to legacy format
        //todo full mapping
        return true;
    }

    //todo implement full adapter pattern for all methods
}

