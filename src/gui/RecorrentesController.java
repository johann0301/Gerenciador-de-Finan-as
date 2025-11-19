package gui;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import logica.GerenciadorFinanceiro;
import modelo.TransacaoRecorrente;

import java.util.List;

public class RecorrentesController {

    // --- Formulário ---
    @FXML private TextField campoValor;
    @FXML private TextField campoCategoria;
    @FXML private TextField campoDescricao;
    @FXML private TextField campoDia;
    @FXML private TextField campoTipo; // Ex: "DESPESA" ou "RECEITA"

    // --- Tabela ---
    @FXML private TableView<TransacaoRecorrente> tabelaRecorrentes;
    @FXML private TableColumn<TransacaoRecorrente, Integer> colunaDia;
    @FXML private TableColumn<TransacaoRecorrente, String> colunaDescricao;
    @FXML private TableColumn<TransacaoRecorrente, Double> colunaValor;
    @FXML private TableColumn<TransacaoRecorrente, String> colunaTipo;
    @FXML private TableColumn<TransacaoRecorrente, String> colunaCategoria;

    private GerenciadorFinanceiro gerenciador = MainApp.gerenciador;

    @FXML
    private void initialize() {
        // Configura as colunas (nomes devem bater com os getters de TransacaoRecorrente)
        colunaDia.setCellValueFactory(new PropertyValueFactory<>("diaDoMes"));
        colunaDescricao.setCellValueFactory(new PropertyValueFactory<>("descricao"));
        colunaValor.setCellValueFactory(new PropertyValueFactory<>("valor"));
        colunaTipo.setCellValueFactory(new PropertyValueFactory<>("tipo"));
        colunaCategoria.setCellValueFactory(new PropertyValueFactory<>("categoria"));

        atualizarTabela();
    }

    private void atualizarTabela() {
        List<TransacaoRecorrente> lista = gerenciador.getTransacoesRecorrentes();
        tabelaRecorrentes.getItems().setAll(lista);
    }

    @FXML
    protected void handleAdicionar() {
        try {
            double valor = Double.parseDouble(campoValor.getText());
            String categoria = campoCategoria.getText();
            String descricao = campoDescricao.getText();
            String tipo = campoTipo.getText().toUpperCase(); // Força maiúsculas
            int dia = Integer.parseInt(campoDia.getText());

            if (dia < 1 || dia > 31) {
                mostrarAlertaErro("Dia inválido (use 1 a 31).");
                return;
            }
            if (!tipo.equals("RECEITA") && !tipo.equals("DESPESA")) {
                mostrarAlertaErro("Tipo inválido. Use 'RECEITA' ou 'DESPESA'.");
                return;
            }

            TransacaoRecorrente nova = new TransacaoRecorrente(valor, tipo, categoria, descricao, dia);

            gerenciador.adicionarTransacaoRecorrente(nova);

            mostrarAlertaInfo("Transação recorrente adicionada!");

            // Limpa e atualiza
            campoValor.clear();
            campoDescricao.clear();
            campoCategoria.clear();
            campoDia.clear();
            campoTipo.clear();

            atualizarTabela();

        } catch (NumberFormatException e) {
            mostrarAlertaErro("Erro nos números: Verifique Valor e Dia.");
        } catch (Exception e) {
            mostrarAlertaErro("Erro: " + e.getMessage());
        }
    }

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