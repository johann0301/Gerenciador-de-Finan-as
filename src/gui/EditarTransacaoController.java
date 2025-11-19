package gui;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage; // Importe o Stage
import logica.GerenciadorFinanceiro;
import modelo.Transacao;

public class EditarTransacaoController {

    // --- 1. Conexão com o FXML ---
    @FXML
    private TextField campoDescricao;
    @FXML
    private TextField campoValor;
    @FXML
    private TextField campoCategoria;
    @FXML
    private TextField campoData;

    // --- 2. Variáveis de "Memória" ---
    private GerenciadorFinanceiro gerenciador = MainApp.gerenciador;
    private Transacao transacaoParaEditar;
    private TelaPrincipalController telaPrincipalControlador;
    private HistoricoController historicoController; // Para atualizar a tabela

    /**
     * O HistoricoController vai chamar este método para "injetar"
     * a transação selecionada e os outros controladores.
     */
    public void carregarDados(Transacao transacao, TelaPrincipalController tpc, HistoricoController hc) {
        this.transacaoParaEditar = transacao;
        this.telaPrincipalControlador = tpc;
        this.historicoController = hc;

        // Preenche os campos do formulário com os dados da transação
        campoDescricao.setText(transacao.getDescricao());
        campoValor.setText(String.valueOf(transacao.getValor()));
        campoCategoria.setText(transacao.getCategoria());
        campoData.setText(transacao.getData());
    }

    /**
     * Chamado pelo botão "Salvar Alterações" (On Action)
     */
    @FXML
    protected void handleSalvarEdicao() {
        try {
            // 1. Pega os novos dados dos campos de texto
            String novaDescricao = campoDescricao.getText();
            double novoValor = Double.parseDouble(campoValor.getText());
            String novaCategoria = campoCategoria.getText();
            String novaData = campoData.getText();

            // 2. Atualiza o objeto Transacao ORIGINAL
            // (Graças ao Java, 'transacaoParaEditar' é uma referência ao objeto
            // que está na lista do gerenciador, então podemos mudá-lo)
            transacaoParaEditar.setDescricao(novaDescricao);
            transacaoParaEditar.setValor(novoValor);
            transacaoParaEditar.setCategoria(novaCategoria);
            transacaoParaEditar.setData(novaData);

            // 3. Pede ao seu backend para recalcular e salvar
            // (Seu GerenciadorFinanceiro já tem esse método!)
            gerenciador.salvarAposEdicao();

            // 4. Avisa os outros controladores para se atualizarem
            telaPrincipalControlador.atualizarDashboard(); // Atualiza o SALDO
            historicoController.atualizarTabela(); // Atualiza a TABELA

            mostrarAlertaInfo("Transação editada com sucesso!");

            // 5. Fecha a janela "pop-up"
            fecharJanela();

        } catch (NumberFormatException e) {
            mostrarAlertaErro("Valor inválido. Use um número (ex: 10.50).");
        } catch (Exception e) {
            mostrarAlertaErro("Erro ao salvar edição: " + e.getMessage());
        }
    }

    // Método de ajuda para fechar o pop-up
    private void fecharJanela() {
        // Pega o "palco" (Stage) desta janela e fecha
        Stage stage = (Stage) campoDescricao.getScene().getWindow();
        stage.close();
    }

    // --- Métodos de Alerta (Helpers) ---

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
        alert.setHeaderText("Ação Falhou");
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}