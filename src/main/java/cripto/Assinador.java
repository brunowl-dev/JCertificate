package cripto;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.SignatureException;

public class Assinador {
    private static final String ALGORITMO_PADRAO = "SHA256withRSA";

    public static byte[] assinar(byte[] mensagem, PrivateKey chave) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException
    {
        Signature assinador = Signature.getInstance(ALGORITMO_PADRAO);
        assinador.initSign(chave);
        assinador.update(mensagem);
        return assinador.sign();
    }
}
