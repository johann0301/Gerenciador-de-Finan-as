package gui;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import logica.GerenciadorFinanceiro;
import modelo.Transacao;

import java.util.List;

public class BuscarController {

    // --- 1. Conexão com o Formulário de Busca ---
    @FXML
    private TextField campoCategoria;
    @FXML
    private TextField campoDataInicio;
    @FXML
    private TextField campoDataFim;
    @FXML
    private Label labelResultadosCount; // Label para mostrar o status

    // --- 2. Conexão com a Tabela de Resultados ---
    @FXML
    private TableView<Transacao> tabelaResultados;
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

    // --- 3. Instância do Backend ---
    private GerenciadorFinanceiro gerenciador = MainApp.gerenciador;

    /**
     * Roda automaticamente quando o FXML é carregado.
     * Usado para configurar as colunas da tabela.
     */
    @FXML
    private void initialize() {
        // Configura as colunas da tabela (exatamente como no HistoricoController)
        // O texto ("data", "descricao", etc.) deve bater com os getters da sua classe Transacao
        colunaData.setCellValueFactory(new PropertyValueFactory<>("data"));
        colunaDescricao.setCellValueFactory(new PropertyValueFactory<>("descricao"));
        colunaValor.setCellValueFactory(new PropertyValueFactory<>("valor"));
        colunaTipo.setCellValueFactory(new PropertyValueFactory<>("tipo"));
        colunaCategoria.setCellValueFactory(new PropertyValueFactory<>("categoria"));
    }

    /**
     * Chamado pelo "On Action" do botão "Buscar".
     */
    @FXML
    protected void handleBuscar() {
        // 1. Pega os dados dos campos de texto
        String categoria = campoCategoria.getText();
        String dataInicio = campoDataInicio.getText();
        String dataFim = campoDataFim.getText();

        // 2. Chama o método do seu backend que já existe!
        List<Transacao> resultados = gerenciador.buscarTransacoes(dataInicio, dataFim, categoria);

        // 3. Popula a tabela com os resultados da busca
        tabelaResultados.getItems().setAll(resultados);

        // 4. Atualiza o label de status
        if (resultados.isEmpty()) {
            labelResultadosCount.setText("Nenhuma transação encontrada com esses critérios.");
        } else {
            labelResultadosCount.setText(resultados.size() + " transação(ões) encontrada(s).");
        }
    }
}