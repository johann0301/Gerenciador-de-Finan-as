package gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import logica.GerenciadorFinanceiro; // Importe seu gerenciador

import java.io.IOException;

public class MainApp extends Application {

    // 1. Crie uma instância ESTÁTICA do seu gerenciador.
    // Isso permite que TODOS os controllers acessem o *mesmo* gerenciador.
    public static GerenciadorFinanceiro gerenciador;

    @Override
    public void start(Stage stage) throws IOException {
        // 2. Inicialize o gerenciador ANTES de qualquer tela
        gerenciador = new GerenciadorFinanceiro();

        // 3. Verifique se é o primeiro login (como no seu console)
        boolean isPrimeiroLogin = gerenciador.isPrimeiroLogin();

        // 4. Decida qual FXML carregar
        String fxmlInicial;
        String tituloInicial;

        if (isPrimeiroLogin) {
            fxmlInicial = "DefinirSenha.fxml";
            tituloInicial = "Definir Nova Senha";
        } else {
            fxmlInicial = "Login.fxml";
            tituloInicial = "Login - Gestor Financeiro";
        }

        // 5. Carregue o FXML decidido
        FXMLLoader fxmlLoader = new FXMLLoader(MainApp.class.getResource(fxmlInicial));
        Scene scene = new Scene(fxmlLoader.load());

        stage.setTitle(tituloInicial);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}