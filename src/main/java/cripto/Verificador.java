package cripto;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;

public class Verificador 
{
    public static boolean verificar(byte[] mensagem, byte[] mensagemAssinada, PublicKey chave) throws InvalidKeyException, NoSuchAlgorithmException, SignatureException
    {
        Signature verificador = Signature.getInstance("SHA256withRSA");
        verificador.initVerify(chave);
        verificador.update(mensagem);
        return verificador.verify(mensagemAssinada);
    }
}
