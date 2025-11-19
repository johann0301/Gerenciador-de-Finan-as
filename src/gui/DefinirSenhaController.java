package gui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;
import logica.GerenciadorFinanceiro; // Importe o gerenciador

import java.io.IOException;

public class DefinirSenhaController {

    @FXML
    private PasswordField campoSenha;

    @FXML
    private PasswordField campoConfirmarSenha;

    // Pega o gerenciador estático que o MainApp criou
    private GerenciadorFinanceiro gerenciador = MainApp.gerenciador;

    @FXML
    protected void handleSalvarSenha() {
        String senha = campoSenha.getText();
        String confirmacao = campoConfirmarSenha.getText();

        if (senha.isEmpty() || confirmacao.isEmpty()) {
            mostrarAlertaErro("Os campos não podem estar vazios.");
            return;
        }

        if (!senha.equals(confirmacao)) {
            mostrarAlertaErro("As senhas não conferem!");
            return;
        }

        // Sucesso!
        gerenciador.definirNovaSenha(senha);
        mostrarAlertaInfo("Senha definida com sucesso! Você será levado ao painel principal.");

        // Agora, trocamos para a Tela Principal
        trocarDeTela("TelaPrincipal.fxml", "Meu Gestor Financeiro");
    }

    // --- Métodos de ajuda (Helpers) ---

    private void trocarDeTela(String fxml, String titulo) {
        try {
            Parent proximaTela = FXMLLoader.load(getClass().getResource(fxml));
            Scene proximaScene = new Scene(proximaTela);

            // Pega a "Janela" (Stage) atual
            Stage stage = (Stage) campoSenha.getScene().getWindow();

            stage.setScene(proximaScene);
            stage.setTitle(titulo);
        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlertaErro("Erro ao carregar a próxima tela.");
        }
    }

    private void mostrarAlertaErro(String mensagem) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erro");
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }

    private void mostrarAlertaInfo(String mensagem) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Sucesso");
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}