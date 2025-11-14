import java.time.LocalDate;

/**
 * Nova classe para armazenar configurações do usuário.
 * Isso armazena a senha, o orçamento e dados de controle.
 */
public class Configuracao {

    private String senhaHash;
    private double orcamentoMensal;
    private LocalDate dataUltimaVerificacaoRecorrente;

    // Construtor padrão
    public Configuracao() {
        this.orcamentoMensal = 0.0;
        this.senhaHash = null; // null indica que nenhuma senha foi definida
        this.dataUltimaVerificacaoRecorrente = null;
    }

    // --- Getters e Setters ---

    public String getSenhaHash() {
        return senhaHash;
    }

    public void setSenhaHash(String senhaHash) {
        this.senhaHash = senhaHash;
    }

    public double getOrcamentoMensal() {
        return orcamentoMensal;
    }

    public void setOrcamentoMensal(double orcamentoMensal) {
        this.orcamentoMensal = orcamentoMensal;
    }

    public LocalDate getDataUltimaVerificacaoRecorrente() {
        return dataUltimaVerificacaoRecorrente;
    }

    public void setDataUltimaVerificacaoRecorrente(LocalDate dataUltimaVerificacaoRecorrente) {
        this.dataUltimaVerificacaoRecorrente = dataUltimaVerificacaoRecorrente;
    }
}