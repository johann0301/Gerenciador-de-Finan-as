package gui;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import logica.GerenciadorFinanceiro;
import modelo.Transacao;
import javafx.scene.control.Label;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class AdicionarTransacaoController {

    @FXML
    private TextField campoDescricao;

    @FXML
    private TextField campoValor;

    @FXML
    private Label labelTitulo;

    private String tipoTransacao;
    private GerenciadorFinanceiro gerenciador = MainApp.gerenciador;

    // --- NOVA ADIÇÃO ---
    // Variável para "lembrar" quem é o controlador principal
    private TelaPrincipalController telaPrincipalControlador;

    /**
     * --- NOVO MÉTODO ---
     * A TelaPrincipal vai chamar este método para nos dar uma referência a ela.
     */
    public void setTelaPrincipalController(TelaPrincipalController telaPrincipalControlador) {
        this.telaPrincipalControlador = telaPrincipalControlador;
    }

    public void setTipoTransacao(String tipo) {
        this.tipoTransacao = tipo;
    }


    @FXML
    protected void handleAdicionarTransacao() {
        String descricao = campoDescricao.getText();
        String valorStr = campoValor.getText();

        if (descricao.isEmpty() || valorStr.isEmpty()) {
            mostrarAlertaErro("Por favor, preencha todos os campos.");
            return;
        }

        try {
            double valor = Double.parseDouble(valorStr);
            String dataHoje = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

            Transacao novaTransacao = new Transacao(
                    valor,
                    this.tipoTransacao,
                    "Geral",
                    dataHoje,
                    descricao
            );

            gerenciador.adicionarTransacao(novaTransacao);

            mostrarAlertaInfo("Transação '" + this.tipoTransacao + "' adicionada com sucesso!");

            campoDescricao.clear();
            campoValor.clear();

            // --- NOVA ADIÇÃO ---
            // Avisa a tela principal para atualizar o saldo!
            if (telaPrincipalControlador != null) {
                telaPrincipalControlador.atualizarDashboard();
            }

        } catch (NumberFormatException e) {
            mostrarAlertaErro("Valor inválido. Por favor, use um número (ex: 10.50).");
        } catch (Exception e) {
            mostrarAlertaErro("Erro ao adicionar transação: " + e.getMessage());
        }
    }

    private void mostrarAlertaInfo(String mensagem) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Sucesso");
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }

    private void mostrarAlertaErro(String mensagem) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erro");
        alert.setHeaderText("Não foi possível completar a ação");
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
    public void setTituloPagina(String titulo) {
        this.labelTitulo.setText(titulo);
    }
}