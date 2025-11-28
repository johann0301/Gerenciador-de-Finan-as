package gui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import logica.GerenciadorFinanceiro;

import java.io.IOException;
import java.util.List;

public class TelaPrincipalController {

    @FXML
    private Label labelSaldo;
    @FXML
    private Label labelOrcamento;
    @FXML
    private Label labelAlertas;
    @FXML
    private AnchorPane areaDeConteudo;

    private GerenciadorFinanceiro gerenciador = MainApp.gerenciador;

    @FXML
    private void initialize() {
        System.out.println("Tela Principal carregada!");
        atualizarDashboard();
    }

    public void atualizarDashboard() {
        labelSaldo.setText(String.format("SALDO ATUAL: R$ %.2f", gerenciador.getSaldo()));
        double orcamento = gerenciador.getOrcamentoMensal();
        if (orcamento > 0) {
            double gastos = gerenciador.getGastosMesAtual();
            double percentual = (gastos / orcamento) * 100.0;
            labelOrcamento.setText(String.format("ORÇAMENTO: R$ %.2f / R$ %.2f (%.0f%%)", gastos, orcamento, percentual));
        } else {
            labelOrcamento.setText("Orçamento: (Não definido)");
        }
        List<String> alertas = gerenciador.getAlertasFinanceiros();
        if (alertas.isEmpty()) {
            labelAlertas.setText("--- Sem Alertas ---");
        } else {
            labelAlertas.setText("! " + String.join("\n! ", alertas));
        }
    }

    // --- 5. Métodos do Menu (On Action) ---

    @FXML
    protected void handleMenuAdicionarReceita() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("AdicionarTransacao.fxml"));
            Node subPagina = loader.load();

            AdicionarTransacaoController controller = loader.getController();
            controller.setTelaPrincipalController(this);
            controller.setTipoTransacao("RECEITA");

            // NOVO: Define o título da página
            controller.setTituloPagina("Adicionar Receita");

            setAnchor(subPagina);
            areaDeConteudo.getChildren().setAll(subPagina);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void handleMenuAdicionarDespesa() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("AdicionarTransacao.fxml"));
            Node subPagina = loader.load();

            AdicionarTransacaoController controller = loader.getController();
            controller.setTelaPrincipalController(this);
            controller.setTipoTransacao("DESPESA");

            // NOVO: Define o título da página
            controller.setTituloPagina("Adicionar Despesa");

            setAnchor(subPagina);
            areaDeConteudo.getChildren().setAll(subPagina);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void handleMenuVerHistorico() {
        System.out.println("Botão 'Extrato/Histórico' clicado.");
        try {
            // --- MUDANÇA IMPORTANTE AQUI ---
            // Tivemos que mudar para o FXMLLoader para pegar o controller
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Historico.fxml"));
            Node subPagina = loader.load();

            // Pega o controller do Historico
            HistoricoController controller = loader.getController();
            // Passa a referência da tela principal para ele
            controller.setTelaPrincipalController(this);

            setAnchor(subPagina);
            areaDeConteudo.getChildren().setAll(subPagina);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // O método "carregarSubPagina(String fxmlNome)" foi removido
    // porque agora todos os carregamentos precisam pegar o controller.

    // --- Métodos "Stub" (Vazios) para o resto do menu ---

    @FXML
    protected void handleMenuBuscarTransacoes() {
        System.out.println("Botão 'Buscar' clicado.");
        carregarSubPagina("Buscar.fxml");
    }

    @FXML
    protected void handleMenuRelatoriosEstatisticos() {
        System.out.println("Botão 'Relatórios' clicado.");
        carregarSubPagina("Relatorios.fxml");
    }

    @FXML
    protected void handleMenuDefinirOrcamentoMensal() {
        System.out.println("Botão 'Orçamento' clicado.");
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Orcamento.fxml"));
            Node subPagina = loader.load();

            // Pega o controller e passa a referência da tela principal
            OrcamentoController controller = loader.getController();
            controller.setTelaPrincipalController(this);

            setAnchor(subPagina);
            areaDeConteudo.getChildren().setAll(subPagina);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void handleMenuGerenciarTransacoesRecorrentes() {
        System.out.println("Botão 'Recorrentes' clicado.");
        carregarSubPagina("Recorrentes.fxml");
    }

    @FXML
    protected void handleMenuVerSugestoesInteligentes() {
        System.out.println("Botão 'Sugestões' clicado.");
        carregarSubPagina("Sugestoes.fxml");
    }

    @FXML
    protected void handleMenuBackupRestauracao() {
        System.out.println("Botão 'Backup' clicado.");
        carregarSubPagina("Backup.fxml");
    }

    private void carregarSubPagina(String fxmlNome) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlNome));
            Node subPagina = loader.load();

            setAnchor(subPagina);
            areaDeConteudo.getChildren().setAll(subPagina);

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Erro: Não foi possível carregar a sub-página: " + fxmlNome);
            // (Aqui você pode mostrar um Alert de erro)
        }
    }

    private void setAnchor(Node node) {
        AnchorPane.setTopAnchor(node, 0.0);
        AnchorPane.setBottomAnchor(node, 0.0);
        AnchorPane.setLeftAnchor(node, 0.0);
        AnchorPane.setRightAnchor(node, 0.0);
    }
}