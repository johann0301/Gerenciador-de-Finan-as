package gui;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import logica.GerenciadorFinanceiro;

import java.util.Optional;

public class BackupController {

    @FXML
    private ListView<String> listaBackups;

    private GerenciadorFinanceiro gerenciador = MainApp.gerenciador;

    @FXML
    private void initialize() {
        // Carrega a lista de backups imediatamente ao abrir a tela
        atualizarListaBackups();
    }

    private void atualizarListaBackups() {
        listaBackups.getItems().setAll(gerenciador.listarBackups());
    }

    @FXML
    protected void handleCriarBackup() {
        // 1. Chama o backend para criar o backup
        String resultado = gerenciador.backupDados();

        // 2. Avisa o usuário e atualiza a lista
        if (resultado.startsWith("Erro")) {
            mostrarAlertaErro(resultado);
        } else {
            mostrarAlertaInfo(resultado);
            atualizarListaBackups();
        }
    }

    @FXML
    protected void handleRestaurar() {
        // 1. Pega o nome do backup selecionado
        String backupSelecionado = listaBackups.getSelectionModel().getSelectedItem();

        if (backupSelecionado == null) {
            mostrarAlertaErro("Por favor, selecione um arquivo de backup para restaurar.");
            return;
        }

        // 2. Abre um pop-up de confirmação (MUITO IMPORTANTE!)
        Alert confirmacao = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacao.setTitle("Confirmação de Restauração");
        confirmacao.setHeaderText("ATENÇÃO: Você tem certeza?");
        confirmacao.setContentText("Restaurar o backup '" + backupSelecionado
                + "' irá APAGAR todos os dados atuais. Deseja continuar?");

        Optional<ButtonType> resultado = confirmacao.showAndWait();

        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            // 3. Chama o backend para restaurar
            String resultadoRestauracao = gerenciador.restaurarDados(backupSelecionado);

            // 4. Avisa o usuário
            if (resultadoRestauracao.startsWith("Erro")) {
                mostrarAlertaErro(resultadoRestauracao);
            } else {
                mostrarAlertaInfo(resultadoRestauracao);
                // NOTA: O Saldo do dashboard principal não é atualizado automaticamente aqui.
                // Mas o usuário fará o login de novo (ou voltará), então está OK por enquanto.
            }
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