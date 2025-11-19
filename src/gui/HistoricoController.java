package gui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader; // Importe o FXMLLoader
import javafx.scene.Parent;   // Importe o Parent
import javafx.scene.Scene;     // Importe o Scene
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality; // Para janelas "pop-up"
import javafx.stage.Stage;     // Para criar a nova janela
import logica.GerenciadorFinanceiro;
import modelo.Transacao;

import java.io.IOException; // Para tratar erros
import java.util.List;

public class HistoricoController {

    @FXML
    private TableView<Transacao> tabelaHistorico;
    @FXML
    private TableColumn<Transacao, String> colunaData;
    @FXML
    private TableColumn<Transacao, String> colunaDescricao;
    @FXML
    private TableColumn<Transacao, Double> colunaValor;
    @FXML
    private TableColumn<Transacao, String> colunaTipo;
    @FXML
    private TableColumn<Transacao, String> colunaCategoria;

    private GerenciadorFinanceiro gerenciador = MainApp.gerenciador;
    private TelaPrincipalController telaPrincipalControlador;

    public void setTelaPrincipalController(TelaPrincipalController telaPrincipalControlador) {
        this.telaPrincipalControlador = telaPrincipalControlador;
    }

    @FXML
    private void initialize() {
        configurarTabela();
        atualizarTabela(); // Renomeamos a lógica de carregamento
    }

    /**
     * Configura as colunas da tabela (só precisa rodar uma vez)
     */
    private void configurarTabela() {
        colunaData.setCellValueFactory(new PropertyValueFactory<>("data"));
        colunaDescricao.setCellValueFactory(new PropertyValueFactory<>("descricao"));
        colunaValor.setCellValueFactory(new PropertyValueFactory<>("valor"));
        colunaTipo.setCellValueFactory(new PropertyValueFactory<>("tipo"));
        colunaCategoria.setCellValueFactory(new PropertyValueFactory<>("categoria"));
    }

    /**
     * --- NOVO MÉTODO PÚBLICO ---
     * Pega os dados frescos do gerenciador e atualiza a tabela.
     * O pop-up de edição vai chamar isso.
     */
    public void atualizarTabela() {
        List<Transacao> listaDeTransacoes = gerenciador.getListaDeTransacoes();
        tabelaHistorico.getItems().setAll(listaDeTransacoes);
    }

    @FXML
    protected void handleRemoverSelecionado() {
        Transacao transacaoSelecionada = tabelaHistorico.getSelectionModel().getSelectedItem();
        if (transacaoSelecionada == null) {
            mostrarAlertaErro("Nenhuma transação foi selecionada.");
            return;
        }
        try {
            int indiceReal = gerenciador.getListaDeTransacoes().indexOf(transacaoSelecionada);
            int indiceParaBackend = indiceReal + 1;
            gerenciador.removerTransacao(indiceParaBackend);

            // Simplesmente atualiza a tabela inteira (em vez de só remover o item)
            atualizarTabela();

            mostrarAlertaInfo("Transação removida com sucesso!");

            if (telaPrincipalControlador != null) {
                telaPrincipalControlador.atualizarDashboard();
            }
        } catch (Exception e) {
            mostrarAlertaErro("Ocorreu um erro ao remover a transação: " + e.getMessage());
        }
    }

    /**
     * --- O NOVO MÉTODO PARA "EDITAR" ---
     * Chamado pelo "On Action" do botão "Editar Selecionado".
     * (Certifique-se de definir o On Action no Scene Builder como "handleEditarSelecionado")
     */
    @FXML
    protected void handleEditarSelecionado() {
        // 1. Pega a transação selecionada
        Transacao transacaoSelecionada = tabelaHistorico.getSelectionModel().getSelectedItem();

        if (transacaoSelecionada == null) {
            mostrarAlertaErro("Nenhuma transação foi selecionada.");
            return;
        }

        try {
            // 2. Carrega o FXML da janela pop-up
            FXMLLoader loader = new FXMLLoader(getClass().getResource("EditarTransacao.fxml"));
            Parent root = loader.load();

            // 3. Pega o controller do pop-up
            EditarTransacaoController controller = loader.getController();

            // 4. "Injeta" os dados na janela pop-up
            controller.carregarDados(transacaoSelecionada, telaPrincipalControlador, this);

            // 5. Cria e mostra a nova janela (Stage)
            Stage popupStage = new Stage();
            popupStage.initModality(Modality.APPLICATION_MODAL); // Trava a janela principal
            popupStage.setTitle("Editar Transação");
            popupStage.setScene(new Scene(root));
            popupStage.showAndWait(); // Mostra e espera a janela ser fechada

        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlertaErro("Erro ao abrir a tela de edição.");
        }
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