package cripto;
import java.security.KeyPair;
import java.security.KeyPairGenerator;

public class Principal {
	public static void main(String[] args) 
	{
		try {
			KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
			keyGen.initialize(2048);
			
			KeyPair chave = keyGen.generateKeyPair();
			
			//Mensagem assinada válida - printa true
			byte[] mensagem = "Maria Luiza".getBytes();
			byte[] mensagemAssinada = Assinador.assinar("SHA256withRSA", mensagem, chave.getPrivate());
			System.out.println(Verificador.verificar("SHA256withRSA", mensagem, mensagemAssinada, chave.getPublic()));

			//Mensagem assinada inválida - mensagem original vai ser alterada após a assinatura
			byte[] mensagemAlterada = "Maria Luisa".getBytes(); //Maria Luiza -> Maria Luisa, alteração de 1 byte
			System.out.println(Verificador.verificar("SHA256withRSA", mensagemAlterada, mensagemAssinada, chave.getPublic()));
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		
	}
}
