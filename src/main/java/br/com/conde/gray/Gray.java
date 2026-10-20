package br.com.conde.gray;

import java.util.ArrayList;
import java.util.List;

/**
 * O CÓDIGO DE GRAY, de 1947, que percorre todos os números mudando um bit por vez.
 *
 * <p>A sequência de 3 bits é 000, 001, 011, 010, 110, 111, 101, 100. De um para o próximo muda
 * sempre exatamente um bit, e do último para o primeiro também: a sequência é um ciclo
 * fechado, o que raramente se diz junto.
 *
 * <p>Ele existe por um motivo físico. Num disco que marca posição com trilhas de preto e
 * branco, passar de 011 para 100 em binário comum muda três bits de uma vez. Se os sensores
 * não lerem no mesmo instante, a posição lida pode ser qualquer coisa entre uma e outra. Com
 * um bit mudando por vez, o pior erro possível é uma posição.
 *
 * <p>A fórmula que gera tudo isso cabe numa linha: {@code n ou-exclusivo (n deslocado um para a
 * direita)}. A segunda medida confere que essa linha dá exatamente a sequência construída pela
 * regra de espelhamento, que é como Gray a descreveu.
 */
public final class Gray {

    private Gray() {}

    /** A fórmula de uma linha: o número ou-exclusivo com ele mesmo deslocado. */
    public static int codificar(int numero) {
        return numero ^ (numero >>> 1);
    }

    /**
     * A VOLTA, que não é de uma linha.
     *
     * <p>Decodificar exige desfazer o ou-exclusivo acumulando da esquerda para a direita, e é
     * por isso que o laço existe: cada bit do resultado depende de todos os bits mais altos.
     */
    public static int decodificar(int codigo) {
        int numero = codigo;

        for (int deslocamento = 1; deslocamento < 32; deslocamento <<= 1)
            numero ^= numero >>> deslocamento;

        return numero;
    }

    /** A sequência inteira de n bits, pela fórmula. */
    public static int[] sequencia(int bits) {
        if (bits < 1 || bits > 20)
            throw new IllegalArgumentException("bits entre 1 e 20: " + bits);

        int quantos = 1 << bits;

        int[] saida = new int[quantos];

        for (int indice = 0; indice < quantos; indice++) saida[indice] = codificar(indice);

        return saida;
    }

    /**
     * A MESMA SEQUÊNCIA, pela regra de espelhamento, que é como Gray a descreveu.
     *
     * <p>Para n bits: escreva a sequência de n-1 bits, depois escreva ela de trás para frente
     * com um 1 na frente. Os de cima ganham um 0 na frente, que não muda nada.
     *
     * <p>Esta construção não usa a fórmula, e por isso ela serve de juiz dela.
     */
    public static int[] porEspelho(int bits) {
        int[] atual = {0, 1};

        for (int nivel = 2; nivel <= bits; nivel++) {
            int quantos = atual.length;

            int[] proximo = new int[quantos * 2];

            for (int indice = 0; indice < quantos; indice++) {
                proximo[indice] = atual[indice];

                /* A metade de baixo e a de cima ao contrario, com o bit novo ligado. */
                proximo[quantos + indice] = atual[quantos - 1 - indice] | (1 << (nivel - 1));
            }

            atual = proximo;
        }

        return bits == 1 ? new int[] {0, 1} : atual;
    }

    /** Em quantos bits dois números diferem. */
    public static int bitsDiferentes(int a, int b) {
        return Integer.bitCount(a ^ b);
    }

    /** A sequência muda exatamente um bit a cada passo, e fecha o ciclo no fim. */
    public static boolean ehCodigoDeGray(int[] sequencia) {
        for (int indice = 0; indice + 1 < sequencia.length; indice++)
            if (bitsDiferentes(sequencia[indice], sequencia[indice + 1]) != 1) return false;

        return true;
    }

    /** Além de mudar um bit por passo, volta ao começo mudando um bit só. */
    public static boolean ehCiclo(int[] sequencia) {
        return ehCodigoDeGray(sequencia)
                && bitsDiferentes(sequencia[sequencia.length - 1], sequencia[0]) == 1;
    }

    /** A sequência passa por todo número exatamente uma vez. */
    public static boolean passaPorTodos(int[] sequencia, int bits) {
        boolean[] visto = new boolean[1 << bits];

        for (int valor : sequencia) {
            if (valor < 0 || valor >= visto.length || visto[valor]) return false;

            visto[valor] = true;
        }

        for (boolean passou : visto) if (!passou) return false;

        return true;
    }

    /**
     * TODOS OS CAMINHOS que mudam um bit por vez e passam por tudo, voltando ao começo.
     *
     * <p>São os ciclos hamiltonianos do cubo de n dimensões. O código de Gray clássico é UM
     * deles, e a terceira medida conta quantos existem: com 4 bits já são milhares.
     *
     * <p>Fixar o começo em zero e o segundo elemento tira as repetições por rotação e por
     * espelho que não interessam.
     */
    public static int quantosCiclos(int bits) {
        int quantos = 1 << bits;

        boolean[] usado = new boolean[quantos];

        int[] caminho = new int[quantos];

        usado[0] = true;
        caminho[0] = 0;

        return contar(caminho, usado, 1, bits);
    }

    private static int contar(int[] caminho, boolean[] usado, int posicao, int bits) {
        if (posicao == caminho.length)
            return bitsDiferentes(caminho[posicao - 1], caminho[0]) == 1 ? 1 : 0;

        int total = 0;

        for (int bit = 0; bit < bits; bit++) {
            int proximo = caminho[posicao - 1] ^ (1 << bit);

            if (usado[proximo]) continue;

            usado[proximo] = true;
            caminho[posicao] = proximo;

            total += contar(caminho, usado, posicao + 1, bits);

            usado[proximo] = false;
        }

        return total;
    }

    public static String emBinario(int valor, int bits) {
        StringBuilder saida = new StringBuilder();

        for (int bit = bits - 1; bit >= 0; bit--) saida.append((valor >> bit) & 1);

        return saida.toString();
    }
}
