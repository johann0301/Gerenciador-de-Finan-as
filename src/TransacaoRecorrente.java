/**
 * Nova classe para armazenar transações recorrentes (Feature 11).
 */
public class TransacaoRecorrente {

    private double valor;
    private String tipo; // "RECEITA" ou "DESPESA"
    private String categoria;
    private String descricao;
    private int diaDoMes; // Dia do mês que a transação deve ocorrer (1-31)

    // Construtor
    public TransacaoRecorrente(double valor, String tipo, String categoria, String descricao, int diaDoMes) {
        this.valor = valor;
        this.tipo = tipo;
        this.categoria = categoria;
        this.descricao = descricao;
        this.diaDoMes = diaDoMes;
    }

    // --- Getters ---
    public double getValor() {
        return valor;
    }

    public String getTipo() {
        return tipo;
    }

    public String getCategoria() {
        return categoria;
    }

    public String getDescricao() {
        return descricao;
    }

    public int getDiaDoMes() {
        return diaDoMes;
    }

    @Override
    public String toString() {
        // Define como a transação recorrente será impressa
        return String.format("[Todo dia %d] %s: R$ %.2f (%s) - %s",
                diaDoMes, tipo, valor, categoria, descricao);
    }
}