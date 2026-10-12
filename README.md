# JCertificate

Aplicação desktop em **Java** (Swing/JFrame) que **simula** um sistema de certificação e assinatura digital: cada usuário tem uma conta e um par de chaves gerado pela aplicação, pode assinar **qualquer arquivo** e qualquer pessoa pode **verificar** quem assinou e se o arquivo foi alterado depois.

> ⚠️ **Este projeto é uma simulação educacional.** Não existe Autoridade Certificadora (AC) real, e nada aqui tem validade jurídica. O "dono" de uma assinatura é o usuário cadastrado na própria aplicação. Não use para proteger dados reais.

## Requisitos do projeto

- Autenticação própria (usuário e senha), com usuários persistidos em **SQLite**.
- Cada usuário possui seu "certificado digital": conta com senha própria e par de chaves (pública e privada) **gerado pela aplicação**.
- A **chave privada é de uso exclusivo do usuário**.
- Assinar **qualquer arquivo**.
- Verificar e validar arquivos assinados pela aplicação, indicando o assinante ou informando que a assinatura é inválida / o arquivo foi modificado.
- Interface gráfica em **JFrame (Swing)**.

## Versões

### V1: MVP ponta a ponta

O mínimo para o fluxo completo funcionar, já atendendo aos requisitos acima.

- [x] **Projeto Maven configurado** (Java 21 + driver JDBC do SQLite)
- [ ] **Camada de criptografia isolada e testada** (gerar par RSA 2048, assinar, verificar, detectar alteração de 1 byte)
- [ ] **Hash de arquivos em blocos** (qualquer tamanho de arquivo, sem carregar tudo na memória)
- [ ] **Banco SQLite** com tabela de usuários
- [ ] **Cadastro e login**: senha armazenada como hash PBKDF2WithHmacSHA256 + salt (ver "Decisões de design")
- [ ] **Geração do par de chaves no cadastro**: chave pública guardada em claro; chave privada guardada **cifrada** com chave derivada da senha do usuário
- [ ] **Assinar arquivo**: exige login e senha, gera arquivo `.sig` ao lado do original
- [ ] **Verificar arquivo**: lê o `.sig`, encontra o usuário no banco e mostra "assinado por X" ou "assinatura INVÁLIDA / arquivo modificado"
- [ ] **Telas JFrame**: login/cadastro, assinar, verificar
- [ ] **Testes automatizados** da camada de criptografia
- [ ] README final com instruções de execução

### V2: Certificado de verdade (simulado)

Hoje a "identidade" é só um nome ligado a uma chave. Nesta versão a identidade passa a ser **atestada**.

- [ ] **Certificado do usuário**: nome, chave pública, número de série, data de emissão e validade, **assinados por uma "AC da aplicação"** (par de chaves raiz da própria app)
- [ ] Verificação passa a checar a **cadeia**: assinatura do arquivo, certificado do assinante e validade
- [ ] **Expiração e revogação** de certificados
- [ ] **Carimbo de tempo** registrado na assinatura
- [ ] Troca de senha (recifrando a chave privada)
- [ ] Bloqueio temporário após tentativas de login falhas

### V3: Extensões

- [ ] **Troca de mensagens entre usuários**, válidas apenas com certificado válido
- [ ] Exportar/importar certificado e chave pública
- [ ] Refinamento da interface e empacotamento em `.jar` executável

### Fora do escopo

AC real, ICP-Brasil, certificados X.509 reais, validade jurídica.

## Como funciona (conceitos)

- **Hash (SHA-256)**: "impressão digital" de tamanho fixo do arquivo; qualquer mudança gera um hash completamente diferente.
- **Par de chaves (RSA)**: a chave **privada** assina e fica só com o dono; a **pública** verifica e pode ser compartilhada.
- **Assinatura**: o hash do arquivo assinado com a chave privada. Quem tem a chave pública confere se a assinatura corresponde ao arquivo e ao dono da chave.

Criptografia com a API padrão do JDK (`java.security`, `javax.crypto`). A única dependência externa prevista é o **driver JDBC do SQLite**.

## Decisões de design

| Tema | Decisão | Status |
|---|---|---|
| Persistência de usuários | SQLite (driver `sqlite-jdbc`) | Decidido |
| Senha de login | **PBKDF2WithHmacSHA256** (nativo do JDK) com salt aleatório por usuário e muitas iterações, em vez de SHA-256 simples | Decidido |
| Chave privada | Guardada no banco **cifrada** (AES-GCM) com chave derivada da senha do usuário, com salt próprio, separada do hash de login | Proposta |
| Local da assinatura | Arquivo `.sig` ao lado do original | Proposta |
| Identificação do assinante | O `.sig` inclui o nome do usuário; a verificação busca a chave pública dele no banco | Proposta |
| Gerenciador de dependências | Maven | Decidido |

## Limitações conhecidas

- Sem AC real: qualquer pessoa pode se cadastrar com qualquer nome.
- Na V1, o "certificado" é apenas conta + par de chaves; certificado atestado só na V2.
- Na V1, sem carimbo de tempo, revogação ou expiração.
- A segurança depende da força da senha do usuário.

## Estrutura do projeto

```
jcertificate/
├── pom.xml
├── README.md
├── .gitignore
└── src/main/java/
    ├── modelo/          # Usuario
    ├── cripto/          # GeradorDeChaves, Assinador, Verificador, ProtecaoDeSenha (sem Swing)
    ├── persistencia/    # Conexão SQLite, RepositorioUsuarios
    └── ui/              # Telas Swing
```

Os pacotes acima são o plano e serão criados conforme o desenvolvimento avança. A camada `cripto` não conhece a interface gráfica nem o banco: recebe bytes e devolve bytes ou `boolean`, o que permite testá-la isoladamente.

## Como executar

_A preencher quando a V1 estiver rodando._ Requisitos: JDK 21 ou superior e Maven (ou uma IDE com suporte a Maven, como Eclipse ou IntelliJ).

## Autor

Bruno Wai Lu: projeto de portfólio, desenvolvido durante o curso de ADS na FATEC Rubens Lara.