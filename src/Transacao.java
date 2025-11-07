// Arquivo: Transacao.java

/*
 * Esta é a classe "molde". Ela não faz nada sozinha,
 * apenas armazena os dados de UMA transação.
 */
public class Transacao {

    private double valor;
    private String tipo;      // Será "RECEITA" ou "DESPESA"
    private String categoria; // Ex: "Salário", "Alimentação", "Lazer"
    private String data;      // Usaremos String (texto) "dd/MM/aaaa" para simplificar
    private String descricao;

    // Construtor: Usado para criar um novo objeto Transacao
    public Transacao(double valor, String tipo, String categoria, String data, String descricao) {
        this.valor = valor;
        this.tipo = tipo;
        this.categoria = categoria;
        this.data = data;
        this.descricao = descricao;
    }

    // --- Getters e Setters ---
    // Getters são usados para LER os valores
    public double getValor() { return valor; }
    public String getTipo() { return tipo; }
    public String getCategoria() { return categoria; }
    public String getData() { return data; }
    public String getDescricao() { return descricao; }

    // Setters são usados para EDITAR os valores
    public void setValor(double valor) { this.valor = valor; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public void setCategoria(String categoria) { this.categoria = categoria; }
    public void setData(String data) { this.data = data; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    // --- Método toString ---
    // Isso define como a transação será impressa no console.
    @Override
    public String toString() {
        // O "R$ %.2f" formata o valor para 2 casas decimais
        return String.format("[%s] %s: R$ %.2f (%s) - %s",
                data, tipo, valor, categoria, descricao);
    }
}