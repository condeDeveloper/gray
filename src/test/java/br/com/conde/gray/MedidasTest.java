package br.com.conde.gray;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/** Os números que aparecem escritos no README, travados em teste. */
class MedidasTest {

    /* ===================== 1. correcao ===================== */

    @Test
    @DisplayName("as tres promessas valem em todo tamanho ate 14 bits")
    void tresPromessas() {
        for (Medidas.Correcao c : Medidas.correcao()) {
            assertTrue(c.mudaUmBit(), c.bits() + " bits");
            assertTrue(c.passaPorTodos(), c.bits() + " bits");
            assertTrue(c.fechaOCiclo(), c.bits() + " bits");

            assertEquals(1 << c.bits(), c.valores(), c.bits() + " bits");
        }
    }

    /* ===================== 2. formula ===================== */

    @Test
    @DisplayName("a formula bate com o espelho em todo tamanho, e a volta nao erra")
    void formulaBate() {
        for (Medidas.Formula f : Medidas.formula()) {
            assertTrue(f.batemComEspelho(), f.bits() + " bits");
            assertEquals(0, f.errosDeIdaEVolta(), f.bits() + " bits");
        }

        assertEquals(0, Medidas.errosEmUmMilhao());
    }

    /* ===================== 3. ciclo ===================== */

    @Test
    @DisplayName("COM 4 BITS existem 2.688 sequencias que cumprem a mesma promessa")
    void doisMilSeiscentosEOitentaEOito() {
        assertEquals(2, Medidas.ciclo(2).quantos());
        assertEquals(12, Medidas.ciclo(3).quantos());
        assertEquals(2688, Medidas.ciclo(4).quantos());

        /* E o classico e uma delas. */
        for (int bits = 2; bits <= 4; bits++)
            assertTrue(Medidas.oClassicoEhUmDeles(bits), bits + " bits");
    }

    @Test
    @DisplayName("e a contagem explode: de 12 para 2.688 ao ganhar um bit")
    void contagemExplode() {
        assertTrue(Medidas.ciclo(4).quantos() > 200 * Medidas.ciclo(3).quantos());
    }

    /* ===================== 4. quantos ===================== */

    @Test
    @DisplayName("O BINARIO MUDA DOIS BITS EM MEDIA, e isso nao melhora com a largura")
    void binarioMudaDois() {
        for (Medidas.Mudanca m : Medidas.quantos()) {
            assertTrue(m.binarioEmMedia() > 1.5, m.bits() + " bits");
            assertTrue(m.binarioEmMedia() < 2.01, m.bits() + " bits");
        }

        /* Com 16 bits ja esta em 2,000. */
        assertEquals(2.000, Medidas.quantos(16).binarioEmMedia(), 0.001);

        /* E com 3 bits ainda nao chegou la: a serie converge por baixo. */
        assertTrue(Medidas.quantos(3).binarioEmMedia() < Medidas.quantos(16).binarioEmMedia());
    }

    @Test
    @DisplayName("mas o PIOR do binario e a largura inteira, e o do gray e sempre 1")
    void piorCaso() {
        for (Medidas.Mudanca m : Medidas.quantos()) {
            assertEquals(m.bits(), m.binarioNoPior(), m.bits() + " bits");

            assertEquals(1, m.grayNoPior(), m.bits() + " bits");
            assertEquals(1.0, m.grayEmMedia(), 1e-12, m.bits() + " bits");
        }
    }

    /* ===================== 5. contador ===================== */

    @Test
    @DisplayName("LENDO BINARIO no meio da virada o erro chega a 8 posicoes")
    void erroDeOitoPosicoes() {
        assertEquals(8, Medidas.piorErroEmBinario());

        assertEquals(16, Medidas.contador().size());
    }

    @Test
    @DisplayName("e lendo gray o erro e sempre no maximo 1")
    void erroDeUmaPosicao() {
        assertEquals(1, Medidas.piorErroEmGray());

        assertTrue(Medidas.piorErroEmBinario() > 7 * Medidas.piorErroEmGray());
    }

    @Test
    @DisplayName("as 16 leituras possiveis incluem a certa e a totalmente errada")
    void todasAsLeituras() {
        boolean achouCerta = false;
        boolean achouZero = false;

        for (Medidas.Leitura l : Medidas.contador()) {
            if (l.leituraErrada() == 8) achouCerta = true;
            if (l.leituraErrada() == 0) achouZero = true;
        }

        assertTrue(achouCerta, "a leitura certa tem de estar entre as possiveis");
        assertTrue(achouZero, "zero e uma leitura possivel ao passar de 7 para 8");
    }
}
