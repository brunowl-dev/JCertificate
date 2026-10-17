package cripto;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.Signature;

public class Principal {
	public static void main(String[] args) 
	{
		try {
			KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
			keyGen.initialize(2048);
			
			KeyPair chave = keyGen.generateKeyPair();
			
			//Mensagem assinada válida - printa true
			Signature assinador = Signature.getInstance("SHA256withRSA");
			byte[] mensagem = "Maria Luiza".getBytes();
			assinador.initSign(chave.getPrivate());
			assinador.update(mensagem);
			byte[] mensagemAssinada = assinador.sign();
			assinador.initVerify(chave.getPublic());
			assinador.update(mensagem);
			System.out.println(assinador.verify(mensagemAssinada));

			//Mensagem assinada inválida - mensagem original vai ser alterada após a assinatura
			byte[] mensagemAlterada = "Maria Luisa".getBytes(); //Maria Luiza -> Maria Luisa, alteração de 1 byte
			assinador.update(mensagemAlterada);
			System.out.println(assinador.verify(mensagemAssinada));
		}
		catch (Exception e) {
			System.out.println("O Ambiente não suporta RSA!");
		}
		
		
	}
}
