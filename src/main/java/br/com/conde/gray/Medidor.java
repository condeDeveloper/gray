package br.com.conde.gray;

import java.util.Locale;

/** O MEDIDOR: roda as varreduras e imprime as tabelas do README. */
public final class Medidor {

    private Medidor() {}

    public static void main(String[] argumentos) {
        Locale.setDefault(Locale.US);

        String passo = argumentos.length > 0 ? argumentos[0] : "tudo";

        switch (passo) {
            case "correcao" -> correcao();
            case "formula" -> formula();
            case "ciclo" -> ciclo();
            case "quantos" -> quantos();
            case "contador" -> contador();
            default -> {
                correcao();
                formula();
                ciclo();
                quantos();
                contador();
            }
        }
    }

    private static void titulo(String texto) {
        System.out.println();
        System.out.println(texto);
        System.out.println("=".repeat(texto.length()));
        System.out.println();
    }

    static void correcao() {
        titulo("1. a sequencia cumpre as tres promessas");

        System.out.print("a de 3 bits: ");
        for (int valor : Gray.sequencia(3)) System.out.print(Gray.emBinario(valor, 3) + " ");
        System.out.println();
        System.out.println();

        System.out.println("bits  valores  muda um bit  passa por todos  fecha o ciclo");

        for (Medidas.Correcao c : Medidas.correcao())
            if (c.bits() <= 5 || c.bits() % 4 == 0)
                System.out.printf("%4d  %7d  %11s  %15s  %13s%n", c.bits(), c.valores(),
                        c.mudaUmBit() ? "sim" : "NAO", c.passaPorTodos() ? "sim" : "NAO",
                        c.fechaOCiclo() ? "sim" : "NAO");

        System.out.println();
        System.out.println("a terceira coluna e a que quase nunca se diz junto: a sequencia e um");
        System.out.println("CICLO, nao uma lista. do ultimo valor ao primeiro tambem muda um bit");
        System.out.println("so, e e isso que faz ela servir num disco que GIRA.");
    }

    static void formula() {
        titulo("2. a formula de uma linha contra a construcao de gray");

        System.out.println("bits  valores  bate com o espelho  erros de ida e volta");

        for (Medidas.Formula f : Medidas.formula())
            if (f.bits() <= 5 || f.bits() % 4 == 0)
                System.out.printf("%4d  %7d  %18s  %20d%n", f.bits(), f.valores(),
                        f.batemComEspelho() ? "sim" : "NAO", f.errosDeIdaEVolta());

        System.out.println();
        System.out.println("gray descreveu a sequencia por ESPELHAMENTO: escreva a de n-1 bits,");
        System.out.println("depois ela de tras para frente com um 1 na frente.");
        System.out.println();
        System.out.println("a formula n ou-exclusivo (n deslocado um) nao se parece nada com");
        System.out.printf("isso, e da a MESMA sequencia. a volta nao erra em %d numeros.%n",
                1 << 20);
    }

    static void ciclo() {
        titulo("3. quantas sequencias cumprem a mesma promessa");

        System.out.println("bits  valores  quantos ciclos  o classico esta entre eles");

        for (Medidas.Ciclos c : Medidas.ciclo())
            System.out.printf("%4d  %7d  %14d  %25s%n", c.bits(), c.valores(), c.quantos(),
                    Medidas.oClassicoEhUmDeles(c.bits()) ? "sim" : "NAO");

        System.out.println();
        System.out.printf("com 4 bits existem %d maneiras de percorrer todos os valores%n",
                Medidas.ciclo(4).quantos());
        System.out.println("mudando um bit por vez e voltando ao comeco. o codigo de gray e UMA.");
        System.out.println();
        System.out.println("em linguagem de grafos, sao os ciclos hamiltonianos do cubo de n");
        System.out.println("dimensoes. chamar o classico de O codigo de gray esconde que ha");
        System.out.println("escolha, e a escolha importa quando se quer outra propriedade junto.");
    }

    static void quantos() {
        titulo("4. quantos bits mudam a cada passo");

        System.out.println("bits  binario em media  pior do binario  gray em media  pior do gray");

        for (Medidas.Mudanca m : Medidas.quantos())
            if (m.bits() <= 6 || m.bits() % 4 == 0)
                System.out.printf("%4d  %16.3f  %15d  %13.3f  %13d%n", m.bits(),
                        m.binarioEmMedia(), m.binarioNoPior(), m.grayEmMedia(), m.grayNoPior());

        System.out.println();
        System.out.println("contar em binario comum muda DOIS bits por passo em media, e esse");
        System.out.println("numero nao depende da largura: metade dos passos muda um bit, um");
        System.out.println("quarto muda dois, e a soma da serie e 2.");
        System.out.println();
        System.out.println("a media e tranquilizadora e e a coluna ERRADA de olhar. a que importa");
        System.out.println("e o pior caso, que e a largura inteira.");
    }

    static void contador() {
        titulo("5. o erro de leitura, que e a razao de o codigo existir");

        System.out.println("passando de 7 para 8, em binario de 4 bits: 0111 vira 1000.");
        System.out.println("se os sensores nao leem no mesmo instante, cada bit pode vir do");
        System.out.println("valor velho ou do novo. todas as leituras possiveis:");
        System.out.println();
        System.out.println("leitura  valor  erro em posicoes");

        for (Medidas.Leitura l : Medidas.contador())
            System.out.printf("   %s  %5d  %16d%n", l.binario(), l.leituraErrada(),
                    l.erroEmPosicoes());

        System.out.printf("%npior erro lendo BINARIO:  %d posicoes%n", Medidas.piorErroEmBinario());
        System.out.printf("pior erro lendo GRAY:     %d posicao%n", Medidas.piorErroEmGray());

        System.out.println();
        System.out.println("com gray, so um bit esta mudando, entao a leitura so pode dar um dos");
        System.out.println("dois valores vizinhos. o erro maximo e uma posicao, sempre.");
        System.out.println();
        System.out.println("nao e otimizacao nem elegancia: e a diferenca entre errar uma");
        System.out.println("posicao e ler um numero que nunca existiu.");
    }
}
