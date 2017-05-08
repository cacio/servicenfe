package br.com.prodasiq.leopardoa7printer;

/**
 * Created by crsproda on 22/11/2016.
 */

public class BoletoUtils extends Boleto{

    public BoletoUtils(String linhaDigitavel) {
        setLinhaDigitavel(linhaDigitavel);

        if (getLinhaDigitavel() == null || getLinhaDigitavel().equals("")) {
            throw new IllegalArgumentException("Verifique a linha digitavel");
        }

        setLinhaDigitavel(linhaDigitavel);
    }

    public BoletoUtils() {
    }

    // calcula ao codigo de barra dado a linha
    public String calcularCodigoBarra(String linha) {

        String barra = linha.replaceAll("\\D*", "");
        // troca tudo que não for
        // dígito ( \D ) por
        // vazio.

        // completa com zeros a direita da string
        if (barra.length() < 47) {
            barra = barra + "000000000000000".substring(0, 47 - barra.length());
        }

        barra = barra.substring(0, 4) + barra.substring(32, 47)
                + barra.substring(4, 9) + barra.substring(10, 20)
                + barra.substring(21, 31);

        // calcula digito verificador
        int digito = calcularDigitoModulo11(barra.substring(0, 4)
                + barra.substring(5, 44));

        // verifica digito

        if (digito != Integer.valueOf(barra.substring(4, 5))) {
            throw new IllegalArgumentException("Digito Verificador incorreto");
        }

        return barra;

    }

    // Calculo do digito verificador
    public int calcularDigitoModulo11(String numero) {

        int soma = 0;
        int peso = 2;
        int digito;

        for (int i = numero.length() - 1; i >= 0; i--) {

            soma = soma + Integer.valueOf(numero.substring(i, i + 1)) * peso;

            if (peso < 9) {
                peso = peso + 1;
            } else {
                peso = 2;
            }
        }

        digito = 11 - (soma % 11);

        if (digito > 9) {
            digito = 0;
        }

        if (digito == 0) {
            digito = 1;
        }

        return digito;

    }

    public String[] instrucaoToArray(String instrucao, int sizeLine) {
        int lines = (instrucao.length() / sizeLine)
                + (instrucao.length() % sizeLine != 0 ? 1 : 0);

        if (lines > 7) {

            throw new IllegalArgumentException("maximo 7 linhas de instrucao");
        }

        String[] lineInstrucao = new String[7];
        for (int i = lines; i < 7; i++) {
            lineInstrucao[i] = "";

        }

        int posInstr = 0;
        for (int posLine = 0; posLine <= lines - 1; posLine++) {

            if (instrucao.length() % sizeLine > 0 && posLine == lines - 1) {
                lineInstrucao[posLine] = instrucao.substring(posInstr, posInstr
                        + (instrucao.length() % sizeLine));

                break;
            }

            lineInstrucao[posLine] = instrucao.substring(posInstr, posInstr
                    + (sizeLine));
            posInstr += sizeLine;
        }
        return lineInstrucao;
    }

    @Override
    public void genNumeroCodBarras() {

        setNumeroCodBarras(calcularCodigoBarra(getLinhaDigitavel()));

    }
}
