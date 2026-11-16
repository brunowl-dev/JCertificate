# Tasklist — JCertificate

Lista de funcionalidades por versão. Marque `[x]` ao concluir. Só avance para a próxima versão quando a anterior estiver fechada.

Legenda: `[ ]` pendente · `[x]` concluído · ❓ decisão em aberto (definir antes de implementar)

---

## V0 — Preparação do projeto

- [x] Criar projeto Maven no Eclipse (`jcertificate-app`)
- [x] Criar repositório no GitHub (`brunowl-dev/JCertificate`)
- [x] Adicionar dependência `sqlite-jdbc` no `pom.xml`
- [x] Configurar `pom.xml` (Java 21, encoding UTF-8, JUnit)
- [ ] Definir estrutura de pacotes (`br.com.brunowl.jcertificate.cripto`, `.modelo`, `.persistencia`, `.ui`)
- [x] Criar `.gitignore` (target/, banco .db, arquivos da IDE)
- [x] README inicial com descrição, escopo por versão e aviso de que é uma **simulação** (sem autoridade certificadora real)

---

## V1 — MVP ponta a ponta (assinar e verificar arquivos)

### 1. Criptografia (testar isoladamente, sem GUI)

> Observação: o algoritmo `SHA256withRSA` **já calcula o SHA-256 internamente** antes de assinar.
> Não se calcula o hash separadamente para depois assinar (isso geraria hash duplo):
> os bytes do arquivo vão direto para `Signature.update(...)`.

- [ ] Gerar par de chaves RSA 2048 (classe `GeradorDeChaves`)
- [x] Assinar bytes com a chave privada (`Assinador`)
- [x] Verificar assinatura de bytes com a chave pública (`Verificador`)
- [ ] Fixar o algoritmo como constante dentro das classes (não receber `String algoritmo` por parâmetro)
- [ ] Assinar/verificar **arquivo** lendo em blocos (`InputStream` + `update` em loop), sem carregar o arquivo inteiro na memória
- [ ] Derivar/armazenar senha com PBKDF2WithHmacSHA256 (salt aleatório por usuário, muitas iterações)
- [ ] Cifrar/decifrar a chave privada com AES-GCM, usando chave derivada da senha (salt próprio, separado do salt do login)
- [ ] Testes JUnit:
  - [x] assinar → verificar = válido
  - [x] mensagem alterada após assinar → inválido
  - [x] chave nula → `InvalidKeyException`
  - [ ] chave pública de outro usuário → inválido
  - [ ] assinatura corrompida com tamanho errado → `SignatureException` (tratar como "inválida" na GUI)
  - [ ] arquivo: assinar → verificar = válido; alterar 1 byte do arquivo → inválido
  - [ ] senha correta/incorreta (PBKDF2)
  - [ ] chave privada: cifrar → decifrar com senha correta = mesma chave; senha errada → falha

### 2. Persistência (SQLite)

- [ ] Criar banco e tabela de usuários (nome de usuário, hash da senha, salt, chave pública, chave privada cifrada, salt/IV da chave privada)
- [ ] Cadastrar usuário
- [ ] Buscar usuário por nome
- [ ] Listar usuários (necessário para identificar quem assinou)
- [x] Definir como guardar a chave privada → **no banco, cifrada com AES-GCM com chave derivada da senha**

### 3. Contas e autenticação

- [ ] Criar conta (usuário + senha), gerando as chaves automaticamente
- [ ] Impedir nome de usuário duplicado
- [ ] Login (usuário + senha)
- [ ] Logout
- [ ] Regra: só assina quem estiver logado, usando a chave privada do próprio usuário (ninguém digita chave manualmente)

### 4. Assinar arquivo

- [ ] Selecionar qualquer arquivo (JFileChooser)
- [ ] Assinar com a chave privada do usuário logado (pede a senha para decifrar a chave)
- [x] Definir onde guardar a assinatura → **arquivo `.sig` ao lado do original, contendo o nome do assinante**
- [ ] Definir o formato do conteúdo do `.sig` (nome do usuário + assinatura)
- [ ] Mostrar mensagem de sucesso

### 5. Verificar arquivo

- [ ] Selecionar arquivo e seu `.sig`
- [ ] Verificar sem precisar estar logado
- [ ] Assinatura válida: mostrar **qual usuário assinou**
- [ ] Arquivo modificado ou assinatura inválida: mostrar claramente "assinatura inválida / arquivo modificado"
- [ ] Assinatura de usuário desconhecido: tratar e informar

### 6. Interface (JFrame/Swing)

- [ ] Tela de login
- [ ] Tela de cadastro
- [ ] Tela principal (assinar / verificar)
- [ ] Mensagens de erro claras (arquivo inexistente, senha errada, banco indisponível)

### 7. Fechamento da V1

- [ ] Revisar tratamento de exceções
- [ ] Atualizar README (como rodar, como usar, limitações, aviso de simulação)
- [ ] Capturas de tela no README
- [ ] Gerar o jar executável (`mvn clean package`)
- [ ] Tag/release `v1.0` no GitHub

---

## V2 — Certificado de verdade (simulado)

- [ ] Certificado do usuário: nome, chave pública, número de série, emissão e validade, assinados por uma "AC da aplicação" (par de chaves raiz da app)
- [ ] Verificação checa a cadeia: assinatura do arquivo, certificado do assinante e validade
- [ ] Expiração e revogação de certificados
- [ ] Carimbo de tempo registrado na assinatura
- [ ] Troca de senha (recifrando a chave privada)
- [ ] Bloqueio temporário após tentativas de login falhas

---

## V3 — Extensões

- [ ] Troca de mensagens entre usuários, válidas apenas com certificado válido
- [ ] Assinar automaticamente a mensagem com a chave privada do remetente
- [ ] Caixa de entrada com indicação de "assinatura válida" ou "inválida"
- [ ] Persistir mensagens no SQLite
- [ ] Exportar/importar certificado e chave pública
- [ ] Refinamento da interface

> Os detalhes da V2 e da V3 serão refinados quando a V1 fechar.

---

## Ideias sem versão definida

- (anote aqui qualquer ideia nova antes de decidir em qual versão entra)
