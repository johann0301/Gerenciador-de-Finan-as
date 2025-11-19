package modelo;

public class Transacao {

    private double valor;
    private String tipo; // Será "RECEITA" ou "DESPESA"
    private String categoria; // Ex: "Salário", "Alimentação", "Lazer"
    private String data; // Usaremos String (texto) "dd/MM/yyyy"
    private String descricao;

    // Construtor: Usado para criar um novo objeto modelo.Transacao
    public Transacao(double valor, String tipo, String categoria, String data, String descricao) {
        this.valor = valor;
        this.tipo = tipo;
        this.categoria = categoria;
        this.data = data;
        this.descricao = descricao;
    }

    // --- Getters e Setters ---
    public double getValor() {
        return valor;
    }

    public String getTipo() {
        return tipo;
    }

    public String getCategoria() {
        return categoria;
    }

    public String getData() {
        return data;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public void setData(String data) {
        this.data = data;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    // --- Método toString ---
    @Override
    public String toString() {
        return String.format("[%s] %s: R$ %.2f (%s) - %s",
                data, tipo, valor, categoria, descricao);
    }
}