package cripto;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.SignatureException;

public class Assinador {
    public static byte[] assinar(String algoritmo, byte[] mensagem, PrivateKey chave) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException
    {
        Signature assinador = Signature.getInstance(algoritmo);
        assinador.initSign(chave);
        assinador.update(mensagem);
        return assinador.sign();
    }
}
