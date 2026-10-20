package br.com.conde.gray;

import java.util.ArrayList;
import java.util.List;

/**
 * AS MEDIDAS: a fórmula contra a construção original, e o código clássico contra todos os
 * outros que cumprem a mesma promessa.
 */
public final class Medidas {

    private Medidas() {}

    /* ===================== 1. correcao ===================== */

    public record Correcao(int bits, int valores, boolean mudaUmBit, boolean passaPorTodos,
            boolean fechaOCiclo) {}

    private static List<Correcao> correcao;

    /**
     * A SEQUÊNCIA CUMPRE AS TRÊS PROMESSAS.
     *
     * <p>Muda um bit por passo, passa por todo número exatamente uma vez, e volta ao começo
     * mudando um bit só. As três são conferidas, e a terceira é a que quase nunca se diz:
     * a sequência é um CICLO, não uma lista.
     *
     * <p>É a terceira que faz ela servir num disco que gira.
     */
    public static List<Correcao> correcao() {
        if (correcao != null) return correcao;

        List<Correcao> saida = new ArrayList<>();

        for (int bits = 1; bits <= 14; bits++) {
            int[] sequencia = Gray.sequencia(bits);

            saida.add(new Correcao(bits, sequencia.length, Gray.ehCodigoDeGray(sequencia),
                    Gray.passaPorTodos(sequencia, bits), Gray.ehCiclo(sequencia)));
        }

        return correcao = List.copyOf(saida);
    }

    public static Correcao correcao(int bits) {
        return correcao().stream().filter(x -> x.bits() == bits).findFirst().orElseThrow();
    }

    /* ===================== 2. formula ===================== */

    public record Formula(int bits, int valores, boolean batemComEspelho, int errosDeIdaEVolta) {}

    private static List<Formula> formula;

    /**
     * A FÓRMULA DE UMA LINHA CONTRA A CONSTRUÇÃO DE GRAY.
     *
     * <p>Gray descreveu a sequência por espelhamento: escreva a de n-1 bits, depois ela de
     * trás para frente com um 1 na frente. A fórmula {@code n xor (n >> 1)} não se parece
     * nada com isso.
     *
     * <p>Elas dão a mesma sequência, valor a valor, em todos os tamanhos medidos. E a volta,
     * que precisa de laço, desfaz a ida sem erro em mais de um milhão de números.
     */
    public static List<Formula> formula() {
        if (formula != null) return formula;

        List<Formula> saida = new ArrayList<>();

        for (int bits = 1; bits <= 14; bits++) {
            int[] porFormula = Gray.sequencia(bits);
            int[] porEspelho = Gray.porEspelho(bits);

            int erros = 0;

            for (int numero = 0; numero < porFormula.length; numero++)
                if (Gray.decodificar(Gray.codificar(numero)) != numero) erros++;

            saida.add(new Formula(bits, porFormula.length,
                    java.util.Arrays.equals(porFormula, porEspelho), erros));
        }

        return formula = List.copyOf(saida);
    }

    /** Erros de ida e volta em todo número de 20 bits. */
    public static int errosEmUmMilhao() {
        int erros = 0;

        for (int numero = 0; numero < (1 << 20); numero++)
            if (Gray.decodificar(Gray.codificar(numero)) != numero) erros++;

        return erros;
    }

    /* ===================== 3. ciclo ===================== */

    public record Ciclos(int bits, int valores, int quantos) {}

    private static List<Ciclos> ciclo;

    /**
     * QUANTAS SEQUÊNCIAS CUMPREM A MESMA PROMESSA.
     *
     * <p>O código de Gray não é a única maneira de percorrer todos os números mudando um bit
     * por vez. Ele é UMA, e com 4 bits existem 2.688 outras.
     *
     * <p>Em linguagem de grafos, são os ciclos hamiltonianos do cubo de n dimensões, contados
     * a partir do zero. Chamar o clássico de "o código de Gray" esconde que existe escolha,
     * e a escolha importa quando a aplicação quer outra propriedade além de mudar um bit.
     */
    public static List<Ciclos> ciclo() {
        if (ciclo != null) return ciclo;

        List<Ciclos> saida = new ArrayList<>();

        for (int bits = 2; bits <= 4; bits++)
            saida.add(new Ciclos(bits, 1 << bits, Gray.quantosCiclos(bits)));

        return ciclo = List.copyOf(saida);
    }

    public static Ciclos ciclo(int bits) {
        return ciclo().stream().filter(x -> x.bits() == bits).findFirst().orElseThrow();
    }

    /** O clássico está entre os ciclos contados. */
    public static boolean oClassicoEhUmDeles(int bits) {
        return Gray.ehCiclo(Gray.sequencia(bits))
                && Gray.passaPorTodos(Gray.sequencia(bits), bits);
    }

    /* ===================== 4. quantos ===================== */

    public record Mudanca(int bits, double binarioEmMedia, int binarioNoPior,
            double grayEmMedia, int grayNoPior) {}

    private static List<Mudanca> quantos;

    /**
     * QUANTOS BITS MUDAM A CADA PASSO.
     *
     * <p>Contar em binário comum muda 2 bits por passo, em média, e esse número não depende da
     * largura: ele converge para 2 e fica lá. A metade dos passos muda um bit, um quarto muda
     * dois, e a soma dessa série é 2.
     *
     * <p>A média é tranquilizadora e é a coluna errada para olhar. A coluna que importa é o
     * PIOR caso, que é a largura inteira: passar de 0111 para 1000 muda os quatro bits. É
     * exatamente aí que um sensor lê posição que não existe.
     */
    public static List<Mudanca> quantos() {
        if (quantos != null) return quantos;

        List<Mudanca> saida = new ArrayList<>();

        for (int bits = 3; bits <= 16; bits++) {
            int valores = 1 << bits;

            long somaBinario = 0;
            long somaGray = 0;

            int piorBinario = 0;
            int piorGray = 0;

            for (int numero = 0; numero + 1 < valores; numero++) {
                int binario = Gray.bitsDiferentes(numero, numero + 1);

                somaBinario += binario;
                piorBinario = Math.max(piorBinario, binario);

                int gray = Gray.bitsDiferentes(Gray.codificar(numero),
                        Gray.codificar(numero + 1));

                somaGray += gray;
                piorGray = Math.max(piorGray, gray);
            }

            saida.add(new Mudanca(bits, somaBinario / (double) (valores - 1), piorBinario,
                    somaGray / (double) (valores - 1), piorGray));
        }

        return quantos = List.copyOf(saida);
    }

    public static Mudanca quantos(int bits) {
        return quantos().stream().filter(x -> x.bits() == bits).findFirst().orElseThrow();
    }

    /* ===================== 5. contador ===================== */

    public record Leitura(String binario, int posicaoCerta, int leituraErrada,
            int erroEmPosicoes) {}

    /**
     * O ERRO DE LEITURA, que é a razão de o código existir.
     *
     * <p>Num disco que gira, os sensores não leem no mesmo instante. Se a posição está mudando
     * de 0111 para 1000 e um sensor ainda vê o valor velho, a leitura pode sair qualquer
     * coisa entre as duas: 0000, 1111, 1001, qualquer combinação.
     *
     * <p>Com Gray, só um bit está mudando, então a leitura só pode dar um dos dois valores
     * vizinhos. O erro máximo é uma posição, sempre.
     */
    public static List<Leitura> contador() {
        List<Leitura> saida = new ArrayList<>();

        int bits = 4;

        /* Passando de 7 para 8 em binario: 0111 vira 1000, e os quatro bits mudam. */
        int de = 7;
        int para = 8;

        for (int mistura = 0; mistura < 16; mistura++) {
            /* Cada bit vem do valor velho ou do novo, conforme a mistura. */
            int lido = 0;

            for (int bit = 0; bit < bits; bit++) {
                int origem = (mistura & (1 << bit)) != 0 ? para : de;

                lido |= origem & (1 << bit);
            }

            saida.add(new Leitura(Gray.emBinario(lido, bits), para, lido,
                    Math.abs(lido - para)));
        }

        return saida;
    }

    /** O pior erro possível lendo binário comum no meio da virada. */
    public static int piorErroEmBinario() {
        int pior = 0;

        for (Leitura leitura : contador()) pior = Math.max(pior, leitura.erroEmPosicoes());

        return pior;
    }

    /** O pior erro possível lendo Gray no meio da virada: um bit só muda. */
    public static int piorErroEmGray() {
        int deGray = Gray.codificar(7);
        int paraGray = Gray.codificar(8);

        /* So um bit difere, entao a leitura so pode dar um dos dois. */
        int diferentes = Gray.bitsDiferentes(deGray, paraGray);

        if (diferentes != 1) throw new IllegalStateException("gray mudou mais de um bit");

        return 1;
    }
}
