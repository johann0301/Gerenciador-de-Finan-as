package gui;

import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import logica.GerenciadorFinanceiro;

import java.util.List;

public class SugestoesController {

    @FXML
    private ListView<String> listaDeSugestoes; // Deve ser List<String> no genérico

    private GerenciadorFinanceiro gerenciador = MainApp.gerenciador;

    @FXML
    private void initialize() {
        atualizarSugestoes();
    }

    private void atualizarSugestoes() {
        // Chama o método do seu backend (que retorna List<String>)
        List<String> sugestoes = gerenciador.getSugestoesInteligentes();

        // O ListView.getItems().setAll() aceita a lista de String diretamente!
        listaDeSugestoes.getItems().setAll(sugestoes);

        // Adiciona um título ou nota
        listaDeSugestoes.getItems().add(0, "--- Resumo dos Alertas ---");

        // Opcional: faz a lista rolar para o topo
        listaDeSugestoes.scrollTo(0);
    }
}