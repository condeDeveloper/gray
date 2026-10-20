# Código de Gray

O código de Gray, de 1947, em Java 21, com a fórmula de uma linha medida contra a construção original e o código clássico contado entre todos os que cumprem a mesma promessa.

Ele não é **o** código de Gray. Com 4 bits existem **2.688** maneiras de percorrer todos os valores mudando um bit por vez e voltando ao começo. A clássica é uma delas.

```
mvn -q exec:java -Dexec.args=ciclo
```

## As medidas

### 1. A sequência cumpre as três promessas

```
a de 3 bits:  000 001 011 010 110 111 101 100
```

| bits | valores | muda um bit | passa por todos | fecha o ciclo |
|---:|---:|---|---|---|
| 3 | 8 | sim | sim | sim |
| 4 | 16 | sim | sim | sim |
| 12 | 4.096 | sim | sim | sim |

A terceira coluna é a que quase nunca se diz junto: a sequência é um **ciclo**, não uma lista. Do último valor ao primeiro também muda um bit só, e é isso que faz ela servir num disco que **gira**.

E "muda um bit por passo" não basta sozinho como teste: a sequência `0, 1, 0, 1, 0, 1` passa nele e pula metade dos valores. Por isso as três são conferidas separadas.

### 2. A fórmula de uma linha contra a construção de Gray

| bits | valores | bate com o espelho | erros de ida e volta |
|---:|---:|---|---:|
| 4 | 16 | sim | 0 |
| 8 | 256 | sim | 0 |
| 12 | 4.096 | sim | 0 |

Gray descreveu a sequência por **espelhamento**: escreva a de n-1 bits, depois ela de trás para frente com um 1 na frente.

A fórmula `n ^ (n >> 1)` não se parece nada com isso, e dá a mesma sequência, valor a valor. A volta, que precisa de laço porque cada bit depende de todos os mais altos, desfaz a ida sem erro em 1.048.576 números.

### 3. Quantas sequências cumprem a mesma promessa

| bits | valores | quantos ciclos | o clássico está entre eles |
|---:|---:|---:|---|
| 2 | 4 | 2 | sim |
| 3 | 8 | 12 | sim |
| 4 | 16 | **2.688** | sim |

De 12 para 2.688 ao ganhar um bit.

Em linguagem de grafos, são os ciclos hamiltonianos do cubo de n dimensões. Chamar o clássico de "o código de Gray" esconde que existe escolha, e a escolha importa quando a aplicação quer outra propriedade junto com "muda um bit".

### 4. Quantos bits mudam a cada passo

| bits | binário em média | pior do binário | Gray em média | pior do Gray |
|---:|---:|---:|---:|---:|
| 4 | 1,733 | 4 | 1,000 | 1 |
| 8 | 1,969 | 8 | 1,000 | 1 |
| 16 | 2,000 | 16 | 1,000 | 1 |

Contar em binário comum muda **dois bits por passo em média**, e esse número não depende da largura: metade dos passos muda um bit, um quarto muda dois, e a soma da série é 2.

A média é tranquilizadora e é a coluna errada de olhar. A que importa é o **pior caso**, que é a largura inteira.

### 5. O erro de leitura, que é a razão de o código existir

Passando de 7 para 8 em binário de 4 bits, `0111` vira `1000`. Se os sensores não leem no mesmo instante, cada bit pode vir do valor velho ou do novo. As 16 leituras possíveis:

| leitura | valor | erro em posições |
|---|---:|---:|
| 1000 | 8 | 0 |
| 1001 | 9 | 1 |
| 0111 | 7 | 1 |
| 1111 | 15 | 7 |
| 0000 | 0 | **8** |

```
pior erro lendo binário:  8 posições
pior erro lendo Gray:     1 posição
```

Com Gray só um bit está mudando, então a leitura só pode dar um dos dois valores vizinhos. O erro máximo é uma posição, sempre.

Não é otimização nem elegância: é a diferença entre errar uma posição e ler um número que nunca existiu.

## O que as medidas me corrigiram

**O código de Gray é um de 2.688.** Eu tratava a sequência clássica como se fosse a única que resolve o problema. Ela é a mais fácil de gerar, e com 4 bits há quase três mil outras que mudam um bit por vez e fecham o ciclo.

**A média de bits que mudam em binário é 2, e esse número não ajuda em nada.** Ele não piora com a largura, o que soa como boa notícia. O pior caso piora linearmente, e o pior caso é o único que o disco encontra na hora errada.

**"Muda um bit por passo" não define a sequência.** Eu ia testar só isso. Uma sequência que vai e volta entre dois valores passa nesse teste e é inútil, e foi preciso conferir separado que ela passa por todos os valores e fecha o ciclo.

## Rodar

```bash
mvn -q verify                              # 19 testes
mvn -q exec:java                           # todas as medidas
mvn -q exec:java -Dexec.args=correcao      # 1
mvn -q exec:java -Dexec.args=formula       # 2
mvn -q exec:java -Dexec.args=ciclo         # 3
mvn -q exec:java -Dexec.args=quantos       # 4
mvn -q exec:java -Dexec.args=contador      # 5
```

Java 21, sem dependência fora do JUnit. Nenhum número deste README foi digitado à mão: todos saem de `Medidas`, e os testes conferem os que aparecem escritos aqui.
