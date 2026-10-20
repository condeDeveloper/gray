package br.com.conde.gray;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * A fórmula, a construção por espelho, e as três propriedades.
 *
 * <p>O erro perigoso aqui seria uma sequência que muda um bit por passo e **pula** algum
 * valor. Ela passaria por qualquer teste que só olhasse pares consecutivos, e seria inútil
 * como contador. Por isso {@code passaPorTodos} é conferido separado.
 */
class GrayTest {

    @Test
    @DisplayName("a sequencia de 3 bits, escrita a mao")
    void tresBits() {
        int[] esperada = {0b000, 0b001, 0b011, 0b010, 0b110, 0b111, 0b101, 0b100};

        assertArrayEquals(esperada, Gray.sequencia(3));
    }

    @Test
    @DisplayName("a formula e uma linha: n ou-exclusivo n deslocado")
    void formula() {
        assertEquals(0, Gray.codificar(0));
        assertEquals(1, Gray.codificar(1));
        assertEquals(3, Gray.codificar(2));
        assertEquals(2, Gray.codificar(3));
        assertEquals(6, Gray.codificar(4));
    }

    @Test
    @DisplayName("fora da faixa de bits nao tem sequencia")
    void foraDaFaixa() {
        assertThrows(IllegalArgumentException.class, () -> Gray.sequencia(0));
        assertThrows(IllegalArgumentException.class, () -> Gray.sequencia(21));
    }

    @Test
    @DisplayName("DECODIFICAR DESFAZ CODIFICAR, em mais de um milhao de numeros")
    void idaEVolta() {
        for (int numero = 0; numero < (1 << 20); numero++)
            assertEquals(numero, Gray.decodificar(Gray.codificar(numero)), "numero " + numero);
    }

    @Test
    @DisplayName("A FORMULA E O ESPELHO dao a mesma sequencia")
    void formulaBateComEspelho() {
        for (int bits = 1; bits <= 14; bits++)
            assertArrayEquals(Gray.sequencia(bits), Gray.porEspelho(bits), bits + " bits");
    }

    @Test
    @DisplayName("muda um bit por passo, e tambem do ultimo para o primeiro")
    void asTresPromessas() {
        for (int bits = 1; bits <= 12; bits++) {
            int[] sequencia = Gray.sequencia(bits);

            assertTrue(Gray.ehCodigoDeGray(sequencia), bits + " bits");
            assertTrue(Gray.ehCiclo(sequencia), bits + " bits: nao fecha");
            assertTrue(Gray.passaPorTodos(sequencia, bits), bits + " bits: pulou valor");
        }
    }

    @Test
    @DisplayName("e o teste de 'muda um bit' NAO basta sozinho")
    void mudarUmBitNaoBasta() {
        /* Uma sequencia que vai e volta: muda um bit por passo e nao passa por tudo. */
        int[] vaiEVolta = {0, 1, 0, 1, 0, 1, 0, 1};

        assertTrue(Gray.ehCodigoDeGray(vaiEVolta), "muda um bit por passo");

        assertFalse(Gray.passaPorTodos(vaiEVolta, 3), "mas nao passa por todos");
    }

    @Test
    @DisplayName("contar bits diferentes e simetrico e vale zero para iguais")
    void bitsDiferentes() {
        assertEquals(0, Gray.bitsDiferentes(5, 5));
        assertEquals(1, Gray.bitsDiferentes(0, 1));
        assertEquals(4, Gray.bitsDiferentes(0b0111, 0b1000));

        assertEquals(Gray.bitsDiferentes(7, 8), Gray.bitsDiferentes(8, 7));
    }

    @Test
    @DisplayName("o salto de 7 para 8 muda quatro bits em binario e um em gray")
    void oSaltoDoSete() {
        assertEquals(4, Gray.bitsDiferentes(7, 8));

        assertEquals(1, Gray.bitsDiferentes(Gray.codificar(7), Gray.codificar(8)));
    }

    @Test
    @DisplayName("escrever em binario respeita a largura pedida")
    void emBinario() {
        assertEquals("0101", Gray.emBinario(5, 4));
        assertEquals("101", Gray.emBinario(5, 3));
        assertEquals("00000101", Gray.emBinario(5, 8));

        assertEquals(List.of("000", "001", "011"),
                List.of(Gray.emBinario(0, 3), Gray.emBinario(1, 3), Gray.emBinario(3, 3)));
    }
}
