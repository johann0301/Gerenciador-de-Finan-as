package gui;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import logica.GerenciadorFinanceiro;

public class OrcamentoController {

    @FXML
    private Label labelOrcamentoAtual;

    @FXML
    private TextField campoNovoOrcamento;

    private GerenciadorFinanceiro gerenciador = MainApp.gerenciador;
    private TelaPrincipalController telaPrincipalControlador;

    public void setTelaPrincipalController(TelaPrincipalController tpc) {
        this.telaPrincipalControlador = tpc;
    }

    @FXML
    private void initialize() {
        atualizarVisualizacao();
    }

    private void atualizarVisualizacao() {
        double atual = gerenciador.getOrcamentoMensal();
        labelOrcamentoAtual.setText(String.format("Orçamento Atual: R$ %.2f", atual));
    }

    @FXML
    protected void handleSalvarOrcamento() {
        String valorStr = campoNovoOrcamento.getText();

        if (valorStr.isEmpty()) {
            mostrarAlertaErro("Por favor, digite um valor.");
            return;
        }

        try {
            double novoValor = Double.parseDouble(valorStr);

            if (novoValor < 0) {
                mostrarAlertaErro("O orçamento não pode ser negativo.");
                return;
            }

            // Salva no backend
            gerenciador.setOrcamentoMensal(novoValor);

            mostrarAlertaInfo("Orçamento definido com sucesso!");

            // Atualiza a tela atual
            atualizarVisualizacao();
            campoNovoOrcamento.clear();

            // Atualiza a barra de progresso lá no topo da janela principal!
            if (telaPrincipalControlador != null) {
                telaPrincipalControlador.atualizarDashboard();
            }

        } catch (NumberFormatException e) {
            mostrarAlertaErro("Valor inválido. Use números (ex: 2500.00).");
        }
    }

    // --- Helpers ---
    private void mostrarAlertaInfo(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Sucesso");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    private void mostrarAlertaErro(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erro");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}