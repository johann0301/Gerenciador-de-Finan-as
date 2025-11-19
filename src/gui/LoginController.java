package gui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;
import logica.GerenciadorFinanceiro; // Importe o gerenciador

import java.io.IOException;

public class LoginController {

    @FXML
    private PasswordField campoSenha;

    // Pega o gerenciador estático que o MainApp criou
    private GerenciadorFinanceiro gerenciador = MainApp.gerenciador;

    @FXML
    protected void handleLogin() {
        String senha = campoSenha.getText();

        if (gerenciador.validarLogin(senha)) {
            // Sucesso!
            trocarDeTela("TelaPrincipal.fxml", "Meu Gestor Financeiro");
        } else {
            // Erro!
            mostrarAlertaErro("Senha incorreta!");
        }
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
}