package gui;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import logica.GerenciadorFinanceiro;

public class RelatoriosController {

    @FXML
    private TextArea areaDeRelatorio;

    private GerenciadorFinanceiro gerenciador = MainApp.gerenciador;

    @FXML
    private void initialize() {
        // Opcional: Carregar um relatório padrão ao abrir
        handleRelatorioBalanco();
    }

    @FXML
    protected void handleRelatorioCategoria() {
        String relatorio = gerenciador.relatorioGastosPorCategoria();
        areaDeRelatorio.setText(relatorio);
    }

    @FXML
    protected void handleRelatorioBalanco() {
        String relatorio = gerenciador.relatorioEntradasVsSaidasMesAtual();
        areaDeRelatorio.setText(relatorio);
    }

    @FXML
    protected void handleRelatorioMaiorDespesa() {
        String relatorio = gerenciador.relatorioMaiorDespesaMesAtual();
        areaDeRelatorio.setText(relatorio);
    }

    @FXML
    protected void handleRelatorioMedia() {
        String relatorio = gerenciador.relatorioMediaGastosDiariosMesAtual();
        areaDeRelatorio.setText(relatorio);
    }
}