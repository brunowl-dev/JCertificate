package cripto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.SignatureException;


public class AssinadorVerificadorTest {
    /* Testes por classe:
        Assinador:
            Algoritmo não existente -> lançar exceção NoSuchAlgorithmException
            Chave inválida -> lançar exceção InvalidKeyException
            Chave válida -> assinar com sucesso */
    
    private static KeyPair chave;

    @BeforeAll 
    static void setup() throws NoSuchAlgorithmException {
        KeyPairGenerator keyGen;
        keyGen = KeyPairGenerator.getInstance("RSA");
		keyGen.initialize(2048);
	    chave = keyGen.generateKeyPair();
    }

    @Test 
    void assinador_algoritmoNaoExistente() {
        assertThrows(NoSuchAlgorithmException.class, () -> {
            Assinador.assinar("AlgoritmoInexistente", "Mensagem".getBytes(), chave.getPrivate());
        });
    }       

    @Test 
    void assinador_chaveInvalida() {
        assertThrows(InvalidKeyException.class, () -> {
            Assinador.assinar("SHA256withRSA", "Mensagem".getBytes(), null);
        });
    }

    @Test 
    void assinador_chaveValida() throws Exception {
        //Isso não é um teste de assinatura, mas sim de que a assinatura foi gerada com sucesso. A verificação da assinatura é feita em outro teste.
        byte[] assinatura = Assinador.assinar("SHA256withRSA", "Mensagem".getBytes(), chave.getPrivate());
        assertEquals(256, assinatura.length);
    }

    /* Verificador:
            Chave inválida -> lançar exceção InvalidKeyException
            Algoritmo não existente -> lançar exceção NoSuchAlgorithmException
            Assinatura válida -> retorna sucesso
            Assinatura inválida -> retorna falso */

    @Test 
    void verificador_chaveInvalida() {
        assertThrows(InvalidKeyException.class, () -> {
            Verificador.verificar("SHA256withRSA", "Mensagem".getBytes(), "Assinatura".getBytes(), null);
        });
    }

    @Test 
    void verificador_algoritmoNaoExistente() {
        assertThrows(NoSuchAlgorithmException.class, () -> {
            Verificador.verificar("AlgoritmoInexistente", "Mensagem".getBytes(), "Assinatura".getBytes(), chave.getPublic());
        });
    }

    @Test
    void verificador_assinaturaValida() throws Exception {
        byte[] mensagem = "Mensagem".getBytes();
        byte[] mensagemAssinada = Assinador.assinar("SHA256withRSA", mensagem, chave.getPrivate());
        assertTrue(Verificador.verificar("SHA256withRSA", mensagem, mensagemAssinada, chave.getPublic()));
    }

    @Test 
    void verificador_assinaturaInvalida() throws Exception {
        byte[] mensagem = "Mensagem".getBytes();
        byte[] mensagemAssinada = Assinador.assinar("SHA256withRSA", mensagem, chave.getPrivate());
        byte[] mensagemAlterada = "MensagemAlterada".getBytes();
        assertEquals(false, Verificador.verificar("SHA256withRSA", mensagemAlterada, mensagemAssinada, chave.getPublic()));
    }
}
