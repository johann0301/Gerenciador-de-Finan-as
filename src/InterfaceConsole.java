
// Arquivo: InterfaceConsole.java (Atualizado)
import logica.GerenciadorFinanceiro;
import modelo.Transacao;
import modelo.TransacaoRecorrente;

import java.util.List;
import java.util.Scanner;
// Imports adicionados
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class InterfaceConsole {

    private GerenciadorFinanceiro gerenciador;
    private Scanner scanner;
    private boolean logado; // Controla o estado do login

    // Formatter para data, usado para preencher a data de hoje
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // Construtor
    public InterfaceConsole() {
        // Inicializa o gerenciador (que carrega os dados)
        this.gerenciador = new GerenciadorFinanceiro();
        // Inicializa o scanner
        this.scanner = new Scanner(System.in);
        this.logado = false; // Começa deslogado
    }

    // Método principal que roda o programa
    public void iniciar() {
        // --- Feature 9: Sistema de Login ---
        rodarLogin();

        // Se não logou, encerra o programa
        if (!this.logado) {
            System.out.println("Login falhou. Encerrando o sistema.");
            scanner.close();
            return;
        }

        // --- Feature 11: Processar Recorrentes ---
        // Imediatamente após o login, processa transações pendentes
        gerenciador.processarTransacoesRecorrentes();

        // --- Loop Principal do Menu ---
        int opcao = -1;
        while (opcao != 0) {
            exibirCabecalho(); // Mostra saldo, orçamento e alertas
            exibirMenuPrincipal();

            try {
                opcao = scanner.nextInt();
                scanner.nextLine(); // Limpa o buffer do scanner
            } catch (Exception e) {
                System.out.println("Erro: Por favor, digite um número.");
                scanner.nextLine(); // Limpa o buffer em caso de erro
                opcao = -1; // Reseta a opção para continuar no loop
                continue; // Pula para a próxima iteração
            }

            switch (opcao) {
                case 1:
                    adicionarReceita();
                    break;
                case 2:
                    adicionarDespesa();
                    break;
                case 3: // Feature 12
                    this.gerenciador.listarTransacoes();
                    break;
                case 4:
                    removerTransacao();
                    break;
                case 5:
                    editarTransacao();
                    break;
                case 6: // Feature 6
                    menuBuscarTransacoes();
                    break;
                case 7: // Feature 3
                    menuRelatorios();
                    break;
                case 8: // Feature 1
                    menuDefinirOrcamento();
                    break;
                case 9: // Feature 11
                    menuTransacoesRecorrentes();
                    break;
                case 10: // Feature 8
                    menuSugestoes();
                    break;
                case 11: // Feature 7
                    menuBackupRestauracao();
                    break;
                case 0:
                    System.out.println("Saindo do sistema... Obrigado!");
                    break;
                default:
                    System.out.println("Opção inválida. Tente novamente.");
            }
        }
        // Fecha o scanner ao sair do loop
        scanner.close();
    }

    // --- Feature 9: Lógica de Login ---
    private void rodarLogin() {
        System.out.println("===== Bem-vindo ao Gestor Financeiro =====");
        if (gerenciador.isPrimeiroLogin()) {
            // Força a criação de senha no primeiro acesso
            System.out.println("Detectamos que este é seu primeiro acesso.");
            System.out.println("Por favor, crie uma senha para proteger seus dados.");
            System.out.print("Digite sua nova senha: ");
            String senha1 = scanner.nextLine();
            System.out.print("Confirme sua nova senha: ");
            String senha2 = scanner.nextLine();

            if (senha1.isEmpty() || !senha1.equals(senha2)) {
                System.out.println("Senhas não conferem ou estão em branco. Encerrando.");
                return; // Encerra o método, logado = false
            }
            gerenciador.definirNovaSenha(senha1);
            this.logado = true; // Loga automaticamente
        } else {
            // Pede a senha para usuários existentes
            int tentativas = 3;
            while (tentativas > 0) {
                System.out.print("Digite sua senha: ");
                String senha = scanner.nextLine();
                if (gerenciador.validarLogin(senha)) {
                    this.logado = true;
                    System.out.println("Login realizado com sucesso!");
                    break; // Sai do while
                } else {
                    tentativas--;
                    System.out.printf("Senha incorreta. Você tem %d tentativa(s) restante(s).\n", tentativas);
                }
            }
        }
    }

    // --- Exibição do Menu e Cabeçalho ---

    private void exibirCabecalho() {
        System.out.println("\n---------------------------------------");
        System.out.printf("SALDO ATUAL: R$ %.2f\n", this.gerenciador.getSaldo());

        // Feature 1: Barra de Progresso do Orçamento
        exibirBarraProgressoOrcamento();

        // Feature 2: Alertas Financeiros
        exibirAlertas();

        System.out.println("---------------------------------------");
    }

    private void exibirMenuPrincipal() {
        System.out.println("===== Gestor de Finanças Pessoais =====");
        System.out.println("1. Adicionar Receita");
        System.out.println("2. Adicionar Despesa");
        System.out.println("3. Ver Histórico Detalhado (Extrato)"); // Feature 12
        System.out.println("4. Remover Transação");
        System.out.println("5. Editar Transação");
        System.out.println("------ Funcionalidades Avançadas ------");
        System.out.println("6. Buscar Transações"); // Feature 6
        System.out.println("7. Relatórios Estatísticos"); // Feature 3
        System.out.println("8. Definir Orçamento Mensal"); // Feature 1
        System.out.println("9. Gerenciar Transações Recorrentes"); // Feature 11
        System.out.println("10. Ver Sugestões Inteligentes"); // Feature 8
        System.out.println("11. Backup e Restauração"); // Feature 7
        System.out.println("---------------------------------------");
        System.out.println("0. Sair");
        System.out.print("Escolha uma opção: ");
    }

    // --- Feature 1: Orçamento ---
    private void exibirBarraProgressoOrcamento() {
        double orcamento = gerenciador.getOrcamentoMensal();
        if (orcamento <= 0) {
            System.out.println("Orçamento: (Não definido - Use a opção 8)");
            return;
        }

        double gastos = gerenciador.getGastosMesAtual();
        double percentual = (gastos / orcamento);
        if (percentual < 0)
            percentual = 0;

        // Trava a barra em 100% (20 blocos)
        double percentualBarra = (percentual > 1) ? 1 : percentual;

        int blocosPreenchidos = (int) (percentualBarra * 20); // 20 blocos na barra
        int blocosVazios = 20 - blocosPreenchidos;

        StringBuilder barra = new StringBuilder("Orçamento: [");
        for (int i = 0; i < blocosPreenchidos; i++)
            barra.append("=");
        for (int i = 0; i < blocosVazios; i++)
            barra.append(" ");
        barra.append("]");

        System.out.printf("%s %.0f%% (R$ %.2f / R$ %.2f)\n", barra.toString(), percentual * 100, gastos, orcamento);
    }

    private void menuDefinirOrcamento() {
        System.out.println("\n--- Definir Orçamento Mensal ---");
        System.out.printf("Seu orçamento atual é: R$ %.2f\n", gerenciador.getOrcamentoMensal());
        System.out.print("Digite o novo valor do orçamento (0 para limpar): R$ ");
        try {
            double novoOrcamento = scanner.nextDouble();
            scanner.nextLine();
            gerenciador.setOrcamentoMensal(novoOrcamento);
        } catch (Exception e) {
            System.out.println("Valor inválido.");
            scanner.nextLine();
        }
    }

    // --- Feature 2: Alertas ---
    private void exibirAlertas() {
        List<String> alertas = gerenciador.getAlertasFinanceiros();
        if (!alertas.isEmpty()) {
            System.out.println("--- ALERTA(S) ---");
            for (String alerta : alertas) {
                System.out.println("! " + alerta);
            }
        }
    }

    // --- Métodos Auxiliares (Adicionar, Remover, Editar) ---

    private void adicionarReceita() {
        System.out.println("\n--- Adicionar Receita ---");
        try {
            System.out.print("Valor: R$ ");
            double valor = scanner.nextDouble();
            scanner.nextLine();
            System.out.print("Categoria (ex: Salário, Venda): ");
            String categoria = scanner.nextLine();
            System.out.print("Data (dd/MM/yyyy - padrão: hoje): ");
            String data = scanner.nextLine();
            // Preenche com data de hoje se vazio
            if (data.isEmpty())
                data = LocalDate.now().format(DATE_FORMATTER);
            System.out.print("Descrição (opcional): ");
            String descricao = scanner.nextLine();

            Transacao receita = new Transacao(valor, "RECEITA", categoria, data, descricao);
            gerenciador.adicionarTransacao(receita);
        } catch (Exception e) {
            System.out.println("Erro na entrada de dados. Tente novamente.");
            scanner.nextLine();
        }
    }

    private void adicionarDespesa() {
        System.out.println("\n--- Adicionar Despesa ---");
        try {
            System.out.print("Valor: R$ ");
            double valor = scanner.nextDouble();
            scanner.nextLine();
            System.out.print("Categoria (ex: Alimentação, Transporte): ");
            String categoria = scanner.nextLine();
            System.out.print("Data (dd/MM/yyyy - padrão: hoje): ");
            String data = scanner.nextLine();
            // Preenche com data de hoje se vazio
            if (data.isEmpty())
                data = LocalDate.now().format(DATE_FORMATTER);
            System.out.print("Descrição (opcional): ");
            String descricao = scanner.nextLine();

            Transacao despesa = new Transacao(valor, "DESPESA", categoria, data, descricao);
            gerenciador.adicionarTransacao(despesa);
        } catch (Exception e) {
            System.out.println("Erro na entrada de dados. Tente novamente.");
            scanner.nextLine();
        }
    }

    private void removerTransacao() {
        System.out.println("\n--- Remover Transação ---");
        gerenciador.listarTransacoes();
        System.out.print("Digite o número da transação que deseja remover (0 para cancelar): ");
        try {
            int indice = scanner.nextInt();
            scanner.nextLine();
            if (indice == 0) {
                System.out.println("Remoção cancelada.");
                return;
            }
            gerenciador.removerTransacao(indice);
        } catch (Exception e) {
            System.out.println("Número inválido.");
            scanner.nextLine();
        }
    }

    private void editarTransacao() {
        System.out.println("\n--- Editar Transação ---");
        gerenciador.listarTransacoes();
        System.out.print("Digite o número da transação que deseja editar (0 para cancelar): ");
        try {
            int indice = scanner.nextInt();
            scanner.nextLine();
            if (indice == 0) {
                System.out.println("Edição cancelada.");
                return;
            }
            Transacao t = gerenciador.getTransacaoPorIndice(indice);
            if (t == null) {
                System.out.println("Erro: Índice inválido!");
                return;
            }

            System.out.println("Editando: " + t);
            System.out.println("Deixe em branco para não alterar.");

            // Edita Valor
            System.out.printf("Novo Valor (Atual: %.2f): R$ ", t.getValor());
            String novoValorStr = scanner.nextLine();
            if (!novoValorStr.isEmpty())
                t.setValor(Double.parseDouble(novoValorStr));

            // Edita Categoria
            System.out.printf("Nova Categoria (Atual: %s): ", t.getCategoria());
            String novaCat = scanner.nextLine();
            if (!novaCat.isEmpty())
                t.setCategoria(novaCat);

            // Edita Data
            System.out.printf("Nova Data (dd/MM/yyyy) (Atual: %s): ", t.getData());
            String novaData = scanner.nextLine();
            if (!novaData.isEmpty())
                t.setData(novaData);

            // Edita Descrição
            System.out.printf("Nova Descrição (Atual: %s): ", t.getDescricao());
            String novaDesc = scanner.nextLine();
            if (!novaDesc.isEmpty())
                t.setDescricao(novaDesc);

            gerenciador.salvarAposEdicao();
        } catch (Exception e) {
            System.out.println("Erro na entrada de dados: " + e.getMessage());
            scanner.nextLine();
        }
    }

    // --- Feature 3: Menu de Relatórios ---
    private void menuRelatorios() {
        System.out.println("\n--- Relatórios Estatísticos ---");
        System.out.println("1. Gastos por Categoria (Geral)");
        System.out.println("2. Balanço Entradas vs. Saídas (Mês Atual)");
        System.out.println("3. Maior Despesa (Mês Atual)");
        System.out.println("4. Média de Gastos Diários (Mês Atual)");
        System.out.print("Escolha um relatório (0 para voltar): ");

        try {
            int op = scanner.nextInt();
            scanner.nextLine();
            System.out.println("--- RESULTADO ---");
            switch (op) {
                case 1:
                    System.out.println(gerenciador.relatorioGastosPorCategoria());
                    break;
                case 2:
                    System.out.println(gerenciador.relatorioEntradasVsSaidasMesAtual());
                    break;
                case 3:
                    System.out.println(gerenciador.relatorioMaiorDespesaMesAtual());
                    break;
                case 4:
                    System.out.println(gerenciador.relatorioMediaGastosDiariosMesAtual());
                    break;
                case 0:
                    break;
                default:
                    System.out.println("Opção inválida.");
            }
        } catch (Exception e) {
            System.out.println("Opção inválida.");
            scanner.nextLine();
        }
    }

    // --- Feature 6: Menu de Busca ---
    private void menuBuscarTransacoes() {
        System.out.println("\n--- Buscar Transações ---");
        System.out.println("Deixe em branco para não filtrar.");

        System.out.print("Categoria (ex: Alimentação): ");
        String cat = scanner.nextLine();

        System.out.print("Data de Início (dd/MM/yyyy): ");
        String dataInicio = scanner.nextLine();

        System.out.print("Data de Fim (dd/MM/yyyy): ");
        String dataFim = scanner.nextLine();

        List<Transacao> resultados = gerenciador.buscarTransacoes(dataInicio, dataFim, cat);

        if (resultados.isEmpty()) {
            System.out.println("Nenhuma transação encontrada com esses critérios.");
        } else {
            System.out.printf("--- %d Transação(ões) Encontrada(s) ---\n", resultados.size());
            for (Transacao t : resultados) {
                System.out.println(t);
            }
        }
    }

    // --- Feature 7: Menu de Backup/Restauração ---
    private void menuBackupRestauracao() {
        System.out.println("\n--- Backup e Restauração ---");
        System.out.println("1. Criar Backup Agora");
        System.out.println("2. Restaurar um Backup");
        System.out.print("Escolha uma opção (0 para voltar): ");

        try {
            int op = scanner.nextInt();
            scanner.nextLine();
            if (op == 1) {
                System.out.println(gerenciador.backupDados());
            } else if (op == 2) {
                List<String> backups = gerenciador.listarBackups();
                if (backups.isEmpty()) {
                    System.out.println("Nenhum backup encontrado.");
                    return;
                }
                System.out.println("Backups disponíveis:");
                for (int i = 0; i < backups.size(); i++) {
                    System.out.printf("%d. %s\n", i + 1, backups.get(i));
                }
                System.out.print("Digite o NÚMERO do backup para restaurar (0 para cancelar): ");
                int backupIdx = scanner.nextInt();
                scanner.nextLine();
                if (backupIdx > 0 && backupIdx <= backups.size()) {
                    System.out.println("ATENÇÃO: Isso irá sobrescrever todos os dados atuais!");
                    System.out.print("Digite 'RESTAURAR' para confirmar: ");
                    String confirm = scanner.nextLine();
                    if (confirm.equals("RESTAURAR")) {
                        System.out.println(gerenciador.restaurarDados(backups.get(backupIdx - 1)));
                    } else {
                        System.out.println("Restauração cancelada.");
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Opção inválida.");
            scanner.nextLine();
        }
    }

    // --- Feature 8: Menu de Sugestões ---
    private void menuSugestoes() {
        System.out.println("\n--- Sugestões Inteligentes ---");
        List<String> sugestoes = gerenciador.getSugestoesInteligentes();
        for (String s : sugestoes) {
            System.out.println("- " + s);
        }
    }

    // --- Feature 11: Menu de Transações Recorrentes ---
    private void menuTransacoesRecorrentes() {
        System.out.println("\n--- Gerenciar Transações Recorrentes ---");
        System.out.println("1. Adicionar Nova Transação Recorrente");
        System.out.println("2. Listar Transações Recorrentes");
        System.out.print("Escolha uma opção (0 para voltar): ");

        try {
            int op = scanner.nextInt();
            scanner.nextLine();
            if (op == 1) {
                adicionarRecorrente();
            } else if (op == 2) {
                List<TransacaoRecorrente> recorrentes = gerenciador.getTransacoesRecorrentes();
                if (recorrentes.isEmpty()) {
                    System.out.println("Nenhuma transação recorrente cadastrada.");
                } else {
                    recorrentes.forEach(System.out::println);
                }
            }
        } catch (Exception e) {
            System.out.println("Opção inválida.");
            scanner.nextLine();
        }
    }

    private void adicionarRecorrente() {
        System.out.println("\n--- Adicionar Recorrente ---");
        try {
            System.out.print("Tipo (1: Receita, 2: Despesa): ");
            int tipoNum = scanner.nextInt();
            scanner.nextLine();
            String tipo = (tipoNum == 1) ? "RECEITA" : "DESPESA";

            System.out.print("Valor: R$ ");
            double valor = scanner.nextDouble();
            scanner.nextLine();

            System.out.print("Categoria (ex: Salário, Assinatura): ");
            String categoria = scanner.nextLine();

            System.out.print("Descrição (ex: Netflix, Aluguel): ");
            String descricao = scanner.nextLine();

            System.out.print("Dia do Mês para cobrança (1-31): ");
            int dia = scanner.nextInt();
            scanner.nextLine();

            if (dia < 1 || dia > 31) {
                System.out.println("Dia inválido.");
                return;
            }

            TransacaoRecorrente tr = new TransacaoRecorrente(valor, tipo, categoria, descricao, dia);
            gerenciador.adicionarTransacaoRecorrente(tr);

        } catch (Exception e) {
            System.out.println("Erro na entrada de dados.");
            scanner.nextLine();
        }
    }
}