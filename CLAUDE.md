# CLAUDE.md — JCertificate

Contexto e regras de trabalho para o Claude Code neste projeto. Leia tudo antes de agir.

## 1. Sobre o projeto

**JCertificate** é uma aplicação desktop em **Java puro** (sem Spring ou outros frameworks) com interface **JFrame (Swing)** que **simula** assinatura/certificação digital de arquivos.

- Repositório: `brunowl-dev/JCertificate`
- Projeto Maven: `groupId br.com.brunowl`, `artifactId jcertificate-app`, versão `0.0.1-SNAPSHOT`
- IDE do autor: VS Code (o projeto foi criado no Eclipse). JDK: 26.
- Gerenciador de dependências: **Maven**
- Banco: **SQLite** (via `sqlite-jdbc`)

### Fluxo principal

1. O usuário cria uma conta e a aplicação gera seu par de chaves (pública e privada).
2. Logado, ele seleciona um arquivo; a app calcula o hash e assina com a chave privada **do usuário logado**.
3. Qualquer pessoa carrega o arquivo (com a assinatura) e a app mostra **qual usuário assinou**.
4. Se o arquivo for alterado depois de assinado, a verificação **deve falhar** e mostrar claramente "assinatura inválida / arquivo modificado".

### Regras de negócio já decididas

- Cada usuário = seu certificado digital, protegido por senha própria.
- Chaves geradas pela aplicação; a privada é de uso exclusivo do usuário.
- Só assina quem estiver logado como o dono da chave. **Ninguém digita chave manualmente**; a app usa a privada associada ao usuário logado.
- Deve ser possível assinar **qualquer tipo de arquivo** e verificar arquivos assinados pela aplicação.
- Usuários persistidos em **SQLite** (não usar .txt/.csv).
- Autenticação própria (usuário + senha). Senha armazenada com **PBKDF2WithHmacSHA256** (com salt) — **não** SHA-256 simples.
- É uma **simulação**: não existe autoridade certificadora real; o "dono" é quem está cadastrado na app. Isso deve estar explícito no README.

### Decisões tomadas em 2026-11-16

- **Chave privada:** guardada no banco **cifrada com AES-GCM**, com chave derivada da senha do usuário (PBKDF2, salt próprio, separado do salt do login).
- **Assinatura:** arquivo **`.sig` ao lado do original**, contendo o nome do assinante; a verificação busca a chave pública dele no banco.
- **Ordem das versões:** a do README (V2 = certificado simulado/AC da app; V3 = mensagens e extensões).
- **Hash:** o `SHA256withRSA` já faz o SHA-256 internamente; os bytes do arquivo vão direto para `Signature.update` em blocos (sem hash separado, para não haver hash duplo).
- `Principal.java` é um ambiente de testes manuais do Bruno; não faz parte da aplicação.

### Contexto acadêmico

O projeto une as disciplinas de **Segurança** e **Java** (mesmo professor, Davi). As decisões de segurança precisam ser defensáveis em apresentação.

### Versões e tasklist

O escopo completo por versão está em **`tasklist.md`** (na raiz). Ele é a fonte de verdade do que fazer e em que ordem.

- **V1:** cadastro/login, geração de chaves, assinar arquivo, verificar arquivo, GUI em JFrame.
- **V2:** certificado simulado (AC da aplicação), validade, revogação, carimbo de tempo.
- **V3:** troca de mensagens entre usuários e outras extensões.

Regras de uso da tasklist:

1. Trabalhe **uma tarefa por vez**, na ordem da tasklist, a não ser que o Bruno indique outra.
2. Não implemente nada de uma versão posterior enquanto a atual não estiver fechada.
3. Ao concluir uma tarefa (e o Bruno confirmar que funciona), marque `[x]` no `tasklist.md`.
4. Itens marcados com ❓ são decisões em aberto: **pergunte ao Bruno e registre a decisão** (neste arquivo, na seção de regras de negócio) antes de implementar.
5. Ideias novas vão para "Ideias sem versão definida" no `tasklist.md`; não entram no código sem o Bruno decidir a versão.
6. Mantenha o README com o escopo por versão.

## 2. Arquitetura sugerida

Estrutura simples, com separação clara de responsabilidades:

- **Criptografia** (`java.security`): geração de chaves, hash, assinatura, verificação, derivação de senha. Sem dependência de Swing, **testável isoladamente**.
- **Modelo:** classes de domínio (ex.: usuário).
- **Persistência:** acesso ao SQLite (JDBC puro, padrão DAO/Repository simples).
- **Telas (Swing):** cada tela em sua classe; sem lógica de criptografia dentro da GUI.

Ordem de construção preferida: **criptografia + testes (JUnit) primeiro**, depois persistência, e só então o JFrame.

## 3. Quem é o autor e como trabalhar com ele

Bruno é estudante de ADS (5º semestre) e quer ser desenvolvedor júnior. Este projeto é para o portfólio, e ele precisa **entender e conseguir defender cada parte em entrevista**.

**Nível técnico relevante:**

- Java: sua linguagem mais forte (POO, coleções, exceções, interfaces, Swing/JFrame básico).
- **Não conhece** `java.security`/criptografia, PBKDF2 e Maven (está aprendendo agora).
- Banco de dados: SQL e modelagem básicos, nível não avançado.
- Testes: contato com JUnit e TDD, nível iniciante/intermediário.

### Regras de colaboração (importante)

1. **Claude Code é ferramenta, não o autor do projeto.** O objetivo é aprender, não receber tudo pronto.
2. **No back-end (criptografia, persistência, regras de negócio):** guie passo a passo. **Explique o que cada classe/método da API faz antes de pedir que ele escreva.** Não entregue o código completo de saída; ele aprende fazendo. Só dar nomes de métodos como dica foi difícil demais para ele; explique o conceito e a assinatura e deixe-o escrever.
3. **No front-end/Swing:** pode gerar mais código de apoio, mas sempre explicando o suficiente para ele defender o que foi feito.
4. **Quando ele pedir explicitamente para implementar algo**, implemente, mas explique o que foi feito e por quê.
5. Ao investigar bugs, ajude-o a **encontrar a causa** (pistas, perguntas, onde olhar) em vez de só entregar a correção.
6. A documentação oficial do Java nem sempre é clara para ele: explique de forma **direta**, com exemplos curtos.
7. Antes de grandes mudanças, apresente o plano em poucas linhas e espere confirmação.
8. Mantenha o código simples e legível. Evite frameworks, abstrações ou padrões que ele ainda não domine, a não ser que ele peça.

### O que o Claude Code pode fazer sem ressalvas

Estruturar pastas/pacotes, gerar boilerplate, explicar código existente, criar testes, achar bugs, refatorar com explicação, ajudar a documentar (README, Javadoc).

## 4. Convenções

- Pacote base: `br.com.brunowl.jcertificate` (ajustar conforme a estrutura atual do repositório).
- Classes em `PascalCase`, métodos e variáveis em `camelCase`; nomes em inglês no código, comentários e documentação em português.
- Nunca guardar senha em texto puro nem logar chaves ou senhas.
- Tratar exceções de criptografia e de I/O com mensagens claras para o usuário na GUI.
- Testes com JUnit para toda a camada de criptografia (assinar → verificar OK; alterar o arquivo → verificar falha; chave errada → falha).
- Commits pequenos e com mensagens descritivas.

## 5. Comandos úteis

```bash
mvn clean compile     # compilar
mvn test              # rodar testes
mvn clean package     # gerar o jar
```

## 6. Ao começar uma sessão

1. Leia o `README.md`, este arquivo e o `tasklist.md`.
2. Olhe a estrutura atual do repositório e o `pom.xml` para ver em que ponto o projeto está.
3. Compare com a tasklist e proponha a **próxima tarefa pendente**; confirme com o Bruno antes de sair escrevendo código.
